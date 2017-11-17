package de.rfnbrgr.camscript.cli

import de.rfnbrgr.camscript.device.ExecutionOutput
import de.rfnbrgr.camscript.executor.AbstractLlccExecutor
import org.fusesource.jansi.AnsiConsole

import static org.fusesource.jansi.Ansi.ansi

class CliExecutor extends AbstractLlccExecutor {
    @Override
    void handleOutput(ExecutionOutput output) {
        AnsiConsole.systemInstall()
        println ansi().fgGreen().a("[$output.action] ").reset().a(output.message)
        AnsiConsole.systemUninstall()
    }
}
