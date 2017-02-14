/*
 * Author: dnj
 * Date: Mar 5, 2008, 8:15:32 PM
 * 6.005 Elements of Software Construction
 * (c) 2008, MIT and Daniel Jackson
 */
package sat.env;


/**
 * A Variable is a logical propositional variable.<br>
 * This datatype is immutable.
 */
public class Variable {

	/*
	 * Rep invariant
	 *     name != null
	 */
	private final String name;

	public Variable (String name) {
		this.name = name;
	}

	public Boolean evaluate (Environment e) {
		return e.get(this);
	}

	@Override
	public String toString () {
		return name;
	}

	public String getName () {
		return name;
	}

	/**
	 * @return true iff this and o represent the same literal
	 * (that is, they have the same string name)
	 */
	@Override
	public boolean equals (Object o) {
		Variable v;

		if (o == this) {
			return true;
		}
		if (!(o instanceof Variable)) {
			return false;
		}

		v = (Variable) o;

		return v.name.equals(name);
	}

}
