package com.example.tomal.jupitarplatform;

public class LeadDetailsModel {

    private int customer;
    private String comment;

    public LeadDetailsModel(int customer, String comment) {
        this.customer = customer;
        this.comment = comment;
    }

    public LeadDetailsModel() {
    }

    public int getCustomer() {
        return customer;
    }

    public void setCustomer(int customer) {
        this.customer = customer;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
