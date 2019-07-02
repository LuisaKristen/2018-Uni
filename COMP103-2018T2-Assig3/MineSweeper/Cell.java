// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2018T2, Assignment 3
 * Name: Luisa Kristen
 * Username: kristeluis
 * ID: 300444458
 */

import java.util.*;
import ecs100.*;
import java.awt.Color;
import java.io.*;

/** Represents information about one cell in a MineSweeper map.
 *   It records
 *     its location (Row and Column)
 *     whether it has a mine or not
 *     how many cells around it have mines
 *     whether it is marked or unmarked
 *     whether it is hidden or exposed
 *   Its constructor must specify the location and
 *     whether it has a mine or not
 *   It has methods to 
 *     draw itself, (showing its state appropriately) given origin of the map.
 *     set the number of mines around it.
 *     report whether it has a mine and whether it is exposed
 *     change its state between marked and unmarked
 *     change its state to exposed
 */
public class Cell{
    // Fields
    private boolean mine;
    private int adjacentMines = 0;
    private boolean marked = false;
    private boolean exposed = false;    


    public static final Color LIGHT_GREEN = new Color(0,255,0);
    public static final Color DARK_GREEN = new Color(0,127,0);

    // Constructors
    /** Construct a new Cell object
     */
    public Cell(boolean mine){

        this.mine = mine;
    }

    // Methods
    /** Get the number of mines adjacent to this cell  */
    public int getAdjacentMines(){
        return adjacentMines;
    }

    /** Record the number of adjacent mines */
    public void setAdjacentMines(int num){
        adjacentMines = num;
    }

    /** Does the cell contain a mine? */
    public boolean hasMine(){
        return mine;
    }

    /** Is the cell exposed already? */
    public boolean isExposed(){
        return this.exposed;
    }

    /** Is the cell currently marked? */
    public boolean isMarked(){
        return this.marked;
    }

    /** Set the cell to be exposed? */
    public void setExposed(){
        this.exposed = true;
    }

    /** Toggle the mark */
    public void toggleMark(){
        this.marked = ! this.marked;
    }

    /** Draw the cell */
    public void draw(double x, double y, double size){
        UI.setColor(LIGHT_GREEN);
        UI.fillRect(x, y, size, size);
        if (exposed){ drawExposed(x, y, size); }
        else        { drawHidden(x, y, size); }
    }

    /** Draw white outline and red number or mine */
    private void drawExposed(double x, double y, double size){
        UI.setColor(Color.white);
        UI.drawRect(x, y, size, size);
        UI.setColor(Color.red);
        if (mine){
            UI.setFontSize(16);
            UI.drawString("X", x+size/2-5, y+size/2+7);
            UI.drawString("O", x+size/2-6, y+size/2+7);
            UI.drawString("X", x+size/2-4, y+size/2+7);
            UI.drawString("O", x+size/2-5, y+size/2+7);
            UI.setFontSize(12);
        }
        else if (adjacentMines > 0){
            UI.drawString(""+adjacentMines, x+size/2-5, y+size/2+5);
            UI.drawString(""+adjacentMines, x+size/2-4, y+size/2+5);
        }
    }

    /** Fill dark green with red mark */
    private void drawHidden(double x, double y, double size){
        UI.setColor(DARK_GREEN);
        UI.fillRect(x+1, y+1, size-2, size-2);
        if (marked){
            UI.setLineWidth(2);
            UI.setColor(Color.red);
            UI.drawLine(x+1, y+1, x+size-1, y+size-1);
            UI.drawLine(x+1, y+size-1, x+size-1, y+1);
            UI.setLineWidth(1);
        }
    }

}
