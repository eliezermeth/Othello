import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Eliezer Meth
 * @version 1
 * Start Date: 01.28.2020
 * Last Modified: 01.28.2020
 */

public class OthelloComputerTest
{
    // create private instance of class for tests
    private OthelloLogic ol = new OthelloLogic(8);
    private OthelloComputer oc = new OthelloComputer(ol, Cell.BLACK);

    @Test
    public void getColor()
    {
        assertEquals(Cell.BLACK, oc.getColor());
    }

    @Test
    public void makeMove()
    {
        int[] pos = {2, 3};
        assertArrayEquals(pos, oc.makeMove(ol));
    }
}