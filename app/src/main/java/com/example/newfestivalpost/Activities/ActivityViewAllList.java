package com.example.newfestivalpost.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newfestivalpost.Adapters.AdapterCategoryList;
import com.example.newfestivalpost.Adapters.AdapterChild_Items_Categories;
import com.example.newfestivalpost.Adapters.AdapterHomeParent;
import com.example.newfestivalpost.Adapters.AdapterVideoList;
import com.example.newfestivalpost.Adapters.AdapterViewAllList;
import com.example.newfestivalpost.Adapters.BusinessCatAdepter;
import com.example.newfestivalpost.Adapters.GreetingCatAdepter;
import com.example.newfestivalpost.AnalyticsApplication;
import com.example.newfestivalpost.Model.ModelHomeChild;
import com.example.newfestivalpost.ModelRetrofit.CategoriesData;
import com.example.newfestivalpost.ModelRetrofit.CategoriesRecord;
import com.example.newfestivalpost.ModelRetrofit.List_of_Categories_name;
import com.example.newfestivalpost.ModelRetrofit.VideoHomeData;
import com.example.newfestivalpost.ModelRetrofit.VideoHomeModel;
import com.example.newfestivalpost.R;
import com.example.newfestivalpost.Retrofit.Api;
import com.example.newfestivalpost.Retrofit.Base_Url;
import com.example.newfestivalpost.Utills.Constance;
import com.example.newfestivalpost.Utills.SharedPrefrenceConfig;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

