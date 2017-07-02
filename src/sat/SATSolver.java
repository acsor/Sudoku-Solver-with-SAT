package sat;

import immutable.EmptyImmutableList;
import immutable.ImmutableList;
import sat.env.Environment;
import sat.formula.Clause;
import sat.formula.Formula;
import sat.formula.Literal;
import sat.formula.NegatedLiteral;

import static sat.env.Boolean.FALSE;
import static sat.env.Boolean.TRUE;

/**
 * A simple DPLL SAT solver. See http://en.wikipedia.org/wiki/DPLL_algorithm
 */
public class SATSolver {

	private static Clause EMPTY_CLAUSE = new Clause();

	/**
	 * Solve the problem using a simple version of DPLL with backtracking and
	 * unit propagation. The returned environment binds literals of class
	 * toPlatformBoolean.Variable rather than the special literals used in classification of
	 * class clausal.Literal, so that clients can more readily use it.
	 *
	 * @return an environment for which the problem evaluates to Bool.TRUE, or
	 * null if no such environment exists.
	 */
	public static Environment solve (Formula formula) {
		return solve(formula.getClauses(), new Environment());
	}

	/**
	 * Takes a partial assignment of variables to values, and recursively
	 * searches for a complete satisfying assignment.
	 *
	 * @param clauses formula in conjunctive normal form
	 * @param env     assignment of some or all variables in clauses to true or
	 *                false values.
	 * @return an environment for which all the clauses evaluate to Boolean.TRUE,
	 * or null if no such environment exists.
	 */
	private static Environment solve (ImmutableList<Clause> clauses, Environment env) {
		Environment resultEnv;
		Clause shortest;
		Literal toReduce;

		if (clauses.isEmpty()) {
			return env;
		} else {
			shortest = clauses.first();
		}

		/* For performance sake, I'm not going to use the findShortestClause() method below, as I noticed that
		copy-pasting that code inside the calling method (here) and doing additional, mixed operations, even if a little
		bit untidy, sensibly speeds up the program. */
		if (shortest.isEmpty()) {
			return null;
		}
		for (Clause c: clauses.rest()) {
			if (c.isEmpty()) {
				return null;
			} else if (c.size() < shortest.size()) {
				shortest = c;
			}
		}

		toReduce = shortest.chooseLiteral();

		if (shortest.size() == 1) {
			resultEnv = solve(
					substitute(clauses, toReduce),
					env.put(toReduce.getVariable(), (toReduce instanceof NegatedLiteral) ? FALSE: TRUE)
			);
		} else {
			if (toReduce instanceof NegatedLiteral) {
				resultEnv = solve(substitute(clauses, toReduce), env.putFalse(toReduce.getVariable()));

				if (resultEnv == null) {
					resultEnv = solve(substitute(clauses, toReduce.getNegation()), env.putTrue(toReduce.getVariable()));
				}
			} else {
				resultEnv = solve(substitute(clauses, toReduce), env.putTrue(toReduce.getVariable()));

				if (resultEnv == null) {
					resultEnv = solve(
							substitute(clauses, toReduce.getNegation()), env.putFalse(toReduce.getVariable())
					);
				}
			}
		}

		return resultEnv;
	}

	/**
	 * Given a clause list and literal, produce a new list resulting from
	 * setting that literal to true.
	 *
	 * @param clauses a list of clauses
	 * @param l a literal to set to true
	 * @return a new list of clauses resulting from setting l to true
	 */
	private static ImmutableList<Clause> substitute (ImmutableList<Clause> clauses, Literal l) {
		ImmutableList<Clause> result = new EmptyImmutableList<>();
		Clause reduction;

		for (Clause c: clauses) {
			reduction = c.reduce(l);

			if (reduction != null) {
				result = result.add(reduction);
			}
		}

		return result;
	}

	private static Clause findShortestClause (ImmutableList<Clause> clauses) {
		Clause result = clauses.first();

		if (result.size() > 1) {
			for (Clause c: clauses.rest()) {
				if (c.size() < result.size()) {
					result = c;
				}
				if (result.size() == 1) { // We cannot find a shorter clause
					return result;
				}
			}
		}

		return result;
	}

}
