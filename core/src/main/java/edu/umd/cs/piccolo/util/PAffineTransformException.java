package edu.umd.cs.piccolo.util;

/**
 * This class is used to encapsulate exceptions that may occur while performing transform operations.
 *
 * @since 1.3
 */
public class PAffineTransformException extends RuntimeException {
    /**
     * Allows for future serialization code to understand versioned binary
     * formats.
     */
    private static final long serialVersionUID = 1L;

    private final PAffineTransform errantTransform;

    /**
     * Constructs an Exception that represents an error with the
     * errantTransform.
     * 
     * @param errantTransform transform that caused the error
     */
    public PAffineTransformException(final PAffineTransform errantTransform) {
        this.errantTransform = errantTransform;
    }

    /**
     * Constructs an Exception that represents an error with the
     * errantTransform.
     * 
     * @param message Text message provided by the programmer about the context
     *            of the error
     * @param errantTransform transform that caused the error
     */
    public PAffineTransformException(final String message, final PAffineTransform errantTransform) {
        super(message);
        this.errantTransform = errantTransform;
    }

    /**
     * Constructs an Exception that wraps another and records the errant
     * transform.
     * 
     * @param throwable the root cause of the exception
     * @param errantTransform transform that's related to the error
     */
    public PAffineTransformException(final Throwable throwable, final PAffineTransform errantTransform) {
        super(throwable);
        this.errantTransform = errantTransform;
    }

    /**
     * Constructs an Exception that wraps another and records the errant
     * transform and provides a human readable message about the exception's
     * context.
     * 
     * @param message Text message provided by the programmer about the context
     *            of the error
     * @param throwable the root cause of the exception
     * @param errantTransform transform that's related to the error
     */
    public PAffineTransformException(final String message, final Throwable throwable,
            final PAffineTransform errantTransform) {
        super(message, throwable);
        this.errantTransform = errantTransform;
    }

    /**
     * Used to access the transform related to this exception.
     * 
     * @return transform related to the exception
     */
    public PAffineTransform getErrantTransform() {
        return errantTransform;
    }

}
