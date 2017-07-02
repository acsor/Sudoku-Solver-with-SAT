package sudoku;

import sat.SATSolver;
import sat.env.Environment;
import sat.formula.Formula;

import java.io.IOException;

public class Main {

	/*
	 * Uncomment line(s) below to sudoku.test.test your implementation!
	 */
	public static void main (String[] args) {
		final String dir = "samples/";
		final String[]	samples9x9 = {
				"sudoku_easy.txt",
				"sudoku_easy2.txt",
				"sudoku_hard.txt",
				"sudoku_hard2.txt",
				"sudoku_hard3.txt",
				"sudoku_hard4.txt",
		};

//		timedSolve (new Sudoku(2, new int[][] {
//				new int[] {0, 1, 0, 4},
//				new int[] {0, 2, 0, 0},
//				new int[] {2, 0, 3, 1},
//				new int[] {0, 0, 4, 0},
//		}));

		for (String sample: samples9x9) {
			timedSolveFromFile(3, dir + sample);
		}
	}

	/**
	 * Solve a puzzle and display the solution and the time it took.
	 *
	 * @param sudoku
	 */
	private static void timedSolve (Sudoku sudoku) {
		long started = System.nanoTime(), timeTaken;
		Sudoku solution;
		Formula f;
		Environment e;

		if (!sudoku.isValid()) {
			System.err.println("The selected Sudoku is invalid. Aborting.");
			return;
		}

		System.out.println("Creating SAT formula...");
		f = sudoku.getProblem();

		System.out.println("Solving...");
		e = SATSolver.solve(f);

		if (e != null) {
			System.out.println("Interpreting solution...");
			solution = sudoku.interpretSolution(e);

			if (!solution.isValid()) {
				System.err.println("The solver tried to come up with a solution, but it was invalid:");
			}

			System.out.println("Solution is: \n" + solution);

			timeTaken = (System.nanoTime() - started);
			System.out.format("Time: %.2fms.\n", timeTaken / Math.pow(10, 6));
		} else {
			System.err.println("Failed solving selected Sudoku.");
		}

		System.out.println("\n");
	}

	/**
	 * Solve a puzzle loaded from a file and display the solution and the time it took.
	 *
	 * @param dim      dimension of puzzle
	 * @param filename name of puzzle file to load
	 */
	private static void timedSolveFromFile (int dim, String filename) {
		System.out.format("Solving '%s'.\n", filename);

		try {
			timedSolve(Sudoku.fromFile(dim, filename));
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
	}

}
