package de.rfnbrgr.camscript.device.gphoto2

import de.rfnbrgr.camscript.device.ConfigUpdate
import de.rfnbrgr.camscript.device.Connection
import de.rfnbrgr.camscript.device.FloatRange
import de.rfnbrgr.camscript.device.VariableContext
import de.rfnbrgr.camscript.device.VariableType
import de.rfnbrgr.grphoto2.CameraConnection
import de.rfnbrgr.grphoto2.domain.*
import spock.lang.Specification
import spock.lang.Unroll

class Gphoto2ConnectionSpec extends Specification {

    Connection connection

    def setup() {
        connection = new Gphoto2Connection()
        connection.connection = Mock(CameraConnection)
    }

    static final TEXT_ENTRY = new ConfigEntry(
            new ConfigField('/path/to/text', 'text', 'Text', ConfigFieldType.TEXT, [], false, null, null, null),
            new StringValue('foo bar')
    )
    static final TEXT_ENTRY_RO = new ConfigEntry(
            new ConfigField('/path/to/text_ro', 'text_ro', 'Text Readonly', ConfigFieldType.TEXT, [], true, null, null, null),
            new StringValue('foo bar')
    )
    static final TEXT_ENTRY_WITH_SAME_NAME = new ConfigEntry(
            new ConfigField('/another/path/to/text', 'text', 'Text', ConfigFieldType.TEXT, [], false, null, null, null),
            new StringValue('foo bar')
    )
    static final TOGGLE_ENTRY = new ConfigEntry(
            new ConfigField('/path/to/toggle', 'toggle', 'Toggle', ConfigFieldType.TOGGLE, [], false, null, null, null),
            new IntegerValue(0)
    )
    static final MENU_ENTRY = new ConfigEntry(
            new ConfigField('/path/to/menu', 'menu', 'Menu', ConfigFieldType.MENU, ['On', 'Off'], false, null, null, null),
            new StringValue('On')
    )
    static final RADIO_ENTRY = new ConfigEntry(
            new ConfigField('/path/to/radio', 'radio', 'Radio', ConfigFieldType.RADIO, ['A', 'B', 'C'], false, null, null, null),
            new StringValue('B')
    )
    static final RANGE_ENTRY = new ConfigEntry(
            new ConfigField('/path/to/range', 'range', 'Range', ConfigFieldType.RANGE, [], false, -6f, 6f, 3f),
            new FloatValue(3f)
    )

    @Unroll
    def 'readCameraContext - expected variables #expectedVariables are present'() {
        when:
        def cameraContext = connection.readCameraContext()

        then:
        1 * connection.connection.readConfig() >> mockConfig

        cameraContext.variables == expectedVariables

        where:
        mockConfig                              || expectedVariables
        [TEXT_ENTRY]                            || ['text']
        [TOGGLE_ENTRY, RADIO_ENTRY]             || ['radio', 'toggle']
        [TEXT_ENTRY_RO]                         || []
        [TEXT_ENTRY, TEXT_ENTRY_WITH_SAME_NAME] || ['/another/path/to/text', '/path/to/text']
    }

    @Unroll
    def 'readCameraContext - variableContext is present for #key'() {
        when:
        def cameraContext = connection.readCameraContext()

        then:
        1 * connection.connection.readConfig() >> mockConfig

        def context = cameraContext.variableContext(key)
        context.canonicalName == expectedCanonicalName
        context.type == expectedType
        context.choices == expectedChoices
        context.floatRange == expectedFloatRange

        where:
        mockConfig     | key      || expectedType             | expectedCanonicalName | expectedChoices | expectedFloatRange
        [TEXT_ENTRY]   | 'text'   || VariableType.TEXT        | '/path/to/text'       | []              | null
        [TOGGLE_ENTRY] | 'toggle' || VariableType.INTEGER     | '/path/to/toggle'     | []              | null
        [RADIO_ENTRY]  | 'radio'  || VariableType.CHOICE      | '/path/to/radio'      | ['A', 'B', 'C'] | null
        [MENU_ENTRY]   | 'menu'   || VariableType.CHOICE      | '/path/to/menu'       | ['On', 'Off']   | null
        [RANGE_ENTRY]  | 'range'  || VariableType.FLOAT_RANGE | '/path/to/range'      | []              | new FloatRange(-6f, 6f, 3f)
    }

    def 'readCameraContext - problematic characters in names are replaced'() {
        setup:
        def mockConfig = [
                entryWithPathAndName('/path/here', 'white späce == bad'),
                entryWithPathAndName('/path/there', 'same'),
                entryWithPathAndName('/path/there it is', 'same'),
                entryWithPathAndName('/path/else=where', 'other'),
                entryWithPathAndName('/path/else where', 'other'),
                entryWithPathAndName('/path/else\twhere', 'other'),
                entryWithPathAndName('/path/wait', 'wait'),
        ]

        when:
        def cameraContext = connection.readCameraContext()

        then:
        1 * connection.connection.readConfig() >> mockConfig
        cameraContext.variables == [
                '/path/else_where',
                '/path/else_where1',
                '/path/else_where2',
                '/path/there',
                '/path/there_it_is',
                '_wait',
                'white_sp_ce____bad',
        ]
        cameraContext.variables.each { name ->
            assert cameraContext.variableContext(name) instanceof VariableContext
        }
    }

    private static entryWithPathAndName(String path, String name) {
        new ConfigEntry(
                new ConfigField(path, name, 'Text', ConfigFieldType.TEXT, [], false, null, null, null),
                new StringValue('foo bar')
        )
    }

    def 'update config'() {
        setup:
        def gphotoUpdates = []

        when:
        def updates = [
                new ConfigUpdate('/path/to/text', 'foobar'),
                new ConfigUpdate('/path/to/radio', 'A'),
        ]

        connection.updateConfig(updates)

        then:
        1 * connection.connection.readConfig() >> [TEXT_ENTRY, RADIO_ENTRY]
        1 * connection.connection.updateConfig(_) >> { args -> //noinspection GroovyAssignabilityCheck
            gphotoUpdates = args[0]
        }

        gphotoUpdates.size() == 2
        gphotoUpdates[0].field == TEXT_ENTRY.field
        gphotoUpdates[1].field == RADIO_ENTRY.field
        gphotoUpdates[0].value == 'foobar'
        gphotoUpdates[1].value == 'A'
    }

}
