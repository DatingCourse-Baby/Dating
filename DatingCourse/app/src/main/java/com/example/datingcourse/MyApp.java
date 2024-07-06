package com.example.datingcourse;

import android.app.Application;

import java.util.ArrayList;

public class MyApp extends Application {
    private ArrayList<Photo> photos;

    @Override
    public void onCreate() {
        super.onCreate();
        photos = new ArrayList<>();
    }

    public ArrayList<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<Photo> photos) {
        this.photos = photos;
    }
}
