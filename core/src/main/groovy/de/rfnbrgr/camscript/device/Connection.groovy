package de.rfnbrgr.camscript.device

import de.rfnbrgr.camscript.llcc.LlccAction

interface Connection {

    CameraContext readCameraContext()

    List<ExecutionOutput> execute(LlccAction llcc)

    void close()

}