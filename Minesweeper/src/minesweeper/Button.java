/*
 * Button class for Minesweeper Project
 * Written by Jonathan Wang
 */
package minesweeper;

import java.awt.*;
import javax.swing.JButton;
/**
 *
 * @author jonathan.wang
 */
public class Button extends JButton
{
    // initialize fields
    private int value;  // number to display, -1 means mine
    private int[] location;  // location of the button on the grid
    private boolean visible;  // if the button has been clicked or not
    private boolean flagged;  // if the button has been flagged or not
    public boolean isHappyFace;  // if the button is the happy face button
    public boolean isResizeButton;  // if the button can resize the game board
    public boolean isOptionButton;
    public boolean isStatsButton;  // if the button shows the stats for each difficulty
    // only applies to resize buttons:
    public int[] parameters;  // rows, cols, no. of mines
    
    // constructor
    public Button()
    {
        value = 0;
        location = new int[2];
        visible = false;
        flagged = false;
        isHappyFace = false;
        isResizeButton = false;
        isOptionButton = false;
        isStatsButton = false;
        parameters = new int[3];
        
        setPreferredSize(new Dimension(32, 32));  // set size to 32x32p
    }
    
    // returns the value of the button
    public int getValue()
    {
        return value;
    }
    
    // sets the value of the button
    public void setValue(int inValue)
    {
        value = inValue;
    }
    
    // both getLocation and location don't work for method names...
    // apparently they are both taken in the JButton class or something.
    public int[] buttonLocation()
    {
        return location;
    }
    
    // sets the location of the button
    public void setLocation(int[] inLocation)
    {
        location = inLocation;
    }
    
    // returns if button has been clicked or not
    public boolean isExposed()
    {
        return visible;
    }
    
    // after button has been clicked
    public void expose()
    {
        visible = true;
    }
    
    // if button has been flagged or not
    public boolean isFlagged()
    {
        return flagged;
    }
    
    // flag it
    public void flag()
    {
        flagged = true;
    }
    
    // don't flag it
    public void unflag()
    {
        flagged = false;
    }
}
