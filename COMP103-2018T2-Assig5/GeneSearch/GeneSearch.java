// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2018T2, Assignment 5
 * Name: Luisa Kristen
 * Username: kristeluis
 * ID: 300444458
 */

import ecs100.*;
import java.util.*;
import java.io.*;

/** GeneSearch   */
public class GeneSearch{

    private List<Character> data;    // the genome data to search in
    private List<Character> pattern; // the pattern to search for
    private String patternString;         // the pattern to search for (as a String)
    private int maxErrors = 1;            // number of mismatching characters allowed

    private Map <String,List<Integer>> map; //map from subsequence of bases to list of intergers where it is in the data.
    /**
     * Construct a new GeneSearch object
     */
    public GeneSearch(){
        setupGUI();
        loadData();
    }

    /**
     * Initialise the interface
     */
    public void setupGUI(){
        UI.addTextField("Search Pattern", this::setSearchPattern);
        UI.addButton("ExactSearch", this::exactSearch);
        UI.addButton("Approx Search", this::approximateSearch);
        UI.addSlider("# mismatches allowed", 1, 5, maxErrors,
            (double n)->{maxErrors = (int)n;});
        UI.addButton("Implement Map", this::implementMap);
        UI.addButton("ExactSearch Map", this::mapExact);

        UI.addButton("Quit", UI::quit);
        UI.setDivider(1.0);
    }

    public void setSearchPattern(String v){
        patternString = v.toUpperCase();
        pattern = new ArrayList<Character>();
        for (int i=0; i<v.length(); i++){
            pattern.add(patternString.charAt(i));
        }
        UI.println("Search pattern is now "+ pattern);
    }

    /**
     * Search for all occurrences of the pattern in the data,
     * reporting the position of each occurrence and the total number of matches
     */    
    public void exactSearch(){
        if (pattern==null){UI.println("No pattern");return;}
        UI.println("===================\nExact searching for "+patternString);

        int j=0;
        Character current=pattern.get(j);
        int numMatches=0;
        for (int i=0; i<data.size();i++ ){
            if (data.get(i).equals(current)){
                if (j<pattern.size()-1){ //more letters in pattern to search
                    j++;
                    current=pattern.get(j);
                }
                else { // pattern complete 
                    UI.println("found at: "+(i-pattern.size()+1));
                    j=0;
                    current=pattern.get(j);
                    numMatches++;
                }
            }
            else {
                i=i-j;
                j=0;
                current=pattern.get(j);
            }
        }
        UI.println(numMatches+" exact matches");
    }

    /**
     * Search for all approximate occurrences of the pattern in the data,
     * (pattern is the same as a sub sequence of the data except for at most
     *  maxErrors characters that differ.)
     * Reports the position and data sequence of each occurrence and
     *  the total number of matches
     */    
    public void approximateSearch(){
        if (pattern==null){UI.println("No pattern");return;}
        UI.println("===================");
        UI.printf("searching for %s, %d mismatches allowed\n", patternString, maxErrors);

        int j=0;
        Character current=pattern.get(j);
        int numMatches=0;
        int mismatchesUsed=0;
        for (int i=0; i<data.size();i++ ){
            if (data.get(i).equals(current)){
                if (j<pattern.size()-1){ //more letters in pattern to search
                    j++;
                    current=pattern.get(j);
                }
                else { // pattern complete 
                    String curr="";
                    for (int k=i-pattern.size()+1; k<=i; k++){
                        curr+=data.get(k);
                    }
                    UI.printf("found at %d : %s \n",(i-pattern.size()+1),curr);
                    j=0;
                    current=pattern.get(j);
                    numMatches++;
                }
            }
            else if (mismatchesUsed<maxErrors){
                mismatchesUsed++;
                if (j<pattern.size()-1){ //more letters in pattern to search
                    j++;
                    current=pattern.get(j);
                }
                else { // pattern complete 
                    String curr="";
                    for (int k=i-pattern.size()+1; k<=i; k++){
                        curr+=data.get(k);
                    }
                    UI.printf("found at %d : %s \n",(i-pattern.size()+1),curr);
                    j=0;
                    current=pattern.get(j);
                    numMatches++;
                }
            }
            else {
                mismatchesUsed=0;
                i=i-j;
                j=0;
                current=pattern.get(j);
            }
        }
        UI.printf("%d matches with at most %d differences \n",numMatches,maxErrors );

    }

    /**
     * There are only 1024 possible subsequences of 5 bases (4 choices for each of 5 characters).
     *This builds a Map that listed all the positions where each pattern of 5 bases occurs in the data. 
     */ 
    public void implementMap(){
        map= new HashMap <String, List <Integer>> (1000000);
        String sequence="";
        int pos=0;
        if (data!=null && data.size()>5){
            for (int i=0; i< data.size()-5; i++){
                sequence="";
                for (int j=0;j<5;j++){ //gets a string of 5 bases
                    sequence+=data.get(i+j);
                }
                if (!map.containsKey(sequence)){
                    map.put(sequence,new ArrayList <Integer>());
                }
                map.get(sequence).add(i);
                UI.printMessage("Count: "+i);
            }
        }
        UI.println("Map implemented");
    }

    /**
     * Same as exact search but uses the map
     */
    public void mapExact(){
        if (pattern==null){UI.println("No pattern");return;}
        UI.println("===================\nExact searching for "+patternString);

        String mapSearch;
        int numMatches=0;
        if (patternString.length()>5)
            mapSearch=patternString.substring(0,5);

        else{
            UI.println("Your pattern must be 5 characters or longer to use the map.");
            return;
        }
        List <Integer> results=map.get(mapSearch);
        if (results.size()==0){
            UI.println ("No matches");
            return;
        }

        if (mapSearch.length()==patternString.length()) 
            UI.println(results.size()+" exact matches");
        else{
            int j=5;
            Character current=pattern.get(j);
            for (int i=0;i<results.size();i++){
                j=5;
                current=pattern.get(j);
                while (j<pattern.size()){
                    if (j<pattern.size()-1){ //more letters in pattern to search
                        if (data.get(results.get(i)+j).equals(current)){
                            j++;
                            current=pattern.get(j);
                        }
                        else break;
                    }
                    else if (data.get(results.get(i)+j).equals(current)){ // pattern complete 
                        String curr="";
                        for (int k=results.get(i); k<=results.get(i)+pattern.size()-1; k++){
                            curr+=data.get(k);
                        }
                        UI.println("found at: "+(results.get(i))+" Pattern: " +curr);
                        numMatches++;
                        break;
                    }
                    else break;
                }
            }
        }
        UI.println(numMatches+" exact matches");
    }

    /**
     * Load gene data from file into ArrayList of characters
     */
    public void loadData(){
        data = new ArrayList<Character>(1000000);
        try{
            Scanner sc = new Scanner(new File("acetobacter_pasteurianus.txt"));
            while (sc.hasNext()){
                String line = sc.nextLine();
                for (int i=0; i<line.length(); i++){
                    data.add(line.charAt(i));
                }
            }
            sc.close();
            UI.println("read "+data.size()+" letters");
        }
        catch(IOException e){UI.println("Fail: " + e);}
    }

    public static void main(String[] arguments){
        new GeneSearch();
    }        

}
