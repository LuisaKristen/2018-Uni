//-------------Mechanics-----------------

class Mechanics {
  long currentLapTime=0; // most recent lap time
  long currentBest=999999999; //current best time, set to very large number so the first lap will be smaller than it, thus being the current best
  
  String lapString=""; // lap time but converted to string
  String [] lastFive= {"0", "0", "0", "0", "0"}; // list of last 5 lap times
  float targetCurveture=0; // target curvature of track
  float curvature=0;// curvature at a given time
  ArrayList <PVector> track=new ArrayList<PVector>(); //curvature, distance
  float trackCurve=0; // current curvature of the track
  float playerCurve=0; // current curvature of the track

  float trackDistance;// distance car has moved around the current lap
  int trackSection=0; //section of the track the car is currently on

  int numRounds=0;// number of rounds, after rounds load end screen.
  int lapCompleted=5; // lap completed message shows for 5 method calls 
  boolean lapComplete=false;
  int t; //track number, coresponds to map 
  

  Mechanics(int mapNum) {
    t=mapNum;
  }
  //-------------Time-----------------

  void displayTime(long time) {
    lapComplete=true;
   
    if (time<currentBest ) { // Best time in current run
      currentBest=time;
    }
    int seconds=(int)( time/frameRate); // converts time to seconds
    
    seconds+=2*car.timePenalty; //adds 2 seconds for each time they went off the track in that round

    int minutes=(int)(seconds/60); // seconds=> min
    seconds-=minutes*60; // remainder seconds
    int millis=(int)(time -seconds-minutes); // remainder milliseconds
    lapString= (minutes+"."+seconds+"."+millis); // converts to string
    lastFive[4]=lastFive[3]; // shuffles all laps down 1
    lastFive[3]=lastFive[2];
    lastFive[2]=lastFive[1];
    lastFive[1]=lastFive[0];
    lastFive[0]=lapString;
    numRounds++; // increases number of laps
    if (numRounds==5) { // game finished
      seconds=(int)(currentBest/frameRate); // converts best to string
      minutes=(int)(seconds/60);
      seconds-=minutes*60;
      millis=(int)(currentBest - seconds- minutes);
      lapString= (minutes+"."+seconds+"."+millis);
      
      if (currentBest < best1 && MapNumber==1) { // sets it as high score if best ever
        best1=currentBest;
        bestTime1=lapString;
      } else if (currentBest < best2 && MapNumber==2) {
        best2=currentBest;
        bestTime2=lapString;
      } else if (currentBest < best2 && MapNumber==3) {
        best3=currentBest;
        bestTime3=lapString;
      }
      
      bestLast=lapString;
      button.displayFinish(lapString);
    }
  }
  //-------------Statistics-----------------

  void displayeStats() {
    fill(0);
    textAlign(LEFT);

    text("Distance: " + nf(car.distance, 3, 1), 0, 15); // distance around map
    text("Speed"+nf(car.speed*250, 3, 1), 0, 27); // current speed
    text(lastFive[0], 0, 39);// lap times
    text(lastFive[1], 0, 51);
    text(lastFive[2], 0, 63);
    text(lastFive[3], 0, 75);
    text(lastFive[4], 0, 87);

    fill(255);
    text("Distance: " + nf(car.distance, 3, 1), -1, 14);
    text("Speed"+nf(car.speed*250, 3, 1), -1, 26);
    text(lastFive[0], 2, 40);
    text(lastFive[1], 2, 53);
    text(lastFive[2], 2, 64);
    text(lastFive[3], 2, 77);
    text(lastFive[4], 2, 89);
  }
  //-------------Position-----------------

  void getPos() {
    float offSet=0;
    if (car.distance >=trackDistance) {
      car.distance-=trackDistance; // reset track 
      displayTime(currentLapTime); // update the last lap time
      currentLapTime=0; //resets
      car.timePenalty=0;
    }
     if (lapCompleted>=0 && lapComplete==true) { // displays lap complete message 
      textSize(50);
      fill(255);
      textAlign(CENTER);
      text("Lap Number: " + (numRounds+1), width/2, 100);
      lapCompleted--;
    } else if (lapComplete==true){ // resets lap complete message counter
      lapCompleted=5;
      lapComplete=false;
    }
    trackSection=0; 
    while (trackSection < track.size() && offSet <= car.distance)
    {      
      offSet += track.get(trackSection).y;
      trackSection++;
    }
    targetCurveture=track.get(trackSection-1).x; 
    float fTrackCurveDiff = (targetCurveture - curvature)*1/frameRate *car.speed;
    curvature+=fTrackCurveDiff;

    trackCurve = (targetCurveture - curvature)*1/frameRate *car.speed; // curve of the track 
  }
  //-------------Creates the tracks as a list of PVectors-----------------

  void makeTrack() {
    if (t==1) { // easy 
      track.add(new PVector(0, 10)); //start finish
      track.add(new PVector(0, 200));  // the first number = curvature of corner 
      track.add(new PVector(1, 200));  // second number = length of corner
      track.add(new PVector(0, 400));  // 0 = straight
      track.add(new PVector(-1, 100));  // -x = left
      track.add(new PVector(0, 200));  //+x = right
      track.add(new PVector(-1, 200)); 
      track.add(new PVector(1, 200)); 
      track.add(new PVector(0, 200)); 
      track.add(new PVector(0.2, 500)); 
      track.add(new PVector(0, 200));
    } else if (t==2) { // medium
      track.add(new PVector(0, 10)); //start finish
      track.add(new PVector(0, 100));
      track.add(new PVector(0.2, 100)); 
      track.add(new PVector(0.5, 100)); 
      track.add(new PVector(0.7, 100)); 
      track.add(new PVector(-0.7, 50)); 
      track.add(new PVector(0, 200)); 
      track.add(new PVector(-0.5, 100)); 
      track.add(new PVector(0, 100)); 
      track.add(new PVector(-0.9, 100)); 
      track.add(new PVector(0, 400)); 
      track.add(new PVector(-1, 100)); 
      track.add(new PVector(0, 100)); 
      track.add(new PVector(0.2, 100)); 
      track.add(new PVector(0.5, 100)); 
      track.add(new PVector(0.7, 100));
      track.add(new PVector(0, 500));
      track.add(new PVector(-1, 200));
    } else if (t==3) { // hard
      track.add(new PVector(0, 10)); //start finish
      track.add(new PVector(0, 100));
      track.add(new PVector(1, 100)); 
      track.add(new PVector(-0.5, 100)); 
      track.add(new PVector(-5, 100)); 
      track.add(new PVector(0.5, 50)); 
      track.add(new PVector(0, 150)); 
      track.add(new PVector(1, 100)); 
      track.add(new PVector(0, 100)); 
      track.add(new PVector(-2, 100)); 
      track.add(new PVector(0, 400)); 
      track.add(new PVector(-2, 100)); 
      track.add(new PVector(0, 100)); 
      track.add(new PVector(1, 100)); 
      track.add(new PVector(-0.5, 100)); 
      track.add(new PVector(1, 100));
      track.add(new PVector(-1, 100));
      track.add(new PVector(-0.5, 200));
      track.add(new PVector(1, 100));
      track.add(new PVector(2, 50));
      track.add(new PVector(1, 50));
      track.add(new PVector(-1, 50));
      track.add(new PVector(0, 50));
    }

    for (PVector t : track) { // calculates track total distance
      trackDistance+=t.y;
    }
  }
} // end of object
