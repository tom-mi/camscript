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
    static final RELATIVE_STEP_TOLERANCE = 0.001

    boolean validate(String newValue) {
        switch (type) {
            case VariableType.TEXT: return true
            case VariableType.INTEGER: return validateInt(newValue)
            case VariableType.CHOICE: return validateChoice(newValue)
            case VariableType.FLOAT_RANGE: return validateFloatRange(newValue)
            default: throw new IllegalStateException("Cannot validate context type $type")
        }
    }


    private static validateInt(String newValue) {
        try {
            newValue as Integer
        } catch (NumberFormatException ignored) {
            return false
        }
        return true
    }

    private validateChoice(String newValue) {
        newValue in choices
    }

    private validateFloatRange(String newValue) {
        try {
            def floatValue = newValue as Float

            if (floatValue < floatRange.min - floatRange.increment * RELATIVE_STEP_TOLERANCE ||
                    floatValue > floatRange.max + floatRange.increment * RELATIVE_STEP_TOLERANCE) {
                return false
            }
            float distanceToStep = (floatValue - floatRange.min) % floatRange.increment
            if (distanceToStep > floatRange.increment / 2) {
                distanceToStep -= floatRange.increment
            }
            if (distanceToStep.abs() / floatRange.increment > RELATIVE_STEP_TOLERANCE) {
                return false
            }
            return true
        } catch (NumberFormatException ignored) {
            return false
        }
    }
}
