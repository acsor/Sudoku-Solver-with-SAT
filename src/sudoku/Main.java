package sudoku;

import sat.JeremySATSolver;
import sat.env.Environment;
import sat.formula.Formula;

import java.io.IOException;

public class Main {

	/*
	 * Uncomment line(s) below to sudoku.test.test your implementation!
	 */
	public static void main (String[] args) {
//		timedSolve (new Sudoku(2, new int[][] {
//				new int[] {0, 1, 0, 4},
//				new int[] {0, 0, 0, 0},
//				new int[] {2, 0, 3, 0},
//				new int[] {0, 0, 0, 0},
//		}));
        timedSolveFromFile(3, "samples/sudoku_easy.txt");
//        timedSolveFromFile(3, "samples/sudoku_hard.txt");
	}

	/**
	 * Solve a puzzle and display the solution and the time it took.
	 *
	 * @param sudoku
	 */
	private static void timedSolve (Sudoku sudoku) {
		long started = System.nanoTime();
		long time, timeTaken;

		System.out.println("Creating SAT formula...");
		Formula f = sudoku.getProblem();

		System.out.println("Solving...");
		Environment e = JeremySATSolver.solve(f);

		System.out.println("Interpreting solution...");
		Sudoku solution = sudoku.interpretSolution(e);

		System.out.println("Solution is: \n" + solution);

		time = System.nanoTime();
		timeTaken = (time - started);
		System.out.println("Time: " + timeTaken / Math.pow(10, 6) + "ms");
	}

	/**
	 * Solve a puzzle loaded from a file and display the solution and the time it took.
	 *
	 * @param dim      dimension of puzzle
	 * @param filename name of puzzle file to load
	 */
	private static void timedSolveFromFile (int dim, String filename) {
		try {
			timedSolve(Sudoku.fromFile(dim, filename));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

}
