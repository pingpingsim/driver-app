package com.pingu.driverapp.ui.home.pending.arrival_at_hub;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pingu.driverapp.R;
import com.pingu.driverapp.data.model.Parcel;
import com.pingu.driverapp.databinding.ActivityPendingArrivalAtHubListBinding;
import com.pingu.driverapp.ui.base.BaseActivity;
import com.tylersuehr.esr.EmptyStateRecyclerView;
import com.tylersuehr.esr.TextStateDisplay;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class PendingArrivalAtHubActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    private ActivityPendingArrivalAtHubListBinding binding;
    private PendingArrivalAtHubViewModel pendingArrivalAtHubViewModel;
    private PendingArrivalAtHubAdapter pendingArrivalAtHubAdapter;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private int totalAddress;
    private int totalParcel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pending_arrival_at_hub_list);
        pendingArrivalAtHubViewModel = ViewModelProviders.of(this, viewModelFactory).get(PendingArrivalAtHubViewModel.class);
        observeParcelListResponse();
        initToolbar();
        initRecyclerView();
        initPendingDeliverySearchInput();
    }

    private void initPendingDeliverySearchInput() {
        binding.editTextSearchPendingDelivery.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (binding.editTextSearchPendingDelivery.getRight() - binding.editTextSearchPendingDelivery.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        binding.editTextSearchPendingDelivery.setText("");
                        binding.editTextSearchPendingDelivery.clearFocus();
                        hideSoftKeyboard();
                        filter("");
                        return true;
                    }
                }
                return false;
            }
        });

        binding.editTextSearchPendingDelivery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });
    }

    private void filter(String text) {
        if (pendingArrivalAtHubAdapter != null &&
                pendingArrivalAtHubAdapter.getOriginalPickupList() != null &&
                !pendingArrivalAtHubAdapter.getOriginalPickupList().isEmpty()) {
            if (TextUtils.isEmpty(text)) {
                hideSoftKeyboard();
                pendingArrivalAtHubAdapter.filterPickList(pendingArrivalAtHubAdapter.getOriginalPickupList());
                binding.txtTotalParcels.setText(String.format(getString(R.string.pending_pickup_column_parcels), totalParcel));

            } else {
                List<Parcel> filteredList = new ArrayList<Parcel>();
                int totalParcelCount = 0;
                for (Parcel pickup : pendingArrivalAtHubAdapter.getOriginalPickupList()) {
                    if (pickup.getParcelId().toLowerCase().contains(text.toLowerCase())) {
                        totalParcelCount += pickup.getTotalParcels();
                        filteredList.add(pickup);
                    }
                }
                pendingArrivalAtHubAdapter.filterPickList(filteredList);
                binding.txtTotalParcels.setText(String.format(getString(R.string.pending_pickup_column_parcels), totalParcelCount));
            }
        } else {
        }
    }

    private void initRecyclerView() {
        pendingArrivalAtHubAdapter = new PendingArrivalAtHubAdapter(this, null);
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        binding.recyclerViewPendingArrivalAtHub.setLayoutManager(verticalLayoutManager);
        binding.recyclerViewPendingArrivalAtHub.setAdapter(pendingArrivalAtHubAdapter);
        binding.recyclerViewPendingArrivalAtHub.setStateDisplay(EmptyStateRecyclerView.STATE_EMPTY,
                new TextStateDisplay(this, getString(R.string.msg_list_empty), ""));
        binding.swipeLayout.setOnRefreshListener(this);
    }

    private void observeParcelListResponse() {
        pendingArrivalAtHubViewModel.getData().observe(this, parcelList -> {
                    if (parcelList != null && parcelList.size() > 0) {
                        totalParcel = parcelList.size();
                        pendingArrivalAtHubAdapter.setOriginalPickupList(parcelList);
                        binding.txtTotalParcels.setText(String.format(getString(R.string.pending_pickup_column_parcels), parcelList.size()));
                        binding.recyclerViewPendingArrivalAtHub.invokeState(EmptyStateRecyclerView.STATE_OK);
                    } else {
                        binding.txtTotalParcels.setText(String.format(getString(R.string.pending_pickup_column_parcels), 0));
                        binding.txtTotalParcels.setText(String.format(getString(R.string.pending_pickup_column_parcels), 0));
                        binding.recyclerViewPendingArrivalAtHub.invokeState(EmptyStateRecyclerView.STATE_EMPTY);
                    }
                    binding.swipeLayout.setRefreshing(false);
                }
        );

        pendingArrivalAtHubViewModel.getLoading().observe(this, isLoading -> {
                    if (isLoading) {
                        pendingArrivalAtHubAdapter.setOriginalPickupList(null);
                        binding.recyclerViewPendingArrivalAtHub.invokeState(EmptyStateRecyclerView.STATE_LOADING);
                    }
                }
        );

        pendingArrivalAtHubViewModel.getError().observe(this, errorMsg -> {
                    if (!TextUtils.isEmpty(errorMsg)) {
                        binding.recyclerViewPendingArrivalAtHub.setStateDisplay(EmptyStateRecyclerView.STATE_ERROR,
                                new TextStateDisplay(this, errorMsg, ""));
                        binding.recyclerViewPendingArrivalAtHub.invokeState(EmptyStateRecyclerView.STATE_ERROR);
                        binding.swipeLayout.setRefreshing(false);
                    }
                }
        );
    }

    private void initToolbar() {
        getSupportActionBar().setTitle(getString(R.string.pending_arrival_screen_title));
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onRefresh() {
        if (pendingArrivalAtHubViewModel.isLoadingInProgress)
            binding.swipeLayout.setRefreshing(false);
        else {
            hideSoftKeyboard();
            binding.editTextSearchPendingDelivery.setText("");
            pendingArrivalAtHubViewModel.loadPendingArrivalAtHubParcels(false);
        }
    }
}
