// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2018T2, Assignment 3
 * Name: Luisa Kristen
 * Username: kristeluis
 * ID: 300444458
 */

import ecs100.*;
import java.awt.Color;
import javax.swing.JButton;

/**
 *  Simple 'Minesweeper' program.
 *  There is a grid of cells, some of which contain a mine.
 *  The user can click on a cell to either expose it or to
 *  mark/unmark it.
 *  
 *  If the user exposes a cell with a mine, they lose.
 *  Otherwise, it is uncovered, and shows a number which represents the
 *  number of mines in the eight cells surrounding that one.
 *  If there are no mines adjacent to it, then all the unexposed cells
 *  immediately adjacent to it are exposed (and and so on)
 *
 *  If the user marks a cell, then they cannot expose the cell,
 *  (unless they unmark it first)
 *  When all squares with mines are marked, and all the squares without
 *  mines are exposed, the user has won.
 */
public class MineSweeper {

    public static final int ROWS = 15;
    public static final int COLS = 15;

    public static final double LEFT = 10; 
    public static final double TOP = 10;
    public static final double CELL_SIZE = 20;

    // Fields
    private boolean marking;

    private Cell[][] cells;
    private Integer [][] helper;

    private JButton mrkButton;
    private JButton expButton;
    Color defaultColor;

    /** 
     * Construct a new MineSweeper object
     * and set up the GUI
     */
    public MineSweeper(){
        setupGUI();
        setMarking(false);
        makeGrid();
    }

    /** setup buttons */
    public void setupGUI(){
        UI.setMouseListener(this::doMouse);
        UI.addButton("New Game", this::makeGrid);
        this.expButton = UI.addButton("Expose", ()->setMarking(false));
        this.mrkButton = UI.addButton("Mark", ()->setMarking(true));
        UI.setKeyListener(this::doKey);

        UI.addButton("Helper", this::AISolver);
        UI.addButton("Quit", UI::quit);
        UI.setDivider(0.0);
        UI.printMessage("Toggle between marking using z and x keys.");
    }

    /** Respond to mouse events */
    public void doMouse(String action, double x, double y) {

        if (action.equals("released")){
            int row = (int)((y-TOP)/CELL_SIZE);
            int col = (int)((x-LEFT)/CELL_SIZE);
            if (row>=0 && row < ROWS && col >= 0 && col < COLS){
                if (marking) { mark(row, col);}
                else         { tryExpose(row, col); }
            }
        }
    }

    /**
     * Interprets key presses to toggle between Exposing and Marking
     */  
    public void doKey(String key) {
        if (key.equals("z")){       
            setMarking(false);
            UI.printMessage("Exposing,Toggle between marking using z and x keys.");
        }
        else if (key.equals("x")){ 
            setMarking(true);
            UI.printMessage("Marking,Toggle between marking using z and x keys.");
        }
    }

    /**
     * Remember whether it is "Mark" or "Expose"
     * Change the colour of the "Mark", "Expose" buttons
     */
    public void setMarking(boolean v){
        marking=v;
        if (marking) {
            mrkButton.setBackground(Color.red);
            expButton.setBackground(null);
            UI.printMessage("Marking,Toggle between marking using z and x keys.");
        }
        else {
            expButton.setBackground(Color.red);
            mrkButton.setBackground(null);
            UI.printMessage("Exposing,Toggle between marking using z and x keys.");
        }
    }

    /**
     * Redraws the grid.
     */
    public void draw(){
        UI.clearGraphics();
        for (int i=0;i<ROWS;i++){
            for (int j=0;j<COLS;j++){
                cells[i][j].draw(LEFT+CELL_SIZE*j,TOP+CELL_SIZE*i,CELL_SIZE);
            }
        }
    }

    // Other Methods

    /** 
     * The player has clicked on a cell to expose it
     * - if it is already exposed or marked, do nothing.
     * - if it's a mine: lose (call drawLose()) 
     * - otherwise expose it (call exposeCellAt)
     * then check to see if the player has won and call drawWon() if they have.
     * (This method is not recursive)
     */
    public void tryExpose(int row, int col){
        boolean hasLost=false;
        if(cells[row][col].hasMine()){
            drawLose(); 
            hasLost=true;
        }
        else {
            exposeCellAt(row,col);
            draw();
        }

        if (hasWon()&&hasLost==false){
            drawWin();
        }
        AIMaker();
    }

    /** 
     *  Expose a cell, and spread to its neighbours if safe to do so.
     *  It is guaranteed that this cell is safe to expose (ie, does not have a mine).
     *  If it is already exposed, we are done.
     *  Otherwise expose it, and redraw it.
     *  If the number of adjacent mines of this cell is 0, then
     *     expose all its neighbours (which are safe to expose)
     *     (and if they have no adjacent mine, expose their neighbours, and ....)
     */
    public void exposeCellAt(int row, int col){
        if (cells[row][col].isExposed()==true) return;
        cells[row][col].setExposed();
        if (cells[row][col].getAdjacentMines()==0){
            if (row+1<ROWS)exposeCellAt(row+1,col);
            if (row-1>=0)exposeCellAt(row-1,col);
            if (col+1<COLS )exposeCellAt(row,col+1);
            if (col-1>=0 )exposeCellAt(row,col-1);
            if (row+1<ROWS && col+1<COLS )exposeCellAt(row+1,col+1);
            if (row-1>=0 && col-1>=0)exposeCellAt(row-1,col-1);
            if (row+1<ROWS && col-1>=0 )exposeCellAt(row+1,col-1);
            if (row-1>=0 && col+1<COLS)exposeCellAt(row-1,col+1);
        }
    }

