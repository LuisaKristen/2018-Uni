// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2018T2, Assignment 2
 * Name: Luisa Kristen
 * Username: kristeluis
 * ID: 300444458
 */

import java.awt.Color;
import java.util.*;

/** 
 * Represents information about an atomic element:
 *  the color and size of the circles for rendering it on the graphics pane.
 *  
 */

public class ElementInfo {
    private final String element;
    private final double radius;   // the size of the atom
    private final Color color;

    /** 
     * Constructor requires the name of the element, the radius, and a color.
     */
    public ElementInfo (String element, double radius, Color color) {
        this.element = element;
        this.color = color;
        this.radius = radius;
    }

    public String getElement(){return element;}
    public double getRadius() {return radius;}
    public Color  getColor()  {return color;}

    
}
