package de.rfnbrgr.camscript.device

interface Camera {
    String getName()
    String getDescription()
    BackendName getBackend()
    Map<String, String> toMap()
}