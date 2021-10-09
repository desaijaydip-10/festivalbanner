package com.example.newfestivalpost.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.newfestivalpost.R;

public class PremiumActivity extends AppCompatActivity {

    Context context;
    TextView tv_p_send;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium);

        context=PremiumActivity.this;
        initP();

        tv_p_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(context,PremiumPreview.class);
                startActivity(i);
                finish();
            }
        });
    }

    void initP(){
        tv_p_send=findViewById(R.id.tv_p_send);
    }
}