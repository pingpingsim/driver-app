package com.pingu.driverapp.ui.home.task.all_tasks;

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
import com.pingu.driverapp.databinding.FragmentPickupAllTasksBinding;
import com.tylersuehr.esr.EmptyStateRecyclerView;
import com.tylersuehr.esr.TextStateDisplay;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

public class PickupAllTasksFragment extends DaggerFragment implements SwipeRefreshLayout.OnRefreshListener {
    private FragmentPickupAllTasksBinding binding;
    private PickupAllTasksViewModel pickupAllTasksViewModel;
    private PickupAllTaskAdapter pickupAllTaskAdapter;
    @Inject
    ViewModelProvider.Factory viewModelFactory;

    public static Fragment newInstance() {
        PickupAllTasksFragment pickupListFragment = new PickupAllTasksFragment();
        return pickupListFragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        pickupAllTasksViewModel =
                ViewModelProviders.of(this, viewModelFactory).get(PickupAllTasksViewModel.class);
        observeTaskListResponse();
        initRecyclerView();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pickup_all_tasks, container, false);

        return binding.getRoot();
    }

    private void initRecyclerView() {
        binding.txtTotalSenderAddress.setText(String.format(getString(R.string.pending_pickup_column_sender_address), 0));
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recyclerViewPickupAllTask.setLayoutManager(verticalLayoutManager);
        pickupAllTaskAdapter = new PickupAllTaskAdapter(getContext(), null);
        binding.recyclerViewPickupAllTask.setAdapter(pickupAllTaskAdapter);
        binding.recyclerViewPickupAllTask.setStateDisplay(EmptyStateRecyclerView.STATE_EMPTY,
                new TextStateDisplay(getContext(), getString(R.string.msg_list_empty), ""));
        binding.swipeLayout.setOnRefreshListener(this);
    }

    private void observeTaskListResponse() {
        pickupAllTasksViewModel.getData().observe(this, taskList -> {
                    if (taskList != null && taskList.size() > 0) {
                        pickupAllTaskAdapter.setTaskList(taskList);
                        binding.txtTotalSenderAddress.setText(String.format(getString(R.string.pending_pickup_column_sender_address), taskList.size()));
                        binding.recyclerViewPickupAllTask.invokeState(EmptyStateRecyclerView.STATE_OK);
                    } else {
                        pickupAllTaskAdapter.setTaskList(taskList);
                        binding.txtTotalSenderAddress.setText(String.format(getString(R.string.pending_pickup_column_sender_address), 0));
                        binding.recyclerViewPickupAllTask.invokeState(EmptyStateRecyclerView.STATE_EMPTY);
                    }
                    binding.swipeLayout.setRefreshing(false);
                }
        );

        pickupAllTasksViewModel.getLoading().observe(this, isLoading -> {
                    if (isLoading) {
                        binding.txtTotalSenderAddress.setText(String.format(getString(R.string.pending_pickup_column_sender_address), 0));
                        pickupAllTaskAdapter.setTaskList(null);
                        binding.recyclerViewPickupAllTask.invokeState(EmptyStateRecyclerView.STATE_LOADING);
                    }
                }
        );

        pickupAllTasksViewModel.getError().observe(this, errorMsg -> {
                    if (!TextUtils.isEmpty(errorMsg)) {
                        binding.recyclerViewPickupAllTask.setStateDisplay(EmptyStateRecyclerView.STATE_ERROR,
                                new TextStateDisplay(getContext(), errorMsg, ""));
                        binding.recyclerViewPickupAllTask.invokeState(EmptyStateRecyclerView.STATE_ERROR);
                        binding.swipeLayout.setRefreshing(false);
                    }
                }
        );
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!pickupAllTasksViewModel.hasLoadedOnce)
            pickupAllTasksViewModel.loadAllTasks(true);
    }

    @Override
    public void setMenuVisibility(boolean isVisible) {
        super.setMenuVisibility(isVisible);
        if (!isVisible) {
            if (pickupAllTasksViewModel != null)
                pickupAllTasksViewModel.cleanUp();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onRefresh() {
        if (pickupAllTasksViewModel.isLoadingInProgress)
            binding.swipeLayout.setRefreshing(false);
        else
            pickupAllTasksViewModel.loadAllTasks(false);
    }
}
