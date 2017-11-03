package de.rfnbrgr.camscript.device

import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType

interface CameraFinder {

    void onDetect(
            @ClosureParams(value = SimpleType.class, options = 'de.rfnbrgr.camscript.device.Camera') Closure callback)

    void start()
    void stop()
}