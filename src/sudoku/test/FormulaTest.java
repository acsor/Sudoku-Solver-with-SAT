package sudoku.test;

import org.junit.Test;
import sat.formula.Clause;
import sat.formula.Literal;
import sat.formula.PositiveLiteral;

public class FormulaTest {    
    Literal a = PositiveLiteral.make("a");
    Literal b = PositiveLiteral.make("b");
    Literal c = PositiveLiteral.make("c");
    Literal na = a.getNegation();
    Literal nb = b.getNegation();
    Literal nc = c.getNegation();

    // make sure assertions are turned on!  
    // we don't want to run sudoku.test.test cases without assertions too.
    // see the handout to find out how to turn them on.
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false;
    }
    
    
    // TODO: put your sudoku.test.test cases here

    
    
    // Helper function for constructing a clause.  Takes
    // a variable number of arguments, e.g.
    //  clause(a, b, c) will make the clause (a or b or c)
    // @param e,...   literals in the clause
    // @return clause containing e,...
    private Clause make(Literal... e) {
        Clause c = new Clause();
        for (int i = 0; i < e.length; ++i) {
            c = c.add(e[i]);
        }
        return c;
    }
}