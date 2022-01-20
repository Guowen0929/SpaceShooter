/*
  SpaceShooter Class
  Desc: The game will be focusing on a rectangular box format, the player will control a spaceship and  the asteroids will try to destroy the player spaceship. The player spaceship will be located on the bottom of the rectangle, and asteroids will be coming out of random spaces on the border sides of the rectangle. Your goal is to try to destroy asteroids as many as possible to score points.
  Author: Gordon Zhang and Bowen Yin
 Grade: ICS3U
 Version 2.0 January 16th
*/
 
import java.awt.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.util.Arrays;
// the following imports are needed for music and sound
import javax.sound.sampled.*;
 
public class SpaceShooter {
  //Window size
 static final int WIDTH = 480;
 static final int HEIGHT = 600;
 
 
 
 // key listener
 static MyKeyListener keyListener = new MyKeyListener();
  static boolean shoot = false;
  // Game Window properties
 static JFrame gameWindow;
 static GraphicsPanel canvas;
 static BufferedImage picture;
 static BufferedImage picture2;
 static BufferedImage shipPicture;
 static BufferedImage asteriodPicture;
 static BufferedImage explodePicture;
 static BufferedImage gameOverPicture;
 
 static int pictureX = 0;
 static int pictureY = 0;
 static int leftShipLimit = 10;
 static int rightShipLimit = 420;
 
 static boolean inMenu = true;
 static boolean notStart = true;
 static boolean inGameNotOver = true;
 
 // score properties
 static int score=0;
 static int scoreX=10;
 static int scoreY=30;
 static final int SCORE_ADDTION = 10;
 static int largeSize;
 static Font smallFont;
  // level properties
 static int level = 1;
 static int levelX=350;
 static int levelY=30;
 
 // ship properties
 static final int GROUND = 550;
 static final int RUN_SPEED = 10;
 static int shipW = 50;
 static int shipH = 50;
 static int shipX = WIDTH/2;
 static int shipY = GROUND - shipH;
 static int shipVx = 0;
 static Rectangle shipBox = new Rectangle(shipX, shipY, shipW, shipH);
  //Random Coordinates
 static int randomAsteriodX;
 static int randomAsteriodY;
 
 // asteriod properties
 static int asteriodW = 43;
 static int asteriodH = 43;
 static int numAsteriods = 10;  
 static int asteriodSpeed = 1;
 static int[] asteriodX = new int[numAsteriods];
 static int[] asteriodY = new int[numAsteriods];
 static boolean[] asteriodVisible = new boolean[numAsteriods];
 static boolean[] notHitAsteriods = new boolean[numAsteriods];
 static Rectangle asteriodBox;
  // bullets properties
 static int numBullets = 50;   
 static int[] bulletX = new int[numBullets];
 static int[] bulletY = new int[numBullets];
 static boolean[] bulletVisible = new boolean[numBullets];
 static int bulletW = 6;
 static int bulletH = 10;
 static int bulletSpeed = -10;
 static int currentBullet = 0;
 static Rectangle bulletBox;
 
 // music properties
 static AudioInputStream audioStream;
 static Clip shootingSound;
 static Clip getReadyMusic;
 static Clip backgroundMusic;
 static Clip menuMusic;
 static Clip explodeSound;
 static Clip gameOverSound;
 public static void main(String[] args){
  
   gameWindow = new JFrame("Space Shooter");
   gameWindow.setSize(WIDTH,HEIGHT);
   gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   gameScreen();
   imagePicture();
   menuMusic();
   gameOverSound();
  
   // generate bullets
   Arrays.fill(bulletX,0);
   Arrays.fill(bulletY,0);
   Arrays.fill(bulletVisible,false);
  
 
   Arrays.fill(asteriodVisible,true);
   Arrays.fill(notHitAsteriods,true);
  
 
   randomAsteriodCoordinates();
 
   gameWindow.setLocationRelativeTo(null);
   gameWindow.setVisible(true);
   runGameLoop();
 
  
 } // main method end
 
