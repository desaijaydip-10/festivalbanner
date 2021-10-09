package com.example.newfestivalpost.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.newfestivalpost.ModelRetrofit.ResponseLogin;
import com.example.newfestivalpost.R;
import com.example.newfestivalpost.Retrofit.Api;
import com.example.newfestivalpost.Retrofit.Base_Url;
import com.example.newfestivalpost.Utills.Constance;
import com.example.newfestivalpost.Utills.SharedPrefrenceConfig;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PremiumPreview extends AppCompatActivity {

    Context context;

    LinearLayout ll_whatsapp_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium_preview);

        context = PremiumPreview.this;
        initPP();
        ll_whatsapp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + "+918866253284" + "&text=" + ""));
                startActivity(intent);
            }
        });
    }

    void initPP() {
        ll_whatsapp_btn = findViewById(R.id.ll_whatsapp_btn);
    }


}