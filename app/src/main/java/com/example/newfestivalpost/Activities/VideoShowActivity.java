package com.example.newfestivalpost.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;

import com.example.newfestivalpost.AnalyticsApplication;
import com.example.newfestivalpost.Fragments.DImageListFragment;
import com.example.newfestivalpost.Fragments.DVideoListFragment;
import com.example.newfestivalpost.Fragments.FragmentMyPost;
import com.example.newfestivalpost.Fragments.VideoShowFragment;
import com.example.newfestivalpost.R;
import com.example.newfestivalpost.Utills.Constance;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.List;

public class VideoShowActivity extends AppCompatActivity {


    Context context;
    ViewPager vp_singlepost;
    Tracker mTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_show);
        context=VideoShowActivity.this;
        initVS();

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        setupViewPager(vp_singlepost);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("Image~" + "Google Analytics Testing");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


    void initVS(){
        vp_singlepost=findViewById(R.id.vp_singlepost);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        for (int i=0;i<Constance.mypostVideoList.size();i++)
        {
            adapter.addFragment(new VideoShowFragment(Constance.mypostVideoList.get(i).getAbsolutePath()));
        }


        viewPager.setAdapter(adapter);
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
       // private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
          //  mFragmentTitleList.add(title);
        }


    }

}