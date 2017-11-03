package de.rfnbrgr.camscript.device

import groovy.transform.Immutable
import groovy.transform.ToString

@Immutable
@ToString(includePackage = false)
class CameraContext {

    List<String> variables
    private Map<String, VariableContext> variableContextMap

    VariableContext variableContext(String variable) {
        variableContextMap[variable]
    }

}
