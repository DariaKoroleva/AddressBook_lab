package com.example.addressbook_lab;

import java.io.Serializable;

public class Contact implements Serializable {

    private String first_name, last_name, patronymic, phone_number, id;

    /*
    public Contact(String first_name, String last_name, String patronymic, String phone_number) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.patronymic = patronymic;
        this.phone_number = phone_number;
    }*/

    public String getFirstName() {
        return this.first_name;
    }

    public void setFirstName(String first_name) {
        this.first_name = first_name;
    }

    public String getLastName() {
        return this.last_name;
    }

    public void setLastName(String last_name) {
        this.last_name = last_name;
    }

    public String getPhoneNumber() {
        return this.phone_number;
    }

    public void setPhoneNumber(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getPatronymic() {
        return this.patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic= patronymic;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }
}