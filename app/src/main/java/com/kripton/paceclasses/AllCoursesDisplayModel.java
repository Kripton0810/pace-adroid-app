package com.kripton.paceclasses;

import java.util.List;

public class AllCoursesDisplayModel {
    String course_name_big;
    List<PopularCourseModel> modelList;

    public String getCourse_name_big() {
        return course_name_big;
    }

    public void setCourse_name_big(String course_name_big) {
        this.course_name_big = course_name_big;
    }

    public List<PopularCourseModel> getModelList() {
        return modelList;
    }

    public void setModelList(List<PopularCourseModel> modelList) {
        this.modelList = modelList;
    }

    public AllCoursesDisplayModel(String course_name_big, List<PopularCourseModel> modelList) {
        this.course_name_big = course_name_big;
        this.modelList = modelList;
    }
}
