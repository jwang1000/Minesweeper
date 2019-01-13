/*
 * MINESWEEPER PROJECT
 * Written by Jonathan Wang
 * Sep 13, 2017 - Nov 15
 * Part 1 Sep 13 - 14
 * Part 2 Sep 15
 * Part 3 Sep 19 - 20
 * Part 4 Sep 21 - 22
 * Part 5 Sep 26
 * Part 6 Sep 26 - Oct 4
 *  - Happy face added Sep 26
 *  - Difficulty buttons added Sep 26 - 27, moved Sep 28
 *  - Double click added Sep 27
 *  - Final textures (red mine, misflagged square) added Sep 27
 *  - Flag tracker and timer added Sep 28
 *  - Custom grid size added Sep 29
 *  - First click is always on a 0 added Oct 2
 *  - Option Panel (for small windows) added Oct 3
 *  - ¯\_(ツ)_/¯ added Oct 4
 * Assignment 7 Nov 6 - 15
 *  - File reading added Nov 6, fixed Nov 9
 *  - High scores table added Nov 7
 *  - File writing added (finished) Nov 9
 *  - Name writing added Nov 14
 *  - File creating added Nov 15
 *
 * TODO: fix double clicking also triggering left/right click
 *  - important when double clicking an unexposed square
 *
 * 11 * 11 is minimum grid size for all button text to show
 */
package minesweeper;

/**
 *
 * @author jonathan.wang
 */
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

public class Minesweeper 
{
    // initialize fields
    @SuppressWarnings("FieldMayBeFinal")
    // uhh I guess NetBeans works with this?? Well, it's better than yellow lines.
    ArrayList<Integer> mineNumbers = new ArrayList();
    int mines;
    int rows;
    int cols;
    int tiles_left; // for keeping track of how many tiles are unexposed
    int flags;  // for storing how many flags are left to use, set to # mines at start
    int time;  // to display the time
    int difficulty;  // for storing which difficulty the player is currently on
    Button[][] buttons;  // for storing the buttons after creation
    Button faceButton;  // face button that resets the game
    Button[] resizeButtons;  // 4 buttons that change the difficulty of the game
    Button optionButton;  // when window is too small, display this instead of resizeButtons
    Button statsButton;  // button that shows top times for each difficulty
    Random randNum = new Random();
    int[][] grid;  // grid of mines and squares
    boolean playing;  // to control the timer
    boolean gameOver;  // if game is finished
    boolean victory;  // if game is won
    
    // creating panels, labels, and frame
    Timer timer;
    JLabel flagsLeft;
    JLabel timerLabel;
    JPanel overallPanel;  // contains gamePanel and headerPanel
    JPanel gamePanel;  // contains the game board
    JPanel headerPanel;  // contains the happy face, timer, and flags left counter
    JPanel difficultyPanel;  // contains the difficulty buttons
    JPanel scorePanel;  // contains the stats button
    JFrame frame;
    
    // array of images - index 0 is empty square, 1 - 8 are the respective numbers,
    // and 9 is a mine
    static ImageIcon[] images = new ImageIcon[10];
    
    // saving images
    static ImageIcon UNCLICKED_SQUARE = new ImageIcon("unclicked_square.png");
    static ImageIcon FLAG = new ImageIcon("flagged.png");
    static ImageIcon MISFLAGGED = new ImageIcon("misflagged.png");
    static ImageIcon TRIGGERED_MINE = new ImageIcon("mine_red.png");
    
    // 0 is happy face, 1 is shock, 2 is win, and 3 is dead
    static ImageIcon[] faces = new ImageIcon[4];
    
    // science world
    public static void main(String[] args) 
    {
        new Minesweeper();  // run the code thing
    }

