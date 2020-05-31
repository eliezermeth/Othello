
import javax.swing.*;
import java.awt.*;

// temp
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
// end temp

/**
 * @author Eliezer Meth
 * @version 1
 * Start Date: 02.08.2020
 * Last Modified: 02.09.2020
 */

public class GUItesting
{
    private OthelloLogicInterface game;

    private BoardCell[][] arrayLabels;
    private JButton[][] arrayButtons;


    public GUItesting(OthelloLogicInterface game)
    {
       System.out.println("Entered constructor");

        createFrame();
    }

    private void createFrame()
    {
        JFrame window = new JFrame();
        window.setTitle("Othello");
        window.setSize(550, 550);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        JLayeredPane layPanel = createBoard();
        window.add(layPanel, JLayeredPane.DEFAULT_LAYER);

        window.setVisible(true);
    }

    private JLayeredPane createBoard()
    {
        JLayeredPane layPanel = new JLayeredPane();

        // create under panel
        JComponent under = new JPanel();
        under.setPreferredSize(new Dimension(550, 550));
        under.setLayout(new GridLayout(9, 9, 2, 2));
        for (int i = 1; i <= 81; i++)
        {
            JLabel label = new JLabel(Integer.toString(i));
            label.setBackground(Color.GREEN);
            label.setOpaque(true);
            label.setHorizontalAlignment(SwingConstants.CENTER);

            under.add(label);
        }
        layPanel.add(under, JLayeredPane.DEFAULT_LAYER);

        return layPanel;
    }

    // ------------------------------------------------------------------------
    public static void main(String[] args)
    {
        OthelloLogicInterface game = new OthelloLogic();
        new GUItesting(game);
    }
}

class JTransparentPanel extends JPanel
{
    public JTransparentPanel()
    {
        super.setOpaque(true);
        super.setLayout(null);
    }
}

//----------------------------------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------------------------
