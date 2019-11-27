package com.delarocha.singularia.auxclasses;

public class Account {

    String nombre;
    String email;
    String img_string;
    String img_storage_path;
    String password;
    String pregSeguridad;
    String resSeguridad;
    boolean underReset;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

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

    public String getImg_storage_path() {
        return img_storage_path;
    }

    public void setImg_storage_path(String img_storage_path) {
        this.img_storage_path = img_storage_path;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPregSeguridad() {
        return pregSeguridad;
    }

    public void setPregSeguridad(String pregSeguridad) {
        this.pregSeguridad = pregSeguridad;
    }

    public String getResSeguridad() {
        return resSeguridad;
    }

    public void setResSeguridad(String resSeguridad) {
        this.resSeguridad = resSeguridad;
    }

    public boolean isUnderReset() {
        return underReset;
    }

    public void setUnderReset(boolean underReset) {
        this.underReset = underReset;
    }

    public Account(String nombre, String email, String img_string,String img_storage_path, String password,String pregSeguridad, String resSeguridad, boolean underReset) {
        this.nombre = nombre;
        this.email = email;
        this.img_string = img_string;
        this.img_storage_path = img_storage_path;
        this.password = password;
        this.pregSeguridad = pregSeguridad;
        this.resSeguridad = resSeguridad;
        this.underReset = underReset;
    }

    public Account() {
        this.nombre = "";
        this.email = "";
        this.img_string = "";
        this.img_storage_path = "";
        this.password = "";
        this.pregSeguridad = "";
        this.resSeguridad = "";
        this.underReset = false;
    }
}
