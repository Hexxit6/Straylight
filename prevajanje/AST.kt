import kotlin.math.*

data class Pt(val x: Double, val y: Double) {
    operator fun plus(other: Pt) =
        Pt(x + other.x, y + other.y)

    operator fun times(s: Double) =
        Pt(s * x, s * y) // Število je lahko samo na desni strani

    fun angle(other: Pt) =
        atan2(other.y - y, other.x - x)

    fun dist(other: Pt) =
        hypot(other.x - x, other.y - y)

    fun offset(s: Double, angle: Double) =
        this + Pt(cos(angle), sin(angle)) * s

    override fun toString() =
        "[$x,$y]"
}

class Bezier(private val p0: Pt, private val p1: Pt, private val p2: Pt, private val p3: Pt) {

    // Enačba za krivuljo, https://en.wikipedia.org/wiki/B%C3%A9zier_curve
    fun at(t: Double) =
        p0 * (1.0 - t).pow(3.0) + p1 * 3.0 * (1.0 - t).pow(2.0) * t + p2 * 3.0 * (1.0 - t) * t.pow(2.0) + p3 * t.pow(3.0)

    // Vrne seznam točk na krivulji (polyline)
    fun flatten(segmentsCount: Int): List<Pt> {
        val ps = mutableListOf<Pt>()
        for (i in 0 .. segmentsCount) {
            val t = i / segmentsCount.toDouble()
            ps.add(at(t))
        }
        return ps
    }

    // Približna dolžina krivulje
    fun approxLength(): Double {
        val midpoint = at(0.5)
        return p0.dist(midpoint) + midpoint.dist(p3)
    }

    override fun toString(): String {
        var output: String = ""
        val points = this.flatten(resolutionToSegmentsCount(20.0))
        // val points = this.flatten(16)
        for (p in points) {
            output += p.toString()
            output += ','
        }
        return output.dropLast(1)
    }

    // Število segmentov na enoto (daljša krivulja naj ima več segmentov)
    fun resolutionToSegmentsCount(resolution: Double) = // Resolucija je število segmentov na enoto
        (resolution * approxLength()).coerceAtLeast(2.0).toInt()

    companion object {
        fun bend(t0: Pt, t1: Pt, relativeAngle: Double): Bezier {
            val relativeAngle = Math.toRadians(relativeAngle)
            val oppositeRelativeAngle = PI - relativeAngle // Na drugi strani uporabimo enak odmik

            val angle = t0.angle(t1) // Kot med točkama
            val constant = (4 / 3) * tan(Math.PI / 8) // Magična konstanta, izbrana tako, da dobimo četrtino kroga če sta točki pod 45 stopinjskim kotom

            // Kontrolne točke
            val c0 = t0.offset(constant, angle + relativeAngle)
            val c1 = t1.offset(constant, angle + oppositeRelativeAngle)

            return Bezier(t0, c0, c1, t1)
        }
    }
}



interface Expr {
    fun toGeoJSON(): String
}

class Nil() : Expr {
    override fun toGeoJSON(): String = ""
}

class City(private val content: Expr) : Expr {
    override fun toGeoJSON(): String =
        "{\"type\":\"FeatureCollection\",\"features\":[${content.toGeoJSON()}]}"
}

class Point(val lat: Double, val long: Double) : Expr {
    override fun toGeoJSON(): String =
        "{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"properties\":{},\"coordinates\":[${lat},${long}]}}"

    override fun toString(): String =
        "[${lat},${long}]"

    fun toPt(): Pt =
        Pt(lat, long)
}

class Line(private val point1: Point, private val point2: Point) : Expr {
    override fun toGeoJSON(): String =
        "{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[${point1.toString()},${point2.toString()}]}}"
}

class Box(private val point1: Point, private val point2: Point) : Expr {
    override fun toGeoJSON(): String {
        val dl: Point = Point(point1.lat, point2.long)
        val ur: Point = Point(point2.lat, point1.long)
        return "{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[${point1.toString()},${dl.toString()},${point2.toString()},${ur.toString()},${point1.toString()}]]}}"
    }
}

class Bend(private val point1: Point, private val point2: Point, private val angle: Double) : Expr {
    override fun toGeoJSON(): String {
        val p1: Pt = point1.toPt()
        val p2: Pt = point2.toPt()
        val bend: Bezier = Bezier.bend(p1, p2, angle)
        return "{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[$bend]}}"
    }
}

class Circle(private val point: Point, private val radius: Double) : Expr {
    override fun toGeoJSON(): String {

        val left: Pt = Point(point.lat - radius, point.long).toPt()
        val up: Pt = Point(point.lat, point.long + radius).toPt()
        val down: Pt = Point(point.lat, point.long - radius).toPt()
        val right: Pt = Point(point.lat + radius, point.long).toPt()

        val angle: Double = 53.0
        val bend1: Bezier = Bezier.bend(left, up, angle)
        val bend2: Bezier = Bezier.bend(up, right, angle)
        val bend3: Bezier = Bezier.bend(right, down, angle)
        val bend4: Bezier = Bezier.bend(down, left, angle)

        return "{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[$bend1,$bend2,$bend3,$bend4]]}}"
    }
}

class Comma(private val content1: Expr, private val content2: Expr) : Expr {
    override fun toGeoJSON(): String {
        // add check if element is Nil() that it doesn't use a comma
        if (content1::class.simpleName == "Nil" && content2::class.simpleName != "Nil" ) return content2.toGeoJSON()
        else if (content1::class.simpleName != "Nil" && content2::class.simpleName == "Nil" ) return content1.toGeoJSON()
        else return content1.toGeoJSON() + ',' + content2.toGeoJSON()
    }
}

class Water(private val content: Expr) : Expr {
    override fun toGeoJSON(): String =
        content.toGeoJSON()
}

class Park(private val content: Expr) : Expr {
    override fun toGeoJSON(): String =
        content.toGeoJSON()
}

class Forest(private val content: Expr) : Expr {
    override fun toGeoJSON(): String =
        content.toGeoJSON()
}

class Field(private val content: Expr) : Expr {
    override fun toGeoJSON(): String =
        content.toGeoJSON()
}

