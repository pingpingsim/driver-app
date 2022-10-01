package com.pingu.driverapp.ui.home.pending.delivery;

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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pingu.driverapp.R;
import com.pingu.driverapp.data.model.Parcel;
import com.pingu.driverapp.databinding.ActivityPendingDeliveryListBinding;
import com.pingu.driverapp.ui.base.BaseActivity;
import com.pingu.driverapp.util.DialogHelper;
import com.pingu.driverapp.util.ItemMoveCallback;
import com.tylersuehr.esr.EmptyStateRecyclerView;
import com.tylersuehr.esr.TextStateDisplay;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class PendingDeliveryListActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    private ActivityPendingDeliveryListBinding binding;
    private PendingDeliveryViewModel pendingDeliveryViewModel;
    private PendingDeliveryListAdapter deliveryPendingListAdapter;
    public static final int DELIVERY_DETAILS_REQUEST_CODE = 100;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private int totalAddress;
    private int totalParcel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pending_delivery_list);
        pendingDeliveryViewModel = ViewModelProviders.of(this, viewModelFactory).get(PendingDeliveryViewModel.class);
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
        if (deliveryPendingListAdapter != null &&
                deliveryPendingListAdapter.getOriginalPickupList() != null &&
                !deliveryPendingListAdapter.getOriginalPickupList().isEmpty()) {
            if (TextUtils.isEmpty(text)) {
                hideSoftKeyboard();
                deliveryPendingListAdapter.filterPickList(deliveryPendingListAdapter.getOriginalPickupList());
                binding.txtTotalParcels.setText(String.format(getString(R.string.pending_pickup_column_parcels), totalParcel));

            } else {
                List<Parcel> filteredList = new ArrayList<Parcel>();
                int totalParcelCount = 0;
                for (Parcel pickup : deliveryPendingListAdapter.getOriginalPickupList()) {
                    if (pickup.getRecipientAddress().toLowerCase().contains(text.toLowerCase()) ||
                            pickup.getRecipientAddress().toLowerCase().contains(text.toLowerCase())) {
                        totalParcelCount += pickup.getTotalParcels();
                        filteredList.add(pickup);
                    }
                }
                deliveryPendingListAdapter.filterPickList(filteredList);
                binding.txtTotalParcels.setText(String.format(getString(R.string.pending_pickup_column_parcels), totalParcelCount));
            }
        } else {
        }
    }

    private void initRecyclerView() {
        deliveryPendingListAdapter = new PendingDeliveryListAdapter(this, null);
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        binding.recyclerViewPendingDeliveryList.setLayoutManager(verticalLayoutManager);
        binding.recyclerViewPendingDeliveryList.setAdapter(deliveryPendingListAdapter);
        binding.recyclerViewPendingDeliveryList.setStateDisplay(EmptyStateRecyclerView.STATE_EMPTY,
                new TextStateDisplay(this, getString(R.string.msg_list_empty), ""));
        binding.swipeLayout.setOnRefreshListener(this);

        final ItemMoveCallback itemMoveCallback = new ItemMoveCallback(deliveryPendingListAdapter);
        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemMoveCallback);
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewPendingDeliveryList);
    }

    private void observeParcelListResponse() {
        pendingDeliveryViewModel.getData().observe(this, parcelList -> {
                    if (parcelList != null && parcelList.size() > 0) {
                        totalParcel = parcelList.size();
                        deliveryPendingListAdapter.setOriginalPickupList(parcelList);
                        binding.txtTotalParcels.setText(String.format(getString(R.string.pending_pickup_column_parcels), parcelList.size()));
                        binding.recyclerViewPendingDeliveryList.invokeState(EmptyStateRecyclerView.STATE_OK);
                    } else {
                        binding.txtTotalParcels.setText(String.format(getString(R.string.pending_pickup_column_parcels), 0));
                        binding.txtTotalParcels.setText(String.format(getString(R.string.pending_pickup_column_parcels), 0));
                        binding.recyclerViewPendingDeliveryList.invokeState(EmptyStateRecyclerView.STATE_EMPTY);
                    }
                    binding.swipeLayout.setRefreshing(false);
                }
        );

        pendingDeliveryViewModel.getLoading().observe(this, isLoading -> {
                    if (isLoading) {
                        deliveryPendingListAdapter.setOriginalPickupList(null);
                        binding.recyclerViewPendingDeliveryList.invokeState(EmptyStateRecyclerView.STATE_LOADING);
                    }
                }
        );

        pendingDeliveryViewModel.getError().observe(this, errorMsg -> {
                    if (!TextUtils.isEmpty(errorMsg)) {
                        binding.recyclerViewPendingDeliveryList.setStateDisplay(EmptyStateRecyclerView.STATE_ERROR,
                                new TextStateDisplay(this, errorMsg, ""));
                        binding.recyclerViewPendingDeliveryList.invokeState(EmptyStateRecyclerView.STATE_ERROR);
                        binding.swipeLayout.setRefreshing(false);
                    }
                }
        );
    }

    private void initToolbar() {
        getSupportActionBar().setTitle(getString(R.string.pending_delivery_screen_title));
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
            if (requestCode == DELIVERY_DETAILS_REQUEST_CODE) {
                pendingDeliveryViewModel.loadPendingDeliveryTasks(true);

                if (data != null && data.getExtras() != null) {
                    if (data.getExtras().containsKey("complete_parcel")) {
                        DialogHelper.showAlertDialog(DialogHelper.Type.SUCCESS, "",
                                getString(R.string.operation_sign_delivery_signature_delivery_completed),
                                null, PendingDeliveryListActivity.this, true);

                    } else if (data.getExtras().containsKey("problematic_parcel")) {
                        DialogHelper.showAlertDialog(DialogHelper.Type.SUCCESS, "",
                                getString(R.string.operation_problematic_parcel_submitted),
                                null, PendingDeliveryListActivity.this, true);
                    }

                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
        pendingDeliveryViewModel.savePendingDeliveryOrder(deliveryPendingListAdapter.getOriginalPickupList());
    }

    @Override
    public void onRefresh() {
        if (pendingDeliveryViewModel.isLoadingInProgress)
            binding.swipeLayout.setRefreshing(false);
        else {
            hideSoftKeyboard();
            binding.editTextSearchPendingDelivery.setText("");
            pendingDeliveryViewModel.loadPendingDeliveryTasks(false);
        }
    }
}
