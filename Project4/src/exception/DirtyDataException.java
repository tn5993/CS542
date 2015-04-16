package exception;

public class DirtyDataException extends Exception {

    private static final long serialVersionUID = 1L;

    public DirtyDataException() {super();}

    public DirtyDataException(String msg) {super(msg);}

    public DirtyDataException(String msg, Throwable cause) {super(msg, cause);}

    public DirtyDataException(Throwable cause) {super(cause);}
}