 public static void randomAsteriodCoordinates(){
   // generate asteriods
   for (int i=0; i<10; i++){
     randomAsteriodX = (int)(450*Math.random());
     asteriodX[i]=randomAsteriodX;
     randomAsteriodY = (int)(-600*Math.random());
     asteriodY[i]=randomAsteriodY;
   }
 }
  public static void runGameLoop(){
   shootingSound();
   while (true) {
       gameWindow.repaint();
       try  {Thread.sleep(20);} catch(Exception e){}
      
       // prevent the ship to be outside of the screen
       if (shipX<leftShipLimit){
         shipX+=RUN_SPEED;
       }
       else if (shipX>rightShipLimit){
         shipX-=RUN_SPEED;
       }
      
       if (inGameNotOver==false){
         gameOverSound.start();
         backgroundMusic.stop();
       }
      
       if (shoot == true){
         // assign the coordinates of the top middle point of the ship to the current bullet
         bulletX[currentBullet] = shipX + shipW/2 - bulletW/2;
         bulletY[currentBullet] = shipY;
         bulletVisible[currentBullet] = true;
         currentBullet = (currentBullet + 1)%numBullets;
         shootingSound.start();
         shootingSound.loop(Clip.LOOP_CONTINUOUSLY);
       }
       else{
         shootingSound.stop();
       }
      
       // level
       level = (int)(score/100)+1;
       asteriodSpeed = level;
 
 
       // move the ship
       shipX = shipX + shipVx;
      
       // loop for asteriod going down
 
      // move the bullets
       for (int i=0; i<numBullets; i++){
           if (bulletVisible[i]){
               bulletY[i] = bulletY[i] + bulletSpeed;
               if (bulletY[i]<0)
                   bulletVisible[i] = false;
           }
       }
 
       // move the asteriod
       for (int i=0; i<numAsteriods; i++){
         if (asteriodVisible[i]){
             asteriodY[i] = asteriodY[i] + asteriodSpeed;
             if (asteriodY[i]>600)
                 asteriodVisible[i] = false;              
         }
         else{
           asteriodY[i]=asteriodY[i]%600;
           randomAsteriodX = (int)(450*Math.random());
           asteriodX[i]=randomAsteriodX;
           asteriodVisible[i] = true;
         }
      
       }
      
       shipBox.setLocation(shipX,shipY);
       for (int i=0; i<numBullets; i++){
         bulletBox = new Rectangle(bulletX[i], bulletY[i], bulletW, bulletH);
         // check for collision
         for (int z=0; z<numAsteriods; z++){
           asteriodBox = new Rectangle(asteriodX[z], asteriodY[z], asteriodW, asteriodH);
           if (asteriodBox.intersects(bulletBox) && asteriodY[z]<600 && bulletVisible[i] && notHitAsteriods[z]) {
             if (inGameNotOver){
               score+=SCORE_ADDTION;
               explodeSound();
             }
             notHitAsteriods[z]=false;
             bulletVisible[i]=false;
            
           }
           else if (asteriodBox.intersects(shipBox) && notHitAsteriods[z]){
             inGameNotOver = false;
           }
           else if (asteriodY[z]>600){
             notHitAsteriods[z]=true;
           }
         }
    
       }
      
      
   }
 } // runGameLoop method end
 
 public static void gameScreen(){
  
   canvas = new GraphicsPanel();
   canvas.addKeyListener(keyListener);
   gameWindow.add(canvas);
   
 }
 
  public static void imagePicture(){
   try {               
     picture = ImageIO.read(new File("assets/main.png"));
   } catch (IOException ex){}
  
   try {               
     picture2 = ImageIO.read(new File("assets/starfield.png"));
   } catch (IOException ex){}
  
   try {               
     shipPicture = ImageIO.read(new File("assets/playerShip1_orange.png"));
   } catch (IOException ex){}
 
   try {               
     asteriodPicture = ImageIO.read(new File("assets/meteorBrown_med1.png"));
   } catch (IOException ex){}
  
   try {               
     explodePicture = ImageIO.read(new File("assets/regularExplosion04.png"));
   } catch (IOException ex){}
  
   try {               
     gameOverPicture = ImageIO.read(new File("assets/SpaceShooterGameOver.png"));
   } catch (IOException ex){}
 
 }
  public static void shootingSound(){
   // load and play the sound
   try {
     File audioFile = new File("sounds/pew.wav");
     audioStream = AudioSystem.getAudioInputStream(audioFile);
     shootingSound = AudioSystem.getClip();
     shootingSound.open(audioStream);
   } catch (Exception ex){}
 
 }
 
 public static void explodeSound(){
   // load and play the sound
   try {
     File audioFile = new File("sounds/expl6.wav");
     audioStream = AudioSystem.getAudioInputStream(audioFile);
     explodeSound = AudioSystem.getClip();
     explodeSound.open(audioStream);
   } catch (Exception ex){}
   explodeSound.start();
 
 }
 
 public static void gameOverSound(){
   // load and play the sound
   try {
     File audioFile = new File("sounds/GameOverVoiceSoundEffect.wav");
     audioStream = AudioSystem.getAudioInputStream(audioFile);
     gameOverSound = AudioSystem.getClip();
     gameOverSound.open(audioStream);
   } catch (Exception ex){}
  
 
 }
 
