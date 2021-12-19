package maze;

/**
 * Class to Implementation of NoEntranceException
 */
public class NoEntranceException extends InvalidMazeException {

    /**
     * Basic Exception constructors
     */
    public NoEntranceException(){}
    public NoEntranceException(String gripe){
        super(gripe);
    }
}