import java.io.File
import java.io.InputStream
import java.util.LinkedList

const val EOF_SYMBOL = -1
const val ERROR_STATE = 0
const val SKIP_VALUE = 0

const val NEWLINE = '\n'.code

const val NIL_VALUE			= 1
const val FLOAT_VALUE		= 2
const val HEX_VALUE			= 3
const val CITY_VALUE		= 4
const val VAR_VALUE			= 5
const val IF_VALUE			= 6
const val ELIF_VALUE		= 7
const val ELSE_VALUE		= 8
const val AND_VALUE			= 9
const val OR_VALUE			= 10
const val FOR_VALUE			= 11
const val BREAK_VALUE		= 12
const val FUNCTION_VALUE	= 13
const val TRUE_VALUE		= 14
const val FALSE_VALUE		= 15
const val POINT_VALUE		= 16
const val COLOR_VALUE		= 17
const val LINE_VALUE		= 18
const val BEND_VALUE		= 19
const val ANGLE_VALUE		= 20
const val BOX_VALUE			= 21
const val RADIUS_VALUE		= 22
const val CIRCLE_VALUE		= 23
const val POLYLINE_VALUE	= 24
const val POLYGON_VALUE		= 25
const val WATER_VALUE		= 26
const val PARK_VALUE		= 27
const val FOREST_VALUE		= 28
const val FIELD_VALUE 		= 29
const val EQUALS_VALUE		= 30
const val SEMI_VALUE		= 31
const val PLUS_VALUE		= 32
const val MINUS_VALUE		= 33
const val TIMES_VALUE		= 34
const val DIVIDE_VALUE		= 35
const val LPAREN_VALUE		= 36
const val RPAREN_VALUE		= 37
const val LBRACKET_VALUE	= 38
const val RBRACKET_VALUE	= 39
const val LBRACE_VALUE		= 40
const val RBRACE_VALUE		= 41
const val COMMA_VALUE		= 42
const val EXCLAMATION_VALUE	= 43
const val STRING_VALUE		= 44
const val ID_VALUE			= 45
const val ISSAME_VALUE		= 46
const val NOTSAME_VALUE		= 47
const val GT_VALUE			= 48
const val LT_VALUE			= 49
const val GTEQ_VALUE		= 50
const val LTEQ_VALUE		= 51


interface Automaton {
    val states: Set<Int>
    val alphabet: IntRange
    fun next(state: Int, symbol: Int): Int
    fun value(state: Int): Int
    val startState: Int
    val finalStates: Set<Int>
}

object Example : Automaton {
    override val states = (1..139).toSet()
    override val alphabet = 0 .. 255
    override val startState = 1
	override val finalStates = setOf(2,3,5,8,15,19,22,24,28,30,33,35,39,44,47,50,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,78,83,88,94,98,100,106,110,116,120,125,130,134,135,136,139)

    private val numberOfStates = states.maxOrNull()!! + 1
    private val numberOfSymbols = alphabet.maxOrNull()!! + 1
    private val transitions = Array(numberOfStates) {IntArray(numberOfSymbols)}
    private val values: Array<Int> = Array(numberOfStates) {0}

    private fun setTransition(from: Int, symbol: Char, to: Int) {
        transitions[from][symbol.code] = to
    }

    private fun setValue(state: Int, terminal: Int) {
        values[state] = terminal
    }

    override fun next(state: Int, symbol: Int): Int =
        if (symbol == EOF_SYMBOL) ERROR_STATE
        else {
            assert(states.contains(state))
            assert(alphabet.contains(symbol))
            transitions[state][symbol]
        }

    override fun value(state: Int): Int {
        assert(states.contains(state))
        return values[state]
    }

