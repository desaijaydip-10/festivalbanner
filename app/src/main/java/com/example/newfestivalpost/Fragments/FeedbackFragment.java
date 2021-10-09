package com.example.newfestivalpost.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.newfestivalpost.Adapters.AdapterSingleCatList;
import com.example.newfestivalpost.ModelRetrofit.List_of_Categories_name;
import com.example.newfestivalpost.ModelRetrofit.UserRegister;
import com.example.newfestivalpost.R;
import com.example.newfestivalpost.Retrofit.Api;
import com.example.newfestivalpost.Retrofit.Base_Url;
import com.example.newfestivalpost.Utills.SharedPrefrenceConfig;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedbackFragment extends Fragment {


    View view;
    Context context;
    EditText et_f_name,et_f_contact,et_f_discription;
    SharedPrefrenceConfig sharedprefconfig;
    LinearLayout ll_f_send;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_feedback, container, false);
        context=getContext();
        initF();
        sharedprefconfig = new SharedPrefrenceConfig(context);
        et_f_name.setText(sharedprefconfig.getUser().getName());
        et_f_contact.setText(sharedprefconfig.getUser().getContact());
        et_f_discription.setMaxLines(10);
        ll_f_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFeedback();
            }
        });


        return  view;
    }

    void initF(){
        et_f_name=view.findViewById(R.id.et_f_name);
        et_f_contact=view.findViewById(R.id.et_f_contact);
        et_f_discription=view.findViewById(R.id.et_f_discription);
        ll_f_send=view.findViewById(R.id.ll_f_send);
    }

    public void sendFeedback()
    {
        Api api= Base_Url.getClient().create(Api.class);
        ProgressDialog progressDialog;

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        Call<UserRegister> call = api.feedback(sharedprefconfig.getapitoken().getApi_token(),et_f_name.getText().toString()
                ,et_f_contact.getText().toString(),et_f_discription.getText().toString());

        call.enqueue(new Callback<UserRegister>() {
            @Override
            public void onResponse(Call<UserRegister> call, Response<UserRegister> response) {

                if (response.body().getResult().equals("1") || response.body().getResult() != null)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(response.body().getMessage())
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    progressDialog.hide();

                                }
                            });

                    //Creating dialog box
                    AlertDialog alert = builder.create();
                    alert.show();
                    progressDialog.hide();

                }
                else {
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
                    progressDialog.dismiss();
                }

            }

            @Override
            public void onFailure(Call<UserRegister> call, Throwable t) {
                Toast.makeText(context,"check Your internet Connection",Toast.LENGTH_LONG).show();
                progressDialog.dismiss();

            }
        });

    }
}