/*
 * Author: dnj
 * Date: Mar 5, 2008, 5:02:48 PM
 * 6.005 Elements of Software Construction
 * (c) 2008, MIT and Daniel Jackson
 */
package sat.env;

import immutable.ImmutableListMap;
import immutable.ImmutableMap;

import static sat.env.Boolean.UNDEFINED;

/**
 * An environment is an immutable mapping from variables to boolean values.
 * A special 3-valued Boolean type is used to handle the case
 * in which a variable is to be evaluated that has no binding.
 * <p>
 * Typically, clients are expected to bind variables explicitly only
 * to Boolean.TRUE and Boolean.FALSE, and Boolean.UNDEFINED is used only
 * to return a boolean value for an unbound variable. But this
 * implementation does not prevent a variable from being explicitly
 * bound to UNDEFINED.
 */
public class Environment {

	/*
	 * Rep invariant
	 *     bindings != null
	 */
	private ImmutableMap<Variable, Boolean> bindings;

	private Environment (ImmutableMap<Variable, Boolean> bindings) {
		this.bindings = bindings;
	}

	public Environment () {
		this(new ImmutableListMap<>());
	}

	/**
	 * @return a new environment in which l has the value b.<br>
	 * If a binding for l already exists, overwrites it.
	 */
	public Environment put (Variable v, Boolean b) {
		return new Environment(bindings.put(v, b));
	}

	/**
	 * @return a new environment in which l has the value Boolean.TRUE
	 * if a binding for l already exists, overwrites it
	 */
	public Environment putTrue (Variable v) {
		return new Environment(bindings.put(v, Boolean.TRUE));
	}

	/**
	 * @return a new environment in which l has the value Boolean.FALSE
	 * if a binding for l already exists, overwrites it
	 */
	public Environment putFalse (Variable v) {
		return new Environment(bindings.put(v, Boolean.FALSE));
	}

	/**
	 * @return the boolean value that l is bound to, or
	 * the special UNDEFINED value if it is not bound
	 */
	public Boolean get (Variable v) {
		final Boolean b = bindings.get(v);

		if (b == null) {
			return UNDEFINED;
		}
		return b;
	}

	/**
	 *
	 * @param other another environment to compare with
	 * @return
	 */
	public boolean isEquivalentTo (Environment other) {
		// The problem assignment clearly specifies to not modify or add any public method, amongst other things.
		// An explanation for why this is being done is included in ImmutableMap.
		Boolean temp;

		if (other == null) {
			return false;
		}
		if (other == this) {
			return true;
		}

		for (Variable v: bindings.keys()) {
			temp = get(v);

			if (temp != UNDEFINED && temp != other.get(v)) {
				return false;
			}
		}

		for (Variable v: other.bindings.keys()) {
			temp = get(v);

			if (temp != UNDEFINED && temp != this.get(v)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String toString () {
		return "Environment:" + bindings;
	}

}
