package com.pingu.driverapp.ui.home.task.my_tasks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.pingu.driverapp.databinding.FragmentPickupMyTasksBinding;
import com.pingu.driverapp.ui.base.BaseActivity;
import com.pingu.driverapp.util.Constants;
import com.pingu.driverapp.util.DialogHelper;
import com.tylersuehr.esr.EmptyStateRecyclerView;
import com.tylersuehr.esr.TextStateDisplay;

import java.util.Map;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

public class PickupMyTasksFragment extends DaggerFragment implements PickupMyTaskAdapter.TaskOperationListener,
        SwipeRefreshLayout.OnRefreshListener {
    private FragmentPickupMyTasksBinding binding;
    private PickupMyTasksViewModel pickupMyTasksViewModel;
    private PickupMyTaskAdapter myTaskAdapter;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    public static Fragment newInstance() {
        PickupMyTasksFragment pickupListFragment = new PickupMyTasksFragment();
        return pickupListFragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        pickupMyTasksViewModel =
                ViewModelProviders.of(this, viewModelFactory).get(PickupMyTasksViewModel.class);
        observeRemoveTaskResponse();
        observeTaskListResponse();
        initRecyclerView();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pickup_my_tasks, container, false);

        return binding.getRoot();
    }

    private void observeTaskListResponse() {
        pickupMyTasksViewModel.getData().observe(this, taskList -> {
                    if (taskList != null && taskList.size() > 0) {
                        myTaskAdapter.setTaskList(taskList);
                        binding.txtTotalSenderAddress.setText(String.format(getString(R.string.pending_pickup_column_sender_address), taskList.size()));
                        binding.recyclerViewPickupMyTask.invokeState(EmptyStateRecyclerView.STATE_OK);
                    } else {
                        myTaskAdapter.setTaskList(taskList);
                        binding.txtTotalSenderAddress.setText(String.format(getString(R.string.pending_pickup_column_sender_address), 0));
                        binding.recyclerViewPickupMyTask.invokeState(EmptyStateRecyclerView.STATE_EMPTY);
                    }
                    binding.swipeLayout.setRefreshing(false);
                }
        );

        pickupMyTasksViewModel.getLoading().observe(this, isLoading -> {
                    if (isLoading) {
                        binding.txtTotalSenderAddress.setText(String.format(getString(R.string.pending_pickup_column_sender_address), 0));
                        myTaskAdapter.setTaskList(null);
                        binding.recyclerViewPickupMyTask.invokeState(EmptyStateRecyclerView.STATE_LOADING);
                    }
                }
        );

        pickupMyTasksViewModel.getError().observe(this, errorMsg -> {
                    if (!TextUtils.isEmpty(errorMsg)) {
                        binding.recyclerViewPickupMyTask.setStateDisplay(EmptyStateRecyclerView.STATE_ERROR,
                                new TextStateDisplay(getContext(), errorMsg, ""));
                        binding.recyclerViewPickupMyTask.invokeState(EmptyStateRecyclerView.STATE_ERROR);
                        binding.swipeLayout.setRefreshing(false);
                    }
                }
        );
    }

    private void observeRemoveTaskResponse() {
        pickupMyTasksViewModel.getRemoveTaskSuccess().observe(this, actionStatusMap -> {
                    if (actionStatusMap != null && actionStatusMap.size() > 0) {
                        pickupMyTasksViewModel.loadMyTasks(true);
                        DialogHelper.dismissProgressDialog();

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
                                    getString(R.string.operation_remove_task_success),
                                    null, getContext(), true);

                        } else {
                            DialogHelper.showAlertDialog(DialogHelper.Type.ERROR, "",
                                    getString(R.string.operation_remove_task_failure),
                                    null, getContext(), true);
                        }
                    }
                }
        );

        pickupMyTasksViewModel.getRemoveTaskLoading().observe(this, isLoading -> {
                    if (isLoading) {
                        DialogHelper.showProgressDialog(getContext(),
                                getString(R.string.pickup_list_progress_msg_cancelling_task));
                    }
                }
        );

        pickupMyTasksViewModel.getRemoveTaskError().observe(this, errorMsg -> {
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
        myTaskAdapter = new PickupMyTaskAdapter(getContext(), null, this);
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recyclerViewPickupMyTask.setLayoutManager(verticalLayoutManager);
        binding.recyclerViewPickupMyTask.setAdapter(myTaskAdapter);
        binding.recyclerViewPickupMyTask.setStateDisplay(EmptyStateRecyclerView.STATE_EMPTY,
                new TextStateDisplay(getContext(), getString(R.string.msg_list_empty), ""));
        binding.swipeLayout.setOnRefreshListener(this);
    }

    @Override
    public void onTaskRemoved(Task task) {
        if (task != null && task.getParcelList() != null && task.getParcelList().size() > 0) {
            String[] parcelIds = new String[task.getParcelList().size()];
            for (int i = 0; i < task.getParcelList().size(); i++) {
                parcelIds[i] = task.getParcelList().get(i).getParcelId();
            }
            pickupMyTasksViewModel.removeTask(parcelIds);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!pickupMyTasksViewModel.hasLoadedOnce)
            pickupMyTasksViewModel.loadMyTasks(true);
    }

    @Override
    public void setMenuVisibility(boolean isVisible) {
        super.setMenuVisibility(isVisible);
        if (!isVisible) {
            if (pickupMyTasksViewModel != null)
                pickupMyTasksViewModel.cleanUp();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onRefresh() {
        if (pickupMyTasksViewModel.isLoadingInProgress)
            binding.swipeLayout.setRefreshing(false);
        else
            pickupMyTasksViewModel.loadMyTasks(false);
    }
}
