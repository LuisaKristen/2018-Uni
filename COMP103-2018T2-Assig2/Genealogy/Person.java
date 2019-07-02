// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2018T2, Assignment 2
 *  Name:Luisa Kristen   
 * Username: kristeluis
 * ID: 300444458
 */

import ecs100.*;
import java.util.*;

/**
 * Person
 * Has details of one person in the database.
 * Must have the name and year of birth.
 * May have the names of the mother and the father
 *  (null if they are not known)
 * Has a Set of names of the children (which may be empty)
 */

public class Person {
    private String name;
    private int dob;
    private String mother;
    private String father;
    private Set<String> children = new HashSet<String>();

    /**
     * Create a person with name, dob, mother, and father.
     * if mother or father are "-", then leaves the field null.
     * The children field will contain an empty set.
     */
    public Person(String name, int dob, String mother, String father) {
        this.name = name;
        this.dob = dob;
        if (!"-".equals(mother)) {this.mother = mother;}
        if (!"-".equals(father)) {this.father = father;}
    }

    /**
     * Getters
     */
    public String getName()   {return name;}
    public int getDOB()       {return dob;}
    public String getMother() {return mother;}
    public String getFather() {return father;}

    /**
     * Returns an unmodifiable version of the set of children,
     * Other parts of the program can access the children this way,
     * and can loop through the set of children
     * cannot add or remove children from the person through this set.
     */
    public Set<String> getChildren() {
        return Collections.unmodifiableSet(children);
    }

    /**
     * Add a new child (name) to the set of children
     * Has no effect if name is already listed as a child
     */
    public void addChild(String child){
        children.add(child);
    }

    /**
     * Returns a string containing the details of a person.
     * Note that format is like printf, but returns the String
     * instead of printing it.
     * if the mother and/or father are null, then their names
     * will be given as "-    "
     */
    public String toString() {
        return String.format("%-12s (%d) \tM: %s \tF: %s  \tCh:%s",
            name, dob, (mother==null?"-    ":mother),
            (father==null?"-    ":father),
            children!=null?children:"");
    }
}

