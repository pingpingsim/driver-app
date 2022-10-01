package com.pingu.driverapp.ui.orders.history;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pingu.driverapp.R;
import com.pingu.driverapp.data.model.Parcel;
import com.pingu.driverapp.databinding.AdapterHistoryListItemBinding;
import com.pingu.driverapp.databinding.AdapterHistoryListSectionTitleBinding;
import com.pingu.driverapp.util.ParcelHelper;

import java.util.List;

public class HistoryListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Parcel> originalPickupList;
    private List<Parcel> pickupList;
    private int type;

    public static int SECTION_TITLE_VIEW = 1;
    public static int LIST_ITEM_VIEW = 2;

    public HistoryListAdapter(Context context, List<Parcel> pickupList, final int type) {
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

    @Override
    public int getItemViewType(int position) {

        switch (pickupList.get(position).getRowDataType()) {
            case 1:
                return SECTION_TITLE_VIEW;
            case 2:
                return LIST_ITEM_VIEW;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        if (viewType == SECTION_TITLE_VIEW) {
            AdapterHistoryListSectionTitleBinding sectionTitleBinding = AdapterHistoryListSectionTitleBinding.inflate(layoutInflater, parent, false);
            return new HistoryListSectionTitleViewHolder(sectionTitleBinding);
        } else {//LIST_ITEM_VIEW
            AdapterHistoryListItemBinding itemBinding = AdapterHistoryListItemBinding.inflate(layoutInflater, parent, false);
            return new HistoryListViewHolder(itemBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Parcel pickupItem = pickupList.get(position);

        if (holder.getItemViewType() == SECTION_TITLE_VIEW) {
            ((HistoryListSectionTitleViewHolder) holder).bind(pickupItem, position);
        } else {//LIST_ITEM_VIEW
            ((HistoryListViewHolder) holder).bind(pickupItem, position);
        }
    }

    @Override
    public int getItemCount() {
        return pickupList != null ? pickupList.size() : 0;
    }

    class HistoryListViewHolder extends RecyclerView.ViewHolder {
        private AdapterHistoryListItemBinding binding;

        public HistoryListViewHolder(AdapterHistoryListItemBinding binding) {
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

    class HistoryListSectionTitleViewHolder extends RecyclerView.ViewHolder {
        private AdapterHistoryListSectionTitleBinding binding;

        public HistoryListSectionTitleViewHolder(AdapterHistoryListSectionTitleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Parcel pickup, int index) {
            binding.setIndex(index + 1);
            binding.setPickupItem(pickup);
            binding.executePendingBindings();
        }
    }

    public class ClickHandlers {
        private AdapterHistoryListItemBinding binding;

        public ClickHandlers(AdapterHistoryListItemBinding binding) {
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

