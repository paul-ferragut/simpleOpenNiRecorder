
import SimpleOpenNI.*;


SimpleOpenNI  context;

boolean recording=false;
int startAt=30;//minute
int duration=30000;//in milliseconds / 30 seconds
int start=0;
int currentTime=0;

void setup()
{
  
  

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

void draw()
{
  // update
  
  if(recording==false && minute()==startAt && second()==0)
{
setupRecording();
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
    image(context.sceneImage(), 0, 0, context.sceneWidth()*.4, context.sceneHeight()*.4);
    


  if(recording==false){
   color(255,0,0);
   text("starting to record at:"+hour()+":"+startAt+":00",20,20);
  }    
  
}


void keyPressed()
{
  switch(key)
  {
    
  }
}

void setupRecording()
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

