package de.rfnbrgr.camscript.llcc

import groovy.transform.Canonical

@Canonical
class WaitAction implements LlccAction {
    int durationMilliseconds
}
