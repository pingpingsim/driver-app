package com.pingu.driverapp.widget;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pingu.driverapp.BaseApplication;
import com.pingu.driverapp.R;
import com.pingu.driverapp.data.model.ParcelProblem;
import com.pingu.driverapp.ui.home.operation.problematicparcel.ParcelProblemAdapter;
import com.pingu.driverapp.ui.orders.history.HistoryListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParcelProblemDialog extends Dialog implements ParcelProblemAdapter.ParcelProblemOptionClickListener {
    private Activity activity;
    private ParcelProblemAdapter adapter;
    private ParcelProblemAdapter.ParcelProblemOptionClickListener listener;
    private EditText editTextSearch;

    public ParcelProblemDialog(Activity a, ParcelProblemAdapter.ParcelProblemOptionClickListener listener) {
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
        setContentView(R.layout.dialog_parcel_problem_layout);

        initRecyclerView();
        initParcelProblemSearchInput();
    }

    private void initRecyclerView() {
        List<ParcelProblem> parcelProblemList = new ArrayList<>();
        List<ParcelProblem> parcelDeliveryProblemList =
                ((BaseApplication) getContext().getApplicationContext())
                        .parcelProblemReference.get(getContext().getString(R.string.parcel_delivery_problem));
        List<ParcelProblem> parcelNormalProblemList =
                ((BaseApplication) getContext().getApplicationContext())
                        .parcelProblemReference.get(getContext().getString(R.string.parcel_normal_problem));
        if (parcelDeliveryProblemList != null)
            parcelProblemList.addAll(parcelDeliveryProblemList);
        if (parcelNormalProblemList != null)
            parcelProblemList.addAll(parcelNormalProblemList);

        final RecyclerView recyclerView = findViewById(R.id.recycler_view_parcel_problems);
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(activity, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(verticalLayoutManager);
        adapter = new ParcelProblemAdapter(getContext(), getSectionedDataList(parcelProblemList), this);
        editTextSearch = findViewById(R.id.edit_text_search_parcel_problem);
        recyclerView.setAdapter(adapter);
    }

    private void initParcelProblemSearchInput() {

        editTextSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (editTextSearch.getCompoundDrawables()[DRAWABLE_RIGHT] != null &&
                            event.getRawX() >= (editTextSearch.getRight() - editTextSearch.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                        final String searchInput = editTextSearch.getText().toString();
                        filter(searchInput);
                        return true;
                    }
                }
                return false;
            }
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                final String searchInput = editable.toString();
                filter(searchInput);
            }
        });
    }

    private void filter(String text) {
        if (adapter != null &&
                adapter.getOriginalParcelProblemList() != null &&
                !adapter.getOriginalParcelProblemList().isEmpty()) {
            if (TextUtils.isEmpty(text)) {
//                hideSoftKeyboard();
                adapter.filterParcelProblemList(adapter.getOriginalParcelProblemList());

            } else {
                List<ParcelProblem> filteredList = new ArrayList<ParcelProblem>();
                for (ParcelProblem parcelProblem : adapter.getOriginalParcelProblemList()) {
                    if (parcelProblem.getRowDataType() == HistoryListAdapter.LIST_ITEM_VIEW &&
                            (
                                    (parcelProblem.getReason() != null && parcelProblem.getReason().toLowerCase().contains(text.toLowerCase())) ||
                                            (parcelProblem.getCategory() != null && parcelProblem.getCategory().toLowerCase().contains(text.toLowerCase()))
                            )

                    ) {
                        filteredList.add(parcelProblem);
                    }
                }
                adapter.filterParcelProblemList(getSectionedDataList(filteredList));
            }
        }
    }

    @Override
    public void onOptionSelected(ParcelProblem parcelProblem) {
        listener.onOptionSelected(parcelProblem);
        dismiss();
    }

    private List<ParcelProblem> getSectionedDataList(List<ParcelProblem> parcelProblemList) {
        Map<String, List<ParcelProblem>> categoryVsParcelProblemMap = new HashMap<>();
        if (parcelProblemList != null && parcelProblemList.size() > 0) {
            for (final ParcelProblem parcelProblem : parcelProblemList) {
                if (categoryVsParcelProblemMap.containsKey(parcelProblem.getCategory())) {
                    List<ParcelProblem> list = categoryVsParcelProblemMap.get(parcelProblem.getCategory());
                    list.add(parcelProblem);
                    categoryVsParcelProblemMap.put(parcelProblem.getCategory(), list);

                } else {
                    List<ParcelProblem> list = new ArrayList<>();
                    list.add(parcelProblem);
                    categoryVsParcelProblemMap.put(parcelProblem.getCategory(), list);
                }
            }
        }
        // Populate the sectioned list by date
        List<ParcelProblem> newParcelProblemList = new ArrayList<>();
        for (final Map.Entry<String, List<ParcelProblem>> pickupEntry : categoryVsParcelProblemMap.entrySet()) {
            final String category = pickupEntry.getKey();
            final List<ParcelProblem> list = pickupEntry.getValue();

            //header record
            ParcelProblem headerRecord = new ParcelProblem();
            headerRecord.setCategory(category);
            headerRecord.setRowDataType(HistoryListAdapter.SECTION_TITLE_VIEW);
            newParcelProblemList.add(headerRecord);

            //child record
            if (list != null && list.size() > 0) {
                for (ParcelProblem childRecord : list) {
                    childRecord.setRowDataType(HistoryListAdapter.LIST_ITEM_VIEW);
                    newParcelProblemList.add(childRecord);
                }
            }
        }
        return newParcelProblemList;
    }
}