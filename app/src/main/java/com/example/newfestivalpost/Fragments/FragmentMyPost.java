package com.example.newfestivalpost.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.newfestivalpost.Adapters.AdapterMyPostList;
import com.example.newfestivalpost.R;
import com.example.newfestivalpost.Utills.Constance;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.analytics.Tracker;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FragmentMyPost extends Fragment {

    Context context;
    View view;
    TabLayout tl_mypost;
    ViewPager vp_mypost;
    ProgressBar pb_post;
    Tracker mTracker;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragmen_mypost, container, false);
        context = getContext();
        bindView();
        setupViewPager(vp_mypost);

        tl_mypost.setupWithViewPager(vp_mypost);

        return view;
    }

    public void bindView() {
        tl_mypost=view.findViewById(R.id.tl_mypost);
        vp_mypost=view.findViewById(R.id.vp_mypost);
        pb_post=view.findViewById(R.id.pb_post);


    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
        adapter.addFragment(new DImageListFragment(), "Images");
        adapter.addFragment(new DVideoListFragment(), "Videos");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

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

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
