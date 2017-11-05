package de.rfnbrgr.camscript.compiler

import de.rfnbrgr.camscript.device.CameraContext
import de.rfnbrgr.camscript.device.FloatRange
import de.rfnbrgr.camscript.device.VariableContext
import de.rfnbrgr.camscript.device.VariableType
import de.rfnbrgr.camscript.llcc.CaptureAction
import de.rfnbrgr.camscript.llcc.CompileError
import de.rfnbrgr.camscript.llcc.SayAction
import de.rfnbrgr.camscript.llcc.SetConfigAction
import de.rfnbrgr.camscript.llcc.WaitAction
import spock.lang.Specification
import spock.lang.Unroll

class CamscriptCompilerSpec extends Specification {

    @Unroll
    def 'context-independent - simple statement [#src]'() {
        when:
        def llcc = new CamscriptCompiler().compile(src)

        then:
        llcc.actions.size() == 1
        llcc.actions.first() == expectedAction
        llcc.errors.size() == 0
        !llcc.isExecutable

        when:
        def llccWithContext = new CamscriptCompiler(cameraContext: setupContext()).compile(src)

        then:
        llccWithContext.actions.size() == 1
        llccWithContext.actions.first() == expectedAction
        llccWithContext.errors.size() == 0
        llccWithContext.isExecutable

        where:
        src                    | expectedAction
        'say "Hello World!"\n' | new SayAction(text: 'Hello World!')
        'wait 5ms\n'           | new WaitAction(durationMilliseconds: 5)
        'wait 10s\n'           | new WaitAction(durationMilliseconds: 10_000)
        'wait 42min\n'         | new WaitAction(durationMilliseconds: 42 * 60 * 1000)
        'capture'              | new CaptureAction()
    }

    @Unroll
    def 'context-independent - multiline script [#src]'() {
        when:
        def llcc = new CamscriptCompiler().compile(src)

        then:
        llcc.actions == expectedSequence
        llcc.errors.size() == 0
        !llcc.isExecutable

        when:
        def llccWithContext = new CamscriptCompiler(cameraContext: setupContext()).compile(src)

        then:
        llccWithContext.actions == expectedSequence
        llccWithContext.errors.size() == 0
        llccWithContext.isExecutable

        where:
        src            | expectedSequence
        REPEAT_SCRIPT  | [new SayAction(text: 'Again and again...')] * 5
        REPEAT_0_TIMES | []
    }

    final static REPEAT_SCRIPT = '''\
        repeat 5 times
            say "Again and again..."
        '''.stripIndent()
    final static REPEAT_0_TIMES = '''\
        repeat 0 times
            say "Never again..."
        '''.stripIndent()

    @Unroll
    def 'context-independent - compiler errors are reported [#src]'() {
        when:
        def llcc = new CamscriptCompiler().compile(src)

        then:
        llcc.errors == expectedErrors

        where:
        src         | expectedErrors
        'wait 42\n' | [new CompileError(1, 5, 5, 6, "extraneous input '42' expecting {WS, DURATION}")]
    }

    @Unroll
    def 'without context - simple statement [#src]'() {
        when:
        def llcc = new CamscriptCompiler().compile(src)

        then:
        llcc.actions.size() == 1
        llcc.actions.first() == expectedAction
        llcc.errors.size() == 0
        !llcc.isExecutable

        where:
        src                  | expectedAction
        'aperture = "5.6"\n' | SetConfigAction.of('aperture', '5.6')
    }

    @Unroll
    def 'with context - simple statement [#src]'() {
        when:
        def llcc = new CamscriptCompiler(cameraContext: setupContext()).compile(src)

        then:
        llcc.actions.size() == 1
        llcc.actions.first() == expectedAction
        llcc.errors.size() == 0
        llcc.isExecutable

        where:
        src                  | expectedAction
        'aperture = "5.6"\n' | SetConfigAction.of('/path/aperture', '5.6')
        'aperture = 5.6\n'   | SetConfigAction.of('/path/aperture', '5.6')
        'correction = "3"'   | SetConfigAction.of('/path/correction', '3')
    }

    @Unroll
    def 'with context - compiler errors are reported [#src]'() {
        when:
        def llcc = new CamscriptCompiler(cameraContext: setupContext()).compile(src)

        then:
        llcc.errors == expectedErrors
        !llcc.isExecutable

        where:
        src                  | expectedErrors
        'hole = "2"'         | [new CompileError(1, 0, 0, 3, 'Unknown variable [hole]')]
        'aperture = "2"'     | [new CompileError(1, 11, 11, 13, 'Invalid value [2] for variable [aperture]')]
        'correction = "2.5"' | [new CompileError(1, 13, 13, 17, 'Invalid value [2.5] for variable [correction]')]
        'count = "fortytwo"' | [new CompileError(1, 8, 8, 17, 'Invalid value [fortytwo] for variable [count]')]
    }

    private static setupContext() {
        def contextMap = [
                aperture  : new VariableContext(VariableType.CHOICE, '/path/aperture', ['4', '4.5', '5', '5.6', '6.3', '7.1', '8'], null),
                correction: new VariableContext(VariableType.FLOAT_RANGE, '/path/correction', [], new FloatRange(-3f, 3f, 1f)),
                count     : new VariableContext(VariableType.INTEGER, '/path/count', [], null),
        ]

        new CameraContext(context: contextMap)
    }
}
