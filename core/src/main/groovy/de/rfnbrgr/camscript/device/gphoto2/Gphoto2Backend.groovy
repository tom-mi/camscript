package de.rfnbrgr.camscript.device.gphoto2

import de.rfnbrgr.camscript.device.Backend
import de.rfnbrgr.camscript.device.Camera
import de.rfnbrgr.camscript.device.CameraFinder
import de.rfnbrgr.camscript.device.Connection
import de.rfnbrgr.grphoto2.Grphoto2

class Gphoto2Backend implements Backend {

    private final Grphoto2 grphoto = new Grphoto2()

    @Override
    CameraFinder autodetect() {
        return new Gphoto2CameraFinder(grphoto)
    }

    @Override
    Connection connect(Camera camera) {
        return null
    }

}
