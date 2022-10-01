package com.pingu.driverapp.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pingu.driverapp.R;
import com.pingu.driverapp.databinding.FragmentHomeBinding;
import com.pingu.driverapp.ui.home.operation.deliverysignature.DeliverySignatureActivity;
import com.pingu.driverapp.ui.home.operation.pickup.PickupActivity;
import com.pingu.driverapp.ui.home.operation.problematicparcel.ProblematicParcelActivity;
import com.pingu.driverapp.ui.home.pending.arrival_at_hub.PendingArrivalAtHubActivity;
import com.pingu.driverapp.ui.home.pending.delivery.PendingDeliveryListActivity;
import com.pingu.driverapp.ui.home.pending.pickup.PendingPickupListActivity;
import com.pingu.driverapp.ui.home.task.PickupListActivity;
import com.pingu.driverapp.util.Constants;
import com.pingu.driverapp.util.DateTimeHelper;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

public class HomeFragment extends DaggerFragment implements SwipeRefreshLayout.OnRefreshListener {
    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private static final int PICKUP_LIST_REQUEST_CODE = 500;
    private static final int PENDING_PICKUP_REQUEST_CODE = 501;
    private static final int PENDING_DELIVERY_REQUEST_CODE = 502;
    private static final int PARCEL_PICKUP_REQUEST_CODE = 503;
    private static final int PARCEL_OUT_FOR_DELIVERY_REQUEST_CODE = 504;
    private static final int PARCEL_COMPLETED_REQUEST_CODE = 505;
    private static final int PARCEL_PROBLEMATIC_REQUEST_CODE = 506;
    private static final int PENDING_ARRIVAL_AT_HUB_REQUEST_CODE = 507;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel.class);
        initUI();
        initClickEvents();
        loadAccountData();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);

        return binding.getRoot();
    }

    private void loadAccountData() {
        binding.txtTitleTodayDate.setText(DateTimeHelper.getCurrentDate());

        homeViewModel.getLoadSummaryDone().observe(this, done -> {
                    if (done) {
                        binding.swipeLayout.setRefreshing(false);
                    }
                }
        );

        homeViewModel.getZone().observe(this, zone -> {
                    binding.txtZone.setText(zone);
                }
        );

        homeViewModel.getTotalPendingPickupAddress().observe(this, totalPendingPickup -> {
                    binding.txtTotalPendingPickup.setText(totalPendingPickup + "");
                }
        );

        homeViewModel.getTotalPendingArrivalAtHubParcel().observe(this, totalPendingArrivalAtHub -> {
                    binding.txtTotalPendingArrivalAtHub.setText(totalPendingArrivalAtHub + "");
                }
        );

        homeViewModel.getTotalPendingDeliveryParcel().observe(this, totalPendingDelivery -> {
                    binding.txtTotalPendingDelivery.setText(totalPendingDelivery + "");
                }
        );

        homeViewModel.getTotalAvailableAddress().observe(this, totalAvailableAddress -> {
                    binding.txtTotalAvailableAddress.setText(totalAvailableAddress + "");
                }
        );
    }

    private void initUI() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.app_name));
        binding.swipeLayout.setOnRefreshListener(this);
    }

    private void initClickEvents() {
//        final Spannable span = new SpannableString(getString(R.string.home_pending_arrival_at_hub));
//        span.setSpan(new AbsoluteSizeSpan(12, true), 0, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        binding.txtPendingArrivalAtHub.setText(span, TextView.BufferType.SPANNABLE);

        binding.panelPickupListLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPickupListScreen();
            }
        });
        binding.panelPickupListRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPickupListScreen();
            }
        });
        binding.panelPendingPickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPendingPickupScreen();
            }
        });
        binding.panelPendingArrivalAtHub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPendingArrivalAtHubScreen();
            }
        });
        binding.panelPendingDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPendingDeliveryScreen();
            }
        });
        binding.panelOperationPickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOperationPickupScreen();
            }
        });
        binding.panelOperationDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOperationDeliveryScreen();
            }
        });
        binding.panelOperationDeliveryAndSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOperationDeliveryAndSignatureScreen();
            }
        });
        binding.panelOperationProblematicParcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openProblematicParcelScreen();
            }
        });
    }

    private void openPickupListScreen() {
        Intent pickupListIntent = new Intent(getContext(), PickupListActivity.class);
        startActivityForResult(pickupListIntent, PICKUP_LIST_REQUEST_CODE);
    }

    private void openPendingPickupScreen() {
        Intent pendingPickupListIntent = new Intent(getContext(), PendingPickupListActivity.class);
        startActivityForResult(pendingPickupListIntent, PENDING_PICKUP_REQUEST_CODE);
    }

    private void openPendingArrivalAtHubScreen() {
        Intent pendingArrivalAtHubIntent = new Intent(getContext(), PendingArrivalAtHubActivity.class);
        startActivityForResult(pendingArrivalAtHubIntent, PENDING_ARRIVAL_AT_HUB_REQUEST_CODE);
    }

    private void openPendingDeliveryScreen() {
        Intent pendingDeliveryListIntent = new Intent(getContext(), PendingDeliveryListActivity.class);
        startActivityForResult(pendingDeliveryListIntent, PENDING_DELIVERY_REQUEST_CODE);
    }

    private void openOperationPickupScreen() {
        Intent operationPickupIntent = new Intent(getContext(), PickupActivity.class);
        operationPickupIntent.putExtra(Constants.INTENT_EXTRA_TASK_ACTION_TYPE, getString(R.string.home_pickup));
        startActivityForResult(operationPickupIntent, PARCEL_PICKUP_REQUEST_CODE);
    }

    private void openOperationDeliveryScreen() {
        Intent operationDeliveryIntent = new Intent(getContext(), PickupActivity.class);
        operationDeliveryIntent.putExtra(Constants.INTENT_EXTRA_TASK_ACTION_TYPE, getString(R.string.home_delivery));
        startActivityForResult(operationDeliveryIntent, PARCEL_OUT_FOR_DELIVERY_REQUEST_CODE);
    }

    private void openOperationDeliveryAndSignatureScreen() {
        Intent deliverySignatureIntent = new Intent(getContext(), DeliverySignatureActivity.class);
        startActivityForResult(deliverySignatureIntent, PARCEL_COMPLETED_REQUEST_CODE);
    }

    private void openProblematicParcelScreen() {
        Intent problematicParcelScreen = new Intent(getContext(), ProblematicParcelActivity.class);
        startActivityForResult(problematicParcelScreen, PARCEL_PROBLEMATIC_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICKUP_LIST_REQUEST_CODE || requestCode == PENDING_PICKUP_REQUEST_CODE ||
                    requestCode == PENDING_DELIVERY_REQUEST_CODE || requestCode == PARCEL_PICKUP_REQUEST_CODE ||
                    requestCode == PARCEL_OUT_FOR_DELIVERY_REQUEST_CODE || requestCode == PARCEL_COMPLETED_REQUEST_CODE ||
                    requestCode == PARCEL_PROBLEMATIC_REQUEST_CODE || requestCode == PENDING_ARRIVAL_AT_HUB_REQUEST_CODE) {
                homeViewModel.refreshParcelSummary();
            }
        }
    }

    @Override
    public void onRefresh() {
        homeViewModel.refreshParcelSummary();
    }
}