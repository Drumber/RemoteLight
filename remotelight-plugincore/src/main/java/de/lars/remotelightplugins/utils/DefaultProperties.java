package de.lars.remotelightplugins.utils;

public enum DefaultProperties {
	
	/** plugin main class */
	MAIN("main", true),
	/** plugin name or ID (must be unique) */
	NAME("name", true),
	/** plugin displayname */
	DISPLAYNAME("displayname", false),
	/** plugin author */
	AUTHOR("author", true),
	/** plugin version (should be in format 'x.x.x') */
	VERSION("version", true),
	/** plugin website url */
	URL("url", false);
	
	private final String key;
	private final boolean required;
	
	private DefaultProperties(final String key, final boolean required) {
		this.key = key;
		this.required = required;
	}
	
	@Override
	public String toString() {
		return key;
	}
	
	public String getKey() {
		return toString();
	}
	
	/**
	 * Returns whether the property is required or optional
	 * 
	 * @return true if required, false otherwise
	 */
	public boolean isRequired() {
		return required;
	}

}
