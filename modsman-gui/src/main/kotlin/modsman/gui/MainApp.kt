package modsman.gui

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.TextInputDialog
import javafx.scene.image.Image
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import modsman.BuildConfig
import modsman.Modlist
import modsman.ModlistManager
import modsman.Modsman
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.system.exitProcess

class MainApp : Application() {
    private val title = "modsman ${BuildConfig.VERSION}"

    private fun chooseDirectory(stage: Stage): Path {
        val chooser = DirectoryChooser()
        chooser.title = title
        return chooser.showDialog(stage)?.toPath() ?: exitProcess(0)
    }

    private fun chooseGameVersion(): String {
        val dialog = TextInputDialog("1.15")
        dialog.headerText = "Mod list not found." +
            "\nEnter a game version to initialize a new mod list." +
            "\nModsman will match to any versions that contain this version as a substring." +
            "\nFor example, the input \"1.15\" matches mods for \"1.15\", \"1.15.1\", \"1.15.2\", and \"1.15-Snapshot\"."
        dialog.title = title
        return dialog.showAndWait().orElse(null) ?: exitProcess(0)
    }

    private fun createModsman(stage: Stage): Modsman {
        val path = chooseDirectory(stage)
        if (!Files.exists(path.resolve(Modlist.fileName))) {
            ModlistManager.init(path, chooseGameVersion()).close()
        }
        return Modsman(path, 10)
    }

    override fun start(stage: Stage) {
        stage.setOnCloseRequest { exitProcess(0) }
        stage.title = title
        stage.icons.add(Image(javaClass.getResourceAsStream("icon.png")))

        val fxmlLoader = FXMLLoader(
            javaClass.getResource("Main.fxml"),
            ResourceBundle.getBundle("modsman.gui.strings")
        )

        val scene = Scene(fxmlLoader.load())
        stage.scene = scene
        stage.show()

        val controller = fxmlLoader.getController<MainController>()
        controller.modsman = createModsman(stage)
    }
}
