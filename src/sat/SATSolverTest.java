package sat;

import org.junit.Test;

import sat.formula.Literal;
import sat.formula.PositiveLiteral;

public class SATSolverTest {
    Literal a = PositiveLiteral.make("a");
    Literal b = PositiveLiteral.make("b");
    Literal c = PositiveLiteral.make("c");
    Literal na = a.getNegation();
    Literal nb = b.getNegation();
    Literal nc = c.getNegation();

    // make sure assertions are turned on!  
    // we don't want to run test cases without assertions too.
    // see the handout to find out how to turn them on.
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false;
    }

    // TODO: put your test cases here

    
}