package exception;

public class TimeIntersectionException extends RuntimeException {
    public TimeIntersectionException() {
        super("Время задач пересекается");
    }
}
