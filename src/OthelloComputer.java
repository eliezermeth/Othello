import java.awt.*;
import java.util.ArrayList;

/**
 * @author Eliezer Meth
 * @version 1.5
 * Start Date: 01.26.2020
 * Last Modified: 03.09.2020
 */

public class OthelloComputer
{
    private Cell piece;
    private Cell opponent; // for when minimax implemented

    int boardSize;

    private OthelloComputer()    { } // make default constructor private

    public OthelloComputer(OthelloLogicInterface game, Cell piece) // game required for boardSize; does not trust input
    {
        this.piece = piece;

        if (piece == Cell.BLACK)
            opponent = Cell.WHITE;
        else
            opponent = Cell.BLACK;

        boardSize = game.getBoardSize();
    }

    public Cell getColor() // to see computer color
    {
        return piece;
    }

    public int[] makeMove(OthelloLogicInterface game) // only called if there is a valid move
    {
        int[] cell = {-1, -1};
        int mostCaptured = 0;

        for (int i = 0; i < boardSize; i++)
        {
            for (int j = 0; j < boardSize; j++)
            {
                if (game.isValid(i, j, piece)) // valid move
                {
                    int capture = game.captureNum(i, j, piece);
                    if (capture > mostCaptured)
                    {
                        cell[0] = i;
                        cell[1] = j;
                        mostCaptured = capture;
                    }
                }
            }
        }

        return cell;
    }

    // Error when toDepth is 2+; list passed is zero in function min/max(minimaxUtil(...)); rewrite minimaxUtil outside and then pass
    public Point minimax(OthelloLogicInterface game, int toDepth) // only called if there is a vaild move
    {
        // Top layer; saved to return Point
        setPiece(game);
        ArrayList<Point> arrayPoint = new ArrayList<>();
        ArrayList<OthelloLogicInterface> arrayGame = new ArrayList<>();

        // Try all positions and create gameBoards
        for (int i = 0; i < game.getBoardSize(); i++)
        {
            for (int j = 0; j < game.getBoardSize(); j++)
            {
                if (game.isValid(i, j, piece))
                {
                    arrayPoint.add(new Point(i, j));
                    OthelloLogicInterface copy = game.getCopyGameState();
                    copy.placePiece(i, j, piece);
                    arrayGame.add(copy);
                }
            }
        }

        ArrayList<Integer> possScores = new ArrayList<>();
        if (toDepth == 0)
        {
            for (int i = 0; i < arrayGame.size(); i++)
            {
                possScores.add(getCalculatedScore(arrayGame.get(i)));
            }
        }
        else // look into future
        {
            for (int i = 0; i < arrayGame.size(); i++)
            {
                if (arrayGame.get(i).getPlayerTurn()  != 0)
                    possScores.add(minimaxUtil(arrayGame.get(i), toDepth-1));
                else
                    possScores.add(getCalculatedScore(arrayGame.get(i)));
            }
        }

        int bestScoreIndex = 0;
        int bestScore = possScores.get(bestScoreIndex);
        if (game.getPlayerTurn() == 1) // black; maximizer
        {
            for (int i = 1; i < possScores.size(); i++)
            {
                if (possScores.get(i) > bestScore)
                {
                    bestScoreIndex = i;
                    bestScore = possScores.get(bestScoreIndex);
                }
            }
        }
        else // white; minimizer
        {
            for (int i = 1; i < possScores.size(); i++)
            {
                if (possScores.get(i) < bestScore)
                {
                    bestScoreIndex = i;
                    bestScore = possScores.get(bestScoreIndex);
                }
            }
        }

        return arrayPoint.get(bestScoreIndex);
    }

    public int minimaxUtil(OthelloLogicInterface game, int toDepth) // return int? // only called if there is a valid move
    {
        setPiece(game);
        ArrayList<OthelloLogicInterface> arrayGame = new ArrayList<>();

        // Try all positions and create gameBoards
        for (int i = 0; i < game.getBoardSize(); i++)
        {
            for (int j = 0; j < game.getBoardSize(); j++)
            {
                if (game.isValid(i, j, piece))
                {
                    OthelloLogicInterface copy = game.getCopyGameState();
                    copy.placePiece(i, j, piece);
                    arrayGame.add(copy);
                }
            }
        }

        if (toDepth == 0) // at bottom of recursion
        {
            int index = 0;
            int bestScore = 0; // will be overwritten

            if (game.getPlayerTurn() == 1) // black; maximizer
            {
                bestScore = Integer.MIN_VALUE; // set at worst possible number
                do {
                    int score = getCalculatedScore(arrayGame.get(index));
                    if (score > bestScore)
                        bestScore = score;
                    index++;
                } while (index < arrayGame.size());
            }
            else // white; minimizer
            {
                bestScore = Integer.MAX_VALUE; // set at worst possible number
                do {
                    int score = getCalculatedScore(arrayGame.get(index));
                    if (score < bestScore)
                        bestScore = score;
                    index++;
                } while (index < arrayGame.size());
            }
            // ---------------- worry about Cell.EMPTY?
            return bestScore;
        }
        else // still need recursion
        {
            ArrayList<Integer> possScores = new ArrayList<>();

            // Get scores from levels under
            for (int i = 0; i < arrayGame.size(); i++)
            {
                if (arrayGame.get(i).getPlayerTurn()  != 0)
                    possScores.add(minimaxUtil(arrayGame.get(i), toDepth-1));
                else
                    possScores.add(getCalculatedScore(arrayGame.get(i)));
            }

            // return best possible score
            if (game.getPlayerTurn() == 1) // black; maximizer
                return max(possScores); // -------------------------------------- need to check list size >= 0
            else if (game.getPlayerTurn() == -1) // white; minimizer
                return min(possScores); // -------------------------------------- need to check list size >= 0
            else // == 0; game over
            {
                System.out.println("ERROR ERROR ERROR ERROR ERROR ERROR ERROR ERROR");
                System.exit(-1);
                return 0; // never reached
            }
        }
    }

    private void setPiece(OthelloLogicInterface game)
    {
        if (game.getPlayerTurn() == 1)
            piece = Cell.BLACK;
        else if (game.getPlayerTurn() == -1)
            piece = Cell.WHITE;
        else // playerTurn = 0; game over
            piece = Cell.EMPTY; // holding
    }

    private int getCalculatedScore(OthelloLogicInterface game) // make other methods required for interface?
    {
        int[] scoreArray = game.getScore();
        int score = scoreArray[0] - scoreArray[1]; // black - white
        if (game.getPlayerTurn() == 0) // game over
        {
            if (score > 0)
                score += 100;
            else if (score < 0)
                score -= 100;
            // score = 0 does not matter
        }
        return score;
    }

    private int max(ArrayList<Integer> list)
    {
        int best = list.get(0);
        for (int i = 1; i < list.size(); i++) // only starts when list longer than 1
        {
            if (list.get(i) > best)
                best = list.get(i);
        }
        return best;
    }
    private int min(ArrayList<Integer> list)
    {
        int best = list.get(0);
        for (int i = 1; i < list.size(); i++) // only starts when list longer than 1
        {
            if (list.get(i) < best)
                best = list.get(i);
        }
        return best;
    }
}
// update with getPlayerTurn(); ?

class ScorePoint
{
    private Point point;
    private int score;

    private ScorePoint() { } // private so unable to be called

    public ScorePoint(Point p, int s)
    {
        point = p;
        score = s;
    }

    public Point getPoint() { return point; }
    public void setPoint(Point p) { point = p; }

    public int getScore() { return score; }
}