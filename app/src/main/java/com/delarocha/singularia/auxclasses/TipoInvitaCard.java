package com.delarocha.singularia.auxclasses;

public class TipoInvitaCard {

    String titulo;
    int imageSource;

    public TipoInvitaCard(){

    }

    public TipoInvitaCard(String titulo, int imageSource){
        this.titulo = titulo;
        this.imageSource = imageSource;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getImageSource() {
        return imageSource;
    }

    public void setImageSource(int imageSource) {
        this.imageSource = imageSource;
    }






}
