package tests;

import org.junit.Test;
import org.testng.Assert;
import sat.formula.Clause;
import sat.formula.Formula;
import sat.formula.Literal;
import sat.formula.PositiveLiteral;

import java.util.Arrays;

public class FormulaTest {

	private final Literal
			a = PositiveLiteral.make("a"),
			b = PositiveLiteral.make("b"),
			c = PositiveLiteral.make("c"),
			d = PositiveLiteral.make("d"),
			e = PositiveLiteral.make("e"),
			f = PositiveLiteral.make("f"),
			g = PositiveLiteral.make("g"),
			h = PositiveLiteral.make("h"),
			notA = a.getNegation(),
			notB = b.getNegation(),
			notC = c.getNegation(),
			notD = d.getNegation(),
			notE = e.getNegation(),
			notF = f.getNegation(),
			notG = g.getNegation(),
			notH = h.getNegation();

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

	@Test
	public void testOr () {
		final Formula[] firstInput = {
				new Formula(make(a, b), make(c, d)),
				new Formula(make(a), make(b, c, d)),
				new Formula(make(notA), make(notB)),
		};
		final Formula[] secondInput = {
				new Formula(make(e, f), make(g, h)),
				new Formula(make(e, f, g), make(h)),
				new Formula(make(notC)),
		};
		final Formula[] expectedOutput = {
				new Formula(make(a, b, e, f), make(a, b, g, h), make(c, d, e, f), make(c, d, g, h)),
				new Formula(make(a, e, f, g), make(a, h), make(b, c, d, e, f, g), make(b, c, d, h)),
				new Formula(make(notA, notC), make(notB, notC)),
		};
		final int min = Math.min(firstInput.length, secondInput.length);

		for (int i = 0; i < min; i++) {
			Assert.assertEquals(
					(Object) firstInput[i].or(secondInput[i]),
					(Object) expectedOutput[i]
			);
		}
	}

	@Test
	public void testAnd () {
		final Formula
				first = new Formula(make(a), make(notB), make(c)),
				second = new Formula(make(b), make(notC)),
				expectedResult = new Formula(make(a), make(notB), make(c), make(b), make(notC));

		Assert.assertEquals(
				(Object) first.and(second),
				(Object) expectedResult
		);
	}

	@Test
	public void testNot () {
		final Formula[] formulas = {
				new Formula(),
				new Formula(make(a, b), make(c)), //(a v b) ^ c
				new Formula(make(a, b), make(a, c)),
				new Formula(make(a), make(b), make(c))
		};
		final Formula[] expectedNegations = {
				new Formula(),
				new Formula(make(notA, notC), make(notB, notC)), //(¬a v ¬c) ^ (¬b v ¬c)
				new Formula(make(notA), make(notA, notC), make(notB, notA), make(notB, notC)),
				new Formula(make(notA, notB, notC))
		};
		final int min = Math.min(formulas.length, expectedNegations.length);

		for (int i = 0; i < min; i++) {
			// System.out.format("formulas[%d] = %s\n", i, formulas[i]);
			// System.out.format("formulas[%d].not() = %s\n", i, formulas[i].not());
			// System.out.format("expectedNegations[%d] = %s\n\n", i, expectedNegations[i]);

			Assert.assertEquals(
					(Object) formulas[i].not(),
					(Object) expectedNegations[i]
			);
		}
	}

	@Test
	public void testEquals () {
		final Formula[] first = {
				new Formula(make(a, notA, b), make(c, b)),
				new Formula(make(a, b), make(c, b)),
				new Formula(make(c, a), make(notB, notA)),
				new Formula(make(a, b), make(b, c), make(c, a))
		};
		final Formula[] second = {
				new Formula(make(c, b), make(a, notA, b)),
				new Formula(make(b, a), make(c, b)),
				new Formula(make(notA, notB), make(c, a)),
				new Formula(make(b, c), make(c, a), make(a, b))
		};
		final int min = Math.min(first.length, second.length);

		for (int i = 0; i < min; i++) {
			Assert.assertEquals(
					(Object) first[i],
					(Object) second[i]
			);
		}
	}

	private void testFormulaConstructor (Clause... clauses) {
		final Formula f = new Formula(clauses);

		for (Clause c: clauses) {
			Assert.assertTrue(
					f.contains(c)
			);
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