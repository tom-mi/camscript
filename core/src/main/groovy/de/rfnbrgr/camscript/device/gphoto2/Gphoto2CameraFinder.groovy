package de.rfnbrgr.camscript.device.gphoto2

import de.rfnbrgr.camscript.device.CameraFinder
import de.rfnbrgr.grphoto2.Grphoto2
import de.rfnbrgr.grphoto2.util.NetworkCameraFinder
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType

class Gphoto2CameraFinder implements CameraFinder {

    final private Grphoto2 grphoto
    final private NetworkCameraFinder networkCameraFinder = Grphoto2.networkAutodetect()
    final private List<Closure> callbacks = []
    private boolean stopped = false

    Gphoto2CameraFinder(Grphoto2 grphoto) {
        this.grphoto = grphoto
    }

    @Override
    void start() {
        if (stopped) {
            throw new IllegalStateException('This finder is already stopped. Please use a new CameraFinder.')
        }
        networkCameraFinder.onDetect { camera ->
            callbacks.each { it(new Gphoto2Camera(camera)) }
        }
        Thread.start{
            grphoto.usbAutodetect().each { camera ->
                callbacks.each { it(new Gphoto2Camera(camera)) }
            }
        }
    }

    @Override
    void onDetect(
            @ClosureParams(value = SimpleType.class, options = 'de.rfnbrgr.camscript.device.Camera') Closure callback) {
        callbacks << callback
    }

    @Override
    void stop() {
        stopped = true
        callbacks.clear()
        networkCameraFinder.stop()
    }
}
