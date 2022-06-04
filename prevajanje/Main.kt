fun main(args: Array<String>) {
    // val scanner = Scanner(Example, "city{let varijabla = 4.4+100;let varijabla = 4.4+100;Water{}}".byteInputStream())
    // printTokens(scanner)
	// if (Recognizer(scanner).recognize()) {
	// 	println("accept")
	// } else {
	// 	println("reject")
    // }

    // val expr: Expr = City(Line(Point(2.0,4.0), Point(3.0,6.0)))
    // val expr: Expr = City(Box(Point(2.0,4.0), Point(3.0,6.0)))
    // val expr: Expr = City()
    val expr: Expr = City(
        Comma(
            Comma(
                Line(
                    Point(3.0,4.0),
                    Point(3.0,6.0)
                ),
                Line(
                    Point(2.0,4.0),
                    Point(3.0,6.0)
                )
            ),
            Line(
                Point(2.0,4.0),
                Point(3.0,4.0)
            )
        )
    )
    println(expr.toGeoJSON())

    var scanner = Scanner(Example, "city{ Line(Point(3.0, 4.0), Point(3.0, 6.0)); Line(Point(2.0, 4.0), Point(3.0, 6.0)); Line(Point(2.0, 4.0), Point(3.0, 4.0)); }".byteInputStream())
    val parser = Parser(scanner)
    println(parser.parse())
}
