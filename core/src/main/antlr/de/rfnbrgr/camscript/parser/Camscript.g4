grammar Camscript;
tokens { INDENT, DEDENT }
@lexer::header {
  import com.yuvalshavit.antlr4.DenterHelper;
}
@lexer::members {
  private final DenterHelper denter = new DenterHelper(NL, CamscriptParser.INDENT, CamscriptParser.DEDENT)
  {
    @Override
    public Token pullToken() {
      return CamscriptLexer.super.nextToken();
    }
  };

  @Override
  public Token nextToken() {
    return denter.nextToken();
  }
}


script   : block;

block : statement+;

statement : singleLineStatement | blockStatement;

singleLineStatement : (say | wait_) NL;
blockStatement : repeat;

say : 'say' WS+ DOUBLE_QUOTED_STRING;
wait_: 'wait' WS+ duration;

repeat: 'repeat' WS+ INT WS+ 'times' INDENT block DEDENT;

duration: INT DURATION_UNIT;

NL: ('\r'? '\n' ' '*);

DURATION_UNIT : UNIT_MS | UNIT_S | UNIT_MIN;
SINGLE_QUOTED_STRING: '\'' (~['\r\n])* '\'';
DOUBLE_QUOTED_STRING: '"' (~["\r\n] | '""')* '"';

WS      : (' ' | '\t') ;
COLON           : ':';

INT            : DIGIT+;

UNIT_MS  : 'ms';
UNIT_S   : 's';
UNIT_MIN : 'min';

fragment DIGIT : [0-9];
ERR_CHAR : .;
