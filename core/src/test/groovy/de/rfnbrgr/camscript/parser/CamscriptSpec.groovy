package de.rfnbrgr.camscript.parser

import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import spock.lang.Specification
import spock.lang.Unroll

class CamscriptSpec extends Specification {

    @Unroll
    def 'statement [#statement] is parsed'() {
        setup:
        def parser = setupParserForInput(statement)
        when:
        def tree = parser.script()

        then:
        parser.getNumberOfSyntaxErrors() == 0

        where:
        statement << [
                'say "Hello World"\n',
                'wait 42ms\n',
                'wait 5s\n',
                'wait 10min\n',
        ]
    }

    @Unroll
    def 'whole scripts are parsed'() {
        setup:
        def parser = setupParserForInput(source)
        when:
        def tree = parser.statement()

        then:
        parser.getNumberOfSyntaxErrors() == 0

        where:
        source << ['''\
            say "Hello World"
        ''', '''\
            wait 5s
        '''].collect { it.stripIndent() }
    }

    private setupParserForInput(input) {
        def inputStream = new ANTLRInputStream(input)
        def lexer = new CamscriptLexer(inputStream)
        def tokens = new CommonTokenStream(lexer)
        new CamscriptParser(tokens)
    }
}
