package com.getrealtimedata;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by admin on 12/5/2017.
 */
@IgnoreExtraProperties
public class Customer {

    public Double lat;
    public Double log;
    public Float bearing;
    public Integer speed;


    public Customer() {
    }

    public Customer(Double lat, Double log, Float bearing, Integer speed) {
        this.lat = lat;
        this.log = log;
        this.bearing = bearing;
        this.speed = speed;
    }
}
