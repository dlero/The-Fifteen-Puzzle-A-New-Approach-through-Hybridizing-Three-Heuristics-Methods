
# The Fifteen Puzzle—A New Approach through Hybridizing Three Heuristics Methods

The Fifteen Puzzle is a classical problem that has intrigued mathematics enthusiasts for centuries due to its enormous state space, which contains around 10^13 states that require exploration. In this study, the Bidirectional A* (BA*) search algorithm utilizing three heuristics - Manhattan distance (MD), linear conflict (LC), and walking distance (WD) - has been employed to solve the Fifteen Puzzle problem.

### Authors: 
Dler O. Hasan, Aso M. Aladdin, Hardi Sabah Talabani, Tarik Ahmed Rashid and Seyedali Mirjalili

### Implemented by:
Dler O. Hasan

### Cite as:
Hasan, D.O.; Aladdin, A.M.; Talabani, H.S.; Rashid, T.A.; Mirjalili, S. The Fifteen Puzzle—A New Approach through Hybridizing Three Heuristics Methods. Computers 2023, 12, 11. https://doi.org/10.3390/computers12010011

## The source code includes five files:
- FifteenPuzzle.java: This class contains the code for the BA* algorithm with all heuristics, which finds the solution path for the puzzle instance.

- theCommonMethods.java: This class contains methods to generate the goal state and verify whether the initial state is solvable.

- Main.java: Class with a main method and a simple test for the algorithm implementation

- wdcostsbr; wdcoststl:The last two files each contain 24964 distinct boards for the Walking Distance heuristic, with one for the goal state featuring a blank at the bottom right and the other for the goal state featuring a blank at the top left corner.
