package ttfe;

public class AIWorkspace {
    private int[][] gameState;
    private int numPieces;
    private int boardHeight;
    private int boardWidth;

    public AIWorkspace(SimulatorInterface gameState) {
        this.boardWidth = gameState.getBoardWidth();
        this.boardHeight = gameState.getBoardHeight();
        this.numPieces = gameState.getNumPieces();
        this.numPieces = 0;
        this.gameState = new int[boardHeight][boardWidth];

        
        
        //Current Game Board Cloning
        for (int y_pos = 0; y_pos < boardHeight; y_pos++) {
            for (int x_pos = 0; x_pos < boardWidth; x_pos++) {
                setPieceAt(x_pos, y_pos, gameState.getPieceAt(x_pos, y_pos));
                if (gameState.getPieceAt(x_pos, y_pos) != 0) {
                    this.numPieces += 1;
                }
            }
        }
    }

    public AIWorkspace(AIWorkspace gameState) {
        this.boardWidth = gameState.getBoardWidth();
        this.boardHeight = gameState.getBoardHeight();
        this.numPieces = gameState.getNumPieces();
        this.numPieces = 0;
        this.gameState = new int[boardHeight][boardWidth];

        
        //Current Game Board Cloning
        for (int y_pos = 0; y_pos < boardHeight; y_pos++) {
            for (int x_pos = 0; x_pos < boardWidth; x_pos++) {
                setPieceAt(x_pos, y_pos, gameState.getPieceAt(x_pos, y_pos));
                if (gameState.getPieceAt(x_pos, y_pos) != 0) {
                    this.numPieces += 1;
                }
            }
        }
    }

    public void setPieceAt(int x_pos, int y_pos, int piece) {
        this.gameState[y_pos][x_pos] = piece;
    }
    
    public int getPieceAt(int x_pos, int y_pos) {
        return gameState[y_pos][x_pos];
    }

    public int getNumPieces() {
        return numPieces;
    }
    
    public void updateNumPieces() {
        int tempNumPieces = 0;
        for (int y_pos = 0; y_pos < boardHeight; y_pos++) {
            for (int x_pos = 0; x_pos < boardWidth; x_pos++) {
                if (gameState[y_pos][x_pos] != 0) {
                    tempNumPieces += 1;
                }
            }
        }

        this.numPieces = tempNumPieces;
    }

    public int getBoardHeight() {
        return boardHeight;
    }

    public int getBoardWidth() {
        return boardWidth;
    }

    public boolean isMovePossible() {
        if (isSpaceLeft()) {return true;}
        for (int y_pos = 0; y_pos < boardHeight; y_pos++) {
            for (int x_pos = 0; x_pos < boardWidth; x_pos++) {
                if (y_pos > 0) {
                    if (gameState[y_pos][x_pos] == gameState[y_pos - 1][x_pos]) {return true;}
                }
                if (y_pos < boardHeight - 1){
                    if (gameState[y_pos][x_pos] == gameState[y_pos + 1][x_pos]) {return true;}
                }
                if (x_pos > 0) {
                    if (gameState[y_pos][x_pos] == gameState[y_pos][x_pos - 1]) {return true;}
                }
                if (x_pos < boardWidth - 1){
                    if (gameState[y_pos][x_pos] == gameState[y_pos][x_pos + 1]) {return true;}
                }
            }
        }
        
        return false;
    }
    
    public boolean isSpaceLeft() {
        updateNumPieces();
        return numPieces < (boardHeight * boardWidth);
    }

