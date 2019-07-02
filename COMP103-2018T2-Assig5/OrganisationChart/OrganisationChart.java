// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2018T2, Assignment 5
 * Name: Luisa Kristen
 * Username: kristeluis
 * ID: 300444458
 */

import ecs100.*;
import java.awt.Color;
import java.util.*;
import java.io.*;

/** <description of class OrganisationChart>
 */

public class OrganisationChart {

    // Fields
    private Employee organisation;          // the root of the current organisational chart
    private Employee selectedEmployee = null; // the employee selected by pressing the mouse
    private Employee newPerson = null;      // the employee constructed from the data
    //  the user entered

    private String newInitials = null;      // the data the user entered
    private String newRole = null;

    // constants for the layout
    public static final double NEW_LEFT = 10; // left of the new person Icon
    public static final double NEW_TOP = 10;  // top of the new person Icon

    public static final double ICON_X = 40;   // position and size of the retirement icon
    public static final double ICON_Y = 90;   
    public static final double ICON_RAD = 20; 

    private Map <Employee,Double> totalOff; //total offset per manager, needed for formatting. 
    boolean dragging=false;
    /**
     * Construct a new OrganisationChart object
     * Set up the GUI
     */
    public OrganisationChart() {
        this.setupGUI();
        organisation = new Employee(null, "CEO");   // Set the root node of the organisation
        redraw();
    }

    /**
     * Set up the GUI (buttons and mouse)
     */
    public void setupGUI(){
        UI.setMouseMotionListener( this::doMouse );
        UI.addTextField("Initials", (String v)-> {newInitials=v; redraw();});
        UI.addTextField("Role", (String v)-> {newRole=v; redraw();});
        UI.addButton("Load test tree",  this::makeTestTree); 
        UI.addButton("Save",  this::saveTree); 
        UI.addButton("Load",  this::loadSave); 
        UI.addButton("Format", this::callOrganiseTree); 
        UI.addButton("Dragging", ()-> {dragging=!dragging; if(dragging)UI.printMessage("Toggle dragging, press to turn off.");
                else UI.printMessage("");}); 
        UI.addButton("Quit", UI::quit);
        UI.setWindowSize(1100,500);
        UI.setDivider(0);
    }

    /**
     * Most of the work is initiated by the mouse.
     * The action depends on where the mouse is pressed:
     *   on an employee in the tree, or
     *   the new person
     * and where it is released:
     *   on another employee in the tree,
     *   on the retirement Icon, or
     *   empty space
     * An existing person will be moved around in the tree, retired, or repositioned.
     * The new person will be added into the tree;
     */
    public void doMouse(String action, double x, double y){
        if (action.equals("pressed")){
            if (onNewIcon(x, y)) {
                // get the new person
                newPerson = new Employee(newInitials, newRole);
                selectedEmployee = null;
            }
            else {
                // find the selected employee
                selectedEmployee = findEmployee(x, y, organisation);
                newPerson = null;
            }
        }
        else if (action.equals("dragged")){
            if (dragging){
                if (selectedEmployee!=null){
                    selectedEmployee.moveOffset(x);
                    this.redraw();
                }
            }
        }
        else if (action.equals("released")){
            Employee targetEmployee = findEmployee(x, y, organisation); 
            // acting on an employee in the tree
            if (selectedEmployee != null) {
                if (onRetirementIcon(x, y) ){
                    // moving employee to retirement 
                    retirePerson(selectedEmployee);
                }
                else if (targetEmployee == null || targetEmployee==selectedEmployee) { 
                    // repositioning employee
                    selectedEmployee.moveOffset(x);
                }
                else if (targetEmployee != null) {
                    // Moving existing employee around in the hierarchy.
                    moveEmployee(selectedEmployee, targetEmployee);  
                }
            }

            // acting on the new person
            else if (newPerson != null) {  
                if (targetEmployee != null ) {
                    // Moving new person to hierarchy.
                    addNewPerson(newPerson, targetEmployee);
                    newInitials=null;
                    newRole=null;
                }
            }
            this.redraw();
        }

    }

