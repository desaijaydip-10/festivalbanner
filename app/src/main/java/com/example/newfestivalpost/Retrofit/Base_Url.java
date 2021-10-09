package com.example.newfestivalpost.Retrofit;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Base_Url {

  
//    public static final String BASE_URL = "http://68.183.81.47/project/banner_maker/public/";
    public static final String BASE_URL = "http://68.183.81.47/project/banner_maker/public/api/";
//    public static final String BASE_URL = "http://68.183.81.47/project/banner_maker/";



    private static Retrofit retrofit = null;

    private static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS).build();

   /* public static Retrofit getClient() {


        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }*/

    public static Retrofit getClient()
    {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        return retrofit;
    }

}
