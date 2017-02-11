/**
 * Author: dnj
 * Date: Mar 5, 2008, 9:44:27 PM
 * 6.005 Elements of Software Construction
 * (c) 2008, MIT and Daniel Jackson
 */
package sat.formula;

import immutable.EmptyImmutableList;
import immutable.ImmutableList;
import immutable.NonEmptyImmutableList;

import java.util.Iterator;

/**
 * A class for clauses in a CNF representation of a logic formula.
 * A clause is an immutable set of literals that does not contain
 * a literal and its negation. (Note: as of the current implementation, it is not anymore true
 * that a clause can not contain a variable and its negation.)
 * <p>
 * Note: reduce returns null; a questionable design decision. (Note from the implementor: wow!, we
 * had the same observation. I was thinking about modifying the code...)
 */
public class Clause implements Iterable<Literal> {

	private final ImmutableList<Literal> literals;

	private Clause (ImmutableList<Literal> literals) {
		this.literals = literals;
		checkRepresentation();
	}

	/**
	 * @return a clause contain a single literal
	 */
	public Clause (Literal literal) {
		this(new NonEmptyImmutableList<Literal>(literal));
		checkRepresentation();
	}

	/**
	 * @return an empty clause
	 */
	public Clause () {
		this(new EmptyImmutableList<>());
		checkRepresentation();
	}

	/**
	 * Rep invariant:
	 *       literals is non null but may be empty
	 *       contains no duplicate literals
	 *		 contains no literal and its negation
	 *       contains no null elements
	 *
	 * Abstraction function:
	 *     The list of literals l1, l2, ..., ln represents
	 *     the boolean formula (l1 or l2 or ... or ln)
	 *
	 *     For example, if the list contains a, b, !c, d then the
	 *     corresponding formula is (a or b or !c or d).
	 */
	void checkRepresentation () {
		// check whether assertions are turned on.
		// if they're not on, we want to avoid all the recursive
		// traversal that checkRepresentation(literals) would do.
		try {
			assert false;
		} catch (AssertionError e) {
			checkRepresentation(literals);
		}
	}

	void checkRepresentation (ImmutableList<Literal> literals) {
		assert literals != null : "Clause, Rep invariant: literals non-null";

		if (!literals.isEmpty()) {
			Literal first = literals.first();
			assert first != null : "Clause, Rep invariant: no null elements";

			ImmutableList<Literal> rest = literals.rest();
			assert !rest.contains(first) : "Clause, Rep invariant: no dups";
			//assert !rest.contains(first.getNegation()) : "Clause, Rep invariant: no literal and its negation";

			checkRepresentation(rest);
		}
	}

	/**
	 * Arbitrarily pick a literal from this clause
	 * Requires that clause be non-empty.
	 *
	 * @return a literal belonging to the clause
	 */
	public Literal chooseLiteral () {
		return literals.first();
	}

	/**
	 * @return true if this clause contains one literal
	 */
	public boolean isUnit () {
		return size() == 1;
	}

	/**
	 * @return true if this clause contains zero literals
	 */
	public boolean isEmpty () {
		return size() == 0;
	}

	/**
	 * @return number of literals in this clause
	 */
	public int size () {
		return literals.size();
	}

	/**
	 * Check whether clause contains given literal
	 * Requires: l is non-null
	 *
	 * @return true iff this contains the literal l
	 */
	public boolean contains (Literal l) {
		return literals.contains(l);
	}

	/**
	 * Add a literal to this clause; if already contains the literal's
	 * negation, return null.
	 * Requires: l is non-null.
	 *
	 * @return the new clause with the literal added, or null
	 */
	public Clause add (Literal l) {
		if (literals.contains(l)) {
			return this;
		}
		// if (literals.contains(l.getNegation())) {
		// 	return null;
		// }

		return new Clause(literals.add(l));
	}

	/**
	 * Negates this disjunction in CNF by returning a conjunction (namely, a Formula instance)
	 * of its negated literals.
	 *
	 * @return a Formula instance containing the negation of this clause.
	 */
	public Formula not () {
		Formula result = new Formula();

		for (Literal l: literals) {
			result = result.addClause(new Clause(l.getNegation()));
		}

		return result;
	}

	/**
	 * Merge this clause with another clause to obtain a single clause with
	 * the literals of each. It <b>does not return</b> an empty clause if a literal
	 * appears as positive
	 * in one clause and negative in the other. If a literal appears in the
	 * same polarity in both clauses, just appears once in the result.
	 * Requires: c is non-null.
	 *
	 * @return the merge of this clause and c
	 */
	public Clause merge (Clause c) {
		Clause result = this;

		for (Literal l: c) {
			result = result.add(l);
		}

		return result;
	}

	/**
	 * @return an iterator yielding the literals of this clause
	 * in an arbitrary order
	 */
	public Iterator<Literal> iterator () {
		return literals.iterator();
	}

	/**
	 * Requires: literal is non-null
	 *
	 * @return clause obtained by setting literal to true
	 * or null if the entire clause becomes true
	 */
	public Clause reduce (Literal literal) {
		ImmutableList<Literal> reducedLiterals = reduce(literals, literal);
		if (reducedLiterals == null) return null;
		else return new Clause(reducedLiterals);
	}

	private static ImmutableList<Literal> reduce (ImmutableList<Literal> literals, Literal l) {
		if (literals.isEmpty()) {
			return literals;
		}

		Literal first = literals.first();
		ImmutableList<Literal> rest = literals.rest();

		if (first.equals(l)) {
			return null;
		} else if (first.equals(l.getNegation())) {
			return rest;
		} else {
			ImmutableList<Literal> restR = reduce(rest, l);

			if (restR == null) {
				return null;
			}

			return restR.add(first);
		}
	}

	public String toString () {
		return "Clause" + literals;
	}

	@Override
	public boolean equals (Object that) {
		if (this == that)
			return true;
		if (!(that instanceof Clause))
			return false;

		Clause c = (Clause) that;

		if (size() != c.size()) {
			return false;
		}

		for (Literal l: literals) {
			if (!(c.contains(l))) {
				return false;
			}
		}

		return true;
	}

}
