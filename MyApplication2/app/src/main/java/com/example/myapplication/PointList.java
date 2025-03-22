package com.example.myapplication;

public class PointList {
    String auto_id;
    private float latitude, longitude;

    public String getAuto_id()
    {
        return auto_id;
    }
    public void setAuto_id(String auto_id)
    {
        this.auto_id = auto_id;
    }

    public float getLatitude()
    {
        return latitude;
    }
    public void setLatitude(float latitude)
    {
        this.latitude = latitude;
    }

    public float getLongitude()
    {
        return longitude;
    }
    public void setLongitude(float longitude)
    {
        this.longitude = longitude;
    }

    public PointList(String auto_id,
                   float latitude,
                   float longitude)
    {
        this.auto_id = auto_id;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
