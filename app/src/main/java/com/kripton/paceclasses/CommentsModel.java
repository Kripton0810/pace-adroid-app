package com.kripton.paceclasses;

public class CommentsModel {
    String image,username,review,dayago,comment;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getDayago() {
        return dayago;
    }

    public void setDayago(String dayago) {
        this.dayago = dayago;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public CommentsModel(String image, String username, String review, String dayago, String comment) {
        this.image = image;
        this.username = username;
        this.review = review;
        this.dayago = dayago;
        this.comment = comment;
    }
    //    String
}