    // constructor
    public Minesweeper()
    {
        // initialization of variables and arrays
        rows = 8;
        cols = 8;
        mines = 10;
        tiles_left = rows * cols;
        flags = mines;
        time = 0;
        playing = false;
        gameOver = false;
        victory = false;
        resizeButtons = new Button[4];
        
        // create labels for flagsLeft and timer
        flagsLeft = new JLabel(Integer.toString(flags));
        flagsLeft.setPreferredSize(new Dimension(80, 30));
        flagsLeft.setHorizontalAlignment(JLabel.CENTER);
        timerLabel = new JLabel(Integer.toString(time));
        timerLabel.setPreferredSize(new Dimension(80, 30));
        timerLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // create timer
        timer = new Timer(1000, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent ae) 
            {
                if (playing)
                {
                    time++;
                    
                    if (time < 1000)
                    {
                        timerLabel.setText(Integer.toString(time));

                        if (time == 69)
                            timerLabel.setText("( ͡° ͜ʖ ͡°)");
                        else if (time == 420)
                            timerLabel.setText("BLAZE IT");
                    }
                    else if (time < 2000)
                        timerLabel.setText("¯\\_(ツ)_/¯");
                    else if (time < 3000)
                        timerLabel.setText("u suck");
                    else if (time < 5000)
                        timerLabel.setText("biologist");
                    else
                        timerLabel.setText("geologist");
                }
            }
        }
        );
        // save more images to an array
        images[0] = new ImageIcon("empty_tile.png");
        images[1] = new ImageIcon("1.png");
        images[2] = new ImageIcon("2.png");
        images[3] = new ImageIcon("3.png");
        images[4] = new ImageIcon("4.png");
        images[5] = new ImageIcon("5.png");
        images[6] = new ImageIcon("6.png");
        images[7] = new ImageIcon("7.png");
        images[8] = new ImageIcon("8.png");
        images[9] = new ImageIcon("mine.png");
        
        faces[0] = new ImageIcon("happy_face.png");
        faces[1] = new ImageIcon("shock_face.png");
        faces[2] = new ImageIcon("win_face.png");
        faces[3] = new ImageIcon("dead_face.png");
        
        // scale the images
        int tileWidth = 32;
        int tileHeight = 32;
        UNCLICKED_SQUARE = scaleImage(UNCLICKED_SQUARE, tileWidth, tileHeight);
        FLAG = scaleImage(FLAG, tileWidth, tileHeight);
        MISFLAGGED = scaleImage(MISFLAGGED, tileWidth, tileHeight);
        TRIGGERED_MINE = scaleImage(TRIGGERED_MINE, tileWidth, tileHeight);
        for (int i = 0; i < images.length; i++)
        {
            images[i] = scaleImage(images[i], tileWidth, tileHeight);
        }
        for (int i = 0; i < faces.length; i++)
        {
            faces[i] = scaleImage(faces[i], tileWidth, tileHeight);
        }
        
        // create panel with gridlayout
        gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(rows, cols));
        
        // initialize frame
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
        
        // create difficulty buttons
        for (int p = 0; p < resizeButtons.length; p++)
        {
            resizeButtons[p] = new Button();
            resizeButtons[p].addMouseListener(new MouseClickListener(this));
            resizeButtons[p].isResizeButton = true;
            
            switch (p) 
            {
                // beginner difficulty
                case 0:
                    // rows, cols, no. of mines
                    resizeButtons[p].parameters[0] = 8;
                    resizeButtons[p].parameters[1] = 8;
                    resizeButtons[p].parameters[2] = 10;
                    resizeButtons[p].setText("Beginner");
                    break;
                    
                // intermediate difficulty
                case 1:
                    resizeButtons[p].parameters[0] = 16;
                    resizeButtons[p].parameters[1] = 16;
                    resizeButtons[p].parameters[2] = 40;
                    resizeButtons[p].setText("Intermediate");
                    break;
                    
                // expert difficulty
                case 2:
                    resizeButtons[p].parameters[0] = 16;
                    resizeButtons[p].parameters[1] = 30;
                    resizeButtons[p].parameters[2] = 99;
                    resizeButtons[p].setText("Expert");
                    break;
                    
                // custom difficulty
                default:
                    resizeButtons[p].parameters[0] = 1;
                    resizeButtons[p].parameters[1] = 1;
                    resizeButtons[p].parameters[2] = 0;
                    resizeButtons[p].setText("Custom");
                    break;
            }
        }
        
        // create header bar
        faceButton = new Button();
        faceButton.addMouseListener(new MouseClickListener(this));
        faceButton.setIcon(faces[0]);
        faceButton.isHappyFace = true; // face button is itself .-.
        
        // initialize headerPanel and add buttons and labels
        headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
        headerPanel.add(flagsLeft);
        headerPanel.add(Box.createHorizontalGlue());
        headerPanel.add(faceButton);
        headerPanel.add(Box.createHorizontalGlue());
        headerPanel.add(timerLabel);
        
        // initialize difficultyPanel
        difficultyPanel = new JPanel();
        difficultyPanel.setLayout(new BoxLayout(difficultyPanel, BoxLayout.X_AXIS));
        for (int q = 0; q < resizeButtons.length; q++)
            difficultyPanel.add(resizeButtons[q]);
        
        // create stats button
        statsButton = new Button();
        statsButton.addMouseListener(new MouseClickListener(this));
        statsButton.setText("Top Scores");
        statsButton.isStatsButton = true;
        statsButton.setPreferredSize(new Dimension(120, 32));
        
        // initialize scorePanel
        scorePanel = new JPanel();
        scorePanel.setLayout(new BoxLayout(scorePanel, BoxLayout.X_AXIS));
        scorePanel.add(Box.createHorizontalGlue());
        scorePanel.add(statsButton);
        scorePanel.add(Box.createHorizontalGlue());
        
        // initialize overallPanel
        overallPanel = new JPanel();
        overallPanel.setLayout(new BoxLayout(overallPanel, BoxLayout.Y_AXIS));
        
        // prepare the frame
        overallPanel.add(difficultyPanel);
        overallPanel.add(headerPanel);
        overallPanel.add(gamePanel);
        overallPanel.add(scorePanel);
        frame.add(overallPanel);
        frame.setTitle("Minesweeper");
        frame.setIconImage(images[9].getImage());  // set game image to mine icon
        
        // create a new game
        newGame();
    }
    
    // Return a scaled image from a large image
    private ImageIcon scaleImage(ImageIcon inImage, int width, int height)
    {
        Image image = inImage.getImage();
        Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        ImageIcon output = new ImageIcon(scaledImage);
        return output;
    }
    
    // reset everything every time a new game is created
    public void newGame()
    {
        // reset of variables and arrays
        frame.remove(overallPanel);
        overallPanel.remove(gamePanel);
        overallPanel.remove(headerPanel);
        overallPanel.remove(difficultyPanel);
        difficultyPanel.removeAll();
        gamePanel.removeAll();
        gamePanel.setLayout(new GridLayout(rows, cols));
        
        ///*
        // if window is too small, put difficulty buttons in pop-up instead
        if (rows < 11 || cols < 11)
        {
            difficultyPanel.removeAll();
            optionButton = new Button();
            optionButton.addMouseListener(new MouseClickListener(this));
            optionButton.setText("Options");
            optionButton.isOptionButton = true;
        
            difficultyPanel.add(optionButton);
        }
        else
        {
            for (int q = 0; q < resizeButtons.length; q++)
                difficultyPanel.add(resizeButtons[q]);
        }
        //*/
        
        tiles_left = rows * cols;
        flags = mines;
        time = 0;
        playing = false;
        gameOver = false;
        victory = false;
        mineNumbers.clear();
        grid = new int[rows][cols];

        timer.start();  // start the timer, will only update if playing
        
        // initialize the buttons
        buttons = new Button[rows][cols];
        
        // generate the grid and set the buttons
        mineMaker();
        
        faceButton.setIcon(faces[0]);  // reset face
        
        // set text and font for flagsLeft and timer
        flagsLeft.setText(Integer.toString(flags));
        flagsLeft.setFont(flagsLeft.getFont().deriveFont(16.0f));
        timerLabel.setText(Integer.toString(time));
        timerLabel.setFont(timerLabel.getFont().deriveFont(16.0f));
        
        // prepare and show the frame
        overallPanel.add(difficultyPanel);
        overallPanel.add(headerPanel);
        overallPanel.add(gamePanel);
        overallPanel.add(scorePanel);
        frame.add(overallPanel);
        frame.pack();
        frame.repaint();
    }
    
    // Generates the grid
    private void mineMaker()
    {
        // generating random mine locations
        while (mineNumbers.size() < mines)
        {
            int nextMine = randNum.nextInt(rows * cols);
            if (!mineNumbers.contains(nextMine))
                mineNumbers.add(nextMine);
            // add mine to a random location if that mine is not in mineNumbers already
        }
        Collections.sort(mineNumbers);  // debugging purposes
        
        // setting tile numbers
        for (int mine : mineNumbers)
        {
            int row;
            int col;
            if (rows != cols)  // grid is not square (expert and custom)
            {
                // find small and large dimensions
                int smallest = 0;
                int largest = 0;
                if (rows > cols)
                {
                    smallest = cols;
                    largest = rows;
                }
                else
                {
                    smallest = rows;
                    largest = cols;
                }
                int smallSquare = smallest * smallest;
                
                if (mine > smallSquare - 1)  // mine is outside base square grid
                {
                    if (cols > rows)  // if grid is longer horizontally
                    {
                        row = (mine - smallSquare) / (largest - smallest);
                        col = ((mine - smallSquare) % (largest - smallest)) + smallest;
                    }
                    else  // if grid is longer vertically
                    {
                        row = mine / smallest;  // normal algorithm works here
                        col = mine % smallest;
                    }
                }
                else  // mine is inside square part of grid
                {
                    row = mine / smallest;  // int division gives row, remainder is col
                    col = mine % smallest;
                }
            }
            else  // grid is square
            {
                row = mine / rows;  // int division gives row, remainder is col
                col = mine % cols;
            }
            grid[row][col] = -100;
            // set each mine location to have -100 on the grid
            // the max number a tile could have is 8, so at the end any 
            // negative number is a mine
        }
        
        // set buttons for each grid tile
        for (int k = 0; k < grid.length; k++)
        {
            for (int l = 0; l < grid[k].length; l++)
            {
                buttons[k][l] = new Button();  // initialize each button
                buttons[k][l].addMouseListener(new MouseClickListener(this));
                // add listener (I can't spell that correctly on the first try to save my life)
                // sends the game to the mouseClickListener so it can call methods
                buttons[k][l].setIcon(UNCLICKED_SQUARE);
                
                // set location
                int[] location = {k, l};
                buttons[k][l].setLocation(location);
                gamePanel.add(buttons[k][l]);  // add buttons to the panel
            }
        }
        
        // set the numbers and button numbers for the grid
        setNumbers();
        
        /*
        // print out the grid
        for (int[] row : grid)
        {
            for (int tile : row)
                System.out.print(tile + " | ");  // print a | between tiles
            System.out.println();
        }
        System.out.println();
        */
    }
    
    // Sets the numbers for the game board, set the button numbers afterwards
    private void setNumbers()
    {
        // reset any non-mine numbers already set
        for (int q = 0; q < grid.length; q++)
        {
            for (int r = 0; r < grid[q].length; r++)
            {
                // if tile is not a mine and not already 0
                if (grid[q][r] >= 0)
                    grid[q][r] = 0;
                // tile is a mine, set it to -100
                else
                    grid[q][r] = -100;
            }
        }
        
        // iterate through the grid and find each mine
        for (int s = 0; s < grid.length; s++)
        {
            for (int t = 0; t < grid[s].length; t++)
            {
                // if location is a mine, increment all numbers around it by 1
                if (grid[s][t] < 0)
                {
                    for (int i = s - 1; i <= s + 1; i++)
                    {
                        for (int j = t - 1; j <= t + 1; j++)
                        {
                            if (i >= 0 && i < rows && j >= 0 && j < cols)
                            {
                                // if the square is within the grid, add 1
                                grid[i][j]++;
                            }
                        }
                    }
                }
            }
        }
        
        // after setting numbers, set buttons to correct values
        for (int u = 0; u < grid.length; u++)
        {
            for (int v = 0; v < grid[u].length; v++)
            {
                if (grid[u][v] < 0)
                    grid[u][v] = -1;  // set all mines (negative numbers) to -1
                buttons[u][v].setValue(grid[u][v]);
            }
        }
    }
    
    // Exposes a button and checks if it's zero
    public void buttonClicked(int row, int col)
    {
        buttons[row][col].expose();
        int value = buttons[row][col].getValue();
        tiles_left--;
        
        // if button isn't a mine
        if (value >= 0)
        {
            // set icon to its number
            buttons[row][col].setIcon(images[buttons[row][col].getValue()]);

            // if the exposed square has a value of 0, run zeroClicked again
            if (value == 0)
            {
                int[] output = {row, col};
                zeroClicked(output);
            }
        }
        // if button is a mine
        else
        {
            buttons[row][col].setIcon(TRIGGERED_MINE);
            mineClicked();
        }
        
        checkVictory();
    }
    
    // At the start of a game, if first tile is not zero,
    // move mines so that it is zero
    public void setZero(int[] location)
    {
        ///*
        int row = location[0];
        int col = location[1];
        boolean done = true;
        
        for (int i = row - 1; i <= row + 1; i++)
        {
            for (int j = col - 1; j <= col + 1; j++)
            {
                if (i >= 0 && i < rows && j >= 0 && j < cols)
                {
                    // if the square is within the grid and a mine, move the
                    // mine to the first open square, starting from the top left
                    if (grid[i][j] < 0)
                    {
                        grid[i][j] = 0;  // remove the mine from the tile
                        done = false;
                        for (int q = 0; q < grid.length; q++)
                        {
                            for (int r = 0; r < grid[q].length; r++)
                            {
                                // if tile is not a mine, make it a mine
                                if (grid[q][r] >= 0)
                                {
                                    grid[q][r] = -100;
                                    done = true;
                                    break;
                                }
                            }
                            if (done)
                                break;
                        }
                    }
                }
            }
        }
        
        // reset the numbers for the game board
        setNumbers();
        //*/
    }
    
    // When a mine is clicked, game is over
    // Expose all buttons
    public void mineClicked()
    {
        gameOver = true;
        playing = false;
        flags = 0;
        flagsLeft.setText(Integer.toString(flags));
        for (Button[] buttonGrid : buttons) 
        {
            for (Button button : buttonGrid) 
            {
                if (button.getValue() == -1)  // if square was a mine
                {
                    if (!button.isFlagged() && !button.isExposed())
                    {
                        // if button wasn't flagged or exposed already
                        button.setIcon(images[9]);
                        button.expose();
                    }
                    // if button was flagged already, leave it as a flag
                }
                else  // if square wasn't a mine
                {
                    if (!button.isFlagged())  // if button wasn't flagged
                        button.setIcon(images[button.getValue()]);
                    else  // if button was wrongly flagged
                        button.setIcon(MISFLAGGED);
                    button.expose();
                }
                // don't do tiles_left--, mineClicked means game is over
            }
        }
        
        faceButton.setIcon(faces[3]);  // set to dead face
    }
    
    // Calls when the mouse is double clicked
    // if the square is exposed and a number, and the number of flags adjacent is reached,
    // expose all adjacent tiles that aren't flagged, even if they are mines
    public void doubleClick(int[] location)
    {
        int row = location[0];
        int col = location[1];
        int value = buttons[row][col].getValue();
        int flags = 0;
        
        // if button is a non-zero number and is not flagged
        if (value > 0 && !buttons[row][col].isFlagged())
        {
            // checks every square adjacent to the square for a flag
            for (int m = row - 1; m <= row + 1; m++)
            {
                for (int n = col - 1; n <= col + 1; n++)
                {
                    // square must be within grid!
                    if (m >= 0 && m < rows && n >= 0 && n < cols 
                            && buttons[m][n].isFlagged())
                        flags++;
                }
            }
            
            // if number of flags adjacent to tile is reached
            if (flags == value)
            {
                // checks for every square adjacent to the square
                for (int m = row - 1; m <= row + 1; m++)
                {
                    for (int n = col - 1; n <= col + 1; n++)
                    {
                        // if the square is within the grid and hasn't been exposed or flagged, expose it
                        if (m >= 0 && m < rows && n >= 0 && n < cols 
                                && !buttons[m][n].isExposed() && !buttons[m][n].isFlagged())
                            buttonClicked(m, n);
                    }
                }
            }
        }
    }
    
    // When a zero square is clicked, expose all adjacent number squares
    // recursion recursion recursion recursion recursion recursion recursion recursion recursion recursion
    public void zeroClicked(int[] location)
    {
        int row = location[0];
        int col = location[1];
        
        // checks for every square adjacent to the square
        for (int m = row - 1; m <= row + 1; m++)
        {
            for (int n = col - 1; n <= col + 1; n++)
            {
                // if the square is within the grid and hasn't been exposed or flagged, expose it
                if (m >= 0 && m < rows && n >= 0 && n < cols 
                        && !buttons[m][n].isExposed() && !buttons[m][n].isFlagged())
                    buttonClicked(m, n);
            }
        }
    }
    
    // checks if all non-mine buttons have been exposed
    // will only be successful when all non-mine squares have been exposed, as
    // otherwise a mine will have been triggered
    public void checkVictory()
    {
        // obviously don't call if game is over already
        // (admittedly that wasn't so obvious for about 20 minutes of bug hunting)
        if (tiles_left == mines && !gameOver)
        {
            gameOver = true;
            victory = true;
            playing = false;
            flags = 0;
            flagsLeft.setText(Integer.toString(flags));
            for (int p = 0; p < buttons.length; p++)
            {
                for (int q = 0; q < buttons[p].length; q++)
                {
                    if (buttons[p][q].getValue() == -1)
                    {
                        buttons[p][q].setIcon(FLAG);
                        buttons[p][q].flag();
                    }
                }
            }
            
            // write score to file if it's in the top 10 for its difficulty
            writeScore(time, difficulty);
            
            faceButton.setIcon(faces[2]);  // set to win face
        }
    }
    
    // displays the custom size panel
    public int[] customPanel()
    {
        playing = false;  // to make sure the timer doesn't start when the custom button is pressed
        int[] output = new int[3];  // rows, cols, mines
        
        // create fields and panel for popup
        JTextField rowsField = new JTextField(8);
        JTextField colsField = new JTextField(8);
        JTextField mineField = new JTextField(10);  // ba dum tss
        JPanel customPanel = new JPanel(new GridLayout(0, 1));
        
        // add labels and fields to panel
        customPanel.add(new JLabel("Rows: (between 6 and 25)"));
        customPanel.add(rowsField);
        customPanel.add(new JLabel("Columns: (between 6 and 40)"));
        customPanel.add(colsField);
        customPanel.add(new JLabel("Mines: (cannot be 0)"));
        customPanel.add(mineField);
        customPanel.add(new JLabel("Number of mines cannot exceed number of tiles."));
        
        // show the dialog, save whether or not OK was clicked
        int result = JOptionPane.showConfirmDialog(null, customPanel, "Custom Grid",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        // if OK is clicked
        if (result == JOptionPane.OK_OPTION)
        {
            // sanitize your inputs, people
            try
            {
                int row = Integer.parseInt(rowsField.getText());
                int col = Integer.parseInt(colsField.getText());
                int mine = Integer.parseInt(mineField.getText());
                
                // checking if the parameters fit the constraints
                // if not, default to beginner stats
                if (row >= 6 && row <= 25)
                    output[0] = row;
                else
                    output[0] = 8;
                
                if (col >= 6 && col <= 40)
                    output[1] = col;
                else
                    output[1] = 8;
                
                if (mine > 0)
                {
                    if (mine < output[0] * output[1])
                        output[2] = mine;
                    else
                        output[2] = output[0] * output[1] - 1;
                }
                else
                    output[2] = 10;
            }
            catch (NumberFormatException e)
            {
                output[0] = 8;
                output[1] = 8;
                output[2] = 10;
            }
        }
        // if cancel was clicked or the window was exited in some way
        else
        {
            output[0] = 8;
            output[1] = 8;
            output[2] = 10;
        }
        
        return output;
    }
    
    // displays the panel with best names and times for each difficulty
    public void statsPanel()
    {
        // create panel for frame
        // 3 panels necessary for aesthetics (for labels to be centered)
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        // create panel for difficulty labels
        JPanel namePanel = new JPanel(new GridLayout(1, 3));
        
        // create panel to show stats
        JPanel statsPanel = new JPanel(new GridLayout(10, 5));
        
        // create labels for each category
        // blank label before each to create spacing for grid
        //namePanel.add(new JLabel(""));
        JLabel beginnerLabel = new JLabel ("Beginner");
        beginnerLabel.setHorizontalAlignment(JLabel.CENTER);
        beginnerLabel.setFont(beginnerLabel.getFont().deriveFont(14.0f));
        namePanel.add(beginnerLabel);
        
        //statsPanel.add(new JLabel(""));
        JLabel intermediateLabel = new JLabel ("Intermediate");
        intermediateLabel.setHorizontalAlignment(JLabel.CENTER);
        intermediateLabel.setFont(intermediateLabel.getFont().deriveFont(14.0f));
        namePanel.add(intermediateLabel);
        
        //statsPanel.add(new JLabel(""));
        JLabel expertLabel = new JLabel("Expert");
        expertLabel.setHorizontalAlignment(JLabel.CENTER);
        expertLabel.setFont(expertLabel.getFont().deriveFont(14.0f));
        namePanel.add(expertLabel);
        
        int[] stats = readScores();  // read scores from file
        String[] statNames = readNames();  // read names from file
        
        // iterate through each score and add to the frame
        for (int i = 0; i < stats.length * 2; i++)
        {
            if (i % 2 == 1)  // get scores
            {
                JLabel label = new JLabel(Integer.toString(stats[i / 2]));
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setFont(label.getFont().deriveFont(14.0f));
                statsPanel.add(label);
            }
            else  // get names
            {
                JLabel label = new JLabel(statNames[i / 2]);
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setFont(label.getFont().deriveFont(14.0f));
                statsPanel.add(label);
            }
        }
        
        // create frame
        JFrame statsFrame = new JFrame();
        statsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // add panels to overall panel
        panel.add(namePanel);
        panel.add(statsPanel);
        
        // show the frame
        statsFrame.add(panel);
        statsFrame.pack();
        statsFrame.setResizable(false);
        statsFrame.setVisible(true);
        statsFrame.setTitle("High Scores");
    }
    
    // displays the option panel, only an option (hah) when window is small
    public void optionPanel()
    {
        JPanel optionPanel = new JPanel(new GridLayout(0, 1));
        optionPanel.add(new JLabel("Choose a difficulty level:"));
        Object options[] = {"Beginner", "Intermediate", "Expert", "Custom"};
        
        int choice = JOptionPane.showOptionDialog(null, optionPanel,
                "Difficulty Options", JOptionPane.YES_NO_CANCEL_OPTION, 
                JOptionPane.PLAIN_MESSAGE, null, options, null);
        switch (choice)
        {
            case 0:  // beginner
                rows = 8;
                cols = 8;
                mines = 10;
                difficulty = 0;
                newGame();
                break;
                
            case 1:  // intermediate
                rows = 16;
                cols = 16;
                mines = 40;
                difficulty = 1;
                newGame();
                break;
                
            case 2:  // expert
                rows = 16;
                cols = 30;
                mines = 99;
                difficulty = 2;
                newGame();
                break;
                
            case 3:  // custom
                int[] parameters = customPanel();
                rows = parameters[0];
                cols = parameters[1];
                mines = parameters[2];
                difficulty = -1;
                newGame();
                break;
        }
    }
    
    // reads the scores from the file
    public int[] readScores()
    {
        File file = new File("C:\\Users\\Public\\Scores.txt");
        int[] scores = new int[30];
        
        try
        {
            // true if file does not exist and is created
            if (file.createNewFile())
            {
                // create default scores
                FileWriter fw = new FileWriter(file);
                for (int h = 0; h < 30; h++)
                {
                    fw.write(Integer.toString(999));
                    fw.write(System.lineSeparator());
                }
                fw.close();
            }
            
            // read numbers from file
            Scanner sc = new Scanner(file);
            String[] numbers = new String[30];
            for (int h = 0; h < numbers.length; h++)
                numbers[h] = sc.nextLine();
            
            // convert strings into ints
            for (int i = 0; i < numbers.length; i++)
            {
                scores[i] = (Integer.parseInt(numbers[i]));
            }
        }
        catch(IOException e)
        {
            System.out.println(e);
        }
        
        return scores;
    }
    
    // reads names from the file
    // each name position matches with its score position
    public String[] readNames()
    {
        File file = new File("C:\\Users\\Public\\Names.txt");
        String[] names = new String[30];
        
        try
        {
            // true if file does not exist and is created
            if (file.createNewFile())
            {
                // create default names
                FileWriter fw = new FileWriter(file);
                for (int h = 0; h < 30; h++)
                {
                    fw.write("--");
                    fw.write(System.lineSeparator());
                }
                fw.close();
            }
            
            // read names from file
            Scanner sc = new Scanner(file);
            for (int i = 0; i < names.length; i++)
                names[i] = sc.nextLine();
        }
        catch(IOException e)
        {
            System.out.println(e);
        }
        
        return names;
    }
    
    // checks if a score is in top 10 for the difficulty
    // if it is, return true and write
    // difficulty 0 = beginner, 1 = intermediate, 2 = expert; -1 = custom
    public void writeScore(int score, int difficulty)
    {
        File file = new File("C:\\Users\\Public\\Scores.txt");
        int[] scores = readScores();  // save current scores to scores array
        
        if (difficulty >= 0)  // if difficulty isn't custom
        {
            int[] beginnerScores = new int[10];
            int[] intermediateScores = new int[10];
            int[] expertScores = new int[10];
            
            // assign the scores to their respective arrays
            for (int i = 0; i < scores.length; i++)
            {
                // every third difficulty is part of the same list
                // e.g. scores[i], where i % 3 == 0 is beginner
                if (i % 3 == 0)
                    beginnerScores[i / 3] = scores[i];
                else if (i % 3 == 1)
                    intermediateScores[i / 3] = scores[i];
                else if (i % 3 == 2)
                    expertScores[i / 3] = scores[i];
            }
            
            int temp = 0;  // used for replacing a value in the scores list
            int temp1 = 0;  // same as above
            boolean replace = false;  // if a score has been replaced
            int[] iterate;  // used for replacing scores
            // this is so that I don't have to have 3 if statements with for loops
            int[] iterate1;  // used for iterating through the scores
            int position = 0;  // used for recording what place a score was inserted
            
            // set iterating array to the respective difficulty
            if (difficulty == 0)
                iterate = beginnerScores;
            else if (difficulty == 1)
                iterate = intermediateScores;
            else
                iterate = expertScores;
            
            iterate1 = iterate;  // make sure they are equal
            
            // iterate through scores and check if the current score is better
            // than any value
            for (int j = 0; j < iterate1.length; j++)
            {
                if (!replace)  // if new score isn't better than a value in the array yet
                {
                    if (score < iterate1[j])  // if score is better than value
                    {
                        replace = true;
                        temp = iterate[j];
                        iterate[j] = score;
                        position = j;
                    }
                }
                else  // if score has been found to be better than a value, then replace
                {
                    temp1 = temp;
                    temp = iterate[j];
                    iterate[j] = temp1;
                }
            }
            
            if (replace)  // if a value was replaced
            {
                // reset iterating array to the respective difficulty
                if (difficulty == 0)
                    beginnerScores = iterate;
                else if (difficulty == 1)
                    intermediateScores = iterate;
                else
                    expertScores = iterate;
                
                try
                {
                    // write scores to file
                    FileWriter fw = new FileWriter(file);
                    for (int k = 0; k < scores.length; k++)
                    {
                        if (k % 3 == 0)  // beginner scores
                        {
                            fw.write(Integer.toString(beginnerScores[k / 3]));
                            fw.write(System.lineSeparator());
                        }
                        else if (k % 3 == 1)  // intermediate scores
                        {
                            fw.write(Integer.toString(intermediateScores[k / 3]));
                            fw.write(System.lineSeparator());
                        }
                        else  // expert scores
                        {
                            fw.write(Integer.toString(expertScores[k / 3]));
                            fw.write(System.lineSeparator());
                        }
                    }
                    fw.close();
                }
                catch (IOException e)
                {
                    System.out.println(e);
                }
                
                writeName(position, difficulty);  // call function to record the user's name
            }
        }
    }
    
    // writes names for the high scores
    // called in writeScore
    public void writeName(int position, int difficulty)
    {
        // get the user's name
        
        // create panel
        JPanel nameInput = new JPanel();
        nameInput.add(new JLabel("Enter your name: (max 10 characters)"));
        JTextField txt = new JTextField(10);
        nameInput.add(txt);
        
        String name = "";  // input name, defaults to blank
        String[] options = {"OK"};
        int choice = 0;
        
        // show panel with input
        choice = JOptionPane.showOptionDialog(null, nameInput, 
                "High Score!", JOptionPane.NO_OPTION, JOptionPane.QUESTION_MESSAGE, 
                null, options, options[0]);
        File file = new File("C:\\Users\\Public\\Names.txt");
        
        if (choice == 0)  // if OK was pressed
            name = txt.getText();
        
        String[] names = readNames();  // get the array of names in the file
        String temp = "";  // used for shifting names down one
        
        if (name.length() > 10)
            name = name.substring(0, 10);  
            // if name is longer than 10 chars, get first 10
        
        // shift names along
        // position * 3 and += 3 are used because every 3rd position belongs to the same list
        if (difficulty == 0)  // beginner
        {
            for (int f = position * 3; f < names.length; f += 3)
            {
                temp = names[f];
                names[f] = name;
                name = temp;
            }
        }
        else if (difficulty == 1)  // intermediate
        {
            for (int g = position * 3 + 1; g < names.length; g += 3)
            {
                temp = names[g];
                names[g] = name;
                name = temp;
            }
        }
        else if (difficulty == 2)
        {
            for (int h = position * 3 + 2; h < names.length; h += 3)
            {
                temp = names[h];
                names[h] = name;
                name = temp;
            }
        }
        
        try
        {
            // write names to file
            FileWriter fw = new FileWriter(file);
            
            for (int i = 0; i < names.length; i++)
            {
                fw.write(names[i]);
                fw.write(System.lineSeparator());
            }
            fw.close();
        }
        catch(IOException e)
        {
            System.out.println(e);
        }
    }
}
