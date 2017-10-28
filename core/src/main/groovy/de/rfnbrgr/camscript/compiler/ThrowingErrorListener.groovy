package de.rfnbrgr.camscript.compiler

import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.antlr.v4.runtime.atn.ATNSimulator


class ThrowingErrorListener extends BaseErrorListener {

    @Override
    void syntaxError(Recognizer<?, ? extends ATNSimulator> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        super.syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e)
        throw new ParseError(offendingSymbol, line, charPositionInLine, msg)
    }
}
