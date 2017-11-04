package de.rfnbrgr.camscript.compiler

class GrammarUtil {

    static final KEYWORDS = ['wait', 'say', 'repeat', 'times']
    static final INVALID_VARIABLE_NAME_CHARACTER = /[^\w_\-\/]/
    static final INVALID_VARIABLE_START_CHARACTER = /[^A-Za-z_\/]/

    static String sanitizeVariableName(String name) {
        name = name.replaceAll(INVALID_VARIABLE_NAME_CHARACTER, '_')
        if (name[0] =~ INVALID_VARIABLE_START_CHARACTER) {
            name = '_' + name
        }
        if (name in KEYWORDS) {
            name = '_' + name
        }
        return name
    }


}
