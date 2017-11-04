package de.rfnbrgr.camscript.llcc

import groovy.transform.Immutable

@Immutable
class SetConfigAction implements  LlccAction {
    String variableName
    String newValue
}
