grammar Camscript;
@header { package de.rfnbrgr.camscript.parser; }

script   : block;

block: statement+;

statement: singleLineStatement;

singleLineStatement : (say | wait_) NEWLINE;

say : 'say' WHITESPACE+ DOUBLE_QUOTED_STRING;
wait_: 'wait' WHITESPACE+ duration;

duration: INT DURATION_UNIT;

DURATION_UNIT : UNIT_MS | UNIT_S | UNIT_MIN;
SINGLE_QUOTED_STRING: '\'' (~['\r\n])* '\'';
DOUBLE_QUOTED_STRING: '"' (~["\r\n] | '""')* '"';

WHITESPACE      : (' ' | '\t') ;
NEWLINE         : ('\r'? '\n' | '\r')+ ;
COLON           : ':';

INT            : DIGIT+;

UNIT_MS  : 'ms';
UNIT_S   : 's';
UNIT_MIN : 'min';

fragment DIGIT : [0-9];
