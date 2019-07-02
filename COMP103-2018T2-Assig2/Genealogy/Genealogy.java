// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2018T2, Assignment 2
 * Name:Luisa Kristen   
 * Username: kristeluis
 * ID: 300444458
 */

import ecs100.*;
import java.util.*;
import java.io.*;
import java.lang.Math;

/** Genealogy:
 * Prints out information from a genealogical database
 */

public class Genealogy  {

    // all the people:  key is a name,  value is a Person object
    private final Map<String, Person> database = new HashMap<String, Person>();

    private String selectedName;  //currently selected name.

    private boolean databaseHasBeenFixed = false;
    /**
     * Constructor
     */
    public Genealogy() {
        loadData();
        setupGUI();
    }

    /**
     * Buttons and text field for operations.
     */
    public void setupGUI(){
        UI.addButton("Print all names", this::printAllNames);
        UI.addButton("Print all details", this::printAllDetails);
        UI.addTextField("Name", this::selectPerson);
        UI.addButton("Parent details", this::printParentDetails);
        UI.addButton("Add child", this::addChild);
        UI.addButton("Find & print Children", this::printChildren);
        UI.addButton("Fix database", this::fixDatabase);
        UI.addButton("Print GrandChildren", this::printGrandChildren);
        UI.addButton("Clear Text", UI::clearText);
        UI.addButton("Reload Database", this::loadData);
        UI.addButton("Check Dataset",this::checkDataset);
        UI.addButton("Ancestor Tree", this::ancestorTree);
        UI.addButton("Descendant Tree", this::printDescendants);
        UI.addButton("Quit", UI::quit);
        UI.setDivider(1.0);
    }

    /** 
     *  Load the information from the file "database.txt".
     *        Each line of the file has information about one person:
     *        name, year of birth, mother's name, father's name
     *        (note: a '-' instead of a name means  the mother or father are unknown)
     *        For each line,
     *         - construct a new Person with the information, and
     *   - add to the database map.
     */
    public void loadData() {
        try{
            Scanner sc = new Scanner(new File("database.txt"));
            // read the file to construct the Persons to put in the map
            while (sc.hasNextLine()){
                Scanner lineSc = new Scanner(sc.nextLine());
                String name=lineSc.next();
                int birth=lineSc.nextInt();
                String mum=lineSc.next();
                String dad=lineSc.next();
                Person p = new Person (name,birth,mum,dad);
                database.put(name,p);
            }

            sc.close();
            UI.println("Loaded "+database.size()+" people into the database");
            UI.println("-----------------");
        }catch(IOException e){throw new RuntimeException("Loading database.txt failed" + e);}
    }

    /** Prints out names of all the people in the database */
    public void printAllNames(){
        for (String name : database.keySet()) {
            UI.println(name);
        }
        UI.println("-----------------");
    }

    /** Prints out details of all the people in the database */
    public void printAllDetails(){
        for (String name : database.keySet()) {
            UI.println(database.get(name).toString());
        }

        UI.println("-----------------");
    }

    /**
     * Store value (capitalised properly) in the selectedName field.
     * If there is a person with that name currently in people,
     *  then print out the details of that person,
     * Otherwise, offer to add the person:
     * If the user wants to add the person,
     *  ask for year of birth, mother, and father
     *  create the new Person,
     *  add to the database, and
     *  print out the details of the person.
     * Hint: it may be useful to make an askPerson(String name) method
     * Hint: remember to capitalise the names that you read from the user
     */
    public void selectPerson(String value){
        selectedName = capitalise(value); 
        for (String name : database.keySet()) {
            if (name.equals(selectedName)){
                UI.println(database.get(selectedName).toString());
                UI.println("-----------------");
                return;
            }
        }
        String ans= UI.askString("Would you like to add " + selectedName + "? (y || n)");
        if (ans.equalsIgnoreCase("y") || ans.equalsIgnoreCase("yes")) {
            database.put (selectedName,askPerson(selectedName));
            UI.println("Sucessfully added " + selectedName+ " to the database.");
            UI.println("-----------------");
        }
        else{
            UI.println(selectedName+ " will not be added to the database.");
        }
    }

    /**
     * Creates and returns the new person to add to the database.
     */
    public Person askPerson(String name){
        int birth=UI.askInt("Year of Birth?");
        String mum=capitalise(UI.askString("Mother's name(or - )"));
        String dad=capitalise(UI.askString("Father's name(or - )"));
        Person person=new Person (name, birth,mum,dad);
        return person;
    }

