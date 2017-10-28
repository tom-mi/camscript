package de.rfnbrgr.camscript.compiler

import de.rfnbrgr.camscript.llcc.LlccAction
import de.rfnbrgr.camscript.parser.CamscriptLexer
import de.rfnbrgr.camscript.parser.CamscriptParser
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTreeWalker

class CamscriptCompiler {

    List<LlccAction> compile(String source) {
        def tree = parseSource(source)
        def actions = compileActions(tree)
        return actions
    }

    private static parseSource(String source) {
        def parser = createParser(source)
        parser.addErrorListener(new ThrowingErrorListener())
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
        def walker = new ParseTreeWalker()
        def listener = new ListeningCompiler()
        walker.walk(listener, tree)
        return listener.actions
    }
}
