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
        src                     | expectedAction
        'say "Hello World!"\n'  | new SayAction(text: 'Hello World!')
        'wait 5ms\n'            | new WaitAction(durationMilliseconds: 5)
        'wait 10s\n'            | new WaitAction(durationMilliseconds: 10_000)
        'wait 42min\n'          | new WaitAction(durationMilliseconds: 42 * 60 * 1000)
    }

}
