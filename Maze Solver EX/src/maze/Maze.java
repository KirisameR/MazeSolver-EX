package maze;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;


/**
 * Class providing the definition of Maze, which is the main object to be manipulated
 */
public class Maze implements Serializable {

    /**
     * A enumeration of relative directions of a given tile
     */
    public enum Direction{
        NORTH, SOUTH, EAST, WEST
    }

    /**
     * Preserving the entrance of a maze
     */
    private Tile entrance;

    /**
     * Preserving the exit of a maze
     */
    private Tile exit;

    /**
     * Preserving all lists in an accessible data structure
     */
    private List<List<Tile>> tiles;

    /**
     * Empty constructor
     */
    private Maze() {}

    /**
     * A method responsible for instantiating the maze object by reading in a new maze from 'txt' file
     * @param src     The directory for loading the file from
     * @throws        IOException when cannot read in file
     * @return        The maze to be instantiated
     */
    public static Maze fromTxt(String src) throws IOException {
        Maze maze_return = new Maze();  //initialize the instantiation of the maze to be returned
        try (
            /**  create a file reader obj, to handle the low level details of reading the maze from
             * the 'src' file
             */
            FileReader mazeFile = new FileReader(src);
            BufferedReader mazeStream = new BufferedReader(mazeFile)
            )
        {

            // read in the first line of the file
            List<Integer> x_length = new ArrayList<>();        //initialize a list for storing every row's length

            //compute the shape of the maze
            List<List<Tile>> tmp_list_r = new ArrayList<>();   //contains every row

            int ylen = 0;   //initialize the row number of the maze

            /**
            * read in the rest of the file
            */
            while (true) {
                List<Tile> tmp_list_c = new ArrayList<>();   //contains every tile in a single column
                //read in a line once a time
                String tmpMaze = mazeStream.readLine();

                //stop reading at EOF
                if (tmpMaze == null || tmpMaze.length() == 0) {
                    break;
                }

                int xlen = tmpMaze.length();    //initialize the column number of the maze
                x_length.add(xlen);     //add the current row's length to the list

                //convert str to char[] in order to pick out certain char
                char[] tmpCharMaze = tmpMaze.toCharArray();

                //generate a row of Tiles
                for(int i=0; i<xlen; i++) {
                    Tile tmp_tile = Tile.fromChar(tmpCharMaze[i]);

                    if (tmp_tile == null) {
                        throw new InvalidMazeException("\nMaze Invalid!");
                    }
                    else{
                        tmp_list_c.add(tmp_tile);
                    }
                }

                //add the row to the bigger List
                tmp_list_r.add(tmp_list_c);

                //row ptr + 1
                ylen += 1;

            }

            /**
             * this part is responsible for evaluating the exceptions
             */
            handleExceptions(x_length, tmp_list_r);

            /**
             * set the entrance and exit of the maze to be returned
             */
            maze_return.tiles = tmp_list_r;
            for (int i = 0; i < maze_return.tiles.size(); i++) {
                for (int j = 0; j< maze_return.tiles.get(i).size(); j++) {
                    if (maze_return.tiles.get(i).get(j).toString().equals("e")) {
                        maze_return.setEntrance(maze_return.tiles.get(i).get(j));
                    }
                    if (maze_return.tiles.get(i).get(j).toString().equals("x")) {
                        maze_return.setExit(maze_return.tiles.get(i).get(j));
                    }
                }
            }

            return maze_return;
        }

        // handle the exception that the file is not found
        catch (FileNotFoundException e) {
            throw new FileNotFoundException("\n File not found! ");
        }

        // handle the exception that thrown by the FileReader Methods
        catch (IOException e) {
            throw new IOException("\n IO Error ! ");
        }
    }

    /**
     * A method responsible for handling(detecting) the possible exceptions
     * @param x_length     The list contains the length of every line
     * @throws RaggedMazeException when the maze read in is ragged
     * @throws MultipleEntranceException when the maze has multiple entrance
     * @throws MultipleExitException when the maze has multiple exit
     * @throws NoEntranceException when the maze has no entrance
     * @throws NoExitException when the maze has no exit
     * @param tmp_list_r   The list contains all tiles
     */
    private static void handleExceptions(List x_length, List<List<Tile>> tmp_list_r)
            throws RaggedMazeException, MultipleEntranceException, MultipleExitException,
            NoEntranceException, NoExitException
    {
        /**
         *Testing RaggedMaze
         */
        Set<Integer> tmp_set = new HashSet<>(x_length); //Set: unsafe operations?
        if (tmp_set.size() != 1) {                      //every row's length should be identical
            throw new RaggedMazeException("\nRagged Maze!");
        }

        int xlen = tmp_list_r.get(0).size();     //row size
        int ylen = tmp_list_r.size();            //column size
        int entrance_count = 0;                  //initialize the count of entrances
        int exit_count = 0;                      //initialize the count of exits

        //iterate through the list to count the num of entrances and lists
        for (List<Tile> tileList : tmp_list_r) {
            for (int j = 0; j < xlen; j++) {
                if (tileList.get(j).toString().equals("e")) {
                    entrance_count += 1;
                }
                if (tileList.get(j).toString().equals("x")) {
                    exit_count += 1;
                }
            }
        }

        //case: no entrance
        if (entrance_count == 0) {
            throw new NoEntranceException("\nNo Entrance!");
        }
        //case: more than one entrance
        if (entrance_count > 1) {
            throw new MultipleEntranceException("\nMultiple Entrances!");
        }
        //case: no exit
        if (exit_count == 0) {
            throw new NoExitException("\nNo Exit!");
        }
        //case: more than one exit
        if (exit_count > 1) {
            throw new MultipleExitException("\nMultiple Exits!");
        }
    }