    /**
     * Print all the details of the mother and father of the person
     * with selectedName (if there is one).
     * (If there is no person with the current name, print "no person called ...")
     * If the mother or father's names are unknown, print "unknown".
     * If the mother or father names are known but they are not in
     *  the database, print "...: No details known".
     */
    public void printParentDetails(){
        for (String name : database.keySet()) {
            if (name.equals(selectedName)){
                String mother=database.get(selectedName).getMother();
                if (mother!=null ){
                    if (database.get(mother)!=null){
                        UI.println(database.get(mother).toString());
                    }
                    else {
                        UI.println(mother+" : No details known.");
                    }
                }
                else{ 
                    UI.println("Mother is Unkown");
                }
                String father=database.get(selectedName).getFather();
                if (father!=null){
                    if (database.get(father)!=null){
                        UI.println(database.get(father).toString());
                    }
                    else {
                        UI.println(father+" : No details known.");
                    }
                }
                else{ 
                    UI.println("Father is Unkown");
                } 
                UI.println("-----------------");
                return;
            }
        }
        UI.println("No person called " + selectedName+ " in the database");
        UI.println("-----------------");
    }

    /**
     * Add a child to the person with selectedName (if there is one).
     * If there is no person with the selectedName,
     *   print "no person called ..." and return
     * Ask for the name of a child of the selectedName
     *  (remember to capitalise the child's name)
     * If the child is already recorded as a child of the person,
     *  print a message
     * Otherwise, add the child's name to the current person.
     * If the child's name is not in the current database,
     *   offer to add the child's details to the current database.
     *   Check that the selectedName is either the mother or the father.
     */
    public void addChild(){
        if (database.containsKey(selectedName)){
            String childName=capitalise(UI.askString("What is the name of "+ selectedName+"'s child?"));
            if (database.get(selectedName).getChildren().contains(childName)){
                UI.println("This is already a listed child");
            }
            else{
                if (!database.containsKey(childName)){
                    Person child=askPerson(childName);
                    database.put(childName,child);
                }
                if (isItValidChild(childName,selectedName)){
                    database.get(selectedName).addChild(childName);
                    UI.println(database.get(selectedName).toString());
                }
                else{
                    UI.println("You can not assign "+childName+ " to " + selectedName );
                }
            }
        }
        else{
            UI.println("No person called " + selectedName+ " in the database");
        }
        UI.println("-----------------");
    }

    /**
     * Returns true if one of the parents is the selected name.
     */
    public boolean isItValidChild(String childName, String parentName){
        if (database.get(childName).getMother()!=null ){
            String mother=database.get(childName).getMother();
            if (mother.equals(parentName))            return true;
        }

        if ( database.get(childName).getFather()!=null){
            if (database.get(childName).getFather().equals(parentName))       return true;
        }

        return false;
    }

    /**
     * Print the number of children of the selectedName and their names (if any)
     * Find the children by searching the database for people with
     * selectedName as a parent.
     * Hint: Use the findChildren method (which is needed for other methods also)
     */
    public void printChildren(){
        Set <String> children=findChildren(selectedName);

        if (children.isEmpty()){
            UI.println("There are no children for "+selectedName);
        }
        else {
            UI.println("Number of children for "+selectedName+": "+children.size());
            for (String ch : children) {
                UI.println(ch);
            }
        }

        UI.println("-----------------");
    }

    /**
     * Find (and return) the set of all the names of the children of
     * the given person by searching the database for every person 
     * who has a mother or father equal to the person's name.
     * If there are no children, then return an empty Set
     */
    public Set<String> findChildren(String name){
        Set <String> children= new HashSet <String> ();

        for (String n : database.keySet()) {
            if (isItValidChild(n,name)){
                children.add(n);
            }
        }
        return children;   
    }

    /**
     * When the database is first loaded, none of the Persons will
     * have any children recorded in their children field. 
     * Fix the database so every Person's children includes all the
     * people that have that Person as a parent.
     * Hint: use the findChildren method
     */
    public void fixDatabase(){
        for (String name : database.keySet()) {
            Set <String> children=findChildren(name);
            for (String child : children) {
                database.get(name).addChild(child);
            }
        }
        databaseHasBeenFixed = true;
        UI.println("Found children of each person in database\n-----------------");
    }

