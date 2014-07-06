parser grammar Functions;

// TODO: parameters

functionDeclaration :
   Identifier Identifier '(' ')'
   '{' codeBlock '}'
   ;

functionCall :
	Identifier '(' ')' ;