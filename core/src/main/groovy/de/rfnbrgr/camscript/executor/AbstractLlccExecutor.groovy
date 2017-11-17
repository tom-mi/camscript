package de.rfnbrgr.camscript.executor

import de.rfnbrgr.camscript.device.Connection
import de.rfnbrgr.camscript.device.ExecutionOutput
import de.rfnbrgr.camscript.llcc.*

abstract class AbstractLlccExecutor implements LlccExecutor {

    Connection connection

    @Override
    void execute(Llcc llcc) {
        if (!llcc.isExecutable) {
            throw new ExecutorError()
        }

        llcc.actions.each { LlccAction action ->
            def outputs = connection.execute(action)
            outputs.each { handleOutput(it) }
        }
    }

    abstract void handleOutput(ExecutionOutput output)

}
