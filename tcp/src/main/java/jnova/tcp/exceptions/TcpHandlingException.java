package jnova.tcp.exceptions;

public class TcpHandlingException extends Exception {

    public TcpHandlingException() {
        super();
    }

    public TcpHandlingException(String message) {
        super(message);
    }

    public TcpHandlingException(String message, Throwable cause) {
        super(message, cause);
    }

    public TcpHandlingException(Throwable cause) {
        super(cause);
    }
}

