package tests;

import org.junit.Assert;
import org.junit.Test;
import sat.SATSolver;
import sat.env.Environment;
import sat.formula.Clause;
import sat.formula.Formula;
import sat.formula.Literal;
import sat.formula.PositiveLiteral;

import static sat.env.Boolean.UNDEFINED;

public class SATSolverTest {

	private Literal
			a = PositiveLiteral.make("a"),
			b = PositiveLiteral.make("b"),
			c = PositiveLiteral.make("c"),
			notA = a.getNegation(),
			notB = b.getNegation(),
			notC = c.getNegation();

	/**
		make sure assertions are turned on!
		we don't want to run sudoku.test.test cases without assertions too.
		see the handout to find out how to turn them on.
	 */
	@Test(expected = AssertionError.class)
	public void testAssertionsEnabled () {
		assert false;
	}

	@Test
	public void testSolve () {
		final Formula[] formulae = new Formula[]{
				//The first three formulae below were taken from the problem assignment.
				new Formula(new Clause(a, notB), new Clause(a, b)),
				new Formula(new Clause(a), new Clause(b), new Clause(a), new Clause(notB)),
				new Formula(new Clause(a), new Clause(b), new Clause(notB, c)),

				new Formula(new Clause(a, b, c), new Clause(notA, b, notC), new Clause(notA, notB, c)),
				new Formula(),
				new Formula(new Clause()),

				new Formula(new Clause(a), new Clause(notA)),
				new Formula(new Clause(a, notB, notC), new Clause(notA, b, notC), new Clause(notA, notB, c)),
				new Formula(new Clause(a, b, c), new Clause(notA, notB, c), new Clause(notA, b, c)),

				new Formula(new Clause(notA, b), new Clause(notA, notB)),
		};
		final Environment[] expectedResults = new Environment[]{
				new Environment().putTrue(a.getVariable()).put(b.getVariable(), UNDEFINED),
				null,
				new Environment().putTrue(a.getVariable()).putTrue(b.getVariable()).putTrue(c.getVariable()),

				new Environment().putTrue(b.getVariable()).putTrue(c.getVariable()),
				new Environment(),
				null,

				null,
				new Environment().putTrue(c.getVariable()),
				new Environment().putTrue(c.getVariable()),

				new Environment().putFalse(a.getVariable())
		};
		final int min = Math.min(formulae.length, expectedResults.length);
		Environment result;

		for (int i = 0; i < min; i++) {
			result = SATSolver.solve(formulae[i]);

			System.out.println("Formula: " + formulae[i]);
			System.out.println("Expected result: " + expectedResults[i]);
			System.out.println("Computed result: " + result + "\n");

			Assert.assertTrue(
					(expectedResults[i] == null && result == null) ||
							expectedResults[i].isEquivalentTo(result)
			);
		}
	}

}