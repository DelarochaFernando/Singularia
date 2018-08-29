package com.delarocha.singularia.auxclasses;

public class Account {

    String nombre;
    //String lastname;
    //String surname;
    String email;
    String img_string;
    String password;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /*public String getLastname() {
        return lastname;
    }*/

    /*public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }*/

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImg_string() {
        return img_string;
    }

    public void setImg_string(String img_string) {
        this.img_string = img_string;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Account(String nombre, String lastname, String surname, String email, String img_string, String password) {
        this.nombre = nombre;
        //this.lastname = lastname;
        //this.surname = surname;
        this.email = email;
        this.img_string = img_string;
        this.password = password;
    }

    public Account() {
        this.nombre = "";
        //this.lastname = "";
        //this.surname = "";
        this.email = "";
        this.img_string = "";
    }
}