    /**
     * Find and return an employee that is currently placed over the position (x,y). 
     * Must do a recursive search of the subtree whose root is the given employee.
     * Returns an Employee if it finds one,
     * Returns null if it doesn't.
     * [Completion:] If (x,y) is on two employees, it should return the top one.
     */

    private Employee findEmployee(double x, double y, Employee empl){
        Employee f=null;
        if (onNewIcon(x,y)){
            return newPerson;
        }
        else if (empl.on(x, y)) {   // base case: (x,y) is on root of subtree
            return empl;  
        }
        else {  // look further in the subtree
            Employee s;
            for (Employee t: empl.getTeam()){
                s=findEmployee(x,y,t);
                if (s!=null){
                    f=s;
                }
            } 
        }
        return f;  // it wasn't found;
    }

    /**
     * Add the new employee to the target
     * [STEP 2:] If target is not vacant, add new employee to the target's team
     * [STEP 3:] If target is vacant, fill the position with the initials of new employee
     * [COMPLETION:] If the newPerson has a role but no initials, change the role of the target.
     */
    public void addNewPerson(Employee newEmpl, Employee target){ //not working
        if ((newEmpl == null) || (target == null)){return;}   //invalid arguments
        if (newEmpl.getRole()==null && newEmpl.isVacant()){return;}

        if (newEmpl.isVacant()&& newEmpl.getRole()!=null){
            target.setRole(newEmpl.getRole());
        }

        else if (!target.isVacant()){
            if(newEmpl.getManager()!=null){
                newEmpl.getManager().removeFromTeam(newEmpl);
            }
            target.addToTeam(newEmpl);
        }
        else {
            target.fillVacancy(newEmpl);
        }
    }

    /** Move a current employee (empl) to a new position (target)
     *  [STEP 2:] If the target is not vacant, then
     *    add the employee to the team of the target,
     *    (bringing the whole subtree of the employee with them)
     *  [STEP 3:] If the target is vacant, then
     *     make the employee fill the vacancy, and
     *     if the employee was a manager, then make their old position vacant, but
     *     if they were not a manager, just remove them from the tree.
     *        [COMPLETION:]
     *   Moving the CEO is a problem, and shouldn't be allowed. 
     *   In general, moving any employee to a target that is in the
     *   employee's subtree is a problem and should not be allowed. (Why?) 
     *   
     *   //because otherwise it constantly tries to move its own tree 
     */
    private void moveEmployee(Employee empl, Employee target) { //not working 
        if ((empl == null) || (target == null)){return;}   //invalid arguments.

        if (!inSubtree(target,empl)){
            if (!target.isVacant()){
                target.addToTeam(empl);
            }
            else {
                target.fillVacancy(empl);
                if (empl.isManager()){
                    empl.makeVacant();
                }
                else {empl.getManager().removeFromTeam(empl);}
            }
        }
    }

    /** STEP 3
     * Retire an employee.
     * If they are a manager or the CEO, then make the position vacant
     *  (leaving the employee object in the tree, but no initials)
     * If they are not a manager, then remove them from the tree completely.
     */
    public void retirePerson(Employee empl){
        if (empl.isManager()){
            empl.makeVacant();
        }
        else {
            empl.getManager().removeFromTeam(empl);
        }
    }

    /** (COMPLETION)
     *        Return true if person is in the subtree, and false otherwise
     *        Uses == to determine node equality
     *  Check if person is the same as the root of subTree
     *  if not, check if in any of the subtrees of the team members of the root
     *   (recursive call, which must return true if it finds the person)
     */
    private boolean inSubtree(Employee person, Employee subTree) {
        if (subTree==organisation) { return true; }  // first simple case!!
        if (person==subTree)       { return true; }  // second simple case!!
        // search down the subTree
        for (Employee e: subTree.getTeam()){
            boolean in=inSubtree(person,e);
            if(in){return true;}
        }
        return false;
    }

