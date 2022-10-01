package com.pingu.driverapp.ui.home.task.available_tasks;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pingu.driverapp.R;
import com.pingu.driverapp.data.model.ActionStatus;
import com.pingu.driverapp.data.model.Task;
import com.pingu.driverapp.databinding.FragmentPickupAvailableTasksBinding;
import com.pingu.driverapp.util.Constants;
import com.pingu.driverapp.util.DialogHelper;
import com.tylersuehr.esr.EmptyStateRecyclerView;
import com.tylersuehr.esr.TextStateDisplay;

import java.util.Map;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

public class PickupAvailableTasksFragment extends DaggerFragment implements SwipeRefreshLayout.OnRefreshListener {
    private FragmentPickupAvailableTasksBinding binding;
    private PickupAvailableTasksViewModel pickupAvailableTasksViewModel;
    private PickupAvailableTaskAdapter availableTaskAdapter;
    @Inject
    ViewModelProvider.Factory viewModelFactory;

    public static Fragment newInstance() {
        PickupAvailableTasksFragment pickupListFragment = new PickupAvailableTasksFragment();
        return pickupListFragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        pickupAvailableTasksViewModel =
                ViewModelProviders.of(this, viewModelFactory).get(PickupAvailableTasksViewModel.class);
        observeTaskListResponse();
        observeAcceptTaskResponse();
        initRecyclerView();
        initClickEvents();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pickup_available_tasks, container, false);

