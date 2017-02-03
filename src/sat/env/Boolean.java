/**
 * Author: dnj
 * Date: Feb 27, 2008
 * Time: 3:16:01 PM
 * (c) 2008, Daniel Jackson and MIT
 */
package sat.env;

/**
 * Boolean type for use in evaluating formulas.
 * Undefined value is used to evaluate a variable that is not bound in an Environment.
 */
public enum Boolean {

	TRUE, FALSE, UNDEFINED;

	public Boolean and (Boolean b) {
		if (this == FALSE || b == FALSE) return FALSE;
		if (this == TRUE && b == TRUE) return TRUE;
		return UNDEFINED;
	}

	public Boolean or (Boolean b) {
		if (this == FALSE && b == FALSE) return FALSE;
		if (this == TRUE || b == TRUE) return TRUE;
		return UNDEFINED;
	}

	public Boolean not () {
		if (this == FALSE) return TRUE;
		if (this == TRUE) return FALSE;
		return UNDEFINED;
	}

}
