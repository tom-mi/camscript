package de.rfnbrgr.camscript.llcc

import groovy.transform.Immutable

@Immutable
class Llcc {
    List<LlccAction> actions
    List<CompileError> errors

    boolean isExecutable

}
