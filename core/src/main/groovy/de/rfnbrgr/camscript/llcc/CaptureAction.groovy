package de.rfnbrgr.camscript.llcc

import groovy.transform.Immutable
import groovy.transform.ToString

@Immutable
@ToString(includePackage = false)
class CaptureAction implements LlccAction {
    @Override
    String getName() {
        return 'capture'
    }
}
