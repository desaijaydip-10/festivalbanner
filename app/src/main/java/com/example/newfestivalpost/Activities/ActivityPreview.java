package com.example.newfestivalpost.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.example.newfestivalpost.AnalyticsApplication;
import com.example.newfestivalpost.MainActivity;
import com.example.newfestivalpost.R;
import com.example.newfestivalpost.Utills.Constance;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class ActivityPreview extends AppCompatActivity {

    Context context;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    Tracker mTracker;
    AdView mAdView;
    LinearLayout facbook_ad_banner;
    ImageView iv_preview, iv_vp_play;
    ProgressDialog dialog;
    Bitmap bitmappreviewsave;
    String filePath = "";
    Date currentTime;
    boolean isForShareGlobal;
    RelativeLayout rl_video;
    String name;
    MediaController mediacontroller;
    ProgressDialog pDialog,pDialog1;
    VideoView vv_videoshow;
    boolean isShare = false;

    File oldVideo,oldFrame;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        context = ActivityPreview.this;
        bindView();
        Constance.name_type = getIntent().getExtras().getString("name");
        name = Constance.name_type;
        if (Constance.adType.equals("Ad Mob")) {
            loadAdMobAd();

        } else {
            loadFacebookAd();
        }

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        if (Constance.adType.equals("Ad Mob")) {
            interstitialAdMobAd();
            Log.d("ADssss", "Ad Mob");
        } else {
            interstitialFacbookAd();
            Log.d("ADssss", "Facebook");
        }

        requestPermission();

        if (name.equals("image")) {
            vv_videoshow.setVisibility(View.GONE);
            iv_vp_play.setVisibility(View.GONE);
            iv_preview.setVisibility(View.VISIBLE);
            iv_preview.setImageBitmap(Constance.createdBitmap);
        } else {
            rl_video.setVisibility(View.VISIBLE);
            iv_preview.setVisibility(View.VISIBLE);
            vv_videoshow.setVisibility(View.VISIBLE);
            iv_vp_play.setVisibility(View.VISIBLE);
            playVideo();
            iv_preview.setImageBitmap(Constance.createdBitmap);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("Image~" + "Google Analytics Testing");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        if(isShare){
            vv_videoshow.setVideoPath(filePath);

        }
    }

    public void playVideo() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Buffering...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
        iv_vp_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog.dismiss();
                Log.d("wseqwqwqwqwqwqwqw", "sdss");
                vv_videoshow.start();
                iv_vp_play.setVisibility(View.GONE);
            }
        });
      /*  try {
            mediacontroller = new MediaController(this);
            mediacontroller.setAnchorView(vv_playvideo);
            vv_playvideo.setMediaController(mediacontroller);
            vv_playvideo.setVideoPath(videoCategoriesDataArrayList.get(0).getVideo_url());

        } catch (Exception e) {
            e.printStackTrace();
        }*/
        Log.d("sdhsh","sdhs"+Constance.savedVideoPath);
        if (Constance.savedVideoPath != null) {
            vv_videoshow.setVideoPath(Constance.savedVideoPath);
        } else {
            Toast.makeText(context, "Not get the video path", Toast.LENGTH_LONG).show();
        }
        mediacontroller = new MediaController(context);
        mediacontroller.setMediaPlayer(vv_videoshow);
        vv_videoshow.setMediaController(mediacontroller);
        vv_videoshow.requestFocus();
        //  vv_playvideo.start();
        vv_videoshow.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                pDialog.dismiss();
                //vv_playvideo.start();
            }
        });
        vv_videoshow.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                    mp.stop();
                    iv_vp_play.setVisibility(View.VISIBLE);

                }
                //mp.stop();
                vv_videoshow.pause();
                iv_vp_play.setVisibility(View.VISIBLE);

            }
        });
    }


    public void bindView() {
        mAdView = findViewById(R.id.adview);
        facbook_ad_banner = findViewById(R.id.facbook_ad_banner);
        iv_preview = findViewById(R.id.iv_preview);
        rl_video = findViewById(R.id.rl_video);
        vv_videoshow = findViewById(R.id.vv_videoshow);
        iv_vp_play = findViewById(R.id.iv_vp_play);

    }

    public void onclickPreview(View view) {
        switch (view.getId()) {
            case R.id.iv_backarrow:
                onBackPressed();
                break;
            case R.id.ll_share:
                isShare = true;
                File check=new File((Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" +"Banner Maker"+"/new_video_create.mp4"));
                if(check.exists()){
                    check.delete();
                }
                Log.d("hasdja","ghggg"+name);
                if (name.equals("image")) {
                    shareimage();
                } else {
                    new AsyncTaskExampleNew().execute();
                }
                break;
            case R.id.ll_gotohome:
                startActivity(new Intent(context, ActivityHome.class));
                break;
            case R.id.ll_download:
                isShare = false;
                File check1=new File((Environment.getExternalStorageDirectory().getAbsolutePath() + "/Banner Maker/new_video_create.mp4"));
                if(check1.exists()){
                    check1.delete();
                }


                if (name.equals("image")) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setMessage("Download complete!!!");
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (Constance.adType.equals("Ad Mob")) {
                                interstitialAdMobAd();
                                Log.d("ADssss", "Ad Mob");
                            } else {
                                interstitialFacbookAd();
                                Log.d("ADssss", "Facebook");
                            }

                        }
                    });
                    alert.show();


                } else {
                    new AsyncTaskExampleNew().execute();
                }

                break;
            case R.id.ll_gotomypost:
                Intent i = new Intent(context, ActivityHome.class);
                i.putExtra("check_fragmentname", "fragment_mypost");
                startActivity(i);

                break;
        }
    }


    void saveVideoToInternalStorage() {
     /*   File musicDirectory = new
                File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/FestivalPost");
        musicDirectory.mkdirs();
        musicDirectory.mkdir();*/
        //mVideoName=videoName

        if (!Constance.FileSaveVideoDirectory.exists()) {
            Constance.FileSaveVideoDirectory.mkdir();
        }
        filePath= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" +"Banner Maker"+"/"+System.currentTimeMillis()+".mp4";

        /*  Log.d("dfsfs",""+ sessionManager!!.getValueString("video_name")!!)
          Log.d("dfsfs",""+ sessionManager!!.getValueString("image_name")!!)
          Log.d("dfsfs",""+ mVideoName)*/
        String inputCode1[] = new String[]{
                "-i",
                Constance.savedVideoPath,
                "-i",
                /* "/storage/emulated/0/kiki/demopic.png",*/
                Constance.savedImagePath,
                "-filter_complex",
                "overlay=(W-w):(H-h)",
                "-codec:a",
                "copy",
                filePath
        };


        int rc = FFmpeg.execute(inputCode1);

        if (rc == Config.RETURN_CODE_SUCCESS) {
            Log.i(Config.TAG, "Command execution completed successfully.");
            //Toast.makeText(this@TestingActivity, "Download complete", Toast.LENGTH_LONG).show();
        } else if (rc == Config.RETURN_CODE_CANCEL) {
            Log.i(Config.TAG, "Command execution cancelled by user.");
            //  Toast.makeText(this@TestingActivity, "fail", Toast.LENGTH_LONG).show();
        } else {
            Log.i(
                    Config.TAG, String.format(
                            "Command execution failed with rc=%d and the output below.",
                            rc
                    )
            );
            Config.printLastCommandOutput(Log.INFO);
            //Toast.makeText(this@TestingActivity, "fail 2", Toast.LENGTH_LONG).show();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(filePath)));

        } else {
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(filePath)));

        }

        MediaScannerConnection.scanFile(this,
                new String[]{filePath},
                new String[]{"video/mp4"},
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });


    }

    class AsyncTaskExampleNew extends AsyncTask<Void, Void, Exception> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setCancelable(false);
            dialog.show();
            if (isShare) {
                dialog.setMessage("Please Wait... for Video share...");
            } else {
                // call some loader

                dialog.setMessage("Please Wait... Video is saving...");

            }
        }

        @Override
        protected void onPostExecute(Exception e) {


            if(isShare){
                dialog.dismiss();

                oldVideo=new File(Constance.savedVideoPath);
                oldFrame=new File(Constance.savedImagePath);

                if(oldFrame.exists()){
                    oldFrame.delete();
                }
                if(oldVideo.exists()){
                    vv_videoshow.setVideoPath(filePath);
                    oldVideo.delete();
                }
                //filePath=Environment.getExternalStorageDirectory().getAbsolutePath() + "/New Festival Video Post/"+System.currentTimeMillis()+".mp4";
                Intent share = new Intent(Intent.ACTION_SEND);
                Uri uri = FileProvider.getUriForFile(context, getString(R.string.file_provider_authority), new File(filePath));
                share.setType("Video/*");
                share.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(share, "Share via"));
            }
            else {
                dialog.dismiss();
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setMessage("Download complete!!!");
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                         oldVideo=new File(Constance.savedVideoPath);
                         oldFrame=new File(Constance.savedImagePath);

                         if(oldFrame.exists()){
                             oldFrame.delete();
                         }
                        if(oldVideo.exists()){
                            vv_videoshow.setVideoPath(filePath);
                            oldVideo.delete();
                        }

                        if (Constance.adType.equals("Ad Mob")) {
                            interstitialAdMobAd();
                            Log.d("ADssss", "Ad Mob");
                        } else {
                            interstitialFacbookAd();
                            Log.d("ADssss", "Facebook");
                        }

                        dialog.dismiss();
                    }
                });
                alert.show();
            }

            super.onPostExecute(e);
        }


        @Override
        protected Exception doInBackground(Void... voids) {
            try {
                saveVideoToInternalStorage();
            } catch (Exception e) {
                //p.dismiss();
            }
            return null;
        }
    }

    public void SaveAndShare(View view, boolean ShareImageOrNot) {
        bitmappreviewsave = viewToBitmap(view);
        isForShareGlobal = ShareImageOrNot;
        new NetworkAccess().execute();

    }

    public Bitmap viewToBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);


        return bitmap;
    }

    public class NetworkAccess extends AsyncTask<Void, Void, Exception> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setCancelable(false);
            dialog.show();
            if (isForShareGlobal) {

            } else {
                // call some loader

                dialog.setMessage("Please Wait... Image is saving...");

            }

        }

        @Override
        protected Exception doInBackground(Void... params) {
            // Do background task
            try {
                File direct = null;
                if (isForShareGlobal) {
                    direct = new File(Environment.getExternalStorageDirectory() + Constance.FolderNameShare);
/*
                    direct = new File(Environment.getExternalStorageDirectory(), "New Festival post");
*/

                } else {
                    // direct = new File(Environment.getExternalStorageDirectory(), "New Festival post");

                    direct = new File(Environment.getExternalStorageDirectory() +
                            Constance.FolderNameSave);
                }
                if (!direct.exists()) {
                    Log.d("check_dir", "dir not exist");
                    direct.mkdirs();
                } else {
                    Log.d("check_dir", "dir exist");
                }
                // currentTime = Calendar.getInstance().getTime();
                filePath = direct + "/" + System.currentTimeMillis() + ".png";

                FileOutputStream output = null;

                output = new FileOutputStream(direct + "/" + System.currentTimeMillis() + ".png");
                bitmappreviewsave.compress(Bitmap.CompressFormat.PNG, 100, output);
                output.flush();
                output.close();
                scanFile(context, Uri.fromFile(direct));
                dialog.dismiss();
            } catch (FileNotFoundException e) {
                Log.d("check_dir", "catch" + e);

                return e;
            } catch (IOException e) {
                e.printStackTrace();
                // Toast.makeText(context, "Fail to save image", Toast.LENGTH_SHORT).show();
                // dialog.dismiss();
                return e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Exception e) {
            super.onPostExecute(e);
            // dismiss loader
            // update ui

            if (e == null) {
                if (isForShareGlobal) {
                    shareimage();
//                progressBar.setVisibility(View.GONE);
                    dialog.dismiss();
                } else {
                   /* oldFrame=new File(Constance.savedImagePath);
                    if(oldFrame.exists()){
                        oldFrame.delete();
                    }*/
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setMessage("Download complete!!!");
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                            if (Constance.adType.equals("Ad Mob")) {
                                interstitialAdMobAd();
                                Log.d("ADssss", "Ad Mob");
                            } else {
                                interstitialFacbookAd();
                                Log.d("ADssss", "Facebook");
                            }

                        }
                    });
                    alert.show();
                    dialog.dismiss();

                }


            } else {
                Log.d("check_dir", "else" + e);

                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setMessage("Download Failed!!!" + e);
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Constance.adType.equals("Ad Mob")) {
                            interstitialAdMobAd();
                            Log.d("ADssss", "Ad Mob");
                        } else {
                            interstitialFacbookAd();
                            Log.d("ADssss", "Facebook");
                        }
                        dialog.dismiss();
                    }
                });
                alert.show();
                dialog.dismiss();
            }

        }

    }

    /*
        public class NetworkAccess extends AsyncTask<Void, Void, Exception> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ProgressDialog(context);
                dialog.setMessage("Please Wait... Image is saving...");
                dialog.setCancelable(false);
                dialog.show();
                // call some loader
            }

            @Override
            protected Exception doInBackground(Void... params) {

                // Do background task
                try {
                    File direct = null;
                    if (isForShareGlobal) {
                        direct = new File(Environment.getExternalStorageDirectory()
                                + "/" + Constance.FolderNameShare);


                    } else {
                        direct = new File(Environment.getExternalStorageDirectory()
                                + "/" + Constance.FolderNameSave);

                    }
                    if (!direct.exists()) {
                        direct.mkdirs();
                    }

                    currentTime = Calendar.getInstance().getTime();
                    filePath = direct + "/" + currentTime + ".png";

                    FileOutputStream output = new FileOutputStream(direct + "/" + currentTime + ".png");
                    bitmappreviewsave.compress(Bitmap.CompressFormat.PNG, 100, output);
                    output.flush();
                    output.close();
                    scanFile(context, Uri.fromFile(direct));

                } catch (FileNotFoundException e) {
                    return e;
                } catch (IOException e) {

                    return e;
                }


                return null;

            }

            @Override
            protected void onPostExecute(Exception e) {
                super.onPostExecute(e);
                dialog.dismiss();

                if (e == null) {
                    if (isForShareGlobal) {
                        shareimage();
                        dialog.dismiss();
                    } else {

                        AlertDialog.Builder alert = new AlertDialog.Builder(context);
                        alert.setMessage("Image Saved Successfully !");
                        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (Constance.adType.equals("Ad Mob")) {
                                    interstitialAdMobAd();
                                    Log.d("ADssss", "Ad Mob");
                                } else {
                                    interstitialFacbookAd();
                                    Log.d("ADssss", "Facebook");
                                }
                            }
                        });
                        alert.show();
                        dialog.dismiss();
                        dialog.dismiss();

                    }
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setMessage("Download Failed!!!");
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alert.show();
                    dialog.dismiss();
                }
            }
        }
    */
    private static void scanFile(Context context, Uri imageUri) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(imageUri);
        context.sendBroadcast(scanIntent);

    }

    public void shareimage() {
        Log.d("sdjshdh","dshhj"+Constance.savedImagePath);
        Intent share = new Intent(Intent.ACTION_SEND);
        Uri uri = FileProvider.getUriForFile(context, getString(R.string.file_provider_authority), new File(Constance.savedImagePath));
        share.setType("image/*");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(share, "Share via"));

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

    public void interstitialAdMobAd() {
        if (ActivityHome.getInstance().mInterstitialAd != null) {
            if (ActivityHome.getInstance().mInterstitialAd.isLoaded()) {
                Log.d("shsjks", "sdhsjkhd");
                ActivityHome.getInstance().mInterstitialAd.show();
            } else {
            }
        } else {

        }
    }

    public void interstitialFacbookAd() {

        if (ActivityHome.getInstance().interstitialFacbookAd != null) {
            if (!ActivityHome.getInstance().interstitialFacbookAd.isAdLoaded()) {

                AdSettings.setDebugBuild(true);
                ActivityHome.getInstance().interstitialFacbookAd.loadAd();
            } else {

            }
        } else {

        }
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat
                    .requestPermissions(ActivityPreview.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
        } else {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    // Permission Denied
                    Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}