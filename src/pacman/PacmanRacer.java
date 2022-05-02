import javafx.application.*;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.control.*;
import javafx.scene.control.Alert.*;
import javafx.scene.text.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.*;
import javafx.geometry.*;
import javafx.animation.*;
import java.io.*;
import java.util.*;



import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Racer creates the race lane (Pane) and the ability to
 * keep itself going (Runnable)
 */
class PacmanRacer extends Pane {
   private double racePosX = 540; // x position of the racer
   private double racePosY = 100; // x position of the racer
   private int raceROT = 90; // x position of the racer
   private ImageView aPicView; // a view of the icon ... used to display and move the image
   private int id;

   private double velocity = 2.5;

   private final static String ICON_IMAGE = "pac1.png";

   private int fruitsEaten = 0;
   private boolean isRotRight = false;
   private boolean isRotLeft = false;
   private boolean isMovFwd = false;
   private boolean isMovBck = false;
   private boolean isColided = false;
   private boolean isFire = false;
   private boolean isMoving = false;
   private boolean isAlive = true;
   private Image carImage = null;

   private Scene scene;

   PixelReader pix = null;

   public void init() {
      scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
         @Override
         public void handle(KeyEvent event) {
            if (event.getCode() == KeyCode.D) {
               isRotRight = false;
               isMoving = false;
            }
            if (event.getCode() == KeyCode.A) {
               isRotLeft = false;
               isMoving = false;
            }
            if (event.getCode() == KeyCode.W) {
               if(id==0){
                  try {
                     aPicView.setImage(new Image( new FileInputStream(ICON_IMAGE)));
                  } catch (FileNotFoundException e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                  }
               } else if(id==1){
                  try {
                     aPicView.setImage(new Image( new FileInputStream("pac2.png")));
                  } catch (FileNotFoundException e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                  }
               }
               isMovFwd = false;
               isMoving = false;
            }
            if (event.getCode() == KeyCode.S) {
               if(id==0){
                  try {
                     aPicView.setImage(new Image( new FileInputStream(ICON_IMAGE)));
                  } catch (FileNotFoundException e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                  }
               } else if (id==0){
                  try {
                     aPicView.setImage(new Image( new FileInputStream("pac2.png")));
                  } catch (FileNotFoundException e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                  }
               }
               isMovBck = false;
               isMoving = false;
            }
            if (event.getCode() == KeyCode.SPACE) {
               isFire = false;
            }
         }
      });

      scene.setOnKeyPressed(new EventHandler<KeyEvent>() {

         @Override
         public void handle(KeyEvent event) {

            if (event.getCode() == KeyCode.D) {
               isRotRight = true;
               isMoving = true;
            }

            if (event.getCode() == KeyCode.A) {
               isRotLeft = true;
               isMoving = true;
            }

            if (event.getCode() == KeyCode.W) {
               if(id==0){
                  try {
                     aPicView.setImage(new Image (new FileInputStream ("pac1.gif")));
                  } catch (FileNotFoundException e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                  }
               } else if (id==1){
                  try {
                     aPicView.setImage(new Image (new FileInputStream ("pac2.gif")));
                  } catch (FileNotFoundException e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                  }
               }
               isMovFwd = true;
               isMoving = true;
            }

            if (event.getCode() == KeyCode.S) {
               if(id==0){
                  try {
                     aPicView.setImage(new Image( new FileInputStream("pac1.gif")));
                  } catch (FileNotFoundException e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                  } 
               } else if (id==1){
                  try {
                     aPicView.setImage(new Image( new FileInputStream("pac2.gif")));
                  } catch (FileNotFoundException e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                  } 
               }
               isMovBck = true;
               isMoving = true;
            }

            if (event.getCode() == KeyCode.SPACE  && fruitsEaten>=12) {
               isFire = true;
            }

            

         }

      });
   }

   public PacmanRacer(Scene scene, Image carImage, PixelReader pix) {
      // Draw the icon for the racer
      this.scene = scene;
      if (scene == null) {
         System.exit(0);
      }
      this.carImage = carImage;
      this.pix = pix;
      aPicView = new ImageView(carImage);
      this.getChildren().add(aPicView);
      aPicView.setTranslateY(racePosY);
      aPicView.setTranslateX(racePosX);

   }

   /**
    * update() method keeps the thread (racer) alive and moving.
    * 
    *
    */
   public void update() {

      if (isRotRight) {
         this.raceROT += 3*(velocity/1.4);
         this.aPicView.setRotate(raceROT);
      }
      if (isRotLeft) {
         this.raceROT -= 3*(velocity/1.4);
         this.aPicView.setRotate(raceROT);
      }
      double oldY = this.racePosY; 
      double oldX = this.racePosX;
      
      if (isMovFwd) {
         this.aPicView.setRotate(raceROT);
         this.racePosY -= -1 * (Math.sin(Math.toRadians(raceROT))) * (velocity);
         this.racePosX += (Math.cos(Math.toRadians(raceROT))) * (velocity);
      }
      if (isMovBck) {
         this.aPicView.setRotate(raceROT);
         this.racePosY += -1 * (Math.sin(Math.toRadians(raceROT))) * (velocity / 2);
         this.racePosX -= (Math.cos(Math.toRadians(raceROT))) * (velocity / 2);
      }

      if (!checkCollision((int)racePosX, (int)racePosY)){
         this.aPicView.setTranslateY(racePosY);
         this.aPicView.setTranslateX(racePosX);
      } else {
         this.racePosX = oldX;
         this.racePosY = oldY;
      }
   } // end update()

   /**update other players */
   public void updateOther(){
      
   }


   
   /** 
    * @param x
    * @param y
    * @return boolean
    */
   private boolean checkCollision(int x, int y){
      for (int i = x; i < (x + this.carImage.getHeight()); i++) {
         for (int j = y; j < (y + this.carImage.getWidth() ); j++) {
            Color color = pix.getColor(i, j);
            if(color.getRed()<0.5){
               return true;
            }
         }
      }
      return false;
   }

   
   /** 
    * @return ImageView
    */
   public ImageView getImage(){
      return this.aPicView;
   }
   
   /** 
    * @param newImage
    */
   public void changeImage(String newImage){
      this.aPicView = new ImageView(new Image(newImage));
   }
   
   /** 
    * @return int
    */
   public int getRot(){
      return this.raceROT;
   }
   
   /** 
    * @return boolean
    */
   public boolean isFire(){
      return this.isFire;
   }
   
   /** 
    * @return boolean
    */
   public boolean isMoving(){
      return this.isMoving;
   }
   
   /** 
    * @param fruitsEaten
    */
   public void setFruitsEaten(int fruitsEaten){
      this.fruitsEaten = fruitsEaten;
   }
   
   /** 
    * @return int
    */
   public int getFruitsEaten(){
      return this.fruitsEaten;
   }
   public void restart(){
      this.fruitsEaten = 0;
      this.raceROT =90;
      this.racePosX = 540;
      this.racePosY = 100;
   }
   public void incVelocity(){
      this.velocity+=0.5;
   }
   
   /** 
    * @param id
    */
   public void setID(int id){
      this.id = id;
   }
   public void die(){
      this.isAlive = false;
   }
   
   /** 
    * @return boolean
    */
   public boolean isAlive(){
      return this.isAlive;
   }

} // end inner class Racer
