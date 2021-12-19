package maze;

/**
 * Class to Implementation of InvalidMazeException
 */
public class InvalidMazeException extends RuntimeException {

    /**
     * Basic Exception constructors
     */
    public InvalidMazeException(){}
    public InvalidMazeException(String gripe){
        super(gripe);
    }
}
