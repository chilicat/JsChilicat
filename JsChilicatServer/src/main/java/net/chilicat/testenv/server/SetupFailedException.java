package net.chilicat.testenv.server;

/**
 */
class SetupFailedException extends RuntimeException {
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
