import java.io.Serializable;
//Serialization - conversion of the state of an object into a byte stream

public class Status implements Serializable {
    private int ID;
    private int sliderStatus;

    public Status(int id, int sliderStatus) {
        this.ID = id;
        this.sliderStatus = sliderStatus;
    }

    
    /** 
     * @return int
     */
    int getID() {
        return ID;
    }

    
    /** 
     * @return int
     */
    int getSliderStatus() {
        return sliderStatus;
    }

    
    /** 
     * @return String
     */
    public String toString() {
        return "Status [ID=" + ID + ", sliderStatus=" + sliderStatus + "]";
    }

}
