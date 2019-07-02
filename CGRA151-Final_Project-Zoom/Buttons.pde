//-------------Buttons Object-----------------

class Buttons {
  float buttonY1=height/3; //standard y position
  float buttonSize1=200; // standard width
  float buttonSize2=100; //standard height
  float buttonX2=2*width/3;
  float buttonX1=width/3 - buttonSize1;

  float largeButtonX=(width/2)-50-buttonX1*1.5; // large button size used for cars
  float carX1=(width/2)-1.5*largeButtonX - 100; // car button positions
  float carX2=(width/2)-0.5*largeButtonX ;
  float carX3=(width/2)+0.5*largeButtonX +100;


  Buttons() {
    displayButtons();
  }
//-------------Exit-----------------

  void exitButton() { 
    buttonTemplate(3*width/4, height/2-150, buttonSize1, buttonSize2, "Home");
  }
  //-------------Back-----------------

  void displayBack() { 
    buttonTemplate(width/2-buttonSize1/2, 3*height/4, buttonSize1, buttonSize2, "Back");
  }
  //-------------Home Screen-----------------

  void displayButtons() { 
    bg.drawBackground();
    textSize(200);
    fill(255);
    textAlign(CENTER);
    text("Zoom", width/2, 200);

    //play button
    buttonTemplate(buttonX1, buttonY1, buttonSize1, buttonSize2, "Play");
    //how to button
    buttonTemplate(buttonX2, buttonY1, buttonSize1, buttonSize2, "How To");
    //high scores
    buttonTemplate(width/2-buttonSize1/2, 3*height/4, buttonSize1, buttonSize2, "Scores");
    //map button
    buttonTemplate(width/2-buttonSize1/2, buttonY1, buttonSize1, buttonSize2, "Options");
  }
  //-------------How To-----------------

  void displayHowTo() { 
    bg.drawBackground();
    textSize(30);
    fill(0);
    textAlign(CENTER);
    text("How to play:", width/2, 100);
    text("Use the Arrow or WASD keys to move,", width/2, 150);
    text("Complete 5 laps to win,", width/2, 200);
    text("Don't stray off the path, or you will receieve a time penalty,", width/2, 250);
    text("Choose your Vechicle and Map", width/2, 300);

    text("Hint: Only press one button at a time.", width/2, 350);
    text("Press ESC to exit the game.", width/2, 400);

    fill(255);
    text("How to play:", -2+width/2, 98);
    text("Use the Arrow or WASD keys to move,", -2+width/2, 148);
    text("Complete 5 laps to win,", -2+width/2, 198);
    text("Don't stray off the path, or you will receieve a time penalty,", -2+width/2, 248);
    text("Choose your Vechicle and Map", -2+width/2, 298);
    text("Hint: Only press one button at a time.", -2+width/2, 348);
    text("Press ESC to exit the game.", -2+width/2, 398);

    displayBack();
  }
//-------------High Scores-----------------

  void displayHighScores() {
    bg.drawBackground();
    textSize(52);
    fill(0);
    textAlign(CENTER);
    text("Best Score:", width/2, 100);
    text("Map 1: "+bestTime1, width/2, 200);
    text("Map 2: "+bestTime2, width/2, 300);
    text("Map 3: "+bestTime3, width/2, 400);

    fill(255);
    text("Best Score:", -2+width/2, 98);
    text("Map 1: "+bestTime1, -2+width/2, 198);
    text("Map 2: "+bestTime2, -2+width/2, 298);
    text("Map 3: "+bestTime3, -2+ width/2, 398);
    displayBack();
  }
  
 //-------------Finish Race-----------------
 
  void displayFinish(String lapString) {
    bg.drawBackground();
    textSize(52);
    fill(0);
    textAlign(CENTER);
    text("Congrats, you finished the race!", width/2, 100);
    text("Your best time was: "+lapString, width/2, 200);
    buttonTemplate(width/2-buttonSize1/2, 3*height/4, buttonSize1, buttonSize2, "Back");
    playing=false;
    finishedRace=true;
  }
//-------------Map Selection-----------------

