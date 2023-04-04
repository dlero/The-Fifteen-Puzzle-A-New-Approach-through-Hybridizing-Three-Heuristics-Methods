import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.*;

/**
 * The Fifteen Puzzle—A New Approach through Hybridizing Three Heuristics Methods
 * Authors: Dler O. Hasan, Aso M. Aladdin, Hardi Sabah Talabani, Tarik Ahmed Rashid and Seyedali Mirjalili
 * It is implemented by Dler O. Hasan
 * Cite as: Hasan, D.O.; Aladdin, A.M.; Talabani, H.S.; Rashid, T.A.; Mirjalili, S. The Fifteen Puzzle—A New Approach through Hybridizing Three Heuristics Methods. Computers 2023, 12, 11. https://doi.org/10.3390/computers12010011
 *
 * To see the number of moves that each heuristic guesses for each puzzle instance, uncomment all the commented code below.
 */
public class FifteenPuzzle {
    public static int numRow;
    public static int numCol;
    public int[][] theBoard;
    private int spaceXDir;
    private int spaceYDir;
    public static String blankAt;
    static List<FifteenPuzzle> solution;
    public static int[][] theGoal;
    HashMap<FifteenPuzzle, Integer> depthFromStartState = new HashMap<FifteenPuzzle, Integer>();
    HashMap<FifteenPuzzle, Integer> depthFromGoalState = new HashMap<FifteenPuzzle, Integer>();
    public static int[][] theProblemGoal;
    static HashMap<FifteenPuzzle, FifteenPuzzle> keyValueStatesFSS = new HashMap<FifteenPuzzle, FifteenPuzzle>();
    static HashMap<FifteenPuzzle, FifteenPuzzle> keyValueStatesFGS = new HashMap<FifteenPuzzle, FifteenPuzzle>();
    PriorityQueue<objectOrder> visitStatesFSS = new PriorityQueue<objectOrder>();
    PriorityQueue<objectOrder> visitStatesFGS = new PriorityQueue<objectOrder>();
    LinkedList<FifteenPuzzle> theSolution = new LinkedList<FifteenPuzzle>();
    static int iterator = 0;
    static int iterator2 = 0;
    private double totalHeuristicValues;
    long startTime;
    int isTwoWay = 0;//20000000;
    boolean firstStepOfCycle = true;

    double CalculateHeuristicFSS(FifteenPuzzle object, String tempBlankAt) {
        if (tempBlankAt.equals("topLeft")) {
            return object.wdTopLeftFromSS()
                    + object.linearHorizontalConflictFromS() + object.linearVerticalConflictFromS()
                    + (double) object.calculateManhattanDistanceBlankAtTopLeft() / 3;
        } else {
            return
                    object.wdBottomRightFromSS()
                            + object.linearHorizontalConflictFromS() + object.linearVerticalConflictFromS()
                            + (double) object.calculateManhattanDistance() / 3;
        }
    }

    double CalculateHeuristicFGS(FifteenPuzzle object) {
        if (blankAt.equals("topLeft")) {
            return
                    (double) (object.walkingDistanceVRFromG() + object.walkingDistanceHRFromG()) / 1.1 //1.232
                            + object.linearHorizontalConflictFromG() + object.linearVerticalConflictFromG()
                            + (double) object.calculateManhattanDistance2() / 3;
        } else {
            return
                    (double) (object.walkingDistanceVRFromG() + object.walkingDistanceHRFromG()) / 1.1 //1.232
                            + object.linearHorizontalConflictFromG() + object.linearVerticalConflictFromG()
                            + (double) object.calculateManhattanDistance2() / 3;
        }
    }

    public void ShowState2D(FifteenPuzzle object) {
        String ss = "";
        for (int x = 0; x < numRow; x++) {
            for (int y = 0; y < numCol; y++) {
                ss += object.theBoard[x][y] + "  ";
            }
            System.out.println(ss);
            ss = "";
        }
    }

    DecimalFormat df = new DecimalFormat("#.#");

    public void ShowHeuristicValues(double totalHeuristicV, int depth, int wdFromStart, int wdFromGoal, int LHC, int LVC, double MD, String direction) {
        System.out.println("\nTotal Heuristic Value: " + Double.valueOf(df.format(totalHeuristicV)) + " Depth: " + depth);
        System.out.println(direction +
                "WDFrom GOAL: " + wdFromGoal + " - " +
                "WDFrom Start: " + wdFromStart + " - " +
                "LHC:" + LHC + " - " +
                "LVC:" + LVC + " - " +
                "MD:" + Double.valueOf(df.format(MD)) + " - "
        );
    }

    public void showTotalNumberOfNodes(int GenStatesFromSS, int ExpStatesFromSS, int GenStatesFromGS, int ExpStatesFromGS, long ElapsedTime) {
        System.out.println("Generated States From SS: " + GenStatesFromSS + " - Expanded States From SS: " + ExpStatesFromSS);
        System.out.println("Generated States From GS: " + GenStatesFromGS + " - Expanded States From GS: " + ExpStatesFromGS);
        System.out.println("Total States: " + (GenStatesFromSS + GenStatesFromGS));
        System.out.println("Total State Expansion: " + (ExpStatesFromSS + ExpStatesFromGS));
        System.out.println("Time: " + (double) ElapsedTime / 1000 + " sec");
    }

