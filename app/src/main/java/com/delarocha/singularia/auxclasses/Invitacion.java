package com.delarocha.singularia.auxclasses;

public class Invitacion{

    String nombreInvita;
    String tipoInvita; //[BabyShower:0, Bautizo:1, Boda:2, Despedida:3, FiestaInf:4, FiestaTem:5, Graduación:6, XV:7]
    String modeloInvita;//[estilo_1, estilo_2, estilo_3]
    String img_string;
    String cantidad;
    String precio; //[Precio_1:250, Precio_2:500, Precio_3:1000]
    String comentario;
    boolean tieneOferta;
    boolean onShopCar;

    public Invitacion(){

    }

    public Invitacion(
            String nombreInvita,
            String tipoInvita,
            String modeloInvita,
            String img_string,
            String cantidad,
            String precio,
            String comentario,
            boolean tieneOferta,
            boolean onShopCar) {
        this.nombreInvita = nombreInvita;
        this.tipoInvita = tipoInvita;
        this.modeloInvita = modeloInvita;
        this.img_string = img_string;
        this.cantidad = cantidad;
        this.precio = precio;
        this.comentario = comentario;
        this.tieneOferta = tieneOferta;
        this.onShopCar = onShopCar;
    }

    public String getNombreInvita() {
        return nombreInvita;
    }

    public void setNombreInvita(String nombreInvita) {
        this.nombreInvita = nombreInvita;
    }

    public String getTipoInvita() {
        return tipoInvita;
    }

    public void setTipoInvita(String tipoInvita) {
        this.tipoInvita = tipoInvita;
    }

    public String getModeloInvita() {
        return modeloInvita;
    }

    public void setModeloInvita(String modeloInvita) {
        this.modeloInvita = modeloInvita;
    }

    public String getImg_string() {
        return img_string;
    }

    public void setImg_string(String img_string) {
        this.img_string = img_string;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public boolean isTieneOferta() {
        return tieneOferta;
    }

    public void setTieneOferta(boolean tieneOferta) {
        this.tieneOferta = tieneOferta;
    }

    public boolean isOnShopCar() {
        return onShopCar;
    }

    public void setOnShopCar(boolean onShopCar) {
        this.onShopCar = onShopCar;
    }
}
