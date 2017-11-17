package de.rfnbrgr.camscript.cli

import de.rfnbrgr.camscript.compiler.CamscriptCompiler
import de.rfnbrgr.camscript.device.*
import de.rfnbrgr.camscript.llcc.Llcc
import groovy.util.logging.Slf4j
import org.fusesource.jansi.Ansi
import org.fusesource.jansi.AnsiConsole

import static org.fusesource.jansi.Ansi.ansi

@Slf4j
class CamscriptCli {

    final CliBuilder cli
    final CameraManager cameraManager = new CameraManager()

    Connection connection
    CameraContext cameraContext

    CamscriptCli() {
        cli = new CliBuilder(usage: 'camscript [todo] <script>')
        cli._(longOpt: 'script', args: 1, argName: 'SCRIPT', 'Camscript file')
        cli.e(longOpt: 'execute', 'Compile and execute (this is the default behavior)')
        cli.c(longOpt: 'compile', 'Compile only')
        cli.o(longOpt: 'compile-offline', 'Compile offline')
    }

    enum CliMode {
        COMPILE_OFFLINE,
        COMPILE,
        COMPILE_AND_EXECUTE,
    }

    void run(String[] args) {
        def options = parseArgs(args)

        if (options.cliMode in [CliMode.COMPILE, CliMode.COMPILE_AND_EXECUTE]) {
            def camera = autodetect()
            connection = connect(camera)
            cameraContext = connection.readCameraContext()
        }
        def llcc = compile(options.scriptPath)
        printErrors(llcc)
        if (options.cliMode == CliMode.COMPILE_AND_EXECUTE) {
            execute(llcc)
        }
    }

    private parseArgs(String[] args) {
        def options = cli.parse(args)

        return [
                cliMode   : determineCliMode(options),
                scriptPath: extractScript(options)
        ]
    }

    private exitWithUsage(String message) {
        System.err.println(message)
        cli.usage()
        System.exit(1)
    }

    private determineCliMode(options) {
        if (!options.e && !options.c && !options.o) {
            return CliMode.COMPILE_AND_EXECUTE
        } else if (options.e && !options.c && !options.o) {
            return CliMode.COMPILE_AND_EXECUTE
        } else if (!options.e && options.c && !options.o) {
            return CliMode.COMPILE
        } else if (!options.e && !options.c && options.o) {
            return CliMode.COMPILE_OFFLINE
        } else {
            exitWithUsage('Invalid options: -e, -c and -o are mutually exclusive')
        }
    }

    private String extractScript(options) {
        if (!options.script || !new File(options.script as String).isFile()) {
            exitWithUsage('Please specify an existing script file.')
        }
        return options.script
    }

    private Camera autodetect() {
        AnsiConsole.systemInstall()
        CameraFinder finder = cameraManager.autodetect()
        List<Camera> detectedCameras = []
        finder.onDetect { Camera camera ->
            def i = detectedCameras.size() + 1
            detectedCameras << camera
            println ansi()
                    .eraseLine(Ansi.Erase.BACKWARD).cursorToColumn(0)
                    .bold().a("[$i]").boldOff().a(" $camera.name ").fgCyan().a("$camera.description").reset()
            printPrompt()
        }
        println 'Detecting cameras ...'
        finder.start()
        Camera chosenCamera
        while (chosenCamera == null) {
            def input = System.in.newReader().readLine()
            if (detectedCameras.isEmpty()) {
                continue
            }
            if (input == '') {
                chosenCamera = detectedCameras[0]
                break
            }
            try {
                chosenCamera = detectedCameras.get((input as Integer) - 1)
                break
            } catch (NumberFormatException | IndexOutOfBoundsException ignored) {
                println ansi().fgRed().a("Invalid choice [$input]").reset()
                printPrompt()
            }
        }
        finder.stop()
        AnsiConsole.systemUninstall()
        chosenCamera
    }

    private static printPrompt() {
        print 'Choose a camera to connect to [1]: '
        System.out.flush()
    }

    private Connection connect(Camera camera) {
        println "Connecting to camera $camera.name ..."
        def connection = cameraManager.connect(camera)
        println "Connected."
        connection
    }

    private Llcc compile(String scriptPath) {
        println 'Compiling script...'
        def source = new File(scriptPath).text
        new CamscriptCompiler(cameraContext: cameraContext).compile(source)
    }

    private static void printErrors(Llcc llcc) {
        if (llcc.errors.size() > 0) {
            println ansi().fgRed().a("Script contained ${llcc.errors.size()} errors:").reset()
            llcc.errors.each { error ->
                println ansi().fgCyan().format("Line %4s pos %3s ", error.line, error.col).fgDefault()
                        .a(error.message).reset()
            }
        }
    }

    private void execute(Llcc llcc) {
        if (!llcc.isExecutable) {
            println ansi().fgRed().a('Cannot execute script.').reset()
        } else {
            def executor = new CliExecutor(connection: connection)
            executor.execute(llcc)
        }
    }

    static void main(String[] args) {
        new CamscriptCli().run(args)
    }

}
