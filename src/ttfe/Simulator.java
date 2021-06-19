package ttfe;

import java.util.Random;

public class Simulator implements SimulatorInterface {
    private int[][] board;
    private int points;
    private int numMoves;
    private int numPieces;
    private int boardHeight;
    private int boardWidth;
    private Random random;


    public Simulator(int width, int height, Random r) {
        if (width < 2 || height < 2 || r == null) {
            throw new IllegalArgumentException("Invalid board specification");
        }

        this.boardWidth = width;
        this.boardHeight = height;
        this.board = new int[height][width];

        this.random = r;

        this.points = 0;
        this.numMoves = 0;
        this.numPieces = 0;

        this.addPiece();
        this.addPiece();
        //now how to save the seed

    }

    // public void setPoints(int meltedPieces) {
    //     this.points += meltedPieces; //idk if this score counter logic is right lol
    // }

    // public void setNumMoves() {
    //     this.numMoves += 1; //assuming there is no undo feature
    // }

    private void setNumPieces() {
        int tempNumPieces = 0;
        for (int y_pos = 0; y_pos < boardHeight; y_pos++) {
            for (int x_pos = 0; x_pos < boardWidth; x_pos++) {
                if (board[y_pos][x_pos] != 0) {
                    tempNumPieces += 1;
                }
            }
        }

        this.numPieces = tempNumPieces;
    }

    public void addPiece() {
        int x_pos;
        int y_pos;
        int dice;

        if (!isSpaceLeft()) {
            throw new IllegalArgumentException("The board is already full");
        }

        do {
            x_pos = getRandom().nextInt(getBoardWidth());
            y_pos = getRandom().nextInt(getBoardWidth());
        } while (getPieceAt(x_pos, y_pos) != 0);
        
        dice = getRandom().nextInt(10); //[ 0 : upperbound [

        this.numPieces += 1;

        if (dice < 8) {
            setPieceAt(x_pos, y_pos, 2);

            return;
        }//90% : 2

        setPieceAt(x_pos, y_pos, 4); //10% : 4

    }
    
    public int getBoardHeight() {
        return boardHeight;
    }

    public int getBoardWidth() {
        return boardWidth;
    }

    public int getNumMoves() {
        return numMoves;
    }

    public int getNumPieces() {
        return numPieces;
    }

    public int getPieceAt(int x, int y) {
        if (x < 0 || x >= getBoardWidth() || y < 0 || y >= getBoardHeight()) {
            throw new IllegalArgumentException("Invalid coordinate: possibly out of bound!");
        }
        return board[y][x];
    }

    public int getPoints() {
        return points;
    }

    public boolean isMovePossible() {
        if (isSpaceLeft()) {return true;}
        for (int y_pos = 0; y_pos < getBoardHeight(); y_pos++) {
            for (int x_pos = 0; x_pos < getBoardWidth(); x_pos++) {
                // if (getPieceAt(x_pos, y_pos) == 0) { return true; }
                if (y_pos > 0) { //north edge
                    if (getPieceAt(x_pos, y_pos) == getPieceAt(x_pos, y_pos - 1)) {
                        return true;
                    }
                }
                if (y_pos < getBoardHeight() - 2) { //south edge
                    if (getPieceAt(x_pos, y_pos) == getPieceAt(x_pos, y_pos + 1)) {
                        return true;
                    }
                }
                if (x_pos > 0) { // west edge
                    if (getPieceAt(x_pos, y_pos) == getPieceAt(x_pos - 1, y_pos)) {
                        return true;
                    }
                }
                if (x_pos < getBoardWidth() - 2){ //east edge
                    if (getPieceAt(x_pos, y_pos) == getPieceAt(x_pos + 1, y_pos)) {
                        return true;
                    }
                }
            }
        }
        return false;

    }

