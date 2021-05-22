package com.lithium.easyclean.mainPackage.start;
import java.io.Serializable;

public class Toilet implements Serializable {
    private String cleaner;
    private int turbidity;
    private String id = null;
    private String location = null;

    public Toilet(){

    }

    public Toilet(String id, String location, String cleaner, int turbidity){
        setId(id);
        setLocation(location);
        setCleaner(cleaner);
        setTurbidity(turbidity);
    }


    public int getTurbidity() {
        return turbidity;
    }

    public String getCleaner() {
        return cleaner;
    }

    public String getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setCleaner(String cleaner) {
        this.cleaner = cleaner;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTurbidity(int turbidity) {
        this.turbidity = turbidity;
    }
}


