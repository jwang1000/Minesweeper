/*
 * MouseClickListener for Minesweeper Project
 * Written by Jonathan Wang
 */
package minesweeper;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.SwingUtilities;

/**
 *
 * @author jonathan.wang
 */
public class MouseClickListener implements MouseListener
{
    Minesweeper game;
    
    public MouseClickListener(Minesweeper inGame)
    {
        game = inGame;
    }
    
    @Override
    public void mouseClicked(MouseEvent me) 
    {
    }

    @Override
    public void mousePressed(MouseEvent me) 
    {
        Button button = (Button) me.getSource();
        
        // if button is not the happy face, resize button, statsButton,
        // or option button and game is not over
        // basically if button is part of grid and game is still going
        if (!button.isHappyFace && !button.isResizeButton && !button.isStatsButton
                && !button.isOptionButton && !game.gameOver)
        {
            // 0 is happy face, 1 is shock, 2 is win, and 3 is dead
            game.faceButton.setIcon(game.faces[1]);  // set to shock face
            
            if (SwingUtilities.isLeftMouseButton(me))  // left click & double click case
            {
                if (SwingUtilities.isRightMouseButton(me))  // double click
                {
                    if (button.isExposed())  // only work if button is exposed
                        game.doubleClick(button.buttonLocation());
                }
                else  // left click only
                {
                    // if this is the first click
                    if (!game.playing)
                    {
                        // if tile is not 0, call the function to make it 0
                        if (button.getValue() != 0)
                        {
                            game.setZero(button.buttonLocation());
                        }
                        game.playing = true;
                    }
                    
                    // only do this if the button hasn't been flagged or exposed yet
                    if (!button.isFlagged() && !button.isExposed())
                    {
                        int[] location = button.buttonLocation();
                        game.buttonClicked(location[0], location[1]);
                    }
                }
            }
            else  // right click only
            {
                if (!button.isExposed())  // if button isn't exposed yet
                {
                    if (button.isFlagged())
                    {
                        button.unflag();
                        button.setIcon(game.UNCLICKED_SQUARE);
                        game.flags++;
                        game.flagsLeft.setText(Integer.toString(game.flags));
                    }
                    else
                    {
                        button.flag();
                        button.setIcon(game.FLAG);
                        game.flags--;
                        game.flagsLeft.setText(Integer.toString(game.flags));
                    }
                }
            }
        
            // if not playing yet and game isn't over, start the timer
            if (!game.playing && !game.gameOver)
                game.playing = true;
        }
        else if (button.isHappyFace) // button is the happy face button
        {
            game.newGame();
        }
        else if (button.isResizeButton)  // button resizes the game board
        {
            // rows, cols, no. of mines
            game.rows = button.parameters[0];
            game.cols = button.parameters[1];
            game.mines = button.parameters[2];
            
            if (game.mines == 10)
                game.difficulty = 0;  // beginner
            else if (game.mines == 40)
                game.difficulty = 1;  // intermediate
            else if (game.mines == 99)
                game.difficulty = 2;  // expert
            else
                game.difficulty = -1;  // custom
            
            // if button is the custom size button
            if (button.parameters[0] == 1)
            {
                int[] parameters = game.customPanel();
                game.rows = parameters[0];
                game.cols = parameters[1];
                game.mines = parameters[2];
                
                game.difficulty = -1;  // show game is on custom difficulty
            }
            
            game.newGame();  // set a new game with the new parameters
        }
        else if (button.isStatsButton)  // button shows stats for each difficulty
        {
            game.statsPanel();
        }
        else if (button.isOptionButton)  // when window is too small, brings up difficulty buttons
        {
            game.optionPanel();
        }
    }

    @Override
    public void mouseReleased(MouseEvent me) 
    {
        Button button = (Button) me.getSource();
        if (!button.isHappyFace && !button.isResizeButton)
        {
            // 0 is happy face, 1 is shock, 2 is win, and 3 is dead
            if (!game.gameOver)  // if game is playing
                game.faceButton.setIcon(game.faces[0]);  // reset face
            else  // if game is over
            {
                if (game.victory)
                    game.faceButton.setIcon(game.faces[2]);  // set to win face
                else
                    game.faceButton.setIcon(game.faces[3]);  // set to dead face
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent me) 
    {
    }

    @Override
    public void mouseExited(MouseEvent me) 
    {
    }
    
}
