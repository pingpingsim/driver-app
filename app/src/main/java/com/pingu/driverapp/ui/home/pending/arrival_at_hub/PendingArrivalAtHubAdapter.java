package com.pingu.driverapp.ui.home.pending.arrival_at_hub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pingu.driverapp.data.model.Parcel;
import com.pingu.driverapp.databinding.AdapterPendingArrivalAtHubItemBinding;

import java.util.List;

public class PendingArrivalAtHubAdapter extends RecyclerView.Adapter<PendingArrivalAtHubAdapter.PendingArrivalAtHubViewHolder> {
    private Context context;
    private List<Parcel> pickupList;
    private List<Parcel> originalPickupList;

    public PendingArrivalAtHubAdapter(Context context, List<Parcel> pickupList) {
        this.context = context;
        this.pickupList = pickupList;
        this.originalPickupList = pickupList;
    }

    public void setOriginalPickupList(List<Parcel> pickupList) {
        this.originalPickupList = pickupList;
        this.pickupList = pickupList;//Unfiltered
        notifyDataSetChanged();
    }

    public List<Parcel> getOriginalPickupList() {
        return originalPickupList;
    }

    public void filterPickList(List<Parcel> filteredPickupList) {
        this.pickupList = filteredPickupList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PendingArrivalAtHubViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        AdapterPendingArrivalAtHubItemBinding itemBinding = AdapterPendingArrivalAtHubItemBinding.inflate(layoutInflater, parent, false);
        return new PendingArrivalAtHubViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingArrivalAtHubViewHolder holder, int position) {
        Parcel pickupItem = pickupList.get(position);
        holder.bind(pickupItem, position + 1);
    }

    @Override
    public int getItemCount() {
        return pickupList != null ? pickupList.size() : 0;
    }

    class PendingArrivalAtHubViewHolder extends RecyclerView.ViewHolder {
        private AdapterPendingArrivalAtHubItemBinding binding;

        public PendingArrivalAtHubViewHolder(AdapterPendingArrivalAtHubItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Parcel pickup, int no) {
            binding.setNo(no);
            binding.setPickupItem(pickup);
            binding.executePendingBindings();
            binding.setHandler(new ClickHandlers(binding));
        }
    }

    public class ClickHandlers {
        private AdapterPendingArrivalAtHubItemBinding binding;

        public ClickHandlers(AdapterPendingArrivalAtHubItemBinding binding) {
            this.binding = binding;
        }
    }
}

