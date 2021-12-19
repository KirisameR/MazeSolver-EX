package maze;

import java.io.Serializable;


/**
 * Class providing the definition and some handle operations on Tile object
 */
public class Tile implements Serializable {

    /**
     * A enumeration of tile types
     */
    public enum Type{
        CORRIDOR, ENTRANCE, EXIT, WALL
    }

    /**
     * Preserving the type of the tile
     */
    private Type type;

    /**
     * private constructor for tiles
     * @param t     the type of the tile to be instantiated
     */
    private Tile(Type t){
        type = t;
    }

    /**
     * A method responsible for instantiating Tiles from a char which specify its type
     * @param c     The character of the tile to be instantiated
     * @return      the tile to be instantiated
     */
    protected static Tile fromChar(char c){
        //e == entrance
        //# == wall
        //. == corridor
        //x == exit

        switch (c)
        {
            case 'e':
                return new Tile(Type.ENTRANCE);
            case '#':
                return new Tile(Type.WALL);
            case '.':
                return new Tile(Type.CORRIDOR);
            case 'x':
                return new Tile(Type.EXIT);
            default:
                return null;
        }
    }

    /**
     * A method responsible for returning the type of the tile
     * @return the type of the tile
     */
    public Tile.Type getType(){
        return type;
    }

    /**
     * A method responsible for verify whether the tile is navigable or not (i.e. is the tile a wall?)
     * @return a boolean value indicates whether it's navigable or not
     */
    public boolean isNavigable(){
        return (type != Type.WALL);
    }

    /**
     * A method responsible for converting the tile to a string, handy when printing out the maze
     * @return a string indicates the type of the tile
     */
    public String toString(){
        switch(type)
        {
            case ENTRANCE:
                return "e";
            case CORRIDOR:
                return ".";
            case WALL:
                return "#";
            case EXIT:
                return "x";
            default:
                return null;
        }
    }
}