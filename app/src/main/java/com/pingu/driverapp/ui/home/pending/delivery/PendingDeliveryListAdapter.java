package com.pingu.driverapp.ui.home.pending.delivery;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pingu.driverapp.R;
import com.pingu.driverapp.data.model.Parcel;
import com.pingu.driverapp.data.model.Task;
import com.pingu.driverapp.databinding.AdapterPendingDeliveryListItemBinding;
import com.pingu.driverapp.ui.home.pending.delivery.details.DeliveryDetailsActivity;
import com.pingu.driverapp.util.Constants;
import com.pingu.driverapp.util.ItemMoveCallback;
import com.pingu.driverapp.util.ParcelHelper;

import java.util.Collections;
import java.util.List;

public class PendingDeliveryListAdapter extends RecyclerView.Adapter<PendingDeliveryListAdapter.PendingDeliveryListViewHolder>
        implements ItemMoveCallback.ItemTouchHelperContract {
    private Context context;
    private List<Parcel> pickupList;
    private List<Parcel> originalPickupList;

    public PendingDeliveryListAdapter(Context context, List<Parcel> pickupList) {
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
    public PendingDeliveryListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        AdapterPendingDeliveryListItemBinding itemBinding = AdapterPendingDeliveryListItemBinding.inflate(layoutInflater, parent, false);
        return new PendingDeliveryListViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingDeliveryListViewHolder holder, int position) {
        Parcel pickupItem = pickupList.get(position);
        holder.bind(pickupItem, position);
    }

    @Override
    public int getItemCount() {
        return pickupList != null ? pickupList.size() : 0;
    }

    class PendingDeliveryListViewHolder extends RecyclerView.ViewHolder {
        private AdapterPendingDeliveryListItemBinding binding;

        public PendingDeliveryListViewHolder(AdapterPendingDeliveryListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Parcel pickup, int index) {
            binding.setIndex(index + 1);
            binding.setPickupItem(pickup);
            binding.executePendingBindings();
            binding.setHandler(new ClickHandlers(binding));
        }
    }

    public class ClickHandlers {
        private AdapterPendingDeliveryListItemBinding binding;

        public ClickHandlers(AdapterPendingDeliveryListItemBinding binding) {
            this.binding = binding;
        }

        public void onMapMenuSelected(View view, Parcel pickup) {
            ParcelHelper.openMap(context, pickup.getRecipientAddress());
        }

        public void onCallMenuSelected(View view, Parcel pickup) {
            ParcelHelper.callNumber(context, pickup.getRecipientContactNumber());
        }

        public void onWhatsappMenuSelected(View view, Parcel pickup) {
            final String message = String.format(context.getString(R.string.whatsAppMsgOutForDelivery), pickup.getParcelId());
            ParcelHelper.openWhatsApp(pickup.getRecipientContactNumber(), message, context);
        }

        public void onDeliveryItemSelected(View view, Parcel pickup) {
            Intent deliveryDetailsIntent = new Intent(context, DeliveryDetailsActivity.class);
            deliveryDetailsIntent.putExtra(Constants.INTENT_EXTRA_DELIVERY_ITEM, new Gson().toJson(pickup));
            ((PendingDeliveryListActivity) context).startActivityForResult(deliveryDetailsIntent,
                    PendingDeliveryListActivity.DELIVERY_DETAILS_REQUEST_CODE);
        }
    }

    public void onRowMoved(final int fromPosition, final int toPosition) {
        if (fromPosition < toPosition) {

            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(pickupList, i, i + 1);
            }

        } else {
            for (int i = (toPosition + 1); i >= fromPosition; i--) {
                Collections.swap(pickupList, i, i - 1);

            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }
}

