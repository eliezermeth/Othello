import java.util.Arrays;

/**
 * @author Eliezer Meth
 * @version 1.1
 * Start Date: 01.22.2020
 * Last Modified: 03.09.2020
 */

/*
 * http://www.ffothello.org/livres/beginner-Randy-Fang.pdf
 *      A    B    C    D    E    F    G    H
 *    -----------------------------------------
 * 1  |    |    |    |    |    |    |    |    |
 *    |---------------------------------------|
 * 2  |    |    |    |    |    |    |    |    |
 *    |---------------------------------------|
 * 3  |    |    |    |    |    |    |    |    |
 *    |---------------------------------------|
 * 4  |    |    |    | W  |  B |    |    |    |
 *    |---------------------------------------|
 * 5  |    |    |    | B  |  W |    |    |    |
 *    |---------------------------------------|
 * 6  |    |    |    |    |    |    |    |    |
 *    |---------------------------------------|
 * 7  |    |    |    |    |    |    |    |    |
 *    |---------------------------------------|
 * 8  |    |    |    |    |    |    |    |    |
 *    -----------------------------------------
 */

enum Cell {EMPTY, BLACK, WHITE}

public class OthelloLogic implements OthelloLogicInterface, Cloneable
{
    private Cell[][] board;
    private int[][] possPosition; // for quick isValid(); 0 = empty/not valid; 1 = empty/valid; 2 = occupied
    private final int boardSize;

    // for player rotation
    private int playerTurn;
    private boolean playerSkipped;

    public OthelloLogic() // default constructor
    {
        this(8);
    }

    public OthelloLogic(int num)
    {
        if (num % 2 != 0 && num < 1) // if num not even or <1, default
            num = 8;

        boardSize = num;
        board = new Cell[boardSize][boardSize];
        possPosition = new int[boardSize][boardSize];

        for (Cell[] c : board)
        {
            Arrays.fill(c, Cell.EMPTY);
        }

        // assume input board size is even
        // create starting positions
        board[(boardSize/2)-1][(boardSize/2)-1] = Cell.WHITE;
        board[(boardSize/2)-1][(boardSize/2)] = Cell.BLACK;
        board[(boardSize/2)][(boardSize/2)-1] = Cell.BLACK;
        board[(boardSize/2)][(boardSize/2)] = Cell.WHITE;
        // update possPosition array
        updatePossPosition((boardSize/2)-1, (boardSize/2)-1);
        updatePossPosition((boardSize/2)-1, (boardSize/2));
        updatePossPosition((boardSize/2), (boardSize/2)-1);
        updatePossPosition((boardSize/2), (boardSize/2));

        // set up player rotation
        playerTurn = 1;
        playerSkipped = false;
    }

    private OthelloLogic(Cell[][] board, int[][] possPosition, int playerTurn, boolean playerSkipped) // only for getCopyGameState
    {
        this.board = getBoardDeepCopy(board);
        this.boardSize = board.length;
        this.possPosition = getPossDeepCopy(possPosition);
        this.playerTurn = playerTurn;
        this.playerSkipped = playerSkipped;
    }
    private Cell[][] getBoardDeepCopy(Cell[][] board)
    {
        Cell[][] constructing = new Cell[board.length][board.length];
        for (int i = 0; i < board.length; i++)
        {
            for (int j = 0; j < board.length; j++)
            {
                constructing[i][j] = board[i][j];
            }
        }
        return constructing;
    } // cannot combine deep copy methods since one Cell and other int
    private int[][] getPossDeepCopy(int[][] possPosition)
    {
        int[][] constructing = new int[possPosition.length][possPosition.length];
        for (int i = 0; i < possPosition.length; i++)
        {
            for (int j = 0; j < possPosition.length; j++)
            {
                constructing[i][j] = possPosition[i][j];
            }
        }
        return constructing;
    }

    @Override // interface
    public boolean placePiece(int row, int col, Cell piece) // assume computer numbers
    {
        if (isValid(row, col, piece))
        {
            board[row][col] = piece;
            updatePossPosition(row, col);
            checkFlip(row, col, piece);
            playerTurn *= -1; // flip playerTurn
            return true;
        }
        else
            return false;
    } // return only used by unit test, delete or use?

    @Override // interface
    public boolean isValid(int row, int col, Cell piece) // assume computer numbers
    {

        if (row < 0 || row > boardSize - 1 || col < 0 || col > boardSize-1) // short-circuit; number out of array
            return false;

        if (possPosition[row][col] != 1) // if empty square not next to occupied square
            return false;

        int totalCapture = captureNum(row, col, piece);
        if (totalCapture == 0)
            return false;
        else
            return true;
    }


    @Override // interface
    public boolean hasMove(Cell piece)
    {
        boolean possMove = false;

        for (int i = 0; i < boardSize && !possMove; i++)
        {
            for (int j = 0; j < boardSize && !possMove; j++)
            {
                if (isValid(i, j, piece))
                    possMove = true;
            }
        }

        return possMove;
    }

    private void updatePossPosition(int row, int col)
    {
        possPosition[row][col] = 2; // filled
        updatePossPositionUtil(row-1, col-1); // NW
        updatePossPositionUtil(row-1, col); // N
        updatePossPositionUtil(row-1, col+1); // NE
        updatePossPositionUtil(row, col-1); // W
        updatePossPositionUtil(row, col+1); // E
        updatePossPositionUtil(row+1, col-1); // SW
        updatePossPositionUtil(row+1, col); // S
        updatePossPositionUtil(row+1, col+1); // SE
    }

