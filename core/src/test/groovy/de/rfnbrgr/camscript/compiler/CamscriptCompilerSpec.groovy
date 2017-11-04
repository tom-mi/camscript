package de.rfnbrgr.camscript.compiler

import de.rfnbrgr.camscript.llcc.CompileError
import de.rfnbrgr.camscript.llcc.SayAction
import de.rfnbrgr.camscript.llcc.SetConfigAction
import de.rfnbrgr.camscript.llcc.WaitAction
import spock.lang.Specification
import spock.lang.Unroll

class CamscriptCompilerSpec extends Specification {

    @Unroll
    def 'simple statement [#src]'() {
        when:
        def llcc = new CamscriptCompiler().compile(src)

        then:
        llcc.actions.size() == 1
        llcc.actions.first() == expectedAction
        llcc.errors.size() == 0

        where:
        src                    | expectedAction
        'say "Hello World!"\n' | new SayAction(text: 'Hello World!')
        'wait 5ms\n'           | new WaitAction(durationMilliseconds: 5)
        'wait 10s\n'           | new WaitAction(durationMilliseconds: 10_000)
        'wait 42min\n'         | new WaitAction(durationMilliseconds: 42 * 60 * 1000)
        'aperture = "5.6"\n'   | new SetConfigAction('aperture', '5.6')
    }

    @Unroll
    def 'multiline script [#src]'() {
        when:
        def llcc = new CamscriptCompiler().compile(src)

        then:
        llcc.actions == expectedSequence
        llcc.errors.size() == 0

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
    def 'compiler errors are reported [#src]'() {
        when:
        def llcc = new CamscriptCompiler().compile(src)

        then:
        llcc.errors == expectedErrors

        where:
        src        | expectedErrors
        'wait 5\n' | [new CompileError(1, 5, "extraneous input '5' expecting {WS, DURATION}")]
    }

}
