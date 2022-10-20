package com.kripton.paceclasses;

public class CartModel {
    String  id,courseid,cname,price,image;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCourseid() {
        return courseid;
    }

    public void setCourseid(String courseid) {
        this.courseid = courseid;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public CartModel(String id, String courseid, String cname, String price, String image) {
        this.id = id;
        this.courseid = courseid;
        this.cname = cname;
        this.price = price;
        this.image = image;
    }
}
