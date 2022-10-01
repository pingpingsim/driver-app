package com.pingu.driverapp.widget;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pingu.driverapp.BaseApplication;
import com.pingu.driverapp.R;
import com.pingu.driverapp.data.model.Parcel;
import com.pingu.driverapp.data.model.ParcelProblem;
import com.pingu.driverapp.ui.home.operation.pickup.pickupproblem.PickupProblemDataAdapter;

import java.util.ArrayList;
import java.util.List;

public class PickupProblemDialog extends Dialog implements View.OnClickListener {
    private Activity activity;
    private PickupProblemDataAdapter adapter;
    private PickupProblemOptionClickListener listener;
    private List<Parcel> parcelList;

    public PickupProblemDialog(final Activity a, final PickupProblemOptionClickListener listener, final List<Parcel> parcelList) {
        super(a);
        this.activity = a;
        this.listener = listener;
        this.parcelList = parcelList;
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
        setContentView(R.layout.dialog_pickout_problem_layout);

        final RecyclerView recyclerView = findViewById(R.id.recycler_view_pickup_problems);
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(activity, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(verticalLayoutManager);

        adapter = new PickupProblemDataAdapter(((BaseApplication) getContext().getApplicationContext())
                .getParcelProblemByType(getContext().getString(R.string.parcel_pickup_problem)));

        recyclerView.setAdapter(adapter);

        final TextView cancelTextView = findViewById(R.id.txt_option_cancel);
        final TextView submitTextView = findViewById(R.id.txt_option_submit);
        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        submitTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ParcelProblem selectedParcelProblem = adapter.getSelectedOption();
                if (selectedParcelProblem != null) {
                    listener.onOptionSelected(selectedParcelProblem, parcelList);
                    dismiss();
                } else {
                    Toast.makeText(getContext(), getContext()
                            .getString(R.string.pending_pickup_problem_pls_select_one), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
        dismiss();
    }

    public interface PickupProblemOptionClickListener {
        void onOptionSelected(ParcelProblem parcelProblem, List<Parcel> parcelList);
    }
}