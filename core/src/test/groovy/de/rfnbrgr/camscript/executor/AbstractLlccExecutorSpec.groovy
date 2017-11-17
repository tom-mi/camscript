package de.rfnbrgr.camscript.executor

import de.rfnbrgr.camscript.device.Connection
import de.rfnbrgr.camscript.device.ExecutionOutput
import de.rfnbrgr.camscript.llcc.CaptureAction
import de.rfnbrgr.camscript.llcc.Llcc
import de.rfnbrgr.camscript.llcc.LlccAction
import de.rfnbrgr.camscript.llcc.SayAction
import spock.lang.Specification

class AbstractLlccExecutorSpec extends Specification {

    Connection mockConnection
    TestLlccExecutor executor

    def setup() {
        mockConnection = Mock(Connection)
        executor = new TestLlccExecutor(connection: mockConnection)
    }

    def 'does not execute non-executable llcc'() {
        setup:
        def llcc = new Llcc([new SayAction('Hello')], [], false)

        when:
        executor.execute(llcc)

        then:
        thrown(ExecutorError)
    }

    def 'executes executable llcc'() {
        setup:
        def actions = [new SayAction('Hello'), new CaptureAction()]

        when:
        executor.execute(setupExecutableLlcc(actions))

        then:
        1 * mockConnection.execute(actions[0]) >> [new ExecutionOutput(action: 'action1', message: 'msg1')]
        1 * mockConnection.execute(actions[1]) >> [new ExecutionOutput(action: 'action2', message: 'msg2a'), new ExecutionOutput(action: 'action2', message: 'msg2b')]

        executor.messages == [
                [action: 'action1', message: 'msg1'],
                [action: 'action2', message: 'msg2a'],
                [action: 'action2', message: 'msg2b'],
        ]

    }

    static class TestLlccExecutor extends AbstractLlccExecutor {

        def messages = []

        @Override
        void handleOutput(ExecutionOutput output) {
            messages << [action: output.action, message: output.message]
        }
    }

    private static setupExecutableLlcc(List<LlccAction> actions) {
        new Llcc(actions, [], true)
    }
}

