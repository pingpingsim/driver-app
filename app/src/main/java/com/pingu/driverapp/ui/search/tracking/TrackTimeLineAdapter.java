package com.pingu.driverapp.ui.search.tracking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pingu.driverapp.data.model.Status;
import com.pingu.driverapp.databinding.AdapterTrackTimeLineItemBinding;

import java.util.List;

public class TrackTimeLineAdapter extends RecyclerView.Adapter<TrackTimeLineAdapter.TrackTimeLineViewHolder> {
    private Context context;
    private List<Status> statusList;

    public TrackTimeLineAdapter(Context context, List<Status> statusList) {
        this.context = context;
        this.statusList = statusList;
    }

    public void setStatusList(List<Status> statusList) {
        this.statusList = statusList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TrackTimeLineAdapter.TrackTimeLineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        AdapterTrackTimeLineItemBinding itemBinding = AdapterTrackTimeLineItemBinding.inflate(layoutInflater, parent, false);
        return new TrackTimeLineAdapter.TrackTimeLineViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackTimeLineAdapter.TrackTimeLineViewHolder holder, int position) {
        Status status = statusList.get(position);
        holder.bind(status, position, (statusList != null ? statusList.size() : 0));
    }

    @Override
    public int getItemCount() {
        return statusList != null ? statusList.size() : 0;
    }

    class TrackTimeLineViewHolder extends RecyclerView.ViewHolder {
        private AdapterTrackTimeLineItemBinding binding;

        public TrackTimeLineViewHolder(AdapterTrackTimeLineItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Status status, int index, int totalItemCount) {
            binding.setIndex(index);
            binding.setStatus(status);
            binding.setTotalItemCount(totalItemCount);
            binding.executePendingBindings();
            binding.setHandler(new TrackTimeLineAdapter.ClickHandlers(binding));
        }
    }

    public class ClickHandlers {
        private AdapterTrackTimeLineItemBinding binding;

        public ClickHandlers(AdapterTrackTimeLineItemBinding binding) {
            this.binding = binding;
        }
    }
}

