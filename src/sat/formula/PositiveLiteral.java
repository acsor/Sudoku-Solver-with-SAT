/**
 * Author: dnj
 * Date: Mar 5, 2008, 9:57:47 PM
 * 6.005 Elements of Software Construction
 * (c) 2008, MIT and Daniel Jackson
 */
package sat.formula;

import immutable.ImmutableListMap;
import immutable.ImmutableMap;
import sat.env.Variable;

/**
 * Class representing positive literals.
 * Works with NegatedLiteral to ensure interning of literals.
 * PositiveLiteral objects are immutable.
 */
public class PositiveLiteral extends Literal {

	/*
	 * Mapping of positive literals that have already been allocated, keyed on their names
	 * Invariant: non null, and no key or value is null
	 */
	static ImmutableMap<String, PositiveLiteral> allocatedPosLiterals = new ImmutableListMap<String, PositiveLiteral>();

	private PositiveLiteral (String name) {
		super(name);
	}

	public static PositiveLiteral make (Variable var) {
		return make(var.getName());
	}

	/**
	 * Factory method. Preserves the invariant that only one object
	 * will exist to represent a literal of a given name.
	 *
	 * @return the positive literal with the given name
	 */
	public static PositiveLiteral make (String name) {
		PositiveLiteral literal = allocatedPosLiterals.get(name);
		if (literal == null) {
			literal = new PositiveLiteral(name);
			NegatedLiteral negated = new NegatedLiteral(name);
			literal.negation = negated;
			negated.negation = literal;
			allocatedPosLiterals = allocatedPosLiterals.put(name, literal);
		}
		literal.checkRepresentation();
		return literal;
	}

	public String toString () {
		return var.toString();
	}
}
