/*
 * Author: dnj, Hank Huang
 * Date: March 7, 2009
 * 6.005 Elements of Software Construction
 * (c) 2007-2009, MIT 6.005 Staff
 */
package sudoku;

import sat.env.Environment;
import sat.env.Variable;
import sat.formula.Clause;
import sat.formula.Formula;
import sat.formula.NegatedLiteral;
import sat.formula.PositiveLiteral;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import static sat.env.Boolean.TRUE;

/**
 * Sudoku is an immutable abstract datatype representing instances of Sudoku.
 * Each object is a partially completed Sudoku puzzle.
 */
public class Sudoku {

	private static final String CONST_EMPTY_CELL_REP = ".";
	private static final String DELIM_OCCUPIES = ",";

	public static final int CONST_MIN_CELL = 0;
	public static final int CONST_MIN_VALID_CELL = 1;
	public static final int CONST_MAX_CELL = 9;
	public static final int CONST_EMPTY_CELL = 0;

	public static final int CONST_MIN_BLOCK_SIZE = 1; //Should it really be 1?
	public static final int CONST_MAX_BLOCK_SIZE = 3;
	public static final int CONST_DEFAULT_BLOCK_SIZE = 3;

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
	 *     Whatever the value of empty cells, though, it is stored and used by the class field CONST_EMPTY_CELL.
	 * </p>
	 */
	private final int[][] squares;

	// occupies [i,j,k] means that kth symbol occupies entry in row i, column j
	private Variable[][][] occupies;

