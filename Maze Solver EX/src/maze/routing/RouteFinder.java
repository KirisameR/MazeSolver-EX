package maze.routing;

import maze.Tile;
import maze.Maze;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.io.*;


/**
 * Class providing the definition of RouteFinder, containing the main logic and operations of maze solving
 */
public class RouteFinder implements Serializable {

    /**
     * Preserve the maze to be solved
     */
    private Maze maze;

    /**
     * Preserving the solution route
     */
    private Stack<Tile> route = new Stack<>();

    /**
     * Preserving the indicator of the finish state
     */
    private boolean finished;

    /**
     * Preserving the chosen maze-solving mode
     */
    private int mode;

    /**
     * Preserving the list which stores tile access history
     */
    private ArrayList<Tile> close_list = new ArrayList<>();

    /**
     * Preserving the head of the route
     */
    private Tile head;

    /**
     * A method responsible for instantiating the RouteFinder
     * @param m     The maze to be initialized as the RouteFinder's field
     */
    public RouteFinder(Maze m, int md){
        maze = m;   //set the maze to be solved as the incoming maze
        finished = false;   //initialize the solving state
        mode = md;

        //initialize the entrance
        head = maze.getEntrance();  //set route head to be the entrance of the maze
        route.push(head);           //push the head of the route to the stack
        close_list.add(head);       //put it into the history list
    }

    /**
     * A method responsible for getting the maze to be solved
     * @return      the maze of the route finder
     */
    public Maze getMaze(){
        return maze;
    }

    /**
     * A method responsible for getting the solution route
     * @return      the current route (solution)
     */
    public List<Tile> getRoute() {
        return route;
    }

    /**
     * A method responsible for returning the solving state
     * @return      the boolean value indicates whether the solution is given or not
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * A method responsible for instantiating and initialize the RouteFinder from a serialized file
     * @param s     The directory for loading the file from
     * @throws EOFException when the file is empty
     * @throws FileNotFoundException when the directory lead to nothing
     * @throws ClassNotFoundException when the serialized file read in is invalid
     * @return      the route finder to be instantiated
     */
    public static RouteFinder load(String s) throws EOFException, FileNotFoundException, ClassNotFoundException {
        RouteFinder route_finder_to_read = null;

        try {
            FileInputStream fileIn = new FileInputStream(s);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            route_finder_to_read = (RouteFinder) in.readObject();
            in.close();
            fileIn.close();
        } catch (EOFException e) {
            throw new EOFException("error: empty file!");
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("File not found!");
        } catch (IOException | ClassNotFoundException e){
            throw new ClassNotFoundException("No route found! File is Invalid!");
        }
        return route_finder_to_read;
    }

    /**
     * A method responsible for saving the current game state to a serialized file
     * @param s     The directory for saving the file
     * @throws IOException when unexpected IO error happened
     */
    public void save(String s) throws IOException {
        //instantiating a new route finder which is to be stored, and assign all important attributes to it
        RouteFinder route_finder_to_save = new RouteFinder(maze, mode);
        route_finder_to_save.route = route;
        route_finder_to_save.finished = finished;
        route_finder_to_save.close_list = close_list;
        route_finder_to_save.head = head;
        route_finder_to_save.maze = maze;

        try {
            FileOutputStream fileOut = new FileOutputStream(s);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(route_finder_to_save);
            out.close();
            fileOut.close();
        } catch(IOException e){
            throw new IOException();
        }
    }

    /**
     * A method responsible for executing one step of solution
     * @throws NoRouteFoundException when the algorithm is unable to compute a solution for the maze
     * @return      the boolean value of whether the solution is computed or not
     */
    public boolean step() throws NoRouteFoundException {
        try{
            if (!finished) {
                handleNextStep(head);
                finished = head.toString().equals("x");
            }
            return head.toString().equals("x");
        }
        catch(NoRouteFoundException e) {
            throw new NoRouteFoundException("Unable to solve the maze!");
        }

    }

    /**
     * A method responsible for converting the maze board to a single string
     * @return      the string to be printed to the console
     */
    @Override
    public String toString(){

        int ylen = maze.getTiles().size();            //column size
        String str_return = "";

        for(List<Tile> row : maze.getTiles()) {
            str_return += ((--ylen % 10)) + "  ";
            for(Tile tile : row) {
                String char_processed = close_list.contains(tile)? (route.contains(tile)?"*":"-"):tile.toString();
                str_return += " " + char_processed;
            }
            str_return += '\n';
        }

        str_return += "\n   ";
        for(int x=0; x<maze.getTiles().get(0).size(); x++) {
            str_return += " " + (x % 10);
        }
        return str_return;


    }


    //A* helpers

    /**
     * A method responsible for computing the total cost
     * @param t     The tile to be calculated total cost for
     * @return      the total cost of the specific tile
     */
    private int totalCost(Tile t){
        //calculate base cost
        int base_cost = (Math.abs(maze.getTileLocation(t).getX() - maze.getTileLocation(head).getX())) + Math.abs((maze.getTileLocation(head).getY() - maze.getTileLocation(t).getY()));

        //calculate heuristic cost
        int heuristic_cost = (Math.abs(maze.getTileLocation(t).getX() - maze.getTileLocation(maze.getExit()).getX())) + Math.abs((maze.getTileLocation(maze.getExit()).getY() - maze.getTileLocation(t).getY()));

        return (mode == 0)? base_cost + heuristic_cost : base_cost;
    }

    /**
     * Main handler of the A* algorithm
     * @param t     The tile to be read in as reference for step updating
     */
    private void handleNextStep(Tile t){

        if (mode == 0 || mode == 1) {
            //generate a new list storing all four directions for further enumerations
            List<Maze.Direction> direction = new ArrayList<>(){{add(Maze.Direction.NORTH); add(Maze.Direction.SOUTH); add(Maze.Direction.WEST); add(Maze.Direction.EAST);}};

            //initialize the minimum cost
            int min = Integer.MAX_VALUE;

            //initialize the next step
            Tile next_step = t;

            //enumerate possible choices, and pick the one which has the smallest cost
            for (Maze.Direction dir : direction){
                if ( !(maze.getAdjacentTile(t, dir) == null) && (!maze.getAdjacentTile(t, dir).toString().equals("#")) && (!close_list.contains(maze.getAdjacentTile(t, dir))) && (totalCost(t) <= min) ){
                    min = totalCost(t);
                    next_step = maze.getAdjacentTile(t, dir);
                }
            }
            close_list.add(next_step);  //keep the handler from accessing it again

            //next_step remain unchanged, means we can not provide a solution base on current state
            if (next_step == t) {

                //this case: we cannot find any solution even if we go back to the entrance
                if (route.peek() == maze.getEntrance()) {
                    throw new NoRouteFoundException("I give up, no route found!");
                }

                //go back one step, and try other solutions
                close_list.add(route.pop());
                head = route.peek();
            }
            else{
                //go forward one step
                route.push(next_step);
                close_list.add(head);
                head = next_step;
            }
        }

        else if (mode == 2) {
            // DFS
        }
        else {
            // BFS
        }

    }

    /**
     * Main handler for returning a simpler string
     * for MazeApplication visualisation processing
     * @return      the string to be processed by the maze application
     */
    public String toDefaultString(){
        int ylen = maze.getTiles().size();            //column size
        String str_return = "";

        for(List<Tile> row : maze.getTiles()) {
            for(Tile tile : row) {
                String char_processed = close_list.contains(tile)? (route.contains(tile)?"*":"-"):tile.toString();
                str_return +=  char_processed;
            }
            str_return += '\n';
        }
        return str_return;
    }
}