package com.example.newfestivalpost.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.newfestivalpost.Adapters.BusinessCatAdepter;
import com.example.newfestivalpost.ModelRetrofit.CategoriesData;
import com.example.newfestivalpost.ModelRetrofit.VideoHomeData;
import com.example.newfestivalpost.ModelRetrofit.VideoHomeModel;
import com.example.newfestivalpost.R;
import com.example.newfestivalpost.Retrofit.Api;
import com.example.newfestivalpost.Retrofit.Base_Url;
import com.example.newfestivalpost.Utills.Constance;
import com.example.newfestivalpost.Utills.SharedPrefrenceConfig;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryFragment extends Fragment {


    View view;
    RecyclerView rv_viewalllist;
    Context context;
    SharedPrefrenceConfig sharedPrefrenceConfig;
    String language;
    ArrayList<VideoHomeData> businessCatList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_category, container, false);
        initBCat();
        context=getContext();
        sharedPrefrenceConfig=new SharedPrefrenceConfig(context);

        language=sharedPrefrenceConfig.getPrefString(context,Constance.language,"");

        rv_viewalllist.setLayoutManager(new GridLayoutManager(context,3));
        getBusinessCat();
        return view;
    }
    void initBCat(){
        rv_viewalllist=view.findViewById(R.id.rv_viewalllist);
        businessCatList=new ArrayList<>();
    }

    public void getBusinessCat() {
        Api api= Base_Url.getClient().create(Api.class);
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        Call<VideoHomeModel> call = api.getBusinessCategoriesList(sharedPrefrenceConfig.getPrefString(context, Constance.CToken,null));

        call.enqueue(new Callback<VideoHomeModel>() {
            @Override
            public void onResponse(Call<VideoHomeModel> call, Response<VideoHomeModel> response) {

                if (response.body().getResult().equals("1") || response.body().getResult() != null)
                {
                    if (response.body().getRecords() != null)
                    {

                        if (response.body().getRecords().getData() != null)
                        {

                            Log.d("fdfsdkfk","dsfji"+response.body().getRecords().getData().size());
                            businessCatList.addAll(response.body().getRecords().getData());
                            BusinessCatAdepter businessCatAdepter=new BusinessCatAdepter(context,R.layout.seeall_list_rawl,businessCatList);
                            rv_viewalllist.setAdapter(businessCatAdepter);
                           /* AdapterChild_Items_Categories adapterChild_items_categories=new AdapterChild_Items_Categories(context,viewalllist);
                            rv_childlist.setAdapter(adapterChild_items_categories);
                          */  progressDialog.dismiss();


                        }
                        else
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("getdata null"+response.body().getMessage())
                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();

                                        }
                                    });

                            //Creating dialog box
                            AlertDialog alert = builder.create();
                            alert.show();
                        }


                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("getrecord null"+response.body().getMessage())
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();

                                    }
                                });

                        //Creating dialog box
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("getresult null"+response.body().getMessage())
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();

                                }
                            });

                    //Creating dialog box
                    AlertDialog alert = builder.create();
                    alert.show();
                    progressDialog.dismiss();
                }

            }

            @Override
            public void onFailure(Call<VideoHomeModel> call, Throwable t) {
                Toast.makeText(context,"check Your internet Connection",Toast.LENGTH_LONG).show();
                progressDialog.dismiss();

            }
        });

    }

}