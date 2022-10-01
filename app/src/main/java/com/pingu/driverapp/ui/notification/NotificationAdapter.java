package com.pingu.driverapp.ui.notification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pingu.driverapp.data.model.Announcement;
import com.pingu.driverapp.databinding.AdapterNotificationItemBinding;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private Context context;
    private List<Announcement> announcementList;

    public NotificationAdapter(Context context, List<Announcement> announcementList) {
        this.context = context;
        this.announcementList = announcementList;
    }

    public void setAnnouncementList(List<Announcement> announcementList) {
        this.announcementList = announcementList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        AdapterNotificationItemBinding itemBinding = AdapterNotificationItemBinding.inflate(layoutInflater, parent, false);
        return new NotificationViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Announcement announcement = announcementList.get(position);
        holder.bind(announcement);
    }

    @Override
    public int getItemCount() {
        return announcementList != null ? announcementList.size() : 0;
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder {
        private AdapterNotificationItemBinding binding;

        public NotificationViewHolder(AdapterNotificationItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Announcement announcement) {
            binding.setAnnouncementItem(announcement);
            binding.executePendingBindings();
        }
    }

    public class ClickHandlers {
        private AdapterNotificationItemBinding binding;

        public ClickHandlers(AdapterNotificationItemBinding binding) {
            this.binding = binding;
        }

        public void onItemSelected(View view, Announcement announcement) {
            Toast.makeText(context, "Notification item selected!", Toast.LENGTH_SHORT).show();
        }
    }
}

