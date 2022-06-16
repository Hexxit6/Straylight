grammar DSL;

//================
// TOKENS
//================

UNIT: 'nil';
FLOAT: [0-9]+('.'[0-9]+)?;
HEX: '#'[a-fA-F0-9]{6};

CITY: 'city';
VAR: 'let';
IF: 'if';
ELIF: 'elif';
ELSE: 'else';
AND: 'and';
OR: 'or';
FOR: 'for';
BREAK: 'break';
FUNCTION: 'func';
TRUE: 'true';
FALSE: 'false';
WS: [ \n\t\r]+ -> skip;

// other keywords:
POINT: 'Point';
COLOR: 'Color';
LINE: 'Line';
BEND: 'Bend';
ANGLE: 'Angle';
BOX: 'Box';
RADIUS: 'Radius';
CIRCLE: 'Circle';
POLYLINE: 'Polyline';
POLYGON: 'Polygon';

WATER: 'Water';
PARK: 'Park';
FOREST: 'Forest';
FIELD: 'Field';

EQUALS: '=';
SEMI: ';';
PLUS: '+';
MINUS: '-';
TIMES: '*';
DIVIDE: '/';
LPAREN: '(';
RPAREN: ')';
LBRACKET: '[';
RBRACKET: ']';
LBRACE: '{';
RBRACE: '}';
COMMA: ',';
EXCLAMATION: '!';

STRING: '"' ~('\r'|'\n'|'"')+ '"';
ID: [_a-zA-Z]+[_a-zA-Z0-9]*;

// COLOR: 'blue'|'teal'|'cyan'|'green'|'lime'|'yellow'|'pink'|'white'|'lightgray'|'gray'|'darkgray'|'black'|'olive'|'brown'|'orange'|'red'|'purple'|'magenta'|'violet';
// colors are from latex


//================
// RULES
//================
init
    : city
    | function init
    ;

city
	: CITY ID block EOF
	;

block
	: LBRACE (statements|) RBRACE
	;

statements
	: statement statements?
	;

statement
	: initialization
	| assignment
	| flow
	| for
	| BREAK
	| funcall
    | line
    | bend
    | box
    | circle
    | polyline
    | polygon
    | water
    | park
    | forest
    | field
	;

initialization
	: VAR ID EQUALS values SEMI
	;

assignment
	: ID EQUALS values SEMI
	;

values
    : expr
    | STRING
    | point
    | list
    ;

// expressions LL(1):
expr
    : term expr1
    ;

expr1
    : PLUS term expr1
    | MINUS term expr1
    |
    ;

term
    : factor term1
    ;

term1
    : TIMES factor term1
    | DIVIDE factor term1
    |
    ;

factor
    : LPAREN expr RPAREN
    | ID
    | FLOAT
    | MINUS FLOAT   // ????? -> is this a good solution? -> it fixes negative values but it also allows for: -1--1 != 1
    ;

point
    : POINT LPAREN expr COMMA expr RPAREN
    ;

list
    : LBRACKET values (COMMA values)* RBRACKET
    ;

flow
    : IF condition block (ELIF condition block)* (ELSE block)?
    ;

condition
    : LPAREN comparison (AND comparison|OR comparison)* RPAREN
    ;

// comparisons LL(1):
comparison
    : expr comp
    ;

comp
    : '<=' expr
    | '>=' expr
    |  '<' expr
    | '>' expr
    | '==' expr
    | '!=' expr
    ;


// for (val var=1; 1>2 and 3==3; var = var+1) {}
for
    : FOR LPAREN initialization comparison ((AND comparison|OR comparison)+)? SEMI ID EQUALS expr RPAREN block
    ;

// function definition and function call:
function
    : FUNCTION ID LPAREN (VAR ID (COMMA VAR ID)*)? RPAREN block
    ;

funcall
    : EXCLAMATION ID LPAREN (values (COMMA values)*)? RPAREN
    ;

// predefined functions:
line
    : LINE LPAREN point COMMA point RPAREN
    ;

angle
    : ANGLE LPAREN expr RPAREN
    ;

bend
    : BEND LPAREN point COMMA point COMMA angle RPAREN
    ;

box
    : BOX LPAREN point COMMA point RPAREN
    ;

radius
    : RADIUS LPAREN expr RPAREN
    ;

circle
    : CIRCLE LPAREN point COMMA radius RPAREN
    ;

points
    : point (COMMA point)*
    ;

polyline
    : POLYLINE LPAREN points RPAREN
    ;

polygon
    : POLYGON LPAREN FLOAT RPAREN
    ;

// code blocks (water, park, ...):
water
    : WATER block
    ;

park
    : PARK block
    ;

forest
    : FOREST block
    ;

field
    : FIELD block
    ;