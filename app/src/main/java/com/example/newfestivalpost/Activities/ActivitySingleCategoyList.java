package com.example.newfestivalpost.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.newfestivalpost.Adapters.AdapterCategoryList;
import com.example.newfestivalpost.Adapters.AdapterSingleCatList;
import com.example.newfestivalpost.Adapters.BusinessListAdapter;
import com.example.newfestivalpost.Adapters.GreetingListAdapter;
import com.example.newfestivalpost.AnalyticsApplication;
import com.example.newfestivalpost.ModelRetrofit.CategoriesData;
import com.example.newfestivalpost.ModelRetrofit.List_of_Categories_name;
import com.example.newfestivalpost.ModelRetrofit.ResponseLogin;
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

public class ActivitySingleCategoyList extends AppCompatActivity {

    Context context;
    AdView mAdView;
    LinearLayout facbook_ad_banner;
    RecyclerView rv_singlecatlist;
    TextView tv_singlecatname;
    String childitemtittle, catnameid, catType;
    ImageView iv_backarrow, iv_firstimage, iv_al_language;
    // ArrayList<ModelHomeChild> modelsingleChildList;
    ArrayList<CategoriesData> modelsingleChildList;
    ArrayList<VideoHomeData> businessList, greetingImage;
    public AdapterSingleCatList adapterSingleCatList;
    public BusinessListAdapter businessListAdapter;
    public GreetingListAdapter greetingListAdapter;
    LinearLayout ll_next_singlecatlist;
    public static String finalimage;
    public static ActivitySingleCategoyList instance = null;
    SharedPrefrenceConfig sharedPrefrenceConfig;
    LinearLayout ll_selectedimageview;
    Tracker mTracker;

    String language;

    public PopupWindowHelper popupWindowHelper;
    View popupview_down;
    String frameName[] = {"All", "English", "Hindi", "Gujarati"};

    public ActivitySingleCategoyList() {
        instance = ActivitySingleCategoyList.this;
    }

    public static synchronized ActivitySingleCategoyList getInstance() {
        if (instance == null) {
            instance = new ActivitySingleCategoyList();
        }
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_categoy_list);
        context = ActivitySingleCategoyList.this;
        bindView();
        sharedPrefrenceConfig = new SharedPrefrenceConfig(context);
        language = sharedPrefrenceConfig.getPrefString(context, Constance.language, "");
        calculationForHeight();
        if (Constance.adType.equals("Ad Mob")) {
            loadAdMobAd();

        } else {
            loadFacebookAd();
        }

        getprofiledetails();
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        childitemtittle = getIntent().getExtras().getString("childitemtittle");
        catType = getIntent().getExtras().getString("catType");
        catnameid = getIntent().getExtras().getString("catnameid");
        Log.d("fdfjsfi", "jfdsfisf" + catnameid);

        tv_singlecatname.setText(childitemtittle);
        rv_singlecatlist.setLayoutManager(new GridLayoutManager(context, 3));

        modelsingleChildList = new ArrayList<>();

        if (catType.equals("festival")) {
            getcatimagelist();
        }

        if (catType.equals("business")) {
            getBusinesslist();
        }


        if (catType.equals("greeting")) {
            getGreetinImageslist();
        }

        // modelsingleChildList.clear();
        // modelsingleChildList.addAll(Constance.childDataList);

       /* adapterSingleCatList = new AdapterSingleCatList(context, modelsingleChildList);
        rv_singlecatlist.setAdapter(adapterSingleCatList);*/

        //   iv_firstimage.setImageResource(modelsingleChildList.get(0).getChilditemimage());
        //  finalimage = modelsingleChildList.get(0).getChilditemimage();

