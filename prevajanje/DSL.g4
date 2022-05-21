grammar DSL;

UNIT: 'nil';
FLOAT: [0-9]+('.'[0-9]+)?;  // ('+'|'-')? -> should this have a preffix or do i make that separate
COLOR: 'blue'|'teal'|'cyan'|'green'|'lime'|'yellow'|'pink'|'white'|'lightgray'|'gray'|'darkgray'|'black'|'olive'|'brown'|'orange'|'red'|'purple'|'magenta'|'violet'|HEX;
// STRING: '"' ~('\r'|'\n'|'"'|' ')+ '"';
ELIF: 'elif';
ELSE: 'else';
VAR: 'let';
TRUE: 'true';
FALSE: 'false';
HEX: '#'[a-fA-F0-9]{6};
ID: [_a-zA-Z][_a-zA-Z0-9]*;
WS: [ \n\t\r]+ -> skip;


city
    : 'city' ID block EOF
    ;

block
    : '{' blockStatements '}'
    ;

blockStatements
    : blockStatement blockStatements?
    ;

blockStatement
    : localVariableDeclaration
    | statement
    ;

localVariableDeclaration
    : VAR ID '=' expression ';'   // -> look at this more closely!
    ;

statement
    : 'if' parExpression statement (ELIF parExpression statement)? (ELSE statement)?        // -> look at this more closely!
    | 'for' '(' forControl ')' block
    | block
    ;

point
    : '(' expression ',' expression ')'
    ;

parExpression
    : '(' expression ')'
    ;

forInit
    : localVariableDeclaration
    | expressionList
    ;

list
    : '[' expressionList ']'
    ;

expressionList
    : expression (',' expression)*
    ;

arguments
    : '(' expressionList? ')'
    ;

forControl
    : forInit ';' comparison ';' forUpdate=expressionList       // TODO - simplify?
    ;

comparison
    : expression bop=('<=' | '>=' | '>' | '<') expression
    | expression bop=('==' | '!=') expression
    ;

expression
    : literal
    // TODO - add operations (+,-,*,/,())
    ;

literal
    : ID
    | FLOAT
    //| STRING
    ;

color
    : 'color' '(' COLOR ')'
    ;

command
    : line
    | bend
    | box
    | circle
    | polyline
    | polygon
    ;

line
    : 'line' '(' point ',' point ')'
    ;

angle
    : 'angle' '(' FLOAT ')'
    ;

bend
    : 'bend' '(' point ',' point ',' angle ')'
    ;

box
    : 'box' '(' point ',' point ')'
    ;

radius
    : 'radius' '(' FLOAT ')'
    ;

circle
    : 'circle' '(' point ',' radius ')'
    ;

points
    : point (',' points)?
    ;

polyline
    : 'polyline' '(' points ')'
    ;

polygon
    : 'polygon' '(' FLOAT ')'
    ;

function
    : 'func' ID arguments block
    ;