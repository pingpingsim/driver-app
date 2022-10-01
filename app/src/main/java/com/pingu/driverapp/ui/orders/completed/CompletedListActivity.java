package com.pingu.driverapp.ui.orders.completed;

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

import com.pingu.driverapp.R;
import com.pingu.driverapp.data.model.Parcel;
import com.pingu.driverapp.databinding.ActivityCompletedListBinding;
import com.pingu.driverapp.ui.base.BaseActivity;
import com.pingu.driverapp.util.Constants;
import com.tylersuehr.esr.EmptyStateRecyclerView;
import com.tylersuehr.esr.TextStateDisplay;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class CompletedListActivity extends BaseActivity {
    private ActivityCompletedListBinding binding;
    private CompletedListViewModel completedListViewModel;
    private CompletedListAdapter completedListAdapter;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private int totalAddress;
    private int totalParcel;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_completed_list);
        completedListViewModel = ViewModelProviders.of(this, viewModelFactory).get(CompletedListViewModel.class);
        initToolbar();
        initRecyclerView();
        initPendingPickupSearchInput();

        observeTaskListResponse();
        completedListViewModel.loadTodayCompletedTask(type);
    }

    private void initPendingPickupSearchInput() {
        binding.editTextSearchCompletedList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (binding.editTextSearchCompletedList.getRight() - binding.editTextSearchCompletedList.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        binding.editTextSearchCompletedList.setText("");
                        binding.editTextSearchCompletedList.clearFocus();
                        filter("");
                        return true;
                    }
                }
                return false;
            }
        });

        binding.editTextSearchCompletedList.addTextChangedListener(new TextWatcher() {
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
        if (completedListAdapter != null &&
                completedListAdapter.getOriginalPickupList() != null &&
                !completedListAdapter.getOriginalPickupList().isEmpty()) {
            if (TextUtils.isEmpty(text)) {
                hideSoftKeyboard();
                completedListAdapter.filterPickList(completedListAdapter.getOriginalPickupList());
                binding.txtTotalParcels.setText(String.format(getString(R.string.pending_pickup_column_parcels), totalParcel));

            } else {
                List<Parcel> filteredList = new ArrayList<Parcel>();
                int totalParcelCount = 0;
                for (Parcel pickup : completedListAdapter.getOriginalPickupList()) {
                    if (pickup.getRecipientAddress().toLowerCase().contains(text.toLowerCase()) ||
                            pickup.getRecipientName().toLowerCase().contains(text.toLowerCase()) ||
                            (pickup.getParcelId() != null && pickup.getParcelId().toLowerCase().contains(text.toLowerCase()))
                    ) {
                        totalParcelCount += pickup.getTotalParcels();
                        filteredList.add(pickup);
                    }
                }
                completedListAdapter.filterPickList(filteredList);
                binding.txtTotalParcels.setText(String.format(getString(R.string.pending_pickup_column_parcels), totalParcelCount));
            }
        }
    }

    private void initRecyclerView() {
        completedListAdapter = new CompletedListAdapter(this, null, type);
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        binding.recyclerViewCompletedList.setLayoutManager(verticalLayoutManager);
        binding.recyclerViewCompletedList.setAdapter(completedListAdapter);
        binding.recyclerViewCompletedList.setStateDisplay(EmptyStateRecyclerView.STATE_EMPTY,
                new TextStateDisplay(this, getString(R.string.msg_list_empty), ""));
    }

    private void initToolbar() {

        if (getIntent().getExtras() != null &&
                getIntent().getExtras().containsKey(Constants.INTENT_EXTRA_TASK_COMPLETED_TYPE)) {
            final String title = getIntent().getExtras().getString(Constants.INTENT_EXTRA_TASK_COMPLETED_TYPE);
            getSupportActionBar().setTitle(title);
            if (title.equals(getString(R.string.orders_picked_up))) {
                type = 1;
            } else if (title.equals(getString(R.string.orders_delivery))) {
                type = 2;
            } else if (title.equals(getString(R.string.orders_delivery_and_signature))) {
                type = 3;
            } else if (title.equals(getString(R.string.orders_problematic_parcel))) {
                type = 4;
            }
            binding.setScreenType(type);
        }
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getSupportActionBar().setElevation(0);

        final Drawable upArrow = getResources().getDrawable(R.mipmap.back_arrow);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void observeTaskListResponse() {
        completedListViewModel.getData().observe(this, parcelList -> {
                    if (parcelList != null && parcelList.size() > 0) {
                        completedListAdapter.setOriginalPickupList(parcelList);
                        binding.txtTotalParcels.setText(String.format(getString(R.string.pending_pickup_column_parcels), parcelList.size()));
                        binding.recyclerViewCompletedList.invokeState(EmptyStateRecyclerView.STATE_OK);
                    } else {
                        binding.txtTotalParcels.setText(String.format(getString(R.string.pending_pickup_column_parcels), 0));
                        binding.recyclerViewCompletedList.invokeState(EmptyStateRecyclerView.STATE_EMPTY);
                    }
                }
        );

        completedListViewModel.getLoading().observe(this, isLoading -> {
                    if (isLoading) {
                        completedListAdapter.setOriginalPickupList(null);
                        binding.recyclerViewCompletedList.invokeState(EmptyStateRecyclerView.STATE_LOADING);
                    }
                }
        );

        completedListViewModel.getError().observe(this, errorMsg -> {
                    if (!TextUtils.isEmpty(errorMsg)) {
                        binding.recyclerViewCompletedList.setStateDisplay(EmptyStateRecyclerView.STATE_ERROR,
                                new TextStateDisplay(this, errorMsg, ""));
                        binding.recyclerViewCompletedList.invokeState(EmptyStateRecyclerView.STATE_ERROR);
                    }
                }
        );
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
}
