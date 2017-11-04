package de.rfnbrgr.camscript.llcc

import groovy.transform.Immutable

@Immutable
class CompileError {
    int line
    int charPositionInLine
    String message
}
