package com.pingu.driverapp.ui.orders.history;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pingu.driverapp.R;
import com.pingu.driverapp.data.model.Parcel;
import com.pingu.driverapp.databinding.ActivityHistoryListBinding;
import com.pingu.driverapp.ui.base.BaseActivity;
import com.pingu.driverapp.ui.orders.history.datepicker.DatePickerDataAdapter;
import com.pingu.driverapp.util.Constants;
import com.pingu.driverapp.widget.HistoryDatePickerDialog;
import com.tylersuehr.esr.EmptyStateRecyclerView;
import com.tylersuehr.esr.TextStateDisplay;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;

public class HistoryListActivity extends BaseActivity implements DatePickerDataAdapter.DatePickerDialogOptionClickListener {
    private ActivityHistoryListBinding binding;
    private HistoryListViewModel historyListViewModel;
    private HistoryListAdapter historyListAdapter;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private HistoryDatePickerDialog datePickerDialog;
    private int totalAddress;
    private int totalParcel;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_history_list);
        historyListViewModel = ViewModelProviders.of(this, viewModelFactory).get(HistoryListViewModel.class);
        initToolbar();
        initPendingPickupSearchInput();

        initRecyclerView();
        observeTaskListResponse();

        historyListViewModel.loadHistoryTask(type, "");
    }

    private void initPendingPickupSearchInput() {
        controlClearSearchInputIconVisibility(false);

        binding.editTextSearchHistoryList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (binding.editTextSearchHistoryList.getCompoundDrawables()[DRAWABLE_RIGHT] != null &&
                            event.getRawX() >= (binding.editTextSearchHistoryList.getRight() - binding.editTextSearchHistoryList.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        binding.editTextSearchHistoryList.setText("");
                        binding.editTextSearchHistoryList.clearFocus();
                        filter("");
                        return true;
                    }
                }
                return false;
            }
        });

        binding.editTextSearchHistoryList.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                final String searchInput = editable.toString();
                controlClearSearchInputIconVisibility(TextUtils.isEmpty(searchInput) ? false : true);
                filter(searchInput);
            }
        });
    }

    private void controlClearSearchInputIconVisibility(final boolean visible) {
        final Drawable leftDrawable = getResources().getDrawable(R.mipmap.icon_search);

        if (visible) {
            final Drawable rightDrawable = getResources().getDrawable(R.mipmap.icon_search_cancel);
            binding.editTextSearchHistoryList.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, rightDrawable, null);
        } else {
            binding.editTextSearchHistoryList.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);
        }
    }

    private void filter(String text) {
        if (historyListAdapter != null &&
                historyListAdapter.getOriginalPickupList() != null &&
                !historyListAdapter.getOriginalPickupList().isEmpty()) {
            if (TextUtils.isEmpty(text)) {
                hideSoftKeyboard();
                historyListAdapter.filterPickList(historyListAdapter.getOriginalPickupList());
                binding.txtTotalParcels.setText(String.format(getString(R.string.pending_pickup_column_parcels), totalParcel));

            } else {
                List<Parcel> filteredList = new ArrayList<Parcel>();
                int totalParcelCount = 0;
                for (Parcel pickup : historyListAdapter.getOriginalPickupList()) {
                    if (pickup.getRowDataType() == HistoryListAdapter.LIST_ITEM_VIEW &&
                            (
                                    (pickup.getSenderAddress() != null && pickup.getSenderAddress().toLowerCase().contains(text.toLowerCase())) ||
                                            (pickup.getSenderName() != null && pickup.getSenderName().toLowerCase().contains(text.toLowerCase())) ||
                                            (pickup.getParcelId() != null && pickup.getParcelId().toLowerCase().contains(text.toLowerCase()))
                            )

                    ) {
                        totalParcelCount += pickup.getTotalParcels();
                        filteredList.add(pickup);
                    }
                }
                historyListAdapter.filterPickList(getSectionedDataList(filteredList));
                binding.txtTotalParcels.setText(String.format(getString(R.string.pending_pickup_column_parcels), totalParcelCount));
            }
        }
    }

    private List<Parcel> getSectionedDataList(List<Parcel> pickupList) {
        // Group the list by date

        Comparator<String> dateComparator = new Comparator<String>() {

            @Override
            public int compare(String date1Str, String date2Str) {
                try {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                    Date date1 = dateFormat.parse(date1Str);
                    Date date2 = dateFormat.parse(date2Str);

                    return date2.compareTo(date1);
                } catch (Exception ex) {
                    return 0;
                }
            }
        };

        Map<String, List<Parcel>> dateVsPickupMap = new TreeMap<>(dateComparator);
        if (pickupList != null && pickupList.size() > 0) {
            for (Parcel pickupItem : pickupList) {
                if (dateVsPickupMap.containsKey(pickupItem.getParcelStatusDate())) {
                    List<Parcel> list = dateVsPickupMap.get(pickupItem.getParcelStatusDate());
                    list.add(pickupItem);
                    dateVsPickupMap.put(pickupItem.getParcelStatusDate(), list);

                } else {
                    List<Parcel> list = new ArrayList<>();
                    list.add(pickupItem);
                    dateVsPickupMap.put(pickupItem.getParcelStatusDate(), list);
                }
            }
        }
        // Populate the sectioned list by date
        List<Parcel> newPickupList = new ArrayList<>();
        int index = 1;
        for (Map.Entry<String, List<Parcel>> pickupEntry : dateVsPickupMap.entrySet()) {
            final String date = pickupEntry.getKey();
            final List<Parcel> list = pickupEntry.getValue();

            //header record
            Parcel headerRecord = new Parcel();
            headerRecord.setStatusDate(date);
            headerRecord.setRowDataType(HistoryListAdapter.SECTION_TITLE_VIEW);
            newPickupList.add(headerRecord);

            //child record
            if (list != null && list.size() > 0) {
                for (Parcel childRecord : list) {
                    childRecord.setRowDataType(HistoryListAdapter.LIST_ITEM_VIEW);
                    childRecord.setIndex(index);
                    newPickupList.add(childRecord);
                    index += 1;
                }
            }
        }
        return newPickupList;
    }

    private void initRecyclerView() {
        historyListAdapter = new HistoryListAdapter(this, null, type);
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        binding.recyclerViewHistoryList.setLayoutManager(verticalLayoutManager);
        binding.recyclerViewHistoryList.setAdapter(historyListAdapter);
        binding.recyclerViewHistoryList.setStateDisplay(EmptyStateRecyclerView.STATE_EMPTY,
                new TextStateDisplay(this, getString(R.string.msg_list_empty), ""));
    }

    private void observeTaskListResponse() {
        historyListViewModel.getData().observe(this, parcelList -> {
                    if (parcelList != null && parcelList.size() > 0) {
                        historyListAdapter.setOriginalPickupList(getSectionedDataList(parcelList));
                        binding.txtTotalParcels.setText(String.format(getString(R.string.pending_pickup_column_parcels), parcelList.size()));
                        binding.recyclerViewHistoryList.invokeState(EmptyStateRecyclerView.STATE_OK);
                    } else {
                        historyListAdapter.setOriginalPickupList(null);
                        binding.txtTotalParcels.setText(String.format(getString(R.string.pending_pickup_column_parcels), 0));
                        binding.recyclerViewHistoryList.invokeState(EmptyStateRecyclerView.STATE_EMPTY);
                    }
                }
        );

        historyListViewModel.getLoading().observe(this, isLoading -> {
                    if (isLoading) {
                        historyListAdapter.setOriginalPickupList(null);
                        binding.recyclerViewHistoryList.invokeState(EmptyStateRecyclerView.STATE_LOADING);
                    }
                }
        );

        historyListViewModel.getError().observe(this, errorMsg -> {
                    if (!TextUtils.isEmpty(errorMsg)) {
                        binding.recyclerViewHistoryList.setStateDisplay(EmptyStateRecyclerView.STATE_ERROR,
                                new TextStateDisplay(this, errorMsg, ""));
                        binding.recyclerViewHistoryList.invokeState(EmptyStateRecyclerView.STATE_ERROR);
                    }
                }
        );
    }

    private void initToolbar() {

        if (getIntent().getExtras() != null &&
                getIntent().getExtras().containsKey(Constants.INTENT_EXTRA_TASK_HISTORY_TYPE)) {
            final String title = getIntent().getExtras().getString(Constants.INTENT_EXTRA_TASK_HISTORY_TYPE);
            getSupportActionBar().setTitle(title);

            if (title.toLowerCase().equals(String.format(getString(R.string.orders_history_list), getString(R.string.orders_picked_up)).toLowerCase())) {
                type = 1;
            } else if (title.toLowerCase().equals(String.format(getString(R.string.orders_history_list), getString(R.string.orders_delivery)).toLowerCase())) {
                type = 2;
            } else if (title.toLowerCase().equals(String.format(getString(R.string.orders_history_list), getString(R.string.orders_delivery_and_signature)).toLowerCase())) {
                type = 3;
            } else if (title.toLowerCase().equals(String.format(getString(R.string.orders_history_list), getString(R.string.orders_problematic_parcel)).toLowerCase())) {
                type = 4;
            }
            binding.setScreenType(type);
        }
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getSupportActionBar().setElevation(0);

        final Drawable upArrow = getResources().getDrawable(R.mipmap.back_arrow);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_date_filter:
                openDatePickerDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.history_menu, menu);
        return true;
    }

    private void openDatePickerDialog() {
        datePickerDialog = new HistoryDatePickerDialog(this, this);
        datePickerDialog.show();
    }

    public void onDateOptionSelected(String date) {
        historyListViewModel.loadHistoryTask(type, date);
        if (datePickerDialog != null) {
            datePickerDialog.dismiss();
        }
    }
}
