import java.io.Serializable;
//Serialization - conversion of the state of an object into a byte stream

public class DotStatus implements Serializable {
    private int ID;
    private double x;
    private double y;
    private int raceROT;

    public DotStatus(int id, double x, double y, int raceROT) {
        this.ID = id;
        this.x = x;
        this.y = y;
        this.raceROT =raceROT;
    }

    
    /** 
     * @return int
     */
    int getID() {
        return this.ID;
    }

    
    /** 
     * @return double
     */
    double getX() {
        return this.x;
    }

    
    /** 
     * @return double
     */
    double getY() {
        return y;
    }
    
    /** 
     * @return int
     */
    int getRot(){
        return this.raceROT;
    }

    
    /** 
     * @return String
     */
    public String toString() {
        return "Status [ID=" + ID + ", X= " + x + "Y= "+y+"]";
    }

}
