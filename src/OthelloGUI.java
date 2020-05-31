import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 * @author Eliezer Meth
 * @version 1.5
 * Start Date: 01.29.2020
 * Last Modified: 02.12.2020
 */

public class OthelloGUI extends JFrame
{
    private OthelloLogicInterface game;
    private BoardCell[][] arrayLabels;
    // private JTransparentButton[][] arrayButtons // must make JTransparentButton class
    // set pieces
    private String black;
    private String white;
    private JTextField keyboardInput;
    // player turn
    private int playerTurn;
    private Cell turnPiece;
    // game information
    private JInformationLabel turnInfo;
    private JInformationLabel blackScore;
    private JInformationLabel whiteScore;

    public static void main(String[] args)
    {
        OthelloGUI og = new OthelloGUI(new OthelloLogic());
        og.setupGame();
    }

    public OthelloGUI(OthelloLogicInterface game)
    {
        // set defaults
        this.game = game;
        black = "X";
        white = "O";
    }

    public void setupGame()
    {
        // set window details
        int WINDOW_WIDTH = 900;
        int WINDOW_HEIGHT = 615;

        setTitle("Othello"); // set title
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT); // set size
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // create window panes
        createWindow();

        setVisible(true);

        // opening game methods
        populateBoard();
        playGame();
    }

    private void createWindow()
    {
        setLayout(new GridBagLayout());

        // Menu bar
        GridBagConstraints gbcMenu = new GridBagConstraints();
        gbcMenu.gridx = 0;
        gbcMenu.gridy = 0;
        gbcMenu.gridwidth = GridBagConstraints.REMAINDER; // or 3
        gbcMenu.fill = GridBagConstraints.HORIZONTAL;
        add(createMenuPanel(), gbcMenu);

        // Board section
        GridBagConstraints gbcBoard = new GridBagConstraints();
        gbcBoard.gridx = 0;
        gbcBoard.gridy = 1;
        gbcBoard.gridheight = GridBagConstraints.REMAINDER; // or 2
        gbcBoard.fill = GridBagConstraints.BOTH; // ???
        gbcBoard.anchor = GridBagConstraints.CENTER; // ???
        gbcBoard.weightx = 0.5;
        gbcBoard.weighty = 0.5;
        //window.add(createBoardPanel(), gbcBoard);
        add(createGamePanel(), gbcBoard);

        // Turn section
        GridBagConstraints gbcTurn = new GridBagConstraints();
        gbcTurn.gridx = 1;
        gbcTurn.gridy = 1;
        gbcTurn.fill = GridBagConstraints.HORIZONTAL;
        gbcTurn.anchor = GridBagConstraints.CENTER;
        gbcTurn.weightx = 0.16;
        gbcTurn.weighty = 0.3;
        add(createTurnPanel(), gbcTurn);

        // Score section
        GridBagConstraints gbcScore = new GridBagConstraints();
        gbcScore.gridx = 1;
        gbcScore.gridy = 2;
        gbcScore.gridheight = GridBagConstraints.REMAINDER; // or 1
        gbcScore.fill = GridBagConstraints.HORIZONTAL;
        gbcScore.anchor = GridBagConstraints.CENTER;
        gbcScore.weightx = 0.16;
        gbcScore.weighty = 0.3;
        add(createScorePanel(), gbcScore);
    }


    private JPanel createMenuPanel()
    {
        JPanel menu = new JPanel();
        menu.setPreferredSize(new Dimension(900, 50));
        menu.setBackground(Color.GREEN);

        JLabel menuLabel = new JLabel("Menu");
        menuLabel.setOpaque(true);
        menuLabel.setBackground(Color.LIGHT_GRAY);
        menu.add(menuLabel);

        return menu;
    }

    private JLayeredPane createGamePanel()
    {
        JLayeredPane panel = new JLayeredPane();
        panel.setLayout(null);

        Dimension d = new Dimension(550, 550);
        JPanel boardPanel = createBoardPanel(d);
        JPanel buttonPanel = createButtonPanel(d);
        panel.add(boardPanel, JLayeredPane.DEFAULT_LAYER);
        panel.add(buttonPanel, JLayeredPane.PALETTE_LAYER);

        return panel;
    }

    private JPanel createBoardPanel(Dimension preferredSize)
    {
        JPanelWithBackground boardPanel = new JPanelWithBackground("C:\\Users\\emeth\\IdeaProjects\\Othello\\wood.jpg", preferredSize);
        boardPanel.setPreferredSize(preferredSize);
        boardPanel.setLayout(new GridLayout(9, 9, 2, 2));

        arrayLabels = new BoardCell[8][8];
        for (int i = -1; i < 8; i++) // start -1 for letter and number spaces
        {
            for (int j = -1; j < 8; j++) // start -1 for letter and number spaces
            {
                if (i == -1) // letter row
                {
                    if (j >= 0) // start letters
                    {
                        char text = 'A';
                        boardPanel.add(new DisplayCell(Character.toString((char) (text + j))));
                    }
                    else // top left
                    {
                        JPanel space = new JPanel();
                        space.setOpaque(false); // invisible
                        space.add(new JLabel(""));
                        boardPanel.add(space);
                    }
                }
                else if (j == -1) // number column; top left taken care of
                {
                    boardPanel.add(new DisplayCell(Integer.toString(i+1)));
                }
                else
                {
                    BoardCell cell = new BoardCell();
                    boardPanel.add(cell);
                    arrayLabels[i][j] = cell;
                }
            }
        }
        return boardPanel; // can return since inherits from JPanel
    }

    private JPanel createButtonPanel(Dimension dimension) // not working
    {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(dimension);
        buttonPanel.setLayout(new GridLayout(9, 9, 2, 2));
        buttonPanel.setOpaque(false);
        /*
        JButton keyboardInput = new JButton("A");
        keyboardInput.addActionListener(new KeyboardInputButtonListener());
        buttonPanel.add(keyboardInput);
        keyboardInput.setVisible(true);
        */
        // create 64 buttons to overlay board grid and place in array, invisible until specifically set visible
        return buttonPanel;
    }

    // create method for dragging a colored circle to a cell in the grid and dropping it there for move; add to JLayeredPane

    private JPanel createTurnPanel()
    {
        JPanel turnPanel = new JPanel();
        turnPanel.setPreferredSize(new Dimension(350, 200));
        turnPanel.setBackground(Color.BLUE);
        turnPanel.setLayout(new BoxLayout(turnPanel, BoxLayout.Y_AXIS));

        JInformationLabel turnLabel = new JInformationLabel("Turn:", Color.BLACK);
        turnPanel.add(turnLabel);

        turnInfo = new JInformationLabel("STARTING GAME", Color.BLACK); // starting text never seen; change?
        turnPanel.add(turnInfo);

        keyboardInput = new JTextField(5);
        turnPanel.add(keyboardInput);

        JButton keyboardInputButton = new JButton("Place piece");
        keyboardInputButton.addActionListener(new KeyboardInputButtonListener());
        turnPanel.add(keyboardInputButton);

        return turnPanel;
    }

    private JPanel createScorePanel()
    {
        JPanel scorePanel = new JPanel();
        scorePanel.setBackground(Color.GRAY);
        scorePanel.setLayout(new BoxLayout(scorePanel, BoxLayout.Y_AXIS));

        int[] score = game.getScore();
        // black
        scorePanel.add(new JInformationLabel("Black", Color.BLACK));
        blackScore = new JInformationLabel(Integer.toString(score[0]), Color.BLACK);
        scorePanel.add(blackScore);
        // white
        scorePanel.add(new JInformationLabel("White", Color.WHITE));
        whiteScore = new JInformationLabel(Integer.toString(score[1]), Color.WHITE);
        scorePanel.add(whiteScore);

        return scorePanel;
    }

    private void populateBoard()
    {
        for (int i = 0; i < 8; i++)
        {
            for(int j = 0; j < 8; j++)
            {
                if (game.getCell(i, j) == Cell.BLACK)
                    arrayLabels[i][j].setText(black);
                else if (game.getCell(i, j) == Cell.WHITE)
                    arrayLabels[i][j].setText(white);
            }
        }
    }

    private void playGame()
    {
        // create array for possible computer players
        OthelloComputer[] players = new OthelloComputer[2];

        // no computer players as of now; code reflects such; must be modified

        playerTurn = game.getPlayerTurn();
        while (playerTurn != 0) // 0 == game over
        {
            toggleGameInfo(playerTurn);
        }
    }

    private void toggleGameInfo(int playTurn)
    {
        playerTurn = playTurn;
        if (playerTurn == 1) // black
        {
            turnPiece = Cell.BLACK;
            turnInfo.setText("Black");
        }
        else if(playerTurn == -1) // white
        {
            turnPiece = Cell.WHITE;
            turnInfo.setText("White");
        }
        else // == 0
            turnInfo.setText("Game Over");

        int[] score = game.getScore();
        blackScore.setText(Integer.toString(score[0]));
        whiteScore.setText(Integer.toString(score[1]));
    }

    // Action listeners ------------------------------------------------------------------------------------------------
    private class KeyboardInputButtonListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            String input = keyboardInput.getText();
            // human will input column then row
            int row = (int) input.charAt(1) - 49; // compensate with extra -1 since human input
            int col = (int) input.charAt(0) - 65;

            // if valid
            if (game.isValid(row, col, turnPiece))
            {
                game.placePiece(row, col, turnPiece);
                keyboardInput.setText(""); // reset text box
                populateBoard();
                toggleGameInfo(game.getPlayerTurn());
                // code

            }
            else
            {
                JOptionPane.showMessageDialog(null, "Invalid input.  Please try again.");
            }
        }
    }
}
/*
 * MENU:
 *      - New game
 *      - Number of players
 *          + Computer intelligence
 *      - Size of board (by 2)
 *      - Turn off score
 *      - Method of play:
 *          + show hints on cell
 *          + drag circle
 *          + click button on cell
 *
 */
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
class DisplayCell extends JLabel // holds letter/number
{
    public DisplayCell(String text)
    {
        super.setText(text);
    }
    @Override
    public void setHorizontalAlignment(int alignment) {
        super.setHorizontalAlignment(CENTER);
    }

