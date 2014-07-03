parser grammar Variables;

variableDeclaration : Identifier Identifier ';' ;

parameterDeclaration : Identifier Identifier
		( ',' Identifier Identifier )* ;
		
variableUse : Identifier ;