import cn.xm.weidongjian.popuphelper.PopupWindowHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityViewAllList extends AppCompatActivity {

    Context context;

    AdView mAdView;
    LinearLayout facbook_ad_banner,ll_select_list,ll_as_festival,ll_as_category;
    TextView tv_viewalltittlename,tv_as_festival,tv_as_category;
    RecyclerView rv_viewalllist,rv_cat_alv;
    //ArrayList<ModelHomeChild> viewalllist;
    ArrayList<CategoriesData> viewalllist;
    AdapterViewAllList adapterViewAllList;
    String parentname,language;
    ImageView iv_backarrow,iv_al_language;
    Tracker mTracker;
    SharedPrefrenceConfig sharedPrefrenceConfig;
    AdapterVideoList adapterVideoList;
    ArrayList<VideoHomeData> businessCatList,videoCategoriesDataArrayList,greetingCatList;
    public PopupWindowHelper popupWindowHelper;
    View popupview_down;
    String frameName[]={"English","Hindi","Gujarati"};
    public static ActivityViewAllList instance = null;

    private final String TAG = ActivityViewAllList.class.getSimpleName();
    public ActivityViewAllList() {

        instance = ActivityViewAllList.this;
    }

    public static synchronized ActivityViewAllList getInstance() {
        if (instance == null) {
            instance = new ActivityViewAllList();
        }
        return instance;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_all_list);

        context=ActivityViewAllList.this;
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();


        bindView();
        if (Constance.adType.equals("Ad Mob")) {
            loadAdMobAd();

        } else {

            loadFacebookAd();
        }
        parentname=getIntent().getExtras().getString("parentname");
        tv_viewalltittlename.setText(parentname);

        sharedPrefrenceConfig=new SharedPrefrenceConfig(context);

        language=sharedPrefrenceConfig.getPrefString(context,Constance.language,"");

        viewalllist=new ArrayList<>();


        rv_viewalllist.setLayoutManager(new GridLayoutManager(context,3));
        rv_cat_alv.setLayoutManager(new GridLayoutManager(context,3));
       /* viewalllist=new ArrayList<>();
        viewalllist.clear();
        viewalllist.addAll(Constance.viewAllDataList);

        rv_viewalllist.setLayoutManager(new GridLayoutManager(context,3));
        adapterViewAllList=new AdapterViewAllList(context,viewalllist);
        rv_viewalllist.setAdapter(adapterViewAllList);
*/

        if(parentname.equals("Business")){
            rv_viewalllist.setVisibility(View.VISIBLE);
            getBusinessCat();
        }
        if(parentname.equals("Video")){
            rv_viewalllist.setVisibility(View.VISIBLE);
            getVideoFestival();
            ll_select_list.setVisibility(View.VISIBLE);

        }
        if(parentname.equals("Festival")){
            rv_viewalllist.setVisibility(View.VISIBLE);
            getCategoriesViewAll();
        }
        if(parentname.equals("Greeting")){
            rv_viewalllist.setVisibility(View.VISIBLE);
            getGreetingCat();
        }

        iv_backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        iv_al_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popupview_down = LayoutInflater.from(context).inflate(R.layout.category_dropdown_raw, null);
                popupWindowHelper = new PopupWindowHelper(popupview_down);

                popupWindowHelper.showAsDropDown(v, 0, 0);
                RecyclerView rv_cat_dd_raw=popupview_down.findViewById(R.id.rv_cat_dd_raw);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
                rv_cat_dd_raw.setLayoutManager(gridLayoutManager);
                rv_cat_dd_raw.setHasFixedSize(true);

                AdapterCategoryList adapterCategory = new AdapterCategoryList(context, frameName,"allList");
                rv_cat_dd_raw.setAdapter(adapterCategory);
            }
        });

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("Image~" + "Google Analytics Testing");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void loadList(){

        if(parentname.equals("Business")){
            getBusinessCat();
        }
        if(parentname.equals("Video")){
            getVideoBusiness();
        }
        if(parentname.equals("Festival")){
            getCategoriesViewAll();
        }
    }

    public void getCategoriesViewAll() {
        viewalllist.clear();
        Log.d("check_apitoken","1"+sharedPrefrenceConfig.getapitoken().getApi_token());
        Api api= Base_Url.getClient().create(Api.class);
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        Call<List_of_Categories_name> call = api.getCategoriesNameList(sharedPrefrenceConfig.getapitoken().getApi_token());

        call.enqueue(new Callback<List_of_Categories_name>() {
            @Override
            public void onResponse(Call<List_of_Categories_name> call, Response<List_of_Categories_name> response) {
                Log.d("check_apitoken","2: "+sharedPrefrenceConfig.getapitoken().getApi_token());

                if (response.body().getResult().equals("1") || response.body().getResult() != null)
                {
                    if (response.body().getRecords() != null)
                    {

                        if (response.body().getRecords().getData() != null)
                        {

                            CategoriesRecord record = response.body().getRecords();
                            viewalllist.addAll(response.body().getRecords().getData());
                            adapterViewAllList=new AdapterViewAllList(context,viewalllist);
                            rv_viewalllist.setAdapter(adapterViewAllList);
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
            public void onFailure(Call<List_of_Categories_name> call, Throwable t) {
                Toast.makeText(context,"check Your internet Connection",Toast.LENGTH_LONG).show();
                progressDialog.dismiss();

            }
        });

    }

    public void getGreetingCat() {
        Api api = Base_Url.getClient().create(Api.class);
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        Call<VideoHomeModel> call = api.getGreetingCategoriesList(sharedPrefrenceConfig.getPrefString(context, Constance.CToken, null));

        call.enqueue(new Callback<VideoHomeModel>() {
            @Override
            public void onResponse(Call<VideoHomeModel> call, Response<VideoHomeModel> response) {

                if (response.body().getResult().equals("1") || response.body().getResult() != null) {
                    if (response.body().getRecords() != null) {

                        if (response.body().getRecords().getData() != null) {

                            rv_viewalllist.setVisibility(View.VISIBLE);
                            Log.d("fdfsdkfk", "dsfji" + response.body().getRecords().getData().size());
                            greetingCatList.addAll(response.body().getRecords().getData());
                            GreetingCatAdepter greetingCatAdepter = new GreetingCatAdepter(context, R.layout.seeall_list_rawl,greetingCatList);
                            rv_viewalllist.setAdapter(greetingCatAdepter);
                           /* AdapterChild_Items_Categories adapterChild_items_categories=new AdapterChild_Items_Categories(context,viewalllist);
                            rv_childlist.setAdapter(adapterChild_items_categories);
                          */
                            progressDialog.dismiss();


                        } else {
                            rv_viewalllist.setVisibility(View.GONE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("getdata null" + response.body().getMessage())
                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();

                                        }
                                    });

                            //Creating dialog box
                            AlertDialog alert = builder.create();
                            alert.show();
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

    public void getBusinessCat() {
        Api api= Base_Url.getClient().create(Api.class);
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        Call<VideoHomeModel> call = api.getBusinessCategoriesList(sharedPrefrenceConfig.getPrefString(context,Constance.CToken,null));

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

    public void getVideoBusiness() {
        videoCategoriesDataArrayList.clear();
        Api api= Base_Url.getClient().create(Api.class);
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        Call<VideoHomeModel> call = api.getBusinessCategoriesList(sharedPrefrenceConfig.getPrefString(context,Constance.CToken,null));

        call.enqueue(new Callback<VideoHomeModel>() {
            @Override
            public void onResponse(Call<VideoHomeModel> call, Response<VideoHomeModel> response) {
                if (response.body().getResult().equals("1") || response.body().getResult() != null)
                {
                    if (response.body().getRecords() != null)
                    {
                        if (response.body().getRecords().getData() != null && response.body().getRecords().getData().size()!=0)
                        {
                            Log.d("fdfsdkfk","dsfji"+response.body().getRecords().getData().size());


                            //Toast.makeText(context,"not null data ",Toast.LENGTH_LONG).show();
                            videoCategoriesDataArrayList.addAll(response.body().getRecords().getData());
                            adapterVideoList=new AdapterVideoList(context,videoCategoriesDataArrayList,"businessviewall");
                            rv_cat_alv.setAdapter(adapterVideoList);
                            progressDialog.dismiss();
                        }
                        else
                        {


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
                        progressDialog.dismiss();
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

    public void getVideoFestival() {
        videoCategoriesDataArrayList.clear();
        Api api= Base_Url.getClient().create(Api.class);
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        Call<VideoHomeModel> call = api.festivaslVideoCatList(sharedPrefrenceConfig.getPrefString(context,Constance.CToken,null));

        call.enqueue(new Callback<VideoHomeModel>() {
            @Override
            public void onResponse(Call<VideoHomeModel> call, Response<VideoHomeModel> response) {
                if (response.body().getResult().equals("1") || response.body().getResult() != null)
                {
                    if (response.body().getRecords() != null)
                    {
                        if (response.body().getRecords().getData() != null && response.body().getRecords().getData().size()!=0)
                        {
                            Log.d("fdfsdkfk","dsfji"+response.body().getRecords().getData().size());
                            //Toast.makeText(context,"not null data ",Toast.LENGTH_LONG).show();
                            videoCategoriesDataArrayList.addAll(response.body().getRecords().getData());
                            adapterVideoList=new AdapterVideoList(context,videoCategoriesDataArrayList,"festivalviewall");
                            rv_viewalllist.setAdapter(adapterVideoList);
                            progressDialog.dismiss();
                        }
                        else
                        {


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
                        progressDialog.dismiss();
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


    public void bindView() {
        mAdView = findViewById(R.id.adview);
        facbook_ad_banner = findViewById(R.id.facbook_ad_banner);
        tv_viewalltittlename = findViewById(R.id.tv_viewalltittlename);
        rv_viewalllist = findViewById(R.id.rv_viewalllist);
        iv_backarrow = findViewById(R.id.iv_backarrow);
        iv_al_language = findViewById(R.id.iv_al_language);
        ll_select_list = findViewById(R.id.ll_select_list);
        tv_as_festival = findViewById(R.id.tv_as_festival);
        ll_as_festival = findViewById(R.id.ll_as_festival);
        ll_as_category = findViewById(R.id.ll_as_category);
        tv_as_category = findViewById(R.id.tv_as_category);
        rv_cat_alv = findViewById(R.id.rv_cat_alv);
        businessCatList=new ArrayList<>();
        greetingCatList=new ArrayList<>();
        videoCategoriesDataArrayList=new ArrayList<>();
    }
    public void loadAdMobAd() {

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                // Check the LogCat to get your test device ID
                .addTestDevice("C04B1BFFB0774708339BC273F8A43708")
                .build();

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdClosed() {
                // Toast.makeText(getApplicationContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Toast.makeText(getApplicationContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLeftApplication() {
                // Toast.makeText(getApplicationContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });

        mAdView.loadAd(adRequest);
    }

    public void loadFacebookAd() {

        mAdView.setVisibility(View.GONE);
        facbook_ad_banner.setVisibility(View.VISIBLE);
        com.facebook.ads.AdView adFaceView = new com.facebook.ads.AdView(context, getResources().getString(R.string.facebook_banner_id), AdSize.BANNER_HEIGHT_50);

        AdSettings.setDebugBuild(true);

        // Add the ad view to your activity layout
        facbook_ad_banner.addView(adFaceView);


        adFaceView.loadAd();

    }
}