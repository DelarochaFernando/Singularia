package com.delarocha.singularia.auxclasses;

import androidx.annotation.NonNull;

public class ImageSavedPath {

    private String imageURL;

    public ImageSavedPath(){}
    public ImageSavedPath(String imageURL){
        this.imageURL = imageURL;
    }

    public String getImageURL(String imageURL){
        return imageURL;
    }

    @NonNull
    @Override
    public String toString() {
        return "ImageSavedPath{"+
                "imageURL='"+imageURL+'\''+
                '}';
        //return super.toString();
    }
}
