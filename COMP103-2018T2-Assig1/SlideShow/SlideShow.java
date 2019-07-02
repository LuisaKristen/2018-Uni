// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2018T2, Assignment 1
 * Name: Luisa Kristen 
 * Username: Kristeluis 
 * ID: 300444458
 */

import java.util.*;
import ecs100.*;
import java.awt.Color;
import java.io.*;

/**
 * This class contains the main method of the program. 
 * 
 * A SlideShow object represents the slideshow application and sets up the buttons in the UI. 
 * 
 * @author pondy
 */
public class SlideShow {

    public static final int LARGE_SIZE = 450;   // size of images during slide show
    public static final int SMALL_SIZE = 100;   // size of images when editing list
    public static final int GAP = 10;           // gap between images when editing
    public static final int COLUMNS = 6;        // Number of columns of thumbnails
    private List<String> images; //  List of image file names. 
    private int currentImage = -1;     // index of currently selected image.
    private int clickImage=-1;
    // Should always be a valid index if there are any images

    private boolean showRunning;      // flag signalling whether the slideshow is running or not
    private boolean paused;
    /**
     * Constructor 
     */
    public SlideShow() {
        this.setupGUI();
        this.images=new ArrayList<String>();
    }

    /**
     * Initialises the UI window, and sets up the buttons. 
     */
    public void setupGUI() {
        UI.initialise();

        UI.addButton("Run show",   this::runShow);
        UI.addButton("Edit show",    this::editShow);
        UI.addButton("add before",   this::addBefore);
        UI.addButton("add after",    this::addAfter);
        UI.addButton("move left",      this:: moveLeft);
        UI.addButton("move right",     this:: moveRight);
        UI.addButton("move to start",  this:: moveStart);
        UI.addButton("move to end",    this:: moveEnd);
        UI.addButton("remove",       this::remove);
        UI.addButton("remove all",   this::removeAll);
        UI.addButton("reverse",      this::reverse);
        UI.addButton("shuffle",      this::shuffle);
        UI.addButton("Testing",      this::setTestList);
        UI.addButton("Help",          this::help);
        UI.addButton("Quit",         UI::quit);

        UI.setKeyListener(this::doKey);
        UI.setMouseListener(this::doMouse);
        UI.setDivider(0);
        UI.printMessage("Mouse must be over graphics pane to use the keys, press 'Help' while editing for help.");

    }

    // RUNNING
    /**
     * As long as the show isn't already running, and there are some
     * images to show, start the show running from the currently selected image.
     * The show should keep running indefinitely, as long as the
     * showRunning field is still true.
     * Cycles through the images, going back to the start when it gets to the end.
     * The currentImage field should always contain the index of the current image.
     */
    public void runShow(){
        this.showRunning=true;
        this.currentImage--; //Slideshow starts by changing to next image, so going back one starts at the correct one. 
        this.display();
    }

    /**
     * Stop the show by changing showRunning to false.
     * Redisplay the list of images, so they can be edited
     */
    public void editShow(){
        this.showRunning=false;
        this.display();
    }

    /**
     * Display just the current slide if the show is running.
     * If the show is not running, display the list of images
     * (as thumbnails) highlighting the current image
     */
    public void display(){
        UI.clearGraphics();
        int colCount=0;
        int rowCount=0;
        while (this.showRunning){
            if (this.images.size()!=0){
                if (this.paused==false){
                    this.currentImage++;
                    UI.clearGraphics();
                }
                if (this.currentImage==this.images.size()){ //restarts
                    this.currentImage=0;
                }
                String name=this.images.get(this.currentImage);
                UI.drawImage(name,GAP,GAP,LARGE_SIZE,LARGE_SIZE);
                if (this.paused) return;
                UI.sleep(1000); 
            }
            else{this.showRunning=false;} //if there are no pictures to show it goes back to edit mode
        }

        if(this.showRunning==false){
            UI.clearGraphics();
            for (int i=0; i<this.images.size(); i++){ //draws all the images
                String name=this.images.get(i);
                if (i%COLUMNS==0&& i!=0){
                    colCount=0;
                    rowCount++;
                }
                double x=(SMALL_SIZE+GAP)*colCount+GAP;
                double y=(SMALL_SIZE+GAP)*rowCount+GAP;
                UI.drawImage(name,x, y,SMALL_SIZE,SMALL_SIZE);
                colCount++;
            }
            if(this.currentImage!=-1){ //draws the red rectangle around the selected image, if there is one
                UI.setColor(Color.red); 
                UI.setLineWidth(3);
                colCount=this.currentImage%COLUMNS;
                rowCount=this.currentImage/COLUMNS;
                double selectX=(SMALL_SIZE+GAP)*colCount+GAP;
                double selectY=(SMALL_SIZE+GAP)*rowCount+GAP;
                UI.drawRect(selectX,selectY,SMALL_SIZE,SMALL_SIZE);

            }
        }
    }