    /**
     * Saves the tree to a file specfied by the user
     */
    public void saveTree(){
        try {
            File save=new File (UIFileChooser.save());
            PrintStream out = new PrintStream(save+".txt");
            saveTreeHelper(organisation,out);
        }
        catch(IOException e) {
            UI.printf("File Failure %s \n", e);
        }
    }

    /**
     * Recursivly finds the employees to add to the file which is being saved.
     */
    public void saveTreeHelper(Employee e,PrintStream out){
        out.println(e.toStringFull());
        for (Employee t: e.getTeam()){
            saveTreeHelper(t, out);
        }
    }

    /**
     * Loads a previously saved file and prints it out
     */
    public void loadSave(){
        organisation = new Employee(null, "CEO");   // Set the root node of the organisation
        try {
            File myFile = new File(UIFileChooser.open());
            Scanner scan = new Scanner(myFile);
            //CEO
            Scanner LSc =new Scanner(scan.nextLine());
            String name=LSc.next();
            String role=LSc.next();
            double offset=LSc.nextDouble();
            double children=LSc.nextDouble();
            organisation = new Employee(name,role,offset);   // Set the root node of the organisation
            loadHelper(scan,organisation,children);
        }
        catch(IOException e) {UI.printf("File Failure %s \n", e);}
        this.redraw();
    }

    /**
     * Aids the loadSave funtion by recusivly calling itslef to find more employees in the team
     */
    public void loadHelper(Scanner scan,Employee e,  double children){
        for (int i=0;i<children;i++) {
            Scanner LSc =new Scanner(scan.nextLine());
            String name=LSc.next();
            String role=LSc.next();
            double offset=LSc.nextDouble();
            double c=LSc.nextDouble();
            Employee newEmpl=new Employee (name,role, offset);
            e.addToTeam(newEmpl);
            loadHelper(scan,newEmpl,c);
        }
    }

    /**
     * Calls the Organise tree, creates a new hashmap with the offsets for each manager, and redraws
     */
    public void callOrganiseTree(){
        totalOff=new HashMap <Employee,Double> ();
        organiseTree(organisation);
        this.redraw();
    }

    
    /**
     * Organises the tree by working out the offset required for the subtree, and recursivly organises the subtree.
     */
    public void organiseTree(Employee e){

        double width=45;
        totalOff.put(e,(-organiseRecurse(e)/2.0)*width);
        if (e==organisation){
            UI.println(totalOff.get(e));
            for (Employee t: e.getTeam()){
                organiseTree(t);
            }
            e.setOffset(0);}
        else{
            for (Employee t: e.getTeam()){
                organiseTree(t);
            }
            if (e.getManager().getTeam().size()==1){e.setOffset(0);return;}
            int numChildren=organiseRecurse(e);
            double offset=numChildren*width;
            if (totalOff.get(e.getManager())!=null){
                e.setOffset(totalOff.get(e.getManager())+numChildren*width);
                offset+=totalOff.get(e.getManager());
            }
            else{
                e.setOffset(numChildren*width);
            }
            totalOff.put(e.getManager(),offset);
        }
    }

    /**
     * Works out how many people are in the subtree
     */
    public int organiseRecurse(Employee e){
        int count=0;
        count++;
        for (Employee team:e.getTeam())
            count+=organiseRecurse(team);
        return count;
    }

    // Drawing the tree  =========================================
    /**
     * Redraw the chart.
     */
    private void redraw() {
        UI.clearGraphics();
        drawTree(organisation);
        drawNewIcon();
        drawRetireIcon();
    }    

    /** [STEP 1:]
     *  Recursive method to draw all nodes in a subtree, given the root node.
     *        (The provided code just draws the root node;
     *         you need to make it draw all the nodes.)
     */
    private void drawTree(Employee empl) {
        empl.draw();
        //draw the nodes under empl
        for (Employee t: empl.getTeam()){
            drawTree(t);
        }
    }

