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

singleLineStatement : (say | wait_ | setConfig ) NL;
blockStatement : repeat;

say : 'say' WS+ DOUBLE_QUOTED_STRING;

wait_: 'wait' WS+ DURATION;

setConfig: variableName WS+ '=' WS+ variableValue;

variableName: IDENTIFIER;
variableValue: SINGLE_QUOTED_STRING | DOUBLE_QUOTED_STRING | INT | FLOAT;


repeat: 'repeat' WS+ INT WS+ 'times' INDENT block DEDENT;


NL: ('\r'? '\n' ' '*);
DOUBLE_QUOTED_STRING: '"' (~["\r\n] | '\\"')* '"'
   {
     String s = getText();
     s = s.substring(1, s.length() - 1);
     s = s.replace("\\\"", "\"");
     setText(s);
   };
SINGLE_QUOTED_STRING: '\'' (~['\r\n] | '\\\'' )* '\''
   {
     String s = getText();
     s = s.substring(1, s.length() - 1);
     s = s.replace("\\'", "'");
     setText(s);
   };

WS      : (' ' | '\t') ;


DURATION: INT ('s' | 'ms' | 'min');
INT : [0-9]+;
FLOAT : [0-9]+ '.' [0-9]*;
IDENTIFIER: [A-Za-z_/] ([A-Za-z_/\-])*;


ERR_CHAR : .;
