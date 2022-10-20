package com.kripton.paceclasses;

public class PaymentModel {
    String method,id,amount,cname,created_at;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public PaymentModel(String method, String id, String amount, String cname, String created_at) {
        this.method = method;
        this.id = id;
        this.amount = amount;
        this.cname = cname;
        this.created_at = created_at;
    }
}
