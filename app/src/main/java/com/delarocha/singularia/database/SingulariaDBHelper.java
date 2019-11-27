package com.delarocha.singularia.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.delarocha.singularia.gallery.ImageFromFireBase;

import java.util.ArrayList;
import java.util.List;

public class SingulariaDBHelper extends SQLiteOpenHelper {

    private SQLiteDatabase db;
    public static final String DATABASENAME = "SingulariaDB.db";
    public static final int DBVERSION = 1;


    protected String CREATE_TABLE_GALLERY_IMAGES =
       "CREATE TABLE "+ SingulariaDBContract.GalleryImagesTable.TABLE_NAME+" ("
         +SingulariaDBContract.GalleryImagesTable._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
         +SingulariaDBContract.GalleryImagesTable.UID+" TEXT NOT NULL,"
         +SingulariaDBContract.GalleryImagesTable.DEVICE_IMEI+" TEXT NOT NULL,"
         +SingulariaDBContract.GalleryImagesTable.EMAIL+" TEXT NOT NULL,"
         +SingulariaDBContract.GalleryImagesTable.FECHA+" TEXT NOT NULL,"
         +SingulariaDBContract.GalleryImagesTable.IMG_STRING+" TEXT NOT NULL,"
         +SingulariaDBContract.GalleryImagesTable.NOMBRE+" TEXT NOT NULL,"
         +SingulariaDBContract.GalleryImagesTable.PASSWORD+" TEXT NOT NULL)";

    protected String CREATE_TABLE_USER_LOGIN_STATUS =
            "CREATE TABLE";



    public SingulariaDBHelper(Context context) {
        super(context, DATABASENAME, null, DBVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(CREATE_TABLE_GALLERY_IMAGES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void openDB() throws SQLException{
        db = this.getReadableDatabase();
    }

    public void openDBToWrite() throws SQLiteException{
        db = this.getWritableDatabase();
    }

    public void closeDB(){
        db.close();
    }

    public long insertGalleryImage(ImageFromFireBase image){

        long res = 0;
        try{

            ContentValues values = new ContentValues();
            openDBToWrite();

                values.put(SingulariaDBContract.GalleryImagesTable.DEVICE_IMEI,image.getDevice_imei());
                values.put(SingulariaDBContract.GalleryImagesTable.EMAIL,image.getEmail());
                values.put(SingulariaDBContract.GalleryImagesTable.FECHA,image.getFecha());
                values.put(SingulariaDBContract.GalleryImagesTable.IMG_STRING,image.getFecha());
                values.put(SingulariaDBContract.GalleryImagesTable.NOMBRE,image.getNombre());
                values.put(SingulariaDBContract.GalleryImagesTable.PASSWORD,image.getPassword());

                res = db.insert(SingulariaDBContract.GalleryImagesTable.TABLE_NAME,null,values);

        }catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public List<ImageFromFireBase> getAllGalleryImages(String userid) {
        List<ImageFromFireBase> resList;
        Cursor c = null;
        String imei = "";
        String email = "";
        String fecha = "";
        String img_str = "";
        String nombre = "";
        String password = "";
        String uid = "";
        resList = new ArrayList<>();
        try {
            openDB();
            String SelectImagesQuery = "SELECT "
                    +SingulariaDBContract.GalleryImagesTable.DEVICE_IMEI+","
            +SingulariaDBContract.GalleryImagesTable.EMAIL+","
            +SingulariaDBContract.GalleryImagesTable.FECHA+","
            +SingulariaDBContract.GalleryImagesTable.IMG_STRING+","
            +SingulariaDBContract.GalleryImagesTable.NOMBRE+","
            +SingulariaDBContract.GalleryImagesTable.PASSWORD+" FROM "
            +SingulariaDBContract.GalleryImagesTable.TABLE_NAME+" WHERE "
            +SingulariaDBContract.GalleryImagesTable.UID+ "= '"+userid+"' ";
            c = db.rawQuery(SelectImagesQuery, null);

            if (c != null) {

                if (c.getCount() > 0) {

                    if (c.moveToFirst()) {
                        imei = c.getString(0);
                        email = c.getString(1);
                        fecha = c.getString(2);
                        img_str = c.getString(3);
                        nombre = c.getString(4);
                        password = c.getString(5);
                        uid = c.getString(6);
                        resList.add(new ImageFromFireBase(imei,email,fecha,img_str,nombre,password,uid));

                        while (c.moveToNext()){
                            imei = c.getString(0);
                            email = c.getString(1);
                            fecha = c.getString(2);
                            img_str = c.getString(3);
                            nombre = c.getString(4);
                            password = c.getString(5);
                            uid = c.getString(6);
                            resList.add(new ImageFromFireBase(imei,email,fecha,img_str,nombre,password,uid));
                        }
                    }
                    //return resList;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            //return resList;
        }
        return resList;
    }
}
