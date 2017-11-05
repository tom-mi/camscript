package de.rfnbrgr.camscript.llcc

import de.rfnbrgr.camscript.device.ConfigUpdate
import groovy.transform.Immutable

@Immutable
class SetConfigAction implements  LlccAction {
    List<ConfigUpdate> updates

    static SetConfigAction of(String variableName, String newValue) {
        new SetConfigAction([new ConfigUpdate(variableName, newValue)])
    }
}
