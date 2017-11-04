package de.rfnbrgr.camscript.device.gphoto2

import de.rfnbrgr.camscript.compiler.GrammarUtil
import de.rfnbrgr.camscript.device.*
import de.rfnbrgr.grphoto2.CameraConnection
import de.rfnbrgr.grphoto2.domain.ConfigField
import de.rfnbrgr.grphoto2.domain.ConfigFieldType

class Gphoto2Connection implements Connection {

    CameraConnection connection

    @Override
    CameraContext readCameraContext() {
        def config = connection.readConfig()
        def writableEntries = config.findAll { !it.field.readOnly }
        def duplicates = writableEntries*.field*.name
                .groupBy { it }
                .findAll { name, sameNames -> sameNames.size > 1 }
                .collect { name, sameNames -> name }

        List<String> variableNames = []
        Map<String, VariableContext> variableContext = [:]

        writableEntries.each { entry ->
            def name = entry.field.name in duplicates ? entry.field.path : entry.field.name
            name = GrammarUtil.sanitizeVariableName(name)
            name = handleDuplicate(variableNames, name)
            variableNames << name
            def type = mapType(entry.field)
            def floatRange = mapFloatRange(entry.field)
            variableContext[name] = new VariableContext(type, entry.field.path, entry.field.choices, floatRange)
        }

        new CameraContext(context: variableContext)
    }


    private static mapType(ConfigField field) {
        switch (field.type) {
            case ConfigFieldType.TEXT: return VariableType.TEXT
            case ConfigFieldType.RANGE: return VariableType.FLOAT_RANGE
            case [ConfigFieldType.MENU, ConfigFieldType.RADIO]: return VariableType.CHOICE
            case [ConfigFieldType.TOGGLE, ConfigFieldType.DATE]: return VariableType.INTEGER
            default: throw new IllegalStateException("Cannot map field type $field.type")
        }
    }

    private static mapFloatRange(ConfigField field) {
        if (field.type == ConfigFieldType.RANGE) {
            return new FloatRange(field.rangeMin, field.rangeMax, field.rangeIncrement)
        }
    }

    private static handleDuplicate(List<String> strings, String original) {
        def s = original
        def i = 0
        while (s in strings) {
            i++
            s = original + i
        }
        return s
    }

    @Override
    void close() {
        connection.close()
    }
}
