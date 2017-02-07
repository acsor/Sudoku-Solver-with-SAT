package tests;

import org.junit.Test;
import sat.formula.Clause;
import sat.formula.Literal;
import sat.formula.PositiveLiteral;

import static org.junit.Assert.assertTrue;

public class ClauseTest {

	// helpful values for sudoku.test.test cases
	Clause empty = makeClause();
	Literal p = PositiveLiteral.make("P");
	Literal q = PositiveLiteral.make("Q");
	Literal r = PositiveLiteral.make("R");
	Literal np = p.getNegation();
	Literal nq = q.getNegation();
	Literal nr = r.getNegation();
	Clause cp = makeClause(p);
	Clause cq = makeClause(q);
	Clause cr = makeClause(r);
	Clause cnp = makeClause(np);
	Clause cnq = makeClause(nq);
	Clause cpq = makeClause(p, q);
	Clause cpqr = makeClause(p, q, r);
	Clause cpnq = makeClause(p, nq);

	// makeClause sure assertions are turned on!
	// we don't want to run sudoku.test.test cases without assertions too.
	// see the handout to find out how to turn them on.
	@Test(expected = AssertionError.class)
	public void testAssertionsEnabled () {
		assert false;
	}

	@Test
	public void testChooseLiteral () {
		Clause c = cpqr;
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