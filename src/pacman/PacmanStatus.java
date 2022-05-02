import java.io.Serializable;
//Serialization - conversion of the state of an object into a byte stream

public class PacmanStatus implements Serializable {
    private int id;
    private double racePosX; // x position of the racer
    private double racePosY; // x position of the racer
    private int raceROT; // x position of the racer
    // private ImageView aPicView; // a view of the icon ... used to display and move the image

    private int fruitsEaten = 12;
    private boolean isRotRight = false;
    private boolean isRotLeft = false;
    private boolean isMovFwd = false;
    private boolean isMovBck = false;
    private boolean isColided = false;
    private boolean isFire = false;

    public PacmanStatus(int id, double racePosX, double racePosY, int raceROT) {
        this.id = id;
        this.racePosX = racePosX;
        this.racePosY = racePosY;
        this.raceROT = raceROT;
    }

    
    /** 
     * @return int
     */
    int getID() {
        return this.id;
    }

    
    /** 
     * @return double
     */
    double getRacePosX() {
        return this.racePosX;
    }

    
    /** 
     * @return double
     */
    double getRacePosY() {
        return this.racePosY;
    }

    
    /** 
     * @return int
     */
    int getRaceROT(){
        return this.raceROT;
    }

    
    /** 
     * @return String
     */
    public String toString() {
        return "Status [ID=" + id + ", X=" + racePosX+"Y="+ racePosY+ "ROT="+raceROT + "]";
    }

}
