package com.pingu.driverapp.ui.home.task.my_tasks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pingu.driverapp.data.model.Task;
import com.pingu.driverapp.databinding.AdapterPickupMyTaskItemBinding;

import java.util.List;

public class PickupMyTaskAdapter extends RecyclerView.Adapter<PickupMyTaskAdapter.PickupMyTaskViewHolder> {
    private Context context;
    private List<Task> taskList;
    private TaskOperationListener taskOperationListener;

    public interface TaskOperationListener {
        public void onTaskRemoved(Task task);
    }

    public PickupMyTaskAdapter(final Context context, final List<Task> taskList, final TaskOperationListener taskOperationListener) {
        this.context = context;
        this.taskList = taskList;
        this.taskOperationListener = taskOperationListener;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PickupMyTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        AdapterPickupMyTaskItemBinding itemBinding = AdapterPickupMyTaskItemBinding.inflate(layoutInflater, parent, false);
        return new PickupMyTaskViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PickupMyTaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.bind(task, position);
    }

    @Override
    public int getItemCount() {
        return taskList != null ? taskList.size() : 0;
    }

    class PickupMyTaskViewHolder extends RecyclerView.ViewHolder {
        private AdapterPickupMyTaskItemBinding binding;

        public PickupMyTaskViewHolder(AdapterPickupMyTaskItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Task task, int index) {
            binding.setIndex(index + 1);
            binding.setTask(task);
            binding.executePendingBindings();
            binding.setHandler(new ClickHandlers(binding));
        }
    }

    public class ClickHandlers {
        private AdapterPickupMyTaskItemBinding binding;

        public ClickHandlers(AdapterPickupMyTaskItemBinding binding) {
            this.binding = binding;
        }

        public void onRemoveTask(View view, Task task) {
            taskOperationListener.onTaskRemoved(task);
        }
    }
}

