package de.rfnbrgr.camscript.llcc

import groovy.transform.Canonical
import groovy.transform.ToString

@Canonical
@ToString(includePackage = false)
class WaitAction implements LlccAction {
    int durationMilliseconds

    @Override
    String getName() {
        return 'wait'
    }
}
