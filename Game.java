/**
 * Game Class - Primary game logic for a Java-based Processing Game
 * @author Marcus Bistline
 * @author Thomas Dillon
 * @author Taha Khallouf
 * @version 5/29/25
 * Added example for using grid method setAllMarks()
 */

//import processing.sound.*;
import processing.core.PApplet;
import processing.core.PImage;

import java.util.ArrayList;

public class Game extends PApplet{

  //------------------ GAME VARIABLES --------------------//

  // VARIABLES: Processing variable to do Processing things
  PApplet p;

  // VARIABLES: Title Bar
  String titleText = "Alchemist Ascent";
  String extraText = "CurrentLevel?";
  String name = "Undefined";

  // VARIABLES: Whole Game
  AnimatedSprite runningHorse;
  boolean doAnimation;

  // VARIABLES: splashScreen
  Screen splashScreen;
  PImage splashBg;
  String splashBgFile = "images/startScreen.png";
  //SoundFile song;

  // VARIABLES: Scientist
  AnimatedSprite scientist;
  String scientistFile = "sprites/sci.png";
  String scientistJson = "sprites/sci.json";
  int health = 3;

  //VARIABLES: labWorld Pixel-based Platformer
  World labWorld;
  PImage labWorldBg;
  String labWorldBgFile = "images/alchemyScreen.png";
  Platform plat1A;
  Platform plat1B;
  Sprite portal1;

  // VARIABLES: caveWorld
  World caveWorld;
  PImage caveWorldBg;
  String caveWorldBgFile = "images/caveWorld.png";
  Platform plat2A;
  Platform plat2B;
  Sprite portal2;


  // VARIABLES: endScreen
  World endScreen;
  PImage endBg;
  String endBgFile = "images/youwin.png";


  // VARIABLES: Tracking the current Screen being displayed
  Screen currentScreen;
  CycleTimer slowCycleTimer;

  boolean start = true;


  //------------------ REQUIRED PROCESSING METHODS --------------------//

  // Processing method that runs once for screen resolution settings
  public void settings() {
    //SETUP: Match the screen size to the background image size
    size(800,600);  //these will automatically be saved as width & height

    // Allows p variable to be used by other classes to access PApplet methods
    p = this;
    
  }

  //Required Processing method that gets run once
  public void setup() {

    p.imageMode(p.CORNER);    //Set Images to read coordinates at corners
    //fullScreen();   //only use if not using a specfic bg image
    
    //SETUP: Set the title on the title bar
    surface.setTitle(titleText);

    //SETUP: Load BG images used in all screens
    splashBg = p.loadImage(splashBgFile);
    labWorldBg = loadImage(labWorldBgFile);
    caveWorldBg = p.loadImage(caveWorldBgFile);

    endBg = p.loadImage(endBgFile);

    //SETUP: If non-moving, Resize all BG images to exactly match the screen size
    splashBg.resize(p.width, p.height);
    labWorldBg.resize(p.width, p.height);
    caveWorldBg.resize(p.width, p.height);

    endBg.resize(p.width, p.height);   

    //SETUP: Construct each Screen, World, Grid
    splashScreen = new Screen(p, "splash", splashBg);
    labWorld = new World(p,"platformer", labWorldBg);
    caveWorld = new World(p, "cave", caveWorldBg);

    endScreen = new World(p, "end", endBg);
    currentScreen = splashScreen;

    //SETUP: Construct Game objects used in All Screens
    runningHorse = new AnimatedSprite(p, "sprites/horse_run.png", "sprites/horse_run.json", 50.0f, 75.0f, 1.0f);

    //SETUP: Setup scientist stuff
    scientist = new AnimatedSprite(p, scientistFile, scientistJson, 0.0f, 0.0f, 0.5f);
    scientist.resize(50, 50);
    System.out.println("Done loading Scientist stuff...");
    
    // SETUP: Setup more labWorld objects
    plat1A = new Platform(p, PColor.MAGENTA, 0.0f, 0.0f, 200.0f, 20.0f);
    plat1A.setOutlineColor(PColor.BLACK);
    plat1A.stopGravity(); 
    plat1B = new Platform(p, PColor.CYAN, 0.0f, 0.0f, 200.0f, 20.0f);
    plat1B.setOutlineColor(PColor.BLACK);
    plat1B.stopGravity();
    portal1 = new Sprite(p, "images/portal.png",1.0f,200,420);

    labWorld.addSprite(plat1A);   
    labWorld.addSprite(scientist);
    labWorld.addSprite(plat1B);
    labWorld.addSprite(portal1);
    System.out.println("Done loading Level 1 (labWorld)...");

    //SETUP: caveWorld objects
    plat2A = new Platform(p, PColor.GRAY, 0.0f, 0.0f, 100.0f, 10.0f);
    plat2A.setOutlineColor(PColor.BLACK);
    plat2A.stopGravity();
    plat2B = new Platform(p, PColor.GRAY, 0.0f, 0.0f, 100.0f, 10.0f);
    plat2B.setOutlineColor(PColor.BLACK);
    plat2B.stopGravity();
    portal2 = new Sprite(p, "images/portal.png",1.0f,300,520);

    caveWorld.addSprite(plat2A);   
    caveWorld.addSprite(scientist);
    caveWorld.addSprite(plat2B);
    caveWorld.addSprite(portal2);

    System.out.println("Done loading level 2 (caveWorld)...");

    //SETUP: Sound
    // Load a soundfile from the sounds folder of the sketch and play it back
     //song = new SoundFile(p, "sounds/Lenny_Kravitz_Fly_Away.mp3");
     //song.play();
    
    System.out.println("Game started...");

  } //end setup()


