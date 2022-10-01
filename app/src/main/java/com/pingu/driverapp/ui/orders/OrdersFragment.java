package com.pingu.driverapp.ui.orders;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pingu.driverapp.R;
import com.pingu.driverapp.databinding.FragmentOrdersBinding;
import com.pingu.driverapp.ui.orders.completed.CompletedListActivity;
import com.pingu.driverapp.ui.orders.history.HistoryListActivity;
import com.pingu.driverapp.util.Constants;
import com.pingu.driverapp.util.DateTimeHelper;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

public class OrdersFragment extends DaggerFragment implements SwipeRefreshLayout.OnRefreshListener {
    private FragmentOrdersBinding binding;
    private OrdersViewModel ordersViewModel;
    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ordersViewModel =
                ViewModelProviders.of(this, viewModelFactory).get(OrdersViewModel.class);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.title_orders));
        initUI();
        loadOrderSummary();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_orders, container, false);

        return binding.getRoot();
    }

    private void loadOrderSummary() {
        ordersViewModel.getLoadSummaryDone().observe(this, done -> {
                    if (done) {
                        binding.swipeLayout.setRefreshing(false);
                    }
                }
        );

        //Today
        ordersViewModel.getTotalTodayPickupParcel().observe(this, totalTodayPickupParcel -> {
                    binding.txtTotalTodayPickup.setText(totalTodayPickupParcel + "");
                }
        );

        ordersViewModel.getTotalTodayDeliveryParcel().observe(this, totalTodayDeliveryParcel -> {
                    binding.txtTotalTodayDelivery.setText(totalTodayDeliveryParcel + "");
                }
        );

        ordersViewModel.getTotalTodayCompletedParcel().observe(this, totalTodayCompletedParcel -> {
                    binding.txtTotalTodayDeliveryAndSignature.setText(totalTodayCompletedParcel + "");
                }
        );

        ordersViewModel.getTotalTodayProblematicParcel().observe(this, totalTodayProblematicParcel -> {
                    binding.txtTotalTodayProblematicParcel.setText(totalTodayProblematicParcel + "");
                }
        );

        //History
        ordersViewModel.getTotalHistoryPickupParcel().observe(this, totalHistoryPickupParcel -> {
                    binding.txtTotalHistoryPickup.setText(totalHistoryPickupParcel + "");
                }
        );

        ordersViewModel.getTotalHistoryDeliveryParcel().observe(this, totalHistoryDeliveryParcel -> {
                    binding.txtTotalHistoryDelivery.setText(totalHistoryDeliveryParcel + "");
                }
        );

        ordersViewModel.getTotalHistoryCompletedParcel().observe(this, totalHistoryCompletedParcel -> {
                    binding.txtTotalHistoryDeliveryAndSignature.setText(totalHistoryCompletedParcel + "");
                }
        );

        ordersViewModel.getTotalHistoryProblematicParcel().observe(this, totalHistoryProblematicParcel -> {
                    binding.txtTotalHistoryProblematicParcel.setText(totalHistoryProblematicParcel + "");
                }
        );
    }

    private void initUI() {
        binding.swipeLayout.setOnRefreshListener(this);
        binding.txtTitleTodayDate.setText(DateTimeHelper.getCurrentDate());

        // Today completed
        binding.panelTodayPickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCompletedListScreen(getString(R.string.orders_picked_up));
            }
        });
        binding.panelTodayDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCompletedListScreen(getString(R.string.orders_delivery));
            }
        });
        binding.panelTodayDeliveryAndSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCompletedListScreen(getString(R.string.orders_delivery_and_signature));
            }
        });
        binding.panelTodayProblematicParcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCompletedListScreen(getString(R.string.orders_problematic_parcel));
            }
        });

        // History
        binding.panelHistoryPickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHistoryListScreen(String.format(getString(R.string.orders_history_list), getString(R.string.orders_picked_up)));
            }
        });
        binding.panelHistoryDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHistoryListScreen(String.format(getString(R.string.orders_history_list), getString(R.string.orders_delivery)));
            }
        });
        binding.panelHistoryDeliveryAndSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHistoryListScreen(String.format(getString(R.string.orders_history_list), getString(R.string.orders_delivery_and_signature)));
            }
        });
        binding.panelHistoryProblematicParcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHistoryListScreen(String.format(getString(R.string.orders_history_list), getString(R.string.orders_problematic_parcel)));
            }
        });
    }

    private void openCompletedListScreen(final String title) {
        Intent completedListIntent = new Intent(getContext(), CompletedListActivity.class);
        completedListIntent.putExtra(Constants.INTENT_EXTRA_TASK_COMPLETED_TYPE, title);
        startActivity(completedListIntent);
    }

    private void openHistoryListScreen(final String title) {
        Intent completedListIntent = new Intent(getContext(), HistoryListActivity.class);
        completedListIntent.putExtra(Constants.INTENT_EXTRA_TASK_HISTORY_TYPE, title);
        startActivity(completedListIntent);
    }

    @Override
    public void onRefresh() {
        ordersViewModel.loadOrderSummaryData();
    }
}