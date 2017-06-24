package tests;

import org.junit.Assert;
import org.junit.Test;
import sat.formula.Clause;
import sat.formula.Literal;
import sat.formula.PositiveLiteral;

import static org.junit.Assert.assertTrue;

public class ClauseTest {

	private Literal p = PositiveLiteral.make("P"),
			q = PositiveLiteral.make("Q"),
			r = PositiveLiteral.make("R"),
			notP = p.getNegation(),
			notQ = q.getNegation(),
			notR = r.getNegation();

	/**
		makeClause sure assertions are turned on!
		we don't want to run sudoku.test.test cases without assertions too.
		see the handout to find out how to turn them on.
	 */
	@Test(expected = AssertionError.class)
	public void testAssertionsEnabled () {
		assert false;
	}

	@Test
	public void testSubstitute () {
		final Clause[] clauses = {
				makeClause(p, q, r),
				makeClause(p, notQ, r),
				makeClause(notP, notQ, r),
				makeClause(p, notQ, notR),
				makeClause(notP, q, notR),
		};
		final Literal toReduce = q;
		final Clause[] expectedResult = {
				null,
				makeClause(p, r),
				makeClause(notP, r),
				makeClause(p, notR),
				null
		};
		final int min = Math.min(clauses.length, expectedResult.length);

		for (int i = 0; i < min; i++) {
			Assert.assertEquals(
					expectedResult[i],
					clauses[i].reduce(toReduce)
			);
		}
	}

	@Test
	public void testChooseLiteral () {
		Clause c = makeClause(p, q, r);

		while (!(c.isEmpty())) {
			Literal l = c.chooseLiteral();
			assertTrue(c.contains(l));
			c = c.reduce(l.getNegation());
		}
	}

	private Clause makeClause (Literal... e) {
		Clause c = new Clause();

		for (int i = 0; i < e.length; ++i) {
			c = c.add(e[i]);
		}

		return c;
	}

}