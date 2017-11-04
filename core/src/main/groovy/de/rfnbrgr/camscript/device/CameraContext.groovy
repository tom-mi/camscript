package de.rfnbrgr.camscript.device

import groovy.transform.Immutable
import groovy.transform.ToString

@Immutable
@ToString(includePackage = false)
class CameraContext {

    private Map<String, VariableContext> context

    VariableContext variableContext(String variable) {
        context[variable]
    }

    List<String> getVariables() {
        return context.keySet().sort()
    }

}