    //---------------------------------------------------------A* Algorithm-----------------------------------------------------
    public List<FifteenPuzzle> AStarAlgorithm() {
        DecimalFormat df = new DecimalFormat("#.#");
        startTime = System.currentTimeMillis();
        if (iterator == 0) {
            depthFromStartState.put(this, 0);
            keyValueStatesFSS.put(this, null);
            double f_score = CalculateHeuristicFSS(this, blankAt);
            this.totalHeuristicValues = f_score;
            visitStatesFSS.add(new objectOrder(f_score, this));
        }

        while (!visitStatesFSS.isEmpty()) {
            FifteenPuzzle selectFromQueue = visitStatesFSS.remove().objReturn();

            if (selectFromQueue.isSolved()) {
                long stopTime = System.currentTimeMillis();
                long elapsedTime = stopTime - startTime;
                FifteenPuzzle backtrace = selectFromQueue;
                while (backtrace != null) {
//                    ShowHeuristicValues(CalculateHeuristicFSS(backtrace, blankAt), depthFromStartState.get(backtrace),
//                            blankAt.equals("topLeft") ? backtrace.wdTopLeftFromSS() : backtrace.wdBottomRightFromSS(), 0,
//                            blankAt.equals("topLeft") ? 0 : backtrace.linearHorizontalConflictFromS(), blankAt.equals("topLeft") ? 0 : backtrace.linearVerticalConflictFromS(),
//                            blankAt.equals("topLeft") ? (double) backtrace.calculateManhattanDistanceBlankAtTopLeft() / 3 : (double) backtrace.calculateManhattanDistance() / 3, "From Start Only: ");
//                    ShowState2D(backtrace);
                    theSolution.addFirst(backtrace);
                    backtrace = keyValueStatesFSS.get(backtrace);
                }
                showTotalNumberOfNodes(keyValueStatesFSS.size(), (keyValueStatesFSS.size() - visitStatesFSS.size()), keyValueStatesFGS.size(), (keyValueStatesFGS.size() - visitStatesFGS.size()), elapsedTime);
                return theSolution;
            }

            for (FifteenPuzzle selectObject : selectFromQueue.holdingAllPossibleMoves()) {
                if (!keyValueStatesFSS.containsKey(selectObject)) {
                    int g_score = depthFromStartState.get(selectFromQueue) + 1;
                    double f_score = g_score + CalculateHeuristicFSS(selectObject, blankAt);
//                    System.out.println("Dleroooooooo " + f_score);
                    selectObject.totalHeuristicValues = f_score;
                    keyValueStatesFSS.put(selectObject, selectFromQueue);
                    depthFromStartState.put(selectObject, g_score);
                    visitStatesFSS.add(new objectOrder(f_score, selectObject)); // calculate heuristic from "selectObject" to the Goal

                    if (keyValueStatesFGS.containsKey(selectObject)) {
                        long stopTime = System.currentTimeMillis();
                        long elapsedTime = stopTime - startTime;
                        FifteenPuzzle backtraceFSS = selectObject;
                        while (backtraceFSS != null) {
//                            ShowHeuristicValues(CalculateHeuristicFSS(backtraceFSS, blankAt), depthFromStartState.get(backtraceFSS),
//                                    blankAt.equals("topLeft") ? backtraceFSS.wdTopLeftFromSS() : backtraceFSS.wdBottomRightFromSS(), 0,
//                                    backtraceFSS.linearHorizontalConflictFromS(), backtraceFSS.linearVerticalConflictFromS(),
//                                    blankAt.equals("topLeft") ? (double) backtraceFSS.calculateManhattanDistanceBlankAtTopLeft() / 3 : (double) backtraceFSS.calculateManhattanDistance() / 3, "From Start To Goal: ");

                            theSolution.addFirst(backtraceFSS);
//                            ShowState2D(backtraceFSS);
                            backtraceFSS = keyValueStatesFSS.get(backtraceFSS);
                        }
                        boolean exceptFirstObj = false;
                        FifteenPuzzle backtraceFGS = selectObject;
                        while (backtraceFGS != null) {
//                            ShowHeuristicValues(CalculateHeuristicFGS(backtraceFGS), depthFromGoalState.get(backtraceFGS), 0,
//                                    backtraceFGS.walkingDistanceHRFromG() + backtraceFGS.walkingDistanceVRFromG(),
//                                    backtraceFGS.linearHorizontalConflictFromG(), backtraceFGS.linearVerticalConflictFromG(),
//                                    (double) backtraceFGS.calculateManhattanDistance2() / 3, "A-From Goal To Start: ");

                            if (exceptFirstObj) {
                                theSolution.addLast(backtraceFGS);
                            }
                            exceptFirstObj = true;
//                            ShowState2D(backtraceFGS);
                            backtraceFGS = keyValueStatesFGS.get(backtraceFGS);
                        }
                        showTotalNumberOfNodes(keyValueStatesFSS.size(), (keyValueStatesFSS.size() - visitStatesFSS.size()), keyValueStatesFGS.size(), (keyValueStatesFGS.size() - visitStatesFGS.size()), elapsedTime);
                        return theSolution;
                    }
//                    }

                }
            }

            iterator++;
//----------------------------------From Goal to Problem State----------------------------------------
            //the forward search continue until 75,000 states are expanded
            if (firstStepOfCycle ? iterator % 75000 == isTwoWay : iterator % 15000 == isTwoWay) {
                if (iterator2 == 0) {
                    //Make the goal as the start point
                    FifteenPuzzle twistTheBoard = new FifteenPuzzle();
                    if (blankAt.equals("topLeft")) {
                        int theGoalVar = 0;
                        for (int i = 0; i < numRow; i++) {
                            for (int j = 0; j < numCol; j++) {
                                if (i == 0 && j == 0) {
                                    twistTheBoard.theBoard[i][j] = 0;
                                    twistTheBoard.spaceXDir = i;
                                    twistTheBoard.spaceYDir = j;

                                } else
                                    twistTheBoard.theBoard[i][j] = theGoalVar;
                                theGoalVar++;
                            }
                        }
                    } else {
                        int theGoalVar = 1;
                        for (int i = 0; i < numRow; i++) {
                            for (int j = 0; j < numCol; j++) {
                                if (i == numRow - 1 && j == numCol - 1) {
                                    theGoalVar = 0;
                                    twistTheBoard.theBoard[i][j] = 0;
                                    twistTheBoard.spaceXDir = i;
                                    twistTheBoard.spaceYDir = j;

                                } else
                                    twistTheBoard.theBoard[i][j] = theGoalVar;
                                theGoalVar++;
                            }
                        }
                    }

                    depthFromGoalState.put(twistTheBoard, 0);
                    keyValueStatesFGS.put(twistTheBoard, null);
                    visitStatesFGS.add(new objectOrder(CalculateHeuristicFGS(twistTheBoard), twistTheBoard));

                }

                while (!visitStatesFGS.isEmpty()) {

                    FifteenPuzzle selectFromQueue2 = visitStatesFGS.remove().objReturn();

                    if (selectFromQueue2.countMisplacedSquares2() == 0) {
                        long stopTime = System.currentTimeMillis();
                        long elapsedTime = stopTime - startTime;
                        FifteenPuzzle backtrace = selectFromQueue2;
                        while (backtrace != null) {
//                            ShowHeuristicValues(CalculateHeuristicFGS(backtrace), depthFromGoalState.get(backtrace), 0,
//                                    backtrace.walkingDistanceHRFromG() + backtrace.walkingDistanceVRFromG(),
//                                    backtrace.linearHorizontalConflictFromG(), backtrace.linearVerticalConflictFromG(),
//                                    (double) backtrace.calculateManhattanDistance2() / 3, "From Goal Only: ");

                            theSolution.addLast(backtrace);
//                            ShowState2D(backtrace);
                            backtrace = keyValueStatesFGS.get(backtrace);
                        }
                        showTotalNumberOfNodes(keyValueStatesFSS.size(), (keyValueStatesFSS.size() - visitStatesFSS.size()), keyValueStatesFGS.size(), (keyValueStatesFGS.size() - visitStatesFGS.size()), elapsedTime);
                        return theSolution;
                    }

                    for (FifteenPuzzle selectObject2 : selectFromQueue2.holdingAllPossibleMoves()) {
                        if (!keyValueStatesFGS.containsKey(selectObject2)) {
                            keyValueStatesFGS.put(selectObject2, selectFromQueue2);
                            depthFromGoalState.put(selectObject2, depthFromGoalState.get(selectFromQueue2) + 1);

                            double g_score = depthFromGoalState.get(selectFromQueue2) + 1;
                            double f_score = g_score + CalculateHeuristicFGS(selectObject2);
                            visitStatesFGS.add(new objectOrder(f_score, selectObject2)); // calculate heuristic from "selectObject" to the Goal

                            if (keyValueStatesFSS.containsKey(selectObject2)) {
                                long stopTime = System.currentTimeMillis();
                                long elapsedTime = stopTime - startTime;
                                FifteenPuzzle backtrace = selectObject2;
                                while (backtrace != null) {
//                                    ShowHeuristicValues(CalculateHeuristicFSS(backtrace, blankAt), depthFromStartState.get(backtrace),
//                                            blankAt.equals("topLeft") ? backtrace.wdTopLeftFromSS() : backtrace.wdBottomRightFromSS(), 0,
//                                            backtrace.linearHorizontalConflictFromS(), backtrace.linearVerticalConflictFromS(),
//                                            blankAt.equals("topLeft") ? (double) backtrace.calculateManhattanDistanceBlankAtTopLeft() / 3 : (double) backtrace.calculateManhattanDistance() / 3, "B-From Start To Goal: ");

                                    theSolution.addFirst(backtrace);
//                                    ShowState2D(backtrace);
                                    backtrace = keyValueStatesFSS.get(backtrace);
                                }
                                boolean exceptFirstObj = false;
                                FifteenPuzzle backtrace2 = selectObject2;
                                while (backtrace2 != null) {
//                                    ShowHeuristicValues(CalculateHeuristicFGS(backtrace2), depthFromGoalState.get(backtrace2), 0,
//                                            backtrace2.walkingDistanceHRFromG() + backtrace2.walkingDistanceVRFromG(),
//                                            backtrace2.linearHorizontalConflictFromG(), backtrace2.linearVerticalConflictFromG(),
//                                            (double) backtrace2.calculateManhattanDistance2() / 3, "B-From Goal To Start: ");

                                    if (exceptFirstObj) {
                                        theSolution.addLast(backtrace2);
                                    }
                                    exceptFirstObj = true;
//                                    ShowState2D(backtrace2);
                                    backtrace2 = keyValueStatesFGS.get(backtrace2);
                                }
                                showTotalNumberOfNodes(keyValueStatesFSS.size(), (keyValueStatesFSS.size() - visitStatesFSS.size()), keyValueStatesFGS.size(), (keyValueStatesFGS.size() - visitStatesFGS.size()), elapsedTime);
                                return theSolution;
                            }
                        }
                    }
                    iterator2++;
                    if (firstStepOfCycle ? iterator2 % 75000 == 0 : iterator2 % 15000 == 0) {
                        break;
                    }

                }
                firstStepOfCycle = false;
            }
        }
        return null;
    }

