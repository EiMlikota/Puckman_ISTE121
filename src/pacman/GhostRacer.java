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

//import com.sun.glass.events.KeyEvent;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Racer creates the race lane (Pane) and the ability to
 * keep itself going (Runnable)
 */
class GhostRacer extends Pane {
   private double racePosX=740; // x position of the racer
   private double racePosY=360; // x position of the racer
   private int raceROT = 0; // x position of the racer
   private ImageView aPicView; // a view of the icon ... used to display and move the image

   private double velocity = 2;

   private boolean isRotRight = false;
   private boolean isRotLeft = false;
   private boolean isMovFwd = false;
   private boolean isMovBck = false;
   private boolean isColided = false;
   private boolean isAlive = true;

   private Image carImage = null;

   private Scene scene;

   PixelReader pix = null;
   /*
   public void randomizePosition(){
      racePosX = Math.random();
      racePosY = Math.random();
      if(checkCollision((int)racePosX,(int)racePosY)){
         randomizePosition();
      }
   }
   public void init() {
      randomizePosition();
      
   }
   */

   public GhostRacer(Scene scene, Image carImage, PixelReader pix) {
      // Draw the icon for the racer
      this.scene = scene;
      if (scene == null) {
         System.exit(0);
      }
      this.carImage = carImage;
      this.pix = pix;
      this.raceROT += Math.random()*360;
      aPicView = new ImageView(carImage);
      this.getChildren().add(aPicView);
      aPicView.setTranslateY(racePosY);
      aPicView.setTranslateX(racePosX);
   }

   /**
    * update() method keeps the thread (racer) alive and moving.
    * 
    * @param EventHandler
    */
   public void update() {
      

      // int x = (int) aPicView.getTranslateX();
      // int y = (int) aPicView.getTranslateY();
      // isColided = checkCollision(x, y);
      // System.out.println(isColided);

      double oldY = racePosY;
      double oldX = racePosX;

      racePosY -= (-1 * (Math.sin(Math.toRadians(raceROT))) * (velocity))*0.5;
      racePosX += ((Math.cos(Math.toRadians(raceROT))) * (velocity))*0.5;

      if (!checkCollision((int) racePosX, (int) racePosY)) {
         aPicView.setTranslateY(racePosY);
         aPicView.setTranslateX(racePosX);
      } else {
         racePosX = oldX;
         racePosY = oldY;
         raceROT += Math.random()*360;
      }
   } // end update()

   
   /** 
    * @return ImageView
    */
   public ImageView getImage(){
      return this.aPicView;
   }

   public void scare(){
      try {
         this.aPicView.setImage(new Image(new FileInputStream("ghost_scared.gif")));
      } catch (FileNotFoundException e) {

         e.printStackTrace();
      }
   }

   
   /** 
    * @return boolean
    */
   public boolean isAlive(){
      return this.isAlive;
   }
   public void die(){
      this.isAlive = false;
   }

   
   /** 
    * @param x
    * @param y
    * @return boolean
    */
   private boolean checkCollision(int x, int y) {
      for (int i = x; i < (x + this.carImage.getHeight()); i++) {
         for (int j = y; j < (y + this.carImage.getWidth()); j++) {
            Color color = pix.getColor(i, j);
            if (color.getRed() < 0.5) {
               return true;
            }
         }
      }
      return false;
   }

   
   /** 
    * @param x
    * @param y
    */
   public void restart(int x, int y){
      racePosX = x;
      racePosY = y;
      isAlive = true;
   }
   public void incVelocity(){
      this.velocity+=0.5;
   }
   
   /** 
    * @param x
    * @param y
    */
   public void setRacePos(double x, double y){
      this.racePosX = x;
      this.racePosY = y;
   }

} // end inner class Racer
