package maze;

/**
 * Class to Implementation of MultipleExitException
 */
public class MultipleExitException extends InvalidMazeException {

    /**
     * Basic Exception constructors
     */
    public MultipleExitException(){}
    public MultipleExitException(String gripe){
        super(gripe);
    }
}