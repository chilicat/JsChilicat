package net.chilicat.testenv.core;

/**
 */
public class SetupFailedException extends TestEnvException {
    public SetupFailedException() {
    }

    public SetupFailedException(String message) {
        super(message);
    }

    public SetupFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public SetupFailedException(Throwable cause) {
        super(cause);
    }
}
