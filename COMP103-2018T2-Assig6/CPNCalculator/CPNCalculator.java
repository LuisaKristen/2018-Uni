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
import java.io.*;
import java.awt.Color;

/** 
 * Calculator for Cambridge-Polish Notation expressions
 * User can type in an expression (in CPN) and the program
 * will compute and print out the value of the expression.
 * The template is based on the version in the lectures,
 *  which only handled + - * /, and did not do any checking
 *  for valid expressions
 * The program should handle a wider range of operators and
 *  check and report certain kinds of invalid expressions
 */

public class CPNCalculator{   

    /**
     * Main Read-evaluate-print loop
     * Reads an expression from the user then evaluates it and prints the result
     * Invalid expressions could cause errors when reading.
     * The try-catch prevents these errors from crashing the programe - 
     *  the error is caught, and a message printed, then the loop continues.
     */
    public static void main(String[] args){
        UI.addButton("Quit", UI::quit); 
        UI.setDivider(1.0);
        UI.println("Enter expressions in pre-order format with spaces");
        UI.println("eg   ( * ( + 4 5 8 3 -10 ) 7 ( / 6 4 ) 18 )");
        while (true){
            UI.println();
            Scanner sc = new Scanner(UI.askString("expr:"));
            try {
                GTNode<String> expr = readExpr(sc);
                printExpression(expr, false);
                UI.println();

                UI.println(" -> " + evaluate(expr));
            }catch(Exception e){UI.println("invalid expression"+e);}
        }
    }

    /**
     * Recursively construct expression tree from scanner input
     */
    public static GTNode<String> readExpr(Scanner sc){
        if (sc.hasNext("\\(")) {                     // next token is an opening bracket
            sc.next();                               // the opening (
            if (sc.hasNext("\\)")){  //makes a ()
                UI.println ("Error found: ()"); return null;
            }
            String op = sc.next();                   // the operator
            GTNode<String> node = new GTNode<String>(op);  
            while (! sc.hasNext("\\)")){
                GTNode<String> child = readExpr(sc); // the arguments
                node.addChild(child); 
                if (!sc.hasNext()){
                    UI.println ("Error found: No closing bracket"); 
                    return null;}
            }
            sc.next();                               // the closing )
            return node;
        }
        else {                                       // next token must be a number
            return new GTNode<String>(sc.next());
        }
    }

