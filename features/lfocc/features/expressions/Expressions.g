parser grammar Expressions;

expression :
	| internalExpression
	| '(' expression ')'
	;
	
internalExpression :
	comparison
	;

comparison :
	addition (
	( '==' | '!=' | '<' | '<=' | '>' | '>='	)
	addition )?
	;
	
addition :
	multiplication (
	( '+' | '-' | '||' )
	multiplication )?
	;
	
multiplication :
	unsigned (
	( '*' | '/' | '%' | '&&' )
	unsigned )?
	;

unsigned :
	( '+' | '-' )? externalExpression
	;