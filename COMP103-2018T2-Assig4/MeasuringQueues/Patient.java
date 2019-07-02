// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2018T2, Assignment 4
 * Name: Luisa Kristen
 * Username: kristeluis
 * ID: 300444458
 */
import ecs100.*;
import java.awt.Color;
import java.util.*;
import java.io.*;

/** 
 * Cutdown version of a Patient with nothing but a priority for ordering
 */

public class Patient implements Comparable<Patient>{

    private static final Random rand = new Random();  // the random number generator

    private int priority;  

    /**
     * Construct a new Patient object
     * with random priorities
     */
    public Patient(){
        priority = rand.nextInt()>>>12; //random (non-negative) priority, 0 .. 2^20
    }


    /** 
     * Compare this Patient with another Patient to determine who should
     *  be treated first.
     * A patient should be earlier in the ordering if they should be treated first.
     * The ordering depends on the triage priority and the arrival time.
     */
    public int compareTo(Patient other){
        if (this.priority < other.priority) { return -1; }
        if (this.priority > other.priority) { return 1; }
        return 0;
    }

    /** toString */
    public String toString(){
        return "P:"+priority;
    }


}
