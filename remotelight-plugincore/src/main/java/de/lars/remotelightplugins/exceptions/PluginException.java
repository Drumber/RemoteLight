package de.lars.remotelightplugins.exceptions;

/**
 * Thrown when a plugin related exception occurs.
 */
public class PluginException extends Exception {
	private static final long serialVersionUID = 2991724748145993725L;

	/**
     * Constructs a new PluginException with the specified detail message.
     *
     * @param   message   the detail message. The detail message is saved for
     *          later retrieval by the {@link #getMessage()} method.
     */
	public PluginException(String message) {
		super(message);
	}
	
    /**
     * Constructs a new PluginException with the specified cause.
     *
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
	public PluginException(Throwable cause) {
		super(cause);
	}
	
    /**
     * Constructs a new PluginException with the specified detail message and
     * cause.
     *
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
	public PluginException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
