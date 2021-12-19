package maze.visualisation.resources.helpers;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

/**
 * A helper for loading the file
 */
public class FileLoadHelper {

    /**
     * A general method for loading a file by using the OS's built-in dialog
     * {@link FileChooser}
     *
     * @param main_stage    {@link Stage} of the JavaFX App
     * @param extFilter     {@link javafx.stage.FileChooser.ExtensionFilter} a filter restrict the type of the file which
     *                                                                      is allowed to be read in
     * @return      the full path of the chosen directory
     */
    public static String load(Stage main_stage, FileChooser.ExtensionFilter extFilter){
        FileChooser file_Chooser = new FileChooser();           // initialize a file chooser
        file_Chooser.getExtensionFilters().add(extFilter);
        File file = file_Chooser.showOpenDialog(main_stage);     // show the dialog on screen
        if (file != null) {
            return file.getAbsolutePath();
        }else {
            return null;
        }
    }

}
