package jdbc;

public class SolutionException extends RuntimeException{
    public SolutionException() {
        super();
    }

    public SolutionException(String message) {
        super(message);
    }

    public SolutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public SolutionException(Throwable cause) {
        super(cause);
    }
}