  void displayMap() {
    bg.drawBackground();
    buttonTemplateTwo(buttonX1, 100, buttonSize1, buttonSize2, "Easy", color(50, 40, 230) ); //map 1
    buttonTemplateTwo(width/2-buttonSize1/2, 100, buttonSize1, buttonSize2, "Medium", color(50, 40, 230)); //map 2
    buttonTemplateTwo(buttonX2, 100, buttonSize1, buttonSize2, "Hard", color(50, 40, 230)); // map 3

    buttonTemplateTwo(carX1, 300, buttonSize1*1.5, buttonSize1, "", color(180, 255, 200) ); //car 1
    buttonTemplateTwo(carX2, 300, buttonSize1*1.5, buttonSize1, "", color(180, 255, 200)); //car 2
    buttonTemplateTwo(carX3, 300, buttonSize1*1.5, buttonSize1, "", color(180, 255, 200) ); //car 3

float middle= (buttonSize1*1.5)/2;

    textSize(30);
    fill(255);
    textAlign(CENTER);
    text("Stock", carX1+middle, 480);
    text("Super", carX2+middle, 480);
    text("Turbo", carX3+middle, 480);
    fill(0);
    middle-=2;
    text("Stock", carX1+middle, 478);
    text("Super", carX2+middle, 478);
    text("Turbo", carX3+middle, 478);

    image(loadImage("Commo.png"), carX1+70, 320); //loads car image into box
    image(loadImage("CommoWhite.png"), carX2+70, 320);
    image(loadImage("CommoBlack.png"), carX3+70, 320);

    buttonTemplate(width/2-buttonSize1/2, 3*height/4, buttonSize1, buttonSize2, "Back");

    stroke(255, 0, 0);
    noFill();
    if (MapNumber==1) {
      rect(buttonX1-2, 98, buttonSize1+4, buttonSize2+4, 7);
    } else if (MapNumber==2) {
      rect(-2+width/2-buttonSize1/2, 98, buttonSize1+4, buttonSize2+4, 7);
    } else if (MapNumber==3) {
      rect(buttonX2-2, 98, buttonSize1+4, buttonSize2+4, 7);
    }

    if (CarNumber==1) {
      rect(carX1-2, 298, 4+buttonSize1*1.5, 4+buttonSize1, 7);
    } else if (CarNumber==2) {
      rect(carX2-2, 298, 4+buttonSize1*1.5, 4+buttonSize1, 7);
    } else if (CarNumber==3) {
      rect(carX3-2, 298, 4+buttonSize1*1.5, 4+buttonSize1, 7);
    }
  }
//-------------Button Template One-----------------

  void buttonTemplate(float x, float y, float size1, float size2, String s) { //green sign with sticks 
    stroke(23, 143, 56);
    strokeWeight(3);
    fill(255);
    rect(x, y, size1, size2, 7);
    fill(23, 143, 56);
    noStroke();
    rect(x+4, y+4, size1-8, size2-8, 7);

    fill(255);
    textSize(52);
    textAlign(CENTER);
    text(s, x+size1/2, y+size1/3);

    stroke(0);
    line(x+size1/3, y+100, x+size1/3, y+150);
    line(x+2*size1/3, y+100, x+2*size1/3, y+150);
  }
//-------------Button Template Two-----------------

  void buttonTemplateTwo(float x, float y, float size1, float size2, String s, color c) { //used for buttons without the sticks, also can specify the colour
    stroke(c);
    strokeWeight(3);
    fill(255);
    rect(x, y, size1, size2, 7);
    fill(c);
    noStroke();
    rect(x+4, y+4, size1-8, size2-8, 7);

    fill(255);
    textSize(52);
    textAlign(CENTER);
    text(s, x+size1/2, y+size1/3);
  }
} // end of object
