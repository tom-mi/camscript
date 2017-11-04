package de.rfnbrgr.camscript.compiler

import de.rfnbrgr.camscript.llcc.SayAction
import de.rfnbrgr.camscript.llcc.WaitAction
import spock.lang.Specification
import spock.lang.Unroll

class CamscriptCompilerSpec extends Specification {

    @Unroll
    def 'simple statement [#src]'() {
        when:
        def sequence = new CamscriptCompiler().compile(src)

        then:
        sequence.size() == 1
        sequence.first() == expectedAction

        where:
        src                    | expectedAction
        'say "Hello World!"\n' | new SayAction(text: 'Hello World!')
        'wait 5ms\n'           | new WaitAction(durationMilliseconds: 5)
        'wait 10s\n'           | new WaitAction(durationMilliseconds: 10_000)
        'wait 42min\n'         | new WaitAction(durationMilliseconds: 42 * 60 * 1000)
    }

    @Unroll
    def 'multiline script [#src]'() {
        when:
        def sequence = new CamscriptCompiler().compile(src)

        then:
        sequence == expectedSequence

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
}