    init {
		// escapes
        setTransition(1, ' ', 136)
        setTransition(1, '\n', 136)
        setTransition(1, '\t', 136)

		// id
        for (i in 'a'..'z') setTransition(2, i, 2)
        for (i in 'A'..'Z') setTransition(2, i, 2)
        for (i in '0'..'9') setTransition(2, i, 2)
		setTransition(2, '_', 2)

		// float
        for (i in '0'..'9') setTransition(1, i, 3)
        for (i in '0'..'9') setTransition(3, i, 3)
        setTransition(3, '.', 4)
        for (i in '0'..'9') setTransition(4, i, 5)
        for (i in '0'..'9') setTransition(5, i, 5)

		// unit
		setTransition(1, 'n', 6)
		setTransition(6, 'i', 7)
		setTransition(7, 'l', 8)
        for (i in 'a'..'h') setTransition(6, i, 2)
        for (i in 'j'..'z') setTransition(6, i, 2)
        for (i in 'a'..'k') setTransition(7, i, 2)
        for (i in 'm'..'z') setTransition(7, i, 2)

		// hex
		setTransition(1, '#', 9)
		for (j in 9..14) {
			for (i in 'a'..'f') setTransition(j, i, j+1)
			for (i in 'A'..'F') setTransition(j, i, j+1)
			for (i in '0'..'9') setTransition(j, i, j+1)
		}

		// city
		setTransition(1, 'c', 16)
		setTransition(16, 'i', 17)
		setTransition(17, 't', 18)
		setTransition(18, 'y', 19)
        for (i in 'a'..'h') setTransition(16, i, 2)
        for (i in 'j'..'z') setTransition(16, i, 2)
        for (i in 'a'..'s') setTransition(17, i, 2)
        for (i in 'u'..'z') setTransition(17, i, 2)
        for (i in 'a'..'x') setTransition(18, i, 2)
        					setTransition(18, 'z', 2)

		// var
		setTransition(1, 'l', 20)
		setTransition(20, 'e', 21)
		setTransition(21, 't', 22)
        for (i in 'a'..'d') setTransition(20, i, 2)
        for (i in 'f'..'z') setTransition(20, i, 2)
        for (i in 'a'..'s') setTransition(21, i, 2)
        for (i in 'u'..'z') setTransition(21, i, 2)

		// if
		setTransition(1, 'i', 23)
		setTransition(23, 'f', 24)
        for (i in 'a'..'e') setTransition(23, i, 2)
        for (i in 'g'..'z') setTransition(23, i, 2)

		// elif, else
		setTransition(1, 'e', 25)
		setTransition(25, 'l', 26)
        for (i in 'a'..'k') setTransition(25, i, 2)
        for (i in 'm'..'z') setTransition(25, i, 2)
		
		setTransition(26, 'i', 27)
		setTransition(27, 'f', 28)
        for (i in 'a'..'h') setTransition(26, i, 2)
        for (i in 'j'..'r') setTransition(26, i, 2)
        for (i in 't'..'z') setTransition(26, i, 2)
        for (i in 'a'..'e') setTransition(27, i, 2)
        for (i in 'g'..'z') setTransition(27, i, 2)

		setTransition(26, 's', 29)
		setTransition(29, 'e', 30)
        for (i in 'a'..'d') setTransition(29, i, 2)
        for (i in 'f'..'z') setTransition(29, i, 2)

		// and
		setTransition(1, 'a', 31)
		setTransition(31, 'n', 32)
		setTransition(32, 'd', 33)
        for (i in 'a'..'m') setTransition(31, i, 2)
        for (i in 'o'..'z') setTransition(31, i, 2)
        for (i in 'a'..'c') setTransition(32, i, 2)
        for (i in 'e'..'z') setTransition(32, i, 2)

		// or
		setTransition(1, 'o', 34)
		setTransition(34, 'r', 35)
        for (i in 'a'..'q') setTransition(34, i, 2)
        for (i in 's'..'z') setTransition(34, i, 2)

		// true
		setTransition(1, 't', 36)
		setTransition(36, 'r', 37)
		setTransition(37, 'u', 38)
		setTransition(38, 'e', 39)
        for (i in 'a'..'q') setTransition(36, i, 2)
        for (i in 's'..'z') setTransition(36, i, 2)
        for (i in 'a'..'t') setTransition(37, i, 2)
        for (i in 'v'..'z') setTransition(37, i, 2)
        for (i in 'a'..'d') setTransition(38, i, 2)
        for (i in 'f'..'z') setTransition(38, i, 2)

		// break
		setTransition(1, 'b', 40)
		setTransition(40, 'r', 41)
		setTransition(41, 'e', 42)
		setTransition(42, 'a', 43)
		setTransition(43, 'k', 44)
        for (i in 'a'..'q') setTransition(40, i, 2)
        for (i in 's'..'z') setTransition(40, i, 2)
        for (i in 'a'..'d') setTransition(41, i, 2)
        for (i in 'f'..'z') setTransition(41, i, 2)
        for (i in 'b'..'q') setTransition(42, i, 2)
        for (i in 'a'..'j') setTransition(43, i, 2)
        for (i in 'l'..'z') setTransition(43, i, 2)

		// for, func, false
		setTransition(1, 'f', 45)
		setTransition(45, 'o', 46)
		setTransition(46, 'r', 47)
        for (i in 'b'..'n') setTransition(45, i, 2)
        for (i in 'p'..'t') setTransition(45, i, 2)
        for (i in 'v'..'z') setTransition(45, i, 2)
        for (i in 'a'..'q') setTransition(46, i, 2)
        for (i in 's'..'z') setTransition(46, i, 2)

		setTransition(45, 'u', 48)
		setTransition(48, 'n', 49)
		setTransition(49, 'c', 50)
        for (i in 'a'..'m') setTransition(48, i, 2)
        for (i in 'o'..'z') setTransition(48, i, 2)
        for (i in 'a'..'b') setTransition(49, i, 2)
        for (i in 'd'..'z') setTransition(49, i, 2)

		setTransition(45, 'a', 51)
		setTransition(51, 'l', 52)
		setTransition(52, 's', 53)
		setTransition(53, 'e', 54)
        for (i in 'a'..'k') setTransition(51, i, 2)
        for (i in 'm'..'z') setTransition(51, i, 2)
        for (i in 'a'..'r') setTransition(52, i, 2)
        for (i in 't'..'z') setTransition(52, i, 2)
        for (i in 'a'..'d') setTransition(53, i, 2)
        for (i in 'f'..'z') setTransition(53, i, 2)

		// symbols
		setTransition(1, '=', 55)
		setTransition(55, '=', 56)
		setTransition(1, ';', 57)
		setTransition(1, '+', 58)
		setTransition(1, '-', 59)
		setTransition(1, '*', 60)
		setTransition(1, '/', 61)
		setTransition(1, '(', 62)
		setTransition(1, ')', 63)
		setTransition(1, '[', 64)
		setTransition(1, ']', 65)
		setTransition(1, '{', 66)
		setTransition(1, '}', 67)
		setTransition(1, ',', 68)
		setTransition(1, '!', 69)
		setTransition(69, '=', 70)
		setTransition(1, '>', 71)
		setTransition(71, '=', 72)
		setTransition(1, '<', 73)
		setTransition(73, '=', 74)

		// Line
		setTransition(1, 'L', 75)
		setTransition(75, 'i', 76)
		setTransition(76, 'n', 77)
		setTransition(77, 'e', 78)
        for (i in 'a'..'h') setTransition(75, i, 2)
        for (i in 'j'..'z') setTransition(75, i, 2)
        for (i in 'a'..'m') setTransition(76, i, 2)
        for (i in 'o'..'z') setTransition(76, i, 2)
        for (i in 'a'..'d') setTransition(77, i, 2)
        for (i in 'f'..'z') setTransition(77, i, 2)

		// Water
		setTransition(1, 'W', 79)
		setTransition(79, 'a', 80)
		setTransition(80, 't', 81)
		setTransition(81, 'e', 82)
		setTransition(82, 'r', 83)
        for (i in 'b'..'z') setTransition(79, i, 2)
        for (i in 'a'..'s') setTransition(80, i, 2)
        for (i in 'u'..'z') setTransition(80, i, 2)
        for (i in 'a'..'d') setTransition(81, i, 2)
        for (i in 'f'..'z') setTransition(81, i, 2)
        for (i in 'a'..'q') setTransition(82, i, 2)
        for (i in 's'..'z') setTransition(82, i, 2)

		// Angle
		setTransition(1, 'A', 84)
		setTransition(84, 'n', 85)
		setTransition(85, 'g', 86)
		setTransition(86, 'l', 87)
		setTransition(87, 'e', 88)
        for (i in 'a'..'m') setTransition(84, i, 2)
        for (i in 'o'..'z') setTransition(84, i, 2)
        for (i in 'a'..'f') setTransition(85, i, 2)
        for (i in 'h'..'z') setTransition(85, i, 2)
        for (i in 'a'..'k') setTransition(86, i, 2)
        for (i in 'm'..'z') setTransition(86, i, 2)
        for (i in 'a'..'d') setTransition(87, i, 2)
        for (i in 'f'..'z') setTransition(87, i, 2)

		// Radius
		setTransition(1, 'R', 89)
		setTransition(89, 'a', 90)
		setTransition(90, 'd', 91)
		setTransition(91, 'i', 92)
		setTransition(92, 'u', 93)
		setTransition(93, 's', 94)
        for (i in 'b'..'z') setTransition(89, i, 2)
        for (i in 'a'..'c') setTransition(90, i, 2)
        for (i in 'e'..'z') setTransition(90, i, 2)
        for (i in 'a'..'h') setTransition(91, i, 2)
        for (i in 'j'..'z') setTransition(91, i, 2)
        for (i in 'a'..'t') setTransition(92, i, 2)
        for (i in 'v'..'z') setTransition(92, i, 2)
        for (i in 'a'..'r') setTransition(93, i, 2)
        for (i in 't'..'z') setTransition(93, i, 2)

		// Bend, Box
		setTransition(1, 'B', 95)
		setTransition(95, 'e', 96)
		setTransition(96, 'n', 97)
		setTransition(97, 'd', 98)
        for (i in 'a'..'d') setTransition(95, i, 2)
        for (i in 'f'..'n') setTransition(95, i, 2)
        for (i in 'a'..'m') setTransition(96, i, 2)
        for (i in 'o'..'z') setTransition(96, i, 2)
        for (i in 'a'..'c') setTransition(97, i, 2)
        for (i in 'e'..'z') setTransition(97, i, 2)

		setTransition(95, 'o', 99)
		setTransition(99, 'x', 100)
        for (i in 'a'..'w') setTransition(99, i, 2)
        for (i in 'y'..'z') setTransition(99, i, 2)

		// Forest, Field
		setTransition(1, 'F', 101)
		setTransition(101, 'o', 102)
		setTransition(102, 'r', 103)
		setTransition(103, 'e', 104)
		setTransition(104, 's', 105)
		setTransition(105, 't', 106)
        for (i in 'a'..'h') setTransition(101, i, 2)
        for (i in 'j'..'n') setTransition(101, i, 2)
        for (i in 'p'..'z') setTransition(101, i, 2)
        for (i in 'a'..'q') setTransition(102, i, 2)
        for (i in 's'..'z') setTransition(102, i, 2)
        for (i in 'a'..'d') setTransition(103, i, 2)
        for (i in 'f'..'z') setTransition(103, i, 2)
        for (i in 'a'..'r') setTransition(104, i, 2)
        for (i in 't'..'z') setTransition(104, i, 2)
        for (i in 'a'..'s') setTransition(105, i, 2)
        for (i in 'u'..'z') setTransition(105, i, 2)
		
		setTransition(101, 'i', 107)
		setTransition(107, 'e', 108)
		setTransition(108, 'l', 109)
		setTransition(109, 'd', 110)
        for (i in 'a'..'d') setTransition(107, i, 2)
        for (i in 'f'..'z') setTransition(107, i, 2)
        for (i in 'a'..'k') setTransition(108, i, 2)
        for (i in 'm'..'z') setTransition(108, i, 2)
        for (i in 'a'..'c') setTransition(109, i, 2)
        for (i in 'e'..'z') setTransition(109, i, 2)

		// Circle, Color
		setTransition(1, 'C', 111)
		setTransition(111, 'i', 112)
		setTransition(112, 'r', 113)
		setTransition(113, 'c', 114)
		setTransition(114, 'l', 115)
		setTransition(115, 'e', 116)
        for (i in 'a'..'h') setTransition(111, i, 2)
        for (i in 'j'..'n') setTransition(111, i, 2)
        for (i in 'p'..'z') setTransition(111, i, 2)
        for (i in 'a'..'q') setTransition(112, i, 2)
        for (i in 's'..'z') setTransition(112, i, 2)
        for (i in 'a'..'b') setTransition(113, i, 2)
        for (i in 'd'..'z') setTransition(113, i, 2)
        for (i in 'a'..'k') setTransition(114, i, 2)
        for (i in 'm'..'z') setTransition(114, i, 2)
        for (i in 'a'..'d') setTransition(115, i, 2)
        for (i in 'f'..'z') setTransition(115, i, 2)

		setTransition(111, 'o', 117)
		setTransition(117, 'l', 118)
		setTransition(118, 'o', 119)
		setTransition(119, 'r', 120)
        for (i in 'a'..'k') setTransition(117, i, 2)
        for (i in 'm'..'z') setTransition(117, i, 2)
        for (i in 'a'..'n') setTransition(118, i, 2)
        for (i in 'p'..'z') setTransition(118, i, 2)
        for (i in 'a'..'q') setTransition(119, i, 2)
        for (i in 's'..'z') setTransition(119, i, 2)

		// Polygon, Polyline, Point
		setTransition(1, 'P', 121)
		setTransition(121, 'o', 122)
		setTransition(122, 'i', 123)
		setTransition(123, 'n', 124)
		setTransition(124, 't', 125)
        for (i in 'a'..'n') setTransition(121, i, 2)
        for (i in 'p'..'z') setTransition(121, i, 2)
        for (i in 'a'..'h') setTransition(122, i, 2)
        for (i in 'j'..'k') setTransition(122, i, 2)
        for (i in 'm'..'z') setTransition(122, i, 2)
        for (i in 'a'..'m') setTransition(123, i, 2)
        for (i in 'o'..'z') setTransition(123, i, 2)
        for (i in 'a'..'s') setTransition(124, i, 2)
        for (i in 'u'..'z') setTransition(124, i, 2)

		setTransition(122, 'l', 126)
		setTransition(126, 'y', 127)
        for (i in 'a'..'x') setTransition(126, i, 2)
        setTransition(126, 'z', 2)

        setTransition(127, 'g', 128)
        setTransition(128, 'o', 129)
        setTransition(129, 'n', 130)
        for (i in 'a'..'f') setTransition(127, i, 2)
        for (i in 'h'..'k') setTransition(127, i, 2)
        for (i in 'm'..'z') setTransition(127, i, 2)
        for (i in 'a'..'n') setTransition(128, i, 2)
        for (i in 'p'..'z') setTransition(128, i, 2)
        for (i in 'a'..'m') setTransition(129, i, 2)
        for (i in 'o'..'z') setTransition(129, i, 2)

        setTransition(127, 'l', 131)
        setTransition(131, 'i', 132)
        setTransition(132, 'n', 133)
        setTransition(133, 'e', 134)
        for (i in 'a'..'h') setTransition(131, i, 2)
        for (i in 'j'..'z') setTransition(131, i, 2)
        for (i in 'a'..'m') setTransition(132, i, 2)
        for (i in 'o'..'z') setTransition(132, i, 2)
        for (i in 'a'..'d') setTransition(133, i, 2)
        for (i in 'f'..'z') setTransition(133, i, 2)

		// Id - fixed
		setTransition(1, 'd', 135)
		setTransition(1, 'g', 135)
		setTransition(1, 'j', 135)
		setTransition(1, 'k', 135)
		setTransition(1, 'm', 135)
		for (i in 'p'..'s') setTransition(1, i, 135)
		for (i in 'u'..'z') setTransition(1, i, 135)
		for (i in 'D'..'K') setTransition(1, i, 135)
		for (i in 'M'..'O') setTransition(1, i, 135)
		setTransition(1, 'Q', 135)
		for (i in 'S'..'V') setTransition(1, i, 135)
		for (i in 'X'..'Z') setTransition(1, i, 135)
		setTransition(1, '_', 135)

		for (i in 'a'..'z') setTransition(135, i, 2)
		for (i in 'A'..'Z') setTransition(135, i, 2)
		for (i in '0'..'9') setTransition(135, i, 2)
		setTransition(135, '_', 2)

		// Strings
		setTransition(1, '"', 137)
		for (i in 'a'..'z') setTransition(137, i, 138)
		for (i in 'A'..'Z') setTransition(137, i, 138)
		for (i in '0'..'9') setTransition(137, i, 138)
		setTransition(137, ' ', 138)

		for (i in 'a'..'z') setTransition(138, i, 138)
		for (i in 'A'..'Z') setTransition(138, i, 138)
		for (i in '0'..'9') setTransition(138, i, 138)
		setTransition(138, ' ', 138)

		setTransition(138, '"', 139)


		// mapping end states to values
        setValue(2, ID_VALUE)
        setValue(3, FLOAT_VALUE)
        setValue(5, FLOAT_VALUE)
        setValue(8, NIL_VALUE)
        setValue(15, HEX_VALUE)
        setValue(19, CITY_VALUE)
        setValue(22, VAR_VALUE)
        setValue(24, IF_VALUE)
        setValue(28, ELIF_VALUE)
        setValue(30, ELSE_VALUE)
        setValue(33, AND_VALUE)
        setValue(35, OR_VALUE)
        setValue(39, TRUE_VALUE)
        setValue(44, BREAK_VALUE)
        setValue(47, FOR_VALUE)
        setValue(50, FUNCTION_VALUE)
        setValue(54, FALSE_VALUE)
        setValue(55, EQUALS_VALUE)
        setValue(56, ISSAME_VALUE)
        setValue(57, SEMI_VALUE)
        setValue(58, PLUS_VALUE)
        setValue(59, MINUS_VALUE)
        setValue(60, TIMES_VALUE)
        setValue(61, DIVIDE_VALUE)
        setValue(62, LPAREN_VALUE)
        setValue(63, RPAREN_VALUE)
        setValue(64, LBRACKET_VALUE)
        setValue(65, RBRACKET_VALUE)
        setValue(66, LBRACE_VALUE)
        setValue(67, RBRACE_VALUE)
        setValue(68, COMMA_VALUE)
        setValue(69, EXCLAMATION_VALUE)
        setValue(70, NOTSAME_VALUE)
        setValue(71, GT_VALUE)
        setValue(72, GTEQ_VALUE)
        setValue(73, LT_VALUE)
        setValue(74, LTEQ_VALUE)
		setValue(78, LINE_VALUE)
		setValue(83, WATER_VALUE)
		setValue(88, ANGLE_VALUE)
		setValue(94, RADIUS_VALUE)
		setValue(98, BEND_VALUE)
		setValue(100, BOX_VALUE)
		setValue(106, FOREST_VALUE)
		setValue(110, FIELD_VALUE)
		setValue(116, CIRCLE_VALUE)
		setValue(120, COLOR_VALUE)
		setValue(125, POINT_VALUE)
		setValue(130, POLYGON_VALUE)
		setValue(134, POLYLINE_VALUE)
		setValue(135, ID_VALUE)
		setValue(139, STRING_VALUE)
    }
}