    private int calculateManhattanDistanceBlankAtTopLeft() {
        int SumManhattanDistance = 0;
        for (int x = 0; x < numRow; x++)
            for (int y = 0; y < numCol; y++) {
                int value = theBoard[x][y];
                if (value != 0) {
                    int xCoordinate = value / numRow; //numCol;
                    int yCoordinate = value % numCol;
                    SumManhattanDistance += Math.abs(x - xCoordinate) + Math.abs(y - yCoordinate);
                }
            }
        return SumManhattanDistance;
    }

    //    --------------------WD----------------------
    ArrayList<Character> firWDHRFromG = new ArrayList<>();
    ArrayList<Character> serWDHRFromG = new ArrayList<>();
    ArrayList<Character> thrWDHRFromG = new ArrayList<>();
    ArrayList<Character> forWDHRFromG = new ArrayList<>();
    ArrayList<ArrayList<Character>> groupWDHRFromG = new ArrayList<>();
    int zeroPositionWDHRFromG = 0;

    public int walkingDistanceHRFromG() {

        firWDHRFromG.clear();
        serWDHRFromG.clear();
        thrWDHRFromG.clear();
        forWDHRFromG.clear();
        groupWDHRFromG.clear();

        Character numToChar = 'z';
        int firstTimeZeroPos = 0;
        if (zeroPositionAtNRow <= 1) {
            for (int i = 0; i < numRow; i++) {
                for (int j = 0; j < numCol; j++) {
                    for (int x = 0; x < numRow; x++) {
                        for (int y = 0; y < numCol; y++) {
                            if (theBoard[i][j] == theProblemGoal[x][y]) {
                                if (x == 0)
                                    numToChar = 'D';
                                if (x == 1)
                                    numToChar = 'C';
                                if (x == 2)
                                    numToChar = 'B';
                                if (x == 3)
                                    numToChar = 'A';

                                if (theBoard[i][j] == 0) {
                                    numToChar = '0';
                                    zeroPositionWDHRFromG = numRow - 1 - i;

                                }

                                if (theProblemGoal[i][j] == 0) {
                                    firstTimeZeroPos = numRow - 1 - i;
                                }
                                if (i == 3)
                                    firWDHRFromG.add(numToChar);
                                if (i == 2)
                                    serWDHRFromG.add(numToChar);
                                if (i == 1)
                                    thrWDHRFromG.add(numToChar);
                                if (i == 0)
                                    forWDHRFromG.add(numToChar);
                                break;
                            }

                        }
                    }
                }
            }
        } else {
            for (int i = 0; i < numRow; i++) {
                for (int j = 0; j < numCol; j++) {
                    for (int x = 0; x < numRow; x++) {
                        for (int y = 0; y < numCol; y++) {
                            if (theBoard[i][j] == theProblemGoal[x][y]) {
                                if (x == 0)
                                    numToChar = 'A';
                                if (x == 1)
                                    numToChar = 'B';
                                if (x == 2)
                                    numToChar = 'C';
                                if (x == 3)
                                    numToChar = 'D';

                                if (theBoard[i][j] == 0) {
                                    numToChar = '0';
                                    zeroPositionWDHRFromG = i;
                                }

                                if (theProblemGoal[i][j] == 0) {
                                    firstTimeZeroPos = i;
                                }
                                if (i == 0)
                                    firWDHRFromG.add(numToChar);
                                if (i == 1)
                                    serWDHRFromG.add(numToChar);
                                if (i == 2)
                                    thrWDHRFromG.add(numToChar);
                                if (i == 3)
                                    forWDHRFromG.add(numToChar);
                                break;
                            }

                        }
                    }
                }
            }
        }

        Collections.sort(firWDHRFromG);
        Collections.sort(serWDHRFromG);
        Collections.sort(thrWDHRFromG);
        Collections.sort(forWDHRFromG);
        groupWDHRFromG.add(firWDHRFromG);
        groupWDHRFromG.add(serWDHRFromG);
        groupWDHRFromG.add(thrWDHRFromG);
        groupWDHRFromG.add(forWDHRFromG);

        Character[] chSearchFor = {'A', 'B', 'C', 'D'};
        int occurrences = 0, rowsCompleted = 0;
        for (int p = 0; p < numRow; p++) {
            occurrences = Collections.frequency(groupWDHRFromG.get(p), chSearchFor[p]);
            if (p != firstTimeZeroPos && occurrences == numCol)
                rowsCompleted++;
            else if (p == firstTimeZeroPos && occurrences == numCol - 1)
                rowsCompleted++;
            if (rowsCompleted != p + 1)
                break;
        }

        int chSearchForOC, chSearchForOCAtZeroPosition;
        int steps = 0;
        while (steps <= 100 && rowsCompleted < 4) {

            chSearchForOC = Collections.frequency(groupWDHRFromG.get(rowsCompleted), chSearchFor[rowsCompleted]);

            if (chSearchForOC == 4 && rowsCompleted != firstTimeZeroPos) {
                rowsCompleted++;
            }

            if (chSearchForOC == 3 && rowsCompleted == firstTimeZeroPos) {
                rowsCompleted++;
            }
            if (firstTimeZeroPos == 3 && rowsCompleted == 3)
                break;

            if (rowsCompleted == 4)
                break;

            chSearchForOCAtZeroPosition = Collections.frequency(groupWDHRFromG.get(zeroPositionWDHRFromG), chSearchFor[rowsCompleted]);

            //----------------------------------------------------------
            if (zeroPositionWDHRFromG > rowsCompleted && zeroPositionWDHRFromG < numRow - 1) {
                Character tempC1 = groupWDHRFromG.get(zeroPositionWDHRFromG - 1).get(numCol - 1);
                Character tempC2 = groupWDHRFromG.get(zeroPositionWDHRFromG + 1).get(0);

                if (tempC2 == chSearchFor[rowsCompleted] && chSearchForOCAtZeroPosition < 3) {
                    moveDown(groupWDHRFromG, zeroPositionWDHRFromG, tempC2);
                    zeroPositionWDHRFromG = zeroPositionWDHRFromG + 1;
                } else if (zeroPositionWDHRFromG < numRow - 2) {
                    Character tempC3;
                    tempC3 = groupWDHRFromG.get(zeroPositionWDHRFromG + 2).get(0);
                    if (tempC3 == chSearchFor[rowsCompleted]) {
                        moveDown(groupWDHRFromG, zeroPositionWDHRFromG, tempC2);
                        zeroPositionWDHRFromG = zeroPositionWDHRFromG + 1;
                    } else {
                        moveUp(groupWDHRFromG, zeroPositionWDHRFromG, tempC1);
                        zeroPositionWDHRFromG = zeroPositionWDHRFromG - 1;
                    }
                } else {
                    moveUp(groupWDHRFromG, zeroPositionWDHRFromG, tempC1);
                    zeroPositionWDHRFromG = zeroPositionWDHRFromG - 1;

                }


            } else if (zeroPositionWDHRFromG == numRow - 1) {
                Character tempC = groupWDHRFromG.get(zeroPositionWDHRFromG - 1).get(numCol - 1);
                moveUp(groupWDHRFromG, zeroPositionWDHRFromG, tempC);
                zeroPositionWDHRFromG = zeroPositionWDHRFromG - 1;
            } else if (zeroPositionWDHRFromG == rowsCompleted) {
                Character tempC = groupWDHRFromG.get(zeroPositionWDHRFromG + 1).get(0);
                moveDown(groupWDHRFromG, zeroPositionWDHRFromG, tempC);
                zeroPositionWDHRFromG = zeroPositionWDHRFromG + 1;
            }
            steps++;
        }

        return steps;
    }

