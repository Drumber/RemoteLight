package de.lars.remotelightplugins.exceptions;

/**
 * Thrown when failed to initialize the plugin.
 */
public class PluginInitException extends PluginLoadException {
	private static final long serialVersionUID = 3286686638543634597L;

	/**
     * Constructs a new PluginInitException with the specified detail message.
     *
     * @param   message   the detail message. The detail message is saved for
     *          later retrieval by the {@link #getMessage()} method.
     */
	public PluginInitException(String message) {
		super(message);
	}
	
    /**
     * Constructs a new PluginInitException with the specified cause.
     *
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
	public PluginInitException(Throwable cause) {
		super(cause);
	}
	
    /**
     * Constructs a new PluginInitException with the specified detail message and
     * cause.
     *
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
	public PluginInitException(String message, Throwable cause) {
		super(message, cause);
	}

}
