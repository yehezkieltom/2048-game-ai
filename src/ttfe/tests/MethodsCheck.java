package ttfe.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import ttfe.MoveDirection;
import ttfe.SimulatorInterface;
import ttfe.TTFEFactory;

public class MethodsCheck {
    private SimulatorInterface game;

    @Before
    public void setUp() {
        game = TTFEFactory.createSimulator(4, 4, new Random());
    }
    //Konstruktor Test
    @Test
    public void testInitialPoint() {
        assertEquals("The initial state did not have zero points", 0,
                game.getPoints());
    }

    @Test
    public void testInitialPieces() {
        assertEquals("The initial piece on board is not 2", 2,
                game.getNumPieces());
    }
    @Test
    public void testInitialPieceValue() {
        int invalid_value = 0;
        for (int y_pos = 0; y_pos < game.getBoardHeight(); y_pos++) {
            for (int x_pos = 0; x_pos < game.getBoardWidth(); x_pos++) {
                if (game.getPieceAt(x_pos, y_pos) == 2 || game.getPieceAt(x_pos, y_pos) == 4
                    || game.getPieceAt(x_pos, y_pos)  == 0){
                        continue;
                }
                invalid_value += 1;
            }
        }

        assertEquals("One or more initial piece(s) have invalid value", 0, invalid_value);
    }

    //addPiece Test
    @Test
    public void testValidAddedPiece() {

        int invalid_value = 0;
        int[][] initial_board = new int[game.getBoardHeight()][game.getBoardWidth()];
        // int[][] current_board = new int[game.getBoardHeight()][game.getBoardWidth()];

        //snapshot of initial state
        for (int y_pos = 0; y_pos < game.getBoardHeight(); y_pos++) {
            for (int x_pos = 0; x_pos < game.getBoardWidth(); x_pos++) {
                initial_board[y_pos][x_pos] = game.getPieceAt(x_pos, y_pos);
            }
        }

        // //check which move is possible
        // for (MoveDirection dir: MoveDirection.values()) {
        //     game.performMove(dir);
        //     if (game.getNumMoves() == 1) {
        //         break;
        //     }
        // }
        int expected_piece_num = game.getNumPieces() + 1;
        game.addPiece();
        assertEquals("Piece not added correctly, current piece should be " + expected_piece_num + ", got " + game.getNumPieces() + ".",
                expected_piece_num, game.getNumPieces());

        for (int y_pos = 0; y_pos < game.getBoardHeight(); y_pos++) {
            for (int x_pos = 0; x_pos < game.getBoardWidth(); x_pos++){
                // current_board[y_pos][x_pos] = game.getPieceAt(x_pos, y_pos);

                if (initial_board[y_pos][x_pos] == 0 && 
                    !(game.getPieceAt(x_pos, y_pos) == 2 || game.getPieceAt(x_pos, y_pos) == 4
                    || game.getPieceAt(x_pos, y_pos) == 0)) {
                        invalid_value += 1;
                }
            }
        }

        assertEquals("Invalid Piece Value added to board", 0, invalid_value);

    }

    //getNumMoves() Tests
    @Test
    public void testInitialgetNumMoves() {
        assertEquals("getNumMoves() at initial state is  expected to return 0, got " + game.getNumMoves(), 0, game.getNumMoves());
    }

    @Test
    public void testOneMoveGetNumMoves() {
        //check which move is possible
        if (game.isMovePossible(MoveDirection.NORTH) == true) {
            game.performMove(MoveDirection.NORTH);
        } else if (game.isMovePossible(MoveDirection.SOUTH) == true) {
            game.performMove(MoveDirection.SOUTH);
        } else if (game.isMovePossible(MoveDirection.WEST) == true) {
            game.performMove(MoveDirection.WEST);
        } else {
            game.performMove(MoveDirection.EAST);
        }
        assertEquals("getNumMoves() after single move is expected to return 1, got " + game.getNumMoves(), 1, game.getNumMoves());
    }

    //getNumPieces() Tests

    @Test
    public void testInitialGetNumPieces() {
        int filled_spot = 0;
        for (int y_pos = 0; y_pos < game.getBoardHeight(); y_pos++){
            for (int x_pos = 0; x_pos < game.getBoardWidth(); x_pos++){
                if (game.getPieceAt(x_pos, y_pos) != 0) {
                    filled_spot += 1;
                }
            }
        }

        assertEquals("getNumPieces() at initial state is expected to return 2, got " + filled_spot, 2, filled_spot);
    }

    //getPoints() Tests

    @Test
    public void testInitialGetPoints() {
        assertEquals("getPoints() at initial state is expected to return 0, got" + game.getPoints(), 0, game.getPoints());
    }

