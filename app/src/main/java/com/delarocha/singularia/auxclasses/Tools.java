package com.delarocha.singularia.auxclasses;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by jmata on 07/01/2019.
 */

public class Tools {

    Context mContext;
    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor mEditor;
    public static String FIRESTORE_INVITACIONES_COLLECTION = "invitaciones";
    public static String FIRESTORE_ACCOUNTS_COLLECTION = "accounts";
    public static final String FIRESTORE_IMGS_COLLECTION = "imagenesPromociones";
    public static final String FIRESTORE_SHOPSESSION_COLLECTION = "shopSession";
    public static final String FIRESTORE_GALLERY_COLLECTION = "imageGallery";
    public static final String ITEMCOUNT_KEYNAME = "itemCount";
    public static final String SWIPE_DIRECTION = "swipe_direction";
    public static final String COUNT_MOVES = "count_moves";
    public static String UNDEFINED = "undefined";
    public static String FROM_TIPOINVITA_TAG = "fromTipoInvita";
    public static final String FIRESTORAGE_IMGS_URL = "gs://singularia-fase1-2018.appspot.com/";
    public static final String USER_LOOGED_IN_STATUS_KEYNAME = "LoggedIn";
    public static final String USER_LOGIN_EMAIL_KEYNAME ="userLoginEmail";
    public static final String USER_LOGIN_PSW_KEYNAME ="userLoginPsw";
    //public static final String

    public Tools(Context context){
        this.mContext = context;
        this.mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public void setStringPreference(String keyname, String value){
        mEditor = mSharedPreferences.edit();
        mEditor.putString(keyname, value);
        mEditor.commit();
    }

    public String getStringPreference(String keyname){
        return mSharedPreferences.getString(keyname,null);
    }

    public void setIntPreference(String keyname, int value){
        mEditor = mSharedPreferences.edit();
        mEditor.putInt(keyname, value);
        mEditor.commit();
    }

    public int getIntPreference(String keyname){
        return mSharedPreferences.getInt(keyname,-1);
    }

    public void setBooleanPreference(String keyname, boolean value){
        mEditor = mSharedPreferences.edit();
        mEditor.putBoolean(keyname,value);
        mEditor.commit();
    }

    public void clearUser(){
        mEditor = mSharedPreferences.edit();
        mEditor.clear();
        mEditor.commit();
    }

    public boolean getBooleanPreference(String keyname){
        return mSharedPreferences.getBoolean(keyname, false);
    }

    public float convertDpToPixel(float dp) {
        Resources resources = mContext.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    public float convertPixelsToDp(float px) {
        Resources resources = mContext.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    public static float convertDpToPixel(Context context, float dp) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    public static float convertPixelsToDp(Context context, float px) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    public static Double getDiagonalInch(Context c) {

        DisplayMetrics metrics = new DisplayMetrics();
        double diaInch = 0;
        WindowManager wm = (WindowManager) c.getSystemService(
                Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        final int measuredwidth = metrics.widthPixels;
        final int measuredheight = metrics.heightPixels;

        final double diagonal = Math.sqrt((measuredwidth * measuredwidth)
                + (measuredheight * measuredheight));


        diaInch = diagonal / metrics.densityDpi;

        return diaInch;

    }

    public Bitmap getBitmapOrientationAdjusted(Bitmap bitmap){
        boolean landscape = false;
        boolean portrait = false;
        //Bitmap newBitmap = bitmap;
        Bitmap adjusted = null;
        try{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //bitmap.compress(Bitmap.CompressFormat.JPEG,80,baos);

            File file = new File(mContext.getFilesDir(), "Image"
                    + new Random().nextInt() + ".jpeg");
            FileOutputStream out = mContext.openFileOutput(file.getName(),
                    Context.MODE_PRIVATE);
            //newBitmap.compress(Bitmap.CompressFormat.JPEG,80,out);
            bitmap.compress(Bitmap.CompressFormat.JPEG,80,out);
            out.flush();
            out.close();
            String path = file.getAbsolutePath();
            //String path = MediaStore.Images.Media.insertImage(mContext.getContentResolver()
                    //,bitmap,"Title",null);
            File f = new File(path);
            Uri uri = Uri.fromFile(f);
            ExifInterface exif = new ExifInterface(uri.getPath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL);
            int angle = 0;
            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    angle = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    angle = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    angle = 90;
                    break;
                default:
                    angle = 0;
                    break;
            }
            if(bitmap.getWidth()>bitmap.getHeight()) {
                landscape = true;
            }else{
                portrait = true;
            }

            Matrix m = new Matrix();
            if(angle == 0&& landscape){
                m.postRotate(90);
                //m.setScale(50,50);
            }else if(angle == 0 && portrait) {
                m.postRotate(270);
                //m.setScale(70,70);
            }

            //newBitmap = Bitmap.createBitmap(newBitmap,0,0,newBitmap.getWidth(),newBitmap.getHeight(),m,true);
            float wdpfloat = 30f;
            float hdpfloat = 30f;
            int w = (int) convertDpToPixel(mContext,wdpfloat);
            int h = (int) convertDpToPixel(mContext,hdpfloat);
            adjusted = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),m,true);
            //adjusted = Bitmap.createBitmap(bitmap,0,0,w,h,m,true);

        }catch (Exception e){
            e.printStackTrace();
        }

        return adjusted;
    }


    @SuppressLint("MissingPermission")
    public static String getIMEI (Context context) {
        try {
            TelephonyManager mngr = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                return mngr.getImei();
            }else{

                if (mngr.getDeviceId() != null) {
                    return mngr.getDeviceId();
                }
                return UNDEFINED;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return UNDEFINED;
        }
    }

    public String getFecha(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        //Date mDate = sdf.parse(fec_alta);
        //long dateInMiliseconds = mDate.getTime();
        //String dateMiliText = String.valueOf(dateInMiliseconds);

        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        long dateInMiliseconds = calendar.getTimeInMillis();
        String imei = (String) getIMEI(mContext);
        String dayString = "";
        String monthString = "";
        String minuteString = "";
        String secondString = "";
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day   = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        if(month<10){
            monthString = "0"+month;
        }else{
            monthString = String.valueOf(month);
        }
        if(day<10){
            dayString = "0"+day;
        }else{
            dayString = String.valueOf(day);
        }
        if(minute<10){
            minuteString = "0"+minute;
        }else{
            minuteString = String.valueOf(minute);
        }
        if(second<10){
            secondString = "0"+second;
        }else{
            secondString = String.valueOf(second);
        }

        String fecha = String.valueOf(year+"-"+monthString+"-"+dayString);
        return fecha;
    }

}
