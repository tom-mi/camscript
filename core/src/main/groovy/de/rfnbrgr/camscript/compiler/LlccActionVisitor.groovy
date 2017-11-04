package de.rfnbrgr.camscript.compiler

import de.rfnbrgr.camscript.llcc.LlccAction
import de.rfnbrgr.camscript.llcc.SayAction
import de.rfnbrgr.camscript.llcc.WaitAction
import de.rfnbrgr.camscript.parser.CamscriptBaseVisitor
import de.rfnbrgr.camscript.parser.CamscriptParser

class LlccActionVisitor extends CamscriptBaseVisitor<List<LlccAction>> {

    @Override
    protected List<LlccAction> aggregateResult(List<LlccAction> aggregate, List<LlccAction> nextResult) {
        if (aggregate == null) {
            return nextResult
        } else if (nextResult == null) {
            return aggregate
        }
        aggregate + nextResult
    }

    @Override
    List<LlccAction> visitSay(CamscriptParser.SayContext ctx) {
        [new SayAction(text: ctx.DOUBLE_QUOTED_STRING().getText()[1..-2])]
    }

    @Override
    List<LlccAction> visitWait_(CamscriptParser.Wait_Context ctx) {
        def durationMilliseconds = parseDuration(ctx.duration().INT().text, ctx.duration().DURATION_UNIT())
        [new WaitAction(durationMilliseconds: durationMilliseconds)]
    }

    private static int parseDuration(duration, unit) {
        switch (unit) {
            case 'ms': return duration as Integer
            case 's': return (duration as Integer) * 1000
            case 'min': return (duration as Integer) * 60_000
            default: throw new RuntimeException("Encountered unknown duration unit $unit")
        }
    }
}
