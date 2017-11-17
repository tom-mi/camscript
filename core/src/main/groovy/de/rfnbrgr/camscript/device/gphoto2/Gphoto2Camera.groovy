package de.rfnbrgr.camscript.device.gphoto2

import de.rfnbrgr.camscript.device.BackendName
import de.rfnbrgr.camscript.device.Camera
import de.rfnbrgr.grphoto2.domain.DetectedCamera
import groovy.transform.Immutable
import groovy.transform.ToString

@Immutable
@ToString(includePackage = false)
class Gphoto2Camera implements Camera {

    final DetectedCamera camera

    @Override
    String getName() {
        return camera.name
    }

    @Override
    String getDescription() {
        return "libgphoto2 - $camera.model - $camera.path"
    }

    @Override
    BackendName getBackend() {
        BackendName.GPHOTO
    }

    @Override
    Map<String, String> toMap() {
        [
                name: camera.name,
                path: camera.path,
                guid: camera.guid,
        ]
    }
}
