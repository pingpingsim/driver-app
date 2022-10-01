package com.pingu.driverapp.widget;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pingu.driverapp.R;
import com.pingu.driverapp.ui.orders.history.datepicker.DatePickerDataAdapter;
import com.pingu.driverapp.util.DateTimeHelper;

import java.util.ArrayList;
import java.util.List;

public class HistoryDatePickerDialog extends Dialog implements View.OnClickListener {
    private Activity activity;
    private Dialog dialog;
    private RecyclerView recyclerView;
    private DatePickerDataAdapter adapter;
    private DatePickerDataAdapter.DatePickerDialogOptionClickListener listener;

    public HistoryDatePickerDialog(Activity a, DatePickerDataAdapter.DatePickerDialogOptionClickListener listener) {
        super(a);
        this.activity = a;
        this.listener = listener;
        setupLayout();
    }

    private void setupLayout() {
        setCanceledOnTouchOutside(true);
        setCancelable(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_history_date_picker_layout);

        recyclerView = findViewById(R.id.recycler_view_date_options);
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(activity, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(verticalLayoutManager);

        List<String> list = new ArrayList<>();
        list.add("All");
        list.addAll(DateTimeHelper.getPast3DaysDate());
        adapter = new DatePickerDataAdapter(list, listener);

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
        dismiss();
    }
}