    // Other Methods (you will need quite a lot of additional methods).

    /**
     * Adds images before the currently selected image
     */
    public void addBefore(){
        if(this.showRunning==false){ //cannot add images while it is running
            String name=UIFileChooser.open();
            if (name==null){
                UI.printMessage("You did not select a file");
                return;}
            int pos;
            if (this.images.size()==0){
                pos=0;
            }
            else{
                pos=this.currentImage-1;
            }
            this.images.add(pos,name);
            this.currentImage=pos;
            this.display();
        }
    }

    /**
     * Adds image after the currently selected image
     */public void addAfter(){
        if(this.showRunning==false){//cannot add images while it is running
            String name=UIFileChooser.open();
            if (name==null){
                UI.printMessage("You did not select a file");
                return;}
            int pos=this.currentImage+1;
            this.images.add(pos,name);
            this.currentImage=pos;
            this.display();
        }
    }

    /**
     * Moves the currently selected image left
     */public void moveLeft(){
        if(this.showRunning==false&&this.currentImage!=0){//cannot move images while it is running
            String curImage=this.images.get(this.currentImage);//holds the string of the current image
            String holdImage=this.images.get(this.currentImage-1);//holds the string of the image being swapped
            this.images.set(this.currentImage-1,curImage); //swaps images
            this.images.set(this.currentImage,holdImage);
            this.currentImage--; //reselects current image
            this.display();
        }
    }

    /**
     * Moves the current image right 
     */public void moveRight(){
        if(this.showRunning==false&&this.currentImage!=this.images.size()-1){//cannot move images while it is running
            String curImage=this.images.get(this.currentImage);//holds the string of the current image
            String holdImage=this.images.get(this.currentImage+1); //holds the string of the image being swapped
            this.images.set(this.currentImage+1,curImage);//swaps images
            this.images.set(this.currentImage,holdImage);
            this.currentImage++;  //reselects current image
            this.display(); 
        }
    }

    /**
     * Moves the currently selected image to the start of the list 
     */
    public void moveStart(){
        if(this.currentImage!=0&&this.images.size()!=0&&this.showRunning==false){//cannot move images while it is running
            String curImage=this.images.get(this.currentImage);//holds the string of the current image
            this.images.remove(this.currentImage);//delets the old position
            this.images.add(0,curImage);//adds the image to the start of the list
            this.currentImage=0;  //reselects current image
            this.display(); 
        }
    }

    /**
     * Moves the currently selected image to the end of the list
     */
    public void moveEnd(){
        if(this.showRunning==false&&this.currentImage!=this.images.size()-1&&this.images.size()!=0){//cannot move images while it is running
            String curImage=this.images.get(this.currentImage);//holds the string of the current image
            this.images.remove(this.currentImage);//delets the old position
            this.images.add(curImage);//adds the image to the start of the list
            this.currentImage=this.images.size()-1;  //reselects current image
            this.display(); 
        }
    }

    /**
     * Moves the currently selected image up one row
     */
    public void moveUp(){
        if (this.showRunning==false){
            if (this.currentImage>COLUMNS){
                String imageHold=this.images.get(this.currentImage);
                this.images.remove(this.currentImage);
                this.images.add(this.currentImage-COLUMNS,imageHold);
                this.currentImage=this.currentImage-COLUMNS;
            }
            this.display(); 
        }
    }

