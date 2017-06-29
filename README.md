# Sudoku Solver with SAT
This repository is an implementation of the MIT OCW 6.005 course problem assignment 4
(2011 version, now outdated and archived [here](http://dspace.mit.edu/handle/1721.1/106923)). Visit
[this page](https://ocw.mit.edu/courses/electrical-engineering-and-computer-science/6-005-software-construction-spring-2016/)
for the currently active version of the course.

The Sudoku Solver with SAT solves Sudoku puzzles by representing the game constraints with propositional formulas in [CNF 
(Conjunctive Normal Form)](https://en.wikipedia.org/wiki/Conjunctive_normal_form). These formulas  are then solved by an implementation of
[SAT](https://en.wikipedia.org/wiki/Boolean_satisfiability_problem), which is constructed following
the [DPLL algorithm](https://en.wikipedia.org/wiki/DPLL_algorithm). More details about the problem assignment can be
found in the `Instructions.pdf` file in the project root directory.

## Basic usage
This implementation <b>still doesn't work</b> as intended. The `src/sudoku/Main.java` launcher outputs a partial solution
to the various Sudoku grids, but only when some problem constraints are weakened. Some work is being done to update
the solution and will be uploaded to this repository when ready.