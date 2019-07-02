// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2018T2, Assignment 2
 * Name: Luisa Kristen
 * Username: kristeluis
 * ID: 300444458
 */

import ecs100.*;
import java.awt.Color;
import java.util.*;
import java.io.*;

/** 
 *  Renders a molecule on the graphics pane from different positions.
 *  
 *  A molecule consists of a collection of molecule elements, i.e., atoms.
 *  Each atom has a type or element (eg, Carbon, or Hydrogen, or Oxygen, ..),
 *  and a three dimensional position in the molecule (x, y, z).
 *
 *  Each molecule is described in a file by a list of atoms and their positions.
 *  The molecule is rendered by drawing a colored circle for each atom.
 *  The size and color of each atom is determined by the type of the atom.
 * 
 *  The description of the size and color for rendering the different types of
 *  atoms is stored in the file "element-info.txt" which should be read and
 *  stored in a Map.  When an atom is rendered, the element type should be looked up in
 *  the map to find the size and color.
 * 
 *  A molecule can be rendered from different perspectives, and the program
 *  provides buttons to control the perspective of the rendering.
 *  
 */

public class MoleculeRenderer {

    public static final double MIDX = 300;   // The middle on the (x axis)
    public static final double MIDY = 0;     // The middle on the (y axis)
    public static final double MIDZ = 200;   // The middle depth (z axis)

    // Map containing info about the size and color of each element type.
    private Map<String, ElementInfo> elements; 

    // The collection of the atoms in the current molecule
    private List<Atom> molecule = new ArrayList<Atom>();  

    //angle that the molecule is currently displayed at
    private double theta=0;
    private double phi=0;

    private boolean withBonds; //only true if molecule contains bonds
    // Constructor:
    /** 
     * Sets up the Graphical User Interface and reads the file of element data of
     * each possible type of atom into a Map: where the type is the key
     * and an ElementInfo object is the value (containing size and color).
     */
    public MoleculeRenderer() {
        setupGUI();
        readElementInfo();    //  Read the atom info
    }

    public void setupGUI(){
        UI.addButton("Molecule", this::loadMolecule);
        UI.addButton("FromFront", this::showFromFront);
        UI.addButton("FromBack", this::showFromBack);
        UI.addButton("FromRight", this::showFromRight);
        UI.addButton("FromLeft", this::showFromLeft);
        UI.addButton("FromTop", this::showFromTop);
        UI.addButton("FromBot", this::showFromBot);
        UI.addButton("Quit", UI::quit);
        UI.setDivider(0.0);

        UI.setKeyListener(this::doKey);
    }

    
    /** 
     * Reads the file "element-info.txt" which contains radius and color
     * information about each type of element:
     *   element name, a radius, and red, green, blue, components of the color (integers)
     * Stores the info in a Map in the elements field.
     * The element name is the key.
     */
    private void readElementInfo() {
        elements= new HashMap <String, ElementInfo> ();
        UI.printMessage("Reading the element information...");
        try {
            Scanner scan = new Scanner(new File("element-info.txt"));
            while (scan.hasNextLine()){
                Scanner lineSc=new Scanner(scan.nextLine());
                String name=lineSc.next();
                int rad=lineSc.nextInt();
                int red=lineSc.nextInt();
                int green=lineSc.nextInt();
                int blue=lineSc.nextInt();
                new Color (red,green,blue);
                ElementInfo e= new ElementInfo(name,rad,new Color (red,green,blue));
                elements.put(name,e);
            }
            UI.printMessage("Reading element information: " + elements.size() + " elements found.");
        }
        catch (IOException ex) {UI.printMessage("Reading element information FAILED."+ex);}
    }

    /**
     * Ask the user to choose a file that contains info about a molecule,
     * load the information, and render it on the graphics pane.
     */
    public void loadMolecule(){
        String filename = UIFileChooser.open();
        readMoleculeFile(filename);
        showFromFront();
    }

