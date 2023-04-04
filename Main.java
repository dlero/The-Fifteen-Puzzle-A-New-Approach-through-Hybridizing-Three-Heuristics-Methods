/**
 * The Fifteen Puzzle—A New Approach through Hybridizing Three Heuristics Methods
 * Authors: Dler O. Hasan, Aso M. Aladdin, Hardi Sabah Talabani, Tarik Ahmed Rashid and Seyedali Mirjalili
 * It is implemented by Dler O. Hasan
 * Cite as: Hasan, D.O.; Aladdin, A.M.; Talabani, H.S.; Rashid, T.A.; Mirjalili, S. The Fifteen Puzzle—A New Approach through Hybridizing Three Heuristics Methods. Computers 2023, 12, 11. https://doi.org/10.3390/computers12010011
 */
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static theCommonMethods theCommonMethods = new theCommonMethods();
    private static ArrayList<Integer> BoardCellsNumbers = new ArrayList<Integer>();
    private static FifteenPuzzle auto = new FifteenPuzzle();
    static int numRow = 4;// = 4;
    static int numColumn = 4;// = 4;
    private static Integer[] theGoal;
    private static List<FifteenPuzzle> solution;
    private static String blankPosition = "topLeft"; //bottomRight

    public static void main(String[] args) {
        theGoal = new Integer[numRow * numColumn];
        theGoal = theCommonMethods.theGoalBoard(numRow, numColumn, blankPosition);
        theCommonMethods.FillAndShuffleCells(BoardCellsNumbers, numRow, numColumn, blankPosition);
        FifteenPuzzle.numRow = numRow;
        FifteenPuzzle.numCol = numColumn;
        auto.engine(BoardCellsNumbers, blankPosition);

        solution = FifteenPuzzle.solution;
        int steps = solution.size() - 1;
        System.out.println("Number of Moves: " + steps);
        for (int i = 0; i < theGoal.length; i++) {
            System.out.print(BoardCellsNumbers.get(i) + ", ");
        }
    }
}