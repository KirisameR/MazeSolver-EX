package maze;

/**
 * Class to Implementation of NoExitExceptio
 */
public class NoExitException extends InvalidMazeException {

    /**
     * Basic Exception constructors
     */
    public NoExitException(){}
    public NoExitException(String gripe){
        super(gripe);
    }
}