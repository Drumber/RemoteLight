package de.lars.remotelightplugins.exceptions;

/**
 * Thrown when failed to parse or invalid plugin properties file.
 */
public class PluginPropertiesException extends Exception {
	private static final long serialVersionUID = 121679394833970208L;

	/**
     * Constructs a new PluginPropertiesException with the specified detail message.
     *
     * @param   message   the detail message. The detail message is saved for
     *          later retrieval by the {@link #getMessage()} method.
     */
	public PluginPropertiesException(String message) {
		super(message);
	}
	
    /**
     * Constructs a new PluginPropertiesException with the specified cause.
     *
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
	public PluginPropertiesException(Throwable cause) {
		super(cause);
	}
	
    /**
     * Constructs a new PluginPropertiesException with the specified detail message and
     * cause.
     *
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
	public PluginPropertiesException(String message, Throwable cause) {
		super(message, cause);
	}

}
