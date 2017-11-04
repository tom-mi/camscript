package de.rfnbrgr.camscript.compiler

import de.rfnbrgr.camscript.llcc.CompileError
import de.rfnbrgr.camscript.llcc.Llcc
import de.rfnbrgr.camscript.parser.CamscriptLexer
import de.rfnbrgr.camscript.parser.CamscriptParser
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.antlr.v4.runtime.atn.ATNSimulator

class CamscriptCompiler {

    List<CompileError> errors = []

    static class ErrorListener extends BaseErrorListener {

        List<CompileError> errors

        @Override
        void syntaxError(Recognizer<?, ? extends ATNSimulator> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
            super.syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e)
            errors << new CompileError(line, charPositionInLine, msg)
        }

    }

    Llcc compile(String source) {
        def tree = parseSource(source)
        def actions = compileActions(tree)
        return new Llcc(actions, errors, true)
    }

    private parseSource(String source) {
        def parser = createParser(source)
        parser.addErrorListener(new ErrorListener(errors: errors))
        def tree = parser.script()

        return tree
    }

    private static createParser(String input) {
        def inputStream = new ANTLRInputStream(input)
        def lexer = new CamscriptLexer(inputStream)
        def tokens = new CommonTokenStream(lexer)
        new CamscriptParser(tokens)
    }

    private static compileActions(CamscriptParser.ScriptContext tree) {
        def visitor = new LlccActionVisitor()
        visitor.visitScript(tree)
    }
}
