import java.io.*;
import java.util.*;

class MLReport{
    private  PrintWriter writer;
    private  PrintWriter out;

    // net configuration
    int nInp; // number of inputs
    int nHid; // number of neurons in hidden layer
    int nOut; //number of outputs

    // neuron coeficients
    private double bh[];
    private double wh[][]; 
    private double bo[];
    private double wo[][];
    // neuron derivatives 
    private double der_bh[];
    private double der_wh[][];
    private double der_bo[];
    private double der_wo[][];
    // levels inside the neuron
    private double zh[];
    private double yh[];
    private double zo[];
    private double yo[];
    // current neuron inputs
    private double x[];
    private double nu = 1;
    private double dw;
    // current target
    private double t[];
    
    private double err[];
    private double errTotal;
    private double nSteps =1500;
    private double deltao[];
    private double deltah[];

    private int nPatts;
    private double x_t[][];
    private double t_t[][];
    private double x_w[][];

    private int patn = 0;

    private String trainName="train.txt";
    private String workName="work.txt";
    public MLReport() {
        nInp = 64;
        nHid = 8;
        nOut = 8;
        
        nPatts=750;

        // inputs
        x = new double[nInp];
        // hidden layer
        wh = new double[nHid][nInp];
        bh = new double[nHid];
        zh  = new double[nHid];
        yh  = new double[nHid];
        der_wh = new double[nHid][nInp];
        der_bh = new double[nHid];

        // output layer
        bo = new double[nOut];
        der_bo = new double[nOut];
        wo = new double[nOut][nHid];
        der_wo = new double[nOut][nHid];
        zo  = new double[nOut];
        yo  = new double[nOut];

        t = new double[nOut];
        deltao = new double[nOut];
        deltah = new double[nHid];
        err = new double[nOut];

        x_t=new double [nPatts][nInp];
        t_t=new double [nPatts][8];
        x_w=new double [nPatts][nInp];

        dw = 0.1;

    }

    public void userInteraction(){
        Scanner in=null;
        try{
            in= new Scanner (System.in);
        }
        catch(Exception e){
            System.out.println("Error while creating scanner:" + e.getMessage());
            return;
        }  

        System.out.println("Would you like to train the system? (y/n)");
        String ans=in.next();
        while (!ans.equalsIgnoreCase("y")&&!ans.equalsIgnoreCase("yes")&&!ans.equalsIgnoreCase("n")&&!ans.equalsIgnoreCase("no")){
            System.out.println("Would you like to train the system? (y/n)");
            ans=in.next();
        }
        if (ans.equalsIgnoreCase("y")||ans.equalsIgnoreCase("yes")){
            System.out.println("What is the name of the file? (include .txt)");
            trainName=in.next();
            InitNet(-1,1);
            LoadTrainingSet(trainName); 
            System.out.println("Training...");
            Train();
            saveTrainedSet();
        }
        else if (ans.equalsIgnoreCase("n")||ans.equalsIgnoreCase("no")){
            System.out.println("Loading previously trained file...");
            loadTrainedSet();
        }

        System.out.println("What is the name of the working dataset? (include .txt)");
        workName=in.next();
        LoadWorkingSet(workName);
        processWorkingSet();
    }

    public void saveTrainedSet(){
        try {
            writer = new PrintWriter("save.txt", "UTF-8");
        } catch(Exception e){
            System.out.println("Error while reading file line by line:" + e.getMessage());
            return;
        }  
        writer.flush();
        for ( int inp = 0 ; inp < nInp ; inp++){
            for (int hid = 0 ; hid < nHid; hid++){
                writer.println(wh[hid][inp]);
            }
        }
        // hidden layer biases
        for (int hid = 0 ; hid < nHid; hid++){
            writer.println(bh[hid]);
        }
        // output layer weights
        for (int hid = 0 ; hid < nHid; hid++){
            for ( int out = 0 ; out < nOut ; out++){
                writer.println(wo[out][hid]);
            }
        }
        //output neuron bias 
        for (int out =0 ; out< nOut;out++){
            writer.println(bo[out]);
        }
        writer.flush();
        writer.close();
    }

    public void loadTrainedSet(){
        Scanner sc=null;
        try { sc =new Scanner(new File ("save.txt"));}
        catch(Exception e){
            System.out.println("save.txt is not present" + e.getMessage());
            return;
        } 
        for ( int inp = 0 ; inp < nInp ; inp++){
            for (int hid = 0 ; hid < nHid; hid++){
                wh[hid][inp]=sc.nextDouble();
            }
        }
        // hidden layer biases
        for (int hid = 0 ; hid < nHid; hid++){
            bh[hid]=sc.nextDouble();
        }
        // output layer weights
        for (int hid = 0 ; hid < nHid; hid++){
            for ( int out = 0 ; out < nOut ; out++){
                wo[out][hid]=sc.nextDouble();
            }
        }
        //output neuron bias 
        for (int out =0 ; out< nOut;out++){
            bo[out]=sc.nextDouble();
        }
        System.out.println("Parameters loaded");
    }

