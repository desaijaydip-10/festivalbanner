package com.example.newfestivalpost.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.newfestivalpost.AnalyticsApplication;
import com.example.newfestivalpost.ModelRetrofit.UserRegister;
import com.example.newfestivalpost.ModelRetrofit.VideoHomeModel;
import com.example.newfestivalpost.R;
import com.example.newfestivalpost.Retrofit.Api;
import com.example.newfestivalpost.Retrofit.Base_Url;
import com.example.newfestivalpost.Utills.PathUtills;
import com.example.newfestivalpost.Utills.SharedPrefrenceConfig;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.net.URISyntaxException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivitySignUp extends AppCompatActivity {

    Context context;
    TextView et_city, et_password, et_email, et_username, et_business, et_contactno;
    String imagepath;
    SharedPrefrenceConfig sharedprefconfig;
    ImageView iv_profile_person;
    String[] permissionsRequired = {Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    private static final int PERMISSION_CALLBACK_CONSTANT = 200;
    Tracker mTracker;
    MultipartBody.Part image_multipart;
    String[] businessList;
    int index =1;
    Spinner s_business_list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        context = ActivitySignUp.this;

        sharedprefconfig = new SharedPrefrenceConfig(context);
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        et_username = findViewById(R.id.et_username);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        et_city = findViewById(R.id.et_city);
        et_business = findViewById(R.id.et_business);
        iv_profile_person = findViewById(R.id.iv_profile_person);
        et_contactno = findViewById(R.id.et_contactno);
        s_business_list = findViewById(R.id.s_business_list);
        businessList();
        iv_profile_person.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ActivityCompat.checkSelfPermission(context, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(context, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(context, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED) {
                    checkPermission();
                } else {
                    PickImageFromMobileGallery();
                }
            }
        });
    }

    public void PickImageFromMobileGallery() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start((Activity) context);

    }

    public void onClickSignUp(View view) {
        switch (view.getId()) {
            case R.id.ll_signup_btn:
                createuser();
                break;
            case R.id.tv_singnin:
                Intent tv_singnin = new Intent(context, ActivitySignIn.class);
                startActivity(tv_singnin);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("Image~" + "Google Analytics Testing");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void businessList() {
        Api api = Base_Url.getClient().create(Api.class);
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        Call<UserRegister> call = api.businessList();

        call.enqueue(new Callback<UserRegister>() {
            @Override
            public void onResponse(Call<UserRegister> call, Response<UserRegister> response) {

                if (response.body().getResult().equals("1") || response.body().getResult() != null) {
                    if (response.body().getRecords() != null) {
                        progressDialog.hide();
                        businessList = new String[response.body().getRecords().size()+1];
                        businessList[0]="select Business";
                        for (int i = 0; i < response.body().getRecords().size(); i++) {

                            businessList[index] = response.body().getRecords().get(i).getName();
                            index++;
                        }

                        bussList();

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
            public void onFailure(Call<UserRegister> call, Throwable t) {
                Toast.makeText(context, "check Your internet Connection", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();

            }
        });
    }

    void bussList() {


        ArrayAdapter bankArrayAdepter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, businessList);
        bankArrayAdepter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        s_business_list.setAdapter(bankArrayAdepter);
        s_business_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //Toast.makeText(getApplicationContext(), month[position], Toast.LENGTH_LONG).show();
                //month1 = String.valueOf(position + 1);
                // Toast.makeText(context,""+bankID[position]+bank[position],Toast.LENGTH_LONG).show();

            ((TextView) s_business_list.getSelectedView()).setTextSize(18);
              ((TextView) s_business_list.getSelectedView()).setTextColor(getResources().getColor(R.color.colorBlack));
                et_business.setText(businessList[position]);
                if(businessList[position].equals("select Business")){
                    //et_business.setHint("select Business");
                    ((TextView) s_business_list.getSelectedView()).setTextColor(getResources().getColor(R.color.colorLightGrey));

                }
                else {
                    et_business.setText(businessList[position]);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    public void createuser() {
        MultipartBody.Part multipart_creatuser;
       /* if (imagepath != null && !imagepath.equals(""))
        {
            File file = new File(imagepath);
            // Create a request body with file and image media type
            RequestBody rbImage = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            String FileName = file.getName();
            image_multipart = MultipartBody.Part.createFormData("image", FileName, rbImage);
            Log.d("checkimage","image_multipart :"+String.valueOf(image_multipart));

            //Log.e("file_name", String.valueOf(image_multipart));
        }
        else
            {
            Toast.makeText(context, "Image is empty", Toast.LENGTH_SHORT).show();
            // Create a request body with file and image media type
            RequestBody rbImage = RequestBody.create(MultipartBody.FORM, "");
            // Create MultipartBody.Part using file request-body,file name and part name
            image_multipart = MultipartBody.Part.createFormData("image", "", rbImage);
        }*/
        if (imagepath != null && !imagepath.equals("")) {
            File file = new File(imagepath);
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file);
            multipart_creatuser = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        } else {
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), "");
            multipart_creatuser = MultipartBody.Part.createFormData("image", "", requestFile);

        }

        Log.d("djsfsjf", "dfjsj" + et_contactno.getText().toString());

        RequestBody rbname = RequestBody.create(MediaType.parse("text/plain"), et_username.getText().toString());
        RequestBody rbcity = RequestBody.create(MediaType.parse("text/plain"), et_city.getText().toString());
        RequestBody rbbusiness = RequestBody.create(MediaType.parse("text/plain"), et_business.getText().toString());
        RequestBody rbcontactNo = RequestBody.create(MediaType.parse("text/plain"), et_contactno.getText().toString());
        RequestBody rbpassword = RequestBody.create(MediaType.parse("text/plain"), et_password.getText().toString());
        RequestBody rbemail = RequestBody.create(MediaType.parse("text/plain"), et_email.getText().toString());

        Api api = Base_Url.getClient().create(Api.class);
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCancelable(false);
        Call<UserRegister> call = api.signUp(rbname, rbemail, rbpassword, rbcity, multipart_creatuser, rbbusiness, rbcontactNo);

        call.enqueue(new Callback<UserRegister>() {
            @Override
            public void onResponse(Call<UserRegister> call, Response<UserRegister> response) {
                UserRegister userResponse = response.body();
                if (userResponse.getResult() != null && userResponse.getResult().equals("1")) {
                    if (userResponse.getRecord() != null) {
                        Toast.makeText(context, "" + response.body().getMessage(), Toast.LENGTH_LONG).show();
                        // sharedprefconfig.saveUser(userResponse.getRecord());
                        Intent i = new Intent(context, ActivitySignIn.class);
                        startActivity(i);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage(response.body().getMessage())
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }

                } else {
                    Toast.makeText(context, "" + response.body().getMessage(), Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();

            }

            @Override
            public void onFailure(Call<UserRegister> call, Throwable t) {
                progressDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Check Your Internet Connection" + t)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    public void checkPermission() {

        if (ActivityCompat.checkSelfPermission(context, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ActivitySignUp.this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(ActivitySignUp.this, permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(ActivitySignUp.this, permissionsRequired[2])) {
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(ActivitySignUp.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        onBackPressed();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(ActivitySignUp.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("abc", "createquote");
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    imagepath = PathUtills.getPath(context, resultUri);
                    Glide.with(context).load(imagepath).into(iv_profile_person);

                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }


    }


}