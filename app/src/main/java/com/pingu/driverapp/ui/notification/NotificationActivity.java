package com.pingu.driverapp.ui.notification;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pingu.driverapp.R;
import com.pingu.driverapp.databinding.ActivityNotificationBinding;
import com.tylersuehr.esr.EmptyStateRecyclerView;
import com.tylersuehr.esr.TextStateDisplay;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public class NotificationActivity extends DaggerAppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private ActivityNotificationBinding binding;
    private NotificationViewModel notificationViewModel;
    private NotificationAdapter notificationAdapter;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification);
        notificationViewModel = ViewModelProviders.of(this, viewModelFactory).get(NotificationViewModel.class);
        observeNotificationResponse();
        initToolbar();
        initRecyclerView();
    }

    private void initToolbar() {
        getSupportActionBar().setTitle(getString(R.string.title_notifications));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getSupportActionBar().setElevation(0);

        final Drawable upArrow = getResources().getDrawable(R.mipmap.back_arrow);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initRecyclerView() {
        notificationAdapter = new NotificationAdapter(this, null);
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        binding.recyclerViewNotificationList.setLayoutManager(verticalLayoutManager);
        binding.recyclerViewNotificationList.setAdapter(notificationAdapter);
        binding.recyclerViewNotificationList.setStateDisplay(EmptyStateRecyclerView.STATE_EMPTY,
                new TextStateDisplay(this, getString(R.string.msg_list_empty), ""));
        binding.swipeLayout.setOnRefreshListener(this);
    }

    private void observeNotificationResponse() {
        notificationViewModel.getData().observe(this, announcementList -> {
                    if (announcementList != null && announcementList.size() > 0) {
                        binding.recyclerViewNotificationList.invokeState(EmptyStateRecyclerView.STATE_OK);
                        notificationAdapter.setAnnouncementList(announcementList);
                    } else {
                        notificationAdapter.setAnnouncementList(null);
                        binding.recyclerViewNotificationList.invokeState(EmptyStateRecyclerView.STATE_EMPTY);
                    }
                    binding.swipeLayout.setRefreshing(false);
                }
        );

        notificationViewModel.getLoading().observe(this, isLoading -> {
                    if (isLoading) {
                        notificationAdapter.setAnnouncementList(null);
                        binding.recyclerViewNotificationList.invokeState(EmptyStateRecyclerView.STATE_LOADING);
                    }
                }
        );

        notificationViewModel.getError().observe(this, errorMsg -> {
                    if (!TextUtils.isEmpty(errorMsg)) {
                        binding.recyclerViewNotificationList.setStateDisplay(EmptyStateRecyclerView.STATE_ERROR,
                                new TextStateDisplay(this, errorMsg, ""));
                        binding.recyclerViewNotificationList.invokeState(EmptyStateRecyclerView.STATE_ERROR);
                        binding.swipeLayout.setRefreshing(false);
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.notification_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_notification:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRefresh() {
        if (notificationViewModel.isLoadingInProgress)
            binding.swipeLayout.setRefreshing(false);
        else {
            notificationViewModel.loadNotification(false);
        }
    }
}
