/**
 * Exception used when trying to load google tiles
 * @author seb
 */
public class MapsGoogleException extends Exception {

    /**
     * Creates a new instance of MapsGoogleException without detail message.
     */
    public MapsGoogleException() {
        super();
    }

    /**
     * Constructs an instance of MapsGoogleException with the specified detail message.
     * @param msg the detail message.
     */
    public MapsGoogleException(String msg) {
        super(msg);
    }
}
