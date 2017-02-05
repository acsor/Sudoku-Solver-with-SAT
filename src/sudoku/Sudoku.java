/**
 * Author: dnj, Hank Huang
 * Date: March 7, 2009
 * 6.005 Elements of Software Construction
 * (c) 2007-2009, MIT 6.005 Staff
 */
package sudoku;

import com.sun.javaws.exceptions.InvalidArgumentException;
import sat.env.Environment;
import sat.env.Variable;
import sat.formula.Formula;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * Sudoku is an immutable abstract datatype representing instances of Sudoku.
 * Each object is a partially completed Sudoku puzzle.
 */
public class Sudoku {

	private static final String VAL_EMPTY_CELL_REP = ".";

	public static final int VAL_MIN_CELL = 0;
	public static final int VAL_MAX_CELL = 9;
	public static final int VAL_EMPTY_CELL = 0;

	public static final int VAL_MIN_BLOCK_SIZE = 1; //Should it really be 1?
	public static final int VAL_MAX_BLOCK_SIZE = 3;
	public static final int VAL_DEFAULT_BLOCK_SIZE = 3;

	private final int blockSize; // blockSize: standard puzzle has blockSize 3
	private final int size; // number of rows and columns: standard puzzle has size 9

	/**
	 * <p>
	 * Known values: squares[i][j] represents the squares in the ith row and jth column,
	 * contains -1 if the digit is not present, else i>=0 to represent the digit i+1
	 * (digits are indexed from 0 and not 1 so that we can take the number k
	 * from squares[i][j] and use it to index into occupies[i][j][k])
	 * </p>
	 *
	 * <b>Note from the class implementor:</b>
	 * <p>
	 *     The specification just above, which hasn't been constructed by the one writing
	 *     this comment, is in conflict with what is specified at Sudoku(int, int[][]) method's
	 *     comment. I am choosing to store a value of 0 for indicating an empty cell, because
	 *     in a real Sudoku game cells are generally not filled with zeros, and so there
	 *     shouldn't be any problem in using that number as a meta-value.<br>
	 *     Whatever the value of empty cells, though, it is stored and used by the class field VAL_EMPTY_CELL.
	 * </p>
	 */
	private final int[][] squares;

	// occupies [i,j,k] means that kth symbol occupies entry in row i, column j
	//private final Variable[][][] occupies;
	private int[][] selectedSymbol;

	/**
	 * Create an empty Sudoku puzzle of blockSize blockSize.
	 *
	 * @param blockSize size of one block of the puzzle. For example, new Sudoku(3)
	 *            makes a standard Sudoku puzzle with a 9x9 grid.
	 */
	public Sudoku (int blockSize) {
		this.blockSize = blockSize;
		size = (int) Math.pow(blockSize, 2);
		squares = new int[size][];

		for (int i = 0; i < squares.length; i++) {
			//The JRE should take care of initializing int[] arrays' values to 0.
			squares[i] = new int[size];
		}

		checkRepresentation();
	}

	/**
	 * Creates a Sudoku puzzle.<br>
	 *
	 * @param squares digits or blanks of the Sudoku grid. squares[i][j] represents
	 *               the squares in the ith row and jth column, contains 0 for a
	 *               blank, else i to represent the digit i. So
	 *               <ul>
	 *				 	<li>0, 0, 0, 1</li>
	 *				 	<li>2, 3, 0, 4</li>
	 *				 	<li>0, 0, 0, 3</li>
	 *				 	<li>4, 1, 0, 2</li>
	 *				 </ul>
	 *               represents the dimension-2 Sudoku grid:
	 *               <ul>
	 *				 	<li>...1</li>
	 *				 	<li>23.4</li>
	 *				 	<li>...3</li>
	 *				 	<li>41.2</li>
	 *               </ul>
	 * @param blockSize blockSize of puzzle Requires that blockSize * blockSize == squares.length == squares[i].length
	 * for 0<=i<blockSize.<br>
	 */
	public Sudoku (int blockSize, int[][] squares) {
		this.squares = squares.clone();
		this.blockSize = blockSize;
		size = (int) Math.pow(blockSize, 2);

		checkRepresentation();
	}

	/**
	 * Checks the representation invariant of the current instance.<br>
	 * In particular, checkRepresentation() does the following:
	 * <ul>
	 *     <li>Checks that blockSize and size have positive values;</li>
	 *     <li>Ensures that blockSize fits into size by testing blockSize ^ 2 = size;</li>
	 *     <li>Checks that columns' and rows' length matches size;</li>
	 *     <li>Examines values of squares, controlling they are contained within the range
	 *     0..9, where 0 indicates a "missing" value.</li>
	 * </ul>
	 */
	private void checkRepresentation () {
		if (blockSize <= 0) {
			throw new IllegalArgumentException("blockSize must be a positive number");
		}
		if (size <= 0) {
			throw new IllegalArgumentException("size must be a positive number");
		}
		if (Math.pow(blockSize, 2) != size) {
			throw new IllegalArgumentException(
					String.format(
							"blockSize ^ 2 (%d) is not size (%d)",
							(int) Math.pow(blockSize, 2), size
					)
			);
		}
		if (squares.length != size) {
			throw new IllegalArgumentException(
					String.format(
							"column length must be %d, found %d",
							size, squares.length
					)
			);
		}
		for (int row = 0; row < squares.length; row++) {
			if (squares[row].length != size) {
				throw new IllegalArgumentException(
						String.format(
								"%dth row length must be %d, found %d",
								row, size, squares[row].length
						)
				);
			}
		}

		for (int row = 0; row < squares.length; row++) {
			for (int column = 0; column < squares[row].length; column++) {
				if (squares[row][column] < VAL_MIN_CELL || squares[row][column] > VAL_MAX_CELL) {
					throw new IllegalArgumentException(
							String.format(
									"cell (%d, %d)'s value must be comprised between %d and %d, found %d",
									row, column, VAL_MIN_CELL, VAL_MAX_CELL, squares[row][column]
							)
					);
				}
			}
		}
	}

