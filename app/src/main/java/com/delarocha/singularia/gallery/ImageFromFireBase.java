package com.delarocha.singularia.gallery;

public class ImageFromFireBase {

    String uid;
    String device_imei;
    String email;
    String fecha;
    String imgStoragePath;
    String nombre;
    String password;
    
    public ImageFromFireBase(){
        
        this.device_imei= "";
        this.email= "";
        this.fecha= "";
        this.imgStoragePath= "";
        this.nombre= "";
        this.password= "";
    }

    public ImageFromFireBase(String device_imei, String email, String fecha, String imgStoragePath,
                             String nombre, String password, String uid){
        this.device_imei= device_imei;
        this.email= email;
        this.fecha= fecha;
        this.imgStoragePath= imgStoragePath;
        this.nombre= nombre;
        this.password= password;
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDevice_imei() {
        return device_imei;
    }

    public void setDevice_imei(String device_imei) {
        this.device_imei = device_imei;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getImgStoragePath() {
        return imgStoragePath;
    }

    public void setImgStoragePath(String imgStoragePath) {
        this.imgStoragePath = imgStoragePath;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
