package de.rfnbrgr.camscript.llcc

import groovy.transform.Canonical

@Canonical
class SayAction implements LlccAction {
    String text
}
