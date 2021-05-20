package com.lithium.easyclean.mainPackage.start;
import java.io.Serializable;

public class Toilet implements Serializable {
    private String cleaner;
    private int turbidity;
    private String id = null;
    private String name;

    public Toilet(){

    }
    public Toilet(String id, String name, String cleaner, int turbidity){
        this.id = id;
        this.name = name;
        this.cleaner = cleaner;
        this.turbidity = turbidity;
    }
    public Toilet(String id, String name, int turbidity){
        this.id = id;
        this.name = name;
        this.turbidity = turbidity;
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

    public String getName() {
        return name;
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


