package de.rfnbrgr.camscript.llcc

import groovy.transform.Canonical
import groovy.transform.ToString

@Canonical
@ToString(includePackage = false)
class SayAction implements LlccAction {
    String text
}