	/**
	 * Create an empty Sudoku puzzle of blockSize blockSize.
	 *
	 * @param blockSize size of one block of the puzzle. For example, new Sudoku(3)
	 *            makes a standard Sudoku puzzle with a 9x9 grid.
	 */
	public Sudoku (int blockSize) {
		this.blockSize = blockSize;
		size = (int) Math.pow(blockSize, 2);
		squares = new int[size][size];

		checkRepresentation();
		initializeOccupies();
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
		initializeOccupies();
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
				if (squares[row][column] < CONST_MIN_CELL || squares[row][column] > CONST_MAX_CELL) {
					throw new IllegalArgumentException(
							String.format(
									"cell (%d, %d)'s value must be comprised between %d and %d, found %d",
									row, column, CONST_MIN_CELL, CONST_MAX_CELL, squares[row][column]
							)
					);
				}
			}
		}
	}

	private void initializeOccupies () {
		occupies = new Variable[size][size][CONST_MAX_CELL];

		for (int row = 0; row < squares.length; row++) {
			for (int column = 0; column < squares[row].length; column++) {
				if (squares[row][column] != CONST_EMPTY_CELL) {
					occupies[row][column][squares[row][column] - 1] =
						variableFactory(row, column, squares[row][column]);
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
		if (blockSize > CONST_MAX_BLOCK_SIZE) {
			throw new IllegalArgumentException(
					String.format(
							"blockSize argument (%d) greater than max allowed (%d)",
							blockSize, CONST_MAX_BLOCK_SIZE
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

	/**
	 * <p>
	 * 		Produce readable string representation of this Sukoku grid, e.g. for a 4
	 * 		x 4 sudoku problem:<br>
	 * 		12.4<br>
	 * 		3412<br>
	 * 		2.43<br>
	 * 		4321<br>
	 * </p>
	 *
	 * @return a string corresponding to this grid
	 */
	@Override
	public String toString () {
		final StringBuilder b = new StringBuilder();

		for (int row = 0; row < squares.length; row++) {
			for (int column = 0; column < squares[row].length; column++) {
				if (squares[row][column] == CONST_EMPTY_CELL) {
					b.append(CONST_EMPTY_CELL_REP);
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
		return new ProblemFactory().getProblem();
	}

	private class ProblemFactory {

		Formula getProblem () {
			Formula result = new Formula();

			result = loadFromGrid(result);
			result = atMostOneDigitPerSquare(result);
			result = exactlyOncePerRow(result);
			result = exactlyOncePerColumn(result);
			result = exactlyOncePerBlock(result);

			return result;
		}

		private Formula loadFromGrid (Formula f) {
			//Solution must be consistent with the starting grid.
			for (int row = 0; row < occupies.length; row++) {
				for (int column = 0; column < occupies[row].length; column++) {
					for (int value = 0; value < occupies[row][column].length; value++) {
						if (occupies[row][column][value] != null) {
							f = f.addClause(
									new Clause(
											PositiveLiteral.make(occupies[row][column][value])
									)
							);
						}
					}
				}
			}

			return f;
		}

		private Formula atMostOneDigitPerSquare (Formula f) {
			//At most one digit per square
			for (int row = 0; row < squares.length; row++) {
				for (int column = 0; column < squares[row].length; column++) {

					for (int first = CONST_MIN_VALID_CELL; first <= CONST_MAX_CELL; first++) {
						for (int second = first + 1; second <= CONST_MAX_CELL; second++) {
							f = f.addClause (
									new Clause(
											NegatedLiteral.make(variableFactory(row, column, first)),
											NegatedLiteral.make(variableFactory(row, column, second))
									)
							);
						}
					}

				}
			}

			return f;
		}

		private Formula exactlyOncePerRow (Formula f) {
			Clause atLeastOnce, atMostOnce;

			//The code below will guarantee that a certain digit k will appear at least once in every row r.
			for (int row = 0; row < squares.length; row++) {
				atLeastOnce = new Clause();

				for (int value = CONST_MIN_VALID_CELL; value <= CONST_MAX_CELL; value++) {
					for (int column = 0; column < squares[row].length; column++) {
						atLeastOnce = atLeastOnce.add(
								PositiveLiteral.make(
										variableFactory(row, column, value)
								)
						);
					}
				}

				f = f.addClause(atLeastOnce);
			}

			//The code below will guarantee that every valid digit k does not appear more than once in a given row r.
			for (int row = 0; row < squares.length; row++) {
				atMostOnce = new Clause();

				for (int value = CONST_MIN_VALID_CELL; value <= CONST_MAX_CELL; value++) {
					for (int firstCol = 0; firstCol < squares[row].length; firstCol++) {
						for (int secondCol = firstCol + 1; secondCol < squares[row].length; secondCol++) {
							atMostOnce = atMostOnce
									.add(NegatedLiteral.make(variableFactory(row, firstCol, value)))
									.add(NegatedLiteral.make(variableFactory(row, secondCol, value)));
						}
					}
				}

				f = f.addClause(atMostOnce);
			}

			return f;
		}

		private Formula exactlyOncePerColumn (Formula f) {
			Clause atLeastOnce, atMostOnce;

			//The code below guarantees that each valid value k appear at least once in every column c.
			for (int column = 0; column < squares[0].length; column++) {
				atLeastOnce = new Clause();

				for (int value = CONST_MIN_VALID_CELL; value <= CONST_MAX_CELL; value++) {
					for (int row = 0; row < squares.length; row++) {
						atLeastOnce = atLeastOnce.add(
								PositiveLiteral.make(
										variableFactory(row, column, value)
								)
						);
					}
				}

				f = f.addClause(atLeastOnce);
			}

			//The code below guarantees that each valid value k does not appear more than once in every column c.
			for (int column = 0; column < squares[0].length; column++) {
				atMostOnce = new Clause();

				for (int value = CONST_MIN_VALID_CELL; value <= CONST_MAX_CELL; value++) {
					for (int firstRow = 0; firstRow < squares.length; firstRow++) {
						for (int secondRow = firstRow + 1; secondRow < squares.length; secondRow++) {
							atMostOnce = atMostOnce
									.add(NegatedLiteral.make(variableFactory(firstRow, column, value)))
									.add(NegatedLiteral.make(variableFactory(secondRow, column, value)));
						}
					}
				}

				f = f.addClause(atMostOnce);
			}

			return f;
		}

		private Formula exactlyOncePerBlock (Formula f) {
			Clause atLeastOnce, atMostOnce;
			int rowFactor, columnFactor;

			//The code below guarantees that each value k is present at least once in every block.
			for (int block = 0; block < Math.pow(blockSize, 2); block++) { //For every block:
				atLeastOnce = new Clause();
				rowFactor = block / blockSize;
				columnFactor = block % blockSize;

				for (int row = 0; row < blockSize; row++) { //For every row in the current block:
					for (int column = 0; column < blockSize; column++) { //For every column in the current block:
						for (int value = CONST_MIN_VALID_CELL; value < CONST_MAX_CELL; value++) {
							atLeastOnce = atLeastOnce.add(
									PositiveLiteral.make(
											variableFactory(
													row + rowFactor * blockSize,
													column + columnFactor * blockSize,
													value
											)
									)
							);
						}
					}
				}

				f = f.addClause(atLeastOnce);
			}

			//The code below guarantees that each value k is present at most once in every block.
			for (int block = 0; block < Math.pow(blockSize, 2); block++) { //For every block:
				atMostOnce = new Clause();
				rowFactor = block / blockSize;
				columnFactor = block % blockSize;

				for (int value = CONST_MIN_VALID_CELL; value < CONST_MAX_CELL; value++) {
					for (int row = 0; row < blockSize; row++) { //For every row in the current block:
						for (int column = 0; column < blockSize; column++) { //For every column in the current block:
						}
					}
				}

				f = f.addClause(atMostOnce);
			}

			return f;
		}

		private int minColumnLength (Formula f) {
			int result = -1;

			if (squares.length > 0) {
				result = squares[0].length;

				for (int i = 1; i < squares.length; i++) {
					if (result < squares[i].length) {
						result = squares[i].length;
					}
				}
			}

			return result;
		}

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
		final Sudoku solution = new Sudoku(blockSize, squares);

		for (int row = 0; row < solution.squares.length; row++) {
			for (int column = 0; column < solution.squares[row].length; column++) {
				for (int value = CONST_MIN_VALID_CELL; value < CONST_MAX_CELL; value++) {
					if (e.get(variableFactory(row, column, value)) == TRUE) {
						solution.squares[row][column] = value;
					}
				}
			}
		}

		return solution;
	}

	private static int[] stringToIntCellArray (String row) throws ParseException {
		final int[] result = new int[row.length()];
		int cellValue;

		for (int i = 0; i < row.length(); i++) {
			if (String.valueOf(row.charAt(i)).contentEquals(CONST_EMPTY_CELL_REP)) {
				result[i] = CONST_EMPTY_CELL;
			} else {
				cellValue = Integer.valueOf("" + row.subSequence(i, i + 1));

				if (cellValue <= CONST_MIN_CELL || cellValue > CONST_MAX_CELL) {
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

	private Variable variableFactory (int row, int column, int value) {
		if (value < CONST_MIN_CELL || value > CONST_MAX_CELL) {
			throw new IllegalStateException(
					String.format(
							"Value (%d) must be comprised between %d and %d",
							value, CONST_MIN_CELL, CONST_MAX_CELL
					)
			);
		}
		if (row < 0 || row > squares.length) {
			throw new IllegalStateException(
					String.format(
							"row (%d) is less than 0 or greater than %d",
							row, squares.length
					)
			);
		}
		if (column < 0 || column > squares[row].length) {
			throw new IllegalStateException(
					String.format(
							"column (%d) is less than 0 or greater than %d",
							column, squares[row].length
					)
			);
		}

		return new Variable(
				String.join(
						DELIM_OCCUPIES,
						String.valueOf(row),
						String.valueOf(column),
						String.valueOf(value)
				)
		);
	}

}
