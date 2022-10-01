package com.pingu.driverapp.ui.home.task.available_tasks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pingu.driverapp.data.model.Task;
import com.pingu.driverapp.databinding.AdapterPickupAvailableTaskItemBinding;

import java.util.List;

public class PickupAvailableTaskAdapter extends RecyclerView.Adapter<PickupAvailableTaskAdapter.PickupAvailableTaskViewHolder> {
    private Context context;
    private List<Task> taskList;
    private RadioButton lastRadioBtn;

    public PickupAvailableTaskAdapter(Context context, List<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
        notifyDataSetChanged();
    }

    public Task getSelectedPickupItem() {
        if (taskList != null && taskList.size() > 0) {
            for (Task task : taskList) {
                if (task.isChecked()) {
                    return task;
                }
            }
        }
        return null;
    }

    @NonNull
    @Override
    public PickupAvailableTaskAdapter.PickupAvailableTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        AdapterPickupAvailableTaskItemBinding itemBinding = AdapterPickupAvailableTaskItemBinding.inflate(layoutInflater, parent, false);
        return new PickupAvailableTaskAdapter.PickupAvailableTaskViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PickupAvailableTaskAdapter.PickupAvailableTaskViewHolder holder, int position) {
        Task pickupItem = taskList.get(position);
        holder.bind(pickupItem, position);
    }

    @Override
    public int getItemCount() {
        return taskList != null ? taskList.size() : 0;
    }

    class PickupAvailableTaskViewHolder extends RecyclerView.ViewHolder {
        private AdapterPickupAvailableTaskItemBinding binding;

        public PickupAvailableTaskViewHolder(AdapterPickupAvailableTaskItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Task task, int index) {
            binding.setIndex(index + 1);
            binding.setTask(task);
            binding.executePendingBindings();
            binding.setHandler(new PickupAvailableTaskAdapter.ClickHandlers(binding));
            binding.panelData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new ClickHandlers(binding).onItemSelected(binding.radioBtn, binding.getTask(), index);
                }
            });
        }
    }

    public class ClickHandlers {
        private AdapterPickupAvailableTaskItemBinding binding;

        public ClickHandlers(AdapterPickupAvailableTaskItemBinding binding) {
            this.binding = binding;
        }

        public void onItemSelected(View view, Task task, int index) {
            final RadioButton currentRadioBtn = (RadioButton) (view);
            boolean isSame = false;

            if (lastRadioBtn != null) {
                if (lastRadioBtn == currentRadioBtn) {
                    isSame = true;
                    if (currentRadioBtn.isChecked()) {
                        currentRadioBtn.setChecked(false);
                        lastRadioBtn = null;
                    } else {
                        currentRadioBtn.setChecked(true);
                    }
                } else
                    lastRadioBtn.setChecked(false);
            }

            if (!isSame) {
                currentRadioBtn.setChecked(true);
                lastRadioBtn = currentRadioBtn;
            }
        }
    }
}