        return binding.getRoot();
    }

    private void initClickEvents() {
        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Task selectedTask = availableTaskAdapter.getSelectedPickupItem();
                if (selectedTask != null) {

                    if (selectedTask != null && selectedTask.getParcelList() != null && selectedTask.getParcelList().size() > 0) {
                        String[] parcelIds = new String[selectedTask.getParcelList().size()];
                        for (int i = 0; i < selectedTask.getParcelList().size(); i++) {
                            parcelIds[i] = selectedTask.getParcelList().get(i).getParcelId();
                        }
                        pickupAvailableTasksViewModel.acceptTask(parcelIds);
                    }

                } else {
                    DialogHelper.showAlertDialog(DialogHelper.Type.ERROR, "",
                            getString(R.string.operation_validation_task_at_least_one),
                            null, getContext(), true);

                }
            }
        });
    }

    private void observeTaskListResponse() {
        pickupAvailableTasksViewModel.getData().observe(this, taskList -> {
                    if (taskList != null && taskList.size() > 0) {
                        availableTaskAdapter.setTaskList(taskList);
                        binding.txtTotalSenderAddress.setText(String.format(getString(R.string.pending_pickup_column_sender_address), taskList.size()));
                        binding.panelConfirmBtn.setVisibility(View.VISIBLE);
                        binding.recyclerViewPickupAvailableTask.invokeState(EmptyStateRecyclerView.STATE_OK);
                    } else {
                        availableTaskAdapter.setTaskList(taskList);
                        binding.txtTotalSenderAddress.setText(String.format(getString(R.string.pending_pickup_column_sender_address), 0));
                        binding.panelConfirmBtn.setVisibility(View.GONE);
                        binding.recyclerViewPickupAvailableTask.invokeState(EmptyStateRecyclerView.STATE_EMPTY);
                    }
                    binding.swipeLayout.setRefreshing(false);
                }
        );

        pickupAvailableTasksViewModel.getLoading().observe(this, isLoading -> {
                    if (isLoading) {
                        availableTaskAdapter.setTaskList(null);
                        binding.txtTotalSenderAddress.setText(String.format(getString(R.string.pending_pickup_column_sender_address), 0));
                        binding.panelConfirmBtn.setVisibility(View.GONE);
                        binding.recyclerViewPickupAvailableTask.invokeState(EmptyStateRecyclerView.STATE_LOADING);
                    }
                }
        );

        pickupAvailableTasksViewModel.getError().observe(this, errorMsg -> {
                    if (!TextUtils.isEmpty(errorMsg)) {
                        binding.panelConfirmBtn.setVisibility(View.GONE);
                        binding.recyclerViewPickupAvailableTask.setStateDisplay(EmptyStateRecyclerView.STATE_ERROR,
                                new TextStateDisplay(getContext(), errorMsg, ""));
                        binding.recyclerViewPickupAvailableTask.invokeState(EmptyStateRecyclerView.STATE_ERROR);
                        binding.swipeLayout.setRefreshing(false);
                    }
                }
        );
    }

    private void observeAcceptTaskResponse() {
        pickupAvailableTasksViewModel.getAcceptTaskSuccess().observe(this, actionStatusMap -> {
                    pickupAvailableTasksViewModel.loadAvailableTasks(true);
                    DialogHelper.dismissProgressDialog();

                    if (actionStatusMap != null && actionStatusMap.size() > 0) {

                        String errorMsg = "";
                        for (Map.Entry<String, ActionStatus> entry : actionStatusMap.entrySet()) {
                            final String parcelId = entry.getKey();
                            final ActionStatus actionStatus = entry.getValue();

                            if (!(actionStatus != null &&
                                    actionStatus.getStatus() == Constants.API_STATUS_SUCCESS)) {
                                errorMsg += String.format("%1$s:%2$s\n", parcelId, actionStatus.getMessage());
                                break;
                            }
                        }

                        if (TextUtils.isEmpty(errorMsg)) {
                            DialogHelper.showAlertDialog(DialogHelper.Type.SUCCESS, "",
                                    getString(R.string.operation_accept_task_success),
                                    null, getContext(), true);
                        } else {
                            DialogHelper.showAlertDialog(DialogHelper.Type.ERROR, "",
                                    getString(R.string.operation_accept_task_failure),
                                    null, getContext(), true);
                        }
                    }
                }
        );

        pickupAvailableTasksViewModel.getAcceptTaskLoading().observe(this, isLoading -> {
                    if (isLoading) {
                        DialogHelper.showProgressDialog(getContext(),
                                getString(R.string.pickup_list_progress_msg_accepting_task));
                    }
                }
        );

        pickupAvailableTasksViewModel.getAcceptTaskError().observe(this, errorMsg -> {
                    if (!TextUtils.isEmpty(errorMsg)) {
                        DialogHelper.dismissProgressDialog();
                        DialogHelper.showAlertDialog(DialogHelper.Type.ERROR, "", errorMsg,
                                null, getContext(), true);
                    }
                }
        );
    }

    private void initRecyclerView() {
        binding.txtTotalSenderAddress.setText(String.format(getString(R.string.pending_pickup_column_sender_address), 0));
        availableTaskAdapter = new PickupAvailableTaskAdapter(getContext(), null);
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recyclerViewPickupAvailableTask.setLayoutManager(verticalLayoutManager);
        binding.recyclerViewPickupAvailableTask.setAdapter(availableTaskAdapter);
        binding.recyclerViewPickupAvailableTask.setStateDisplay(EmptyStateRecyclerView.STATE_EMPTY,
                new TextStateDisplay(getContext(), getString(R.string.msg_list_empty), ""));
        binding.swipeLayout.setOnRefreshListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!pickupAvailableTasksViewModel.hasLoadedOnce)
            pickupAvailableTasksViewModel.loadAvailableTasks(true);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void setMenuVisibility(boolean isVisible) {
        super.setMenuVisibility(isVisible);
        if (!isVisible) {
            if (pickupAvailableTasksViewModel != null)
                pickupAvailableTasksViewModel.cleanUp();
        }
    }

    @Override
    public void onRefresh() {
        if (pickupAvailableTasksViewModel.isLoadingInProgress)
            binding.swipeLayout.setRefreshing(false);
        else
            pickupAvailableTasksViewModel.loadAvailableTasks(false);
    }
}
