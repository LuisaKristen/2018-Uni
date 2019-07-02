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

/** 
 *  Represents information about an atom in a molecule.
 *  
 *  The information includes
 *   - the kind of atom
 *   - the 3D position of the atom
 *     x is measured from the left to the right
 *     y is measured from the top to the bottom
 *     z is measured from the front to the back.
 *   
 */

public class Atom implements Comparable<Atom> {

    private String kind;    // the kind of atom.
    // coordinates of center of atom, relative to top left front corner
    private double x;       // distance from the left
    private double y;       // distance from the top
    private double z;       // distance from the front

    private List <Integer> bonds;

    /** Constructor: requires the position and kind  */
    public Atom (double x, double y, double z, String kind) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.kind = kind;
        bonds=new ArrayList <Integer> ();
    }

    /** get X position (distance from the left) */
    public double getX() {return x;}

    /** get Y position (distance from the top) */
    public double getY() {return y;}

    /** get Z position (distance beyond the front) */
    public double getZ() {return z;}

    /** get Kind  */
    public String getKind() {return kind;}

    /** get list of bonds*/
    public List getBonds(){return bonds;}

    /** 
     *  Renders the atom on the graphics pane at the position (u, v).
     *  using a circle of the size and color specified in the elementInfo map.
     *  The circle should also have a black border
     */
    public void render(double u, double v, Map<String, ElementInfo> elementInfo) {
        UI.setColor(elementInfo.get(kind).getColor());
        UI.fillOval(u-elementInfo.get(kind).getRadius(),v-elementInfo.get(kind).getRadius(), 2*elementInfo.get(kind).getRadius(), 2*elementInfo.get(kind).getRadius());
        UI.setColor(Color.black);
        UI.drawOval(u-elementInfo.get(kind).getRadius(),v-elementInfo.get(kind).getRadius(), 2*elementInfo.get(kind).getRadius(),2*elementInfo.get(kind).getRadius());
    
    }

    /**
     * compareToZ returns
     *   -1 if this comes before other
     *    0 if this is the same as other
     *    1 if this comes after other
     * The default ordering is by the z position (back to front)
     * so atoms with large z come before atoms with small z
     */
    public int compareTo(Atom other){
        if (z>other.getZ()){
            return -1;
        }
        else if(z==other.getZ()){
            return 0;
        }
        else{
            return 1;
        }
    }

    public void addBond(int bondedTo){
        bonds.add(bondedTo);
    }
}
