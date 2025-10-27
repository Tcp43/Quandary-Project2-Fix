package parser;

import java_cup.runtime.Symbol;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.ComplexSymbolFactory.Location;
import static parser.sym.*;
import interpreter.Interpreter;

%%

%public
%class Lexer
%cup
%implements sym
%char
%line
%column
%type java_cup.runtime.Symbol
%unicode

%{
    StringBuffer string = new StringBuffer();
    public Lexer(java.io.Reader in, ComplexSymbolFactory sf){
	this(in);
	symbolFactory = sf;
    }
    ComplexSymbolFactory symbolFactory;

  private Symbol symbol(String name, int sym) {
      return symbolFactory.newSymbol(name, sym, new Location(yyline+1,yycolumn+1,yyline+1), new Location(yyline+1,yycolumn+yylength(),yycolumn+1));
  }
  
  private Symbol symbol(String name, int sym, Object val) {
      Location left = new Location(yyline + 1, yycolumn + 1, yyline + 1);
      Location right = new Location(yyline + 1, yycolumn + yylength(), yycolumn + 1);
      return symbolFactory.newSymbol(name, sym, left, right, val);
  } 
  /*private Symbol symbol(String name, int sym, Object val, int buflength) {
      Location left = new Location(yyline + 1, yycolumn + yylength() - buflength, yychar + yylength() - buflength);
      Location right = new Location(yyline + 1, yycolumn + yylength(), yychar + yylength());
      return symbolFactory.newSymbol(name, sym, left, right, val);
  }*/      
  private void error(String message) {
    System.out.println("Error at line "+ (yyline + 1) + ", column " + (yycolumn + 1) + " : " + message);
  }
%} 

%eofval{
     return symbolFactory.newSymbol("EOF", EOF, new Location(yyline + 1, yycolumn + 1, yychar), new Location(yyline + 1, yycolumn + 1, yychar + 1));
%eofval}

Identifier = [A-Za-z_][A-Za-z0-9_]*

IntLiteral = 0 | [1-9][0-9]*

new_line = \r|\n|\r\n;

white_space = {new_line} | [ \t\f]

%%

<YYINITIAL>{
/* This is where tokens are recognized. Every token recognized by the scanner corresponds to a terminal in the parser's grammar. */

/* int literal token */
{IntLiteral}     { return symbol("INTCONST", INTCONST, Long.parseLong(yytext())); }

  "main"            { return symbol("MAIN", MAIN); }
  "randomInt"     { return symbol("RANDOMINT", RANDOMINT); }
  "int"            { return symbol("INT", INT); }
  "return"         { return symbol("RETURN", RETURN); }
  "print"          { return symbol("PRINT", PRINT); }
  "if"             { return symbol("IF", IF); }
  "else"           { return symbol("ELSE", ELSE); }
  ","              { return symbol(",", COMMA); }

  
  "<="             { return symbol("LE", LE); }
  ">="             { return symbol("GE", GE); }
  "=="             { return symbol("EQ", EQ); }
  "!="             { return symbol("NE", NE); }
  "&&"             { return symbol("AND", AND); }
  "||"             { return symbol("OR", OR); }
  "+"              { return symbol("PLUS", PLUS); }
  "-"              { return symbol("MINUS", MINUS); }
  "*"              { return symbol("TIMES", TIMES); }
  "("              { return symbol("LPAREN", LPAREN); }
  ")"              { return symbol("RPAREN", RPAREN); }
  "{"              { return symbol("LCURLY", LCURLY); }
  "}"              { return symbol("RCURLY", RCURLY); }
  "="              { return symbol("ASSIGN", ASSIGN); }
  "<"              { return symbol("LT", LT); }
  ">"              { return symbol("GT", GT); }
  "!"              { return symbol("NOT", NOT); }
  ";"              { return symbol("SEMICOLON", SEMICOLON); }

  /* ----- Identifier (AFTER keywords) ----- */
  {Identifier}     { return symbol("IDENT", IDENT, yytext()); }

/* You shouldn't need to modify anything below this */

/* comments */
"/*" [^*] ~"*/" | "/*" "*"+ "/"
                  { /* ignore comments */ }

{white_space}     { /* ignore */ }

}

/* error fallback */
[^]               { /*error("Illegal character <" + yytext() + ">");*/ Interpreter.fatalError("Illegal character <" + yytext() + ">", Interpreter.EXIT_PARSING_ERROR); }
