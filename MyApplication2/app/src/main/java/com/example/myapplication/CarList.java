package com.example.myapplication;

public class CarList {
    private String auto_id, auto_name, auto_number, auto_gaz, auto_status, auto_price, auto_stayStreet, auto_image, auto_imageBIG;
    private float latitude, longitude;

    public String getAuto_id(){return auto_id;}
    public void setAuto_id(String auto_id){this.auto_id = auto_id;}
    public String getAuto_name(){return auto_name;}
    public void setAuto_name(String auto_name){this.auto_name = auto_name;}
    public String getAuto_number(){return auto_number;}
    public void setAuto_number(String auto_number){this.auto_number = auto_number;}
    public String getAuto_gaz(){return auto_gaz;}
    public void setAuto_gaz(String auto_gaz){this.auto_gaz = auto_gaz;}
    public String getAuto_status(){return auto_status;}
    public void setAuto_status(String auto_status){this.auto_status = auto_status;}
    public String getAuto_price(){return auto_price;}
    public void setAuto_price(String auto_price){this.auto_price = auto_price;}
    public String getAuto_stayStreet(){return auto_stayStreet;}
    public void setAuto_stayStreet(String auto_stayStreet){this.auto_stayStreet = auto_stayStreet;}
    public String getAuto_image(){return auto_image;}
    public void setAuto_image(String auto_imageBIG){this.auto_image = auto_image;}
    public String getAuto_imageBIG(){return auto_image;}
    public void setAuto_imageBIG(String auto_imageBIG){this.auto_imageBIG = auto_imageBIG;}
    public float getLatitude(){return latitude;}
    public void setLatitude(float latitude){this.latitude = latitude;}
    public float getLongitude(){return longitude;}
    public void setLongitude(float longitude){this.longitude = longitude;}
    public CarList(String auto_id,
                   String auto_name,
                   String auto_number,
                   String auto_gaz,
                   String auto_status,
                   String auto_price,
                   String auto_stayStreet,
                   String auto_image,
                   String auto_imageBIG,
                   float latitude,
                   float longitude)
    {
        this.auto_id = auto_id;
        this.auto_name = auto_name;
        this.auto_number = auto_number;
        this.auto_gaz = auto_gaz;
        this.auto_status = auto_status;
        this.auto_price = auto_price;
        this.auto_stayStreet = auto_stayStreet;
        this.auto_image = auto_image;
        this.auto_imageBIG = auto_imageBIG;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
