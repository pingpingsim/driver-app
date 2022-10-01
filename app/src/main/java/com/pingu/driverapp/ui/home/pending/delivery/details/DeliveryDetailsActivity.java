package com.pingu.driverapp.ui.home.pending.delivery.details;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.ebanx.swipebtn.OnActiveListener;
import com.google.gson.Gson;
import com.pingu.driverapp.R;
import com.pingu.driverapp.data.model.Parcel;
import com.pingu.driverapp.databinding.ActivityDeliveryDetailsBinding;
import com.pingu.driverapp.ui.base.BaseActivity;
import com.pingu.driverapp.ui.home.operation.deliverysignature.DeliverySignatureActivity;
import com.pingu.driverapp.ui.home.operation.problematicparcel.ProblematicParcelActivity;
import com.pingu.driverapp.util.Constants;
import com.pingu.driverapp.util.ParcelHelper;

import javax.inject.Inject;

public class DeliveryDetailsActivity extends BaseActivity {
    private ActivityDeliveryDetailsBinding binding;
    private DeliveryDetailsViewModel deliveryDetailsViewModel;
    private Parcel parcel;
    private static final int PROBLEMATIC_PARCEL_REQUEST_CODE = 300;
    private static final int COMPLETE_PARCEL_REQUEST_CODE = 301;
    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_delivery_details);
        deliveryDetailsViewModel = ViewModelProviders.of(this, viewModelFactory).get(DeliveryDetailsViewModel.class);
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(Constants.INTENT_EXTRA_DELIVERY_ITEM)) {
            final String jsonObjStr = getIntent().getExtras().getString(Constants.INTENT_EXTRA_DELIVERY_ITEM);
            parcel = new Gson().fromJson(jsonObjStr, Parcel.class);
            binding.setPickupItem(parcel);
            binding.setHandler(new ClickHandlers(binding));
        }
        initToolbar();
        initEventListeners();
    }

    private void initToolbar() {
        getSupportActionBar().setTitle(getString(R.string.delivery_details_screen_title));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getSupportActionBar().setElevation(0);

        final Drawable upArrow = getResources().getDrawable(R.mipmap.back_arrow);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initEventListeners() {
        binding.swipeBtnSign.setOnActiveListener(new OnActiveListener() {
            @Override
            public void onActive() {
                openDeliveryAndSignatureScreen();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class ClickHandlers {
        private ActivityDeliveryDetailsBinding binding;

        public ClickHandlers(ActivityDeliveryDetailsBinding binding) {
            this.binding = binding;
        }

        public void onMapMenuSelected(View view, Parcel pickup) {
            ParcelHelper.openMap(DeliveryDetailsActivity.this, pickup.getRecipientAddress());
        }

        public void onCallMenuSelected(View view, Parcel pickup) {
            ParcelHelper.callNumber(DeliveryDetailsActivity.this, pickup.getRecipientContactNumber());
        }

        public void onWhatsappMenuSelected(View view, Parcel pickup) {
            final String message = String.format(getString(R.string.whatsAppMsgOutForDelivery), pickup.getParcelId());
            ParcelHelper.openWhatsApp(pickup.getRecipientContactNumber(), message, DeliveryDetailsActivity.this);
        }

        public void onProblematicBtnClick(View view, Parcel pickup) {
            openProblematicParcelScreen();
        }
    }

    private void openDeliveryAndSignatureScreen() {
        Intent intent = new Intent(this, DeliverySignatureActivity.class);
        intent.putExtra(Constants.INTENT_EXTRA_PARCEL_ID, parcel.getParcelId());
        intent.putExtra(Constants.INTENT_EXTRA_RECEIVER_NAME, parcel.getRecipientName());
        startActivityForResult(intent, COMPLETE_PARCEL_REQUEST_CODE);
    }

    private void openProblematicParcelScreen() {
        Intent intent = new Intent(this, ProblematicParcelActivity.class);
        intent.putExtra(Constants.INTENT_EXTRA_PARCEL_ID, parcel.getParcelId());
        startActivityForResult(intent, PROBLEMATIC_PARCEL_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == COMPLETE_PARCEL_REQUEST_CODE &&
                    data != null && data.getExtras() != null &&
                    data.getExtras().containsKey(Constants.INTENT_EXTRA_SUCCESS)) {
                Intent intent = new Intent();
                intent.putExtra("complete_parcel", true);
                setResult(RESULT_OK, intent);
                finish();
            } else if (requestCode == PROBLEMATIC_PARCEL_REQUEST_CODE &&
                    data != null && data.getExtras() != null &&
                    data.getExtras().containsKey(Constants.INTENT_EXTRA_SUCCESS)) {
                Intent intent = new Intent();
                intent.putExtra("problematic_parcel", true);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }
}
