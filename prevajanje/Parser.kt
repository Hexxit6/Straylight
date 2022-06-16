class Parser(private val scanner: Scanner) {
    private var last: Token? = null

    fun parse(): String {
        last = scanner.getToken()
        val status = parseInit()
        return when(last?.value) {
            null -> "{\"type\":\"FeatureCollection\",\"features\":[" + status.toGeoJSON() + "]}"
            else -> throw Error("Error in parse()")
        }
    }

    private fun parseInit(): Expr {
        when(last?.value) {
            CITY_VALUE -> return parseCity()
            else -> throw Error("Error in parseInit()")
        }
    }

    private fun parseCity(): Expr {
        when(last?.value) {
            CITY_VALUE -> {
                parseTerminal(CITY_VALUE)
                return parseBlock()
            }
            else -> throw Error("Error in parseCity()")
        }
    }

    private fun parseBlock(): Expr {
        when(last?.value) {
            LBRACE_VALUE -> {
                parseTerminal(LBRACE_VALUE)
                val output = parseStatements()
                parseTerminal(RBRACE_VALUE)
                return output
            }
            else -> throw Error("Error in parseBlock()")
        }
    }

    private fun parseStatements(): Expr {
        val lista: MutableList<Expr> = mutableListOf()

        while (last?.value != RBRACE_VALUE) {
            when(last?.value) {
                POINT_VALUE -> lista.add(parsePoint())
                LINE_VALUE -> lista.add(parseLine())
                BOX_VALUE -> lista.add(parseBox())
                CIRCLE_VALUE -> lista.add(parseCircle())
                WATER_VALUE -> lista.add(parseWater())
                PARK_VALUE -> lista.add(parsePark())
                FOREST_VALUE -> lista.add(parseForest())
                FIELD_VALUE -> lista.add(parseField())
                RBRACE_VALUE -> lista.add(Nil())
                else -> throw Error("Error in parseStatements()")
            }
            parseTerminal(SEMI_VALUE)
        }

        var output: Expr = Nil()
        for (i in lista) output = Comma(output, i)

        return output
    }

    private fun parsePoint(): Point {
        parseTerminal(POINT_VALUE)
        parseTerminal(LPAREN_VALUE)
        val x: Double = parseFloat(FLOAT_VALUE)
        parseTerminal(COMMA_VALUE)
        val y: Double = parseFloat(FLOAT_VALUE)
        parseTerminal(RPAREN_VALUE)
        return Point(x, y)
    }

    private fun parseLine(): Expr {
        parseTerminal(LINE_VALUE)
        parseTerminal(LPAREN_VALUE)
        val point1: Point = parsePoint()
        parseTerminal(COMMA_VALUE)
        val point2: Point = parsePoint()
        parseTerminal(RPAREN_VALUE)
        return Line(point1, point2)
    }

    private fun parseBox(): Expr {
        parseTerminal(BOX_VALUE)
        parseTerminal(LPAREN_VALUE)
        val point1: Point = parsePoint()
        parseTerminal(COMMA_VALUE)
        val point2: Point = parsePoint()
        parseTerminal(RPAREN_VALUE)
        return Box(point1, point2)
    }

    private fun parseCircle(): Expr {
        parseTerminal(CIRCLE_VALUE)
        parseTerminal(LPAREN_VALUE)
        val point: Point = parsePoint()
        parseTerminal(COMMA_VALUE)
        val radius: Double = parseFloat(FLOAT_VALUE)
        parseTerminal(RPAREN_VALUE)
        return Circle(point, radius)
    }

    private fun parseWater(): Expr {
        parseTerminal(WATER_VALUE)
        return Water(parseBlock())
    }

    private fun parsePark(): Expr {
        parseTerminal(PARK_VALUE)
        return Park(parseBlock())
    }

    private fun parseForest(): Expr {
        parseTerminal(FOREST_VALUE)
        return Forest(parseBlock())
    }

    private fun parseField(): Expr {
        parseTerminal(FIELD_VALUE)
        return Field(parseBlock())
    }


    private fun parseTerminal(value: Int): String? {
        if (last?.value == value) {
            val curr = last
            last = scanner.getToken()
            return curr?.lexeme
        }
        else throw Error("Error in parseTerminal()")
    }

    private fun parseFloat(value: Int) =
        parseTerminal(value)!!.toDouble()
}

