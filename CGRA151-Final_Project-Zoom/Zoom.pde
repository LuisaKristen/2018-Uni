//-------------Objects----------------- //<>//
Background bg; 
Car car;
Mechanics mc;
Buttons button;

//-------------Keys Pressed-----------------
boolean [] keys = {false, false, false, false};

//-------------Booleans for what stage-----------------
boolean playing=false;
boolean howTo=false;
boolean highScores=false;
boolean finishedRace=false;
boolean maps=false;

//-------------Race start Countdown-----------------
int timeUntil= 20;

//-------------High scores as strings-----------------
String bestTime1="0";
String bestTime2="0";
String bestTime3="0";
String bestLast="0";


//-------------High scores as longs-----------------
long best1=999999999;
long best2=999999999;
long best3=999999999;

//-------------Selected Map, Car and Speed-----------------
int MapNumber=1;
int CarNumber=1;
float maxSpeed=0.65;


//-------------Setup-----------------
void setup() {
  fullScreen();
  background(0);
  loadPlay();
}

void loadPlay() { 
  playing=false; // resets all booleans to false
  howTo=false;
  highScores=false;
  finishedRace=false;
  mc=new Mechanics(MapNumber); // initilises all new objects
  car=new Car();
  bg= new Background();
  mc.makeTrack();
  button=new Buttons();
  bestLast="0";
  button.displayButtons();
  timeUntil=20;
}
//-------------Draw-----------------

void draw() {  
  if ( timeUntil >0 && playing) {// Before the race starts
    bg.drawBackground();
    car.drawCar();
    button.exitButton();
    startRace();
  } else if (playing) { //Playing
    textSize(15);
    background(0);
    bg.drawBackground();
    car.drawCar();
    mc.displayeStats();
    mc.getPos();
    car.updateDistance();
    mc.currentLapTime++;
    button.exitButton();
  } else if (!playing && !howTo&& !highScores&&!finishedRace&&!maps) { // Home Screen
    button.displayButtons();
  } else if (howTo) { //how to
    button.displayHowTo();
  } else if (highScores) { // high scores
    button.displayHighScores();
  } else if (finishedRace) { // end of race
    button.displayFinish(bestLast);
  } else if (maps) { // option selections
    button.displayMap();
  }
}

//-------------Keys-----------------
void keyPressed() { // sets coresponding key in array to true
  if (playing) {
    if ( keyCode == UP||key=='w' ) {
      keys[0]=true;
    }

    if (keyCode == DOWN||key=='s') {
      keys[1]=true;
    }

    if (keyCode == LEFT||key=='a') {
      keys[2]=true;
    }
    if (keyCode == RIGHT||key=='d') {
      keys[3]=true;
    }
    doKeys();
  }
}

void keyReleased() { // sets coresponding key in array to false
  if (playing) {
    if ( keyCode == UP||key=='w' ) {
      keys[0]=false;
    }
    if (keyCode == DOWN||key=='s') {
      keys[1]=false;
    }

    if (keyCode == LEFT||key=='a') {
      keys[2]=false;
    }
    if (keyCode == RIGHT||key=='d') {
      keys[3]=false;
    }
  }
}

void doKeys() { // does the action on the keys 
  if (keys[0]&&!keys[1]&&!keys[2]&&!keys[3]) { // can only press one key at a time 
    car.direction=0;
    car.speed+= 2/frameRate;
    car.updateDistance();
  } else if (keys[1]&&!keys[0]&&!keys[2]&&!keys[3]) {
    car.direction=0;
    car.speed-= 0.2/frameRate;
    car.updateDistance();
  } else if (keys[2]&&!keys[3]) {
    car.direction=-1;
    mc.playerCurve-=0.7/frameRate;
    car.dx-=10;
    car.updateDistance();
  } else if (keys[3]&&!keys[2]) {
    car.direction=1;
    mc.playerCurve+=0.7/frameRate;
    car.dx+=10;
    car.updateDistance();
  }
}


//-------------Mouse-----------------
void mousePressed() {
  if (mouseY>button.buttonY1 &&mouseY<button.buttonY1+button.buttonSize2 && !playing &&!howTo&&!highScores&&!maps) { 
    if (mouseX>button.buttonX1&& mouseX<button.buttonX1+button.buttonSize1) { //play button
      playing=true;
    } else if (mouseX>button.buttonX2&& mouseX<button.buttonX2+button.buttonSize1) { //how to button
      howTo=true;
    } else if (mouseX>width/2-button.buttonSize1/2 &&mouseX<width/2+button.buttonSize1/2) { //into map select
      maps=true;
    }
  } else if (mouseY>100 &&mouseY<100+button.buttonSize2 && maps) {  // map selection
    if (mouseX>button.buttonX1&& mouseX<button.buttonX1+button.buttonSize1) { //Easy
      MapNumber=1;
    } else if (mouseX>(width-button.buttonSize1)/2 &&mouseX<(width+button.buttonSize1)/2) { //Medium
      MapNumber=2;
    } else if (mouseX>button.buttonX2&& mouseX<button.buttonX2+button.buttonSize1) { //Hard
      MapNumber=3;
    }
  } else if (mouseY>300 &&mouseY<300+button.buttonSize1 && maps) {  // selecting cars
    if (mouseX>button.carX1&& mouseX<button.carX1+button.buttonSize1*1.5) { //Green
      CarNumber=1;
      maxSpeed=0.65;
    } else if (mouseX>button.carX2 &&mouseX<+button.carX2+button.buttonSize1*1.5) { //Black
      CarNumber=2;
      maxSpeed=0.85;
    } else if (mouseX>button.carX3 &&mouseX<+button.carX3+button.buttonSize1*1.5) { //White
      CarNumber=3;
      maxSpeed=1;
    }
  } else if (mouseX>width/2-button.buttonSize1/2 &&mouseX<width/2+button.buttonSize1/2 ) {
    if (mouseY>3*height/4&& mouseY<3*height/4+button.buttonSize2) {
      if (!playing &&(howTo||finishedRace||highScores||maps)) { // back from how to, high scores, maps
        howTo=false;
        highScores=false;
        finishedRace=false;
        maps=false;
        setup();
      } else if (!playing && !howTo&&!highScores) { //into high scores
        highScores=true;
      }
    }
  } else if (mouseX>3*width/4 &&mouseX<3*width/4+button.buttonSize1&& playing &&!howTo&&!highScores&&!maps) { //home button
    if (mouseY>height/2-1500&& mouseY<height/2-150+button.buttonSize2) {
      setup();
    }
  }
}

//-------------Start of Race display-----------------
void startRace() {

  timeUntil--;
  fill(255);
  textSize(52);
  textAlign(CENTER);

  if (timeUntil>15) {
    text("3...", width/2, height/4);
  } else if (timeUntil >10 ) {
    text("2...", width/2, height/4);
  } else if (timeUntil >5 ) {
    text("1...", width/2, height/4);
  } else if (timeUntil >0 ) {
    text("Go!!", width/2, height/4);
  }
}
