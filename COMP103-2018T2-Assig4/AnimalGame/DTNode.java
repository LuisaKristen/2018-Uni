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
 * A Decision Tree is a structure for getting to an answer by following
 * through a tree of yes/no questions.
 * Each internal node has a question and two children.
 * - the Yes child is the decision tree to follow if the answer to the question is yes
 * - the No child is the decision tree to follow if the answer to the question is no
 * Leaves of the tree do not have a question, but have an Answer.
 *  They should not have any children.
 * A leaf node can be turned into an internal node by giving it a question and two children.
*/

public class DTNode{


    private boolean isQuestionNode;
    private DTNode yes;
    private DTNode no;
    private String text;
    


    /**
     * Construct a new leaf node with an answer.
     */
    public DTNode(String ans){
        isQuestionNode = false;
        text = ans;
    }

    /**
     * Construct a new internal node with a question and two children
     */
    public DTNode(String question, DTNode y, DTNode n){
        isQuestionNode = true;
        text = question;
        yes = y;
        no = n;
    }


    /** Getters */
    public boolean isQuestion(){return isQuestionNode;}
    public String getText(){return text;}
    public DTNode getYes(){return yes;}
    public DTNode getNo(){return no;}

    /**
     * Change a leaf node into an internal question node
     * by providing a question and two subtrees
     */
    public void convertToQuestion(String question, DTNode y, DTNode n){
        if(isQuestionNode){
            throw new RuntimeException("Node is already a question node");
        }
        if(question==null || y==null || n==null){
            throw new RuntimeException("Question nodes must have a question and two valid subtrees");
        }
        isQuestionNode = true;
        text = question;
        yes = y;
        no = n;
    }

    /** Return a String describing the node */
    public String toString(){
        if (isQuestionNode){
            return ("Question: "+text);
        }
        else {
            return ("Decision: "+text);
        }
    }

    public static final int WIDTH = 80;
    public static final int HEIGHT = 15;

    /**
     * Draw the node (as a box with the text) on the graphics pane
     * (x,y) is the center of the box
     * The box should be WIDTH wide, and HEIGHT high, and
     * the text should be truncated to fit.
     */
    public void draw(double x, double y){
        double left = x-WIDTH/2;
        String toDraw = text.substring(0, Math.min(text.length(),15));
        
        UI.eraseRect(left, y-HEIGHT/2, WIDTH, HEIGHT);  // to clear anything behind it
        UI.drawRect(left, y-HEIGHT/2, WIDTH, HEIGHT);
        UI.drawString(toDraw, left+2, y+6);             // assume height of characters = 12
    }
        
    
}
