package ttfe;

import java.util.Random;

public class EIMAlaMotleikz implements PlayerInterface{
    private double[][] WeightMatrixS;
    private int boardHeight;
    private int boardWidth;
    private double WeightMatrixBase = 2.5;//use 1.7 @depth = 6 // make sure are you using pow or multiplication
    private double stuckValue = -100000;
    private int depth = 3;// 3 for speed, 6 for precision
    private int moveEfficiencyOffset = 3;
    private double NumPieceDeltaMultiplier = 1000;
    private double combinationScoreMultiplier = 150;// this grows with biggest piece, needs to be scaled up
    private AIWorkspace basisBoardReference;

    public MoveDirection getPlayerMove(SimulatorInterface game, UserInterface ui) {
        this.WeightMatrixS = new double[game.getBoardHeight()][game.getBoardWidth()];
        this.boardHeight = game.getBoardHeight();
        this.boardWidth = game.getBoardWidth();
        int n = 0;

        //S Weighted Matrix Creation
        for (int y_pos = 0; y_pos < boardHeight; y_pos++) {
            if (y_pos % 2 == 0) {
                for (int x_pos = 0; x_pos < boardWidth; x_pos++) {
                    WeightMatrixS[y_pos][x_pos] = Math.pow(WeightMatrixBase, n);
                    // WeightMatrixS[y_pos][x_pos] = WeightMatrixBase * n;
                    n++;
                }
            }
            if (y_pos % 2 == 1) {
                for (int x_pos = boardWidth - 1; x_pos >= 0; x_pos--) {
                    WeightMatrixS[y_pos][x_pos] = Math.pow(WeightMatrixBase, n);
                    // WeightMatrixS[y_pos][x_pos] = WeightMatrixBase * n;
                    n++;
                }
            }
        }

        basisBoardReference = new AIWorkspace(game);

        return nextMove();
    }

    private MoveDirection nextMove() {
        double bestScore = -1;
        MoveDirection bestMove = randomMove();
        double score = 0;

        for (MoveDirection dir: MoveDirection.values()){
            if (!isMovePossible(basisBoardReference, dir)) {
                continue;
            }
            AIWorkspace performedBoard = new AIWorkspace(basisBoardReference);
            score = performedBoard.simulateMove(dir) * combinationScoreMultiplier;

            // printGameState(basisBoardReference);
            
            // if (isIdentical(basisBoardReference, performedBoard)) {
            //     continue;
            // }

            score += expectimax(performedBoard, depth - 1, false);
            if (score > bestScore) {
                bestScore = score;
                bestMove = dir;
            }
        }

        return bestMove;
    }

    private double expectimax(AIWorkspace board, int n, boolean maxPlayer) {
        if (n == 0) {
            if (!board.isMovePossible()) {
                return stuckValue;
            }
            // double patternHeuristic = patternHeuristic(board);
            // double moveEfficiency = moveEfficiency(board);
            // System.out.println("patternHeuristic: " + patternHeuristic);
            // System.out.println("moveEfficiency: " + moveEfficiency);
            // System.out.println("Actual Calculation returned");
            return patternHeuristic(board) + moveEfficiency(board);
        }

        if (maxPlayer) {
            double bestScore = -1;
            double score = 0;
            for (MoveDirection dir: MoveDirection.values()){
                if (!isMovePossible(board, dir)) {
                    continue;
                }
                AIWorkspace performedBoard = new AIWorkspace(board);
                score = performedBoard.simulateMove(dir) * combinationScoreMultiplier;

                // if (isIdentical(board, performedBoard)){
                //     continue;
                // }
                score += expectimax(performedBoard, n - 1, false);

                if (score > bestScore) {
                    bestScore = score;
                }

            }
            return bestScore;
        }

        double sumScore = 0;
        int num = 0;
        for (int coordinate: board.getEmptyCoordinate()) {
            AIWorkspace performedBoardWNP = new AIWorkspace(board);
            Random r = new Random();
            int newPieceDecision = r.nextInt(10);
            int[] newPiece = new int[]{2,2,2,2,2,2,2,2,2,4};
            performedBoardWNP.setPieceAt(coordinate%boardWidth, coordinate/boardWidth, newPiece[newPieceDecision]);

            sumScore += expectimax(performedBoardWNP, n - 1, true);

            num += 1;
        }

        if (num == 0){
            return expectimax(board, n - 1, true);
        }

        return sumScore / num;
    }

    // private boolean isIdentical(AIWorkspace cmp1, AIWorkspace cmp2){
    //     if (cmp1.getBoardHeight() != cmp2.getBoardHeight() || cmp1.getBoardWidth() != cmp2.getBoardWidth()){
    //         throw new IllegalArgumentException();
    //     }
    //     for (int y_pos = 0; y_pos < cmp1.getBoardHeight(); y_pos++){
    //         for (int x_pos = 0; x_pos < cmp1.getBoardWidth(); x_pos++){
    //             if (cmp1.getPieceAt(x_pos, y_pos) != cmp2.getPieceAt(x_pos, y_pos)){
    //                 return false;
    //             }
    //         }
    //     }

    //     return true;
    // }

    private MoveDirection randomMove() {
        Random r = new Random();
        return MoveDirection.values()[r.nextInt(MoveDirection.values().length)];
    }

    private int patternHeuristic(AIWorkspace board) {
        int sum = 0;
        for (int y_pos = 0; y_pos < boardHeight; y_pos++) {
            for (int x_pos = 0; x_pos < boardWidth; x_pos++) {
                sum += WeightMatrixS[y_pos][x_pos] * board.getPieceAt(x_pos, y_pos);
            }
        }

        return sum;
    }

    private double moveEfficiency(AIWorkspace board) {
        return (basisBoardReference.getNumPieces() - board.getNumPieces() + moveEfficiencyOffset) * NumPieceDeltaMultiplier;
    }

/*     private void printGameState(AIWorkspace board) {
        for (int y = 0; y < board.getBoardHeight(); y++) {
            for (int x = 0; x < board.getBoardWidth(); x++) {
                System.out.print("|" + board.getPieceAt(x, y) + "|");
            }
            if(y != board.getBoardHeight() - 1){System.out.println("\n---------");}
        }
        System.out.print("\n\n\n");
    } */

    private boolean isMovePossible(AIWorkspace board, MoveDirection direction) {
        if (direction == null){
            throw new IllegalArgumentException("Invalid direction was given");
        }
        int x_dir = 0;
        int y_dir = 0;
        int x_start = 0;
        int x_end = board.getBoardWidth();
        int y_start = 0;
        int y_end = board.getBoardHeight();
        switch (direction) {
            case NORTH:
                y_dir = -1;
                y_start = 1;
                break;
            case SOUTH:
                y_dir = 1;
                y_end -= 1;
                break;
            case WEST:
                x_dir = -1;
                x_start = 1;
                break;
            case EAST:
                x_dir = 1;
                x_end -= 1;
                break;
            default:
                throw new IllegalArgumentException("Invalid direction was given");
        }

        for (int y_pos = y_start; y_pos < y_end; y_pos++) {
            for (int x_pos = x_start; x_pos < x_end; x_pos++) {
                if (board.getPieceAt(x_pos, y_pos) != 0){
                    if (board.getPieceAt(x_pos + x_dir, y_pos + y_dir) == 0) { // if the destination is 0
                        return true;
                    }
                    if (board.getPieceAt(x_pos, y_pos) == board.getPieceAt(x_pos + x_dir, y_pos + y_dir)){ //if 2 pieces are same
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
