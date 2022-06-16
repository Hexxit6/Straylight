class Recognizer(private val scanner: Scanner) {
    private var last: Token? = null

    fun recognize(): Boolean {
        last = scanner.getToken()
        val status = recognizeInit()
        return when (last?.value) {
            null -> status
            else -> false
        }
    }

    private fun recognizeInit(): Boolean {
		return when(last?.value) {
			CITY_VALUE -> recognizeCity()
			FUNCTION_VALUE -> recognizeFunction()
			null -> true
			else -> false
		}
	}
    
    private fun recognizeCity(): Boolean =
        recognizeTerminal(CITY_VALUE) && recognizeBlock()

	private fun recognizeBlock(): Boolean =
		recognizeTerminal(LBRACE_VALUE) && recognizeStatements() && recognizeTerminal(RBRACE_VALUE)

	private fun recognizeStatements(): Boolean {
		return when(last?.value) {
			VAR_VALUE -> recognizeInitialization() && recognizeStatements()
			ID_VALUE -> recognizeAssignment() && recognizeStatements()
			IF_VALUE -> recognizeFlow() && recognizeStatements()
			FOR_VALUE -> recognizeFor() && recognizeStatements()
			BREAK_VALUE -> recognizeTerminal(BREAK_VALUE) && recognizeStatements()
			EXCLAMATION_VALUE -> recognizeFunction() && recognizeStatements()
			LINE_VALUE -> recognizeLine() && recognizeStatements()
			BEND_VALUE -> recognizeBend() && recognizeStatements()
			BOX_VALUE -> recognizeBox() && recognizeStatements()
			CIRCLE_VALUE -> recognizeCircle() && recognizeStatements()
			POLYLINE_VALUE -> recognizePolyline() && recognizeStatements()
			POLYGON_VALUE -> recognizePolygon() && recognizeStatements()
			WATER_VALUE -> recognizeWater() && recognizeStatements()
			PARK_VALUE -> recognizePark() && recognizeStatements()
			FOREST_VALUE -> recognizeForest() && recognizeStatements()
			FIELD_VALUE -> recognizeForest() && recognizeStatements()
			FIELD_VALUE -> recognizeField() && recognizeStatements()
			RBRACE_VALUE -> true
			else -> false
		}
	}
    
    private fun recognizeAssignment(): Boolean =
    	recognizeTerminal(ID_VALUE) && recognizeTerminal(EQUALS_VALUE) && recognizeValues() && recognizeTerminal(SEMI_VALUE)

	private fun recognizeInitialization(): Boolean =
		recognizeTerminal(VAR_VALUE) && recognizeTerminal(ID_VALUE) && recognizeTerminal(EQUALS_VALUE) && recognizeValues() && recognizeTerminal(SEMI_VALUE)

	private fun recognizeValues(): Boolean {
		return when(last?.value) {
			ID_VALUE, MINUS_VALUE, LPAREN_VALUE, FLOAT_VALUE -> recognizeExpr()
			STRING_VALUE -> recognizeTerminal(STRING_VALUE)
			POINT_VALUE -> recognizePoint()
			LBRACKET_VALUE -> recognizeList()
			else -> false
		}
	}

	private fun recognizeExpr(): Boolean =
		recognizeTerm() && recognizeExpr_()

	private fun recognizeExpr_(): Boolean {
		return when(last?.value) {
			PLUS_VALUE -> recognizeTerminal(PLUS_VALUE) && recognizeTerm() && recognizeExpr_()
			MINUS_VALUE -> recognizeTerminal(MINUS_VALUE) && recognizeTerm() && recognizeExpr_()
			else -> true
		}
	}

	private fun recognizeTerm(): Boolean =
		recognizeFactor() && recognizeTerm_()

	private fun recognizeTerm_(): Boolean {
		return when(last?.value) {
			TIMES_VALUE -> recognizeTerminal(TIMES_VALUE) && recognizeFactor() && recognizeTerm_()
			DIVIDE_VALUE -> recognizeTerminal(DIVIDE_VALUE) && recognizeFactor() && recognizeTerm_()
			PLUS_VALUE, MINUS_VALUE, SEMI_VALUE -> true
			else -> false
		}
	}

	private fun recognizeFactor(): Boolean {
		return when(last?.value) {
			LPAREN_VALUE -> recognizeTerminal(LPAREN_VALUE) && recognizeExpr() && recognizeTerminal(RPAREN_VALUE)
			ID_VALUE -> recognizeTerminal(ID_VALUE)
			FLOAT_VALUE -> recognizeTerminal(FLOAT_VALUE)
			MINUS_VALUE -> recognizeTerminal(MINUS_VALUE) && recognizeTerminal(FLOAT_VALUE)
			else -> false
		}
	}

	private fun recognizePoint(): Boolean =
		recognizeTerminal(POINT_VALUE) && recognizeTerminal(LPAREN_VALUE) && recognizeExpr() && recognizeTerminal(COMMA_VALUE) && recognizeExpr() && recognizeTerminal(RPAREN_VALUE)
	
	private fun recognizeList(): Boolean =
		recognizeTerminal(LBRACKET_VALUE) && recognizeValues() && recognizeValue2() && recognizeTerminal(RBRACKET_VALUE)
	
	private fun recognizeValue2(): Boolean {
		return when(last?.value) {
			COMMA_VALUE -> recognizeTerminal(COMMA_VALUE) && recognizeValues() && recognizeValue2()
			RPAREN_VALUE, RBRACKET_VALUE -> true
			else -> false
		}
	}

	private fun recognizeFlow(): Boolean =
		recognizeTerminal(IF_VALUE) && recognizeCondition() && recognizeBlock() && recognizeElif() && recognizeElse()

	private fun recognizeElif(): Boolean {
		return when(last?.value) {
			ELIF_VALUE -> recognizeTerminal(ELIF_VALUE) && recognizeCondition() && recognizeBlock()
			ELSE_VALUE -> true
			else -> false
		}
	}

	private fun recognizeElse(): Boolean {
		return when(last?.value) {
			ELSE_VALUE -> recognizeTerminal(ELSE_VALUE) && recognizeBlock()
			ID_VALUE, RBRACE_VALUE, BREAK_VALUE, VAR_VALUE, IF_VALUE, FOR_VALUE, EXCLAMATION_VALUE, LINE_VALUE, BEND_VALUE, BOX_VALUE, CIRCLE_VALUE, POLYLINE_VALUE, POLYGON_VALUE, WATER_VALUE, PARK_VALUE, FOREST_VALUE, FIELD_VALUE -> true
			else -> false
		}
	}

	private fun recognizeCondition(): Boolean =
		recognizeTerminal(LPAREN_VALUE) && recognizeComparison() && recognizeComparisons() && recognizeTerminal(RPAREN_VALUE)

	private fun recognizeComparisons(): Boolean {
		return when(last?.value) {
			AND_VALUE -> recognizeTerminal(AND_VALUE) && recognizeComparison() && recognizeComparisons()
			OR_VALUE -> recognizeTerminal(OR_VALUE) && recognizeComparison() && recognizeComparisons()
			SEMI_VALUE, RPAREN_VALUE -> true
			else -> false
		}
	}

	private fun recognizeComparison(): Boolean =
		recognizeExpr() && recognizeComp()
	
	private fun recognizeComp(): Boolean {
		return when(last?.value) {
			LTEQ_VALUE -> recognizeTerminal(LTEQ_VALUE) && recognizeExpr()
			GTEQ_VALUE -> recognizeTerminal(GTEQ_VALUE) && recognizeExpr()
			LT_VALUE -> recognizeTerminal(LT_VALUE) && recognizeExpr()
			GT_VALUE -> recognizeTerminal(GT_VALUE) && recognizeExpr()
			ISSAME_VALUE -> recognizeTerminal(ISSAME_VALUE) && recognizeExpr()
			NOTSAME_VALUE -> recognizeTerminal(NOTSAME_VALUE) && recognizeExpr()
			else -> false
		}
	}

	private fun recognizeFor(): Boolean =
		recognizeTerminal(FOR_VALUE) && recognizeInitialization() && recognizeComparison() && recognizeComparisons() && recognizeTerminal(SEMI_VALUE) && recognizeTerminal(ID_VALUE) && recognizeTerminal(EQUALS_VALUE) && recognizeExpr() && recognizeTerminal(RPAREN_VALUE) && recognizeBlock()

	private fun recognizeFunction(): Boolean =
		recognizeTerminal(FUNCTION_VALUE) && recognizeTerminal(ID_VALUE) && recognizeTerminal(LPAREN_VALUE) && recognizeVars() && recognizeTerminal(RPAREN_VALUE)

	private fun recognizeVars(): Boolean {
		return when(last?.value) {
			VAR_VALUE -> recognizeTerminal(VAR_VALUE) && recognizeTerminal(ID_VALUE) && recognizeVars()
			RPAREN_VALUE -> true
			else -> false
		}
	}

	private fun recognizeLine(): Boolean =
		recognizeTerminal(LINE_VALUE) && recognizeTerminal(LPAREN_VALUE) && recognizePoint() && recognizeTerminal(COMMA_VALUE) && recognizePoint() && recognizeTerminal(RPAREN_VALUE)

	private fun recognizeAngle(): Boolean =
		recognizeTerminal(ANGLE_VALUE) && recognizeTerminal(LPAREN_VALUE) && recognizeExpr() && recognizeTerminal(RPAREN_VALUE)

	private fun recognizeBend(): Boolean = 
		recognizeTerminal(BEND_VALUE) && recognizeTerminal(LPAREN_VALUE) && recognizePoint() && recognizeTerminal(COMMA_VALUE) && recognizePoint() && recognizeTerminal(COMMA_VALUE) && recognizeAngle() && recognizeTerminal(RPAREN_VALUE)

	private fun recognizeBox(): Boolean =
		recognizeTerminal(BOX_VALUE) && recognizeTerminal(LPAREN_VALUE) && recognizePoint() && recognizeTerminal(COMMA_VALUE) && recognizePoint() && recognizeTerminal(RPAREN_VALUE)

	private fun recognizeRadius(): Boolean =
		recognizeTerminal(RADIUS_VALUE) && recognizeTerminal(LPAREN_VALUE) && recognizeExpr() && recognizeTerminal(RPAREN_VALUE)

	private fun recognizeCircle(): Boolean =
		recognizeTerminal(CIRCLE_VALUE) && recognizeTerminal(LPAREN_VALUE) && recognizePoint() && recognizeTerminal(COMMA_VALUE) && recognizeRadius() && recognizeTerminal(RPAREN_VALUE)

	private fun recognizePoints(): Boolean {
		return when(last?.value) {
			POINT_VALUE -> recognizePoint() && recognizePoints()
			COMMA_VALUE -> recognizeTerminal(COMMA_VALUE) && recognizePoints()
			RPAREN_VALUE -> true
			else -> false
		}
	}

	private fun recognizePolyline(): Boolean =
		recognizeTerminal(POLYLINE_VALUE) && recognizeTerminal(LPAREN_VALUE) && recognizePoints() && recognizeTerminal(RPAREN_VALUE)

	private fun recognizePolygon(): Boolean =
		recognizeTerminal(POLYGON_VALUE) && recognizeTerminal(LPAREN_VALUE) && recognizeTerminal(FLOAT_VALUE) && recognizeTerminal(RPAREN_VALUE)

	private fun recognizeWater(): Boolean =
		recognizeTerminal(WATER_VALUE) && recognizeBlock()

	private fun recognizePark(): Boolean =
		recognizeTerminal(PARK_VALUE) && recognizeBlock()

	private fun recognizeForest(): Boolean =
		recognizeTerminal(FOREST_VALUE) && recognizeBlock()

	private fun recognizeField(): Boolean =
		recognizeTerminal(FIELD_VALUE) && recognizeBlock()


    private fun recognizeTerminal(value: Int) =
        if (last?.value == value) {
            last = scanner.getToken()
            // println(last?.value)	// DEBUGGING
            true
        }
        else false
}
