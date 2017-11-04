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
        parser.script()

        then:
        parser.getNumberOfSyntaxErrors() == 0

        where:
        statement << [
                'say "Hello World"\n',
                'wait 42ms\n',
                'wait 5s\n',
                'wait 10min\n',
                "f-number = 'f/7,1'\n",
                'manualfocusdrive = "Near 3"\n',
                /shutterspeed = "10'"/ + '\n',
                /shutterspeed = "10\""/ + '\n',
                's = "7"',
                'wait = "7"',
        ]
    }

    @Unroll
    def 'whole scripts are parsed: [#source]'() {
        setup:
        def parser = setupParserForInput(source)
        when:
        parser.statement()

        then:
        parser.getNumberOfSyntaxErrors() == 0

        where:
        source << ['''\
            say "Hello World"
        ''', '''\
            wait 5s
        ''', '''\
            repeat 5 times
                say "Again and again"
        '''].collect { it.stripIndent() }
    }

    private static setupParserForInput(String input) {
        def inputStream = new ANTLRInputStream(input)
        def lexer = new CamscriptLexer(inputStream)
        def tokens = new CommonTokenStream(lexer)
        new CamscriptParser(tokens)
    }
}
