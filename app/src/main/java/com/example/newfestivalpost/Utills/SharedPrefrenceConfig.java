package com.example.newfestivalpost.Utills;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.newfestivalpost.ModelRetrofit.CompanyRecord;
import com.example.newfestivalpost.ModelRetrofit.RecordRegister;
import com.example.newfestivalpost.ModelRetrofit.ResponseLogin;


public class SharedPrefrenceConfig {

    private static final String SHARED_PREF_NAME = "My_shared_pref";
    private Context context;

    public SharedPrefrenceConfig(Context context) {
        this.context = context;
    }

    public static void savebooleanPreferance(Context context, String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MP", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
        editor.commit();
    }

    public static boolean getPrefBoolean(Context context, String key, boolean defvalue) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("MP", Context.MODE_PRIVATE);
        boolean value = sharedPreferences.getBoolean(key, defvalue);
        return value;
    }


    public static void saveStringPreferance(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MP", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
        editor.commit();
    }

    public static String getPrefString(Context context, String key, String defvalue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MP", Context.MODE_PRIVATE);
        String value = sharedPreferences.getString(key, defvalue);
        return value;
    }



    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        //return sharedPreferences.getInt("id", -1) != -1;
        return sharedPreferences.getString("id", null) != null;

    }

    /*public boolean isLoggedout() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        //return sharedPreferences.getInt("id", -1) != -1;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
*/

    public RecordRegister getUser() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new RecordRegister(
                sharedPreferences.getString("id", null),
                sharedPreferences.getString("name", null),
                sharedPreferences.getString("email", null),
                sharedPreferences.getString("city", null),
                sharedPreferences.getString("imageurl", null),
                sharedPreferences.getString("imagepath", null),
                sharedPreferences.getString("contact", null));
    }

    public ResponseLogin getapitoken() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new ResponseLogin(sharedPreferences.getString("api_token", null));
    }

    public void saveUser(RecordRegister user) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("id", user.getId());
        editor.putString("email", user.getEmail());
        editor.putString("name", user.getName());
        editor.putString("city", user.getCity());
        editor.putString("imageurl", user.getImage_url());
        editor.putString("imagepath", user.getImage());
        editor.putString("contact", user.getContact());
        editor.apply();
    }
    public void saveapitoken(ResponseLogin responseuser) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("api_token", responseuser.getApi_token());
        editor.apply();
    }
    public void saveCompanyDetails(CompanyRecord companyRecord) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("id", companyRecord.getId());
        editor.putString("email", companyRecord.getEmail());
        editor.putString("name", companyRecord.getName());
        editor.putString("contact", companyRecord.getContact());
        editor.putString("address", companyRecord.getAddress());
        editor.putString("logo", companyRecord.getLogo());
        editor.apply();
    }
    public CompanyRecord getCompanyDetails() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new CompanyRecord(
                sharedPreferences.getString("id", null),
                sharedPreferences.getString("email", null),
                sharedPreferences.getString("name", null),
                sharedPreferences.getString("contact", null),
                sharedPreferences.getString("address", null),
                sharedPreferences.getString("logo", null));
    }
    //for logout
    public void clear() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

}
