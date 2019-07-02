// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2018T2, Assignment 6
 * Name: Luisa Kristen
 * Username: kristeluis
 * ID: 300444458
 */

import ecs100.*;
import java.util.*;
import java.awt.Color;
import javax.swing.*;
/** 
 *  Show all permutations of a list of items
 *  The items are names of image files (of emojis)
 *  Displays each possible permutation as a column of images.
 *  If there are too many for the screen, it highlights and keeps replacing the last column.
 *
 *  For the core, you only have to write one method - the extendPermutations(...) method
 *  which does the recursive search.  
 */
public class Permutations {

    public static final List<String> IMG_NAMES =
        Arrays.asList("smiling.png", "omg.png", "halo.png", "crying.png","angry.png",
            "sleeping.png", "astonished.png", "smiling-eyes.png", "sunglasses.png",
            "sweat.png","blue.png", "brown.png", "gray.png", "navy.png", "orange.png", "pink.png");

    public static final double DISPLAY_LEFT = 10;
    public static final double DISPLAY_TOP = 20;
    public static final double IMAGE_SIZE = 40;
    public static final double DISPLAY_SEP = IMAGE_SIZE + 2;

    private List<String> selectedList;  // current list to permute and display

    private int nextCol = 0;            // current row to display.
    private long permutationCount = 0;  // number of permutations found so far.

    private boolean paused=false;
    /**
     * Construct a new Permutations object, setting up the GUI
     */
    public Permutations(){
        setupGUI();
        selectedList = new ArrayList<String>();
        reset();
    }

    /**
     * Buttons to reset and run permutations.
     * Mouse to select images to permute
     */
    public void setupGUI(){
        UI.addButton("Reset list", this::reset);
        UI.addButton("Permute", this::findPermutations);
        UI.addButton("Iterative", this::findPermutationsIterativeHelper);
        UI.addButton("Pause", ()-> paused=!paused);
        UI.addButton("Clear display", this::clear);
        UI.addButton("Quit", UI::quit);
        UI.setMouseListener(this::doMouse);
        UI.setWindowSize(1050,800);
        UI.setDivider(0);
    }

    /**
     * Finds and prints all permutations of the items in selectedList,
     * by calling a recursive method, passing in a set of the items to permute
     * and an empty list to build up.
     * Prints the total number of permutations in the message window (with
     *  UI.printMessage(...);
     */

    public void findPermutations(){
        UI.printMessage(String.format("permuting %d items", selectedList.size()));
        nextCol = 2;
        Set<String> source = new HashSet<String>(selectedList); //a set of the current items
        permutationCount = 0;
        extendPermutation(new ArrayList<String>(), source); 
        UI.printMessage(String.format("FINISHED: %d  items: %,d permutations",
                source.size(), permutationCount));
    }

    /**
     * Recursive method to build all permutations possible by adding the
     *  remaining items on to the end of the permutation built up so far 
     * If there are no remaining items, then permutationSoFar is complete,
     *   so display permutationSoFar and increment permutationCount.
     *   Use the display(...) method to display a permutation
     * Otherwise,
     *  for each of the remaining items,
     *    Extend the permutation with one of the item, and
     *     do a recursive call to extend it more
     *     - remove the item from remaining items
     *     - add it to the permutation so far
     *     - do the recursive call 
     *     - remove the item from the end of the permutation and
     *     - put it back into the remaining items.
     *
     * Remember: you can't modify a collection that you are currently iterating through!!
     *  you may need to make a copy of the remaining items to iterate through it.
     */
    public void extendPermutation(List<String> permutationSoFar, Set<String> remaining){
        if (remaining.size()==0){
            display(permutationSoFar);
            permutationCount++;
        }
        else{
            Set <String>remain2=new HashSet <String> (remaining);
            for (String s :remain2){
                remaining.remove(s);
                permutationSoFar.add(s);
                extendPermutation(permutationSoFar,remaining);
                permutationSoFar.remove(s);
                remaining.add(s);
            }
        }
    }

    /**
     * Finds and prints all permutations of the items in selectedList,
     * by calling a recursive method, passing in a set of the items to permute
     * and an empty list to build up.
     * Prints the total number of permutations in the message window (with
     *  UI.printMessage(...);
     */
    public void findPermutationsIterativeHelper(){
        UI.printMessage(String.format("permuting %d items", selectedList.size()));
        nextCol = 2;
        List<String> source = new ArrayList<String>(selectedList); //a set of the current items
        permutationCount = 0;
        findPermutationsIterative(source); 
        UI.printMessage(String.format("FINISHED: %d  items: %,d permutations",
                source.size(), permutationCount));
    }

