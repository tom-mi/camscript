package de.rfnbrgr.camscript.device

interface Connection {

    CameraContext readCameraContext()

    void setConfigValue(String name, String value)

    void capture()

    void close()

}