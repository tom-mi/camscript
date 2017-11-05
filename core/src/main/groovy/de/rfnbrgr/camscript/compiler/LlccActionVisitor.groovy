package de.rfnbrgr.camscript.compiler

import de.rfnbrgr.camscript.device.CameraContext
import de.rfnbrgr.camscript.llcc.CaptureAction
import de.rfnbrgr.camscript.llcc.CompileError
import de.rfnbrgr.camscript.llcc.LlccAction
import de.rfnbrgr.camscript.llcc.SayAction
import de.rfnbrgr.camscript.llcc.SetConfigAction
import de.rfnbrgr.camscript.llcc.WaitAction
import de.rfnbrgr.camscript.parser.CamscriptBaseVisitor
import de.rfnbrgr.camscript.parser.CamscriptParser
import groovy.util.logging.Slf4j
import org.antlr.v4.runtime.ParserRuleContext

@Slf4j
class LlccActionVisitor extends CamscriptBaseVisitor<List<LlccAction>> {

    CameraContext cameraContext
    List<CompileError> errors

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
        if (ctx.DURATION().text == '<missing DURATION>') {
            return []
        }
        def durationMilliseconds = parseDuration(ctx.DURATION().text)
        [new WaitAction(durationMilliseconds: durationMilliseconds)]
    }

    private static int parseDuration(duration) {
        def match = (duration =~ /(\d+)(.+)/)
        if (!match.matches()) {
            throw new IllegalStateException("Cannot parse duration [$duration]")
        }
        def value = match.group(1)
        def unit = match.group(2)
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
        if (validateVariableName(name, ctx.variableName()) && validateVariableValue(name, value, ctx.variableValue())) {
            def canonicalName = cameraContext?.variableContext(name)?.canonicalName ?: name
            [SetConfigAction.of(canonicalName, value)]
        } else {
            []
        }
    }

    @Override
    List<LlccAction> visitCapture(CamscriptParser.CaptureContext ctx) {
        [new CaptureAction()]
    }

    private boolean validateVariableName(String name, CamscriptParser.VariableNameContext ctx) {
        if (cameraContext != null) {
            if (!(name in cameraContext.variables)) {
                errors << errorFromContext(ctx, "Unknown variable [$name]")
                return false
            }
        }
        return true
    }

    private boolean validateVariableValue(String name, String value, CamscriptParser.VariableValueContext ctx) {
        if (cameraContext != null) {
            if (!cameraContext.variableContext(name).validate(value)) {
                errors << errorFromContext(ctx, "Invalid value [$value] for variable [$name]")
            }
        }
        return true
    }

    private static CompileError errorFromContext(ParserRuleContext ctx, String message) {
        new CompileError(ctx.start.line, ctx.start.charPositionInLine, ctx.start.startIndex, ctx.stop.stopIndex, message)
    }
}
