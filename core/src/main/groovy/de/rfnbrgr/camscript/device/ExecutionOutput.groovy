package de.rfnbrgr.camscript.device

import de.rfnbrgr.camscript.llcc.LlccAction
import groovy.transform.Immutable
import groovy.transform.ToString

import java.time.Instant

@Immutable(knownImmutableClasses = [Instant])
@ToString(includePackage = true)
class ExecutionOutput {
    Instant timestamp
    String action
    String message

    static of(LlccAction action, String message) {
        new ExecutionOutput(action: action.name, message: message, timestamp: Instant.now())
    }
}
