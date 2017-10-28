package de.rfnbrgr.camscript.compiler

import de.rfnbrgr.camscript.llcc.LlccAction

import de.rfnbrgr.camscript.llcc.SayAction
import de.rfnbrgr.camscript.llcc.WaitAction
import de.rfnbrgr.camscript.parser.CamscriptLexer
import de.rfnbrgr.camscript.parser.CamscriptListener
import de.rfnbrgr.camscript.parser.CamscriptParser
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.ErrorNode
import org.antlr.v4.runtime.tree.TerminalNode

class ListeningCompiler implements CamscriptListener {

    List<LlccAction> actions = []

    @Override
    void enterScript(CamscriptParser.ScriptContext ctx) {

    }

    @Override
    void exitScript(CamscriptParser.ScriptContext ctx) {

    }

    @Override
    void enterBlock(CamscriptParser.BlockContext ctx) {

    }

    @Override
    void exitBlock(CamscriptParser.BlockContext ctx) {

    }

    @Override
    void enterStatement(CamscriptParser.StatementContext ctx) {

    }

    @Override
    void exitStatement(CamscriptParser.StatementContext ctx) {

    }

    @Override
    void enterSingleLineStatement(CamscriptParser.SingleLineStatementContext ctx) {

    }

    @Override
    void exitSingleLineStatement(CamscriptParser.SingleLineStatementContext ctx) {

    }

    @Override
    void enterSay(CamscriptParser.SayContext ctx) {
    }

    @Override
    void exitSay(CamscriptParser.SayContext ctx) {
        actions << new SayAction(text: ctx.DOUBLE_QUOTED_STRING().getText()[1..-2])
    }

    @Override
    void enterWait_(CamscriptParser.Wait_Context ctx) {

    }

    @Override
    void exitWait_(CamscriptParser.Wait_Context ctx) {
        def durationMilliseconds = parseDuration(ctx.duration().INT().text, ctx.duration().DURATION_UNIT())
        actions << new WaitAction(durationMilliseconds: durationMilliseconds)
    }

    private static int parseDuration(duration, unit) {
        switch (unit) {
            case 'ms': return duration as Integer
            case 's': return (duration as Integer) * 1000
            case 'min': return (duration as Integer) * 60_000
            default: throw new RuntimeException("Encountered unknown duration unit $unit")
        }
    }

    @Override
    void enterDuration(CamscriptParser.DurationContext ctx) {

    }

    @Override
    void exitDuration(CamscriptParser.DurationContext ctx) {

    }

    @Override
    void visitTerminal(TerminalNode node) {

    }

    @Override
    void visitErrorNode(ErrorNode node) {

    }

    @Override
    void enterEveryRule(ParserRuleContext ctx) {

    }

    @Override
    void exitEveryRule(ParserRuleContext ctx) {

    }
}
