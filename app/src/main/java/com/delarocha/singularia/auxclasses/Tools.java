package com.delarocha.singularia.auxclasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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
    public static final String ITEMCOUNT_KEYNAME = "itemCount";
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


}
