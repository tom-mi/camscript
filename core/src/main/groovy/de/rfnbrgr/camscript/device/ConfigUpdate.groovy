package de.rfnbrgr.camscript.device

import groovy.transform.Immutable

@Immutable
class ConfigUpdate {
    String variableName
    String newValue
}