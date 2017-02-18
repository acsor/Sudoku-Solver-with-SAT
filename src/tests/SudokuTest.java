package tests;

import org.junit.Test;
import org.testng.Assert;
import sudoku.ParseException;
import sudoku.Sudoku;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Scanner;

public class SudokuTest {

	private static final String DIR_SAMPLES = "samples/";

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

		cells[4][0] = -3;
		cells[0][4] = 12;

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

		// System.out.println(s);
		Assert.assertEquals(
				s.toString(),
				expectedResult
		);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFromFileInvalidBlockSize () throws IOException, ParseException {
		final String fileName = "samples/sudoku_evil.txt";
		Sudoku.fromFile(Sudoku.VAL_MAX_BLOCK_SIZE + 1, fileName);
	}

	@Test
	public void testFromFiles () throws IOException, ParseException {
		final File samplesDir = new File(DIR_SAMPLES);
		final File[] sampleFiles;
		final FilenameFilter filter = (dir, name) -> !name.contentEquals("sudoku_4x4.txt");
		Sudoku s;

		if (samplesDir.isDirectory()) {
			sampleFiles = samplesDir.listFiles(filter);

			if (sampleFiles != null) {
				for (File f: sampleFiles) {
					s = Sudoku.fromFile(3, f.getPath());

					// System.out.println(s);
					// System.out.println(readFile(f));

					Assert.assertEquals(
							s.toString(),
							readFile(f)
					);
				}
			}
		}
	}

	private String readFile (File file) throws FileNotFoundException {
		Scanner in = null;
		final StringBuilder b = new StringBuilder();

		try {
			in = new Scanner(file);

			while (in.hasNextLine()) {
				b.append(in.nextLine()).append("\n");
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}

		return b.toString();
	}

}