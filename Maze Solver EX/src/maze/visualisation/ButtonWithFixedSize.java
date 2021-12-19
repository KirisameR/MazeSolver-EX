package maze.visualisation;

import javafx.scene.control.Button;


/**
 * A class holding custom button component
 */
public class ButtonWithFixedSize extends Button {

    /**
     * A constructor of the customized button
     * @param buttonText The text of the button
     */
    public ButtonWithFixedSize(String buttonText){
        super(" " + buttonText + " ");
    }

}
