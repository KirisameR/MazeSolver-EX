package maze.visualisation.resources.helpers;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * A helper for saving the file
 */
public class FileSaveHelper {

    /**
     * A general method for saving a file by using the OS's built-in dialog
     * {@link FileChooser}
     *
     * @param main_stage    {@link Stage} of the JavaFX App
     * @param extFilter     {@link FileChooser.ExtensionFilter} a filter restrict the type of the file which
     *                                                                      is allowed to be saved
     * @return      the full path of the chosen directory
     */
    public static String save(Stage main_stage, FileChooser.ExtensionFilter extFilter){
        FileChooser file_Chooser = new FileChooser();           // initialize a file chooser
        file_Chooser.getExtensionFilters().add(extFilter);    // add the ext name to the dir provided
        File file = file_Chooser.showSaveDialog(main_stage);    // show the dialog on screen

        // delete the existing file, then write it to the disk
        if (file.exists()) {
            file.delete();
        }
        return file.getAbsolutePath();
    }

}
