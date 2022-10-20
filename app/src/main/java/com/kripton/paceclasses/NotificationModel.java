package com.kripton.paceclasses;

public class NotificationModel {
    String id,msg;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public NotificationModel(String id, String msg) {
        this.id = id;
        this.msg = msg;
    }
}
