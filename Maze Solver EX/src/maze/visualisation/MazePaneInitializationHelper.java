package maze.visualisation;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static javafx.geometry.Pos.CENTER;

/**
 * A Class responsible for helping initializing the maze pane
 */
public class MazePaneInitializationHelper extends GridPane {

    /**
     * the helper for initializing the maze pane
     * @param flag         indicating which pattern is going to be used
     * @param maze_pane   the maze pane to be initialized
     * @return            the grid pane with processed tiles
     */
    public static GridPane setPane(int flag, GridPane maze_pane){
        maze_pane.getChildren().clear();    // clear all existing plates
        maze_pane.getColumnConstraints().clear();
        maze_pane.getRowConstraints().clear();
        int row_index = 13;     // initialize it to be 13
        int column_index = 13;  // initialize it to be 13

        int[][] tmp_array_welcome = new int[][] {{4, 3}, {4, 4}, {4, 5}, {8, 3}, {8, 4}, {8, 5}, {3, 8},
                {3, 9}, {9, 8}, {9, 9}, {4, 10}, {5, 10}, {6, 10}, {7, 10}, {8, 10}};
        // keep the coordinates of a question mark revealing request
        int[][] tmp_array_unknown = new int[][] {{4, 5}, {4, 4}, {4, 3}, {5, 2}, {6, 2}, {6, 7}, {7, 2},
                {8, 3}, {8, 4}, {8, 5}, {7, 6}, {6, 7}, {6, 8}, {6, 10}};
        // keep the coordinates of an exclamation mark revealing error
        int[][] tmp_array_error = new int[][] {{6, 2}, {6, 3}, {6, 4}, {6, 5}, {6, 6}, {6, 7}, {6, 7},
                {6, 8}, {6, 10}};

        // declare an array to store these patterns
        int[][][] tmp_array = {tmp_array_unknown, tmp_array_error, tmp_array_welcome};

        // then initialize the maze plate
        for (int row=0; row < row_index; row++){
            for (int col=0; col<column_index; col++){
                Rectangle r = new Rectangle(50+row*32, 50+col*32, 30, 30);

                // draw the bound and fill the context
                if (row %(row_index-1) == 0 || col % (column_index-1) == 0){
                    r.setFill(Color.LIGHTSALMON);
                }
                else{
                    r.setFill(Color.LAVENDERBLUSH);
                }
                // draw the pattern
                for (int[] sub_array:tmp_array[flag]) {
                    if(sub_array[1] == row && sub_array[0] == col){
                        if(flag == 1){
                            r.setFill(Color.RED);
                        }
                        else {
                            r.setFill(Color.CORAL);
                        }
                    }
                }
                maze_pane.add(r, col+1, row);
            }
        }

        // draw the row/column indicator
        for (int i=0; i<row_index; i++){
            // row indicator
            Label tmp_caption = new Label(String.valueOf(row_index-i-1));
            tmp_caption.setPrefWidth(20);
            tmp_caption.setTextFill(Color.LAVENDERBLUSH);
            tmp_caption.setAlignment(CENTER);
            maze_pane.add(tmp_caption, 0, i);
            // column indicator
            Label tmp_caption_1 = new Label(String.valueOf(i));
            tmp_caption_1.setPrefWidth(30);
            tmp_caption_1.setTextFill(Color.LAVENDERBLUSH);
            tmp_caption_1.setAlignment(CENTER);
            maze_pane.add(tmp_caption_1, 1+i,column_index+1);
        }
        return maze_pane;
    }

}
