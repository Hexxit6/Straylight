fun main(args: Array<String>) {
    // val scanner = Scanner(Example, "city{let varijabla = 4.4+100;let varijabla = 4.4+100;Water{}}".byteInputStream())
    // printTokens(scanner)
	// if (Recognizer(scanner).recognize()) {
	// 	println("accept")
	// } else {
	// 	println("reject")
    // }

	var scanner = Scanner(Example, File(args[0]).inputStream())
    // printTokens(scanner)
    val parser = Parser(scanner)
    println(parser.parse())
}