    /**
     * Evaluate an expression and return the value
     * Returns Double.NaN if the expression is invalid in some way.
     */
    public static double evaluate(GTNode<String> expr){
        if (expr==null){ return Double.NaN; }
        if (expr.numberOfChildren()==0){            // must be a number
            if (expr.getItem().equals("PI")){
                return Math.PI;
            }
            else if (expr.getItem().equals("E")){
                return Math.E;
            }

            try { return Double.parseDouble(expr.getItem());}
            catch(Exception e){return Double.NaN;}

        }
        else {
            double ans = Double.NaN;                // answer if no valid operator
            if (expr.getItem().equals("+")){        // addition operator
                ans = 0;
                if (expr.numberOfChildren() >1){
                    for(GTNode<String> child: expr) {
                        ans += evaluate(child);
                    }
                }else {
                    UI.println("Incorrect number of arguments found. Required: 2+, Found: "+expr.numberOfChildren()); 
                    return Double.NaN;
                }
            }
            else if (expr.getItem().equals("*")) {  // multiplication operator 
                ans = 1;
                if (expr.numberOfChildren() >1){
                    for(GTNode<String> child: expr) {
                        ans *= evaluate(child);
                    }
                }
                else {
                    UI.println("Incorrect number of arguments found. Required: 2+, Found: "+expr.numberOfChildren()); 
                    return Double.NaN;
                }
            }
            else if (expr.getItem().equals("-")){  // subtraction operator 
                ans = evaluate(expr.getChild(0));
                if (expr.numberOfChildren() >1){
                    for(int i=1; i<expr.numberOfChildren(); i++){
                        ans -= evaluate(expr.getChild(i));
                    }
                }
                else {
                    UI.println("Incorrect number of arguments found. Required: 2+, Found: "+expr.numberOfChildren()); 
                    return Double.NaN;
                }
            }
            else if (expr.getItem().equals("/")){  // division operator          
                ans = evaluate(expr.getChild(0));
                if (expr.numberOfChildren() >1){
                    for(int i=1; i<expr.numberOfChildren(); i++){
                        ans /= evaluate(expr.getChild(i));
                    }
                }
                else {
                    UI.println("Incorrect number of arguments found. Required: 2+, Found: "+expr.numberOfChildren()); 
                    return Double.NaN;
                }
            }
            else if (expr.getItem().equals("sqrt")){  //square root operator          
                if (expr.numberOfChildren() !=1){
                    UI.println("Incorrect number of arguments found. Required: 1, Found: "+expr.numberOfChildren()); 
                    return Double.NaN;
                }
                ans = Math.sqrt(evaluate(expr.getChild(0)));
            }else if (expr.getItem().equals("^")){  // power operator
                if (expr.numberOfChildren() !=2){
                    UI.println("Incorrect number of arguments found. Required: 2, Found: "+expr.numberOfChildren()); 
                    return Double.NaN;
                }
                ans = Math.pow(evaluate(expr.getChild(0)),evaluate(expr.getChild(1)));
            }else if (expr.getItem().equals("log")){  // log operator          
                if (expr.numberOfChildren() !=1){
                    UI.println("Incorrect number of arguments found. Required: 1, Found: "+expr.numberOfChildren()); 
                    return Double.NaN;
                }
                ans = Math.log10(evaluate(expr.getChild(0)));
            }else if (expr.getItem().equals("ln")){  // ln operator          
                if (expr.numberOfChildren() !=1){
                    UI.println("Incorrect number of arguments found. Required: 1, Found: "+expr.numberOfChildren()); 
                    return Double.NaN;
                }
                ans = Math.log(evaluate(expr.getChild(0)));
            }else if (expr.getItem().equals("sin")){  // sin operator
                if (expr.numberOfChildren() !=1){
                    UI.println("Incorrect number of arguments found. Required: 1, Found: "+expr.numberOfChildren()); 
                    return Double.NaN;
                }
                ans = Math.sin(evaluate(expr.getChild(0)));
            }else if (expr.getItem().equals("cos")){  // cos operator          
                if (expr.numberOfChildren() !=1){
                    UI.println("Incorrect number of arguments found. Required: 1, Found: "+expr.numberOfChildren()); 
                    return Double.NaN;
                }
                ans = Math.cos(evaluate(expr.getChild(0)));
            }else if (expr.getItem().equals("tan")){  // tan operator          
                if (expr.numberOfChildren() !=1){
                    UI.println("Incorrect number of arguments found. Required: 1, Found: "+expr.numberOfChildren()); 
                    return Double.NaN;
                }
                ans = Math.tan(evaluate(expr.getChild(0)));
            }else if (expr.getItem().equals("dist")){  // distance operator          
                if (expr.numberOfChildren() !=4){
                    UI.println("Incorrect number of arguments found. Required: 4, Found: "+expr.numberOfChildren()); 
                    return Double.NaN;
                }
                double x=evaluate(expr.getChild(2))-evaluate(expr.getChild(0));
                double y=evaluate(expr.getChild(3))-evaluate(expr.getChild(1));
                ans= Math.sqrt(Math.pow(x,2)+Math.pow(y,2));
            }else if (expr.getItem().equals("avg")){  // average operator          
                ans=evaluate(expr.getChild(0));
                if (expr.numberOfChildren() > 0){
                    for(int i=1; i<expr.numberOfChildren(); i++){
                        ans += evaluate(expr.getChild(i));
                    }
                }
                else {
                    UI.println("Incorrect number of arguments found. Required: 1+, Found: "+expr.numberOfChildren()); 
                    return Double.NaN;
                }
                ans/=expr.numberOfChildren();
            }

            else {UI.printf(" The operator %s is invalid \n ",expr.getItem());}
            return ans; 
        }
    }

    /**
     * Prints the expression in a reasonable fasion, takes the current expression and a boolean as the parameter,
     * the boolean is true if it is in the middle of an expression, and a furter expression requires a bracket.
     */
    public static void printExpression(GTNode <String> expr, boolean brack){
        if (expr.numberOfChildren()!=0){
            if (expr.getItem().equals("sin") ||expr.getItem().equals("cos") ||expr.getItem().equals("tan") // special cases that need a bracket 
            ||expr.getItem().equals("ln")  ||expr.getItem().equals("log")){
                UI.print(expr.getItem() + "(");
                for(int i=0; i<expr.numberOfChildren(); i++){
                    printExpression(expr.getChild(i), false);
                }
                UI.print(")");
            }
            else if (expr.getItem().equals("dist") || expr.getItem().equals("avg")){ // needs seperate statement as 
                //the individual arguments are seperated by commas. 
                UI.print(expr.getItem() + "(");
                for(int i=0; i<expr.numberOfChildren()-1; i++){
                    printExpression(expr.getChild(i), true);
                    UI.print(",");
                }
                printExpression(expr.getChild(expr.numberOfChildren()-1),true);
                UI.print(")");
            }
            else if (brack){

                UI.print("(");
                for(int i=0; i<expr.numberOfChildren()-1; i++){
                    printExpression(expr.getChild(i), false);
                    UI.print(expr.getItem());
                }
                printExpression(expr.getChild(expr.numberOfChildren()-1),false);
                UI.print(")");
            }

            else{
                for(int i=0; i<expr.numberOfChildren()-1; i++){
                    printExpression(expr.getChild(i), true);
                    UI.print(expr.getItem());
                }
                printExpression(expr.getChild(expr.numberOfChildren()-1),true);
            } 
        }
        else {
            UI.print(expr.getItem());} // must be a number

    }

}