    @Override
    public void setVerticalAlignment(int alignment) {
        super.setVerticalAlignment(CENTER);
    }

    @Override
    public void setFont(Font font) {
        // Monotype Corsiva
        Font letters = new Font("Monotype Corsiva", Font.BOLD, 32);
        super.setFont(letters);
    }

    @Override
    public void setForeground(Color fg) {
        super.setForeground(new Color(255,223,0)); // golden yellow
    }
}
//----------------------------------------------------------------------------------------------------------------------
class BoardCell extends JLabel // holds Cell piece (current X or O)
{
    private Font font = new Font("Calibri", Font.BOLD, 30);

    @Override
    public void setText(String text) { // temporary
        super.setText(text);
        super.setFont(font);

        if (text.equals("X")) // black
            super.setForeground(Color.BLACK);
        else // white
            super.setForeground(Color.WHITE);
    }

    @Override
    public void setHorizontalAlignment(int alignment) {
        super.setHorizontalAlignment(CENTER);
    }

    @Override
    public void setVerticalAlignment(int alignment) {
        super.setVerticalAlignment(CENTER);
    }

    @Override
    public void setBackground(Color bg) {
        super.setBackground(new Color(39, 119, 20)); // green felt
    }

    @Override
    public void setOpaque(boolean isOpaque) {
        super.setOpaque(true);
    }
}
//----------------------------------------------------------------------------------------------------------------------
class JPanelWithBackground extends JPanel
{
    private Image backgroundImage;

    public JPanelWithBackground(String fileName, Dimension preferredSize)
    {
        try {
            backgroundImage = ImageIO.read(new File(fileName));
            // expand with margin of error
            backgroundImage = backgroundImage.getScaledInstance((int) preferredSize.getWidth()+100,
                    (int) preferredSize.getHeight()+100, Image.SCALE_DEFAULT);
        }
        catch (IOException e) // need to handle
        {}
        setSize(preferredSize);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the background image.
        g.drawImage(backgroundImage, 0, 0, this);
    }
}
//----------------------------------------------------------------------------------------------------------------------
class JInformationLabel extends JLabel
{
    Font font = new Font("Times New Roman", Font.PLAIN, 20);

    public JInformationLabel(String text, Color color)
    {
        super.setText(text);
        super.setFont(font);
        super.setForeground(color);
    }

    @Override
    public void setAlignmentX(float alignmentX) {
        super.setAlignmentX(Component.CENTER_ALIGNMENT);
    }
}
