import java_cup.runtime.Symbol;

%%

%class MiniJavaLexer

%implements sym
%cup
%line
%column
%eofclose

%{
private Symbol symbol(int type) {
	return new Symbol(type, yyline+1, yycolumn+1);
}
private Symbol symbol(int type, Object value) {
	return new Symbol(type, yyline+1, yycolumn+1, value);
}
%} 

ID = [a-zA-Z][a-zA-Z0-9_]*
Integer = 0 | [1-9][0-9]*

new_line = \r|\n|\r\n|\z
tab = \t
white_space = {new_line} | {tab} | [ \f]


InputCharacter = [^\r\n]
EndOfLineComment = "//" {InputCharacter}* 

%%

/* keywords */
"public"	{ return symbol(PUBLIC); }
"static"	{ return symbol(STATIC); }
"void"		{ return symbol(VOID); }
"main"		{ return symbol(MAIN); }
"String"	{ return symbol(STRING); }
"class"		{ return symbol(CLASS); }
"extends"	{ return symbol(EXTENDS); }
"return"	{ return symbol(RETURN); }
"int"		{ return symbol(INT); }
"boolean"	{ return symbol(BOOLEAN); }
"if"		{ return symbol(IF); }
"else"		{ return symbol(ELSE); }
"while"		{ return symbol(WHILE); }
"System.out.println"	{ return symbol(PRINTLN); }
"length"	{ return symbol(LENGTH); }
"true"		{ return symbol(TRUE); }
"false"		{ return symbol(FALSE); }
"this"		{ return symbol(THIS); }
"new"		{ return symbol(NEW); }

/* literals */
{Integer}	{ return symbol(INTEGER, new Integer(Integer.parseInt(yytext()))); }

";"		{ return symbol(SEMICOLON); }
"("		{ return symbol(LPAREN); }
")"		{ return symbol(RPAREN); }
"["		{ return symbol(LBRACKET); }
"]"		{ return symbol(RBRACKET); }
"&&"		{ return symbol(AND); }
"<"		{ return symbol(LESSTHAN); }
"+"		{ return symbol(PLUS); }
"-"		{ return symbol(MINUS); }
"*"		{ return symbol(TIMES); }
"{"		{ return symbol(LBRACE); }
"}"		{ return symbol(RBRACE);  }
"!"		{ return symbol(NOT); }
","		{ return symbol(COMMA); }
"."		{ return symbol(DOT); }
"="		{ return symbol(EQUAL); }

{ID}		{ return symbol(ID, yytext());  }

{white_space}		{ /* ignore */ }
{EndOfLineComment}	{ /* ignore */ }
"/*" ~ "*/"		{ /* ignore */ }

/* error fallback */
.|\n	{  /* throw new Error("Illegal character <"+ yytext()+">");*/
		System.err.println("Illegal character <"+ yytext()+">");
		System.exit(1);
	}
