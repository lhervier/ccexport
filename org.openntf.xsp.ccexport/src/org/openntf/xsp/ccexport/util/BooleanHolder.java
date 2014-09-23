package org.openntf.xsp.ccexport.util;

/**
 * Holder pour un boolean
 * @author Lionel HERVIER
 */
public class BooleanHolder {

	/**
	 * Constructeur vide
	 */
	public BooleanHolder() {}
	
	/**
	 * Constructeur à partir d'une valeur
	 * @param value la valeur
	 */
	public BooleanHolder(boolean value) {
		this.value = value;
	}
	
	/**
	 * La valeur
	 */
	public boolean value;
}