    ArrayList<Character> firWDVRFromG = new ArrayList<>();
    ArrayList<Character> serWDVRFromG = new ArrayList<>();
    ArrayList<Character> thrWDVRFromG = new ArrayList<>();
    ArrayList<Character> forWDVRFromG = new ArrayList<>();
    ArrayList<ArrayList<Character>> groupWDVRFromG = new ArrayList<>();
    int zeroPositionWDVRFromG;

    public int walkingDistanceVRFromG() {
        firWDVRFromG.clear();
        serWDVRFromG.clear();
        thrWDVRFromG.clear();
        forWDVRFromG.clear();
        groupWDVRFromG.clear();
        Character numToChar = 'z';
        int firstTimeZeroPos = 0;
        if (zeroPositionAtNColumn <= 1) {
            for (int i = 0; i < numRow; i++) {
                for (int j = 0; j < numCol; j++) {
                    for (int x = 0; x < numRow; x++) {
                        for (int y = 0; y < numCol; y++) {
                            if (theBoard[j][i] == theProblemGoal[y][x]) {
                                if (x == 0)
                                    numToChar = 'D';
                                if (x == 1)
                                    numToChar = 'C';
                                if (x == 2)
                                    numToChar = 'B';
                                if (x == 3)
                                    numToChar = 'A';

                                if (theBoard[j][i] == 0) {
                                    numToChar = '0';
                                    zeroPositionWDVRFromG = numCol - 1 - i;
                                }

                                if (theProblemGoal[j][i] == 0) {
                                    firstTimeZeroPos = numCol - 1 - i;
                                }
                                if (i == 3)
                                    firWDVRFromG.add(numToChar);
                                if (i == 2)
                                    serWDVRFromG.add(numToChar);
                                if (i == 1)
                                    thrWDVRFromG.add(numToChar);
                                if (i == 0)
                                    forWDVRFromG.add(numToChar);
                                break;
                            }

                        }
                    }
                }
            }
        } else {
            for (int i = 0; i < numRow; i++) {
                for (int j = 0; j < numCol; j++) {
                    for (int x = 0; x < numRow; x++) {
                        for (int y = 0; y < numCol; y++) {
                            if (theBoard[j][i] == theProblemGoal[y][x]) {
                                if (x == 0)
                                    numToChar = 'A';
                                if (x == 1)
                                    numToChar = 'B';
                                if (x == 2)
                                    numToChar = 'C';
                                if (x == 3)
                                    numToChar = 'D';

                                if (theBoard[j][i] == 0) {
                                    numToChar = '0';
                                    zeroPositionWDVRFromG = i;
                                }

                                if (theProblemGoal[j][i] == 0) {
                                    firstTimeZeroPos = i;
                                }
                                if (i == 0)
                                    firWDVRFromG.add(numToChar);
                                if (i == 1)
                                    serWDVRFromG.add(numToChar);
                                if (i == 2)
                                    thrWDVRFromG.add(numToChar);
                                if (i == 3)
                                    forWDVRFromG.add(numToChar);
                                break;
                            }

                        }
                    }
                }
            }
        }


        Collections.sort(firWDVRFromG);
        Collections.sort(serWDVRFromG);
        Collections.sort(thrWDVRFromG);
        Collections.sort(forWDVRFromG);
        groupWDVRFromG.add(firWDVRFromG);
        groupWDVRFromG.add(serWDVRFromG);
        groupWDVRFromG.add(thrWDVRFromG);
        groupWDVRFromG.add(forWDVRFromG);

        Character[] chSearchFor = {'A', 'B', 'C', 'D'};
        int occurrences = 0, rowsCompleted = 0;
        for (int p = 0; p < numRow; p++) {
            occurrences = Collections.frequency(groupWDVRFromG.get(p), chSearchFor[p]);
            if (p != firstTimeZeroPos && occurrences == numCol)
                rowsCompleted++;
            else if (p == firstTimeZeroPos && occurrences == numCol - 1)
                rowsCompleted++;

            if (rowsCompleted != p + 1)
                break;
        }

        int chSearchForOC, chSearchForOCAtZeroPosition;
        int steps = 0;
        while (steps <= 100 && rowsCompleted < 4) {

            chSearchForOC = Collections.frequency(groupWDVRFromG.get(rowsCompleted), chSearchFor[rowsCompleted]);

            if (chSearchForOC == 4 && rowsCompleted != firstTimeZeroPos) {
                rowsCompleted++;
            }

            if (chSearchForOC == 3 && rowsCompleted == firstTimeZeroPos) {
                rowsCompleted++;
            }

            if (firstTimeZeroPos == 3 && rowsCompleted == 3)
                break;
            if (rowsCompleted == 4)
                break;

            chSearchForOCAtZeroPosition = Collections.frequency(groupWDVRFromG.get(zeroPositionWDVRFromG), chSearchFor[rowsCompleted]);

            //----------------------------------------------------------
            if (zeroPositionWDVRFromG > rowsCompleted && zeroPositionWDVRFromG < numRow - 1) {
                Character tempC1 = groupWDVRFromG.get(zeroPositionWDVRFromG - 1).get(numCol - 1);
                Character tempC2 = groupWDVRFromG.get(zeroPositionWDVRFromG + 1).get(0);

                if (tempC2 == chSearchFor[rowsCompleted] && chSearchForOCAtZeroPosition < 3) {
                    moveDown(groupWDVRFromG, zeroPositionWDVRFromG, tempC2);
                    zeroPositionWDVRFromG = zeroPositionWDVRFromG + 1;
                } else if (zeroPositionWDVRFromG < numRow - 2) {
                    Character tempC3;
                    tempC3 = groupWDVRFromG.get(zeroPositionWDVRFromG + 2).get(0);
                    if (tempC3 == chSearchFor[rowsCompleted]) {
                        moveDown(groupWDVRFromG, zeroPositionWDVRFromG, tempC2);
                        zeroPositionWDVRFromG = zeroPositionWDVRFromG + 1;
                    } else {
                        moveUp(groupWDVRFromG, zeroPositionWDVRFromG, tempC1);
                        zeroPositionWDVRFromG = zeroPositionWDVRFromG - 1;
                    }
                } else {
                    moveUp(groupWDVRFromG, zeroPositionWDVRFromG, tempC1);
                    zeroPositionWDVRFromG = zeroPositionWDVRFromG - 1;

                }


            } else if (zeroPositionWDVRFromG == numRow - 1) {
                Character tempC = groupWDVRFromG.get(zeroPositionWDVRFromG - 1).get(numCol - 1);
                moveUp(groupWDVRFromG, zeroPositionWDVRFromG, tempC);
                zeroPositionWDVRFromG = zeroPositionWDVRFromG - 1;
            } else if (zeroPositionWDVRFromG == rowsCompleted) {
                Character tempC = groupWDVRFromG.get(zeroPositionWDVRFromG + 1).get(0);
                moveDown(groupWDVRFromG, zeroPositionWDVRFromG, tempC);
                zeroPositionWDVRFromG = zeroPositionWDVRFromG + 1;
            }
            steps++;
        }

        return steps;
    }

