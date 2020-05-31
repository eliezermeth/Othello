/**
 * @author Eliezer Meth
 * @version 1
 * Start Date: 02.08.2020
 * Last Modified: 03.09.2020
 */

public interface OthelloLogicInterface
{
    boolean placePiece(int row, int col, Cell piece); // put down piece
    boolean isValid(int row, int col, Cell piece); // check if move is valid
    boolean hasMove(Cell piece); // check if color has move
    int captureNum(int row, int col, Cell piece); // see how many pieces a placement here will take
    int getBoardSize(); // return board size
    Cell getCell(int row, int col); // get occupant of cell
    int[] getScore(); // return [black, white] total pieces on board
    int getPlayerTurn(); // returns 1 for black, -1 for white, 0 for game over
    OthelloLogicInterface getCopyGameState();
}
