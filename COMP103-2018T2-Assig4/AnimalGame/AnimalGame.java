// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2018T2, Assignment 4
 * Name: Luisa Kristen
 * Username: kristeluis
 * ID: 300444458
 */

/**
 * Guess the Animal Game.
 * The program will play a "guess the animal" game and learn from its mistakes.
 * It has a decision tree for determining the player's animal.
 * When it guesses wrong, it asks the player of another question that would
 *  help it in the future, and adds it to the decision tree. 
 * The program can display the decision tree, and save the tree to a file and load it again,
 *
 * A decision tree is a tree in which all the internal modes have a question, 
 * The answer to the the question determines which way the program will
 *  proceed down the tree.  
 * All the leaf nodes have answers (animals in this case).
 * 
 */

import ecs100.*;
import java.util.*;
import java.io.*;
import java.awt.Color;

public class AnimalGame {

    public DTNode questionsTree;    // root of the decision tree;

    public AnimalGame(){
        setupGUI();
        resetTree();
    }

    /**
     * Set up the interface
     */
    public void setupGUI(){
        UI.addButton("Play", this::play);
        UI.addButton("Print Tree", this::printTree);
        UI.addButton("Draw Tree", this::drawTree);
        UI.addButton("Reset", this::resetTree);
        UI.addButton("Quit", UI::quit);
        UI.setDivider(0.5);
    }

    /**
     * Makes an initial tree with two question nodes and three leaf nodes.
     */
    public void resetTree(){
        questionsTree = new DTNode("has whiskers",
            new DTNode("bigger than person",
                new DTNode("tiger"),
                new DTNode("cat")),
            new DTNode("has trunk",
                new DTNode("Elephant"),
                new DTNode("Snake")));
    }

    /**
     * Play the game.
     * Starts at the top (questionsTree), and works its way down the tree
     *  until it finally gets to a leaf node (with an answer in it)
     * If the current node has a question, then it asks the question in the node,
     * and depending on the answer, goes to the "yes" child or the "no" child.
     * If the current node is a leaf it calls processLeaf on the node
     */
    public void play () {
        DTNode question=questionsTree;
        while(question.isQuestion()){
            String s= UI.askString(question.toString());
            if(s.equalsIgnoreCase("yes")||s.equalsIgnoreCase("y")){
                if (!question.getYes().isQuestion()){
                    processLeaf(question.getYes());
                    return;
                }
                else{
                    question=question.getYes();
                }
            }
            else if(s.equalsIgnoreCase("no")||s.equalsIgnoreCase("n")){
                if (!question.getNo().isQuestion()){
                    processLeaf(question.getNo());
                    return;
                }
                else{
                    question=question.getNo();
                }
            }
            else{
                UI.println("please answer with Yes or No.");
            }
        }
    }

    /**
     * Process a leaf node (a node with an answer in it)
     * Tell the player what the answer is, and ask if it is correct.
     * If it is not correct, ask for the right answer, and a property to distinguish
     *  the guess from the right answer
     * Change the leaf node into a question node asking about that fact,
     *  adding two new leaf nodes to the node, with the guess and the right
     *  answer.
     */
    public void processLeaf(DTNode leaf){    
        //CurrentNode must be a leaf node (an answer node)
        if (leaf==null || leaf.isQuestion()) { return; }

        UI.println(leaf.toString());
        String ans = UI.askString("Is this your animal?");

        if(ans.equalsIgnoreCase("yes")||ans.equalsIgnoreCase("y")){
            UI.println("I won!");
            UI.println("---------------");
        }
        else if(ans.equalsIgnoreCase("no")||ans.equalsIgnoreCase("n")){
            UI.println("I lost!");
            String animal=UI.askString("What is your animal?");
            String question=UI.askString("What distinguishes your animal from my guess?");
            processLeafHelper(leaf, question,animal);

        }
        else{
            UI.println("please answer with Yes or No.");
            processLeaf(leaf);
        }
    }       

    public void processLeafHelper(DTNode leaf, String question,String animal){
        String multi=UI.askString("Is this a multiway question?");
        if(multi.equalsIgnoreCase("yes")||multi.equalsIgnoreCase("y")){
            double nodeNum=UI.askDouble("How many possible answers are there?");

            for (int i=1;i<=nodeNum;i++){
                String option= UI.askString("Option "+ i+ ": ");
                String answer=UI.askString("Answer "+ i+ ": ");
                DTNode guess=new DTNode(leaf.getText());
                leaf.convertToQuestion(option,new DTNode(answer),guess);
                leaf=leaf.getNo();
            }
            UI.println("---------------");
        }
        else if(multi.equalsIgnoreCase("no")||multi.equalsIgnoreCase("n")){
            DTNode guess=new DTNode(leaf.getText());
            leaf.convertToQuestion(question,new DTNode(animal),guess);
            UI.println("---------------");
        }
        else{
            UI.println("please answer with Yes or No.");
            processLeafHelper(leaf,question,animal);
        }

    }

    /**  COMPLETION
     * Print out the contents of the decision tree in the text pane.
     * The root node should be at the top, followed by its "yes" subtree, and then
     * its "no" subtree.
     * Each node should be indented by how deep it is in the tree.
     */
    public void printTree(){
        String indent="";
        DTNode node=questionsTree;
        printSubTree(node,indent);
    }

    /**
     * Recursively print a subtree, given the node at its root
     *  - print the text in the node with the given indentation
     *  - if it is a question node, then 
     *    print its two subtrees with increased indentation
     */
    public void printSubTree(DTNode node, String indent){
        UI.println(indent+node);
        indent=indent+"  ";
        if (node.getYes().isQuestion()){
            printSubTree(node.getYes(),indent);
        }
        else {
            UI.println(indent+node.getYes());
        }
        if(node.getNo().isQuestion()){
            printSubTree(node.getNo(),indent);
        }
        else{
            UI.println(indent+node.getNo());
        }

    }

    /**  CHALLENGE
     * Draw the tree on the graphics pane as boxes, connected by lines.
     * To make the tree fit in a window, the tree should go from left to right
     * (ie, the root should be drawn at the left)
     * The lines should be drawn before the boxes that they are connecting
     */
    public void drawTree(){
        UI.clearGraphics();

        DTNode node=questionsTree;
        drawHelper(node,50,300, 150);
    }

    private double height=7.5; //height to subtract/add from current y 
    //so that the line does not draw over the previous box

    /**
     * Recursively draws a subtree, given the node at its root
     */
    public void drawHelper(DTNode node, double x, double y, double difference){
        node.draw(x,y);
        if (node.getYes().isQuestion()){
            UI.drawLine(x,y-height,x+100,y-difference);
            drawHelper(node.getYes(),x+100,y-difference,difference/2);
        }
        else {
            UI.drawLine(x,y-height,x+100,y-difference);
            node.getYes().draw(x+100, y-difference);
        }
        if(node.getNo().isQuestion()){
            UI.drawLine(x,y+height,x+100,y+difference);
            drawHelper(node.getNo(),x+100,y+difference, difference/2);
        }
        else{
            UI.drawLine(x,y+height,x+100,y+difference);
            node.getNo().draw(x+100, y+difference);
        }
    }

    public static void main (String[] args) { 
        new AnimalGame();
    }

}