    public void moveUp(ArrayList<ArrayList<Character>> groupWDP, int zerPosP, Character tempP) {
        groupWDP.get(zerPosP - 1).set(numCol - 1, '0');
        groupWDP.get(zerPosP).set(0, tempP);
        Collections.sort(groupWDP.get(zerPosP - 1));
        Collections.sort(groupWDP.get(zerPosP));
    }

    public void moveDown(ArrayList<ArrayList<Character>> groupWDP, int zerPosP, Character tempP) {
        groupWDP.get(zerPosP + 1).set(0, '0');
        groupWDP.get(zerPosP).set(0, tempP);
        Collections.sort(groupWDP.get(zerPosP));
        Collections.sort(groupWDP.get(zerPosP + 1));
    }

    private int calculateManhattanDistance2() {

        int SumManhattanDistance = 0;
        for (int x = 0; x < numRow; x++)
            for (int y = 0; y < numCol; y++) {
                int value = theBoard[x][y];
                if (value != 0) {
                    for (int i = 0; i < numRow; i++)
                        for (int j = 0; j < numCol; j++) {
                            if (value == theProblemGoal[i][j]) {
                                if ((Math.abs(x - i) + Math.abs(y - j)) != 0)
                                    SumManhattanDistance += Math.abs(x - i) + Math.abs(y - j);
                            }
                        }
                }
            }

        return SumManhattanDistance;
    }