    /**
     * Print out all the grandchildren of the selectedName (if any)
     * Assume that the database has been "fixed" so that every Person
     * contains a set of all their children.
     * If the selectedName is not in the database, print "... is not known"
     */
    public void printGrandChildren(){
        if (!databaseHasBeenFixed) { UI.println("Database must be fixed first!");}
        if (!database.containsKey(selectedName)){
            UI.println("That person is not known");
            return;
        }

        boolean anyGrandChildren=false;

        Set <String> children= database.get(selectedName).getChildren();
        for (String child : children) {
            Set <String> grandChildren= database.get(child).getChildren();
            for (String grand : grandChildren) {
                UI.println(grand);
            }
            if (!grandChildren.isEmpty()){anyGrandChildren=true;}
        }

        if (anyGrandChildren==false){
            UI.println(selectedName+" does not have any grandchildren.");
            return;
        }
        UI.println("------------------");
    }

    /**
     * Print out all the names that are in the database but for which
     * there is no Person in the database. Do not print any name twice.
     * These will be names of parents or children of Persons in the Database
     * for which a Person object has not been created.
     */
    public void printMissing(){
        UI.println("Missing names:");
        UI.println();
        Set <String> missing= new HashSet <String> ();

        for (String name : database.keySet()) {
            String mother=database.get(name).getMother();
            String father=database.get(name).getFather();
            Set <String> children=database.get(name).getChildren();

            if (mother!=null && !database.containsKey(mother)){
                missing.add(mother);
            }
            if (father!=null && !database.containsKey(father)){
                missing.add(father);
            }
            for (String child : children) {
                if (!database.containsKey(child)){
                    missing.add(child);
                }
            }
        }

        for (String miss : missing) {
            UI.println(miss);
        }
        UI.println("------------------");
    }

    /**
     * Checks that no child is older than their parent
     */
    public void youngerThanParents(){
        UI.println("Children older than their Parents:");
        UI.println();
        for (String name : database.keySet()) {
            String mother=database.get(name).getMother();
            String father=database.get(name).getFather();

            int dobChild= database.get(name).getDOB();

            if (mother!=null){
                int dobMother= database.get(mother).getDOB();
                if (dobChild<dobMother ){
                    UI.printf("%s (%d) cannot be older than their mother - %s (%d) \n",name,dobChild,mother,dobMother);
                }
            }

            if (father!=null){
                int dobFather= database.get(father).getDOB();
                if (dobChild<dobFather){
                    UI.printf("%s (%d) cannot be older than their father - %s (%d)\n",name,dobChild,father,dobFather);
                }
            }
        }
        UI.println("------------------");
    }

    /**
     * Checks that the Parents of each person are not more than 90 years apart.
     */
    public void checkParentsAges(){
        UI.println("Unlikely Parents:");
        UI.println();
        Set <String> names= new HashSet <String> ();
        for (String name : database.keySet()) {
            String mother=database.get(name).getMother();
            String father=database.get(name).getFather();

            if (mother!=null && father!=null){
                int dobMother= database.get(mother).getDOB();
                int dobFather= database.get(father).getDOB();

                int absAge=Math.abs(dobMother-dobFather);

                if (absAge>90){
                    if(names.contains(mother)==false && names.contains(father)==false){
                        UI.printf("%s and %s are %d years apart, they are very unlikely to have children together.\n",mother,father,absAge);
                        names.add(mother);
                        names.add(father);
                    }
                }
            }
        }
        UI.println("------------------");
    }

    /**
     * Checks if anyone is their own ancestor
     */
    public void checkOwnAncestor(){
        UI.println("People who are their own Ancestor:");
        UI.println();
        Set <String> ownAncestors= new HashSet <String> ();
        for (String n: database.keySet()) { 
            List <String> names= new ArrayList <String> ();//names to check
            names.add(n);
            int sizeBefore=0;
            int sizeAfter=1;
            while (sizeBefore<sizeAfter){ //while more people have been added to the list to check, repeat
                for (int i=0; i< names.size();i++ ){ //checks all names in the list so far
                    if (database.get(names.get(i)).getMother()!=null){
                        String mother=database.get(names.get(i)).getMother();
                        if (!names.contains(mother)){ //checks this person isnt in the list already
                            names.add(mother);
                        }
                    }
                    if (database.get(names.get(i)).getFather()!=null){
                        String father=database.get(names.get(i)).getFather();
                        if (!names.contains(father)){
                            names.add(father);
                        }
                    }
                }
                sizeBefore=sizeAfter;
                sizeAfter=names.size();
            }
            names.remove(0); // removes the original person we are searching for 
            for (String check :names){ //checks if they are still in the list-aka their own ancestor
                if(n.equals(check)){ownAncestors.add(n);} 
            }
        }

        for (String n :ownAncestors){ //prints everyone that is their own ancestor 
            UI.println(n+" is their own Ancestor, this cant be!");
        }
        UI.println("------------------");
    }

