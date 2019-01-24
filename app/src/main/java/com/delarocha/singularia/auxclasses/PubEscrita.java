package com.delarocha.singularia.auxclasses;

public class PubEscrita {

    String nombrePublicidad;
    String tipoPublicidad; //[Menús, Flyers, Lonas, Carteles]
    String modeloPublicidad; //[estilo_1, estilo_2, estilo_3]
    String img_string;
    String tamaño; //[tamaño_1:carta, tamaño_2:tabloide,tamaño_3:doblecarta, m2]
    String cantidad;
    String precio;
    String comentario;
    boolean tieneOferta;

    public PubEscrita(){

    }

    public PubEscrita(
            String nombrePublicidad,
            String tipoPublicidad,
            String modeloPublicidad,
            String img_string,
            String tamaño,
            String cantidad,
            String precio,
            String comentario,
            boolean tieneOferta) {
        this.nombrePublicidad = nombrePublicidad;
        this.tipoPublicidad = tipoPublicidad;
        this.modeloPublicidad = modeloPublicidad;
        this.img_string = img_string;
        this.tamaño = tamaño;
        this.cantidad = cantidad;
        this.precio = precio;
        this.comentario = comentario;
        this.tieneOferta = tieneOferta;
    }

    public String getNombrePublicidad() {
        return nombrePublicidad;
    }

    public void setNombrePublicidad(String nombrePublicidad) {
        this.nombrePublicidad = nombrePublicidad;
    }

    public String getTipoPublicidad() {
        return tipoPublicidad;
    }

    public void setTipoPublicidad(String tipoPublicidad) {
        this.tipoPublicidad = tipoPublicidad;
    }

    public String getModeloPublicidad() {
        return modeloPublicidad;
    }

    public void setModeloPublicidad(String modeloPublicidad) {
        this.modeloPublicidad = modeloPublicidad;
    }

    public String getImg_string() {
        return img_string;
    }

    public void setImg_string(String img_string) {
        this.img_string = img_string;
    }

    public String getTamaño() {
        return tamaño;
    }

    public void setTamaño(String tamaño) {
        this.tamaño = tamaño;
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
}