        iv_backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        ll_next_singlecatlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constance.ComeFrom = "image";
                Constance.FromSinglecatActivity = finalimage;
                Log.d("sdasjdiaisdi", "sdhjsd" + Constance.FromSinglecatActivity);
                if (sharedPrefrenceConfig.getPrefBoolean(context, "checkLogin", false)) {

                    Log.d("dfdjfjdfj", "dfuidufiu" + sharedPrefrenceConfig.getPrefString(context, Constance.Status, ""));
                    if (sharedPrefrenceConfig.getPrefString(context, Constance.Status, "").equals("Active")) {
                        Constance.activityName = catType;
                        Intent i = new Intent(context, ActivityCreatePost.class);
                        startActivity(i);
                    } else {
                        sharedPrefrenceConfig.saveStringPreferance(context, Constance.CToken, "");
                        sharedPrefrenceConfig.savebooleanPreferance(context, "checkLogin", false);

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("your token expired so try to login........ ")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        dialog.cancel();
                                    }
                                });

                        //Creating dialog box
                        AlertDialog alert = builder.create();
                        alert.show();

                    }

            /*    i.putExtra("FromSinglecatActivity", finalimage);
                i.putExtra("ComeFrom", "image");
*/

                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("you need a account to continue ... ")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    Intent i = new Intent(context, ActivitySignIn.class);
                                    startActivity(i);
                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();

                                }
                            });
                    //Creating dialog box
                    AlertDialog alert = builder.create();
                    alert.show();


                }

                // ActivityCreateCustomImage.getInstance().setbackgroundSingleImageResource(finalimage);
            }
        });

        iv_al_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                popupview_down = LayoutInflater.from(context).inflate(R.layout.category_dropdown_raw, null);
                popupWindowHelper = new PopupWindowHelper(popupview_down);
                popupWindowHelper.showAsDropDown(v, 0, 0);
                RecyclerView rv_cat_dd_raw = popupview_down.findViewById(R.id.rv_cat_dd_raw);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
                rv_cat_dd_raw.setLayoutManager(gridLayoutManager);
                rv_cat_dd_raw.setHasFixedSize(true);

                AdapterCategoryList adapterCategory = new AdapterCategoryList(context, frameName, "allList");
                rv_cat_dd_raw.setAdapter(adapterCategory);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("fhauhfdsfsdf", "dhasufhuashn");
        mTracker.setScreenName("Image~" + "Google Analytics Testing");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


    public void loadList() {

        language = sharedPrefrenceConfig.getPrefString(context, Constance.language, "");
        if (catType.equals("festival")) {
            getcatimagelist();
        }

        if (catType.equals("business")) {
            getBusinesslist();
        }
        if (catType.equals("greeting")) {
            getGreetinImageslist();
        }
    }

    public void getprofiledetails() {
        Api api = Base_Url.getClient().create(Api.class);
        Call<ResponseLogin> call = api.getProfileDetails(sharedPrefrenceConfig.getPrefString(context, Constance.CToken, null));
        call.enqueue(new Callback<ResponseLogin>() {
            @Override
            public void onResponse(Call<ResponseLogin> call, Response<ResponseLogin> response) {
                ResponseLogin getprofile = response.body();
                if (getprofile.getResult() != null && getprofile.getResult().equals("1")) {
                    if (getprofile.getRecord() != null) {
                        sharedPrefrenceConfig.saveStringPreferance(context, Constance.Status, response.body().getRecord().getStatus());

                        Log.d("dfdjfjdfj", "dfuidufiu" + sharedPrefrenceConfig.getPrefString(context, Constance.Status, ""));
                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("record null : " + response.body().getMessage())
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
                    builder.setMessage(response.body().getMessage())
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

            @Override
            public void onFailure(Call<ResponseLogin> call, Throwable t) {
                Toast.makeText(context, "check Network Connection", Toast.LENGTH_LONG).show();
            }
        });
    }


    public void bindView() {

        mAdView = findViewById(R.id.adview);
        facbook_ad_banner = findViewById(R.id.facbook_ad_banner);
        rv_singlecatlist = findViewById(R.id.rv_singlecatlist);
        tv_singlecatname = findViewById(R.id.tv_singlecatname);
        iv_backarrow = findViewById(R.id.iv_backarrow);
        iv_firstimage = findViewById(R.id.iv_firstimage);
        ll_next_singlecatlist = findViewById(R.id.ll_next_singlecatlist);
        ll_selectedimageview = findViewById(R.id.ll_selectedimageview);
        iv_al_language = findViewById(R.id.iv_al_language);
        businessList = new ArrayList<>();
        greetingImage = new ArrayList<>();

    }

    public void getcatimagelist() {
        modelsingleChildList.clear();
        Log.d("check_apitoken", "1" + sharedPrefrenceConfig.getapitoken().getApi_token());
        Api api = Base_Url.getClient().create(Api.class);
        ProgressDialog progressDialog;

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        Call<List_of_Categories_name> call = api.getCategoriesImageList(sharedPrefrenceConfig.getPrefString(context, Constance.CToken, null), catnameid, language);

        call.enqueue(new Callback<List_of_Categories_name>() {
            @Override
            public void onResponse(Call<List_of_Categories_name> call, Response<List_of_Categories_name> response) {

                if (response.body().getResult().equals("1") || response.body().getResult() != null) {
                    if (response.body().getRecords() != null) {

                        if (response.body().getRecords().getData() != null) {

                            // CategoriesRecord record = response.body().getRecords();

                            modelsingleChildList.addAll(response.body().getRecords().getData());

                            adapterSingleCatList = new AdapterSingleCatList(context, modelsingleChildList);
                            rv_singlecatlist.setAdapter(adapterSingleCatList);
                            if (modelsingleChildList.size() != 0) {
                                Glide.with(context).load(modelsingleChildList.get(0).getImage_url()).placeholder(R.drawable.placeholder).into(iv_firstimage);
                                finalimage = modelsingleChildList.get(0).getImage_url();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage("Items Coming soon....")
                                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                                onBackPressed();
                                            }
                                        });

                                //Creating dialog box
                                AlertDialog alert = builder.create();
                                alert.show();
                            }

                            progressDialog.dismiss();


                        } else {
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
            public void onFailure(Call<List_of_Categories_name> call, Throwable t) {
                Toast.makeText(context, "check Your internet Connection", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();

            }
        });

    }

    public void getBusinesslist() {
        businessList.clear();
        Api api = Base_Url.getClient().create(Api.class);
        ProgressDialog progressDialog;

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        Call<VideoHomeModel> call = api.getBusinessList(sharedPrefrenceConfig.getPrefString(context, Constance.CToken, null), catnameid, language);

        call.enqueue(new Callback<VideoHomeModel>() {
            @Override
            public void onResponse(Call<VideoHomeModel> call, Response<VideoHomeModel> response) {

                if (response.body().getResult().equals("1") || response.body().getResult() != null) {
                    if (response.body().getRecords() != null) {

                        if (response.body().getRecords().getData() != null) {

                            // CategoriesRecord record = response.body().getRecords();

                            businessList.addAll(response.body().getRecords().getData());

                            businessListAdapter = new BusinessListAdapter(context, businessList);
                            rv_singlecatlist.setAdapter(businessListAdapter);
                            if (businessList.size() != 0) {
                                Glide.with(context).load(businessList.get(0).getImage_url()).placeholder(R.drawable.placeholder).into(iv_firstimage);
                                finalimage = businessList.get(0).getImage_url();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage("Items Coming soon....")
                                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                                onBackPressed();
                                            }
                                        });

                                //Creating dialog box
                                AlertDialog alert = builder.create();
                                alert.show();
                            }

                            progressDialog.dismiss();


                        } else {
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

    public void getGreetinImageslist() {
        Log.d("fdfjsfi", "jfdsfisf" + catnameid);

        greetingImage.clear();
        Api api = Base_Url.getClient().create(Api.class);
        ProgressDialog progressDialog;

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        Call<VideoHomeModel> call = api.getGreetingImageList(sharedPrefrenceConfig.getPrefString(context, Constance.CToken, null), catnameid, language);

        call.enqueue(new Callback<VideoHomeModel>() {
            @Override
            public void onResponse(Call<VideoHomeModel> call, Response<VideoHomeModel> response) {

                if (response.body().getResult().equals("1") || response.body().getResult() != null) {
                    if (response.body().getRecords() != null) {

                        if (response.body().getRecords().getData() != null) {

                            // CategoriesRecord record = response.body().getRecords();

                            greetingImage.addAll(response.body().getRecords().getData());

                            greetingListAdapter = new GreetingListAdapter(context, greetingImage);
                            rv_singlecatlist.setAdapter(greetingListAdapter);
                            if (greetingImage.size() != 0) {
                                Glide.with(context).load(greetingImage.get(0).getImage_url()).placeholder(R.drawable.placeholder).into(iv_firstimage);
                                finalimage = greetingImage.get(0).getImage_url();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage("Items Coming soon....")
                                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                                onBackPressed();
                                            }
                                        });

                                //Creating dialog box
                                AlertDialog alert = builder.create();
                                alert.show();
                            }

                            progressDialog.dismiss();


                        } else {
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

    public void setselectedimage(String childitemimage) {
        finalimage = childitemimage;
        Glide.with(context).load(childitemimage).placeholder(R.drawable.placeholder).into(iv_firstimage);
        //  iv_firstimage.setImageResource(childitemimage);
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

    public void calculationForHeight() {
        ViewTreeObserver vto = ll_selectedimageview.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    ll_selectedimageview.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    ll_selectedimageview.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                int heightOfImage;
                int widthOfImage;
                widthOfImage = ll_selectedimageview.getMeasuredWidth();//1080 horizontalview
                heightOfImage = ll_selectedimageview.getMeasuredHeight();//236

                ViewGroup.LayoutParams params = ll_selectedimageview.getLayoutParams();
                params.height = widthOfImage;
                params.width = widthOfImage;
                ll_selectedimageview.setLayoutParams(params);
            }
        });

       /* // displayheight
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        displayMetrics_height = displayMetrics.heightPixels;
        int displayMetrics_width = displayMetrics.widthPixels;
        Log.d("mGLTextureView","displayMetrics_height :"+displayMetrics_height);
        Log.d("mGLTextureView","displayMetrics_width :"+displayMetrics_width);

        //linearlayout view
        ViewTreeObserver vto = mGLTextureView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    ll.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    ll.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                int width  = mGLTextureView.getMeasuredWidth();//1080 horizontalview
                videoview_height = mGLTextureView.getMeasuredHeight();//236
                Log.d("mGLTextureView","videoview_height :"+videoview_height);
                //Log.d("mGLTextureView","displaywidth :"+width);

            }
        });

        //horizontal view
        ViewTreeObserver vto1 = horizontal.getViewTreeObserver();
        vto1.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    horizontal.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    horizontal.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                int width  = horizontal.getMeasuredWidth();//1080 horizontalview
                 horizontal_viewheight = horizontal.getMeasuredHeight();//236
                Log.d("mGLTextureView","horizontal_viewheight :"+ horizontal_viewheight);
                //Log.d("mGLTextureView","displaywidth :"+width);

            }
        });

        int plus=dpToPx(50)+dpToPx(80)+ dpToPx(50)+dpToPx(14)+dpToPx(14);
        int finalplus=horizontal_viewheight+plus;
        Log.d("mGLTextureView","plus :"+plus);

        int minus=displayMetrics_height-finalplus;
       // int lastminus=minus-StatusBar_height;
        int lastminus=minus-statusBarHeight(getResources());
        Constance.heightOfVideo=lastminus;

        Log.d("mGLTextureView","minus :"+minus);
        Log.d("mGLTextureView","lastminus :"+lastminus);
*/
    }

}