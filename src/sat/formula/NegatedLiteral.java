/**
 * Author: dnj
 * Date: Mar 5, 2008, 9:58:43 PM
 * 6.005 Elements of Software Construction
 * (c) 2008, MIT and Daniel Jackson
 */
package sat.formula;

import sat.env.Boolean;
import sat.env.Environment;
import sat.env.Variable;

/**
 * Class representing negative literals.
 * Works with PositiveLiteral to ensure interning of literals.
 * NegatedLiteral objects are immutable.
 */
public class NegatedLiteral extends Literal {

	// should NOT be used by clients
	NegatedLiteral (String name) {
		super(name);
	}

	public static NegatedLiteral make (Variable var) {
		return make(var.getName());
	}

	public static NegatedLiteral make (String name) {
		Literal posLiteral = PositiveLiteral.make(name);
		return (NegatedLiteral) posLiteral.getNegation();
	}

	public Boolean eval (Environment e) {
		return e.get(this.var).not();
	}

	@Override
	public String toString () {
		return "~" + var;
	}

}
