/**
 * Exception used when trying to load google tiles.
 * 
 * @author seb
 * @version 1.0
 */
public class InputException extends Exception {

    /**
     * Creates a new instance of InputException without detail message.
     */
    public InputException() {
        super();
    }

    /**
     * Constructs an instance of InputException with the specified detail 
     * message.
     * 
     * @param msg the detail message.
     */
    public InputException(String msg) {
        super(msg);
    }
}