    /** 
     * Reads the molecule data from a file containing one line for
     *  each atom in the molecule.
     * Each line contains an atom type and the 3D coordinates of the atom.
     * For each atom, the method constructs a Atom object,
     * and adds it to the List of molecule elements in the molecule.
     * To obtain the color and the size of each atom, it has to look up the name
     * of the atom in the Map of ElementInfo objects.
     */
    public void readMoleculeFile(String fname) {
        if (fname.contains("bonds")){
            renderWithBonds(fname);
        }
        else{
            withBonds=false;
            try {
                molecule = new ArrayList<Atom>();  
                Scanner scan = new Scanner(new File(fname));
                while (scan.hasNextLine()){
                    Scanner lineSc=new Scanner(scan.nextLine());
                    String name=lineSc.next();
                    int x=lineSc.nextInt();
                    int y=lineSc.nextInt();
                    int z=lineSc.nextInt();
                    Atom atom = new Atom(x,y,z,name);
                    molecule.add(atom);
                }
            }
            catch(IOException ex) {
                UI.println("Reading molecule file " + fname + " failed."+ex);
            }
        }
    }

    /**
     * Renders the molecule from the front.
     * Sorts the Atoms in the List by their z value, back to front
     * Uses the default ordering of the Atoms
     * Then renders each atom at the position (MIDX+x,y)
     */
    public void showFromFront() {
        Collections.sort(molecule,(a,b)->a.compareTo(b));

        UI.clearGraphics();
        for(Atom atom : molecule) {
            atom.render(MIDX+atom.getX(), atom.getY(), elements);
            if (withBonds){
                List <Integer> bonds=atom.getBonds();
                int startX=(int)atom.getX();
                int startY=(int)atom.getY();
                for (int bond:bonds){
                    int endX=(int)molecule.get(bond).getX();
                    int endY=(int)molecule.get(bond).getY();

                    UI.setColor(Color.black);
                    UI.drawLine(MIDX+startX, startY, MIDX+endX, endY);
                }
            }
        }

        resetPhiTheta();
    }

    /**
     * Renders the molecule from the back.
     * Sorts the Atoms in the List by their z value (front to back)
     * Then renders each atom at (MIDX-x,y) position
     */
    public void showFromBack() {
        Collections.sort(molecule,(a,b)->a.compareTo(b));
        Collections.reverse(molecule);
        UI.clearGraphics();

        for(Atom atom : molecule) {
            atom.render(MIDX-atom.getX(), atom.getY(), elements);
            if (withBonds){
                List <Integer> bonds=atom.getBonds();
                int startX=(int)atom.getX();
                int startY=(int)atom.getY();
                for (int bond:bonds){
                    int endX=(int)molecule.get(bond).getX();
                    int endY=(int)molecule.get(bond).getY();

                    UI.setColor(Color.black);
                    UI.drawLine(MIDX-startX, startY, MIDX-endX, endY);
                }
            }
        }
        resetPhiTheta();
    }

    /**
     * Renders the molecule from the left.
     * Sorts the Atoms in the List by their x value (larger x first)
     * Then renders each atom at (MIDZ-z,y) position
     */
    public void showFromLeft() {
        Collections.sort(molecule,(a,b)->{ 
                if (a.getX()>b.getX()){return -1;}
                else if(a.getX()==b.getX()){return 0;}
                else{return 1;}});

        UI.clearGraphics();
        for(Atom atom : molecule) {
            atom.render(MIDZ-atom.getZ(), atom.getY(), elements);
        }
        resetPhiTheta();
    }

    /**
     * Renders the molecule from the right.
     * Sorts the Atoms in the List by their x value (smaller x first)
     * Then renders each atom at (MIDZ+z,y) position
     */
    public void showFromRight() {
        Collections.sort(molecule,(a,b)->{ 
                if (a.getX()>b.getX()){return -1;}
                else if(a.getX()==b.getX()){return 0;}
                else{return 1;}});
        Collections.reverse(molecule);
        UI.clearGraphics();
        for(Atom atom : molecule) {
            atom.render(MIDZ+atom.getZ(), atom.getY(), elements);
        }
        resetPhiTheta();
    }