    @Test //broken because of setPoints
    public void testPairs2GetPoints() {
        boardCleanUp(0, game.getBoardWidth(), 0, game.getBoardHeight(), game);
        game.setPieceAt(0, 0, 2);
        game.setPieceAt(0, 1, 2);
        game.performMove(MoveDirection.NORTH);
        assertEquals("getPoints() after pairing pieces of 2 expected to return 4, got " + game.getPoints(), 4, game.getPoints());
    }

    //isSpaceLeft() Test

    @Test
    public void testInitialIsSpaceLeft() {
        assertTrue("isSpaceLeft() at initial state is expected to return true", game.isSpaceLeft());
    }

    @Test
    public void testFilledIsSpaceLeft() {
        for (int i = 2; i < (game.getBoardHeight()*game.getBoardWidth()); i++){
            game.addPiece();
        }
        assertFalse("isSpaceLeft() at filled state is expected to return false", game.isSpaceLeft());
    }
    //isMovePossible() Test
    @Test
    public void testIsMovePossibleAvailableSpot(){
        assertTrue("isMovePossible() expected to return true.", game.isMovePossible());
    }

    @Test
    public void testIsMovePossibleIdenticalPieces(){
        boardCleanUp(0, game.getBoardWidth(), 0, game.getBoardHeight(), game);
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++){
                game.setPieceAt(x, y, 2);
            }
        }
        assertTrue("isMovePossible() expected to return true.", game.isMovePossible());
    }

    @Test
    public void testIsMovePossibleCheckerboardPieces(){
        boardCleanUp(0, game.getBoardWidth(), 0, game.getBoardHeight(), game);
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++){
                if (y % 2 == 0){
                    if (x % 2 == 0) {
                        game.setPieceAt(x, y, 2);
                    } else {
                        game.setPieceAt(x, y, 4);
                    }
                } else {
                    if (x % 2 == 0) {
                        game.setPieceAt(x, y, 4);
                    } else {
                        game.setPieceAt(x, y, 2);
                    }
                }
                
            }
        }
        assertFalse("isMovePossible() expected to return false.", game.isMovePossible());
    }
    //isMovePossible(dir) Test
    @Test
    public void testIsMovePossibleNorth() {
        //cleanup
        boardCleanUp(0, game.getBoardWidth(), 0, game.getBoardHeight(), game);

        game.setPieceAt(0, game.getBoardHeight() - 1, 2);
        game.setPieceAt(0, game.getBoardHeight() - 2, 2);

        assertTrue("Expected to return true before move performed", game.isMovePossible(MoveDirection.NORTH));

        game.performMove(MoveDirection.NORTH);

        boardCleanUp(0, game.getBoardWidth(), 1, game.getBoardHeight(), game);

        assertFalse("Expected to return false after move performed and cleanup", game.isMovePossible(MoveDirection.NORTH));
    }

    @Test
    public void testIsMovePossibleWest() {
        //cleanup
        boardCleanUp(0, game.getBoardWidth(), 0, game.getBoardHeight(), game);

        game.setPieceAt(game.getBoardWidth() - 1, 0, 2);
        game.setPieceAt(game.getBoardWidth() - 2, 0, 2);

        assertTrue("Expected to return true before move performed", game.isMovePossible(MoveDirection.WEST));

        game.performMove(MoveDirection.WEST);

        boardCleanUp(1, game.getBoardWidth(), 0, game.getBoardHeight(), game);

        assertFalse("Expected to return false after move performed and cleanup", game.isMovePossible(MoveDirection.WEST));
    }

    @Test
    public void testIsMovePossibleEast() {
        //cleanup
        boardCleanUp(0, game.getBoardWidth(), 0, game.getBoardHeight(), game);

        game.setPieceAt(0, 0, 2);
        game.setPieceAt(1, 0, 2);

        assertTrue("Expected to return true before move performed", game.isMovePossible(MoveDirection.EAST));

        game.performMove(MoveDirection.EAST);

        boardCleanUp(0, game.getBoardWidth() - 1, 0, game.getBoardHeight(), game);

        assertFalse("Expected to return false after move performed and cleanup", game.isMovePossible(MoveDirection.EAST));
    }

    @Test
    public void testIsMovePossibleSouth() {
        //cleanup
        boardCleanUp(0, game.getBoardWidth(), 0, game.getBoardHeight(), game);

        game.setPieceAt(0, 0, 2);
        game.setPieceAt(0, 1, 2);

        assertTrue("Expected to return true before move performed", game.isMovePossible(MoveDirection.SOUTH));

        game.performMove(MoveDirection.SOUTH);

        boardCleanUp(0, game.getBoardWidth(), 0, game.getBoardHeight() - 1, game);

        assertFalse("Expected to return false after move performed and cleanup", game.isMovePossible(MoveDirection.SOUTH));
    }
    @Test 
    public void testIsMovePossibleNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> 
            game.isMovePossible(null));
        assertNotNull(exception);
    }
    // performMove() test
    @Test
    public void testPerformMove3Pieces2InARoW() {
        boardCleanUp(0, game.getBoardWidth(), 0, game.getBoardHeight(), game);
        //tf should i test here???? ---consult notes
        game.setPieceAt(0,0,2);
        game.setPieceAt(0,1,2);
        game.setPieceAt(0,2,2);
        game.performMove(MoveDirection.NORTH);
        assertEquals("Expected to combined the piece into 4, have instead " + game.getPieceAt(0, 0), 4, game.getPieceAt(0, 0));
        
    }
    @Test
    public void testPerformMove224PiecesInARow() {
        boardCleanUp(0, game.getBoardWidth(), 0, game.getBoardHeight(), game);
        game.setPieceAt(0,0,2);
        game.setPieceAt(0,1,2);
        game.setPieceAt(0,2,4);
        game.performMove(MoveDirection.NORTH);
        assertEquals("Expected to combined the piece into 4, have instead " + game.getPieceAt(0, 0), 4, game.getPieceAt(0, 0));
        
    }

    @Test
    public void testPerformMove() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> 
            game.performMove(null));
        assertNotNull(exception);
    }

    @Test
    public void testMerging2248() {
        boardCleanUp(0, game.getBoardWidth(), 0, game.getBoardHeight(), game);
        game.setPieceAt(0, 0, 2);
        game.setPieceAt(1, 0, 2);
        game.setPieceAt(2, 0, 4);
        game.setPieceAt(3, 0, 8);
        game.performMove(MoveDirection.EAST);
        assertEquals("Merging error", 3, game.getNumPieces());
    }

    @Test
    public void testMerging2224() {
        boardCleanUp(0, game.getBoardWidth(), 0, game.getBoardHeight(), game);
        game.setPieceAt(0, 0, 2);
        game.setPieceAt(1, 0, 2);
        game.setPieceAt(2, 0, 2);
        game.setPieceAt(3, 0, 4);
        game.performMove(MoveDirection.EAST);
        assertEquals("Merging error", 3, game.getNumPieces());
    }

    @Test
    public void testMultipleMerge1() {
        boardCleanUp(0, game.getBoardWidth(), 0, game.getBoardHeight(), game);
        game.setPieceAt(0, 0, 2);
        game.setPieceAt(1, 0, 2);
        game.setPieceAt(0, 1, 2);
        game.setPieceAt(1, 1, 2);
        game.setPieceAt(2, 1, 2);
        game.performMove(MoveDirection.EAST);
        assertEquals("Merging error", 3, game.getNumPieces());
    }

    @Test
    public void testMultipleMerge2() {
        boardCleanUp(0, game.getBoardWidth(), 0, game.getBoardHeight(), game);
        game.setPieceAt(0, 0, 4);
        game.setPieceAt(1, 0, 4);
        game.setPieceAt(0, 1, 4);
        game.setPieceAt(1, 1, 4);
        game.setPieceAt(2, 1, 4);
        game.performMove(MoveDirection.EAST);
        assertEquals("Merging error", 3, game.getNumPieces());
    }
    
    @Test
    public void testMultipleMerge3() {
        boardCleanUp(0, game.getBoardWidth(), 0, game.getBoardHeight(), game);
        game.setPieceAt(0, 0, 4);
        game.setPieceAt(1, 0, 4);
        game.setPieceAt(1, 1, 4);
        game.setPieceAt(2, 1, 4);
        game.setPieceAt(2, 2, 4);
        game.setPieceAt(3, 2, 4);
        game.performMove(MoveDirection.EAST);
        assertEquals("Merging error", 3, game.getNumPieces());
    }

    @Test
    public void testMultipleMerge4() {
        boardCleanUp(0, game.getBoardWidth(), 0, game.getBoardHeight(), game);
        game.setPieceAt(0, 0, 2);
        game.setPieceAt(1, 0, 2);
        game.setPieceAt(2, 0, 4);
        game.performMove(MoveDirection.WEST);
        assertEquals("Merging error", 2, game.getNumPieces());
    }

    private void boardCleanUp (int x_start, int x_end, int y_start, int y_end, SimulatorInterface game) {
        for (int y_pos = y_start; y_pos < y_end; y_pos++) {
            for (int x_pos = x_start; x_pos < x_end; x_pos++) {
                game.setPieceAt(x_pos, y_pos, 0);
            }
        }
    }
    

}