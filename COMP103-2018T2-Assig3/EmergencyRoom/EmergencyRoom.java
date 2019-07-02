// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2018T2, Assignment 3
 * Name: Luisa Kristen
 * Username: kristeluis
 * ID: 300444458
 */

import ecs100.*;
import java.util.*;
import java.io.*;

/**
 * Simulation of an EmergencyRoom,
 * The Emergency room has a collection of departments for treating patients (ER beds, X-ray,
 *  Operating Theatre, MRI, Ultrasound, etc).
 * 
 * When patients arrive at the emergency room, they are immediately assessed by the
 *  triage team who determine the priority of the patient and a sequence of treatments
 *  that the patient will need.
 *
 * Each department has
 *  - a Set of patients that they are currently treating,
 *    (There is a maximum size of this set for each department)
 *  - a Queue for the patients waiting for that department.
 *
 * The departments should be in a Map, with the department name (= treatment type) as the key.
 * 
 * When a patient has finished a treatment, they should be moved to the
 *   department for the next treatment they require.
 *
 * When a patient has finished all their treatments, they should be discharged:
 *  - a record of their total time, treatment time, and wait time should be printed,
 *  - the details should be added to the statistics. 
 *
 *
 * The main simulation should consist of
 * a setting up phase which initialises all the queues,
 * a loop that steps through time:
 *   - advances the time by one "tick"
 *   - Processes one time tick for each patient currently in each department
 *     (either making them wait if they are on the queue, or
 *      advancing their treatment if they are being treated)
 *   - Checks for any patients who have completed their current treatment,
 *      and remove from the department
 *   - Move all Patients that completed a treatment to their next department (or discharge them)
 *   - Checks each department, and moves patients from the front of the
 *       waiting queues into the Sets of patients being treated, if there is room
 *   - Gets any new patient that has arrived (depends on arrivalInterval) and
 *       puts them on the appropriate queue
 *   - Redraws all the departments - showing the patients being treated, and
 *     the patients waiting in the queues
 *   - Pauses briefly
 *
 * The simple simulation just has one department - ER beds that can treat 5 people at once.
 * Patients arrive and need treatment for random times.
 */

public class EmergencyRoom{

    private Map<String, Department> departments = new HashMap<String, Department>();
    private boolean running = false;

    // fields controlling the probabilities.
    private int arrivalInterval = 5;   // new patient every 5 ticks, on average
    private double probPri1 = 0.1; // 10% priority 1 patients
    private double probPri2 = 0.2; // 20% priority 2 patients
    private Random random = new Random();  // The random number generator.

    private Map<String, ArrayList> summary;

    private int time = 0; // The simulated time

    /**
     * Construct a new EmergencyRoom object, setting up the GUI
     */
    public EmergencyRoom(){
        setupGUI();
        reset();
    }

    public void setupGUI(){
        UI.addButton("Reset", this::reset);
        UI.addButton("Start", this::run);
        UI.addButton("Stop", ()->{running=false;});
        UI.addSlider("Av arrival interval", 1, 50, arrivalInterval,
            (double val)-> {arrivalInterval = (int)val;});
        UI.addSlider("Prob of Pri 1", 1, 100, probPri1*100,
            (double val)-> {probPri1 = val/100;});
        UI.addSlider("Prob of Pri 2", 1, 100, probPri2*100,
            (double val)-> {probPri2 = Math.min(val/100,1-probPri1);});

        UI.addButton("Quit", UI::quit);
        UI.setWindowSize(1000,600);
        UI.setDivider(0.5);
        setupSummary();
    }

    /**
     * Define the departments available and put them in the map of departments.
     * Each department needs to have a name and a maximum number of patients that
     * it can be treating at the same time.
     * Simple version: just a collection of 5 ER beds.
     */

    public void reset(){
        UI.clearGraphics();
        UI.clearText();
        running=false;
        time = 0;
        departments.put("ER beds",           new Department("ER beds", 8));
        departments.put("Operating Theatre", new Department("Operating Theatre", 2));
        departments.put("X-ray",             new Department("X-ray", 2));
        departments.put("MRI",               new Department("MRI", 1));
        departments.put("Ultrasound",        new Department("Ultrasound", 3));
        departments.put("Cardiology",        new Department("Cardiology", 2));
    }

    public void setupSummary(){
        this.summary=new HashMap <String, ArrayList> ();
        summary.put("waitTime", new ArrayList <Integer> ());
        summary.put("criticalWait", new ArrayList <Integer> ());
        summary.put("busyDept", new ArrayList <String> ());
    }