    // to use this function declare 
    // one more variable nPatts - number 
    // of rows in training dataset
    // set it to 750
    public int LoadTrainingSet(String fileName){
        FileInputStream fstream;
        int patn = 0;
        try {
            System.out.println("Opening trainings file "+fileName);
            fstream = new FileInputStream(fileName);
            System.out.println("File found OK");
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;
            patn = 0;
            System.out.println("Loading training dataset");
            // read until nPatts lines or file ends - whatever first
            while ( (patn<nPatts) && ((strLine = br.readLine()) != null)) {
                String[] fields = strLine.split(" ");
                int i = 0;
                while ( i < nInp){  // read inputs
                    x_t[patn][i] = Double.parseDouble(fields[i]);
                    i++;
                }
                int iOut = 0;
                while( iOut < nOut){
                    t_t[patn][iOut] = Double.parseDouble(fields[i]);
                    iOut++;
                    i++;
                }
                patn++;
            }
            System.out.println("Training file open. "+ (patn-1) +" points loaded");
        } catch (Exception e) {
            System.out.println(" Error loading training dataset. Error at line "+patn);
            System.out.println(e.getMessage());
            return -1;
        }
        return 0;
    }

    public int LoadWorkingSet(String fileName){
        FileInputStream fstream;
        try {
            System.out.println("Opening working set file "+fileName);
            //fileName = "semeion-work.txt";
            fstream = new FileInputStream(fileName);
            System.out.println("File found OK");
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;
            patn = 0;
            System.out.println("Loading work dataset");
            while ( (patn<nPatts) && ((strLine = br.readLine()) != null)) {
                String[] fields = strLine.split(" ");
                int i = 0;
                while ( i < nInp){  // read inputs
                    x_w[patn][i] = Double.parseDouble(fields[i]);
                    i++;
                }
                int iOut = 0;
                patn++;
            }
            System.out.println("Working file open. "+ (patn-1) +" points loaded");
        } catch (Exception e) {
            System.out.println(" Error loading training dataset. Error at line "+patn);
            System.out.println(e.getMessage());
            return -1;
        }
        return 0;
    }
    // returns index of the maximum 
    // in output array
    public int OutputMax(){
        int maxInd = -1;
        double max  = -1000.0;
        for (int out = 0 ; out < nOut ; out++){
            if (yo[out] > max){
                max = yo[out];
                maxInd = out;
            }
        }
        return maxInd;
    }

    //check how many iimages in training set were 
    // identified OK
    public double CheckTrainSet(){
        double success = 0.0;
        for (int iPat = 0 ; iPat < nPatts ; iPat++){
            PickTrainRow(iPat);
            ForwardProp();
            if ( t[OutputMax()] == 1.0){
                success = success + 1.0;
            }
        }
        double detectionRate = success/((double)nPatts);
        return detectionRate;
    }

    public void processWorkingSet(){
        try {
            out = new PrintWriter("answers.txt", "UTF-8");
        } catch(Exception e){
            System.out.println("Error saving answers:" + e.getMessage());
            return;
        }
        out.flush();
        for(int i = 0; i < patn; i++){
            x=x_w[i];
            ForwardProp();
            out.printf("Image number %d = %d \n",i+1,OutputMax());
        }
        System.out.println("Answers saved to file." );
        out.close();
    }

    // fill neuron biases and weights with random numbers 
    public void InitNet(double minX,double maxX){
        for(int i = 0; i < bh.length; i++){
            Random r = new Random();
            bh[i] = minX + (maxX - minX) * r.nextDouble();
        }
        for(int i = 0; i < bo.length; i++){
            Random r = new Random();
            bo[i] = minX + (maxX - minX) * r.nextDouble();
        }
        for(int i = 0; i < wh.length; i++){
            for(int j = 0; j < wh[0].length; j++){
                Random r = new Random();
                wh[i][j] = minX + (maxX - minX) * r.nextDouble();
            }
        }
        for(int i = 0; i < wo.length; i++){
            for(int j = 0; j < wo[0].length; j++){
                Random r = new Random();
                wo[i][j] = minX + (maxX - minX) * r.nextDouble();
            }
        }
    }

    // picks row from training set
    public void PickTrainRow(int row){
        // pick inputs fron training dataset    
        for (int inp = 0 ; inp < nInp; inp++){
            x[inp] = x_t[row][inp];   
        }
        // pick targets from training dataset
        for (int out = 0; out < nOut; out++){
            t[out] = t_t[row][out];   
        }
    }

