package edu.umd.cs.piccolo.util;

public class PAffineTransformException extends RuntimeException {
    /**
     * Allows for future serialization code to understand versioned binary
     * formats.
     */
    private static final long serialVersionUID = 1L;
    
    private final PAffineTransform errantTransform;

    public PAffineTransformException(PAffineTransform errantTransform) {
        this.errantTransform = errantTransform;
    }

    public PAffineTransformException(String message, PAffineTransform errantTransform) {
        super(message);
        this.errantTransform = errantTransform;
    }

    public PAffineTransformException(Throwable throwable, PAffineTransform errantTransform) {
        super(throwable);
        this.errantTransform = errantTransform;
    }

    public PAffineTransformException(String message, Throwable throwable, PAffineTransform errantTransform) {
        super(message, throwable);
        this.errantTransform = errantTransform;
    }
    
    public PAffineTransform getErrantTransform() {
        return errantTransform;
    }

}
