import java.awt.*;
import java.util.Random;
import java.util.Scanner;

/**
 * @author Eliezer Meth
 * @version 1.1
 * Start Date: 01.22.2020
 * Last Modified: 03.13.2020
 */

public class OthelloConsole
{
    public static void main(String[] args)
    {
        OthelloConsole console = new OthelloConsole();
        console.setupGame();
    }

    public void setupGame()
    {
        Scanner input = new Scanner(System.in);

        // get number of players
        int numPlayers = -1;
        boolean validNum = false;
        while (!validNum)
        {
            System.out.print("Enter number of player(s): ");
            //numPlayers = input.nextInt(); //- commented out for testing - put back in - *****************************
            numPlayers = 0;

            if (numPlayers < 0 || numPlayers > 2) // invalid number
                System.out.println("Invalid input.  Please try again.");
            else
                validNum = true;
        }

        // get size of board
        System.out.print("Enter board axis length: ");
        //int size = input.nextInt(); - commented out for testing - put back in - ************************************
        int size = 8;
        System.out.println();

        OthelloLogicInterface game;
        if (size < 0)
            game = new OthelloLogic(); // default
        else
            game = new OthelloLogic(size);

        playGame(game, numPlayers);
    }

    public void playGame(OthelloLogicInterface game, int numPlayers) // need to make it that can be PvP or PvC; currently only PvC & set color
    {
        // create players list
        OthelloComputer[] players = new OthelloComputer[2]; // null == human

        if (numPlayers == 0) // CvC; no need to randomize
        {
            players[0] = new OthelloComputer(game, Cell.BLACK);
            players[1] = new OthelloComputer(game, Cell.WHITE);
        }
        else if (numPlayers == 1)
        {
            // get random position
            Random random = new Random();
            int position = random.nextInt(2); // 0 or 1

            if (position == 0)
                players[0] = new OthelloComputer(game, Cell.BLACK);
            else // 1
                players[1] = new OthelloComputer(game, Cell.WHITE);
        }
        // if (numPlayers == 2), both are human and left as null

        printBoard(game);

        while (game.getPlayerTurn() != 0)
        {
            if (game.getPlayerTurn() == 1) // black
            {
                if (players[0] == null) // human
                    getMove(game, Cell.BLACK);
                else // computer
                {
                    Point cell = players[0].minimax(game, 0);
                    System.out.println("(Computer) Black placing piece at " + ((char) (cell.y + 65)) + (cell.x + 1) + ".");
                    game.placePiece(cell.x, cell.y, Cell.BLACK);
                }
            }
            else  // -1 = white
            {
                if (players[1] == null) // human
                    getMove(game, Cell.WHITE);
                else // computer
                {
                    Point cell = players[1].minimax(game, 2);
                    System.out.println("(Computer) White placing piece at " + ((char) (cell.y + 65)) + (cell.x + 1) + ".");
                    game.placePiece(cell.x, cell.y, Cell.WHITE);
                }

            }
            printBoard(game);
        }

        printResults(game);
    }

    public boolean getMove(OthelloLogicInterface game, Cell turn) // return statement is worthless
    {
        Scanner input = new Scanner(System.in);
        String space;

        int row = 0; // initialize; will be changed in loop
        int col = 0; // initialize; will be changed in loop
        boolean validMove = false;
        while (!validMove)
        {
            System.out.print("Enter cell to place " + turn.toString().toLowerCase() + "  piece: ");
            space = input.nextLine();
            // human will input column then row
            col = (int) space.charAt(0) - 65;
            row = (int) space.charAt(1) - 49; // compensate with extra -1 since human input

            validMove = game.isValid(row, col, turn);
            if (!validMove)
                System.out.println("Invalid input.  Please try again.");
        }

        game.placePiece(row, col, turn);
        return true;
    }

    public void printResults(OthelloLogicInterface game)
    {
        int[] numPieces = game.getScore();

        System.out.println("Black pieces: " + numPieces[0]);
        System.out.println("White pieces: " + numPieces[1]);

        if (numPieces[0] > numPieces[1])
            System.out.println("Black wins.");
        else if (numPieces[0] < numPieces[1])
            System.out.println("White wins.");
        else
            System.out.println("Tie game.");
    }

    public void printBoard(OthelloLogicInterface game)
    {
        int boardSize = game.getBoardSize();
        String empty = " ";     //"\u0020"; // space                   "\u25A0"; // black large square
        String black = "X";     //"\u2B24"; // black large circle      "\u25D8"; // inverse bullet
        String white = "O";     //"\u25EF"; // large circle            "\u25D9"; // inverse white circle

        StringBuilder list = new StringBuilder();

        for (int i = 0; i < boardSize; i++)
        {
            if (i == 0)
            {
                for (int x = 0; x < boardSize; x++)
                {
                    if (x == 0)
                        list.append("    ");
                    list.append((char) (x + 65)); // letter
                    list.append("   "); // space after letter
                }
                list.append("\n");
                list.append(getLine(boardSize));
            }

            for (int j = 0; j < boardSize; j++)
            {
                if (j == 0)
                    list.append(i + 1);
                list.append(" | ");
                if (game.getCell(i, j) == Cell.BLACK)
                    list.append(black);
                else if (game.getCell(i, j) == Cell.WHITE)
                    list.append(white);
                else
                    list.append(empty);
            }
            list.append(" |");
            list.append("\n");
            list.append(getLine(boardSize));
        }
        System.out.println(list.toString());
    }

    private String getLine(int boardSize)
    {
        StringBuilder line = new StringBuilder();
        line.append("  ");
        for (int k = 0; k < boardSize * 4; k++)
        {
            line.append("-");
        }
        line.append("\n");

        return line.toString();
    }
}
// update with getPlayerTurn();