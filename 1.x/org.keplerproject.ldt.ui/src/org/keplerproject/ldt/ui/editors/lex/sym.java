/*
* Copyright (C) 2003-2007 Kepler Project.
*
* Permission is hereby granted, free of charge, to any person obtaining
* a copy of this software and associated documentation files (the
* "Software"), to deal in the Software without restriction, including
* without limitation the rights to use, copy, modify, merge, publish,
* distribute, sublicense, and/or sell copies of the Software, and to
* permit persons to whom the Software is furnished to do so, subject to
* the following conditions:
*
* The above copyright notice and this permission notice shall be
* included in all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
* EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
* MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
* IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
* CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
* TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
* SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package org.keplerproject.ldt.ui.editors.lex;
/**
 * 
 * @author guilherme
 * @version $Id$
 */
public interface sym
{
  int GTEQ = 0;
  int END = 1;
  int GT = 2;
  int NOTEQ = 3;
  int LBRACE = 4;
  int EQ = 5;
  int DO = 6;
  int LT = 7;
  int CHARACTER_LITERAL = 8;
  int IN = 9;
  int EQEQ = 10;
  int BOOLEAN_LITERAL = 11;
  int OR = 12;
  int PLUS = 13;
  int RPAREN = 14;
  int FOR = 15;
  int THEN = 16;
  int FLOATING_POINT_LITERAL = 17;
  int DIV = 18;
  int IF = 19;
  int IDENTIFIER = 20;
  int MOD = 21;
  int LPAREN = 22;
  int LOCAL = 23;
  int BREAK = 24;
  int ELSEIF = 25;
  int RBRACK = 26;
  int SEMICOLON = 27;
  int MINUS = 28;
  int DOT = 29;
  int REPEAT = 30;
  int AND = 31;
  int COLON = 32;
  int LTEQ = 33;
  int FUNCTION = 34;
  int NOT = 35;
  int RBRACE = 36;
  int MULT = 37;
  int LBRACK = 38;
  int UNTIL = 39;
  int WHILE = 40;
  int NIL = 41;
  int ELSE = 42;
  int COMMA = 43;
  int RETURN = 44;
  int EOF = 45;
  int INTEGER_LITERAL = 46;
  int STRING_LITERAL = 47;
  int DBLBRACK = 48;
  int DBRBRACK = 49;
  int MLCOMMENT = 50;
  int MULTILINE_STRING = 51;
}
