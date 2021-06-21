package ttfe;

import java.util.Random;

public class EIMAlaMotleikz implements PlayerInterface{
    private double[][] WeightMatrixS;
    private int boardHeight;
    private int boardWidth;
    private double WeightMatrixBase = 2;
    private double stuckValue = -100000;
    private int depth = 6;// best at 3
    private int moveEfficiencyOffset = 3;
    private double WeightNumPieceDeltaMultiplier = 10;
    private double combinationScoreMultiplier = 80;
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
                    n++;
                }
            }
            if (y_pos % 2 == 1) {
                for (int x_pos = boardWidth - 1; x_pos >= 0; x_pos--) {
                    WeightMatrixS[y_pos][x_pos] = Math.pow(WeightMatrixBase, n);
                    n++;
                }
            }
        }

        basisBoardReference = new AIWorkspace(game);

        return nextMove();
    }

    private MoveDirection nextMove() {
        int n = depth;
        double bestScore = -1;
        MoveDirection bestMove =  randomMove();
        double score = 0;

        for (MoveDirection dir: MoveDirection.values()){
            AIWorkspace performedBoard = new AIWorkspace(basisBoardReference);
            score = performedBoard.simulateMove(dir) * combinationScoreMultiplier;

            // printGameState(basisBoardReference);
            
            if (isIdentical(basisBoardReference, performedBoard)) {
                continue;
            }

            score += expectimax(performedBoard, n - 1, false);
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
            // System.out.println("Actual Calculation returned");
            return patternHeuristic(board) + moveEfficiency(board);
        }

        if (maxPlayer) {
            double bestScore = -1;
            double score = 0;
            for (MoveDirection dir: MoveDirection.values()){
                AIWorkspace performedBoard = new AIWorkspace(board);
                score = performedBoard.simulateMove(dir) * combinationScoreMultiplier;

                if (isIdentical(board, performedBoard)){
                    continue;
                }
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

    private boolean isIdentical(AIWorkspace cmp1, AIWorkspace cmp2){
        if (cmp1.getBoardHeight() != cmp2.getBoardHeight() || cmp1.getBoardWidth() != cmp2.getBoardWidth()){
            throw new IllegalArgumentException();
        }
        for (int y_pos = 0; y_pos < cmp1.getBoardHeight(); y_pos++){
            for (int x_pos = 0; x_pos < cmp1.getBoardWidth(); x_pos++){
                if (cmp1.getPieceAt(x_pos, y_pos) != cmp2.getPieceAt(x_pos, y_pos)){
                    return false;
                }
            }
        }

        return true;
    }

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
        return (basisBoardReference.getNumPieces() - board.getNumPieces() + moveEfficiencyOffset) * WeightNumPieceDeltaMultiplier;
    }

    private void printGameState(AIWorkspace board) {
        for (int y = 0; y < board.getBoardHeight(); y++) {
            for (int x = 0; x < board.getBoardWidth(); x++) {
                System.out.print("|" + board.getPieceAt(x, y) + "|");
            }
            if(y != board.getBoardHeight() - 1){System.out.println("\n---------");}
        }
        System.out.print("\n\n\n");
    }
}
