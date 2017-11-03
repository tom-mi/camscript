package de.rfnbrgr.camscript.device

interface Backend {

    CameraFinder autodetect()
    Connection connect(Camera camera)

}