package de.rfnbrgr.camscript.device

import de.rfnbrgr.camscript.device.gphoto2.Gphoto2Backend
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType

class CameraManager implements Backend {

    private final Map<BackendName, Backend> backends

    CameraManager() {
        backends = [
                (BackendName.GPHOTO): new Gphoto2Backend(),
        ]
    }


    class CombinedCameraFinder implements CameraFinder {

        final private List<Closure> callbacks = []
        final private List<CameraFinder> finders = []

        @Override
        void onDetect(
                @ClosureParams(value = SimpleType.class, options = 'de.rfnbrgr.camscript.device.Camera') Closure callback) {
            callbacks << callback
        }

        @Override
        void start() {
            backends.each { name, backend ->
                def finder = backend.autodetect()
                finder.onDetect { camera -> callbacks.each{ callback -> callback(camera) } }
                finders << finder
            }
            finders.each{ it.start() }
        }

        @Override
        void stop() {
            callbacks.clear()
            finders.each{ it.stop() }
        }
    }

    @Override
    CameraFinder autodetect() {
        new CombinedCameraFinder()
    }

    @Override
    Connection connect(Camera camera) {
        backends[camera.backend].connect(camera)
    }
}