    private static double sigmoid(double x)
    {
        return 1.0 / (1.0 + Math.exp(-x));
    }
    // calculate neuron outputs, and differnce with target
    public void ForwardProp(){
        // input to hidden layer
        // for each neuron in hidden layer
        for ( int i = 0 ; i < nHid ; i++){
            zh[i] = bh[i]; 
            for (int j = 0 ; j < nInp ; j++){ 
                zh[i] = zh[i] + x[j]*wh[i][j]; //run-of-the-mill convolution
            } // y[i] ready 
            yh[i] = sigmoid(zh[i]);
        }
        // for each neuron in output layer
        for (int i=0; i < nOut; i++){
            zo[i] = bo[i];
            for (int j=0; j < nHid; j++){
                zo[i] = zo[i]+ yh[j]*wo[i][j];
            }
            yo[i] = sigmoid(zo[i]);
            err[i] = yo[i] -t[i];
        }
    }

    public double CurrentError(){
        double error = 0.0;
        ForwardProp();
        for (int out = 0 ; out < nOut; out++){
            error = error + err[out]*err[out];
        }
        return error;
    }

    // calculates error for whole Truth Table
    public double TotalDatasetErr(){
        errTotal = 0.0;
        for (int dsRow = 0 ; dsRow < 4 ; dsRow++){
            PickTrainRow(dsRow);
            ForwardProp();
            errTotal = errTotal + CurrentError();
        }
        return errTotal/nOut; // return average error
    }

    private double dsigmoid(double x){
        return (1.0-sigmoid(x))*sigmoid(x);
    }

    public void BackProp(){
        ForwardProp();
        // for output layer
        // for each output calculate delta
        for ( int out = 0 ; out < nOut; out++){
            deltao[out] = (yo[out]-t[out])*dsigmoid(zo[out]);
        }

        // for each neuron of hidden layer
        // calculate deltah
        for ( int hid = 0 ; hid < nHid ; hid++){
            // delta for each hidden layer neuron
            deltah[hid] = 0.0;
            for (int out = 0 ; out < nOut ; out++){
                deltah[hid]=deltah[hid] + wo[out][hid]*deltao[out];
            }
            deltah[hid] = deltah[hid]*dsigmoid(zh[hid]);
        }

        for ( int out = 0 ; out < nOut; out++){
            // bias derivative
            der_bo[out] = deltao[out];
            // for each weight coming from hidden layer

            for ( int hid = 0 ; hid < nHid ; hid++){
                der_wo[out][hid] = deltao[out]*yh[hid];
            }
        }
        // for each neuron of hidden layer
        for ( int hid = 0 ; hid < nHid ; hid++){
            der_bh[hid] = deltah[hid];
        }
        // hidden layer derivatives
        for ( int inp = 0 ; inp < nInp ; inp++){
            for ( int hid = 0 ; hid < nHid ; hid++){
                der_wh[hid][inp] = deltah[hid]*x[inp];
            }
        }

    }

    // make step. Step size
    // determined by derivative
    private void DerStep(){
        for ( int inp = 0 ; inp < nInp ; inp++){
            for (int hid = 0 ; hid < nHid; hid++){
                wh[hid][inp] = wh[hid][inp] - nu*der_wh[hid][inp];
            }
        }
        // hidden layer biases
        for (int hid = 0 ; hid < nHid; hid++){
            bh[hid] = bh[hid]  - nu * der_bh[hid];
        }
        // output layer weights
        for (int hid = 0 ; hid < nHid; hid++){
            for ( int out = 0 ; out < nOut ; out++){
                wo[out][hid] = wo[out][hid] - nu*der_wo[out][hid];
            }
        }
        //output neuron bias 
        for (int out =0 ; out< nOut;out++){
            bo[out] = bo[out] - nu*der_bo[out];
        }
    }

    // train perceptron using global search
    public void Train(){

        int step = 0;
        nSteps = 3000;
        nu = 1;
        while (step < nSteps ){
            //PrintNet();
            int Min = 0 ;
            int Max = nPatts-1;
            int ip = Min + (int)(Math.random() * ((Max - Min) + 1));
            //System.out.println("ip = "+ip);
            PickTrainRow(ip);
            // Deriv(); // calculate deivatives
            BackProp();
            DerStep();  //make search step
            errTotal = TotalDatasetErr();

            if (errTotal < 0.005) { 
                break;
            }
            step = step +1;
        }

        double detectionRate=CheckTrainSet();
        if (detectionRate<0.95){
            InitNet(-1,1);
            Train();}
        else{
            System.out. println("Convergence achieved");
            System.out. println("Error="+errTotal);
            System.out.println("Detection rate = "+detectionRate);
        }

        //Surf2D(); `   
    }


    public static void main(String args[]){
        MLReport ex = new MLReport();
        ex.userInteraction();
        return;
    } //main

} //class