    public int simulateMove(MoveDirection dir) {
        int first_order;
        int end_first_order;
        int end_second_order;
        boolean increment_first_order;

        int x_dir = 0;
        int y_dir = 0;
        switch (dir) {
            case NORTH:
                first_order = 1;
                end_first_order = boardHeight;
                end_second_order = boardWidth;
                increment_first_order = true;
                y_dir = -1;
                break;
            case SOUTH:
                first_order = boardHeight - 2;
                end_first_order = -1;
                end_second_order = boardWidth;
                increment_first_order = false;
                y_dir = 1;
                break;
            case WEST:
                first_order = 1;
                end_first_order = boardWidth;
                end_second_order = boardHeight;
                increment_first_order = true;
                x_dir = -1;
                break;
            case EAST:
                first_order = boardWidth - 2;
                end_first_order = -1;
                end_second_order = boardHeight;
                increment_first_order = false;
                x_dir = 1;
                break;
            default:
                throw new IllegalArgumentException("Invalid direction given!");
        }
        if (!isMovePossible(dir)) {
            return 0;
        }
        boolean[][] combination = new boolean[boardHeight][boardWidth];// default value is false
        int y_pos;
        int x_pos;
        int temp_y_dir;
        int temp_x_dir;
        boolean move_possible;
        int score = 0;
        for(int first_order_i = first_order; intCompare(first_order_i, end_first_order, increment_first_order);
                    first_order_i = inDecrementor(first_order_i, increment_first_order)) {
                        for (int second_order_i = 0; second_order_i < end_second_order; second_order_i++) {
                            x_pos = adapter(first_order_i, second_order_i, dir, "x_pos");
                            y_pos = adapter(first_order_i, second_order_i, dir, "y_pos");

                            if (gameState[y_pos][x_pos] != 0){
                                move_possible = true;
                                temp_y_dir = 0;
                                temp_x_dir = 0;
                                while (intCompare(first_order_i + temp_y_dir + temp_x_dir, first_order + x_dir + y_dir, !increment_first_order)
                                    && !combination[y_pos + temp_y_dir + y_dir][x_pos + temp_x_dir + x_dir] && move_possible) {
                                        if (gameState[y_pos + temp_y_dir][x_pos + temp_x_dir]
                                            == gameState[y_pos + temp_y_dir + y_dir][x_pos + temp_x_dir + x_dir]) {
                                                this.gameState[y_pos + temp_y_dir + y_dir][x_pos + temp_x_dir + x_dir] = gameState[y_pos + temp_y_dir][x_pos + temp_x_dir] * 2;
                                                this.gameState[y_pos + temp_y_dir][x_pos + temp_x_dir] = 0;
                                                combination[y_pos + temp_y_dir + y_dir][x_pos + temp_x_dir + x_dir] = true;
                                                score += gameState[y_pos + temp_y_dir + y_dir][x_pos + temp_x_dir + x_dir];
                                                break;
                                            }

                                        if (gameState[y_pos + temp_y_dir + y_dir][x_pos + temp_x_dir + x_dir] == 0) {
                                            this.gameState[y_pos + temp_y_dir + y_dir][x_pos + temp_x_dir + x_dir] = gameState[y_pos + temp_y_dir][x_pos + temp_x_dir];
                                            this.gameState[y_pos + temp_y_dir][x_pos + temp_x_dir] = 0;
                                            temp_x_dir += x_dir;
                                            temp_y_dir += y_dir;
                                            continue;
                                        }

                                        move_possible = false;
                                }
                            }
                        }
                    }
    
    updateNumPieces();
    
    return score;
    }

    public boolean isMovePossible(MoveDirection dir) {
        int x_dir = 0;
        int y_dir = 0;
        int x_start = 0;
        int x_end = getBoardWidth();
        int y_start = 0;
        int y_end = getBoardHeight();
        switch (dir) {
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
                if (gameState[y_pos][x_pos] != 0){
                    if (gameState[y_pos+y_dir][x_pos+x_dir] == 0) { // if the destination is 0
                        return true;
                    }
                    if (gameState[y_pos][x_pos] == gameState[y_pos+y_dir][x_pos+x_dir]){ //if 2 pieces are same
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public int[] getEmptyCoordinate() {
        int[] emptyCoordinate = new int[boardHeight*boardWidth - numPieces];
        int n = 0;
        for (int y_pos = 0; y_pos < boardHeight; y_pos++){
            for (int x_pos = 0; x_pos < boardWidth; x_pos++){
                if (gameState[y_pos][x_pos] == 0) {
                    emptyCoordinate[n] = x_pos + (y_pos * boardWidth);
                    n++;
                }
            }
        }

        return emptyCoordinate;
    }

    private int inDecrementor(int value, boolean increment) {
        if (increment) {return (value + 1);}
        return (value - 1);
    }

    private boolean intCompare(int current_value, int limit, boolean increment) {
        if (increment) {return current_value < limit;}
        return current_value > limit;
    }

    private int adapter(int first_order, int second_order, MoveDirection dir, String assignTo){
        switch (assignTo){
            case "x_pos":
                switch (dir) {
                    case NORTH: return second_order;
                    case SOUTH: return second_order;
                    case WEST:  return first_order;
                    case EAST:  return first_order;
                    default:    throw new IllegalArgumentException();
                }
            case "y_pos":
                switch (dir) {
                    case NORTH: return first_order;
                    case SOUTH: return first_order;
                    case WEST:  return second_order;
                    case EAST:  return second_order;
                    default:    throw new IllegalArgumentException();
                }
            default: throw new IllegalArgumentException();
        }
    }
}
