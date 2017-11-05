package de.rfnbrgr.camscript.device

import groovy.transform.Immutable
import groovy.transform.ToString

@Immutable
@ToString(includePackage = false)
class ConfigUpdate {
    String canonicalName
    String newValue
}