 public static void getReadyMusic(){
   // load and play the music
   try {
     File audioFile = new File("sounds/getready.wav");
     audioStream = AudioSystem.getAudioInputStream(audioFile);
     getReadyMusic = AudioSystem.getClip();
     getReadyMusic.open(audioStream);
   } catch (Exception ex){}
   getReadyMusic.start();
 
 }
 
 public static void menuMusic(){
   // load and play the music
   try {
     File audioFile = new File("sounds/menu.wav");
     audioStream = AudioSystem.getAudioInputStream(audioFile);
     menuMusic = AudioSystem.getClip();
     menuMusic.open(audioStream);
   } catch (Exception ex){}
   menuMusic.start();
  
 }
  public static void backgroundMusic(){
   // load and play the music
   try {
     File audioFile = new File("sounds/SkyFire (Title Screen).wav");
     audioStream = AudioSystem.getAudioInputStream(audioFile);
     backgroundMusic = AudioSystem.getClip();
     backgroundMusic.open(audioStream);
   } catch (Exception ex){}
   backgroundMusic.start();
   backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
 
 }
 
 static class GraphicsPanel extends JPanel{
   public GraphicsPanel(){
       setFocusable(true);
       requestFocusInWindow();
   }
   public void paintComponent(Graphics g){
       super.paintComponent(g); //required
      
   // draw the picture ("this" refers to the graphics panel)  
       if (inMenu){
         g.drawImage(picture,pictureX,pictureY,this);
         repaint();
       }
       else if (inGameNotOver){
         g.drawImage(picture2,pictureX,pictureY,this);
         repaint();
        
         // draw the score
         g.setColor(Color.white);
         largeSize = 24;
         smallFont = new Font("Arial", Font.BOLD, largeSize);
         g.setFont(smallFont);
         g.drawString("Score: " + score, scoreX , scoreY);
 
         // draw the level
         g.setColor(Color.white);
         largeSize = 24;
         smallFont = new Font("Arial", Font.BOLD, largeSize);
         g.setFont(smallFont);
         g.drawString("Level: " + level, levelX , levelY);
 
 
         // draw the ship   
         g.drawImage(shipPicture,shipX,shipY,this);
         repaint();
        
         // draw ten asteriods
         for (int i=0; i<numAsteriods; i++){
           if (notHitAsteriods[i]){
             if (asteriodVisible[i]){
               g.drawImage(asteriodPicture,asteriodX[i],asteriodY[i],this);
               repaint();
             }
           }
          
           else{
             repaint();
          
           }
          
         }
         
         // draw the bullets
         g.setColor(Color.red);
         for (int i=0; i<numBullets; i++){
             if (bulletVisible[i])
                 g.fillOval(bulletX[i],bulletY[i],bulletW,bulletH);
         }
       }
      
       // Game is over
       else{
         g.drawImage(gameOverPicture,pictureX,pictureY,this);
 
         //Print final score
         g.setColor(Color.white);
         largeSize = 24;
         smallFont = new Font("Arial", Font.BOLD, largeSize);
         g.setFont(smallFont);
         g.drawString("Your Final Score Is : " + score, 100, 420);
         repaint();
        
         //Print final level
         g.setColor(Color.white);
         largeSize = 24;
         smallFont = new Font("Arial", Font.BOLD, largeSize);
         g.setFont(smallFont);
         g.drawString("Your Final Level Is: " + level, 100 , 450);
       }
 
   } // paintComponent method end
 } // GraphicsPanel class end
 
 
 static class MyKeyListener implements KeyListener{  
   public void keyPressed(KeyEvent e){
     int key = e.getKeyCode();
     if (key == KeyEvent.VK_Q){
         System.exit(0);
     }
     else if (key==KeyEvent.VK_ENTER){
       inMenu=false;
       if (notStart){
         getReadyMusic();
         backgroundMusic();
         menuMusic.stop();
       }   
       notStart=false;
     }
     else if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A){
         shipVx = -RUN_SPEED;
     }
     else if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D){
         shipVx = RUN_SPEED; 
     }
     else if (key == KeyEvent.VK_SPACE){
       shoot = true;
     }
     else{
       shipVx = 0;
     } 
 
   }
   public void keyReleased(KeyEvent e){
       int key = e.getKeyCode();
       if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A){
         shipVx = 0;
       }
       else if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D){
         shipVx = 0; 
       }
       else if (key == KeyEvent.VK_SPACE){
         shoot = false;
       }
      
   }  
   public void keyTyped(KeyEvent e){
       char keyChar = e.getKeyChar();
 
   }          
 } // MyKeyListener class end  
 
}
 

