package com.example.newfestivalpost.Fragments;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.newfestivalpost.Activities.ActivityHome;
import com.example.newfestivalpost.Activities.ActivitySignIn;
import com.example.newfestivalpost.ModelRetrofit.CompanyDetails;
import com.example.newfestivalpost.ModelRetrofit.ResponseLogin;
import com.example.newfestivalpost.ModelRetrofit.UserRegister;
import com.example.newfestivalpost.R;
import com.example.newfestivalpost.Retrofit.Api;
import com.example.newfestivalpost.Retrofit.Base_Url;
import com.example.newfestivalpost.Utills.Constance;
import com.example.newfestivalpost.Utills.PathUtills;
import com.example.newfestivalpost.Utills.SharedPrefrenceConfig;
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

import static android.app.Activity.RESULT_OK;

public class FragmentAddBusiness extends Fragment {

    Context context;
    View view;
    Tracker mTracker;
    LinearLayout ll_logo,ll_finish_btn,ll_update_btn;
    public ImageView iv_selectedlogo;
    EditText et_businessaddress,et_businesswebsite,et_businessemail,et_businessmobile,et_businessname;
    String[] permissionsRequired = {Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    SharedPrefrenceConfig sharedprefconfig;

    MultipartBody.Part multipart_image,multipart_updateimage;
    String imagepath;
    String companyid;
    private static final int PERMISSION_CALLBACK_CONSTANT = 200;
    public static FragmentAddBusiness instance = null;

    private final String TAG = FragmentAddBusiness.class.getSimpleName();
    public FragmentAddBusiness() {
        instance = FragmentAddBusiness.this;
    }

    public static synchronized FragmentAddBusiness getInstance() {
        if (instance == null) {
            instance = new FragmentAddBusiness();
        }
        return instance;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_addbusiness, container, false);
        context = getContext();
        bindView();
        sharedprefconfig = new SharedPrefrenceConfig(context);
        Constance.checkFragment="Add_Business";
        hideKeyboard(getActivity());
        ll_logo.setOnClickListener(new View.OnClickListener() {
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
        ll_finish_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addBusiness();
            }
        });
        ll_update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateBusiness();
            }
        });
        getbusinessdetail();
        return view;
    }
    public void bindView()
    {
        ll_logo=view.findViewById(R.id.ll_logo);
        iv_selectedlogo=view.findViewById(R.id.iv_selectedlogo);
        ll_finish_btn=view.findViewById(R.id.ll_finish_btn);
        et_businessaddress=view.findViewById(R.id.et_businessaddress);
        et_businesswebsite=view.findViewById(R.id.et_businesswebsite);
        et_businessemail=view.findViewById(R.id.et_businessemail);
        et_businessmobile=view.findViewById(R.id.et_businessmobile);
        et_businessname=view.findViewById(R.id.et_businessname);
        ll_update_btn=view.findViewById(R.id.ll_update_btn);

    }
    public void UpdateBusiness()
    {
        MultipartBody.Part multipart_updateimage;
        if(imagepath != null && !imagepath.equals(""))
        {
            //pass it like this
            File file = new File(imagepath);
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file);

          multipart_updateimage =
                    MultipartBody.Part.createFormData("logo", file.getName(), requestFile);

        }else
        {
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), "");

            multipart_updateimage =
                    MultipartBody.Part.createFormData("logo", "", requestFile);

        }
           /*if (imagepath != null && !imagepath.equals("")) {
            File file = new File(imagepath);
            // Create a request body with file and image media type
            RequestBody rbImage = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            String FileName = file.getName();
            multipart_updateimage = MultipartBody.Part.createFormData("image", FileName, rbImage);
            Log.d("checkimage","image_multipart :"+String.valueOf(multipart_image));

            //Log.e("file_name", String.valueOf(image_multipart));
        }
        else {
            Toast.makeText(context, "Image Not Update", Toast.LENGTH_SHORT).show();
            // Create a request body with file and image media type
            RequestBody rbImage = RequestBody.create(MultipartBody.FORM, "");
            // Create MultipartBody.Part using file request-body,file name and part name
            multipart_updateimage = MultipartBody.Part.createFormData("image", "", rbImage);
        }*/
        RequestBody rbToken = RequestBody.create(MediaType.parse("text/plain"), sharedprefconfig.getapitoken().getApi_token());

        RequestBody rbname = RequestBody.create(MediaType.parse("text/plain"), et_businessname.getText().toString());
        RequestBody rbaddress = RequestBody.create(MediaType.parse("text/plain"), et_businessaddress.getText().toString());
        RequestBody rbmobile = RequestBody.create(MediaType.parse("text/plain"), et_businessmobile.getText().toString());
        RequestBody rbemail = RequestBody.create(MediaType.parse("text/plain"), et_businessemail.getText().toString());
        RequestBody rbwebsite = RequestBody.create(MediaType.parse("text/plain"), et_businesswebsite.getText().toString());
        RequestBody rbcompanyid = RequestBody.create(MediaType.parse("text/plain"), companyid);

        Api api= Base_Url.getClient().create(Api.class);
        ProgressDialog progressDialog;

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        Log.d("companyid",""+companyid);
        Call<CompanyDetails> call=api.updateBusinessDetails(rbToken,rbname,rbemail,multipart_updateimage,rbaddress,rbmobile,rbcompanyid,rbwebsite) ;

        //Call<CompanyDetails> call=api.updateBusinessDetails(sharedprefconfig.getapitoken().getApi_token(),et_businessname.getText().toString(),et_businessemail.getText().toString(),et_businessaddress.getText().toString(),et_businessmobile.getText().toString(),imagepath,companyid) ;
        Log.d("getLogo","UpdateBusiness imagepath: "+imagepath);

       // Log.d("checkimagepath","UpdateBusiness : "+imagepath);
        call.enqueue(new Callback<CompanyDetails>()
        {
            @Override
            public void onResponse(Call<CompanyDetails> call, Response<CompanyDetails> response)
            {
                CompanyDetails companyDetails = response.body();
                Log.d("userresponse",""+companyDetails);
                if (companyDetails.getResult().equals("1"))
                {
                    if (companyDetails.getRecord() != null)
                    {
                        Log.d("getLogo","UpdateBusiness : "+companyDetails.getRecord().getLogo());
                        Log.d("getLogo","UpdateBusiness url : "+companyDetails.getRecord().getLogo_url());
                        Log.d("getLogo","UpdateBusiness Name  : "+companyDetails.getRecord().getName());

                        Toast.makeText(context, "" + response.body().getMessage(), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                        getbusinessdetail();
                       /* Intent i=new Intent(context, ActivityHome.class);
                        startActivity(i);*/
                    }
                    else {
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

                }
                else
                {
                    Toast.makeText(context, "" + response.body().getMessage(), Toast.LENGTH_LONG).show();
                }

                progressDialog.dismiss();
            }
            @Override
            public void onFailure(Call<CompanyDetails> call, Throwable t) {
                progressDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Check Your Internet Connection"+t)
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
    public void getbusinessdetail() {
        Api api = Base_Url.getClient().create(Api.class);
        //Log.d("checkimagepath","getbusinessdetail : "+imagepath);
        Log.d("getLogo","getbusinessdetail imagepath: "+imagepath);

        Call<ResponseLogin> call = api.getBusinessDetails(sharedprefconfig.getapitoken().getApi_token());
        call.enqueue(new Callback<ResponseLogin>() {
            @Override
            public void onResponse(Call<ResponseLogin> call, Response<ResponseLogin> response) {
                ResponseLogin getprofile = response.body();
                if (getprofile.getResult() != null && getprofile.getResult().equals("1"))
                {
                    if (getprofile.getRecord() != null)
                    {
                        if(getprofile.getRecord().getCompany()!=null)
                        {
                            et_businessemail.setText("" + getprofile.getRecord().getCompany().getEmail());
                            et_businessname.setText("" + getprofile.getRecord().getCompany().getName());
                            et_businessmobile.setText("" + getprofile.getRecord().getCompany().getContact());
                            et_businessaddress.setText("" + getprofile.getRecord().getCompany().getAddress());
                            et_businesswebsite.setText("" + getprofile.getRecord().getCompany().getWebsite());
                            companyid=getprofile.getRecord().getCompany().getId();
                            RequestOptions requestOptions = new RequestOptions();
                            requestOptions.placeholder(R.drawable.userholder);
                            requestOptions.error(R.drawable.userholder);
                            Glide.with(context).load(getprofile.getRecord().getCompany().getLogo_url()).apply(requestOptions).into(iv_selectedlogo);
                            Log.d("getLogo","getbusinessdetail"+getprofile.getRecord().getCompany().getLogo());
                            Log.d("getLogo","getbusinessdetail url"+getprofile.getRecord().getCompany().getLogo_url());
                            ll_update_btn.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("Business Details Not Added")
                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            ll_update_btn.setVisibility(View.GONE);
                                            ll_finish_btn.setVisibility(View.VISIBLE);

                                            et_businessemail.setText("");
                                            et_businessname.setText("");
                                            et_businessmobile.setText("");
                                            et_businessaddress.setText("");
                                            Glide.with(context).load(R.drawable.userholder).into(iv_selectedlogo);

                                        }
                                    });

                            //Creating dialog box
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                        }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("record null : " + response.body().getMessage())
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        et_businessemail.setText("");
                                        et_businessname.setText("");
                                        et_businessmobile.setText("");
                                        et_businessaddress.setText("");
                                        Glide.with(context).load(R.drawable.userholder).into(iv_selectedlogo);

                                    }
                                });

                        //Creating dialog box
                        AlertDialog alert = builder.create();
                        alert.show();
                    }

                }
                else {
                    Intent i=new Intent(context, ActivitySignIn.class);
                    startActivity(i);
                    ActivityHome.getInstance().finish();
                    /*AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(response.body().getMessage())
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    et_businessemail.setText("");
                                    et_businessname.setText("");
                                    et_businessmobile.setText("");
                                    et_businessaddress.setText("");
                                    Glide.with(context).load(R.drawable.userholder).into(iv_selectedlogo);

                                }
                            });

                    //Creating dialog box
                    AlertDialog alert = builder.create();
                    alert.show();*/
                }

            }

            @Override
            public void onFailure(Call<ResponseLogin> call, Throwable t) {
                Toast.makeText(getContext(), "check Network Connection", Toast.LENGTH_LONG).show();
            }
        });
    }
    public void PickImageFromMobileGallery() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(getActivity());

    }

    public void addBusiness()
    {
        MultipartBody.Part multipart_addimage;
        if(imagepath != null && !imagepath.equals(""))
        {
            File file = new File(imagepath);
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file);
            multipart_addimage =
                    MultipartBody.Part.createFormData("logo", file.getName(), requestFile);

        }else
        {
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), "");
            multipart_addimage =
                    MultipartBody.Part.createFormData("logo", "", requestFile);

        }
       /* if (imagepath != null && !imagepath.equals("")) {
            File file = new File(imagepath);
            // Create a request body with file and image media type
            RequestBody rbImage = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            String FileName = file.getName();
            multipart_image = MultipartBody.Part.createFormData("image", FileName, rbImage);
            Log.d("checkimage","image_multipart :"+String.valueOf(multipart_image));

            //Log.e("file_name", String.valueOf(image_multipart));
        }
        else {
            Toast.makeText(context, "Image Not Update", Toast.LENGTH_SHORT).show();
            // Create a request body with file and image media type
            RequestBody rbImage = RequestBody.create(MultipartBody.FORM, "");
            // Create MultipartBody.Part using file request-body,file name and part name
            multipart_image = MultipartBody.Part.createFormData("image", "", rbImage);
        }*/
        //pass it like this
              RequestBody rbToken = RequestBody.create(MediaType.parse("text/plain"), sharedprefconfig.getapitoken().getApi_token());

        RequestBody rbname = RequestBody.create(MediaType.parse("text/plain"), et_businessname.getText().toString());
        RequestBody rbaddress = RequestBody.create(MediaType.parse("text/plain"), et_businessaddress.getText().toString());
        RequestBody rbmobile = RequestBody.create(MediaType.parse("text/plain"), et_businessmobile.getText().toString());
        RequestBody rbemail = RequestBody.create(MediaType.parse("text/plain"), et_businessemail.getText().toString());
        RequestBody rbwebsite = RequestBody.create(MediaType.parse("text/plain"), et_businesswebsite.getText().toString());

        Api api= Base_Url.getClient().create(Api.class);
        ProgressDialog progressDialog;

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
       // Log.d("getLogo","addbusiness imagepath: "+imagepath);

       Call<CompanyDetails> call=api.addBusinessDetails(rbToken,rbname,rbemail,multipart_addimage,rbaddress,rbmobile,rbwebsite) ;
        //Call<CompanyDetails> call=api.addBusinessDetails(sharedprefconfig.getapitoken().getApi_token(),et_businessname.getText().toString(),et_businessemail.getText().toString(),et_businessaddress.getText().toString(),et_businessmobile.getText().toString(),imagepath) ;

        call.enqueue(new Callback<CompanyDetails>()
        {
            @Override
            public void onResponse(Call<CompanyDetails> call, Response<CompanyDetails> response)
            {
                CompanyDetails companyDetails = response.body();
               // Log.d("userresponse",""+companyDetails);
                if (companyDetails.getResult().equals("1"))
                {
                    if (companyDetails.getRecord() != null)
                    {
                       /* Log.d("getLogo","add business : "+companyDetails.getRecord().getLogo());
                        Log.d("getLogo","add business url : "+companyDetails.getRecord().getLogo_url());
                     */   Toast.makeText(context, "" + response.body().getMessage(), Toast.LENGTH_LONG).show();
                       // sharedprefconfig.saveCompanyDetails(companyDetails.getRecord());
                        progressDialog.dismiss();
                        ll_update_btn.setVisibility(View.VISIBLE);
                        ll_finish_btn.setVisibility(View.GONE);
                        getbusinessdetail();
                       /* Intent i=new Intent(context, ActivityHome.class);
                        startActivity(i);*/
                    }
                    else
                        {
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

                }
                else
                {
                    Toast.makeText(context, "" + response.body().getMessage(), Toast.LENGTH_LONG).show();
                }

                progressDialog.dismiss();
            }
            @Override
            public void onFailure(Call<CompanyDetails> call, Throwable t) {
                progressDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Check Your Internet Connection"+t)
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("abc", "createquote");
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
               /* //iv_profile_person_myprofile.setImageURI(resultUri);
                iv_selectedlogo.setImageURI(resultUri);*/

                try {
                    imagepath= PathUtills.getPath(context,resultUri);
                    Glide.with(context).load(imagepath).into(iv_selectedlogo);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    public void checkPermission() {

        if (ActivityCompat.checkSelfPermission(context, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permissionsRequired[2])) {
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(getActivity(), permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        getActivity().onBackPressed();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(getActivity(), permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            }
        }
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View currentFocusedView = activity.getCurrentFocus();
        if (currentFocusedView != null) {
            inputManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
