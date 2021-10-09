package com.example.newfestivalpost.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.newfestivalpost.AnalyticsApplication;
import com.example.newfestivalpost.ModelRetrofit.ResponseLogin;
import com.example.newfestivalpost.R;
import com.example.newfestivalpost.Retrofit.Api;
import com.example.newfestivalpost.Retrofit.Base_Url;
import com.example.newfestivalpost.Utills.Constance;
import com.example.newfestivalpost.Utills.SharedPrefrenceConfig;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OTPActivity extends AppCompatActivity {


    Context context;
    EditText et_otp;
    String mobileNo;
    LinearLayout ll_submit_btn;
    Tracker mTracker;
    SharedPrefrenceConfig sharedprefconfig;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_p);

        context=OTPActivity.this;
        initOTP();
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        sharedprefconfig = new SharedPrefrenceConfig(context);
        if(getIntent().getExtras().getString("mobileNo")!=null){
            mobileNo=getIntent().getExtras().getString("mobileNo");
        }


        ll_submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Log.d("fjdkgj","fjfj "+mobileNo);
                Log.d("fjdkgj","fjfj "+et_otp.getText().toString());

                if(mobileNo!=null && et_otp.getText().toString()!=null){
                    sendOTP();
                }
                else {
                    Toast.makeText(context,"Mobile No or OTP is invalid.... ",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("Image~" + "Google Analytics Testing");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    void initOTP(){

        et_otp=findViewById(R.id.et_otp);
        ll_submit_btn=findViewById(R.id.ll_submit_btn);
    }

    public void sendOTP() {
        Api api = Base_Url.getClient().create(Api.class);

        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCancelable(false);

        Call<ResponseLogin> call = api.sendOTP(getIntent().getExtras().getString("mobileNo"), et_otp.getText().toString());

        call.enqueue(new Callback<ResponseLogin>() {
            @Override
            public void onResponse(Call<ResponseLogin> call, Response<ResponseLogin> response)
            {
                ResponseLogin loginResponse = response.body();
                if (loginResponse.getResult() != null && loginResponse.getResult().equals("1"))
                {
                    SharedPrefrenceConfig.savebooleanPreferance(context,"checkLogin",true);
                    sharedprefconfig.saveapitoken(loginResponse);
                    sharedprefconfig.saveStringPreferance(context, Constance.CToken,response.body().getApi_token());
                    sharedprefconfig.saveUser(loginResponse.getRecord());
                    progressDialog.dismiss();
                    Intent intent = new Intent(context, ActivityHome.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(response.body().getMessage())
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    // Constance.tv_login_logout="Logout";
                                }
                            });

                    //Creating dialog box
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                // progressBar.setVisibility(View.GONE);
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseLogin> call, Throwable t) {
                Toast.makeText(context,"Check Your Internet Connection !!!",Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });

    }

}