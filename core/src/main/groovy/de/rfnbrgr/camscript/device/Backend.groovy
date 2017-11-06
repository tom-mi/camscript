package de.rfnbrgr.camscript.device

interface Backend {

    CameraFinder autodetect()
    Connection connect(Camera camera)
    Camera loadCamera(BackendName backend, String path, String name)

}