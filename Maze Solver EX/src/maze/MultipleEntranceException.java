package maze;

/**
 * Class to Implementation of MultipleEntranceException
 */
public class MultipleEntranceException extends InvalidMazeException {

    /**
     * Basic Exception constructors
     */
    public MultipleEntranceException(){}
    public MultipleEntranceException(String gripe){
        super(gripe);
    }
}