    public boolean isMovePossible(MoveDirection direction) {
        if (direction == null){
            throw new IllegalArgumentException("Invalid direction was given");
        }
        int x_dir = 0;
        int y_dir = 0;
        int x_start = 0;
        int x_end = getBoardWidth();
        int y_start = 0;
        int y_end = getBoardHeight();
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
                if (getPieceAt(x_pos, y_pos) != 0){
                    if (getPieceAt(x_pos + x_dir, y_pos + y_dir) == 0) { // if the destination is 0
                        return true;
                    }
                    if (getPieceAt(x_pos, y_pos) == getPieceAt(x_pos + x_dir, y_pos + y_dir)){ //if 2 pieces are same
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean isSpaceLeft() {
        setNumPieces();
        return getNumPieces() < getBoardHeight()*getBoardWidth();
    }

    
    /*public boolean performMove(MoveDirection direction) {

        int change = 0;
        boolean checked = false;
        //set direction
        int x_dir = 0;
        int y_dir = 0;
        int x_start = 0;
        int x_end = getBoardWidth();
        int y_start = 0;
        int y_end = getBoardHeight();
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
                throw new IllegalArgumentException("Invalid direction is given");
        }

        if (!isMovePossible(direction)){
            return false;
        }

        do {
            change = 0;
            for (int y_pos = y_start; y_pos < y_end; y_pos++) {
                for (int x_pos = x_start; x_pos < x_end; x_pos++) {
                    if (getPieceAt(x_pos, y_pos) != 0) {
                        if (getPieceAt(x_pos, y_pos) == getPieceAt(x_pos + x_dir, y_pos + y_dir)){ //if 2 pieces are same
                            setPieceAt(x_pos + x_dir, y_pos + y_dir, getPieceAt(x_pos, y_pos) * 2);
                            setPieceAt(x_pos, y_pos, 0);
                            this.points += getPieceAt(x_pos + x_dir, y_pos + y_dir);
                            // setPoints(getPieceAt(x_pos + x_dir, y_pos + y_dir) * 2);
                            change += 1;
                            break;
                        }
                        if (getPieceAt(x_pos + x_dir, y_pos + y_dir) == 0) { // if the destination is 0
                            setPieceAt(x_pos + x_dir, y_pos + y_dir, getPieceAt(x_pos, y_pos));
                            setPieceAt(x_pos, y_pos, 0);
                            change += 1;
                            break;
                        }
                    }
                }
            }

            checked = true;

        } while (change != 0 && checked);

        int tempNumPieces = 0;
        for (int y_pos = 0; y_pos < getBoardHeight(); y_pos++) {
            for (int x_pos = 0; x_pos < getBoardWidth(); x_pos++) {
                if (getPieceAt(x_pos, y_pos) > 0) {
                    tempNumPieces += 1;
                }
            }
        }
        
        this.numPieces = tempNumPieces;

        addPiece();

        setNumMoves();

        return true;
    }*/

    /* public boolean performMove(MoveDirection direction) {
        int first_order;
        int end_first_order;
        int end_second_order;
        boolean increment_first_order;

        int x_dir = 0;
        int y_dir = 0;
        if (direction == null){
            throw new IllegalArgumentException("Invalid direction was given.");
        }

        switch (direction) {
            case NORTH:
                first_order = 1;
                end_first_order = getBoardHeight();
                end_second_order = getBoardWidth();
                increment_first_order = true;
                y_dir = -1;
                break;
            case SOUTH:
                first_order = getBoardHeight() - 2;
                end_first_order = -1;
                end_second_order = getBoardWidth();
                increment_first_order = false;
                y_dir = 1;
                break;
            case WEST:
                first_order = 1;
                end_first_order = getBoardWidth();
                end_second_order = getBoardHeight();
                increment_first_order = true;
                x_dir = -1;
                break;
            case EAST:
                first_order = getBoardWidth() - 2;
                end_first_order = -1;
                end_second_order = getBoardHeight();
                increment_first_order = false;
                x_dir = 1;
                break;
            default:
                throw new IllegalArgumentException("Invalid direction given!");
        }
        if (!isMovePossible(direction)) {
            return false;
        }

        boolean[] first_order_combination = new boolean[Math.abs(first_order - end_first_order) + 1];// default value is false
        int change;
        int y_pos;
        int x_pos;
        boolean checked = false;
        do {
            change = 0;
            // boolean combined = false;
            for(int first_order_i = first_order; intCompare(first_order_i, end_first_order, increment_first_order);
                    first_order_i = inDecrementor(first_order_i, increment_first_order)) {
                        for (int second_order_i = 0; second_order_i < end_second_order; second_order_i++) {
                            x_pos = adapter(first_order_i, second_order_i, direction, "x_pos");
                            y_pos = adapter(first_order_i, second_order_i, direction, "y_pos");
                            if(getPieceAt(x_pos, y_pos) != 0){
                                if (getPieceAt(x_pos, y_pos) == getPieceAt(x_pos + x_dir, y_pos + y_dir) && !first_order_combination[first_order_i]) { //if 2 pieces are same
                                    setPieceAt(x_pos + x_dir, y_pos + y_dir, getPieceAt(x_pos, y_pos) * 2);
                                    setPieceAt(x_pos, y_pos, 0);
                                    this.points += getPieceAt(x_pos + x_dir, y_pos + y_dir);
                                    change += 1;
                                    first_order_combination[first_order_i] = true;
                                    break;

                                }
                                if (getPieceAt(x_pos + x_dir, y_pos + y_dir) == 0) { // if the destination is 0
                                    setPieceAt(x_pos + x_dir, y_pos + y_dir, getPieceAt(x_pos, y_pos));
                                    setPieceAt(x_pos, y_pos, 0);
                                    change += 1;
                                    break;
                                }
                            }
                        }
                    }
                checked = true;
                // for (int i = 0; i < Math.abs(first_order - end_first_order); i++){
                //     combined = combined || first_order_combination[i];
                // }
                // if (combined) {
                //     break;
                // }
        } while(checked && change != 0);

        int tempNumPieces = 0;
        for (y_pos = 0; y_pos < getBoardHeight(); y_pos++) {
            for (x_pos = 0; x_pos < getBoardWidth(); x_pos++) {
                if (getPieceAt(x_pos, y_pos) > 0) {
                    tempNumPieces += 1;
                }
            }
        }
        
        this.numPieces = tempNumPieces;

        addPiece();

        setNumMoves();

        return true;
    }
 */
    
    public boolean performMove(MoveDirection direction) {
        int first_order;
        int end_first_order;
        int end_second_order;
        boolean increment_first_order;

        int x_dir = 0;
        int y_dir = 0;
        if (direction == null){
            throw new IllegalArgumentException("Invalid direction was given.");
        }

        switch (direction) {
            case NORTH:
                first_order = 1;
                end_first_order = getBoardHeight();
                end_second_order = getBoardWidth();
                increment_first_order = true;
                y_dir = -1;
                break;
            case SOUTH:
                first_order = getBoardHeight() - 2;
                end_first_order = -1;
                end_second_order = getBoardWidth();
                increment_first_order = false;
                y_dir = 1;
                break;
            case WEST:
                first_order = 1;
                end_first_order = getBoardWidth();
                end_second_order = getBoardHeight();
                increment_first_order = true;
                x_dir = -1;
                break;
            case EAST:
                first_order = getBoardWidth() - 2;
                end_first_order = -1;
                end_second_order = getBoardHeight();
                increment_first_order = false;
                x_dir = 1;
                break;
            default:
                throw new IllegalArgumentException("Invalid direction given!");
        }
        if (!isMovePossible(direction)) {
            return false;
        }

        boolean[][] combination = new boolean[getBoardHeight()][getBoardWidth()];// default value is false
        // int change;
        int y_pos;
        int x_pos;
        int temp_y_dir;
        int temp_x_dir;
        boolean move_possible;
        // boolean checked = false;

        for(int first_order_i = first_order; intCompare(first_order_i, end_first_order, increment_first_order);
                    first_order_i = inDecrementor(first_order_i, increment_first_order)) {
                        for (int second_order_i = 0; second_order_i < end_second_order; second_order_i++) {
                            x_pos = adapter(first_order_i, second_order_i, direction, "x_pos");
                            y_pos = adapter(first_order_i, second_order_i, direction, "y_pos");

                            if (getPieceAt(x_pos, y_pos) != 0){
                                // temp_first_order_pos = first_order_i;
                                move_possible = true;
                                temp_y_dir = 0;
                                temp_x_dir = 0;
                                while (intCompare(first_order_i + temp_y_dir + temp_x_dir, first_order + x_dir + y_dir, !increment_first_order)
                                    && !combination[y_pos + temp_y_dir + y_dir][x_pos + temp_x_dir + x_dir] && move_possible) {
                                        if (getPieceAt(x_pos + temp_x_dir , y_pos + temp_y_dir) 
                                            == getPieceAt(x_pos + temp_x_dir + x_dir, y_pos + temp_y_dir + y_dir)) {
                                                setPieceAt(x_pos + temp_x_dir + x_dir, y_pos + temp_y_dir + y_dir, getPieceAt(x_pos + temp_x_dir, y_pos + temp_y_dir) * 2);
                                                setPieceAt(x_pos + temp_x_dir, y_pos + temp_y_dir, 0);
                                                this.points += getPieceAt(x_pos + temp_x_dir + x_dir, y_pos + temp_y_dir + y_dir);
                                                combination[x_pos + temp_x_dir + x_dir][y_pos + temp_y_dir + y_dir] = true;
                                                break;
                                            }

                                        if (getPieceAt(x_pos + temp_x_dir + x_dir, y_pos + temp_y_dir + y_dir) == 0) {
                                                setPieceAt(x_pos + temp_x_dir + x_dir, y_pos + temp_y_dir + y_dir, getPieceAt(x_pos + temp_x_dir, y_pos + temp_y_dir));
                                                setPieceAt(x_pos + temp_x_dir, y_pos + temp_y_dir, 0);
                                                temp_x_dir += x_dir;
                                                temp_y_dir += y_dir;
                                                continue;
                                        }

                                        move_possible = false;
                                        
                                }
                            }
                        }
                    }
                    
        int tempNumPieces = 0;
        for (y_pos = 0; y_pos < getBoardHeight(); y_pos++) {
            for (x_pos = 0; x_pos < getBoardWidth(); x_pos++) {
                if (getPieceAt(x_pos, y_pos) > 0) {
                    tempNumPieces += 1;
                }
            }
        } 
        
        this.numPieces = tempNumPieces;

        addPiece();
        setNumMoves();
        return true;
    }
 
    public void run(PlayerInterface player, UserInterface ui) {
        if(player == null || ui == null) {
            throw new IllegalArgumentException("Player and/or UI are not initialized");
        }
        while (isMovePossible()) {
            ui.updateScreen(this);
            performMove(player.getPlayerMove(this, ui));
        }
        ui.updateScreen(this);
        ui.showGameOverScreen(this);
    }

    public void setPieceAt(int x, int y, int piece) {

        if (x < 0 || x > getBoardWidth() || y < 0 || y > getBoardHeight()){
            throw new IllegalArgumentException("Piece set at invalid coordinate");
        } 
        if (piece < 0) {
            throw new IllegalArgumentException("Piece have negative value");
        }
        this.board[y][x] = piece; 
    }

    private void setNumMoves() {
        this.numMoves += 1;
    }

    private Random getRandom() {
        return random;
    }



    // public void setPoints(int point) {
    //     this.points += point;
    // }

    // private int[][] getBoard() {
    //     return board;
    // }

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
