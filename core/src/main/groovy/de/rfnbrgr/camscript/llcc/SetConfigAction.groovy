package de.rfnbrgr.camscript.llcc

import de.rfnbrgr.camscript.device.ConfigUpdate
import groovy.transform.Immutable
import groovy.transform.ToString

@Immutable
@ToString(includePackage = false)
class SetConfigAction implements  LlccAction {
    List<ConfigUpdate> updates

    static SetConfigAction of(String canonicalName, String newValue) {
        new SetConfigAction([new ConfigUpdate(canonicalName, newValue)])
    }

    @Override
    String getName() {
        return 'set config'
    }
}