    /**
     * Renders the molecule from the top.
     * Sorts the Atoms in the List by their y value (larger y first)
     * Then renders each atom at (MIDX+x, MIDZ-z) position
     */
    public void showFromTop() {
        Collections.sort(molecule,(a,b)->{ 
                if (a.getY()>b.getY()){return -1;}
                else if(a.getY()==b.getY()){return 0;}
                else{return 1;}});

        UI.clearGraphics();
        for(Atom atom : molecule) {
            atom.render(MIDX+atom.getX(), MIDZ-atom.getZ(), elements);
        }
        resetPhiTheta();
    }

    /**
     * Renders the molecule from the bottom.
     * Sorts the Atoms in the List by their y value (smaller y first)
     * Then renders each atom at (MIDX+x, MIDZ+z) position
     */
    public void showFromBot() {
        Collections.sort(molecule,(a,b)->{ 
                if (a.getY()>b.getY()){return -1;}
                else if(a.getY()==b.getY()){return 0;}
                else{return 1;}});
        Collections.reverse(molecule);
        UI.clearGraphics();
        for(Atom atom : molecule) {
            atom.render(MIDX+atom.getX(), MIDZ+atom.getZ(), elements);
        }
        resetPhiTheta();
    }

    /**
     * Responds to the direction chosen by the user - using the keys
     * 
     */
    public void viewFromDirection(){
        double thetaRad = theta * Math.PI / 180;
        double phiRad = phi * Math.PI / 180;

        double cosTheta=Math.cos(thetaRad);
        double sinTheta=Math.sin(thetaRad);

        double sinPhi=Math.sin(phiRad);
        double cosPhi=Math.cos(phiRad);

        Collections.sort(molecule,(a,b)->{
                double bZ = b.getX()*cosPhi - b.getY()*sinPhi;
                double aZ = a.getX()*cosPhi - a.getY()*sinPhi;

                return (int)(bZ - aZ);  
            });

        UI.clearGraphics();
        for(Atom atom : molecule) {
            double rad=elements.get(atom.getKind()).getRadius();
            double XAfter = atom.getZ()*sinTheta + atom.getX()*cosTheta -rad;
            double YAfter= atom.getZ()*sinPhi+atom.getY()*cosPhi+rad;
            atom.render(MIDX+XAfter,MIDZ+YAfter, elements);
        }
    }

    public void resetPhiTheta(){
        theta=0;
        phi=0;
    }

    /**
     * Interprets key presses.
     */  
    public void doKey(String key) {
        if (key.equals("Left")){
            theta-=5;
            viewFromDirection();
            if (theta>360){
                theta=0;
            }
        }
        else if (key.equals("Right")){
            theta+=5;
            viewFromDirection();
            if (theta>360){
                theta=0;
            }
        }
        else if (key.equals("Up")){
            phi-=5;
            viewFromDirection();
            if (phi>360){
                phi=0;
            }
        }
        else if (key.equals("Down")){
            phi+=5;
            viewFromDirection();
            if (phi>360){
                phi=0;
            }
        }
    }

    public void renderWithBonds(String fname){
        withBonds=true;
        try {
            molecule = new ArrayList<Atom>();  
            Scanner scan = new Scanner(new File(fname));
            while (scan.hasNextLine()){
                Scanner lineSc=new Scanner(scan.nextLine());
                if (!lineSc.hasNextInt()){
                    String name=lineSc.next();
                    int x=lineSc.nextInt();
                    int y=lineSc.nextInt();
                    int z=lineSc.nextInt();
                    Atom atom = new Atom(x,y,z,name);
                    molecule.add(atom);
                }
                else{
                    int bondStart=lineSc.nextInt();
                    int bondEnd=lineSc.nextInt();
                    molecule.get(bondStart).addBond(bondEnd);
                }
            }
        }
        catch(IOException ex) {
            UI.println("Reading molecule file " + fname + " failed."+ex);
        }
        showFromFront();
    }

    public static void main(String args[]) {
        new MoleculeRenderer();
    }
}
