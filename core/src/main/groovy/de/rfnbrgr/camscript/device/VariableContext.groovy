package de.rfnbrgr.camscript.device

import groovy.transform.Immutable
import groovy.transform.ToString

enum VariableType {
    CHOICE,
    TEXT,
    INTEGER,
    FLOAT_RANGE,
}

@Immutable
@ToString(includePackage = false)
class FloatRange {
    float min
    float max
    float increment
}

@Immutable
@ToString(includePackage = false)
class VariableContext {

    VariableType type
    String canonicalName
    String currentValue

    List<String> choices
    FloatRange floatRange

}