data class Token(val value: Int, val lexeme: String, val startRow: Int, val startColumn: Int)

class Scanner(private val automaton: Automaton, private val stream: InputStream) {
    private var state = automaton.startState
    private var last: Int? = null
    private var buffer = LinkedList<Byte>()
    private var row = 1
    private var column = 1

    private fun updatePosition(symbol: Int) {
        if (symbol == NEWLINE) {
            row += 1
            column = 1
        } else {
            column += 1
        }
    }

    private fun getValue(): Int {
        var symbol = last ?: stream.read()
        state = automaton.startState

        while (true) {
            updatePosition(symbol)

            val nextState = automaton.next(state, symbol)
            if (nextState == ERROR_STATE) {
                if (automaton.finalStates.contains(state)) {
                    last = symbol
                    return automaton.value(state)
                } else throw Error("Invalid pattern at ${row}:${column}")
            }
            state = nextState
            buffer.add(symbol.toByte())
            symbol = stream.read()
        }
    }

    fun eof(): Boolean =
        last == EOF_SYMBOL

    fun getToken(): Token? {
        if (eof()) return null

        val startRow = row
        val startColumn = column
        buffer.clear()

        val value = getValue()
        return if (value == SKIP_VALUE)
            getToken()
        else {
            // println(String(buffer.toByteArray()))	// DEBUGGING
            Token(value, String(buffer.toByteArray()), startRow, startColumn)
        }
    }
}

