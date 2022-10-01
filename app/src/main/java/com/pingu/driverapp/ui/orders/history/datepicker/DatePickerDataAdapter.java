package com.pingu.driverapp.ui.orders.history.datepicker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pingu.driverapp.R;

import java.util.List;

public class DatePickerDataAdapter extends RecyclerView.Adapter<DatePickerDataAdapter.DatePickerViewHolder> {
    private List<String> optionList;
    private DatePickerDialogOptionClickListener datePickerDialogOptionClickListener;

    public DatePickerDataAdapter(final List<String> optionList, final DatePickerDialogOptionClickListener listener) {
        this.datePickerDialogOptionClickListener = listener;
        this.optionList = optionList;
    }

    @NonNull
    @Override
    public DatePickerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_date_picker_item, parent, false);

        DatePickerViewHolder vh = new DatePickerViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(@NonNull DatePickerViewHolder fruitViewHolder, int index) {
        fruitViewHolder.dateTextView.setText(optionList.get(index));
    }

    @Override
    public int getItemCount() {
        return (optionList != null ? optionList.size() : 0);
    }

    public class DatePickerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView dateTextView;

        public DatePickerViewHolder(View v) {
            super(v);
            dateTextView = (TextView) v.findViewById(R.id.txt_date_option);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            String selectedValue = optionList.get(this.getAdapterPosition());
            if (selectedValue.toLowerCase().equals("all"))
                selectedValue = "";

            datePickerDialogOptionClickListener.onDateOptionSelected(selectedValue);
        }
    }

    public interface DatePickerDialogOptionClickListener {
        void onDateOptionSelected(String date);
    }
}