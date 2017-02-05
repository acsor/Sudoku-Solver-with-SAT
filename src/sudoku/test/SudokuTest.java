package sudoku.test;

import org.junit.Test;
import org.testng.Assert;
import sudoku.Sudoku;

public class SudokuTest {

	/*
    * make sure assertions are turned on!
    * we don't want to run sudoku.test.test cases without assertions too.
    * see the handout to find out how to turn them on.
    */
    @Test(expected = AssertionError.class)
    public void testAssertionsEnabled() {
        assert false;
    }

    @Test(expected = IllegalArgumentException.class)
	public void negativeBlockSize () {
		new Sudoku(-1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testWrongRowSize () {
		final int blockSize = 3;
		final int size = (int) Math.pow(blockSize, 2);
		final int[][] cells = new int[size][];

		for (int i = 0; i < cells.length; i++) {
			cells[i] = new int[size - 1];
		}

		new Sudoku(blockSize, cells);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testWrongColumnSize () {
		final int blockSize = 3;
		final int size = (int) Math.pow(blockSize, 2);
		final int[][] cells = new int[size - 1][];

		for (int i = 0; i < cells.length; i++) {
			cells[i] = new int[size - 1];
		}

		new Sudoku(blockSize, cells);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidCellValues () {
		final int blockSize = 3;
		final int size = (int) Math.pow(blockSize, 2);
		final int[][] cells = new int[size][];

		for (int i = 0; i < cells.length; i++) {
			cells[i] = new int[size];
		}

		cells[0][4] = 12;
		cells[4][0] = -3;

		new Sudoku(blockSize, cells);
	}

	@Test
	public void testToString () {
		final int[][] cells = {
				{1, 4, 3, 0},
				{3, 0, 4, 1},
				{0, 1, 4, 3},
				{0, 3, 0, 2}
		};
		final String expectedResult =
						"143.\n" +
						"3.41\n" +
						".143\n" +
						".3.2\n";
		final Sudoku s = new Sudoku(2, cells);

		System.out.println(s);
		Assert.assertEquals(
				s.toString(),
				expectedResult
		);
	}

}