fun name(value: Int) =
    when (value) {
        1 -> "Unit"
        2 -> "Float"
        3 -> "Hex"
        4 -> "City"
        5 -> "Var"
        6 -> "If"
        7 -> "Elif"
        8 -> "Else"
        9 -> "And"
        10 -> "Or"
        11 -> "For"
        12 -> "Break"
        13 -> "Function"
        14 -> "True"
        15 -> "False"
        16 -> "Point"
        17 -> "Color"
        18 -> "Line"
        19 -> "Bend"
        20 -> "Angle"
        21 -> "Box"
        22 -> "Radius"
        23 -> "Circle"
        24 -> "Polyline"
		25 -> "Polygon"
        26 -> "Water"
        27 -> "Park"
        28 -> "Forest"
        29 -> "Field"
        30 -> "Equals"
        31 -> "Semi"
        32 -> "Plus"
        33 -> "Minus"
        34 -> "Times"
        35 -> "Divide"
        36 -> "LParen"
        37 -> "RParen"
        38 -> "LBracket"
        39 -> "RBracket"
        40 -> "LBrace"
        41 -> "RBrace"
        42 -> "Comma"
        43 -> "Exclamation"
        44 -> "String"
        45 -> "Id"
		46 -> "IsSame"
		47 -> "NotSame"
		48 -> "GT"
		49 -> "LT"
		50 -> "GTEQ"
		51 -> "LTEQ"
        else -> throw Error("Invalid value")
    }

fun printTokens(scanner: Scanner) {
    val token = scanner.getToken()
    if (token != null) {
        print("${name(token.value)}(\"${token.lexeme}\") ")
        printTokens(scanner)
    }
}

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



fun main(args: Array<String>) {
    val scanner = Scanner(Example, "city{let varijabla = 4.4+100;let varijabla = 4.4+100;Water{}}".byteInputStream())
    // printTokens(scanner)
	if (Recognizer(scanner).recognize()) {
		println("accept")
	} else {
		println("reject")
    }
}
