import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import SimpleOpenNI.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class simpleOpenNiRecorder extends PApplet {





SimpleOpenNI  context;

boolean recording=false;
int startAt=0;//minute
int duration=60000;//in milliseconds / 30 seconds
int start=0;
int currentTime=0;

int[] recordTime = {0,10,15,20,25,30,35,40,45,50,55};

public void setup()
{
  
  startAt=minute()+4;

  context = new SimpleOpenNI(this);

    // recording
    // enable depthMap generation 
    if (context.enableDepth() == false)
    {
      println("Can't open the depthMap, maybe the camera is not connected!"); 
      exit();
      return;
    }

    // enable ir generation
    if (context.enableRGB() == false)
    {
      println("Can't open the rgbMap, maybe the camera is not connected or there is no rgbSensor!"); 
      exit();
      return;
    }


    // set window size 
  if ((context.nodes() & SimpleOpenNI.NODE_DEPTH) != 0)
  {
    if ((context.nodes() & SimpleOpenNI.NODE_IMAGE) != 0)
      // depth + rgb 
      size(context.depthWidth() + 10 +  context.rgbWidth(), 
      context.depthHeight() > context.rgbHeight()? context.depthHeight():context.rgbHeight());   
    else
      // only depth
      size(context.depthWidth(), context.depthHeight());
  }
  else 
    exit();


  if(recording==true){
    setupRecording();
  }
  textFont(createFont("Arial",48));
}

public void draw()
{
  // update
 for(int i=0;i<11;i++){
  if(recording==false && minute()==recordTime[i] && second()==0)//startAt
{
setupRecording();
break;
}
 }
  
  context.update(); 
  if(recording==true){
  background(200, 0, 0);
  currentTime=millis();
  if(currentTime-start>=duration){
    exit();
    recording=false;
  }
  
  }else{
  background(0,200, 0);
   }   


  // draw the cam data
  if ((context.nodes() & SimpleOpenNI.NODE_DEPTH) != 0)
  {
    if ((context.nodes() & SimpleOpenNI.NODE_IMAGE) != 0)
    {
      image(context.depthImage(), 0, 0);   
      image(context.rgbImage(), context.depthWidth() + 10, 0);
    }
    else
      image(context.depthImage(), 0, 0);
  }

  if ((context.nodes() & SimpleOpenNI.NODE_SCENE) != 0)  
    image(context.sceneImage(), 0, 0, context.sceneWidth()*.4f, context.sceneHeight()*.4f);
    


  if(recording==false){
   color(255,0,0);
    for(int i=0;i<11;i++){
  if(recordTime[i]>minute())//startAt
{
  startAt=recordTime[i];
  break;
}
    }
   
   
   text("starting to record at:"+hour()+":"+startAt+":00",20,100);
  }    
  
}


public void keyPressed()
{
  switch(keyCode)
  {
    case UP:
        startAt++;
        if(startAt>=60){
        startAt=0;
        }
    break;
  case DOWN:
        startAt--;
    break;
    
    /*
   case RIGHT:
        startAt++;
        if(startAt>=60){
        startAt=0;
        }
    break;
  case LEFT:
        startAt--;
    break;
    */
    
  }
}

public void setupRecording()
{
    String[] time=new String[2];
  
  time[0]=""+hour()+":"+minute()+":"+second();
  time[1]=""+day()+"/"+month()+"/"+year();
  

  saveStrings("data/time"+time[0]+".txt", time);
  

    // setup the recording 
    context.enableRecorder(SimpleOpenNI.RECORD_MEDIUM_FILE, "record"+time[0]+".oni");

    // select the recording channels
    context.addNodeToRecording(SimpleOpenNI.NODE_DEPTH, 
    SimpleOpenNI.CODEC_16Z_EMB_TABLES);
    context.addNodeToRecording(SimpleOpenNI.NODE_IMAGE, 
    SimpleOpenNI.CODEC_JPEG);

recording=true;

start=millis();

}

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--full-screen", "--bgcolor=#666666", "--hide-stop", "simpleOpenNiRecorder" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
