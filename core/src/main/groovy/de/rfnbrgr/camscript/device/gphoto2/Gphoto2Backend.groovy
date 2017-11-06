package de.rfnbrgr.camscript.device.gphoto2

import de.rfnbrgr.camscript.device.Backend
import de.rfnbrgr.camscript.device.BackendName
import de.rfnbrgr.camscript.device.Camera
import de.rfnbrgr.camscript.device.CameraFinder
import de.rfnbrgr.camscript.device.Connection
import de.rfnbrgr.camscript.device.DeviceError
import de.rfnbrgr.grphoto2.Grphoto2
import de.rfnbrgr.grphoto2.domain.DetectedCamera

class Gphoto2Backend implements Backend {

    private final Grphoto2 grphoto = new Grphoto2()

    @Override
    CameraFinder autodetect() {
        return new Gphoto2CameraFinder(grphoto)
    }

    @Override
    Connection connect(Camera camera) {
        def gphotoConnection = grphoto.connect(((Gphoto2Camera) camera).camera)
        new Gphoto2Connection(connection: gphotoConnection)
    }

    @Override
    Camera loadCamera(BackendName backend, String path, String name) {
        if (backend != BackendName.GPHOTO) {
            throw new DeviceError("Cannot load camera with wrong backend $backend")
        }
        new Gphoto2Camera(new DetectedCamera(name, path))
    }
}
