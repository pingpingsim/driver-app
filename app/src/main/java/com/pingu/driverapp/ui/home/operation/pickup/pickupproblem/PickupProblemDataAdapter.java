package com.pingu.driverapp.ui.home.operation.pickup.pickupproblem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pingu.driverapp.data.model.ParcelProblem;
import com.pingu.driverapp.databinding.AdapterPickupProblemItemBinding;
import com.pingu.driverapp.ui.home.task.available_tasks.PickupAvailableTaskAdapter;

import java.util.List;

public class PickupProblemDataAdapter extends RecyclerView.Adapter<PickupProblemDataAdapter.PickupProblemViewHolder> {
    private List<ParcelProblem> optionList;
    private RadioButton lastRadioBtn;
    private ParcelProblem selectedParcelProblem;

    public PickupProblemDataAdapter(final List<ParcelProblem> optionList) {
        this.optionList = optionList;
    }

    public ParcelProblem getSelectedOption() {
//        ParcelProblem selectedParcelProblem = null;
//        if (optionList != null && optionList.size() > 0) {
//            for (final ParcelProblem parcelProblem : optionList) {
//                if (parcelProblem.isChecked()) {
//                    selectedParcelProblem = parcelProblem;
//                    break;
//                }
//            }
//        }
        return selectedParcelProblem;
    }

    @NonNull
    @Override
    public PickupProblemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {


        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        AdapterPickupProblemItemBinding itemBinding = AdapterPickupProblemItemBinding.inflate(layoutInflater, parent, false);
        return new PickupProblemViewHolder(itemBinding);

    }

    @Override
    public void onBindViewHolder(@NonNull PickupProblemViewHolder holder, int position) {
        ParcelProblem parcelProblem = optionList.get(position);
        holder.bind(parcelProblem, position);
    }

    @Override
    public int getItemCount() {
        return (optionList != null ? optionList.size() : 0);
    }

    public class PickupProblemViewHolder extends RecyclerView.ViewHolder {
        private AdapterPickupProblemItemBinding binding;

        public PickupProblemViewHolder(AdapterPickupProblemItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final ParcelProblem parcelProblem, final int index) {
            binding.setIndex(index);
            binding.setParcelProblem(parcelProblem);
            binding.executePendingBindings();
            binding.setHandler(new PickupProblemDataAdapter.ClickHandlers(binding));
            binding.mainPanel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new PickupProblemDataAdapter.ClickHandlers(binding).onItemSelected(binding.radioBtn, binding.getParcelProblem(), index);
                }
            });
        }
    }

    public class ClickHandlers {
        private AdapterPickupProblemItemBinding binding;

        public ClickHandlers(AdapterPickupProblemItemBinding binding) {
            this.binding = binding;
        }

        public void onItemSelected(final View view, final ParcelProblem parcelProblem, final int index) {
            final RadioButton currentRadioBtn = (RadioButton) (view);
            boolean isSame = false;

            if (lastRadioBtn != null) {
                if (lastRadioBtn == currentRadioBtn) {
                    isSame = true;
                    if (currentRadioBtn.isChecked()) {
                        currentRadioBtn.setChecked(false);
                        selectedParcelProblem = null;
                        lastRadioBtn = null;
                    } else {
                        currentRadioBtn.setChecked(true);
                        selectedParcelProblem = parcelProblem;
                    }
                } else {
                    lastRadioBtn.setChecked(false);
                    selectedParcelProblem = null;
                }
            }

            if (!isSame) {
                currentRadioBtn.setChecked(true);
                selectedParcelProblem = parcelProblem;
                lastRadioBtn = currentRadioBtn;
            }
        }
    }
}