import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import maze.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.control.Button;
import maze.routing.NoRouteFoundException;
import maze.routing.RouteFinder;
import java.io.*;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import static javafx.geometry.Pos.*;

import maze.visualisation.ButtonWithFixedSize;
import maze.visualisation.resources.helpers.FileLoadHelper;
import maze.visualisation.resources.helpers.FileSaveHelper;
import maze.visualisation.MazePaneInitializationHelper;


/**
 * The class contains the GUI for maze solving
 * and corresponding error handling
 * @author Yi Lu
 * @version 0.9.1
 */
public class MazeApplication extends Application {
    /**
     * The maze to be solved
     */
    Maze new_maze;

    /**
     * the route finder for solving the maze
     */
    RouteFinder new_finder = null;

    /**
     * Initialize the notification
     */
    Text caption = new Text(68, 24, "Welcome to Maze Solver EX.\n" +
            "Please either load a new map, or load an existing route.");

    /**
     * The stage of the application
     */
    Stage main_stage, dialog_stage;

    /** The scenes of the application*/
    Scene main_scene, dialog_scene;

    /**
     * The filepath used in file r/w
     */
    String path;

    /**
     * The flag for revealing the state of initialization
     */
    boolean isInitialized = false;

    /**
     * The flag for revealing the state of game condition
     */
    boolean isFinished = false;

    /**
     * The flag for revealing whether the maze plate is displaying notification or the maze
     */
    boolean isNotifying = true;

    /**
     * Initialize the scene width for initial calculation in maze visualization
     */
    int sceneWidth = 650;;

    /**
     * Initialize the scene height for initial calculation in maze visualization
     */
    int sceneHeight = 650;

    /**
     * Initialize the tile size for initial calculation in maze visualization
     */
    int tileSize = 30;

    /** Initialize the algorithm mode, A* == 0, Dijkstra == 1，DFS == 2， BFS == 3 */
    int mode = 0;

    /**
     * The pane used for maze visualization
     */
    GridPane maze_pane = new GridPane();

    /**
     * Standard start method of a JavaFX Application
     * responsible for element rendering and initialization
     * @param stage         The stage to be started
     */
    @Override
    public void start(Stage stage){

        // pass through the stage
        main_stage = stage;

        // load in the logo (sadly, only works in windows)
        Image image = new Image("file:src/maze/visualisation/resources/image/a-star_logo.png");

        // set caption color and fontsize
        caption.setFill(Color.rgb(216, 216, 216));
        caption.setFont(Font.font ("Helvetica", 16));

        // setting the property of the grid pane to store maze visualization
        // and handle its initialization
        maze_pane.setAlignment(CENTER);
        maze_pane.setPadding(new Insets(11.5, 12.5, 13.5, 14.5));
        maze_pane.setHgap(1.5);
        maze_pane.setVgap(1.5);

        // display the smiling face for greeting the user
        handleMazePaneInitialization(2);

        // create a button that will load map from a 'txt' file
        Button loadMapButton = new ButtonWithFixedSize(" Load Map ");
        // create a button that will load route from a serialized file
        Button loadRouteButton = new ButtonWithFixedSize("Load Route");
        // create a button that will save route to a serialized file
        Button saveRouteButton = new ButtonWithFixedSize("Save Route");
        // create a button that will step through the maze
        Button stepButton = new ButtonWithFixedSize(" Step up ");


        // create and configure a horizontal container to hold the buttons
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(CENTER);

        // add the buttons to the horizontal container
        buttonBox.getChildren().addAll(loadMapButton, loadRouteButton, saveRouteButton, stepButton);

        // create and configure a vertical container to hold the button box
        VBox root = new VBox(20);
        root.setBackground(Background.EMPTY);
        root.setAlignment(CENTER);

        //add the button box and the face group to the vertical container
        root.getChildren().addAll(caption, maze_pane, buttonBox);

        // create and configure a new scene
        main_scene = new Scene(root, 650, 650, Color.rgb(50, 50, 50));

        // supply the code that is executed when window is resized horizontally
        main_scene.widthProperty().addListener((e -> {
            if (isInitialized) {
                sceneWidth = (int) main_scene.getWidth();
                handleMazeResize();
            }
        }));

        // supply the code that is executed when window is resized vertically
        main_scene.heightProperty().addListener((e -> {
            if (isInitialized) {
                sceneHeight = (int) main_scene.getHeight();
                handleMazeResize();
            }
        }));



        //read in custom style sheet to achieve a macOS Catalina DarkMode style interface
        main_scene.getStylesheets().add("file:src/maze/visualisation/resources/css/style.css");


        // supply the code that is executed when firing up map loader
        loadMapButton.setOnAction(event ->
            handleLoadPopUpRendering()
        );

        // supply the code that is executed when loading route
        loadRouteButton.setOnAction(e ->
            handleMazeLoadFromSerializedFile()
        );

        // supply the code that is executed when saving route
        saveRouteButton.setOnAction(e ->
            handleMazeSaveToSerializedFile()
        );

        //supply the code that is executed when stepping through the maze
       stepButton.setOnAction(e ->
           handleMazeStep()
        );

        // add the scene to the stage, then set the title
        main_stage.setScene(main_scene);
        main_stage.setTitle("Maze Solver EX");
        main_stage.getIcons().add(image);
        main_stage.setMinHeight(650);
        main_stage.setMinWidth(650);
        // show the stage
        main_stage.show();
    }