    /**
     * A method responsible for get the coordinate of a given tile
     * @param t     The tile whose coordinate to be get
     * @return      The coordinate of the tile
     */
    public Coordinate getTileLocation(Tile t) {
        //iterate through the tiles list to find the given tile
        for (int i=0; i < tiles.size(); i++) {
            int index = tiles.get(i).indexOf(t);
            if (index != -1) {
                //convert the coordinate since the coordinate system in list accessing is different than those
                //used in representing the maze board
                return new Coordinate(index, tiles.size() - 1 - i);
            }
        }
        return new Coordinate(-1, -1);
    }

    /**
     * A method responsible for get the adjacent tile of a given tile
     * @param t     The tile whose adjacent tile is to be get
     * @param v     The direction of the very adjacent tile
     * @return      The adjacent tile
     */
    public Tile getAdjacentTile(Tile t, Direction v) {
        Coordinate origin = getTileLocation(t);
        switch(v.toString())
        {
            case "NORTH":
                origin = new Coordinate(origin.getX(), origin.getY() + 1);
                return getTileAtLocation(origin);
            case "SOUTH":
                origin = new Coordinate(origin.getX(), origin.getY() - 1);
                return getTileAtLocation(origin);
            case "WEST":
                origin = new Coordinate(origin.getX() - 1, origin.getY());
                return getTileAtLocation(origin);
            case "EAST":
                origin = new Coordinate(origin.getX() + 1, origin.getY());
                return getTileAtLocation(origin);
            default:
                origin = null;
                return getTileAtLocation(origin);
        }
    }

    /**
     * A method responsible for returning the entrance
     * @return      The entrance
     */
    public Tile getEntrance() {
        return entrance;
    }

    /**
     * A method responsible for returning the exit
     * @return      The entrance
     */
    public Tile getExit() {
        return exit;
    }

    /**
     * A method responsible for get the tile based on a given coordinate
     * @param c     The target tile's coordinate
     * @return      The tile at the location
     */
    public Tile getTileAtLocation(Coordinate c) {
        //check whether the coordinate is located in the array
        if ((c.getX() >= tiles.get(0).size()) || (c.getX() <= -1) || (c.getY() >= tiles.size()) || (c.getY()<= -1)) {
            return null;
        }
        return getTiles().get(tiles.size() - 1- c.getY()).get(c.getX());    //convert the coordinate and return
    }

    /**
     * A method responsible for returning all tiles stored in certain data structure
     * @return      The tile list
     */
    public List<List<Tile>> getTiles() {
        return tiles;
    }

    /**
     * A method responsible for setting the entrance
     * @param t     The tile which is going to be set as entrance
     */
    private void setEntrance(Tile t) {
        if (getEntrance() == null) {                //check: the entrance is not set?
            if (getTileLocation(t).getX() != -1) {  //check: the tile is in the maze?
                entrance = t;
                return;
            }
            throw new IllegalArgumentException("Tile Not Found!");
        } else {
            throw new MultipleEntranceException("Multiple Entrance detected!");
        }
    }

    /**
     * A method responsible for setting the exit
     * @param t     The tile which is going to be set as exit
     */
    private void setExit(Tile t) {
        if (getExit() == null) {                    //check: the exit is not set?
            if (getTileLocation(t).getX() != -1) {  //check: the tile is in the maze?
                exit = t;
                return;
            }
            throw new IllegalArgumentException("Tile Not Found!");
        } else {
            throw new MultipleExitException("Multiple Exit detected!");
        }
    }

    /**
     * A method responsible for convert the data structure storing lists to a single string
     * @return      The string to be printed to the console
     */
    @Override
    public String toString() {

        int ylen = getTiles().size();  //get column size
        String str_tmp = "";           //initialize the string to be returned

        for(List<Tile> row : getTiles()) {
            str_tmp += ((--ylen % 10)) + "  ";      //set the row axis
            for(Tile tile : row) {
                str_tmp += " " + tile.toString();   //set the row context
            }
            str_tmp += '\n';            //return one line
        }
        str_tmp += "\n   ";             //return one line
        for(int x=0; x<getTiles().get(0).size(); x++) {
            str_tmp += " " + (x % 10);  //set the column axis
        }

        return str_tmp;
    }

    /**
     * A nested class responsible for setting the Coordinate object and provide several helpful methods to it
     */
    public static class Coordinate{

        /**
         * Preserving the x and y axis
         */
        private int x;
        private int y;

        /**
         * Basic constructor
         * @param x_coord     x-axis coordinate
         * @param y_coord     y-axis coordinate
         */
        public Coordinate(int x_coord, int y_coord) {
            x = x_coord;
            y = y_coord;
        }

        /**
         * A method responsible for returning x axis
         * @return      the x axis
         */
        public int getX() {
            return x;
        }

        /**
         * A method responsible for returning y axis
         * @return      the y axis
         */
        public int getY() {
            return y;
        }

        /**
         * A method responsible for converting the coordinate to a string
         * @return      the string of the axis
         */
        public String toString() {
            return "(" + getX() + ", " + getY() + ")";
        }
    }
}