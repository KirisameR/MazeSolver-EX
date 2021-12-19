package maze.routing;

/**
 * Class to Implementation of NoRouteFoundException
 */
public class NoRouteFoundException extends RuntimeException {

    /**
     * Basic Exception constructors
     */
    public NoRouteFoundException(){}
    public NoRouteFoundException(String gripe){
        super(gripe);
    }
}
