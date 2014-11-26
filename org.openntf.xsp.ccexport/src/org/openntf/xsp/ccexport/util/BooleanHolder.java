package org.openntf.xsp.ccexport.util;

/**
 * Simple holder for a boolean value
 * @author Lionel HERVIER
 */
public class BooleanHolder {

	/**
	 * Empty constructor
	 */
	public BooleanHolder() {}
	
	/**
	 * Constructor from a given value
	 * @param value the value
	 */
	public BooleanHolder(boolean value) {
		this.value = value;
	}
	
	/**
	 * The value
	 */
	public boolean value;
}
