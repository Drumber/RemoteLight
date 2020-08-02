package de.lars.remotelightplugins.exceptions;

/**
 * Thrown when failed to load a plugin.
 */
public class PluginLoadException extends Exception {
	private static final long serialVersionUID = 5252242798892196789L;

	/**
     * Constructs a new PluginLoadException with the specified detail message.
     *
     * @param   message   the detail message. The detail message is saved for
     *          later retrieval by the {@link #getMessage()} method.
     */
	public PluginLoadException(String message) {
		super(message);
	}
	
    /**
     * Constructs a new PluginLoadException with the specified cause.
     *
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
	public PluginLoadException(Throwable cause) {
		super(cause);
	}
	
    /**
     * Constructs a new PluginLoadException with the specified detail message and
     * cause.
     *
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
	public PluginLoadException(String message, Throwable cause) {
		super(message, cause);
	}

}
