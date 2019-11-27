package com.delarocha.singularia.database;

import android.provider.BaseColumns;

public class SingulariaDBContract {

    public static abstract class GalleryImagesTable implements BaseColumns{

        public static String TABLE_NAME = "GalleryImages";
        public static String UID = "uid";
        public static String DEVICE_IMEI = "device_imei";
        public static String EMAIL = "email";
        public static String FECHA = "fecha";
        public static String IMG_STRING = "img_string";
        public static String NOMBRE = "nombre";
        public static String PASSWORD = "password";
    }

    public static abstract class UserLoginStatusTable implements BaseColumns{

        public static String TABLE_NAME = "UserLoginStatus";
        public static String UID = "uid";
        public static String DEVICE_IMEI = "device_imei";
        public static String EMAIL = "email";
    }
}
