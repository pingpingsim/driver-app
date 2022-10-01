package com.pingu.driverapp.ui.home.operation.problematicparcel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pingu.driverapp.data.model.ParcelProblem;
import com.pingu.driverapp.databinding.AdapterParcelProblemListItemBinding;
import com.pingu.driverapp.databinding.AdapterParcelProblemListSectionTitleBinding;

import java.util.List;

public class ParcelProblemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<ParcelProblem> originalParcelProblemList;
    private List<ParcelProblem> parcelProblemList;
    private ParcelProblemOptionClickListener parcelProblemOptionClickListener;

    public static int SECTION_TITLE_VIEW = 1;
    public static int LIST_ITEM_VIEW = 2;

    public ParcelProblemAdapter(final Context context, final List<ParcelProblem> parcelProblemList,
                                final ParcelProblemOptionClickListener parcelProblemOptionClickListener) {
        this.context = context;
        this.parcelProblemList = parcelProblemList;
        this.originalParcelProblemList = parcelProblemList;
        this.parcelProblemOptionClickListener = parcelProblemOptionClickListener;
    }

    public interface ParcelProblemOptionClickListener {
        void onOptionSelected(ParcelProblem parcelProblem);
    }

    public void setOriginalParcelProblemList(List<ParcelProblem> originalParcelProblemList) {
        this.originalParcelProblemList = originalParcelProblemList;
        this.parcelProblemList = originalParcelProblemList;
        notifyDataSetChanged();
    }

    public List<ParcelProblem> getOriginalParcelProblemList() {
        return originalParcelProblemList;
    }

    public void filterParcelProblemList(List<ParcelProblem> filteredParcelProblemList) {
        this.parcelProblemList = filteredParcelProblemList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {

        switch (parcelProblemList.get(position).getRowDataType()) {
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
            AdapterParcelProblemListSectionTitleBinding sectionTitleBinding = AdapterParcelProblemListSectionTitleBinding.inflate(layoutInflater, parent, false);
            return new HistoryListSectionTitleViewHolder(sectionTitleBinding);
        } else {//LIST_ITEM_VIEW
            AdapterParcelProblemListItemBinding itemBinding = AdapterParcelProblemListItemBinding.inflate(layoutInflater, parent, false);
            return new HistoryListViewHolder(itemBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ParcelProblem parcelProblem = parcelProblemList.get(position);

        if (holder.getItemViewType() == SECTION_TITLE_VIEW) {
            ((HistoryListSectionTitleViewHolder) holder).bind(parcelProblem, position);
        } else {//LIST_ITEM_VIEW
            ((HistoryListViewHolder) holder).bind(parcelProblem, position);
        }
    }

    @Override
    public int getItemCount() {
        return parcelProblemList != null ? parcelProblemList.size() : 0;
    }

    class HistoryListViewHolder extends RecyclerView.ViewHolder {
        private AdapterParcelProblemListItemBinding binding;

        public HistoryListViewHolder(AdapterParcelProblemListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ParcelProblem parcelProblem, int index) {
            binding.setParcelProblem(parcelProblem);
            binding.executePendingBindings();
            binding.setHandler(new ClickHandlers(binding));
        }
    }

    class HistoryListSectionTitleViewHolder extends RecyclerView.ViewHolder {
        private AdapterParcelProblemListSectionTitleBinding binding;

        public HistoryListSectionTitleViewHolder(AdapterParcelProblemListSectionTitleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ParcelProblem pickup, int index) {
            binding.setParcelProblem(pickup);
            binding.executePendingBindings();
        }
    }

    public class ClickHandlers {
        private AdapterParcelProblemListItemBinding binding;

        public ClickHandlers(AdapterParcelProblemListItemBinding binding) {
            this.binding = binding;
        }

        public void onOptionSelected(View view, ParcelProblem parcelProblem) {
            parcelProblemOptionClickListener.onOptionSelected(parcelProblem);
        }
    }
}

