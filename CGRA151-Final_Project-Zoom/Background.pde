//-------------Background Object-----------------
class Background {
  color nClipColour=color(245, 5, 0); // default clip colour
  color nGrassColour=color(0, 245, 50); //default grass colour
  color nRoadColour =color(95); // default road colur

  float OffTrackRight;  //used to determine if car is off track;
  float OffTrackLeft;

  Background() {
    drawBackground();
  }

  //-------------Decides what background to draw-----------------
  void drawBackground() {
    if (MapNumber==1) {
      background1();
    } else if (MapNumber==2) {
      background2();
    } else if (MapNumber==3) {
      background3();
    }
  }

  //-------------Bg 1, grass setting-----------------
  void background1() {
    for (int y=0; y<height/2; y++) { // for all columns
      for (int x=0; x<width; x++) { // for all rows 
        float perspective= width*y/height; 
        float fMiddlePoint=width/2+2*width/2 * mc.curvature* pow((1.0f - perspective/width), 3); // middle of the road
        float fRoadWidth=(150+perspective)/2; // /2 since the road is symmertical 
        float fClipWidth=fRoadWidth*0.15; 
        float nLeftGrass =(fMiddlePoint-fRoadWidth-fClipWidth); 
        float nLeftClip =(fMiddlePoint-fRoadWidth);
        float nRightGrass =(fMiddlePoint+fRoadWidth+fClipWidth);
        float nRightClip =(fMiddlePoint+fRoadWidth);

        int nRow=(height/2)+y; // start half way down the screen 

        OffTrackRight=nRightClip;
        OffTrackLeft=nLeftClip;


        if (sin(20 *  pow(1 - perspective/width, 3) +car.distance * 0.1) > 0.0) { // works out if current x is a light green or dark green grass patch
          nGrassColour=color(0, 245, 50);
        } else {
          nGrassColour=color(0, 150, 30);
        }
        if (mc.trackSection-1==0) { // start of the track is white, otherwise grey
          nRoadColour=color(255);
        } else {
          nRoadColour=color(95);
        }
        if (-sin(80*pow(1-perspective/width, 2)+car.distance)<0) { //  works out if current x is a red or white clip
          nClipColour=color(255);
        } else {
          nClipColour=color(245, 5, 0);
        }
        if (x<2) { // for the first pixel in a row draw:
          stroke(nGrassColour);
          line (x, nRow, nLeftGrass, nRow);
          stroke(nClipColour);
          line (nLeftGrass, nRow, nLeftClip, nRow);
          stroke(nRoadColour);
          line ( nLeftClip, nRow, nRightClip, nRow);
          stroke(nClipColour);
          line ( nRightClip, nRow, nRightGrass, nRow);
          stroke(nGrassColour);
          line ( nRightGrass, nRow, width, nRow);
        }
      }
    }
    //-------------Bg 1, sky-----------------

    int r=0;
    int g =0;
    int b=160;
    for (int j=0; j<height/2; j++) { 
      stroke(r, g, b);
      line(0, j, width, j);
      g++; // creates gradient
    }
    //-------------Bg 1, hills-----------------

    for (int x = 0; x < width; x++)
    {
      int nHillHeight = (int)(abs(sin(x * 0.01 + mc.trackCurve) * 25.0));
      for (int y2 = (height / 2) - nHillHeight; y2 < height / 2; y2++) {
        stroke(255, 255, 0);
        point(x, y2);
      }
    }
  }

  //-------------Bg 2, Desert-----------------