    private void updatePossPositionUtil(int row, int col)
    {
        if (row >= 0 && row < boardSize && col >= 0 && col < boardSize) // in array
        {
            if (possPosition[row][col] == 0) // was not touching any pieces
                    possPosition[row][col] = 1; // set as possible valid position
        }
    }

    @Override // interface
    public int captureNum(int row, int col, Cell piece) // assume computer number && valid square
    {
        int total = 0;
        total += captureNumUtil(row, col, piece, -1, -1, 0); // NW
        total += captureNumUtil(row, col, piece, -1, 0, 0); // N
        total += captureNumUtil(row, col, piece, -1, 1, 0); // NE
        total += captureNumUtil(row, col, piece, 0, -1, 0); // W
        total += captureNumUtil(row, col, piece, 0, 1, 0); // E
        total += captureNumUtil(row, col, piece, 1, -1, 0); // SW
        total += captureNumUtil(row, col, piece, 1, 0, 0); // S
        total += captureNumUtil(row, col, piece, 1, 1, 0); // SE
        return total;
    }

    private int captureNumUtil(int row, int col, Cell piece, int rowDir, int colDir, int numCap) // assume computer number
    {
        if (row+rowDir < 0 || row+rowDir > boardSize - 1 || col+colDir < 0 || col+colDir > boardSize - 1) // short-circuit; will be out of board
            return 0;
        else if (board[row+rowDir][col+colDir] == Cell.EMPTY) // empty cell in direction
            return 0;
        else if ((board[row+rowDir][col+colDir] == piece)) // own piece
            return numCap;
        else // (board[row+rowDir][col+colDir] == returnOpponent(piece)) // other player's piece
            return captureNumUtil(row+rowDir, col+colDir, piece, rowDir, colDir, numCap+1);
    }

    private void checkFlip(int row, int col, Cell piece)
    {
        checkFlipUtil(row, col, piece, -1, -1); // NW
        checkFlipUtil(row, col, piece, -1, 0); // N
        checkFlipUtil(row, col, piece, -1, 1); // NE
        checkFlipUtil(row, col, piece, 0, -1); // W
        checkFlipUtil(row, col, piece, 0, 1); // E
        checkFlipUtil(row, col, piece, 1, -1); // SW
        checkFlipUtil(row, col, piece, 1, 0); // S
        checkFlipUtil(row, col, piece, 1, 1); // SE
    }

    private void checkFlipUtil(int row, int col, Cell piece, int rowDir, int colDir)
    {
        if (row+rowDir < 0 || row+rowDir > boardSize - 1 || col+colDir < 0 || col+colDir > boardSize - 1) // short-circuit; out of board
            return; // break; for readability
        else if (board[row+rowDir][col+colDir] == Cell.EMPTY) // empty cell in direction
            return; // break; for readability
        else if (board[row+rowDir][col+colDir] == piece) // reached other end capture piece
            flipCell(row, col, piece, rowDir*(-1), colDir*(-1)); // flip directions to move
        else // opponent's piece
            checkFlipUtil(row+rowDir, col+colDir, piece, rowDir, colDir);
    }

    private void flipCell(int row, int col, Cell piece, int rowDir, int colDir)
    {
        if (board[row][col] != piece) // will break when encounters same piece
        {
            board[row][col] = piece;
            flipCell(row+rowDir, col+colDir, piece, rowDir, colDir);
        }
    }

    @Override // interface
    public int getBoardSize() { return boardSize; }

    @Override // interface
    public Cell getCell(int row, int col) { return board[row][col]; } // assumes computer numbers

    private boolean isOpponent(Cell piece, Cell opponent)
    {
        if (opponent != null && opponent != piece)
            return true;
        else
            return false;
    }

    private Cell returnOpponent(Cell piece) // for more complex version
    {
        if (piece == Cell.BLACK)
            return Cell.WHITE;
        else
            return Cell.BLACK;
    }

    @Override // interface
    public int[] getScore()
    {
        int[] numPieces = {0, 0}; // [black, white]

        for (int i = 0; i < boardSize; i++)
        {
            for (int j = 0; j < boardSize; j++)
            {
                if (board[i][j] == Cell.BLACK)
                    numPieces[0]++;
                else if (board[i][j] == Cell.WHITE)
                    numPieces[1]++;
            }
        }

        return numPieces;
    }

    @Override // interface
    public int getPlayerTurn()
    {
        Cell turn;
        if (playerTurn == 1) // black
            turn = Cell.BLACK;
        else // white
            turn = Cell.WHITE;

        if (hasMove(turn))
        {
            playerSkipped = false;
            return playerTurn;
        }
        else // color cannot move
        {
            if (!playerSkipped) // previous color went
            {
                playerSkipped = true;
                playerTurn *= -1;
                return getPlayerTurn(); // run method again;
            }
            else // both colors cannot move
                return 0; // game over
        }
    }

    @Override //  interface
    public OthelloLogicInterface getCopyGameState()
    {
        return new OthelloLogic(board, possPosition, playerTurn, playerSkipped);
    }
}
// rewrite using playerTurn instead of Cell piece?