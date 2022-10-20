package com.kripton.paceclasses;

public class PopularCourseModel {
    String course_name,instructor_name,id,course_fees,ratings;
    String image_url;

    public String getCourse_name() {
        return course_name;
    }

    public void setCourse_name(String course_name) {
        this.course_name = course_name;
    }

    public String getInstructor_name() {
        return instructor_name;
    }

    public void setInstructor_name(String instructor_name) {
        this.instructor_name = instructor_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCourse_fees() {
        return course_fees;
    }

    public void setCourse_fees(String course_fees) {
        this.course_fees = course_fees;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getRatings() {
        return ratings;
    }

    public void setRatings(String ratings) {
        this.ratings = ratings;
    }


    public PopularCourseModel(String course_name, String instructor_name, String id, String course_fees, String image_url, String ratings) {
        this.course_name = course_name;
        this.instructor_name = instructor_name;
        this.id = id;
        this.course_fees = course_fees;
        this.image_url = image_url;
        this.ratings = ratings;
    }
}
