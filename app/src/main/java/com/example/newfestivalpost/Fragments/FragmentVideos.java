package com.example.newfestivalpost.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newfestivalpost.Activities.ActivityHome;
import com.example.newfestivalpost.Adapters.AdapterVideoList;
import com.example.newfestivalpost.Adapters.AdapterViewAllList;
import com.example.newfestivalpost.ModelRetrofit.CategoriesRecord;
import com.example.newfestivalpost.ModelRetrofit.List_of_Categories_name;
import com.example.newfestivalpost.ModelRetrofit.List_of_Video_Category_Name;
import com.example.newfestivalpost.ModelRetrofit.VideoCategoriesData;
import com.example.newfestivalpost.ModelRetrofit.VideoHomeData;
import com.example.newfestivalpost.ModelRetrofit.VideoHomeModel;
import com.example.newfestivalpost.R;
import com.example.newfestivalpost.Retrofit.Api;
import com.example.newfestivalpost.Retrofit.Base_Url;
import com.example.newfestivalpost.Utills.Constance;
import com.example.newfestivalpost.Utills.SharedPrefrenceConfig;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentVideos extends Fragment {

    Context context;
    View view;
    SharedPrefrenceConfig sharedPrefrenceConfig;
    RecyclerView rv_viewalllist,rv_cat_alv;
    Tracker mTracker;
    String language;
    LinearLayout ll_select_list,ll_as_festival,ll_as_category;
    TextView tv_as_festival,tv_as_category;
    ArrayList<VideoHomeData> videoCategoriesDataArrayList;
    AdapterVideoList adapterVideoList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_videos, container, false);
        context = getContext();
        sharedPrefrenceConfig = new SharedPrefrenceConfig(context);
        language = sharedPrefrenceConfig.getPrefString(context, Constance.language, "");

        bindView();
        rv_viewalllist.setLayoutManager(new GridLayoutManager(context,3));
        rv_cat_alv.setLayoutManager(new GridLayoutManager(context,3));
        ll_select_list.setVisibility(View.VISIBLE);
        getVideoFestival();
        ll_as_festival.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rv_viewalllist.setVisibility(View.VISIBLE);
                rv_cat_alv.setVisibility(View.GONE);
                tv_as_festival.setTextColor(getResources().getColor(R.color.colorWhite));
                tv_as_category.setTextColor(getResources().getColor(R.color.colorTheame2));
                ll_as_festival.setBackground(getResources().getDrawable(R.drawable.roundcorner_button));
                ll_as_category.setBackground(null);
                getVideoFestival();
            }
        });
        ll_as_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rv_cat_alv.setVisibility(View.VISIBLE);
                rv_viewalllist.setVisibility(View.GONE);
                tv_as_category.setTextColor(getResources().getColor(R.color.colorWhite));
                tv_as_festival.setTextColor(getResources().getColor(R.color.colorTheame2));
                ll_as_festival.setBackground(null);
                ll_as_category.setBackground(getResources().getDrawable(R.drawable.roundcorner_button));
                getVideoBusiness();
            }
        });

        return view;
    }

    public void bindView() {
        rv_viewalllist = view.findViewById(R.id.rv_viewalllist);
        rv_cat_alv = view.findViewById(R.id.rv_cat_alv);
        ll_select_list = view.findViewById(R.id.ll_select_list);
        ll_as_festival = view.findViewById(R.id.ll_as_festival);
        tv_as_festival = view.findViewById(R.id.tv_as_festival);
        ll_as_category = view.findViewById(R.id.ll_as_category);
        tv_as_category = view.findViewById(R.id.tv_as_category);
        videoCategoriesDataArrayList=new ArrayList<>();
    }


    public void getVideoBusiness() {
        videoCategoriesDataArrayList.clear();
        Api api = Base_Url.getClient().create(Api.class);
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        Call<VideoHomeModel> call = api.getBusinessCategoriesList(sharedPrefrenceConfig.getPrefString(context, Constance.CToken, null));

        call.enqueue(new Callback<VideoHomeModel>() {
            @Override
            public void onResponse(Call<VideoHomeModel> call, Response<VideoHomeModel> response) {
                if (response.body().getResult().equals("1") || response.body().getResult() != null) {
                    if (response.body().getRecords() != null) {
                        if (response.body().getRecords().getData() != null && response.body().getRecords().getData().size() != 0) {
                            Log.d("fdfsdkfk", "dsfji" + response.body().getRecords().getData().size());
                            //Toast.makeText(context,"not null data ",Toast.LENGTH_LONG).show();
                            videoCategoriesDataArrayList.addAll(response.body().getRecords().getData());
                            adapterVideoList = new AdapterVideoList(context, videoCategoriesDataArrayList, "businessviewall");
                            rv_cat_alv.setAdapter(adapterVideoList);
                            progressDialog.dismiss();
                        } else {


                           /* AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("Video Category is coming soon")
                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            dialog.cancel();

                                            startActivity(new Intent(context, ActivityHome.class));


                                        }
                                    });

                            //Creating dialog box
                            AlertDialog alert = builder.create();
                            alert.show();*/
                            progressDialog.dismiss();
                        }


                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("getrecord null" + response.body().getMessage())
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
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("getresult null" + response.body().getMessage())
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
                Toast.makeText(context, "check Your internet Connection", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();

            }
        });

    }

    public void getVideoFestival() {
        videoCategoriesDataArrayList.clear();
        Api api = Base_Url.getClient().create(Api.class);
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        Call<VideoHomeModel> call = api.festivaslVideoCatList(sharedPrefrenceConfig.getPrefString(context, Constance.CToken, null));

        call.enqueue(new Callback<VideoHomeModel>() {
            @Override
            public void onResponse(Call<VideoHomeModel> call, Response<VideoHomeModel> response) {
                if (response.body().getResult().equals("1") || response.body().getResult() != null) {
                    if (response.body().getRecords() != null) {
                        if (response.body().getRecords().getData() != null && response.body().getRecords().getData().size() != 0) {
                            Log.d("fdfsdkfk", "dsfji" + response.body().getRecords().getData().size());
                            //Toast.makeText(context,"not null data ",Toast.LENGTH_LONG).show();
                            videoCategoriesDataArrayList.addAll(response.body().getRecords().getData());
                            adapterVideoList = new AdapterVideoList(context, videoCategoriesDataArrayList, "festivalviewall");
                            rv_viewalllist.setVisibility(View.VISIBLE);
                            rv_viewalllist.setAdapter(adapterVideoList);
                            progressDialog.dismiss();
                        } else {


                           /* AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("Video Category is coming soon")
                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            dialog.cancel();

                                            startActivity(new Intent(context, ActivityHome.class));


                                        }
                                    });

                            //Creating dialog box
                            AlertDialog alert = builder.create();
                            alert.show();*/
                            progressDialog.dismiss();
                        }


                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("getrecord null" + response.body().getMessage())
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
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("getresult null" + response.body().getMessage())
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
                Toast.makeText(context, "check Your internet Connection", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();

            }
        });

    }

}
