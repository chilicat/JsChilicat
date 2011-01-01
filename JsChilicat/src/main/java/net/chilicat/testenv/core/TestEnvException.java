package net.chilicat.testenv.core;

/**
 */
public class TestEnvException extends RuntimeException {
    public TestEnvException() {
    }

    public TestEnvException(String message) {
        super(message);
    }

    public TestEnvException(String message, Throwable cause) {
        super(message, cause);
    }

    public TestEnvException(Throwable cause) {
        super(cause);
    }
}
