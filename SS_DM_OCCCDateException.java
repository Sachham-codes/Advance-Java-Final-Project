import java.io.Serializable;

public class SS_DM_OCCCDateException extends Exception implements Serializable {
    private static final long serialVersionUID = 1L;

    public SS_DM_OCCCDateException(String message) {
        super(message);
    }
}
