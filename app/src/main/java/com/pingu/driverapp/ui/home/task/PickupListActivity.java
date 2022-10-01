package com.pingu.driverapp.ui.home.task;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.tabs.TabLayout;
import com.pingu.driverapp.R;
import com.pingu.driverapp.databinding.ActivityPickupListBinding;
import com.pingu.driverapp.ui.base.BaseActivity;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public class PickupListActivity extends BaseActivity {
    private ActivityPickupListBinding binding;
    private PickupListViewModel pickupListViewModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pickup_list);
        pickupListViewModel = ViewModelProviders.of(this, viewModelFactory).get(PickupListViewModel.class);
        initToolbar();
        initTabLayoutAndViewPager();
    }

    private void initTabLayoutAndViewPager() {
        String[] titles = getResources().getStringArray(R.array.pickup_list_menu_titles);
        PickupListTabPagerAdapter pagerAdapter =
                new PickupListTabPagerAdapter(getSupportFragmentManager(), titles);

        binding.viewPager.setAdapter(pagerAdapter);
        binding.viewPager.setOffscreenPageLimit(1);
        binding.viewPager.disableScroll(true);
        binding.tabLayout.setupWithViewPager(binding.viewPager);

        View root = binding.tabLayout.getChildAt(0);
        if (root instanceof LinearLayout) {
            ((LinearLayout) root).setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(getResources().getColor(R.color.tab_layout_separator_bg));
            drawable.setSize(2, 1);
            ((LinearLayout) root).setDividerPadding(10);
            ((LinearLayout) root).setDividerDrawable(drawable);
        }

        // Change individual tab text color
        TextView menuAllTaskTextView = (TextView)
                LayoutInflater.from(this).inflate(R.layout.text, binding.tabLayout, false);
        menuAllTaskTextView.setTextColor(getResources().getColor(R.color.black));
        menuAllTaskTextView.setText(getString(R.string.pickup_list_all_tasks));
        menuAllTaskTextView.setTypeface(null, Typeface.BOLD);
        binding.tabLayout.getTabAt(0).setCustomView(menuAllTaskTextView);

        TextView menuAvailableTextView = (TextView)
                LayoutInflater.from(this).inflate(R.layout.text, binding.tabLayout, false);
        menuAvailableTextView.setTextColor(getResources().getColor(R.color.profile_logout_red));
        menuAvailableTextView.setText(getString(R.string.pickup_list_available));
        binding.tabLayout.getTabAt(1).setCustomView(menuAvailableTextView);

        TextView menuMyTaskTextView = (TextView)
                LayoutInflater.from(this).inflate(R.layout.text, binding.tabLayout, false);
        menuMyTaskTextView.setTextColor(getResources().getColor(R.color.hyperlink_text));
        menuMyTaskTextView.setText(getString(R.string.pickup_list_my_tasks));
        binding.tabLayout.getTabAt(2).setCustomView(menuMyTaskTextView);

        // Set bold to selected text
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                TextView text = (TextView) tab.getCustomView();
                text.setTypeface(null, Typeface.BOLD);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView text = (TextView) tab.getCustomView();
                text.setTypeface(null, Typeface.NORMAL);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });
        binding.viewPager.setCurrentItem(1);
    }

    private void initToolbar() {
        getSupportActionBar().setTitle(getString(R.string.pickup_list_screen_title));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getSupportActionBar().setElevation(0);

        final Drawable upArrow = getResources().getDrawable(R.mipmap.back_arrow);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }
}