    /**
     * Moves the currently selected image down one row
     */
    public void moveDown(){
        if (this.showRunning==false){
            int totalRows=this.images.size()/COLUMNS;
            int currentRow=this.currentImage/COLUMNS;
            if(totalRows!=currentRow){
                String curImage=this.images.get(this.currentImage);//holds the string of the current image
                this.images.remove(this.currentImage);//delets the old position
                if (this.currentImage+COLUMNS>=this.images.size()){
                    this.images.add(curImage);
                    this.currentImage=this.images.size()-1;
                }
                else{
                    this.images.add(this.currentImage+COLUMNS,curImage);
                    this.currentImage=this.currentImage+COLUMNS;
                }
            }

            this.display(); 
        }
    }

    /**
     * Removes the currently selected image
     */
    public void remove(){
        if(this.showRunning==false&&this.currentImage!=-1){//cannot remove images while it is running, or delete current image if there is none
            this.images.remove(this.currentImage);
            if (this.images.size()==0){
                this.currentImage=-1;
            }
            this.display();
        }
    }

    /**
     * Removes all images from the ArrayList
     */
    public void removeAll(){
        if(this.showRunning==false){//cannot remove images while it is running
            this.images.clear();
            this.currentImage=-1;
            UI.clearGraphics();
        }
    }

    /**
     * Reverses the ArrayList 
     */
    public void reverse(){
        if(this.showRunning==false){//cannot edit images while it is running
            Collections.reverse(this.images);
            this.display();
        }
    }

    /**
     * Shuffles the ArrayList
     */
    public void shuffle(){
        if(this.showRunning==false){//cannot edit images while it is running
            Collections.shuffle(this.images);
            this.display();
        }
    }

    /**
     * Enables the user to load a file of image names
     */
    public void load(){
        try {
            if(this.showRunning==false){
                File myFile = new File(UIFileChooser.open());
                if(myFile==null) return;
                else if (myFile.getName().endsWith(".txt")){
                    Scanner scan = new Scanner(myFile);
                    this.images.clear();
                    while(scan.hasNextLine()) {
                        String name = scan.nextLine();
                        this.images.add(name);
                    }
                }
                else {
                    UI.printMessage("File must end in .txt");
                    return;
                }
            }
        }
        catch(IOException e) {UI.printf("File Failure %s \n", e);}
        this.display();
    }

    /**
     * Enables the user to save the images to a file
     */
    public void save(){
        try {
            if(this.showRunning==false){
                File myFile=(new File(UIFileChooser.save()));
                PrintStream out = new PrintStream(myFile);
                if (out==null)return;
                else if (myFile.getName().endsWith(".txt")){
                    for (int i=0; i<this.images.size(); i++){ 
                        out.println(this.images.get(i));
                    }
                    out.close();
                    UI.printMessage("Saved Successfully");
                }
                else {
                    UI.printMessage("File must end in .txt");
                    return;
                }
            }
        }
        catch(IOException e) {
            UI.printf("File Failure %s \n", e);
        }

    }

    /**
     * This displays a help message to show what buttons can be used. 
     */
    public void help(){
        if (this.showRunning){
            this.showRunning=false;
        }
        UI.clearGraphics();
        UI.drawImage("help.jpg",10,10,LARGE_SIZE,LARGE_SIZE);

    }

    /**
     * Controls the mouse, used for selecting and dragging images
     */
    public void doMouse(String action, double x, double y){

        if(this.showRunning==false&&this.currentImage!=-1){//cannot edit images while it is running
            if (action.equals("pressed")){
                this.clickImage=this.checkClick(x,y);
                this.currentImage=this.clickImage;
            }
            if (action.equals("released")){
                int image2=this.checkClick(x,y);
                if (this.clickImage!=image2){
                    String imageHold=this.images.get(this.clickImage);
                    this.images.remove(this.clickImage);
                    this.images.add(image2,imageHold);
                }
                this.currentImage=image2;
            }
        }
        this.display();
    }

