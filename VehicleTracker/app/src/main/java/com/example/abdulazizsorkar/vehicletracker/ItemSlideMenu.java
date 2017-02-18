package com.example.abdulazizsorkar.vehicletracker;

/**
 * Created by Abdul Aziz Sorkar on 1/8/2016.
 */ 
public class ItemSlideMenu {

    private int imgId;
    private String title;

    public ItemSlideMenu(int ic_launcher, String android) {
        this.imgId=ic_launcher;
        this.title=android;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
