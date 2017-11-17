package de.rfnbrgr.camscript.device.gphoto2

import de.rfnbrgr.camscript.compiler.GrammarUtil
import de.rfnbrgr.camscript.device.*
import de.rfnbrgr.camscript.llcc.CaptureAction
import de.rfnbrgr.camscript.llcc.LlccAction
import de.rfnbrgr.camscript.llcc.SayAction
import de.rfnbrgr.camscript.llcc.SetConfigAction
import de.rfnbrgr.camscript.llcc.WaitAction
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
    List<ExecutionOutput> execute(LlccAction action) {
        throw new ExecuteError("Cannot execute unsupported action $action")
    }

    @SuppressWarnings(["GroovyUnusedDeclaration", "GrMethodMayBeStatic"])
    List<ExecutionOutput> execute(SayAction action) {
        [ExecutionOutput.of(action, action.text)]
    }

    @SuppressWarnings(["GroovyUnusedDeclaration", "GrMethodMayBeStatic"])
    List<ExecutionOutput> execute(WaitAction action) {
        sleepMs(action.durationMilliseconds)
        [ExecutionOutput.of(action, action.durationMilliseconds + 'ms')]
    }

    // extract into static method for testing
    static sleepMs(long duration) {
        sleep(duration)
    }

    @SuppressWarnings(["GroovyUnusedDeclaration"])
    List<ExecutionOutput> execute(SetConfigAction action) {
        def currentConfig = connection.readConfig()

        def gphotoUpdates = action.updates.collect { update ->
            currentConfig.getByPath(update.canonicalName).entryForUpdate(update.newValue)
        }
        connection.updateConfig(gphotoUpdates)

        action.updates.collect { ExecutionOutput.of(action, "$it.canonicalName = $it.newValue") }
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

    @SuppressWarnings(["GroovyUnusedDeclaration"])
    List<ExecutionOutput> execute(CaptureAction action) {
        def file = connection.capture_image()
        [ExecutionOutput.of(action, "captured $file.folder/$file.name")]
    }

    @Override
    void close() {
        connection.close()
    }
}