    /**
     * Finds all the permutations using an iterative method, found here:
     * https://en.wikipedia.org/wiki/Heap%27s_algorithm
     * It prints the first one out manually, then finds all permuations.
     */
    public void findPermutationsIterative(List <String> permutationSoFar){
        display(permutationSoFar); // the original 
        permutationCount++;
        int n = permutationSoFar.size();
        int[] p = new int[n];  
        int i =0;
        while (i < n) {
            if (p[i] < i) { 
                if (i % 2 == 0)
                    Collections.swap(permutationSoFar, 0, i);
                else
                    Collections.swap(permutationSoFar, p[i], i);
                display(permutationSoFar); 
                permutationCount++;
                p[i]++; 
                i = 0; 
            }
            else { 
                p[i] = 0;
                i++;
            }
        }
    }

    /**
     * Select an image to add to the list to permute
     */
    public void doMouse(String action, double x, double y){
        if (action.equals("released")){
            int idx = (int) ((y-DISPLAY_TOP)/DISPLAY_SEP);
            if (idx >=0 && idx < IMG_NAMES.size()){
                String img = IMG_NAMES.get(idx);
                if (!selectedList.contains(img)) {
                    selectedList.add(img);
                    nextCol = 1;
                    display(selectedList);
                }
            }
        }
    }            

    /**
     * Reset the selection and redisplay.
     * Clears all the current permutations
     * (but doesn't stop the current permutation search if
     * it is running).
     */
    public void reset(){
        paused=false;
        selectedList.clear();
        UI.clearGraphics();
        UI.drawString("select images:", DISPLAY_LEFT, DISPLAY_TOP-5);
        nextCol = 0;
        display(IMG_NAMES);
        nextCol = 2;
        double x = DISPLAY_LEFT + DISPLAY_SEP - 1;
        double bot = DISPLAY_TOP+DISPLAY_SEP*IMG_NAMES.size();
        UI.drawLine(x, DISPLAY_TOP, x, bot); 
        UI.drawLine(x+DISPLAY_SEP, DISPLAY_TOP, x+DISPLAY_SEP, bot); 
    }

    /**
     * Clears the display of permutations and resets the nextCol back to 2.
     * (the current permutation search continues running).
     */
    public void clear(){
        UI.eraseRect(DISPLAY_LEFT + DISPLAY_SEP *2, 0, 1050, 1000);
        nextCol = 2;
    }

    /**
     * Display a list of images in the next column.
     * The list should have the names of the image files.
     * Uses the field nextCol to specify where the column is.
     * If there is room, it then moves to the next column, but if
     * it is already at the end of the window, it highlights the
     * column and draws over previous list (with a slight delay)
     */
    public void display (List<String> items ){
        if (!paused){
            double x = DISPLAY_LEFT + nextCol*DISPLAY_SEP;
            double y = DISPLAY_TOP;
            // highlight and offset the final column (animated column)
            if (x>=930){
                UI.sleep(1);
                x+=10;
                UI.setColor(LIGHT_YELLOW);
                UI.fillRect(x,y,IMAGE_SIZE, DISPLAY_SEP*items.size());
                UI.setColor(Color.black);
            }
            else {
                UI.sleep(10);
                nextCol++;
                UI.eraseRect(x,y,IMAGE_SIZE, DISPLAY_SEP*items.size());
            }
            // permutation count at the top
            if (nextCol > 2){
                UI.eraseRect(x,y-20,60,20);
                if (permutationCount>100000000){
                    UI.drawString(permutationCount/1000000+"M", x, y-3);
                }
                else if (permutationCount>100000){
                    UI.drawString(permutationCount/1000+"k", x, y-3);
                }
                else {UI.drawString(permutationCount+"", x, y-3);}
            }
            // images in permutation
            for (String it : items){
                UI.drawImage(it, x, y, IMAGE_SIZE, IMAGE_SIZE);
                y+= DISPLAY_SEP;
            }
        }
    }
    private static final Color LIGHT_YELLOW = new Color(255, 255, 180);

    // Main
    public static void main(String[] arguments) {
        new Permutations();
    }
}