    public int countMisplacedSquares2() {
        int count = 0;
        for (int i = 0; i < numRow; i++) {
            for (int j = 0; j < numCol; j++) {
                if (theBoard[i][j] > 0 && theBoard[i][j] != theProblemGoal[i][j]) {
                    count++;
                }
            }
        }
        return count;
    }

    public int linearHorizontalConflictFromS() {
        int sumLinConfFromG = 0;
        int bPos1 = 0, bPos2 = 0;
        boolean firstElementExist = false;
        boolean secondElementExist = false;
        for (int x = 0; x < numRow; x++) {
            for (int y = 0; y < numCol; y++) {
                for (int i = x; i <= x; i++) {
                    for (int j = y + 1; j < numCol; j++) {
                        for (int p = 0; p < numCol; p++) {
                            if (theBoard[x][y] == theGoal[x][p] && (theBoard[x][y] != 0 || theGoal[x][p] != 0)) {
                                firstElementExist = true;
                                bPos1 = p;
                            }
                            if (theBoard[i][j] == theGoal[x][p] && (theBoard[i][j] != 0 || theGoal[x][p] != 0)) {
                                secondElementExist = true;
                                bPos2 = p;
                            }
                        }

                        if (firstElementExist && secondElementExist && bPos1 > bPos2) {
                            sumLinConfFromG += 2;
                            firstElementExist = false;
                            secondElementExist = false;
                            break;

                        }

                        firstElementExist = false;
                        secondElementExist = false;

                    }
                }
            }
        }


        return sumLinConfFromG;
    }

    public int linearVerticalConflictFromS() {
        int sumLinConfFromG = 0;
        int bPos1 = 0, bPos2 = 0;
        boolean firstElementExist = false;
        boolean secondElementExist = false;
        for (int x = 0; x < numCol; x++) {
            for (int y = 0; y < numRow; y++) {
                for (int i = x; i <= x; i++) {
                    for (int j = y + 1; j < numRow; j++) {
                        if ((theBoard[y][x] != 0 && theBoard[j][i] != 0)) {

                            for (int p = 0; p < numRow; p++) {
                                if (theBoard[y][x] == theGoal[p][x]) {
                                    firstElementExist = true;
                                    bPos1 = p;
                                }
                                if (theBoard[j][i] == theGoal[p][x]) {
                                    secondElementExist = true;
                                    bPos2 = p;
                                }
                            }

                            if (firstElementExist && secondElementExist && bPos1 > bPos2) {
                                sumLinConfFromG += 2;
                                firstElementExist = false;
                                secondElementExist = false;
                                break;

                            }

                            firstElementExist = false;
                            secondElementExist = false;
                        }
                    }
                }
            }
        }


        return sumLinConfFromG;
    }

    public int linearHorizontalConflictFromG() {
        int sumLinConfFromG = 0;
        int bPos1 = 0, bPos2 = 0;
        boolean firstElementExist = false;
        boolean secondElementExist = false;
        for (int x = 0; x < numRow; x++) {
            for (int y = 0; y < numCol; y++) {
                for (int i = x; i <= x; i++) {
                    for (int j = y + 1; j < numCol; j++) {
                        for (int p = 0; p < numCol; p++) {
                            if (theBoard[x][y] == theProblemGoal[x][p] && (theBoard[x][y] != 0 || theProblemGoal[x][p] != 0)) {
                                firstElementExist = true;
                                bPos1 = p;
                            }
                            if (theBoard[i][j] == theProblemGoal[x][p] && (theBoard[i][j] != 0 || theProblemGoal[x][p] != 0)) {
                                secondElementExist = true;
                                bPos2 = p;
                            }
                        }

                        if (firstElementExist && secondElementExist && bPos1 > bPos2) {
                            sumLinConfFromG += 2;
                            firstElementExist = false;
                            secondElementExist = false;
                            break;

                        }

                        firstElementExist = false;
                        secondElementExist = false;

                    }
                }
            }
        }


        return sumLinConfFromG;
    }

    public int linearVerticalConflictFromG() {
        int sumLinConfFromG = 0;
        int bPos1 = 0, bPos2 = 0;
        boolean firstElementExist = false;
        boolean secondElementExist = false;
        for (int x = 0; x < numCol; x++) {
            for (int y = 0; y < numRow; y++) {
                for (int i = x; i <= x; i++) {
                    for (int j = y + 1; j < numRow; j++) {
                        if ((theBoard[y][x] != 0 && theBoard[j][i] != 0)) {

                            for (int p = 0; p < numRow; p++) {
                                if (theBoard[y][x] == theProblemGoal[p][x]) {
                                    firstElementExist = true;
                                    bPos1 = p;
                                }
                                if (theBoard[j][i] == theProblemGoal[p][x]) {
                                    secondElementExist = true;
                                    bPos2 = p;
                                }
                            }

                            if (firstElementExist && secondElementExist && bPos1 > bPos2) {
                                sumLinConfFromG += 2;
                                firstElementExist = false;
                                secondElementExist = false;
                                break;
                            }

                            firstElementExist = false;
                            secondElementExist = false;
                        }
                    }
                }
            }
        }


        return sumLinConfFromG;
    }


