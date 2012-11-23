/**
 * CS1622 Project 3
 * Daniel Motles <dmm141@pitt.edu>
 */
import java_cup.runtime.Symbol;
%%

// Call the class "MiniJavaLexer"
%class MiniJavaLexer
%cup
%line
%column

%{
    private Symbol symbol(int type) {
        return new Symbol(type, yyline+1, yycolumn+1);
    }

    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline+1, yycolumn+1, value);
    }
%}

/* White Space */
LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace     = {LineTerminator} | [ \t\f]

/* comments */
Comment = {TraditionalComment} | {EndOfLineComment} | {DocumentationComment}

TraditionalComment   = "/*" [^*] ~"*/" | "/*" "*"+ "/"
EndOfLineComment     = "//" {InputCharacter}* {LineTerminator}
DocumentationComment = "/**" {CommentContent} "*"+ "/"
CommentContent       = ( [^*] | \*+ [^/*] )*


/* Primitive character classes */
IDENTIFIER           = [:letter:]+ ( [:letter:] | [:digit:] | _ )*
INT                  = 0 | [1-9] [0-9]*

%%
<YYINITIAL> {
    "public"                { return symbol( sym.PUBLIC ); }
    "class"                 { return symbol( sym.CLASS ); }
    "static"                { return symbol( sym.STATIC ); }
    "void"                  { return symbol( sym.VOID ); }
    "main"                  { return symbol( sym.MAIN ); }
    "String"                { return symbol( sym.STRING ); }
    "extends"               { return symbol( sym.EXTENDS ); }
    "return"                { return symbol( sym.RETURN ); }
    "true"                  { return symbol( sym.TRUE ); }
    "false"                 { return symbol( sym.FALSE ); }
    "int"                   { return symbol( sym.INT ); }
    "boolean"               { return symbol( sym.BOOLEAN ); }
    "this"                  { return symbol( sym.THIS ); }
    "if"                    { return symbol( sym.IF ); }
    "else"                  { return symbol( sym.ELSE ); }
    "while"                 { return symbol( sym.WHILE ); }
    "System.out.println"    { return symbol( sym.PRINTLN ); }
    "length"                { return symbol( sym.LENGTH ); }
    "new"                   { return symbol( sym.NEW ); }
    "void"                  { return symbol( sym.VOID ); }
    "length"                { return symbol( sym.LENGTH ); }
    "new"                   { return symbol( sym.NEW ); }
    "&&"                    { return symbol( sym.AND ); }
    "*"                     { return symbol( sym.TIMES ); }
    "+"                     { return symbol( sym.PLUS ); }
    "-"                     { return symbol( sym.MINUS ); }
    "<"                     { return symbol( sym.LESSTHAN ); }
    "."                     { return symbol( sym.DOT ); }
    ";"                     { return symbol( sym.SEMICOLON ); }
    ","                     { return symbol( sym.COMMA ); }
    "="                     { return symbol( sym.EQUALS ); }
    "("                     { return symbol( sym.LPAREN ); }
    ")"                     { return symbol( sym.RPAREN ); }
    "["                     { return symbol( sym.LBRACKET ); }
    "]"                     { return symbol( sym.RBRACKET ); }
    "{"                     { return symbol( sym.LBRACE ); }
    "}"                     { return symbol( sym.RBRACE ); }
    "!"                     { return symbol( sym.BANG ); }
    {INT}                   {
                                Integer val = Integer.parseInt( yytext() );
                                return symbol( sym.INTLITERAL, val );
                            }
    {IDENTIFIER}            { return symbol( sym.IDENTIFIER, yytext() ); }
    {Comment}               { /* ignore */ }
    {WhiteSpace}            { /* ignore */ }
}
.|\n                             { /*System.out.println( output.toString() );*/
                                   throw new Error("Illegal something <"+
                                                    yytext()+">"); }
