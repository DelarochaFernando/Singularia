package com.delarocha.singularia.auxclasses;

/**
 * Created by jmata on 04/01/2019.
 */

public class ShopItem {

    private String nombreUsuario;
    private String emailUsuario;
    private String fecha;
    private String nombrePdto;
    private String imgPdto;
    private String categoriaPdto;
    private String tipoPdto;
    private String modeloPdto;
    private String cantidadPdto;
    private String comentario;
    private String precioPdto;
    private boolean tieneOferta;
    private boolean onShopCar;

    public ShopItem(){}

    public ShopItem(String nombreUsuario, String emailUsuario, String fecha,String nombrePdto,
                    String imgPdto, String categoriaPdto, String tipoPdto, String modeloPdto,
                    String cantidadPdto, String comentario, String precioPdto, boolean tieneOferta, boolean onShopCar) {
        this.nombreUsuario = nombreUsuario;
        this.emailUsuario = emailUsuario;
        this.fecha = fecha;
        this.nombrePdto = nombrePdto;
        this.imgPdto = imgPdto;
        this.categoriaPdto = categoriaPdto;
        this.tipoPdto = tipoPdto;
        this.modeloPdto = modeloPdto;
        this.cantidadPdto = cantidadPdto;
        this.comentario = comentario;
        this.precioPdto = precioPdto;
        this.tieneOferta = tieneOferta;
        this.onShopCar = onShopCar;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }

    public String getFecha() {
        return fecha;
    }

    public String getNombrePdto() {
        return nombrePdto;
    }

    public void setNombrePdto(String nombrePdto) {
        this.nombrePdto = nombrePdto;
    }

    public String getImgPdto() {
        return imgPdto;
    }

    public void setImgPdto(String imgPdto) {
        this.imgPdto = imgPdto;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getCategoriaPdto() {
        return categoriaPdto;
    }

    public void setCategoriaPdto(String categoriaPdto) {
        this.categoriaPdto = categoriaPdto;
    }

    public String getTipoPdto() {
        return tipoPdto;
    }

    public void setTipoPdto(String tipoPdto) {
        this.tipoPdto = tipoPdto;
    }

    public String getModeloPdto() {
        return modeloPdto;
    }

    public void setModeloPdto(String modeloPdto) {
        this.modeloPdto = modeloPdto;
    }

    public String getCantidadPdto() {
        return cantidadPdto;
    }

    public void setCantidadPdto(String cantidadPdto) {
        this.cantidadPdto = cantidadPdto;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getPrecioPdto() {
        return precioPdto;
    }

    public void setPrecioPdto(String precioPdto) {
        this.precioPdto = precioPdto;
    }

    public boolean getTieneOferta() {
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
