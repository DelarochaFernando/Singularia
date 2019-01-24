package com.delarocha.singularia.auxclasses;

public class TipoInvitaCard {

    private String titulo;
    private int imageSource;
    private String url;

    public TipoInvitaCard(){

    }

    public TipoInvitaCard(String titulo, int imageSource, String url){
        this.titulo = titulo;
        this.imageSource = imageSource;
        this.url = url;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