    /**
     * Main loop of the simulation
     */
    public void run(){
        running = true;
        while (running){
            // Hint: if you are stepping through a set, you can't remove
            //   items from the set inside the loop!
            //   If you need to remove items, you can add the items to a
            //   temporary list, and after the loop is done, remove all 
            //   the items on the temporary list from the set.
            List <Patient> toRemove=new ArrayList <Patient>();
            time++;
            UI.printMessage("T:"+time);
            if (time % arrivalInterval==0){
                Patient p= new Patient (time,randomPriority());
                String department1=p.getNextTreatment();
                p.incrementTreatmentNumber();
                departments.get(department1).addPatient(p);
                UI.println("Arrived: " + p);
            }

            for (String current:departments.keySet()){

                Department d=departments.get(current);
                Collection <Patient> waiting=d.getWaitingPatients();
                Collection <Patient> currentP=d.getCurrentPatients();
                for (Patient p:currentP){
                    if (!p.completedCurrentTreatment()){
                        p.advanceTreatmentByTick();
                    }
                    else if(p.completedAllTreatments()){
                        discharge(p);
                        toRemove.add(p);
                    }
                    else if (p.completedCurrentTreatment()){
                        String next=p.getNextTreatment();
                        departments.get(next).addPatient(p);
                        p.incrementTreatmentNumber();
                        toRemove.add(p);
                    }
                }
                for (Patient p:toRemove){
                    d.removePatient(p);
                }
                for (Patient p: waiting){
                    p.waitForATick();
                }
                d.moveFromWaiting();
                toRemove.clear();
            }           

            redraw();
            UI.sleep(300);
        }
        // Stopped
        reportStatistics();
    }

    /**
     * Report that a patient has been discharged, along with any
     * useful statistics about the patient
     */
    public void discharge(Patient p){
        UI.println("Discharge: " + p);

        summary.get("waitTime").add(p.getTotalTime());
        if (p.getPriority()==1)summary.get("criticalWait").add(p.getTotalTime());
        for (String treat: p.getTreatments())summary.get("busyDept").add(treat);        
    }

    /**
     * Report summary statistics about the simulation
     */
    public void reportStatistics(){
        UI.println("---------------");

        UI.printf("The average wait time across all patients was %d ticks. \n", computeAve(summary.get("waitTime")));
        UI.printf("The average wait time across priority 1 patients was %d ticks. \n", computeAve(summary.get("criticalWait")));

        ArrayList <String> dept =summary.get("busyDept");
        Map <String,Integer> sortedDept=new TreeMap <String,Integer>();
        sortedDept.put("ER beds",0);
        sortedDept.put("Operating Theatre",0);
        sortedDept.put("X-ray",0);
        sortedDept.put("MRI",0);  
        sortedDept.put("Ultrasound",0);
        sortedDept.put("Cardiology",0);
        for(String x : dept){
            if (x.equals("ER beds")) sortedDept.put("ER beds",sortedDept.get("ER beds")+1);
            else if (x.equals("Operating Theatre"))sortedDept.put("Operating Theatre",sortedDept.get("Operating Theatre")+1);
            else if (x.equals("X-ray"))sortedDept.put("X-ray",sortedDept.get("X-ray")+1);     
            else if (x.equals("MRI"))sortedDept.put("MRI",sortedDept.get("MRI")+1);
            else if (x.equals("Ultrasound"))sortedDept.put("Ultrasound",sortedDept.get("Ultrasound")+1);
            else if (x.equals("Cardiology"))sortedDept.put("Cardiology",sortedDept.get("Cardiology")+1);
        }
        UI.println("The amount of patients during this simulation in each department:");
        UI.println(sortedDept.toString());

        UI.println("---------------");
    }

    /**
     * Computes the average wait time for the given list. 
     */
    public int computeAve(ArrayList <Integer> list){
        int totalWait = 0;

        if (list.size()!=0){
            for(int x : list)    totalWait += x;
            return totalWait/list.size();
        }
        return 0;
    }

    /**
     * Redraws all the departments
     */
    public void redraw(){
        UI.clearGraphics();
        UI.setFontSize(14);
        UI.drawString("Treating Patients", 5, 15);
        UI.drawString("Waiting Queues", 200, 15);
        UI.drawLine(0,32,400, 32);
        double y = 80;
        for (String dept : new String[]{"ER beds","Operating Theatre", "X-ray", "Ultrasound", "MRI", "Cardiology"}){
            departments.get(dept).redraw(y);
            UI.drawLine(0,y+2,400, y+2);
            y += 50;
        }
    }

    /**  (COMPLETION)
     * Returns a random priority 1 - 3
     * Probability of a priority 1 patient should be probPri1
     * Probability of a priority 2 patient should be probPri2
     * Probability of a priority 3 patient should be (1-probPri1-probPri2)
     */
    public int randomPriority(){
        double num=random.nextDouble();
        double probPri3=1-probPri1-probPri2;
        if (num<=probPri1) return 1;
        else if (num>probPri1 &&num <=probPri2) return 2;
        else return 3;
    }

    public static void main(String[] arguments){
        new EmergencyRoom();
    }        

}
