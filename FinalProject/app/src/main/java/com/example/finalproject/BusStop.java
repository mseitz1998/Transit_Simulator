package com.example.finalproject;

import java.io.Serializable;
import java.util.ArrayList;

public class BusStop implements Serializable {
    String id; //id of bus stop

    BusStop(String idIn){//constructor
        id = idIn;
    }

    //toString method for stop
    public String toString(){
        return id;
    }

}
