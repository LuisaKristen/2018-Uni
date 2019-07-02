// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2018T2, Assignment 6
 * Name: Luisa Kristen
 * Username: kristeluis
 * ID: 300444458
 */

import ecs100.UI;
import java.awt.*;
import java.util.*;

/**
 * Search for a path to the goal in a maze.
 * The maze consists of a graph of Cells:
 *  Each cell has a collection of neighbouring cells.
 *  Each cell can be "visited" and it will remember that it has been visited
 *  A Cell is Iterable, so that you can iterate through its neighbour cells with
 *    for(Cell neigh : cell){....
 *
 * The maze has a goal cell (in the bottom right corner)
 * The user can click on a cell, and the program will search for a path
 * from that cell to the goal.
 * 
 * Every cell that is looked at during the search is coloured  yellow, and then,
 * if the cell turns out to be on a dead end, it is coloured red.
 */

public class MazeSearch {

    public static final int DELAY = 20;

    private Maze maze;
    private String search = "first";   // "first", "all", or "shortest"
    private int pathCount = 0;
    boolean stopped=false;
    
    int currentBest=Integer.MAX_VALUE;
    int currentCount;
    ArrayList <Cell> myPath=new ArrayList <Cell>();
    ArrayList <Cell> bestPath=new ArrayList <Cell>();
    
    public MazeSearch() {
        setupGui();
        makeMaze(10);
    }

    public void setupGui(){
        UI.addTextField("Maze Size", (String v)->{makeMaze(Integer.parseInt(v));});
        UI.setMouseListener(this::doMouse);
        UI.addButton("First path",    ()->{search="first";});
        UI.addButton("All paths",     ()->{search="all";});
        UI.addButton("Shortest path", ()->{search="shortest";;
            });
        UI.addButton("Stop", ()->{stopped=!stopped;});
        UI.addButton("Quit", UI::quit);
    }

    /**
     * Creates a new maze and draws it .
     */
    public void makeMaze(int size){
        maze = new Maze(size);
        maze.draw();
    }

    /**
     * Clicking the mouse on a cell should make the program
     * search for a path from the clicked cell to the goal.
     */
    public void doMouse(String action, double x, double y){
        if (action.equals("released")){
            UI.printMessage("");//clear the previous string
            UI.clearText();
            maze.reset();
            maze.draw();
            pathCount = 0;
            Cell start = maze.getCellAt(x, y);
            if (search=="first"){
                exploreCell(start);
            }
            else if (search=="all"){
                exploreCellAll(start);
                UI.printMessage(pathCount+" Paths found");
            }
            if (search=="shortest"){
                currentBest=Integer.MAX_VALUE;
                bestPath=new ArrayList <Cell>();
                myPath=new ArrayList <Cell>();
                exploreCellShortest(start);
                drawBest();
            }
        }
    }

    /**
     * Search for a path from a cell to the goal.
     * Return true if we got to the goal via this cell (and don't
     *  search for any more paths).
     * Return false if there is not a path via this cell.
     * 
     * If the cell is the goal, then we have found a path - return true.
     * If the cell is already visited, then abandon this path - return false.
     * Otherwise,
     *  Mark the cell as visited, and colour it yellow (and sleep for a short time)
     *  Recursively try exploring from the cell's neighbouring cells, returning true
     *   if a neighbour leads to the goal
     *  If no neighbour leads to a goal,
     *    colour the cell red (to signal failure)
     *    abandon the path - return false.
     */
    public boolean exploreCell(Cell cell) {
        UI.sleep(10);
        if (cell == maze.getGoal()) {
            cell.draw(Color.blue);   // to indicate finding the goal
            return true;
        }
        cell.visit();
        cell.draw(Color.yellow);
        for(Cell c :cell){
            if ( ! c.isVisited()){
                boolean found = exploreCell(c);
                if (found) {return true;}
            }
        }
        cell.draw(Color.red);
        return false;
    }

    /** COMPLETION
     * Search for all paths from a cell,
     * If we reach the goal, then we have found a complete path,
     *  so pause for 1 second
     * Otherwise,
     *  visit the cell, and colour it yellow
     *  Recursively explore from the cell's neighbours, 
     *  unvisit the cell and colour it white.
     */
    public void exploreCellAll(Cell cell) {
        try{
            if (stopped){ 
                throw new RuntimeException("Program Stopped");
            }
            else{
                UI.sleep(10);
                if (cell==maze.getGoal()){
                    cell.draw(Color.blue);   // to indicate finding the goal
                    UI.sleep(500);
                    cell.draw(Color.green);   // going back
                    pathCount++;              //found another path
                }
                else {
                    cell.draw(Color.yellow);
                    cell.visit();
                    for(Cell c : cell){
                        if ( ! c.isVisited()){
                            exploreCellAll(c);
                        }
                    }
                    cell.draw(Color.white);
                    cell.unvisit();
                }
            }
        }

        catch(RuntimeException e){}
    }


    /** CHALLENGE
     * Search for shortest path from a cell,
     * Use Breadth first search.
     */
    public void exploreCellShortest(Cell cell) {
        if (cell==maze.getGoal()){
            if (currentCount<currentBest) {
                currentBest=currentCount;
                bestPath=new ArrayList <Cell> (myPath);
            }
        }
        else {
            cell.visit();
            myPath.add(cell);
            currentCount++;
            for(Cell c : cell){
                if ( ! c.isVisited()){
                    exploreCellShortest(c);
                }
            }
            cell.unvisit();
            myPath.remove(cell);
            currentCount--;
        }
    }

    public void drawBest(){
        for (int i=0; i<bestPath.size();i++ ){
            bestPath.get(i).draw(Color.yellow);
        }
    }

    public static void main(String[] args) {
        new MazeSearch();
    }
}

