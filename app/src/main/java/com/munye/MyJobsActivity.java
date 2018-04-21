package com.munye;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.munye.user.R;
import com.munye.adapter.ViewPagerAdapter;

public class MyJobsActivity extends ActionBarBaseActivity implements View.OnClickListener {

    public boolean isLoadAgain = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_jobs);
        initToolBar();
        setToolBarTitle(getString(R.string.title_my_jobs));
        imgBtnDrawerToggle.setVisibility(View.INVISIBLE);

        imgBtnToolbarBack.setOnClickListener(this);

        ViewPager viewPager = (ViewPager) findViewById(R.id.myJobViewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabMyJobs);
        ViewPagerAdapter adapterViewPager = new ViewPagerAdapter(getSupportFragmentManager(), this);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(adapterViewPager);

    }



    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.imgBtnActionBarBack)
            onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if(this.isTaskRoot())
            backToMapActivity();
        super.onBackPressed();
    }
}
