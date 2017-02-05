package sudoku;

/**
 * Created by n0ne on 05/02/17.<br>
 * Exception used for signaling grammatical errors in Sudoku puzzle files.
 */
public class ParseException extends Exception {

	public ParseException (String message) {
		super(message);
	}

}


