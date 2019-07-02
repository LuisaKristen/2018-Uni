//-------------Car Object-----------------
class Car {
  float carPosition=0; //current position
  float distance=0; //distance car has gone
  float speed=0; // current speed
  float dx=0; // distance car moves left and right when arrows pressed
  int direction=0; // direction of car, 0 for straight, -1 for left and 1 for right
  int timePenalty=0; //number of times the car has gone off the track.
  int displayTimeMessage=60; // display time message for 2 seconds, counts down 
  boolean off=false; // off track boolean 

  PImage commo=loadImage("Commo.png");
  PImage commoRight=loadImage("CommoRight.png");
  PImage commoLeft=loadImage("CommoLeft.png");

  PImage commoB=loadImage("CommoBlack.png");
  PImage commoRightB=loadImage("CommoBlackRight.png");
  PImage commoLeftB=loadImage("CommoBlackLeft.png");

  PImage commoW=loadImage("CommoWhite.png");
  PImage commoRightW=loadImage("CommoWhiteRight.png");
  PImage commoLeftW=loadImage("CommoWhiteLeft.png");

  float carX=(width/2)+ dx-commo.width/2;


  Car() {
    drawCar();
  }
//-------------Draws the Car-----------------

  void drawCar() {
    carX=(width/2)+ dx-commo.width/2;

    if (direction==0) {
      float nCarPos=(width/2)+ dx-commo.width/2;
      float yCarPos=(height/2)+commo.height+55;
      if (CarNumber==1) { // green car
        image(commo, nCarPos, yCarPos);
      } else if (CarNumber==2) { // white car
        image(commoW, nCarPos, yCarPos);
      } else if (CarNumber==3) { // black car
        image(commoB, nCarPos, yCarPos);
      }
    } else if (direction==1) {
      float nCarPos=(width/2)+ dx-commoRight.width/2;
      float yCarPos=(height/2)+commoRight.height-20;
      if (CarNumber==1) {
        image(commoRight, nCarPos, yCarPos);
      } else if (CarNumber==2) {
        image(commoRightW, nCarPos, yCarPos);
      } else if (CarNumber==3) {
        image(commoRightB, nCarPos, yCarPos);
      }
    } else if (direction==-1) {
      float nCarPos=(width/2)+ dx-commoLeft.width/2;
      float yCarPos=(height/2)+commoLeft.height+55;
      if (CarNumber==1) {
        image(commoLeft, nCarPos, yCarPos);
      } else if (CarNumber==2) {
        image(commoLeftW, nCarPos, yCarPos);
      } else if (CarNumber==3) {
        image(commoLeftB, nCarPos, yCarPos);
      }
    }
  }
  
  //-------------Updates Distance-----------------

  void updateDistance() {
    if (displayTimeMessage>=0 && off==true) { // displays off track message 
      textSize(50);
      fill(255);
      textAlign(CENTER);
      text("Off Track! Time Penalty: 2 Seconds", width/2, 100);
      displayTimeMessage--;
    } else if (off==true) { // resets off track message counter
      displayTimeMessage=60;
      off=false;
    }
    speed-= 0.1/frameRate; //naturally slowing down 

    if (carX>bg.OffTrackRight-commo.width  || carX<bg.OffTrackLeft ) { //off track, 20 pixel buffer
      off=true;
      timePenalty++;
      speed=0;
      float pos=((bg.OffTrackRight-bg.OffTrackLeft)/2)-width/3; // works out the x position it should be, ready to be plugged into car pos formula
      dx=pos;
      direction=0;
      drawCar();
    }
    if (speed>maxSpeed) { // cant go faster than max speed
      speed=maxSpeed;
    } else if (speed<0) { // cant slow down past 0
      speed=0;
    }
    distance+=(100*speed)*1/frameRate; // updates distance
  }
  
}// end of object
