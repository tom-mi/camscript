package de.rfnbrgr.camscript.compiler

class ParseError extends Exception {

    def offendingSymbol
    int line
    int charPositionInLine
    String parserMessage

    ParseError(offendingSymbol, int line, int charPositionInLine, String parserMessage) {
        super("Parse error in line $line at position $charPositionInLine: $parserMessage")
        this.offendingSymbol = offendingSymbol
        this.line = line
        this.charPositionInLine = charPositionInLine
        this.parserMessage = parserMessage
    }

}
