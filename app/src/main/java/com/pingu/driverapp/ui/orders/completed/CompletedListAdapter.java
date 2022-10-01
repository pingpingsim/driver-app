package com.pingu.driverapp.ui.orders.completed;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pingu.driverapp.R;
import com.pingu.driverapp.data.model.Parcel;
import com.pingu.driverapp.databinding.AdapterCompletedListItemBinding;
import com.pingu.driverapp.util.ParcelHelper;

import java.util.List;

public class CompletedListAdapter extends RecyclerView.Adapter<CompletedListAdapter.CompletedListViewHolder> {
    private Context context;
    private List<Parcel> originalPickupList;
    private List<Parcel> pickupList;
    private int type;

    public CompletedListAdapter(final Context context, final List<Parcel> pickupList, final int type) {
        this.context = context;
        this.pickupList = pickupList;
        this.originalPickupList = pickupList;
        this.type = type;
    }

    public void setOriginalPickupList(List<Parcel> originalPickupList) {
        this.originalPickupList = originalPickupList;
        this.pickupList = originalPickupList;
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
    public CompletedListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        AdapterCompletedListItemBinding itemBinding = AdapterCompletedListItemBinding.inflate(layoutInflater, parent, false);
        return new CompletedListViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CompletedListViewHolder holder, int position) {
        Parcel pickupItem = pickupList.get(position);
        holder.bind(pickupItem, position);
    }

    @Override
    public int getItemCount() {
        return pickupList != null ? pickupList.size() : 0;
    }

    class CompletedListViewHolder extends RecyclerView.ViewHolder {
        private AdapterCompletedListItemBinding binding;

        public CompletedListViewHolder(AdapterCompletedListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Parcel pickup, int index) {
            binding.setIndex(index + 1);
            binding.setScreenType(type);
            binding.setPickupItem(pickup);
            binding.executePendingBindings();
            binding.setHandler(new ClickHandlers(binding));
            binding.txtRefNo.setTypeface(binding.txtRefNo.getTypeface(), (type == 1) ? Typeface.NORMAL : Typeface.BOLD);
        }
    }

    public class ClickHandlers {
        private AdapterCompletedListItemBinding binding;

        public ClickHandlers(AdapterCompletedListItemBinding binding) {
            this.binding = binding;
        }

        public void onCallMenuSelected(View view, Parcel parcel) {
            ParcelHelper.callNumber(context, parcel.getRecipientContactNumber());
        }

        public void onWhatsappMenuSelected(View view, Parcel parcel) {
            final String message = String.format(context.getString(R.string.whatsAppMsgCompleted), parcel.getParcelId());
            ParcelHelper.openWhatsApp(parcel.getRecipientContactNumber(), message, context);
        }
    }
}

