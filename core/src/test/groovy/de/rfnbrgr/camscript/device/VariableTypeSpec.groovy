package de.rfnbrgr.camscript.device

import spock.lang.Specification
import spock.lang.Unroll

class VariableTypeSpec extends Specification {

    static final TEST_FLOAT_RANGE = new FloatRange(-3f, 6f, 3f)

    @Unroll
    def 'validate #newValue for #type'() {
        setup:
        def context = new VariableContext(type, 'foo', choices, range)

        expect:
        context.validate(newValue) == expectedResult

        where:
        type                     | choices    | range            | newValue  | expectedResult
        VariableType.TEXT        | []         | null             | 'foobar'  | true
        VariableType.INTEGER     | []         | null             | '42'      | true
        VariableType.INTEGER     | []         | null             | 'foo'     | false
        VariableType.CHOICE      | ['A', 'B'] | null             | 'A'       | true
        VariableType.CHOICE      | ['A', 'B'] | null             | 'C'       | false
        VariableType.FLOAT_RANGE | []         | TEST_FLOAT_RANGE | '3'       | true
        VariableType.FLOAT_RANGE | []         | TEST_FLOAT_RANGE | '3.0001'  | true
        VariableType.FLOAT_RANGE | []         | TEST_FLOAT_RANGE | '2.9999'  | true
        VariableType.FLOAT_RANGE | []         | TEST_FLOAT_RANGE | '-3.0001' | true
        VariableType.FLOAT_RANGE | []         | TEST_FLOAT_RANGE | '6.0001'  | true
        VariableType.FLOAT_RANGE | []         | TEST_FLOAT_RANGE | '-6'      | false
        VariableType.FLOAT_RANGE | []         | TEST_FLOAT_RANGE | '9'       | false
        VariableType.FLOAT_RANGE | []         | TEST_FLOAT_RANGE | '2'       | false
        VariableType.FLOAT_RANGE | []         | TEST_FLOAT_RANGE | 'foo'     | false
    }

}
