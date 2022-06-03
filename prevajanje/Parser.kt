class Parser(private val scanner: Scanner) {
	private val last: Token? = null
    
    fun parse(): String {
        last = scanner.getToken()
        val status = recognizeInit()
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
        when(last?.value) {
            POINT_VALUE -> parsePoint()
            LINE_VALUE -> parseLine()
            BOX_VALUE -> parseBox()
            CIRCLE_VALUE -> parseCircle()
            RBRACE_VALUE -> 
            else -> throw Error("Error in parseStatements()")
        }
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
