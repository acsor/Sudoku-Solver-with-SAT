package tests;

import org.junit.Test;
import org.testng.Assert;
import sat.formula.Clause;
import sat.formula.Formula;
import sat.formula.Literal;
import sat.formula.PositiveLiteral;

public class FormulaTest {

	private final Literal
			a = PositiveLiteral.make("a"),
			b = PositiveLiteral.make("b"),
			c = PositiveLiteral.make("c"),
			notA = a.getNegation(),
			notB = b.getNegation(),
			notC = c.getNegation();

	public FormulaTest () {
	}

	/*
	* Make sure assertions are turned on!
	* We don't want to run sudoku.test.test cases without assertions too.
	* See the handout to find out how to turn them on.
	*/
	@Test(expected = AssertionError.class)
	public void testAssertionsEnabled () {
		assert false;
	}

	@Test
	public void testFormulasConstructors () {
		final Clause[][] clauses = {
				{make(a), make(b), make(c)},
				{make(notA), make(notB), make(c)},
				{make(a), make(notB), make(notC)},
				{make(notA), make(b), make(notC)},
		};

		for (int i = 0; i < clauses.length; i++) {
			testFormulaConstructor(clauses[i]);
		}
	}

	private void testFormulaConstructor (Clause... clauses) {
		final Formula f = new Formula(clauses);
		int i = clauses.length - 1;

		for (Clause c: f.getClauses()) {
			Assert.assertEquals(
					c,
					clauses[i]
			);
			i--;
		}
	}

	/**
	 * Helper function for constructing a clause. Takes
	 * a variable number of arguments, e.g.
	 * clause(a, b, c) will make the clause (a or b or c)
	 *
	 * @param e, ... literals in the clause
	 * @return clause containing e, ...
	 */
	private Clause make (Literal... e) {
		Clause c = new Clause();

		for (int i = 0; i < e.length; i++) {
			c = c.add(e[i]);
		}

		return c;
	}

}