    // OTHER DRAWING METHODS =======================================
    /**
     * Redraw the new Person box
     */
    private void drawNewIcon(){
        UI.setColor((newInitials==null)?Employee.V_BACKGROUND:Employee.BACKGROUND);
        UI.fillRect(NEW_LEFT,NEW_TOP,Employee.WIDTH, Employee.HEIGHT);
        UI.setColor(Color.black);
        UI.drawRect(NEW_LEFT,NEW_TOP,Employee.WIDTH, Employee.HEIGHT);
        UI.drawString((newInitials==null)?"--":newInitials, NEW_LEFT+5, NEW_TOP+12);
        UI.drawString((newRole==null)?"--":newRole, NEW_LEFT+5, NEW_TOP+26); 
    }

    /**
     * Redraw the retirement Icon
     */
    private void drawRetireIcon(){
        UI.setColor(Color.red);
        UI.setLineWidth(5);
        UI.drawOval(ICON_X-ICON_RAD, ICON_Y-ICON_RAD, ICON_RAD*2, ICON_RAD*2);
        double off = ICON_RAD*0.68;
        UI.drawLine((ICON_X - off), (ICON_Y - off), (ICON_X + off), (ICON_Y + off));
        UI.setLineWidth(1);
        UI.setColor(Color.black);
    }

    /** is the mouse position on the New Person box */
    private boolean onNewIcon(double x, double y){
        return ((x >= NEW_LEFT) && (x <= NEW_LEFT + Employee.WIDTH) &&
            (y >= NEW_TOP) && (y <= NEW_TOP + Employee.HEIGHT));
    }

    /** is the mouse position on the retirement icon */
    private boolean onRetirementIcon(double x, double y){
        return (Math.abs(x - ICON_X) < ICON_RAD) && (Math.abs(y - ICON_Y) < ICON_RAD);
    }

    // Testing ==============================================
    /**
     * Makes an initial tree so you can test your program
     */
    private void makeTestTree(){
        organisation = new Employee("AA", "CEO");
        Employee aa = new Employee("AS", "VP1");
        Employee bb = new Employee("BV", "VP2");
        Employee cc = new Employee("CW", "VP3");
        Employee dd = new Employee("DM", "VP4");
        Employee a1 = new Employee("AF", "AL");
        Employee a2 = new Employee("AH", "AL");
        Employee b1 = new Employee("BK", "AS");
        Employee b2 = new Employee("BL", "DPA");
        Employee d1 = new Employee("CX", "DBP");
        Employee d2 = new Employee("CY", "SEP");
        Employee d3 = new Employee("CZ", "MSP");

        organisation.addToTeam(aa); aa.setOffset(-160);
        organisation.addToTeam(bb); bb.setOffset(-50);
        organisation.addToTeam(cc); cc.setOffset(15);
        organisation.addToTeam(dd); dd.setOffset(120);

        aa.addToTeam(a1); a1.setOffset(-25);
        aa.addToTeam(a2); a2.setOffset(25);
        bb.addToTeam(b1); b1.setOffset(-25);
        bb.addToTeam(b2); b2.setOffset(25);
        dd.addToTeam(d1); d2.setOffset(-50);
        dd.addToTeam(d2); 
        dd.addToTeam(d3); d3.setOffset(50);

        this.redraw();
    }

    //* Test for printing out the tree structure, indented text */
    private void printTree(Employee empl, String indent){
        UI.println(indent+empl+ " " +
            (empl.getManager()==null?"noM":"hasM") + " " +
            empl.getTeam().size()+" reports");
        String subIndent = indent+"  ";
        for (Employee tm : empl.getTeam()){
            printTree(tm, subIndent);
        }
    }

    // Main
    public static void main(String[] arguments) {
        new OrganisationChart();
    }        

}
