package com.pingu.driverapp.ui.home.pending.pickup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pingu.driverapp.R;
import com.pingu.driverapp.data.model.Parcel;
import com.pingu.driverapp.data.model.Task;
import com.pingu.driverapp.databinding.AdapterPendingPickupListItemBinding;
import com.pingu.driverapp.ui.home.operation.pickup.pickupproblem.PickupProblemDataAdapter;
import com.pingu.driverapp.util.DialogHelper;
import com.pingu.driverapp.util.ItemMoveCallback;
import com.pingu.driverapp.util.ParcelHelper;
import com.pingu.driverapp.widget.PickupProblemDialog;

import java.util.Collections;
import java.util.List;

public class PendingPickupListAdapter extends RecyclerView.Adapter<PendingPickupListAdapter.PendingPickupListViewHolder>
        implements ItemMoveCallback.ItemTouchHelperContract {
    private Context context;
    private List<Task> originalPickupList;
    private List<Task> pickupList;
    private PickupProblemDialog.PickupProblemOptionClickListener pickupProblemOptionClickListener;

    public PendingPickupListAdapter(Context context, List<Task> pickupList, PickupProblemDialog.PickupProblemOptionClickListener listener) {
        this.context = context;
        this.pickupList = pickupList;
        this.originalPickupList = pickupList;
        this.pickupProblemOptionClickListener = listener;
    }

    public void setOriginalPickupList(List<Task> pickupList) {
        this.originalPickupList = pickupList;
        this.pickupList = pickupList;//Unfiltered
        notifyDataSetChanged();
    }

    public List<Task> getPickupList() {
        return pickupList;
    }

    public List<Task> getOriginalPickupList() {
        return originalPickupList;
    }

    public void filterPickList(List<Task> filteredPickupList) {
        this.pickupList = filteredPickupList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PendingPickupListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        AdapterPendingPickupListItemBinding itemBinding = AdapterPendingPickupListItemBinding.inflate(layoutInflater, parent, false);
        return new PendingPickupListViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingPickupListViewHolder holder, int position) {
        Task pickupItem = pickupList.get(position);
        holder.bind(pickupItem, position);
    }

    @Override
    public int getItemCount() {
        return pickupList != null ? pickupList.size() : 0;
    }

    class PendingPickupListViewHolder extends RecyclerView.ViewHolder {
        private AdapterPendingPickupListItemBinding binding;

        public PendingPickupListViewHolder(AdapterPendingPickupListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Task pickup, int index) {
            binding.setIndex(index + 1);
            binding.setTask(pickup);
            binding.executePendingBindings();
            binding.setHandler(new ClickHandlers(binding));
        }
    }

    public class ClickHandlers {
        private AdapterPendingPickupListItemBinding binding;

        public ClickHandlers(AdapterPendingPickupListItemBinding binding) {
            this.binding = binding;
        }

        public void onMapMenuSelected(View view, Task task) {
            ParcelHelper.openMap(context, task.getAddress());
        }

        public void onCallMenuSelected(View view, Task task) {
            ParcelHelper.callNumber(context, task.getContactNumber());
        }

        public void onWhatsappMenuSelected(View view, Task task) {
            ParcelHelper.openWhatsApp(task.getContactNumber(), context.getString(R.string.whatsAppMsgPendingPickup), context);
        }

        public void onPickupProblemSelected(View view, Task task) {
            PickupProblemDialog pickupProblemDialog =
                    new PickupProblemDialog(((PendingPickupListActivity) context), pickupProblemOptionClickListener, task.getParcelList());
            pickupProblemDialog.show();
        }
    }

    public void onRowMoved(final int fromPosition, final int toPosition) {
        try {
            if (pickupList.size() != originalPickupList.size()) {
                DialogHelper.showAlertDialog(DialogHelper.Type.INFO, "", "Reordering not allowed in filter mode", "", context, true);
                return;
            }
            if (fromPosition < toPosition) {

                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(originalPickupList, i, i + 1);
                }

            } else {
                for (int i = (toPosition + 1); i >= fromPosition; i--) {
                    Collections.swap(originalPickupList, i, i - 1);

                }
            }
            notifyItemMoved(fromPosition, toPosition);
        } catch (Exception ex) {

        }
    }
}