    /**
     * main container for application running
     * @param args     main method's default argument
     */
    public static void main(String[] args){
        launch(args);
    }

    /**
     * A method responsible for holding the logic and exception handling
     * when reading the maze from the txt file
     */
    public void handleMazeLoadFromTxt() {
        try{
            //set the notification info
            caption.setText("Loading Map...\n");

            // return the ABSOLUTE path for failsafe reason
            path = FileLoadHelper.load(main_stage, new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt"));

            // then try to load the maze according to the dir user provided
            try{
                System.out.println(mode);
                new_maze = Maze.fromTxt(path);             // instantiate the maze
                caption.setText("Successfully loaded maze.\n"); //renew the notification when finished
                new_finder = new RouteFinder(new_maze, mode);     // instantiate the finder
                isFinished = false;                        // initialize finished state
                isInitialized = true;                      // initialize the maze initialization state
                isNotifying = false;
                handleMazeVisualization();
                handleMazeResize();
            }
            catch (RaggedMazeException e) {
                handleMazePaneInitialization(1);
                caption.setText("The maze read in is ragged.\n");
            }
            catch (MultipleEntranceException | MultipleExitException e) {
                handleMazePaneInitialization(1);
                caption.setText("The maze has multiple entrance or exit. \n      Please select a valid maze.");
            }
            catch (NoEntranceException | NoExitException e) {
                handleMazePaneInitialization(1);
                caption.setText("The maze has no entrance or exit. \n      Please select a valid maze.");
            }
            catch (InvalidMazeException e) {
                handleMazePaneInitialization(1);
                caption.setText("The file selected is invalid.\nIt may contains illegal characters.");
            }
            catch (FileNotFoundException e){
                handleMazePaneInitialization(0);
                caption.setText("Unable to locate the file. " +
                        "\nDue to the OS's limitation, you may not able to read in some file.");
                // it happens when you select shortcuts. in macOS, you will be redirected to "xxx.app" folder,
                // in Windows you will be redirected to "xxx.exe", both case will cause FileNotFoundException.
            }
            catch (EOFException e){
                handleMazePaneInitialization(1);
                caption.setText("The file selected is empty. \nPlease select a valid file.");
            }
            catch (IOException e) {
                handleMazePaneInitialization(1);
                caption.setText("Unexpected IO error happened.\n   Please try again.");
            }
        }
        catch(NullPointerException e){
            caption.setText("Maze Solver EX\n");
            // it happens when you open the file selection menu but did nothing.
            // let's just pretend nothing happened.
        }
    }

    /**
     * A method responsible for rendering the txt load option pop-up box
     */
    public void handleLoadPopUpRendering() {

        Text infoLabel = new Text(300, 100, "Select the maze-solving algorithm:");
        infoLabel.setFill(Color.rgb(216, 216, 216));
        infoLabel.setFont(Font.font ("Helvetica", 16));


        ObservableList<String> AlgorithmList =
                FXCollections.observableArrayList(Arrays.asList("A-Star Algorithm", "Dijkstra Algorithm", "DFS Algorithm", "BFS Algorithm"));
        ComboBox<String> comboBox = new ComboBox<String>(AlgorithmList);
        comboBox.getSelectionModel().select(0);
        comboBox.setEditable(false);
        // create a button used in txt loader
        Button loadMapFromTxtButton = new ButtonWithFixedSize(" Load ");
        // supply the code that is executed when loading mao
        loadMapFromTxtButton.setOnAction(event ->
                handleMazeLoadFromTxt()
        );

        comboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String old_str, String new_str) {
                // getSelectedIndex方法可获得选中项的序号，getSelectedItem方法可获得选中项的对象
                mode = comboBox.getSelectionModel().getSelectedIndex();
            }
        });

        HBox selectionBox = new HBox(20);
        selectionBox.setAlignment(CENTER);
        selectionBox.getChildren().addAll(comboBox, loadMapFromTxtButton);

        VBox dialogVbox = new VBox(20);
        dialogVbox.setAlignment(CENTER);
        dialogVbox.getChildren().addAll(infoLabel, selectionBox);
        dialogVbox.setBackground(Background.EMPTY);

        dialog_stage = new Stage();
        dialog_stage.initModality(Modality.APPLICATION_MODAL);
        dialog_stage.initOwner(main_stage);


        dialog_scene = new Scene(dialogVbox, 300, 150, Color.rgb(50, 50, 50));
        dialog_scene.getStylesheets().add("file:src/maze/visualisation/resources/css/style.css");
        dialog_stage.setMinHeight(150);
        dialog_stage.setMinWidth(320);

        dialog_stage.setScene(dialog_scene);
        dialog_stage.show();


    }

    /**
     * A method responsible for holding the logic and exception handling
     * when reading the maze from a serialized file
     */
    public void handleMazeLoadFromSerializedFile() {
        try{
            //set the notification info
            caption.setText("Loading Route...\n");
            FileChooser file_Chooser = new FileChooser();           // initialize a file chooser
            File file = file_Chooser.showOpenDialog(main_stage);     // show the dialog on screen
            path = file.getAbsolutePath();                          // return the ABSOLUTE path for failsafe reason


            // then try to load the maze according to the dir user provided
            try{
                new_finder = RouteFinder.load(path);        // instantiate the finder
                new_maze = new_finder.getMaze();            // instantiate the maze
                caption.setText("Successfully loaded route.\n"); //renew the notification when finished
                isFinished = false;                        // initialize finished state
                isInitialized = true;                      // initialize the maze initialization state
                isNotifying = false;
                handleMazeVisualization();
                handleMazeResize();
            }

            catch (FileNotFoundException e){
                handleMazePaneInitialization(0);
                caption.setText("Unable to locate the file. " +
                        "\nDue to the OS's limitation, you may not able to read in some file.");
                // it happens when you select shortcuts. in macOS, you will be redirected to "xxx.app" folder,
                // in Windows you will be redirected to "xxx.exe", both case will cause FileNotFoundException.
            }
            catch (EOFException e){
                handleMazePaneInitialization(1);
                caption.setText("The file selected is empty. \nPlease select a valid file.");
            }
            catch (ClassNotFoundException e){
                handleMazePaneInitialization(1);
                caption.setText("Cannot read in the route from the file.\nThe file is invalid.");
            }
            catch (RaggedMazeException e) {
                handleMazePaneInitialization(1);
                caption.setText("The maze read in is ragged.\n");
            }
            catch (MultipleEntranceException | MultipleExitException e) {
                handleMazePaneInitialization(1);
                caption.setText("The maze has multiple entrance or exit. \n      Please select a valid maze.");
            }
            catch (NoEntranceException | NoExitException e) {
                handleMazePaneInitialization(1);
                caption.setText("The maze has no entrance or exit. \n      Please select a valid maze.");
            }
        }
        catch(NullPointerException e){
            caption.setText("Maze Solver EX\n");
            // it happens when you open the file selection menu but did nothing.
            // let's just pretend nothing happened.
        }
    }

    /**
     * A method responsible for holding the logic and exception handling
     * when saving the maze and its solving state to a serialized file
     */
    public void handleMazeSaveToSerializedFile() {
        if (isInitialized) {
            try{
                // set the notification info
                caption.setText("Saving Map...\n");

                // return the ABSOLUTE path for failsafe reason
                path = FileSaveHelper.save(main_stage, new FileChooser.ExtensionFilter("Serialized Route files(*.route)", "*.route"));

                // then save the file
                try{
                    new_finder.save(path);
                    caption.setText("Successfully saved maze.\n");
                }
                catch (IOException e) {
                    caption.setText("Cannot save file. \nPlease try again.");
                }
            }
            catch(NullPointerException e){
                caption.setText("Maze Solver EX\n");
                // it happens when you open the file selection menu but did nothing.
                // let's just pretend nothing happened.
            }
        }
        else {
            handleMazePaneInitialization(1);
            caption.setText("You cannot save the route\nif it hasn't been loaded yet.");
        }
    }

    /**
     * A method responsible for holding the logic and exception handling
     * when stepping through the maze
     */
    public void handleMazeStep() {
        if (!isInitialized) {
            handleMazePaneInitialization(1);
            caption.setText("Cannot step up. \nPlease load in a maze first.");
        }else if (!isFinished && isInitialized) {
            try{
                new_finder.step();
                caption.setText("Stepping...\n");
                handleMazeVisualization();
                if(new_finder.isFinished()){
                    isFinished = true;
                    caption.setText("The maze has been solved. \nTotal steps: " + new_finder.getRoute().size());
                }
            }
            catch (NullPointerException e){
                caption.setText("Maze Solver EX\n");
                // in normal case it will never happen,
                // but when it did happened, let's just pretend nothing happened.
            }
            catch (NoRouteFoundException e) {
                tileSize = 30;
                main_stage.setMinHeight(650);
                main_stage.setMinWidth(650);
                handleMazePaneInitialization(1);
                caption.setText("The algorithm cannot solve this maze.\n");
            }
        }
        else {
            caption.setText("Cannot step up more. \nThe maze has been solved in "
                    + new_finder.getRoute().size() + " steps.");
        }
    }

    /**
     * A method responsible for converting the string reported from the route finder to one single string
     * that is used in maze visualization processing
     */
    public void handleMazeVisualization() {
        isNotifying = false;
        int x_len = new_maze.getTiles().get(0).size();
        int y_len = new_maze.getTiles().size();
        maze_pane.getChildren().clear();

        // set tile color according to its type
        for (int row=0; row < y_len; row++){
            for (int col=0; col<x_len; col++){
                Rectangle r = new Rectangle();   // initialize the rectangle
                r.setWidth(tileSize);
                r.setHeight(tileSize);
                if (new_finder.toDefaultString().charAt(col + row*x_len + row) == '#'){
                    r.setFill(Color.LIGHTSALMON);   // wall
                }
                else if (new_finder.toDefaultString().charAt(col + row*x_len + row) == '.'){
                    r.setFill(Color.LAVENDERBLUSH); // corridor
                }
                else if (new_maze.getTiles().get(row).get(col).toString().equals("x")){
                    r.setFill(Color.LIGHTGREEN);    // exit
                }
                else if(new_finder.toDefaultString().charAt(col + row*x_len + row) == '-'){
                    r.setFill(Color.GREY);          // past route
                }
                else{
                    r.setFill(Color.CORAL);         // valid route and entrance
                }
                if (new_finder.toDefaultString().charAt(col + row*x_len + row) == '*') {
                    r.setFill(Color.RED);           //override entrance and exit
                }

                maze_pane.add(r, col+1, row);
            }
        }

        // render the row/column indicators
        for (int i=0; i<y_len; i++) {       // handle column indicator
            Label tmp_caption = new Label(String.valueOf(y_len - i - 1));
            tmp_caption.setPrefWidth(20);
            tmp_caption.setTextFill(Color.LAVENDERBLUSH);
            tmp_caption.setAlignment(CENTER);
            maze_pane.add(tmp_caption, 0, i);
        }
        for (int i=0; i<x_len; i++) {     // handle row indicator
            Label tmp_caption_1 = new Label(String.valueOf(i));
            tmp_caption_1.setPrefWidth(tileSize);
            tmp_caption_1.setTextFill(Color.LAVENDERBLUSH);
            tmp_caption_1.setAlignment(CENTER);
            tmp_caption_1.setTextAlignment(TextAlignment.CENTER);
            maze_pane.add(tmp_caption_1, 1+i,x_len+1);
        }
    }

    /**
     * A method responsible for initializing the maze visualization at the beginning
     * @param flag     Identifying which maze plate face is to be displayed
     */
    public void handleMazePaneInitialization(int flag){
        isNotifying = true;     // reset the notification status
        maze_pane.getChildren().clear();    // clear all existing plates
        maze_pane.getColumnConstraints().clear();
        maze_pane.getRowConstraints().clear();
        maze_pane = MazePaneInitializationHelper.setPane(flag, maze_pane);
    }

    /**
     * A method responsible for calculating the new tile size when the window is resized
     */
    public void handleMazeResize() {
        int maxTileWidth = (sceneWidth - 150) / (new_maze.getTiles().get(0).size());
        int maxTileHeight = (sceneHeight - 200) / new_maze.getTiles().size();
        int newTileSize = (Math.min(maxTileWidth, maxTileHeight));
        int x_len = new_maze.getTiles().get(0).size();
        int y_len = new_maze.getTiles().size();
        if(tileSize == newTileSize) {
            return;
        }
        tileSize = newTileSize;

        // set row/column constraints for failsafe reason
        maze_pane.getColumnConstraints().clear();
        maze_pane.getRowConstraints().clear();

        maze_pane.getColumnConstraints().add(new ColumnConstraints(20));
        for (int i=0; i<x_len; i++) {
            maze_pane.getColumnConstraints().add(new ColumnConstraints(tileSize));
        }
        for (int i=0; i<y_len; i++) {
            maze_pane.getRowConstraints().add(new RowConstraints(tileSize));
        }

        if (!isNotifying) {
            handleMazeVisualization();
        }
    }
}
