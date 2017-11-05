package de.rfnbrgr.camscript.device

interface Connection {

    CameraContext readCameraContext()

    void updateConfig(List<ConfigUpdate> updates)

    void capture()

    void close()

}