    /**
     * Checks where the mouse was clicked
     */
    public int checkClick(double x, double y){
        int sizing=(int) SMALL_SIZE+GAP;
        int left= (int)x;
        int top=(int)y;
        int col=left/sizing;
        int row=(top/sizing);
        int image=(row*COLUMNS)+col;
        if (image>this.images.size()-1){
            return this.images.size()-1;
        }
        return image;
    }

    /**
     * Selects the image to the left of the currently seleccted image
     */
    public void goLeft(){
        if(this.currentImage!=0){
            if(this.showRunning==false ||(this.paused&&this.showRunning)){
                this.currentImage--;
                this.display(); 
            }
        }
    }

    /**
     * Selects the image to the right of the currently seleccted image
     */
    public void goRight(){
        if(this.currentImage!=this.images.size()-1){
            if(this.showRunning==false ||(this.paused&&this.showRunning)){
                this.currentImage++;
                this.display(); 
            }
        }
    }

    /**
     * Selects the image at the start of the list
     */
    public void goStart(){
        if(this.currentImage!=0){
            if(this.showRunning==false ||(this.paused&&this.showRunning)){
                this.currentImage=0; 
                this.display(); 
            } 
        }
    }

    /**
     * Selects the image at the end of the list
     */
    public void goEnd(){
        if(this.currentImage!=this.images.size()-1&&this.showRunning==false){

            if(this.showRunning==false ||(this.paused&&this.showRunning)){
                this.currentImage=this.images.size()-1;
                this.display(); 
            }
        }
    }

    /**
     * Selects the image above of the currently seleccted image
     */
    public void goUp(){
        if (this.showRunning==false){
            if (this.currentImage>COLUMNS){
                this.currentImage=this.currentImage-COLUMNS;
            }
            this.display(); 
        }
    }

    /**
     * Pauses Slideshow
     */
    public void goPause(){
        if (this.showRunning){
            this.paused=!this.paused;
            if (this.paused) UI.printMessage("Paused");
            else UI.printMessage("Playing");
            this.display(); 
        }
    }

    /**
     * Selects the image below of the currently seleccted image, if there is none it jumps to the end of the list
     */
    public void goDown(){
        if(this.showRunning==false){
            int totalRows=this.images.size()/COLUMNS;
            int currentRow=this.currentImage/COLUMNS;
            if(totalRows!=currentRow){
                this.currentImage=this.currentImage+COLUMNS;
            }
            if (this.currentImage>=this.images.size()){ //cannot select an "image" that is after the end
                this.currentImage=this.images.size()-1;  
            }
            this.display(); 
        }
    }

    // More methods for the user interface: keys (and mouse, for challenge)
    /**
     * Interprets key presses.
     * works in both editing the list and in the slide show.
     */  
    public void doKey(String key) {
        if (key.equals("Left"))         goLeft();
        else if (key.equals("Right"))   goRight();
        else if (key.equals("Home"))    goStart();
        else if (key.equals("End"))     goEnd();
        else if (key.equals("Up"))     goUp();
        else if (key.equals("Down"))     goDown();
        else if (key.equals("z")) goPause();
        else if (key.equals("x")) save();
        else if (key.equals("c")) load();
        else if (key.equals("w")) moveUp();
        else if (key.equals("a")) moveLeft();
        else if (key.equals("s")) moveDown();
        else if (key.equals("d")) moveRight();
        else if (key.equals("v")) addAfter();
        else if (key.equals ("b")) remove();
    }

    /**
     * A method that adds a bunch of names to the list of images, for testing.
     */
    public void setTestList(){
        if (showRunning) return;
        String[] names = new String[] {"Atmosphere.jpg", "BachalpseeFlowers.jpg",
                "BoraBora.jpg", "Branch.jpg", "DesertHills.jpg",
                "DropsOfDew.jpg", "Earth_Apollo17.jpg",
                "Frame.jpg", "Galunggung.jpg", "HopetounFalls.jpg",
                "Palma.jpg", "Sky.jpg", "SoapBubble.jpg",
                "Sunrise.jpg", "Winter.jpg"};

        for(String name : names){
            images.add(name);
        }
        currentImage = 0;
        display();
    }

    public static void main(String[] args) {
        new SlideShow();
    }

}
