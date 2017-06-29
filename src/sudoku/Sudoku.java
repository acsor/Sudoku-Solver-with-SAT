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
import java.util.Arrays;
import java.util.Scanner;

import static sat.env.Boolean.TRUE;

/**
 * Sudoku is an immutable abstract datatype representing instances of Sudoku.
 * Each object is a partially completed Sudoku puzzle.
 */
public class Sudoku {

	private static final String CELL_REP_EMPTY = ".";
	private static final String SEP_OCCUPIES = ",";

	/**
	 * The least valid value within every cell of the Sudoku.<br>
	 * A value of {@code CELL_MIN} (or equivalently {@code CELL_EMPTY})
	 * is reserved for empty cells.
	 */
	public static final int CELL_MIN = 0;
	public static final int CELL_MIN_VALID = 1;
	public static final int CELL_MAX_VALID = 9;
	public static final int CELL_EMPTY = 0;

	public static final int BLOCK_SIZE_MIN = 1; //Should it really be 1?
	public static final int BLOCK_SIZE_MAX = 3;
	public static final int BLOCK_SIZE_DEFAULT = 3;

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
	 *     Whatever the value of empty cells, though, it is stored and used by the class field CELL_EMPTY.
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
				if (squares[row][column] < CELL_MIN || squares[row][column] > CELL_MAX_VALID) {
					throw new IllegalArgumentException(
							String.format(
									"cell (%d, %d)'s value must be comprised between %d and %d, found %d",
									row, column, CELL_MIN, CELL_MAX_VALID, squares[row][column]
							)
					);
				}
			}
		}
	}

	private void initializeOccupies () {
		occupies = new Variable[size][size][CELL_MAX_VALID + 1];

		for (int row = 0; row < squares.length; row++) {
			for (int column = 0; column < squares[row].length; column++) {
				if (squares[row][column] != CELL_EMPTY) {
					occupies[row][column][squares[row][column]] =
							variableFactory(row, column, squares[row][column]);
				}
			}
		}
	}

	/**
	 * @return true if the Sudoku constraints are satisfied, false otherwise (cells were filled in wrongly).
	 */
	public boolean isValid () {
		// The JRE specification requires to initialize arrays' values to default values (0), so I'm not going to do it.
		int[] cellHits = new int[CELL_MAX_VALID - CELL_MIN_VALID + 2];

		for (int row = 0; row < squares.length; row++) {
			for (int col = 0; col < squares[row].length; col++) {
				cellHits[squares[row][col]]++;
			}

			if (valueHitExceedsOne(cellHits))
				return false;

			cellHits = new int[cellHits.length];
		}

		cellHits = new int[cellHits.length];
		for (int col = 0; col < squares[0].length; col++) {
			for (int row = 0; row < squares.length; row++) {
				cellHits[squares[row][col]]++;
			}

			if (valueHitExceedsOne(cellHits))
				return false;

			cellHits = new int[cellHits.length];
		}

		cellHits = new int[cellHits.length];
		for (int block = 0; block < Math.pow(blockSize, 2); block++) {
			for (int cell = 0; cell < Math.pow(blockSize, 2); cell++) {
				cellHits[getCellByBlock(block, cell).value]++;
			}

			if (valueHitExceedsOne(cellHits))
				return false;

			cellHits = new int[cellHits.length];
		}

		return true;
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
		if (blockSize > BLOCK_SIZE_MAX) {
			throw new IllegalArgumentException(
					String.format(
							"blockSize argument (%d) greater than max allowed (%d)",
							blockSize, BLOCK_SIZE_MAX
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

	public SudokuCell getCellByBlock (int block, int i) {
		int row = Math.floorDiv(block, blockSize) * blockSize + Math.floorDiv(i, blockSize),
			column = (block % blockSize) * blockSize + (i % blockSize);

		return new SudokuCell(
				row,
				column,
				squares[row][column]
		);
	}

	@Override
	public boolean equals (Object another) {
		final Sudoku a;

		if (!(another instanceof Sudoku)) {
			return false;
		}
		if (this == another) {
			return true;
		}

		a = (Sudoku) another;

		for (int row = 0; row < squares.length; row++) {
			if (!Arrays.equals(squares[row], a.squares[row])) {
				return false;
			}
		}

		return true;
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
				if (squares[row][column] == CELL_EMPTY) {
					b.append(CELL_REP_EMPTY);
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

		public Formula getProblem () {
			Formula[] steps = new Formula[5];
			Formula result = new Formula();

			result = loadFromGrid(result);
			result = atMostOneDigitPerSquare(result);
			result = exactlyOncePerRow(result);
			result = exactlyOncePerColumn(result);
			result = exactlyOncePerBlock(result);

//			for (int i = 0; i < steps.length; i++) {
//				steps[i] = new Formula();
//			}
//
//			steps[0] = loadFromGrid(steps[0]);
//			steps[1] = atMostOneDigitPerSquare(steps[1]);
//			steps[2] = exactlyOncePerRow(steps[2]);
//			steps[3] = exactlyOncePerColumn(steps[3]);
//			steps[4] = exactlyOncePerBlock(steps[4]);
//
//			for (Formula step: steps) {
//				result = result.and(step);
//			}

			return result;
		}

		private Formula loadFromGrid (Formula previous) {
			//Solution must be consistent with the starting grid.
			for (int row = 0; row < occupies.length; row++) {
				for (int column = 0; column < occupies[row].length; column++) {
					for (int value = 0; value < occupies[row][column].length; value++) {
						if (occupies[row][column][value] != null) {
							previous = previous.addClause(
									new Clause(PositiveLiteral.make(occupies[row][column][value]))
							);
						}
					}
				}
			}

			return previous;
		}

		private Formula atMostOneDigitPerSquare (Formula previous) {
			//At most one digit per square
			for (int row = 0; row < squares.length; row++) {
				for (int column = 0; column < squares[row].length; column++) {

					for (int first = CELL_MIN_VALID; first <= CELL_MAX_VALID; first++) {
						for (int second = first + 1; second <= CELL_MAX_VALID; second++) {
							previous = previous.addClause(
									new Clause(
											NegatedLiteral.make(variableFactory(row, column, first)),
											NegatedLiteral.make(variableFactory(row, column, second))
									)
							);
						}
					}

				}
			}

			return previous;
		}

		private Formula exactlyOncePerRow (Formula f) {
			Clause atLeastOnce, atMostOnce;

			//The code below will guarantee that a certain digit k will appear at least once in every row r.
			for (int row = 0; row < squares.length; row++) {
				for (int value = CELL_MIN_VALID; value <= CELL_MAX_VALID; value++) {
					atLeastOnce = new Clause();

					for (int column = 0; column < squares[row].length; column++) {
						atLeastOnce = atLeastOnce.add(
								PositiveLiteral.make(
										variableFactory(row, column, value)
								)
						);
					}

					f = f.addClause(atLeastOnce);
				}
			}

			//The code below will guarantee that every valid digit k does not appear more than once in a given row r.
			for (int row = 0; row < squares.length; row++) {
				for (int value = CELL_MIN_VALID; value <= CELL_MAX_VALID; value++) {
					for (int firstCol = 0; firstCol < squares[row].length; firstCol++) {
						for (int secondCol = firstCol + 1; secondCol < squares[row].length; secondCol++) {
							atMostOnce = new Clause()
									.add(NegatedLiteral.make(variableFactory(row, firstCol, value)))
									.add(NegatedLiteral.make(variableFactory(row, secondCol, value)));
							f = f.addClause(atMostOnce);
						}
					}
				}
			}

			return f;
		}

		private Formula exactlyOncePerColumn (Formula f) {
			Clause atLeastOnce, atMostOnce;

			/* The code below guarantees that each valid value k appear at least once in every column c.
			The number of columns to iterate on is based on the number of "columns" or cells contained within the
			first row (see the condition of the for loop below). */
			for (int column = 0; column < squares[0].length; column++) {
				for (int value = CELL_MIN_VALID; value <= CELL_MAX_VALID; value++) {
					atLeastOnce = new Clause();

					for (int row = 0; row < squares.length; row++) {
						atLeastOnce = atLeastOnce.add(
								PositiveLiteral.make(
										variableFactory(row, column, value)
								)
						);
					}

					f = f.addClause(atLeastOnce);
				}
			}

			/* The code below guarantees that each valid value k does not appear more than once in every column c.
			The number of columns to iterate on is based on the number of "columns" or cells contained within the
			first row (see the condition of the for loop below). */
			for (int column = 0; column < squares[0].length; column++) {
				for (int value = CELL_MIN_VALID; value <= CELL_MAX_VALID; value++) {
					for (int firstRow = 0; firstRow < squares.length; firstRow++) {
						for (int secondRow = firstRow + 1; secondRow < squares.length; secondRow++) {
							atMostOnce = new Clause()
									.add(NegatedLiteral.make(variableFactory(firstRow, column, value)))
									.add(NegatedLiteral.make(variableFactory(secondRow, column, value)));
							f = f.addClause(atMostOnce);
						}
					}
				}
			}

			return f;
		}

		private Formula exactlyOncePerBlock (Formula f) {
			Clause atLeastOnce, atMostOnce;
			SudokuCell cell;
			SudokuCell first, second;

			//The code below guarantees that each value k is present at least once in every block.
			for (int block = 0; block < Math.pow(blockSize, 2); block++) { //For every block:
				for (int value = CELL_MIN_VALID; value <= CELL_MAX_VALID; value++) {
					atLeastOnce = new Clause();

					//For every cell in block block:
					for (int cellIndex = 0; cellIndex < Math.pow(blockSize, 2); cellIndex++) {
						cell = getCellByBlock(block, cellIndex);
						atLeastOnce = atLeastOnce.add(
								PositiveLiteral.make(variableFactory(cell.row, cell.column, value))
						);
					}

					f = f.addClause(atLeastOnce);
				}
			}

			//The code below guarantees that each value k is present at most once in every block.
			for (int block = 0; block < Math.pow(blockSize, 2); block++) { //For every block:
				for (int value = CELL_MIN_VALID; value <= CELL_MAX_VALID; value++) {
					for (int firstCell = 0; firstCell < Math.pow(blockSize, 2); firstCell++) {
						for (int secondCell = firstCell + 1; secondCell < Math.pow(blockSize, 2); secondCell++) {
							first = this.getCellByBlock(block, firstCell);
							second = this.getCellByBlock(block, secondCell);

							atMostOnce = new Clause()
									.add(NegatedLiteral.make(variableFactory(first.row, first.column, value)))
									.add(NegatedLiteral.make(variableFactory(second.row, second.column, value)));
							f = f.addClause(atMostOnce);
						}
					}
				}
			}

			return f;
		}

		/**
		 *
		 * @param block the block for which we want to obtain a cell.
		 * @param i the index of the ith cell we want to retrieve.
		 * @return a SudokuCell with its {@code value} attribute set to null.
		 */
		private SudokuCell getCellByBlock (int block, int i) {
			return new SudokuCell(
					Math.floorDiv(block, blockSize) * blockSize + Math.floorDiv(i, blockSize),
					(block % blockSize) * blockSize + (i % blockSize)
			);
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
				for (int value = CELL_MIN_VALID; value <= CELL_MAX_VALID; value++) {
					if (solution.squares[row][column] == CELL_EMPTY && e.get(variableFactory(row, column, value)) == TRUE) {
						solution.squares[row][column] = value;
					}
				}
			}
		}

		return solution;
	}

	private Variable variableFactory (int row, int column, int value) {
		if (value < CELL_MIN || value > CELL_MAX_VALID) {
			throw new IllegalStateException(
					String.format(
							"Value (%d) must be comprised between %d and %d",
							value, CELL_MIN, CELL_MAX_VALID
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
						SEP_OCCUPIES,
						String.valueOf(row),
						String.valueOf(column),
						String.valueOf(value)
				) + "\n"
		);
	}

	private static int[] stringToIntCellArray (String row) throws ParseException {
		final int[] result = new int[row.length()];
		int cellValue;

		for (int i = 0; i < row.length(); i++) {
			if (String.valueOf(row.charAt(i)).contentEquals(CELL_REP_EMPTY)) {
				result[i] = CELL_EMPTY;
			} else {
				cellValue = Integer.valueOf("" + row.subSequence(i, i + 1));

				if (cellValue <= CELL_MIN || cellValue > CELL_MAX_VALID) {
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

	private boolean valueHitExceedsOne (int[] values) {
		for (int i = 0; i < values.length; i++) {
			if (values[i] > 1)
				return true;
		}

		return false;
	}

	public static class SudokuCell {

		public final Integer row, column, value;

		public SudokuCell (int row, int column) {
			this.row = row;
			this.column = column;
			this.value = null;
		}

		public SudokuCell (int row, int column, int value) {
			this.row = row;
			this.column = column;
			this.value = value;
		}

	}

}