  //Required Processing method that automatically loops
  //(Anything drawn on the screen should be called from here)
  public void draw() {

    // DRAW LOOP: Update Screen Visuals
    updateTitleBar();
    updateScreen();

    // DRAW LOOP: Set Timers
    int cycleTime = 1;  //milliseconds
    int slowCycleTime = 300;  //milliseconds
    if(slowCycleTimer == null){
      slowCycleTimer = new CycleTimer(p, slowCycleTime);
    }

    // DRAW LOOP: Populate & Move Sprites
    if(slowCycleTimer.isDone()){
      populateSprites();
      moveSprites();
    }

    checkCollision();

    // DRAW LOOP: Pause Game Cycle
    currentScreen.pause(cycleTime);   // slows down the game cycles

    // DRAW LOOP: Check for end of game
    if(isGameOver()){
      endGame();
    }

  } //end draw()

  //------------------ USER INPUT METHODS --------------------//


  //Known Processing method that automatically will run whenever a key is pressed
  public void keyPressed(){

    //check what key was pressed
    System.out.println("\nKey pressed: " + p.keyCode); //key gives you a character for the key pressed

    //What to do when a key is pressed?

    // if the 'n' key is pressed, ask for their name
    if(p.key == 'n'){
      name = Input.getString("What is your name?");
    }

    // if the 't' key is pressed, then toggle the animation on/off
    if(p.key == 't'){
      //Toggle the animation on & off
      doAnimation = !doAnimation;
      System.out.println("doAnimation: " + doAnimation);
    }

    //-------------------- KEYS FOR GENERAL GAMEPLAY --------------------

    //update this according to which levels are part of the general jumping around
    if(currentScreen == labWorld || currentScreen == caveWorld){
      
      if(p.key == 'q'){
        System.out.println("stopping");
        scientist.stopGravity();
        scientist.setSpeed(0.0f, 0.0f);

      }

      if(p.key == 's'){
        scientist.startGravity();
      }

      if(p.key == 'w'){
        scientist.jump();
      }

      if(p.key == 'a'){
        scientist.setSpeedX(-3f);
      }

      if(p.key == 'd'){
        scientist.setSpeedX(3f);
      }


    }


    //CHANGING SCREENS BASED ON KEYS
    //change to level1 if 1 key pressed, level2 if 2 key is pressed
    if(p.key == '1'){
      currentScreen = labWorld;
      startLabWorld();

    } else if(p.key == '2'){
      currentScreen = caveWorld;
      startCaveWorld();

    } else if(p.key == '3'){
      currentScreen = null;

      //reset the moving Platform every time the Screen is re-displayed
      

    }

  }

  public void startLabWorld(){
      problemBeGone();
      plat1A.moveTo(500.0f, 200.0f);
      plat1A.setSpeed(0,0);
      plat1B.moveTo(200.0f, 400.0f);
      plat1B.setSpeed(0f,0f);
      scientist.moveTo(500f, 100f);
      scientist.setSpeed(0f,0f);
  }

  public void startCaveWorld(){
      problemBeGone();
      plat2A.moveTo(300.0f, 200.0f);
      plat2A.setSpeed(0,0);
      plat2B.moveTo(100.0f, 400.0f);
      plat2B.setSpeed(0f,0f);
      scientist.moveTo(500f, 100f);
      scientist.setSpeed(0f,0f);
  }

  public void keyReleased(){

    if(p.key == 'a'){
      scientist.setSpeedX(0f);
    }

    if(p.key == 'd'){
      scientist.setSpeedX(0f);
    }

  }

  // Known Processing method that automatically will run when a mouse click triggers it
  public void mouseClicked(){
    
    // Print coordinates of mouse click
    System.out.println("\nMouse was clicked at (" + p.mouseX + "," + p.mouseY + ")");

    // Display color of pixel clicked
    int color = p.get(p.mouseX, p.mouseY);
    PColor.printPColor(p, color);
    

  }



  //------------------ CUSTOM  GAME METHODS --------------------//

  //Temporary solution to platfroms staying to long
  //Will need to update it for each object added
  public void problemBeGone(){
      plat1A.moveTo(-200.0f, -200.0f);
      plat1B.moveTo(-200.0f, -200.0f);
      plat2A.moveTo(-200.0f, -200.0f);
      plat2B.moveTo(-200.0f, -200.0f);
  }

