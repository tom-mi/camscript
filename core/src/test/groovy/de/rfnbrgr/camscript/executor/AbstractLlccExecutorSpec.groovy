package de.rfnbrgr.camscript.executor

import de.rfnbrgr.camscript.device.Connection
import de.rfnbrgr.camscript.llcc.*
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
        def llcc = new Llcc([new SayAction("Hello")], [], false)

        when:
        executor.execute(llcc)

        then:
        thrown(ExecutorError)
    }

    def 'executes SayAction'() {
        when:
        executor.execute(setupExecutableLlcc([new SayAction('Hello')]))

        then:
        executor.messages == [[action: 'say', message: 'Hello']]
    }

    def 'executes WaitAction'() {
        setup:
        def sleepDurations = []
        def waitTimeMs = 4200
        AbstractLlccExecutor.metaClass.static.sleep = { long duration -> sleepDurations << duration }

        when:
        executor.execute(setupExecutableLlcc([new WaitAction(waitTimeMs)]))

        then:
        sleepDurations == [waitTimeMs]
        executor.messages == [[action: 'wait', message: '4200ms']]
    }

    def 'executes SetConfigAction'() {
        setup:
        def name = 'aperture'
        def value = '5.6'

        when:
        executor.execute(setupExecutableLlcc([new SetConfigAction(name, value)]))

        then:
        1 * mockConnection.setConfigValue(name, value)
        executor.messages == [[action: 'set config', message: "$name = $value"]]
    }

    def 'executes CaptureAction'() {
        when:
        executor.execute(setupExecutableLlcc([new CaptureAction()]))

        then:
        1 * mockConnection.capture()
        executor.messages == [[action: 'capture', message: 'capturing image']]
    }

    static class TestLlccExecutor extends AbstractLlccExecutor {

        def messages = []

        @Override
        void handleMessage(String action, String message) {
            messages << [action: action, message: message]
        }
    }

    private static setupExecutableLlcc(List<LlccAction> actions) {
        new Llcc(actions, [], true)
    }
}

