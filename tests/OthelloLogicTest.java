import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Eliezer Meth
 * @version 1
 * Start Date: 01.28.2020
 * Last Modified: 02.12.2020
 */

public class OthelloLogicTest
{
    // create private instance of class for tests
    private OthelloLogic ol = new OthelloLogic(8);

    @Test
    public void placePiece()
    {
        assertTrue(ol.placePiece(2, 3, Cell.BLACK)); // can place
        assertEquals(Cell.BLACK, ol.getCell(3,3)); // piece flipped
        assertFalse(ol.placePiece(0, 0, Cell.WHITE)); // try placing in middle of nowhere
    }

    @Test
    public void isValid()
    {
        assertTrue(ol.isValid(2, 3, Cell.BLACK));
        assertFalse(ol.isValid(1, 1, Cell.BLACK)); // away from all pieces
        assertFalse(ol.isValid(3, 3, Cell.BLACK)); // on top of another piece
    }

    @Test
    public void hasMove()
    {
        // beginning of game, both have valid moves
        assertTrue(ol.hasMove(Cell.BLACK));
        assertTrue(ol.hasMove(Cell.WHITE));
    }

    @Test
    public void captureNum()
    {
        assertEquals(1, ol.captureNum(2,3, Cell.BLACK));
        assertNotEquals(2, ol.captureNum(5, 4, Cell.BLACK)); // valid move; returns 1
    }

    @Test
    public void getBoardSize() // constructor used with 8 passed in
    {
        assertEquals(8, ol.getBoardSize());
        assertNotEquals(3, ol.getBoardSize());
    }

    @Test
    public void getCell()
    {
        assertEquals(Cell.WHITE, ol.getCell(3, 3));
        assertEquals(Cell.BLACK, ol.getCell(3, 4));
        assertEquals(Cell.EMPTY, ol.getCell(3, 5));
    }

    @Test
    public void getScore()
    {
        // since called at beginning of game, should return [2, 2]
        int[] beginning = {2, 2};
        assertArrayEquals(ol.getScore(), beginning);
        int[] afterFirstMove = {4, 1};
        ol.placePiece(2, 3, Cell.BLACK);
        assertArrayEquals(ol.getScore(), afterFirstMove);
    }

    @Test
    public void getPlayerTurn()
    {
        // first move
        assertEquals(1, ol.getPlayerTurn()); // 1 = black
        ol.placePiece(2, 3, Cell.BLACK); // valid move; will increment player turn
        assertEquals(-1, ol.getPlayerTurn()); // -1 = white
    }
}