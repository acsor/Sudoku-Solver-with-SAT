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
 * Before I start solving this point, I should point out that I don't see any recursive
 * definition for this data type, which is what makes data type expression worth using.
 * Nevertheless, I'll try writing it as best as I can:<br>
 *     Formula = Formula() + Formula(Clause) + Formula(Variable)
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

	/**
	 * Create a new problem for solving that contains a single clause.<br>
	 *
	 * @return the problem with a single clause c.
	 */
	public Formula (Clause c) {
		clauses = new NonEmptyImmutableList<>(c);
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
	void checkRepresentation () {
		if (clauses == null) {
			throw new IllegalStateException("clauses must not be null");
		}
	}

	/**
	 * Add a clause to this problem.<br>
	 *
	 * @return a new problem with the clauses of this, but c added.
	 */
	public Formula addClause (Clause c) {
		clauses = clauses.add(c);
		checkRepresentation();

		return this;
	}

	/**
	 * Get the clauses of the formula.<br>
	 *
	 * @return list of clauses.
	 */
	public ImmutableList<Clause> getClauses () {
		return clauses;
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
	 * @return a new problem corresponding to the disjunction of this and p
	 */
	public Formula or (Formula p) {
		// TODO: implement this.
		// Hint: you'll need to use the distributive law to preserve conjunctive normal form, i.e.:
		//   to do (a & b) .or (c & d) (also expressible as (a AND b) OR (c AND d))
		//   you'll need to make (a | b) & (a | c) & (b | c) & (b | d)
		throw new RuntimeException("not yet implemented.");
	}

	/**
	 * @return a new problem corresponding to the negation of this
	 */
	public Formula not () {
		// TODO: implement this.
		// Hint: you'll need to apply DeMorgan's Laws (http://en.wikipedia.org/wiki/De_Morgan's_laws)
		// to move the negation down to the literals, and the distributive law to preserve
		// conjunctive normal form, i.e.:
		//   if you start with (a | b) & c,
		//   you'll need to make !((a | b) & c)
		//                       => (!a & !b) | !c            (moving negation down to the literals)
		//                       => (!a | !c) & (!b | !c)    (conjunctive normal form)
		throw new RuntimeException("not yet implemented.");
	}

	/**
	 * @return number of clauses in this.
	 */
	public int getSize () {
		return clauses.size();
	}

	/**
	 * @return string representation of this formula.
	 */
	public String toString () {
		final StringBuilder b = new StringBuilder();
		b.append("Problem[");

		for (Clause c : clauses) {
			b.append("\n").append(c);
		}

		return b.append("]").toString();
	}

}