    //---------------------------------------------------------Manhattan Distance Heuristic-----------------------------------------------------
    private int calculateManhattanDistance() {
        int SumManhattanDistance = 0;
        for (int x = 0; x < numRow; x++)
            for (int y = 0; y < numCol; y++) {
                int value = theBoard[x][y];
                if (value != 0) {
                    int xCoordinate = (value - 1) / numRow; //numCol;
                    int yCoordinate = (value - 1) % numCol;
                    SumManhattanDistance += Math.abs(x - xCoordinate) + Math.abs(y - yCoordinate);
                }
            }
        return SumManhattanDistance;
    }


    //---------------------------------------------------------Blank Position-----------------------------------------------------
    public void spacePosition() {
        for (int i = 0; i < numRow; i++) {
            for (int j = 0; j < numCol; j++) {
                if (theBoard[i][j] == 0) {
                    spaceXDir = i;
                    spaceYDir = j;
                }
            }
        }

    }

    //---------------------------------------------------------All Valid Moves-----------------------------------------------------
    public ArrayList<Integer> allowableMoves() {
        spacePosition();
        ArrayList<Integer> allowableMoves = new ArrayList<Integer>();
        for (int x = 0; x < numRow; x++) {
            for (int y = 0; y < numCol; y++) {
                if (x == spaceXDir && (y == (spaceYDir + 1) || y == (spaceYDir - 1)))
                    allowableMoves.add(theBoard[x][y]);
                else if (y == spaceYDir && (x == (spaceXDir + 1) || x == (spaceXDir - 1)))
                    allowableMoves.add(theBoard[x][y]);
            }
        }
        return allowableMoves;
    }

    //---------------------------------------------------------Moves a tile into the space-----------------------------------------------------
    public void moveSquare(int squareNum) {
        boolean boolBreak = true;
        for (int x = 0; x < numRow; x++) {
            for (int y = 0; y < numCol; y++) {
                if (theBoard[x][y] == squareNum) {
                    theBoard[spaceXDir][spaceYDir] = squareNum;
                    theBoard[x][y] = 0;
                    spaceXDir = x;
                    spaceYDir = y;
                    boolBreak = false;
                    break;
                }
            }
            if (!boolBreak)
                break;
        }
    }

    //---------------------------------------------------------All Valid Moves into and Array List Object-----------------------------------------------------
    public ArrayList<FifteenPuzzle> holdingAllPossibleMoves() {
        ArrayList<FifteenPuzzle> auto = new ArrayList<FifteenPuzzle>();

        for (int squareNumber : allowableMoves()) {
            FifteenPuzzle temp = new FifteenPuzzle(this);
            temp.moveSquare(squareNumber);
            auto.add(temp);
        }
        return auto;
    }

    //---------------------------------------------------------Move Constructor-----------------------------------------------------
    public FifteenPuzzle(FifteenPuzzle auto) {
        this();
        for (int x = 0; x < numRow; x++) {
            for (int y = 0; y < numCol; y++) {
                theBoard[x][y] = auto.theBoard[x][y];
            }
        }
        spaceXDir = auto.spaceXDir;
        spaceYDir = auto.spaceYDir;
    }

    //---------------------------------------------------------Default Constructor-----------------------------------------------------
    public FifteenPuzzle() {
        theBoard = new int[numRow][numCol];
    }

    //---------------------------------------------------------Check if two Objects are equal or not-----------------------------------------------------
    @Override
    public boolean equals(Object o) {
        if (o instanceof FifteenPuzzle) {
            for (int x = 0; x < numRow; x++) {
                for (int y = 0; y < numCol; y++) {
                    if (this.theBoard[x][y] != ((FifteenPuzzle) o).theBoard[x][y])
                        return false;

                }
            }
            return true;
        }
        return false;
    }

    //---------------------------------------------------------Hash Code-----------------------------------------------------
    @Override
    public int hashCode() {
        int hashCode = 0;
        for (int x = 0; x < numRow; x++) {
            for (int y = 0; y < numCol; y++) {
                hashCode = (hashCode * 11) + this.theBoard[x][y];
            }
        }
        return hashCode;
    }

    //---------------------------------------------------------Arrange objects in order according to the Heuristics-----------------------------------------------------
    public class objectOrder implements Comparable {
        private double priority;
        private FifteenPuzzle theObject;

        public objectOrder(double aPriority, FifteenPuzzle aDescription) {
            priority = aPriority;
            theObject = aDescription;
        }

        public FifteenPuzzle objReturn() {
            return theObject;
        }

        public int compareTo(Object otherObject) {
            objectOrder other = (objectOrder) otherObject;
            if (priority < other.priority) return -1;
            if (priority > other.priority) return 1;
            return 0;
        }
    }

    //---------------------------------------------------------Check if solved or not-----------------------------------------------------
    public boolean isSolved() {
        return countMisplacedSquares() == 0;
    }

    //---------------------------------------------------------Misplaced Tile Heuristic-----------------------------------------------------
    public int countMisplacedSquares() {
        int count = 0;
        for (int i = 0; i < numRow; i++) {
            for (int j = 0; j < numCol; j++) {
                if (theBoard[i][j] > 0 && theBoard[i][j] != theGoal[i][j]) {
                    count++;
                }
            }
        }
        return count;
    }

    //---------------------------------------------------------The Goal-----------------------------------------------------
    public void theGoal(String blankAt) {
        theGoal = new int[numRow][numCol];
        if (blankAt == "topLeft") {
            int theGoalVar = 0;
            for (int i = 0; i < numRow; i++) {
                for (int j = 0; j < numCol; j++) {
                    theGoal[i][j] = theGoalVar;
                    theGoalVar++;
                }
            }
        } else {
            int theGoalVar = 1;
            for (int i = 0; i < numRow; i++) {
                for (int j = 0; j < numCol; j++) {
                    if (i == numRow - 1 && j == numCol - 1) {
                        theGoalVar = 0;
                        theGoal[i][j] = 0;
                    } else
                        theGoal[i][j] = theGoalVar;
                    theGoalVar++;
                }
            }
        }
    }

    //---------------------Walking Distance Bottom Right-------------------------
    static Map<Long, Integer> wdcosts = new HashMap<Long, Integer>();