  void background2() {
    for (int y=0; y<height/2; y++) { // same as BG 1
      for (int x=0; x<width; x++) {
        float perspective= width*y/height;
        float fMiddlePoint=width/2+2*width/2 * mc.curvature* pow((1.0f - perspective/width), 3);
        float fRoadWidth=(150+perspective)/2; // /2 since the road is symmertical 
        float fClipWidth=fRoadWidth*0.15;
        float nLeftGrass =(fMiddlePoint-fRoadWidth-fClipWidth);
        float nLeftClip =(fMiddlePoint-fRoadWidth);
        float nRightGrass =(fMiddlePoint+fRoadWidth+fClipWidth);
        float nRightClip =(fMiddlePoint+fRoadWidth);
        int nRow=(height/2)+y;

        OffTrackRight=nRightClip;
        OffTrackLeft=nLeftClip;

        if (sin(20 *  pow(1 - perspective/width, 3) +car.distance * 0.1) > 0.0) {
          nGrassColour=color(255, 180, 0);
        } else {
          nGrassColour=color(230, 140, 15);
        }
        if (-sin(80*pow(1-perspective/width, 2)+car.distance)<0) {
          nClipColour=color(255);
        } else {
          nClipColour=color(245, 5, 0);
        }

        if (mc.trackSection-1==0) {
          nRoadColour=color(255);
        } else {
          nRoadColour=color(95);
        }

        if (x<2) {
          stroke(nGrassColour);
          line (x, nRow, nLeftGrass, nRow);
          stroke(nClipColour);
          line (nLeftGrass, nRow, nLeftClip, nRow);
          stroke(nRoadColour);
          line ( nLeftClip, nRow, nRightClip, nRow);
          stroke(nClipColour);
          line ( nRightClip, nRow, nRightGrass, nRow);
          stroke(nGrassColour);
          line ( nRightGrass, nRow, width, nRow);
        }
      }
    }

    int r=200;
    int g =255;
    int b=255;
    for (int j=0; j<height/2; j++) {
      stroke(r, g, b);
      line(0, j, width, j);
      g--;
    }
    for (int x = 0; x < width; x++)
    {
      int nHillHeight = (int)(abs(sin(x * 0.01 + mc.trackCurve) * 25.0));
      for (int y2 = (height / 2) - nHillHeight; y2 < height / 2; y2++) {
        stroke(255, 255, 0);
        point(x, y2);
      }
    }
  }
  //-------------Bg 3, Hell-----------------

  void background3() {
    for (int y=0; y<height/2; y++) { // same as BG 1
      for (int x=0; x<width; x++) {
        float perspective= width*y/height;
        float fMiddlePoint=width/2+2*width/2 * mc.curvature* pow((1.0f - perspective/width), 3);
        float fRoadWidth=(150+perspective)/2; // /2 since the road is symmertical 
        float fClipWidth=fRoadWidth*0.15;
        float nLeftGrass =(fMiddlePoint-fRoadWidth-fClipWidth);
        float nLeftClip =(fMiddlePoint-fRoadWidth);
        float nRightGrass =(fMiddlePoint+fRoadWidth+fClipWidth);
        float nRightClip =(fMiddlePoint+fRoadWidth);
        int nRow=(height/2)+y;


        OffTrackRight=nRightClip;
        OffTrackLeft=nLeftClip;

        if (sin(20 *  pow(1 - perspective/width, 3) +car.distance * 0.1) > 0.0) {
          nGrassColour=color(105, 0, 0);
        } else {
          nGrassColour=color(255, 50, 0);
        }
        if (-sin(80*pow(1-perspective/width, 2)+car.distance)<0) {
          nClipColour=color(255);
        } else {
          nClipColour=color(245, 5, 0);
        }

        if (mc.trackSection-1==0) {
          nRoadColour=color(255);
        } else {
          nRoadColour=color(95);
        }

        if (x<2) {
          stroke(nGrassColour);
          line (x, nRow, nLeftGrass, nRow);
          stroke(nClipColour);
          line (nLeftGrass, nRow, nLeftClip, nRow);
          stroke(nRoadColour);
          line ( nLeftClip, nRow, nRightClip, nRow);
          stroke(nClipColour);
          line ( nRightClip, nRow, nRightGrass, nRow);
          stroke(nGrassColour);
          line ( nRightGrass, nRow, width, nRow);
        }
      }
    }

    int r=255;
    int g =0;
    int b=200;
    for (int j=0; j<height/2; j++) {
      stroke(r, g, b);
      line(0, j, width, j);
      r--;
      b--;
    }
    for (int x = 0; x < width; x++)
    {
      int nHillHeight = (int)(abs(sin(x * 0.01 + mc.trackCurve) * 50.0));
      for (int y2 = (height / 2) - nHillHeight; y2 < height / 2; y2++) {
        stroke(253, 68, 12);
        point(x, y2);
      }
    }
  }
} // closes object
