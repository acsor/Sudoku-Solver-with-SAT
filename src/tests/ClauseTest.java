package tests;

import org.junit.Test;
import sat.formula.Clause;
import sat.formula.Literal;
import sat.formula.NegatedLiteral;
import sat.formula.PositiveLiteral;

import static org.junit.Assert.assertTrue;

public class ClauseTest {

	Literal p = PositiveLiteral.make("P"),
			q = PositiveLiteral.make("Q"),
			r = PositiveLiteral.make("R"),
			notP = p.getNegation(),
			notQ = q.getNegation(),
			notR = r.getNegation();
	Clause empty = makeClause();
	Clause cp = makeClause(p);
	Clause cq = makeClause(q);
	Clause cr = makeClause(r);
	Clause cnp = makeClause(notP);
	Clause cnq = makeClause(notQ);
	Clause cpq = makeClause(p, q);
	Clause cpqr = makeClause(p, q, r);
	Clause cpnq = makeClause(p, notQ);

	// makeClause sure assertions are turned on!
	// we don't want to run sudoku.test.test cases without assertions too.
	// see the handout to find out how to turn them on.
	@Test(expected = AssertionError.class)
	public void testAssertionsEnabled () {
		assert false;
	}

	@Test
	public void testReduce () {
		Clause c = new Clause().add(p).add(q).add(r);
		Literal toReduce = c.chooseLiteral();

		System.out.println(c);

		c = c.reduce(toReduce);

		System.out.println(c);
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