    /**
     * Checks the database for inconsistencies
     */
    public void checkDataset(){ 
        youngerThanParents();
        printMissing();
        checkParentsAges();
        checkOwnAncestor();
    }

    /**
     * Prints out the tree of ancestors. 
     * One Generation at a time
     */
    public void ancestorTree(){
        if (!database.containsKey(selectedName)){
            UI.println("That person is not known");
            return;
        }
        Map <String,Integer> names=new HashMap <String, Integer> (); //maps name to generation
        List <String> namesCopy=new ArrayList <String>(); //contains all the same values, but are kept in order of generation
        List <String> MorF=new ArrayList <String>(); //contains m or f for all ancestors, with the same index as that ancestor

        names.put(selectedName,0);
        namesCopy.add(selectedName);
        MorF.add(" "); //required as the selected name is not a mother or father
        int sizeBefore=0;
        int sizeAfter=1;

        int gen=1;
        while (sizeBefore<sizeAfter){
            for (int i=0;i<namesCopy.size();i++){

                if (database.get(namesCopy.get(i)).getFather()!=null){
                    String father=database.get(namesCopy.get(i)).getFather();
                    if (!names.containsKey(father)){
                        namesCopy.add(i+1,father);
                        names.put(father,names.get(namesCopy.get(i))+1);
                        MorF.add(i+1,"F:");
                    }
                }
                if (database.get(namesCopy.get(i)).getMother()!=null){
                    String mother=database.get(namesCopy.get(i)).getMother();
                    if (!names.containsKey(mother)){
                        namesCopy.add(i+1,mother);
                        names.put(mother,names.get(namesCopy.get(i))+1);
                        MorF.add(i+1,"M:");
                    }
                }
                gen++;
            }
            sizeBefore=sizeAfter;
            sizeAfter=names.keySet().size();
        }
        for (int i=0;i<namesCopy.size();i++){
            String indent=" ";
            int space=names.get(namesCopy.get(i));
            for (int j=0; j<space;j++){
                indent=indent+ "  ";
            }

            String name=indent+MorF.get(i)+namesCopy.get(i);
            UI.printf("%s (%s) \n",name, database.get(namesCopy.get(i)).getDOB());
        }
        UI.println("------------------");
    }

    /**
     * Prints the children (and childrens children etc) of the selected person
     * Database has to be fixed.
     * Prints one generation at a time.
     */
    public void printDescendants (){
        if (!database.containsKey(selectedName)){
            UI.println("That person is not known");
            return;
        }
        if (!databaseHasBeenFixed) { UI.println("Database must be fixed first!");}
        Map <String,Integer> names=new HashMap <String, Integer> ();
        List <String> namesCopy=new ArrayList <String>();

        names.put(selectedName,0);
        namesCopy.add(selectedName);
        int sizeBefore=0;
        int sizeAfter=1;

        int gen=1;
        while (sizeBefore<sizeAfter){
            for (int i=0;i<namesCopy.size();i++){
                Set <String> children= new HashSet <String>(database.get(namesCopy.get(i)).getChildren());
                for (String child:children){
                    if (!names.containsKey(child)){
                        names.put(child,gen);
                        namesCopy.add(i+1,child);
                    }
                }
                gen++;
            }
            sizeBefore=sizeAfter;
            sizeAfter=names.keySet().size();
        }

        for (String print: namesCopy){
            String indent=" ";
            int space=names.get(print);
            for (int i=0; i<space;i++){
                indent=indent+ "  ";
            }
            int dob=database.get(print).getDOB();
            print=indent+print;
            UI.printf("%s (%d) \n",print,dob);
        }
        UI.println("------------------");
    }

    /**
     * Return a capitalised version of a string
     */
    public String capitalise(String s){
        return s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase();
    }

    public static void main(String[] args) throws IOException {
        new Genealogy();
    }
}
