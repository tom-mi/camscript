package de.rfnbrgr.camscript.compiler

import de.rfnbrgr.camscript.llcc.LlccAction
import de.rfnbrgr.camscript.llcc.SayAction
import de.rfnbrgr.camscript.llcc.SetConfigAction
import de.rfnbrgr.camscript.llcc.WaitAction
import de.rfnbrgr.camscript.parser.CamscriptBaseVisitor
import de.rfnbrgr.camscript.parser.CamscriptParser
import groovy.util.logging.Slf4j

@Slf4j
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
        [new SayAction(text: ctx.DOUBLE_QUOTED_STRING().text)]
    }

    @Override
    List<LlccAction> visitWait_(CamscriptParser.Wait_Context ctx) {
        def durationMilliseconds = parseDuration(ctx.DURATION().text)
        [new WaitAction(durationMilliseconds: durationMilliseconds)]
    }

    private static int parseDuration(duration) {
        println duration
        def match = (duration =~ /(\d+)(.+)/)
        if (!match.matches()) {
            throw new IllegalStateException("Cannot parse duration [$duration]")
        }
        def value = match[0][1]
        def unit = match[0][2]
        switch (unit) {
            case 'ms': return value as Integer
            case 's': return (value as Integer) * 1000
            case 'min': return (value as Integer) * 60_000
            default: throw new RuntimeException("Encountered unknown duration unit $unit")
        }
    }

    @Override
    List<LlccAction> visitRepeat(CamscriptParser.RepeatContext ctx) {
        def count = ctx.INT().getText() as Integer
        if (count > 0) {
            (1..count).collectMany {
                visitBlock(ctx.block())
            }
        } else {
            []
        }
    }

    @Override
    List<LlccAction> visitSetConfig(CamscriptParser.SetConfigContext ctx) {
        def name = ctx.variableName().text
        def value = ctx.variableValue().text
        [new SetConfigAction(name, value)]
    }
}
