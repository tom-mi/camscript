package de.rfnbrgr.camscript.executor

import de.rfnbrgr.camscript.device.Connection
import de.rfnbrgr.camscript.llcc.*

abstract class AbstractLlccExecutor implements LlccExecutor {

    Connection connection

    @Override
    void execute(Llcc llcc) {
        if (!llcc.isExecutable) {
            throw new ExecutorError()
        }

        llcc.actions.each{ LlccAction action ->
            executeAction(action)
        }
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    protected executeAction(SayAction action) {
        handleMessage('say', action.text)
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    protected executeAction(WaitAction action) {
        handleMessage('wait', action.durationMilliseconds + 'ms')
        sleepMs(action.durationMilliseconds)
    }

    // extract into static method for testing
    static sleepMs(long duration) {
        sleep(duration)
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    protected executeAction(SetConfigAction action) {
        action.updates.each { update ->
            handleMessage('set config', "$update.canonicalName = $update.newValue")
        }
        connection.updateConfig(action.updates)
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    protected executeAction(CaptureAction action) {
        handleMessage('capture', 'capturing image')
        connection.capture()
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    protected executeAction(action) {
        throw new IllegalStateException("Executor cannot handle $action")
    }

    abstract void handleMessage(String action, String message)

}