    public static void fillTheWDMapFromFile() {
        String filePath;
        if (blankAt.equals("topLeft"))
            filePath = "src/main/java/wdcoststl.txt";
        else
            filePath = "src/main/java/wdcostsbr.txt";

        try (final Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                final String line = scanner.nextLine();
                final String[] parts = line.split(":");
                wdcosts.put(Long.valueOf(parts[0]), Integer.valueOf(parts[1]));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static Map<Integer, Integer> goals = new HashMap<Integer, Integer>();

    public static void convertGoalBotR() {
        int[] goal = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 0};

        for (Integer key : goal) {
            int tem = Arrays.binarySearch(goal, key);
            tem = tem == -1 ? 15 : tem;
            goals.put(key, tem);
        }
    }

    public static void convertGoalTopL() {
        int[] goal = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
        for (Integer key : goal) {
            int tem = Arrays.binarySearch(goal, key);
            tem = tem == -1 ? 15 : tem;
            goals.put(key, tem);
        }
    }

    static BigInteger big1;

    public int wdBottomRightFromSS() {
        Map<Integer, Integer> boarMap = new HashMap<Integer, Integer>();
        int it = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < numCol; j++) {
                boarMap.put(it, theBoard[i][j]);
                it++;
            }
        }
        long ht = 0L;
        long vt = 0L;
        int d = 0;
        big1 = new BigInteger(String.valueOf(numCol));
        int b = big1.bitLength();

        for (Map.Entry<Integer, Integer> entry : boarMap.entrySet()) {
            {
                if (entry.getValue() == 0) continue;
                int g = goals.get(entry.getValue());
//                System.out.println(entry.getValue() + " -- " + g);
                int xi = entry.getKey() % numCol, yi = entry.getKey() / numCol;
                int xg = g % numCol, yg = g / numCol;
                ht += 1L << (b * (numCol * yi + yg));
                //L To treat the value as Long
                vt += 1L << (b * (numCol * xi + xg));
                if (yg == yi) {
                    for (int k = entry.getKey() + 1;
                         k < entry.getKey() - entry.getKey() % numCol + numCol;
                         k++) {
                        if (boarMap.get(k) > 0 && goals.get(boarMap.get(k)) / numCol == yi && goals.get(boarMap.get(k)) < g)
                            d += 2;
                    }
                }
                if (xg == xi) {
                    for (int k = entry.getKey() + numCol;
                         k < numCol * numCol;
                         k += numCol) {
                        if (boarMap.get(k) > 0 && goals.get(boarMap.get(k)) % numCol == xi && goals.get(boarMap.get(k)) < g)
                            d += 2;
                    }
                }
            }


        }
        d = wdcosts.get(ht) + wdcosts.get(vt);
        return d;
    }

    public int wdTopLeftFromSS() {
        Map<Integer, Integer> boarMap = new HashMap<Integer, Integer>();
        int it = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < numCol; j++) {
                boarMap.put(it, theBoard[i][j]);
                it++;
            }
        }
        long ht = 0L;
        long vt = 0L;
        int d = 0;
        big1 = new BigInteger(String.valueOf(numCol));
        int b = big1.bitLength();

        for (Map.Entry<Integer, Integer> entry : boarMap.entrySet()) {
            if (entry.getValue() == 0) continue;
            int g = goals.get(entry.getValue());

            int xi = entry.getKey() % numCol, yi = entry.getKey() / numCol;
            int xg = g % numCol, yg = g / numCol;

            ht += 1L << (b * (numCol * yi + yg));
            vt += 1L << (b * (numCol * xi + xg));
            if (yg == yi) {
                for (int k = entry.getKey() + 1;
                     k < entry.getKey() - entry.getKey() % numCol + numCol;
                     k++) {
                    if (boarMap.get(k) > 0 && goals.get(boarMap.get(k)) / numCol == yi && goals.get(boarMap.get(k)) < g)
                        d += 2;
                }
            }
            if (xg == xi) {
                for (int k = entry.getKey() + numCol;
                     k < numCol * numCol;
                     k += numCol) {
                    if (boarMap.get(k) > 0 && goals.get(boarMap.get(k)) % numCol == xi && goals.get(boarMap.get(k)) < g)
                        d += 2;
                }
            }
        }

        d = wdcosts.get(ht) + wdcosts.get(vt);
        return d;
    }

    public void engine(ArrayList<Integer> BoardCellsNumbers, String blankAtp) {
        //As static variables, they need to be cleared.
        keyValueStatesFSS.clear();
        keyValueStatesFGS.clear();
        iterator = 0;
        iterator2 = 0;
        FifteenPuzzle autosolve = new FifteenPuzzle();
        autosolve.theGoal(blankAtp);
        blankAt = blankAtp;

        int index = 0;
        for (int i = 0; i < numRow; i++) {
            for (int j = 0; j < numCol; j++) {
                autosolve.theBoard[i][j] = BoardCellsNumbers.get(index);//sendTheShuffleBoard[i][j];
                if (BoardCellsNumbers.get(index) == 0) {
                    autosolve.spaceXDir = i;
                    autosolve.spaceYDir = j;
                }
                index++;
            }
        }
        theProblemGoal = new int[numRow][numCol];
        int index2 = 0;
        int a;
        for (int i = 0; i < numRow; i++) {
            for (int j = 0; j < numCol; j++) {
                a = BoardCellsNumbers.get(index2);
                if (a == 0)
                    zeroPositionAtNRow = i;
                if (a == 0)
                    zeroPositionAtNColumn = j;
                theProblemGoal[i][j] = BoardCellsNumbers.get(index2);
                index2++;
            }
        }
        fillTheWDMapFromFile();
        if (blankAt.equals("topLeft")) {
            convertGoalTopL();
        } else {

            convertGoalBotR();
        }
        solution = autosolve.AStarAlgorithm();
        System.out.println();
        //-------
        int depth = 0;
        for (FifteenPuzzle fif: solution) {
            System.out.println("Depth: " + depth);
            String ss = "";
            for (int x = 0; x < numRow; x++) {
                for (int y = 0; y < numCol; y++) {
                    ss += fif.theBoard[x][y] + "\t";
                }
                System.out.println(ss);
                ss = "";
            }
            System.out.println();
            depth++;
        }
    }

    public static int zeroPositionAtNRow;
    public static int zeroPositionAtNColumn;
}