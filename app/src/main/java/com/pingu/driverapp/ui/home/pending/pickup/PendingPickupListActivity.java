package com.pingu.driverapp.ui.home.pending.pickup;

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
import com.pingu.driverapp.data.model.ParcelProblem;
import com.pingu.driverapp.data.model.Task;
import com.pingu.driverapp.databinding.ActivityPendingPickupListBinding;
import com.pingu.driverapp.ui.base.BaseActivity;
import com.pingu.driverapp.util.DialogHelper;
import com.pingu.driverapp.util.ItemMoveCallback;
import com.pingu.driverapp.util.Variables;
import com.pingu.driverapp.widget.PickupProblemDialog;
import com.tylersuehr.esr.EmptyStateRecyclerView;
import com.tylersuehr.esr.TextStateDisplay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class PendingPickupListActivity extends BaseActivity
        implements PickupProblemDialog.PickupProblemOptionClickListener, SwipeRefreshLayout.OnRefreshListener {
    private ActivityPendingPickupListBinding binding;
    private PendingPickupViewModel pendingPickupViewModel;
    private PendingPickupListAdapter pickupPendingListAdapter;
    private Map<String, Boolean> pickupProblemSubmitStatusMap = new HashMap<>();

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private int totalAddress;
    private int totalParcel;
    private int totalProblematicParcels;

    private ItemTouchHelper itemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pending_pickup_list);
        pendingPickupViewModel = ViewModelProviders.of(this, viewModelFactory).get(PendingPickupViewModel.class);
        observePendingPickupListResponse();
        observeProblematicParcelSubmitResponse();
        initToolbar();
        initRecyclerView();
        initPendingPickupSearchInput();
    }

    private void initPendingPickupSearchInput() {
        binding.editTextSearchPendingPickup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (binding.editTextSearchPendingPickup.getRight() - binding.editTextSearchPendingPickup.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        binding.editTextSearchPendingPickup.setText("");
                        binding.editTextSearchPendingPickup.clearFocus();
                        hideSoftKeyboard();
                        filter("");
                        return true;
                    }
                }
                return false;
            }
        });

        binding.editTextSearchPendingPickup.addTextChangedListener(new TextWatcher() {
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
        if (pickupPendingListAdapter != null &&
                pickupPendingListAdapter.getOriginalPickupList() != null &&
                !pickupPendingListAdapter.getOriginalPickupList().isEmpty()) {
            if (TextUtils.isEmpty(text)) {
                hideSoftKeyboard();
                pickupPendingListAdapter.filterPickList(pickupPendingListAdapter.getOriginalPickupList());
                binding.txtTotalSenderAddress.setText(String.format(getString(R.string.pending_pickup_column_sender_address), pickupPendingListAdapter.getOriginalPickupList().size()));
                binding.txtTotalParcels.setText(String.format(getString(R.string.pending_pickup_column_parcels), totalParcel));

            } else {
                List<Task> filteredList = new ArrayList<Task>();
                int totalParcelCount = 0;
                for (Task pickup : pickupPendingListAdapter.getOriginalPickupList()) {
                    if (pickup.getAddress().toLowerCase().contains(text.toLowerCase()) ||
                            pickup.getName().toLowerCase().contains(text.toLowerCase())) {
                        totalParcelCount += pickup.getParcelList().size();
                        filteredList.add(pickup);
                    }
                }
                pickupPendingListAdapter.filterPickList(filteredList);
                binding.txtTotalSenderAddress.setText(String.format(getString(R.string.pending_pickup_column_sender_address), filteredList.size()));
                binding.txtTotalParcels.setText(String.format(getString(R.string.pending_pickup_column_parcels), totalParcelCount));
            }
        } else {
        }
    }

    private void initRecyclerView() {
        binding.txtTotalSenderAddress.setText(String.format(getString(R.string.pending_pickup_column_sender_address), 0));
        pickupPendingListAdapter = new PendingPickupListAdapter(this, null, this);
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        binding.recyclerViewPendingPickupList.setLayoutManager(verticalLayoutManager);
        binding.recyclerViewPendingPickupList.setAdapter(pickupPendingListAdapter);
        binding.recyclerViewPendingPickupList.setHasFixedSize(true);
        binding.recyclerViewPendingPickupList.setStateDisplay(EmptyStateRecyclerView.STATE_EMPTY,
                new TextStateDisplay(this, getString(R.string.msg_list_empty), ""));
        binding.swipeLayout.setOnRefreshListener(this);

        final ItemMoveCallback itemMoveCallback = new ItemMoveCallback(pickupPendingListAdapter);
        itemTouchHelper = new ItemTouchHelper(itemMoveCallback);
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewPendingPickupList);
    }

    private int getTotalParcelCount(List<Task> taskList) {
        int totalParcelCount = 0;

        if (taskList != null && taskList.size() > 0) {
            for (final Task task : taskList) {
                totalParcelCount += task.getParcelList() != null ? task.getParcelList().size() : 0;
            }
        }

        return totalParcelCount;
    }

    private void observePendingPickupListResponse() {
        pendingPickupViewModel.getData().observe(this, taskList -> {
                    if (taskList != null && taskList.size() > 0) {
                        pickupPendingListAdapter.setOriginalPickupList(taskList);
                        totalAddress = taskList.size();
                        totalParcel = getTotalParcelCount(taskList);
                        binding.txtTotalParcels.setText(String.format(getString(R.string.pending_pickup_column_parcels), totalParcel));
                        binding.txtTotalSenderAddress.setText(String.format(getString(R.string.pending_pickup_column_sender_address), totalAddress));
                        binding.recyclerViewPendingPickupList.invokeState(EmptyStateRecyclerView.STATE_OK);
                    } else {
                        pickupPendingListAdapter.setOriginalPickupList(null);
                        binding.txtTotalParcels.setText(String.format(getString(R.string.pending_pickup_column_parcels), 0));
                        binding.txtTotalSenderAddress.setText(String.format(getString(R.string.pending_pickup_column_sender_address), 0));
                        binding.recyclerViewPendingPickupList.invokeState(EmptyStateRecyclerView.STATE_EMPTY);
                    }
                    binding.swipeLayout.setRefreshing(false);
                }
        );

        pendingPickupViewModel.getLoading().observe(this, isLoading -> {
                    if (isLoading) {
                        binding.txtTotalParcels.setText(String.format(getString(R.string.pending_pickup_column_parcels), 0));
                        binding.txtTotalSenderAddress.setText(String.format(getString(R.string.pending_pickup_column_sender_address), 0));
                        pickupPendingListAdapter.setOriginalPickupList(null);
                        binding.recyclerViewPendingPickupList.invokeState(EmptyStateRecyclerView.STATE_LOADING);
                    }
                }
        );

        pendingPickupViewModel.getError().observe(this, errorMsg -> {
                    if (!TextUtils.isEmpty(errorMsg)) {
                        binding.recyclerViewPendingPickupList.setStateDisplay(EmptyStateRecyclerView.STATE_ERROR,
                                new TextStateDisplay(this, errorMsg, ""));
                        binding.recyclerViewPendingPickupList.invokeState(EmptyStateRecyclerView.STATE_ERROR);
                        binding.swipeLayout.setRefreshing(false);
                    }
                }
        );
    }

    private void initToolbar() {
        getSupportActionBar().setTitle(getString(R.string.pending_pickup_screen_title));
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

    public void onOptionSelected(final ParcelProblem parcelProblem, final List<Parcel> parcelList) {
        if (parcelList != null && parcelProblem != null) {
            DialogHelper.showProgressDialog(this,
                    getString(R.string.pending_pickup_progress_msg_report_pickup_problem));

            totalProblematicParcels = parcelList.size();
            pickupProblemSubmitStatusMap = new HashMap<>();

            for (final Parcel parcel : parcelList) {
                pendingPickupViewModel.submitProblematicParcel(parcel.getParcelId(), parcelProblem.getId());
            }
        }
    }

    private void observeProblematicParcelSubmitResponse() {
        pendingPickupViewModel.getProblematicParcelSubmitSuccess().observe(this, parcelId -> {
                    pickupProblemSubmitStatusMap.put(parcelId, true);
                    checkProblematicPickupSubmissionCompletion();
                }
        );

        pendingPickupViewModel.getProblematicParcelSubmitLoading().observe(this, isLoading -> {
                    if (isLoading) {
                    }
                }
        );

        pendingPickupViewModel.getProblematicParcelSubmitError().observe(this, errorParcelId -> {
                    if (!TextUtils.isEmpty(errorParcelId)) {
                        pickupProblemSubmitStatusMap.put(errorParcelId, false);
                        checkProblematicPickupSubmissionCompletion();
                    }
                }
        );
    }

    private void checkProblematicPickupSubmissionCompletion() {
        if (pickupProblemSubmitStatusMap.size() == totalProblematicParcels) {
            boolean hasError = false;
            pendingPickupViewModel.loadPendingPickupTasks(true);

            for (Map.Entry<String, Boolean> entry : pickupProblemSubmitStatusMap.entrySet()) {
                final String parcelId = entry.getKey();
                final boolean isSuccess = entry.getValue();
                if (!isSuccess) {
                    hasError = true;
                    break;
                }
            }

            DialogHelper.dismissProgressDialog();

            if (hasError) {
                if (Variables.isNetworkConnected) {
                    DialogHelper.showAlertDialog(DialogHelper.Type.ERROR, "",
                            getString(R.string.pending_pickup_problematic_parcel_submission_failure),
                            "", this, true);
                } else {
                    DialogHelper.showAlertDialog(DialogHelper.Type.ERROR, "",
                            getString(R.string.error_no_internet),
                            "", this, true);
                }
            } else {
                DialogHelper.showAlertDialog(DialogHelper.Type.SUCCESS, "",
                        getString(R.string.pending_pickup_problematic_parcel_submission_success),
                        "", this, true);
            }
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
        pendingPickupViewModel.savePendingPickupOrder(pickupPendingListAdapter.getOriginalPickupList());
    }

    @Override
    public void onRefresh() {
        if (pendingPickupViewModel.isLoadingInProgress)
            binding.swipeLayout.setRefreshing(false);
        else {
            hideSoftKeyboard();
            binding.editTextSearchPendingPickup.setText("");
            pendingPickupViewModel.loadPendingPickupTasks(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
