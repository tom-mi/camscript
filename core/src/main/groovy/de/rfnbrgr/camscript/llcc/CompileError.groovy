package de.rfnbrgr.camscript.llcc

import groovy.transform.Immutable
import groovy.transform.ToString

@Immutable
@ToString(includePackage = false)
class CompileError {
    int line
    int col
    int startIndex
    int stopIndex
    String message
}
