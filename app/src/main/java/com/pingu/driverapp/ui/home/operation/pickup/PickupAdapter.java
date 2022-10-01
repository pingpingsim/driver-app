package com.pingu.driverapp.ui.home.operation.pickup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pingu.driverapp.data.model.Parcel;
import com.pingu.driverapp.databinding.AdapterOperationPickupItemBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PickupAdapter extends RecyclerView.Adapter<PickupAdapter.OperationPickupListViewHolder> {
    private Context context;
    private List<Parcel> pickupList;
    private ParcelOperationListener parcelOperationListener;

    public interface ParcelOperationListener {
        public void onParcelCanceled();
    }

    public PickupAdapter(Context context, List<Parcel> pickupList, ParcelOperationListener parcelOperationListener) {
        this.context = context;
        this.pickupList = pickupList;
        this.parcelOperationListener = parcelOperationListener;
    }

    public void setPickupList(List<Parcel> pickupList) {
        this.pickupList = pickupList;
        notifyDataSetChanged();
    }

    public List<Parcel> getPickupList() {
        return pickupList;
    }

    public boolean addParcel(final Parcel pickupParcel) {
        if (pickupList == null)
            pickupList = new ArrayList<>();

        if (!pickupList.contains(pickupParcel)) {
            pickupList.add(0, pickupParcel);
            notifyDataSetChanged();
            return true;
        } else {
            return false;
        }
    }

    public boolean removeParcel(final Parcel targetPickup) {
        if (pickupList != null && pickupList.size() > 0) {
            pickupList.remove(targetPickup);
            notifyDataSetChanged();

            return true;
        }

        return false;
    }

    @NonNull
    @Override
    public OperationPickupListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        AdapterOperationPickupItemBinding itemBinding = AdapterOperationPickupItemBinding.inflate(layoutInflater, parent, false);
        return new OperationPickupListViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull OperationPickupListViewHolder holder, int position) {
        Parcel pickupItem = pickupList.get(position);
        holder.bind(pickupItem);
    }

    @Override
    public int getItemCount() {
        return pickupList != null ? pickupList.size() : 0;
    }

    class OperationPickupListViewHolder extends RecyclerView.ViewHolder {
        private AdapterOperationPickupItemBinding binding;

        public OperationPickupListViewHolder(AdapterOperationPickupItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Parcel pickup) {
            binding.setPickupItem(pickup);
            binding.executePendingBindings();
            binding.setHandler(new ClickHandlers(binding));
        }
    }

    public class ClickHandlers {
        private AdapterOperationPickupItemBinding binding;

        public ClickHandlers(AdapterOperationPickupItemBinding binding) {
            this.binding = binding;
        }

        public void onRemoveParcel(View view, Parcel pickup) {
            removeParcel(pickup);
            parcelOperationListener.onParcelCanceled();
        }
    }
}

