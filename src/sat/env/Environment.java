/**
 * Author: dnj
 * Date: Mar 5, 2008, 5:02:48 PM
 * 6.005 Elements of Software Construction
 * (c) 2008, MIT and Daniel Jackson
 */
package sat.env;

import immutable.ImmutableListMap;
import immutable.ImmutableMap;

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
		this(new ImmutableListMap<Variable, Boolean>());
	}

	/**
	 * @return a new environment in which l has the value b
	 * if a binding for l already exists, overwrites it
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
	 * the special UNDEFINED value of it is not bound
	 */
	public Boolean get (Variable v) {
		final Boolean b = bindings.get(v);

		if (b == null) {
			return Boolean.UNDEFINED;
		}
		return b;
	}

	@Override
	public String toString () {
		return "Environment:" + bindings;
	}

}
