fun main(args: Array<String>) {
    val scanner = Scanner(Example, "city{let varijabla = 4.4+100;let varijabla = 4.4+100;Water{}}".byteInputStream())
    // printTokens(scanner)
	if (Recognizer(scanner).recognize()) {
		println("accept")
	} else {
		println("reject")
    }
}