	/**
	 * Reads in a file containing a Sudoku puzzle.
	 *
	 * @param blockSize Dimension of puzzle. Requires: at most blockSize of 3, because
	 *                 otherwise need different file format.
	 * @param fileName of file containing puzzle. The file should contain one line
	 *                 per row, with each squares in the row represented by a digit,
	 *                 if known, and a period otherwise. With blockSize blockSize, the file
	 *                 should contain blockSize * blockSize rows, and each row should contain
	 *                 blockSize * blockSize characters.
	 * @return Sudoku object corresponding to file contents
	 * @throws IOException    if file reading encounters an error.
	 * @throws ParseException if file has error in its format.
	 * @throws IllegalArgumentException if blockSize value is invalid.
	 */
	public static Sudoku fromFile (int blockSize, String fileName) throws IOException, ParseException {
		if (blockSize > VAL_MAX_BLOCK_SIZE) {
			throw new IllegalArgumentException(
					String.format(
							"blockSize argument (%d) greater than max allowed (%d)",
							blockSize, VAL_MAX_BLOCK_SIZE
					)
			);
		}
		int[][] cells = new int[(int) Math.pow(blockSize, 2)][];
		int row = 0;
		Scanner in = null;

		try {
			in = new Scanner(new File(fileName));

			while (in.hasNextLine()) {
				cells[row] = stringToIntCellArray(in.nextLine());

				if (cells[row].length != Math.pow(blockSize, 2)) {
					throw new ParseException(
							String.format(
									"Row %d contains %d characters, %d expected",
									row, cells[row].length, (int) Math.pow(blockSize, 2)
							)
					);
				}

				row++;
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}

		if (row != Math.pow(blockSize, 2)) {
			throw new ParseException(
					String.format(
							"File contains %d rows, expected %d",
							row, (int) Math.pow(blockSize, 2)
					)
			);
		}

		//The representation will be checked automatically by the Sudoku constructor
		return new Sudoku(blockSize, cells);
	}

	public Variable occupies (int row, int column, int value) {
		final Variable result = new Variable(
				String.format("%d%d%d", row, column, value)
		);

		return result;
	}

	/**
	 * Produce readable string representation of this Sukoku grid, e.g. for a 4
	 * x 4 sudoku problem:
	 * 12.4
	 * 3412
	 * 2.43
	 * 4321
	 *
	 * @return a string corresponding to this grid
	 */
	@Override
	public String toString () {
		final StringBuilder b = new StringBuilder();

		for (int row = 0; row < squares.length; row++) {
			for (int column = 0; column < squares[row].length; column++) {
				if (squares[row][column] == VAL_EMPTY_CELL) {
					b.append(VAL_EMPTY_CELL_REP);
				} else {
					b.append(squares[row][column]);
				}
			}
			b.append("\n");
		}

		return b.toString();
	}

	/**
	 * @return a SAT problem corresponding to the puzzle, using variables with
	 * names of the form occupies(i,j,k) to indicate that the kth symbol
	 * occupies the entry in row i, column j
	 */
	public Formula getProblem () {
		// TODO: implement this.
		throw new RuntimeException("not yet implemented.");
	}

	/**
	 * Interpret the solved SAT problem as a filled-in grid.
	 *
	 * @param e Assignment of variables to values that solves this puzzle.
	 *          Requires that e came from a solution to this.getProblem().
	 * @return a new Sudoku grid containing the solution to the puzzle, with no
	 * blank entries.
	 */
	public Sudoku interpretSolution (Environment e) {
		// TODO: implement this.
		throw new RuntimeException("not yet implemented.");
	}

	private static int[] stringToIntCellArray (String row) throws ParseException {
		final int[] result = new int[row.length()];
		int cellValue;

		for (int i = 0; i < row.length(); i++) {
			if (String.valueOf(row.charAt(i)).contentEquals(VAL_EMPTY_CELL_REP)) {
				result[i] = VAL_EMPTY_CELL;
			} else {
				cellValue = Integer.valueOf("" + row.subSequence(i, i + 1));

				if (cellValue <= VAL_MIN_CELL || cellValue > VAL_MAX_CELL) {
					throw new ParseException(
							String.format(
									"Unrecognized symbol %c", row.charAt(i)
							)
					);
				} else {
					result[i] = cellValue;
				}
			}
		}

		return result;
	}

}
