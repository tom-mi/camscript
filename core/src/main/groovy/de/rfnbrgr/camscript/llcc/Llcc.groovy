package de.rfnbrgr.camscript.llcc

import groovy.transform.Immutable
import groovy.transform.ToString

@Immutable
@ToString(includePackage = false)
class Llcc {
    List<LlccAction> actions
    List<CompileError> errors

    boolean isExecutable

}
