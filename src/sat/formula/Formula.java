/**
 * Author: dnj, Hank Huang
 * Date: March 7, 2009
 * 6.005 Elements of Software Construction
 * (c) 2007-2009, MIT 6.005 Staff
 */
package sat.formula;

import immutable.EmptyImmutableList;
import immutable.ImmutableList;
import immutable.NonEmptyImmutableList;
import sat.env.Variable;

import java.util.Iterator;

/**
 * <p>
 * Formula represents an immutable boolean formula in
 * conjunctive normal form, intended to be solved by a
 * SAT solver.
 * </p>
 *
 * <p>
 * Point a. of Problem 2.
 *     <ul>
 *         <li>Formula = ImmutableList&lt;Clause&gt;;</li>
 *         <li>Clause = ImmutableList&lt;Literal&gt;;</li>
 *         <li>Literal = PositiveLiteral(Variable v) + NegativeLiteral(Variable v);</li>
 *     </ul>
 * </p>
 *
 */
public class Formula implements Iterable<Clause> {

	private ImmutableList<Clause> clauses;

	/**
	 * Create a new problem for solving that contains no clauses (that is the
	 * vacuously true problem).<br>
	 *
	 * @return the true problem.
	 */
	public Formula () {
		clauses = new EmptyImmutableList<>();
		checkRepresentation();
	}

	/**
	 * Create a new problem for solving that contains a single clause with a
	 * single literal.<br>
	 *
	 * @return the problem with a single clause containing the literal l.
	 */
	public Formula (Variable l) {
		clauses = new NonEmptyImmutableList<>(new Clause(PositiveLiteral.make(l)));
		checkRepresentation();
	}

	public Formula (ImmutableList<Clause> clauses) {
		this.clauses = clauses;
		checkRepresentation();
	}

	public Formula (Clause... clauses) {
		this.clauses = new EmptyImmutableList<>();

		for (Clause c: clauses) {
			this.clauses = this.clauses.add(c);
		}

		checkRepresentation();
	}

	/**
	 * <p>
	 * Rep invariant:
	 *      clauses != null
	 *      clauses contains no null elements (ensured by spec of ImmutableList)
	 * </p>
	 *
	 * <p>
	 * Note: although a formula is intended to be a set,
	 * the list may include duplicate clauses without any problems.
	 * The cost of ensuring that the list has no duplicates is not worth paying.
	 *
	 *    Abstraction function:
	 *        The list of clauses c1, c2, ..., cn represents
	 *        the boolean formula (c1 and c2 and ... and cn)
	 *
	 *        For example, if the list contains the two clauses (a, b) and (!c, d), then the
	 *        corresponding formula is (a or b) and (!c or d).
	 * </p>
	 */
	private void checkRepresentation () {
		if (clauses == null) {
			throw new IllegalStateException("clauses must not be null");
		}
	}

	/**
	 * Add a clause to this problem.<br>
	 * The current instance is not modified.<br>
	 *
	 * @return a new problem with the clauses of this, but c added.
	 */
	public Formula addClause (Clause c) {
		return new Formula(clauses.add(c));
	}

	/**
	 * Get the clauses of the formula.<br>
	 *
	 * @return list of clauses.
	 */
	public ImmutableList<Clause> getClauses () {
		return clauses;
	}

	public boolean contains (Clause c) {
		return clauses.contains(c);
	}

	/**
	 * Iterator over clauses.<br>
	 *
	 * @return an iterator that yields each clause of this in some arbitrary
	 * order.
	 */
	public Iterator<Clause> iterator () {
		return clauses.iterator();
	}

	/**
	 * @param p Formula instance in CNF.
	 * @return a new problem corresponding to the conjunction of this and p.
	 */
	public Formula and (Formula p) {
		final Formula result = new Formula();
		result.clauses = this.clauses;

		for (Clause c: p) {
			result.clauses = result.clauses.add(c);
		}

		return result;
	}

	/**
	 * @param p Formula instance in CNF.
	 * @return a new problem corresponding to the disjunction of this and p.
	 */
	public Formula or (Formula p) {
		/*
		Hint: you'll need to use the distributive law to preserve conjunctive normal form, i.e.:
			to do (a ^ b) v (c ^ d)
			you'll need to make (a v b) ^ (a v c) ^ (b v c) ^ (b v d) (not from the implementor: aren't the first
			two clauses wrong? Shouldn't they be (a v c) ^ (a v d)?)
		*/
		Formula result = new Formula();

		for (Clause first: clauses) {
			for (Clause second: p.clauses) {
				result = result.addClause(first.merge(second));
			}
		}

		return result;
	}

	/**
	 * @return a new problem corresponding to the negation of this.
	 */
	public Formula not () {
		/*
		TO-DO: test this code.
		Hint: you'll need to apply DeMorgan's Laws (http://en.wikipedia.org/wiki/De_Morgan's_laws)
		to move the negation down to the literals, and the distributive law to preserve
		conjunctive normal form, i.e.:
			if you start with (a v b) ^ c,
			you'll need to make ¬((a v b) ^ c)
		                      <=> (¬a ^ ¬b) v ¬c            (moving negation down to the literals)
		                      <=> (¬a v ¬c) ^ (¬b v ¬c)     (conjunctive normal form)
		 */
		Formula result = null;

		if (clauses.size() == 0) {
			result = new Formula();
		} else if (clauses.size() > 0) {
			result = clauses.first().not();

			for (Clause c: clauses.rest()) {
				result = result.or(c.not());
			}
		}

		return result;
	}

	/**
	 * @return number of clauses in this.
	 */
	public int getSize () {
		return clauses.size();
	}

	@Override
	public boolean equals (Object that) {
		final Formula cThat;

		if (!(that instanceof Formula)) {
			return false;
		}
		if (this == that) {
			return true;
		}

		cThat = (Formula) that;

		for (Clause c: clauses) {
			if (!cThat.contains(c)) {
				return false;
			}
		}
		for (Clause c: cThat.clauses) {
			if (!this.contains(c)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * @return string representation of this formula.
	 */
	public String toString () {
		final StringBuilder b = new StringBuilder();
		final Iterator<Clause> it = clauses.iterator();

		b.append(Formula.class.getSimpleName()).append("[");

		while (it.hasNext()) {
			b.append(it.next());

			if (it.hasNext()) {
				b.append(", ");
			}
		}

		return b.append("]").toString();
	}

}
