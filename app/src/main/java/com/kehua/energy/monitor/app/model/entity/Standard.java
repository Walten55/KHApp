package com.kehua.energy.monitor.app.model.entity;

public class Standard {

    private int imageResId;

    private String name;

    private int id;

    public Standard( int id, String name,int imageResId) {
        this.imageResId = imageResId;
        this.name = name;
        this.id = id;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
