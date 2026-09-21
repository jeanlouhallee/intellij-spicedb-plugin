package com.authzed.intellij.spicedb.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;

import static com.authzed.intellij.spicedb.psi.SpiceDbTypes.*;

%%

%public
%class _SpiceDbLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

WHITE_SPACE=[ \t\r\n]+
LINE_COMMENT="//"[^\r\n]*
BLOCK_COMMENT="/*"([^*]|"*"+[^*/])*"*"+"/"
IDENTIFIER=[a-zA-Z_][a-zA-Z0-9_]*
NUMBER=[0-9]+
STRING=\"([^\"\r\n\\]|\\.)*\"

%%

<YYINITIAL> {
  {WHITE_SPACE}        { return TokenType.WHITE_SPACE; }
  {LINE_COMMENT}       { return LINE_COMMENT; }
  {BLOCK_COMMENT}      { return BLOCK_COMMENT; }

  "definition"         { return DEFINITION; }
  "partial"            { return PARTIAL; }
  "caveat"             { return CAVEAT; }
  "relation"           { return RELATION; }
  "permission"         { return PERMISSION; }
  "use"                { return USE; }
  "import"             { return IMPORT; }
  "with"               { return WITH; }
  "and"                { return AND; }
  "nil"                { return NIL; }

  "..."                { return DOTDOTDOT; }
  "->"                 { return ARROW; }
  "<="                 { return LE; }
  ">="                 { return GE; }
  "=="                 { return EQEQ; }
  "!="                 { return NEQ; }
  "&&"                 { return ANDAND; }
  "||"                 { return OROR; }

  "{"                  { return LBRACE; }
  "}"                  { return RBRACE; }
  "("                  { return LPAREN; }
  ")"                  { return RPAREN; }
  "["                  { return LBRACKET; }
  "]"                  { return RBRACKET; }
  "|"                  { return PIPE; }
  ":"                  { return COLON; }
  "#"                  { return HASH; }
  "*"                  { return STAR; }
  "+"                  { return PLUS; }
  "-"                  { return MINUS; }
  "&"                  { return AMP; }
  "="                  { return EQ; }
  "."                  { return DOT; }
  ","                  { return COMMA; }
  ";"                  { return SEMICOLON; }
  "<"                  { return LT; }
  ">"                  { return GT; }
  "!"                  { return BANG; }
  "?"                  { return QUESTION; }
  "%"                  { return PERCENT; }
  "/"                  { return SLASH; }

  {IDENTIFIER}         { return IDENTIFIER; }
  {NUMBER}             { return NUMBER; }
  {STRING}             { return STRING; }
}

[^] { return TokenType.BAD_CHARACTER; }
