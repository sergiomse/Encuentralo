package com.sergiomse.encuentralo.model;

import java.util.Date;

/**
 * Created by sergiomse@gmail.com on 24/09/2015.
 */
public class Thing {

    private long id;
    private String imagePath;
    private String tags;
    private String location;
    private Date modifDate;

    public Thing() {
    }

    public Thing(long id, String imagePath, String tags, String location, Date modifDate) {
        this.id = id;
        this.imagePath = imagePath;
        this.tags = tags;
        this.location = location;
        this.modifDate = modifDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getModifDate() {
        return modifDate;
    }

    public void setModifDate(Date modifDate) {
        this.modifDate = modifDate;
    }
}
