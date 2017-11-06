package de.rfnbrgr.camscript.cli

import de.rfnbrgr.camscript.executor.AbstractLlccExecutor
import org.fusesource.jansi.AnsiConsole

import static org.fusesource.jansi.Ansi.ansi

class CliExecutor extends AbstractLlccExecutor {
    @Override
    void handleMessage(String action, String message) {
        AnsiConsole.systemInstall()
        println ansi().fgGreen().a("[$action] ").reset().a(message)
        AnsiConsole.systemUninstall()
    }
}