    /**
     * Mark/unmark the cell.
     * If the cell is exposed, don't do anything,
     * If it is marked, unmark it.
     * otherwise mark it and redraw.
     * (Marking cannot make the player win or lose)
     */
    public void mark(int row, int col){
        if (cells[row][col].isExposed()==false){
            cells[row][col].toggleMark();
        }
        draw();
    }

    /** 
     * Returns true if the player has won:
     * If all the cells without a mine have been exposed, then the player has won.
     */
    public boolean hasWon(){

        for (int i=0;i<ROWS;i++){
            for (int j=0;j<COLS;j++){
                if (cells[i][j].isExposed()==false && cells[i][j].hasMine()==false){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Construct a grid with random mines.
     */
    public void makeGrid(){
        UI.clearGraphics();
        this.cells = new Cell[ROWS][COLS];
        for (int row=0; row < ROWS; row++){
            double y = TOP+row*CELL_SIZE;
            for (int col=0; col<COLS; col++){
                double x =LEFT+col*CELL_SIZE;
                boolean isMine = Math.random()<0.1;     // approx 1 in 10 cells is a mine 
                this.cells[row][col] = new Cell(isMine);
                this.cells[row][col].draw(x, y, CELL_SIZE);
            }
        }
        // now compute the number of adjacent mines for each cell
        for (int row=0; row<ROWS; row++){
            for (int col=0; col<COLS; col++){
                int count = 0;
                //look at each cell in the neighbourhood.
                for (int r=Math.max(row-1,0); r<Math.min(row+2, ROWS); r++){
                    for (int c=Math.max(col-1,0); c<Math.min(col+2, COLS); c++){
                        if (cells[r][c].hasMine())
                            count++;
                    }
                }
                if (this.cells[row][col].hasMine())
                    count--;  // we weren't suppose to count this cell, just the adjacent ones.

                this.cells[row][col].setAdjacentMines(count);
            }
        }
    }

    /** Draw a message telling the player they have won */
    public void drawWin(){
        UI.setFontSize(28);
        UI.drawString("You Win!", LEFT + COLS*CELL_SIZE + 20, TOP + ROWS*CELL_SIZE/2);
        UI.setFontSize(12);
    }

    /**
     * Draw a message telling the player they have lost
     * and expose all the cells and redraw them
     */
    public void drawLose(){
        for (int row=0; row<ROWS; row++){
            for (int col=0; col<COLS; col++){
                cells[row][col].setExposed();
                cells[row][col].draw(LEFT+col*CELL_SIZE, TOP+row*CELL_SIZE, CELL_SIZE);
            }
        }
        UI.setFontSize(28);
        UI.drawString("You Lose!", LEFT + COLS*CELL_SIZE+20, TOP + ROWS*CELL_SIZE/2);
        UI.setFontSize(12);
    }

    /**
     * Challenge: A non cheating AI helper.
     * This sets up the 2D array and fills it with integers, only updated if something has been changed. 
     */
    public void AIMaker(){
        this.helper= new Integer [ROWS][COLS];
        for (int row=0; row<ROWS; row++){
            for (int col=0; col<COLS; col++){
                if (cells[row][col].isExposed()){
                    helper[row][col]=cells[row][col].getAdjacentMines();
                }
                else{
                    helper[row][col]=-1;
                }
            }
        }

    }

    /**
     * Uses the 2D array created in the AI maker above to suggest a safe move. 
     */
    public void AISolver(){
        UI.printMessage("Shows mines using Blue X, dissapear if another cell is exposed.");
        for (int row=0; row<ROWS; row++){
            for (int col=0; col<COLS; col++){
                if (helper[row][col]>0){
                    int possibleMines=0;
                    for (int i=-1;i<=1;i++){
                        for (int j=-1;j<=1;j++){
                            if(row+i>=0 && row+i<ROWS && col+j >=0 && col+j<COLS && helper[row+i][col+j]==-1){
                                possibleMines++;
                            }
                        }
                    }
                    if (helper[row][col]==possibleMines){
                        for (int i=-1;i<=1;i++){
                            for (int j=-1;j<=1;j++){
                                if(row+i>=0 && row+i<ROWS && col+j >=0 && col+j<COLS && helper[row+i][col+j]==-1){
                                    drawFlag(row+i,col+j);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void drawFlag(int row,int col){
        double x=LEFT+CELL_SIZE*col;
        double y=TOP+CELL_SIZE*row;

        UI.setLineWidth(2);
        UI.setColor(Color.blue);
        UI.drawLine(x+1, y+1, x+CELL_SIZE-1, y+CELL_SIZE-1);
        UI.drawLine(x+1, y+CELL_SIZE-1, x+CELL_SIZE-1, y+1);
        UI.setLineWidth(1);
    }
    // Main
    public static void main(String[] arguments){
        new MineSweeper();
    }        

}
