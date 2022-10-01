package com.pingu.driverapp.ui.home.task.all_tasks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pingu.driverapp.BaseApplication;
import com.pingu.driverapp.data.model.Task;
import com.pingu.driverapp.databinding.AdapterPickupAllTaskItemBinding;

import java.util.List;

public class PickupAllTaskAdapter extends RecyclerView.Adapter<PickupAllTaskAdapter.PickupAllTaskViewHolder> {
    private Context context;
    private List<Task> taskList;

    public PickupAllTaskAdapter(Context context, List<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PickupAllTaskAdapter.PickupAllTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        AdapterPickupAllTaskItemBinding itemBinding = AdapterPickupAllTaskItemBinding.inflate(layoutInflater, parent, false);
        return new PickupAllTaskAdapter.PickupAllTaskViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PickupAllTaskAdapter.PickupAllTaskViewHolder holder, int position) {
        Task pickupItem = taskList.get(position);
        holder.bind(pickupItem, position);
    }

    @Override
    public int getItemCount() {
        return taskList != null ? taskList.size() : 0;
    }

    class PickupAllTaskViewHolder extends RecyclerView.ViewHolder {
        private AdapterPickupAllTaskItemBinding binding;

        public PickupAllTaskViewHolder(AdapterPickupAllTaskItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Task task, int index) {
            binding.setIndex(index + 1);
            binding.setTaskItem(task);
            binding.executePendingBindings();
            binding.setHandler(new PickupAllTaskAdapter.ClickHandlers(binding));
        }
    }

    public class ClickHandlers {
        private AdapterPickupAllTaskItemBinding binding;

        public ClickHandlers(AdapterPickupAllTaskItemBinding binding) {
            this.binding = binding;
        }

        public void onItemSelected(View view, Task task) {
        }
    }
}