  // Updates the title bar of the Game
  public void updateTitleBar(){

    if(!isGameOver()) {

      extraText = currentScreen.getName();

      //set the title each loop
      surface.setTitle(titleText + "\t// CurrentScreen: " + extraText + " \t // Name: " + name + "\t // Health: " + health );

      //adjust the extra text as desired
    
    }
  }

  // Updates what is drawn on the screen each frame
  public void updateScreen(){

    // UPDATE: first lay down the Background
    currentScreen.showBg();

    // UPDATE: splashScreen
    if(currentScreen == splashScreen){

      // Print an s in console when splashscreen is up
      System.out.print("s");

      // Change the screen to level 1 between 3 and 5 seconds
      if(splashScreen.getScreenTime() > 3000 && splashScreen.getScreenTime() < 5000){
        currentScreen = labWorld;
        startLabWorld();
      }
    }


    // UPDATE: labWorld Screen
    if(currentScreen == labWorld){

      // Print a "1" in console when labWorld
      System.out.print("1");

      if(scientist.isOverlapping(portal1)){
        currentScreen = caveWorld;
        startCaveWorld();
        scientist.moveTo(200,100);
      }



    }

    // if(currentScreen == caveWorld){
    //   if(scientist.isOverlapping(portal1)){
    //     startLabWorld();
    //     scientist.moveTo(200,100);
    //   }
    // }

    // UPDATE: End Screen
    // if(currentScreen == endScreen){

    // }

    // UPDATE: Any Screen
    if(doAnimation){
      runningHorse.animateHorizontal(0.5f, 1.0f, true);
    }

    // UPDATE: Other built-in to current World/Grid/HexGrid
    currentScreen.show();

  }

  // Populates enemies or other sprites on the Screen
  public void populateSprites(){

    //What is the index for the last column?
    

    //Loop through all the rows in the last column

      //Generate a random number


      //10% of the time, decide to add an enemy image to a Tile
      

  }

  
  // Moves around the enemies/sprites on the Screen
  public void moveSprites(){

    //Loop through all of the rows & cols in the grid

        //Store the current GridLocation

        //Store the next GridLocation

        //Check if the current tile has an image that is not piece1      


          //Get image/sprite from current location
            

          //CASE 1: Collision with piece1


          //CASE 2: Move enemy over to new location


          //Erase image/sprite from old location

          //System.out.println(loc + " " + grid.hasTileImage(loc));

            
        //CASE 3: Enemy leaves screen at first column

  }



  // Checks if there is a collision between Sprites on the Screen
  public boolean checkCollision(){

    ArrayList<Sprite> colliders = this.labWorld.getColliders(scientist);

    for( Sprite collider : colliders){

      if(collider instanceof Platform
        && scientist.isTouchingTop(collider)
      ){
        // System.out.println("TOP" + scientist);
        scientist.setBottom(collider.getTop());
        scientist.stopGravity();
      } else {
        scientist.startGravity();
      }
    }


    // if(scientist.isTouchingTop(plat)){
    //   System.out.println("TOP" + scientist);
    //   scientist.stopGravity();
    // } else {
    //   scientist.startGravity();
    // }

    if(scientist.isTouchingLeft(plat1A)){
      System.out.println("left");
      scientist.move(-30f,0f);
    }

    if(scientist.isTouchingRight(plat1A)){
      System.out.println("right");
      scientist.move(30f, 0f);
    }

    if(scientist.isTouchingBottom(plat1A)){
      scientist.move(0f,3f);
      scientist.setAccelerationY(0f);
      scientist.setSpeedY(0f);
      scientist.startGravity();
    }


    // if(scientist.isTouchingTop(plat2)){
    //   System.out.println("TOP" + scientist);
    //   scientist.stopGravity();
    // } else {
    //   scientist.startGravity();
    // }

    if(scientist.isTouchingBottom(plat1B)){
      scientist.move(0f,3f);
      scientist.setAccelerationY(0f);
      scientist.setSpeedY(0f);
      scientist.startGravity();
    }



    //Check what image/sprite is stored in the CURRENT location
    // PImage image = grid.getTileImage(loc);
    // AnimatedSprite sprite = grid.getTileSprite(loc);

    //if empty --> no collision

    //Check what image/sprite is stored in the NEXT location

    //if empty --> no collision

    //check if enemy runs into player

      //clear out the enemy if it hits the player (using cleartTileImage() or clearTileSprite() from Grid class)

      //Update status variable

    //check if a player collides into enemy

    return false; //<--default return
  }

  // Indicates when the main game is over
  public boolean isGameOver(){
    
    return false; //by default, the game is never over
  }

  // Describes what happens after the game is over
  public void endGame(){
      System.out.println("Game Over!");

      // Update the title bar

      // Show any end imagery
      currentScreen = endScreen;

  }


} // end of Game class
