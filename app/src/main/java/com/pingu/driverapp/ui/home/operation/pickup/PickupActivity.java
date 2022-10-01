package com.pingu.driverapp.ui.home.operation.pickup;

import android.Manifest;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.internal.Objects;
import com.pingu.driverapp.R;
import com.pingu.driverapp.data.model.Parcel;
import com.pingu.driverapp.databinding.ActivityOperationPickupBinding;
import com.pingu.driverapp.ui.base.BaseActivity;
import com.pingu.driverapp.util.Constants;
import com.pingu.driverapp.util.DateTimeHelper;
import com.pingu.driverapp.util.DialogHelper;
import com.pingu.driverapp.util.FileHelper;
import com.pingu.driverapp.util.ParcelHelper;
import com.pingu.driverapp.widget.barcodedetection.BarcodeProcessor;
import com.pingu.driverapp.widget.camera.CameraSource;
import com.pingu.driverapp.widget.camera.WorkflowModel;

import java.io.IOException;

import javax.inject.Inject;

public class PickupActivity extends BaseActivity implements PickupAdapter.ParcelOperationListener {
    private ActivityOperationPickupBinding binding;
    private PickupViewModel pickupViewModel;

    private String[] REQUEST_PERMISSIONS = new String[]{Manifest.permission.CAMERA};
    private int RESULT_PERMISSIONS = 0x9000;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private PickupAdapter parcelAdapter;

    private int type;

    private WorkflowModel workflowModel;
    private WorkflowModel.WorkflowState currentWorkflowState;
    private CameraSource cameraSource;
    private AnimatorSet promptChipAnimator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_operation_pickup);
        pickupViewModel = ViewModelProviders.of(this, viewModelFactory).get(PickupViewModel.class);

        observeTaskPickupResponse();

        initToolbar();
        initAddParcelEditText();
        initRecyclerView();
        initBarcodeScanner();

        initClickEvents();
    }

    private void initBarcodeScanner() {
        cameraSource = new CameraSource(binding.cameraPreviewGraphicOverlay);
        promptChipAnimator =
                (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.bottom_prompt_chip_enter);
        promptChipAnimator.setTarget(binding.bottomPromptChip);
        setUpWorkflowModel();
        controlScanBarcodeView(true);
    }

    private void initRecyclerView() {
        binding.txtTotalParcels.setText(String.format(getString(R.string.pending_pickup_column_parcels), 0));
        parcelAdapter = new PickupAdapter(this, null, this);
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        binding.recyclerViewOperationPickupList.setLayoutManager(verticalLayoutManager);
        binding.recyclerViewOperationPickupList.setAdapter(parcelAdapter);
    }

    private void initAddParcelEditText() {
        binding.editTxtBarcodeInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                controlAddParcelBtn();
            }
        });
    }

    private void controlAddParcelBtn() {
        final String parcelId = binding.editTxtBarcodeInput.getText().toString();
        if (!TextUtils.isEmpty(parcelId)) {
            binding.btnAddParcel.setEnabled(true);
            binding.btnAddParcel.setTextColor(getResources().getColor(R.color.white));
            binding.btnAddParcel.setBackground(getResources().getDrawable(R.drawable.enabled_btn_blue_bg));
        } else {
            binding.btnAddParcel.setEnabled(false);
            binding.btnAddParcel.setTextColor(getResources().getColor(R.color.black));
            binding.btnAddParcel.setBackground(getResources().getDrawable(R.drawable.disabled_btn_bg));
        }
    }

    private void controlScanBarcodeView(final boolean enable) {
        if (enable) {
            if (isPermissionGranted()) {
                binding.panelPreviewScanOn.setVisibility(View.VISIBLE);
                binding.panelPreviewScanOff.setVisibility(View.GONE);
                startBarcodeDetection();
            } else {
                binding.panelPreviewScanOff.setVisibility(View.VISIBLE);
                binding.panelPreviewScanOn.setVisibility(View.GONE);
                stopBarcodeDetection();
            }
        } else {
            stopBarcodeDetection();
            binding.panelPreviewScanOn.setVisibility(View.GONE);
            binding.panelPreviewScanOff.setVisibility(View.VISIBLE);
        }
    }

    public void onParcelCanceled() {
        binding.txtTotalParcels.setText(String.format(getString(R.string.pending_pickup_column_parcels), parcelAdapter.getItemCount()));
    }

    private void addParcelItem(final String parcelId, final boolean isFromScan) {
        if (ParcelHelper.validateParcelIdFormat(parcelId)) {
            Parcel pickup = new Parcel();
            pickup.setParcelId(parcelId);
            pickup.setStatusDate(DateTimeHelper.getCurrentDateTime());
            final boolean addParcelSuccess = parcelAdapter.addParcel(pickup);
            if (addParcelSuccess) {
                if (isFromScan)
                    FileHelper.playBeepSuccess(this);
                binding.txtTotalParcels.setText(
                        String.format(getString(R.string.pending_pickup_column_parcels), parcelAdapter.getItemCount()));
            } else {
                FileHelper.playBeepFailure(this);
                Toast.makeText(this,
                        String.format(getString(R.string.operation_pickup_duplicate_parcel_id), parcelId), Toast.LENGTH_SHORT).show();
            }
        } else {
            FileHelper.playBeepFailure(this);
            Toast.makeText(this,
                    getString(R.string.validation_incorrect_parcel_id_format), Toast.LENGTH_SHORT).show();
        }
    }

    private void initClickEvents() {
        binding.btnAddParcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String parcelId = binding.editTxtBarcodeInput.getText().toString().trim();
                if (!TextUtils.isEmpty(parcelId)) {
                    addParcelItem(parcelId, false);

                    hideSoftKeyboard();
                    binding.editTxtBarcodeInput.setText("");
                    binding.editTxtBarcodeInput.clearFocus();
                }
            }
        });
        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (parcelAdapter != null && parcelAdapter.getItemCount() > 0) {
                    pickupViewModel.pickupOrDeliverParcels(parcelAdapter.getPickupList(), type);

                } else {
                    DialogHelper.showAlertDialog(DialogHelper.Type.ERROR, "",
                            getString(R.string.operation_validation_parcel_at_least_one), null,
                            PickupActivity.this, true);
                }
            }
        });
        binding.panelPreviewScanOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlScanBarcodeView(false);
            }
        });
        binding.panelPreviewScanOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlScanBarcodeView(true);
            }
        });
    }

    private void initToolbar() {
        if (getIntent().getExtras() != null &&
                getIntent().getExtras().containsKey(Constants.INTENT_EXTRA_TASK_ACTION_TYPE)) {
            final String title = getIntent().getExtras().getString(Constants.INTENT_EXTRA_TASK_ACTION_TYPE);
            getSupportActionBar().setTitle(title);
            if (title.startsWith(getString(R.string.home_pickup))) {
                type = 1;
            } else if (title.startsWith(getString(R.string.home_delivery))) {
                type = 2;
            }
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void observeTaskPickupResponse() {
        pickupViewModel.getData().observe(this, success -> {
                    if (success) {
                        DialogHelper.dismissProgressDialog();
                        DialogHelper.showAlertDialog(DialogHelper.Type.SUCCESS, "",
                                (type == 1) ? getString(R.string.operation_pickup_success) : getString(R.string.operation_out_for_delivery),
                                null, PickupActivity.this, true);

                        parcelAdapter.setPickupList(null);
                        binding.txtTotalParcels.setText(String.format(getString(R.string.pending_pickup_column_parcels), 0));
                    }
                }
        );

        pickupViewModel.getLoading().observe(this, isLoading -> {
                    if (isLoading) {
                        DialogHelper.showProgressDialog(this,
                                getString(R.string.operation_pickup_process_request));
                    }
                }
        );

        pickupViewModel.getError().observe(this, errorMsg -> {
                    if (!TextUtils.isEmpty(errorMsg)) {
                        DialogHelper.dismissProgressDialog();
                        DialogHelper.showAlertDialog(DialogHelper.Type.ERROR, "", errorMsg,
                                null, PickupActivity.this, true);
                    }
                }
        );
    }

    private void startCameraPreview() {
        if (!workflowModel.isCameraLive() && cameraSource != null) {
            try {
                workflowModel.markCameraLive();
                binding.cameraPreview.start(cameraSource);
            } catch (IOException e) {
                //Log.e(TAG, "Failed to start camera preview!", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }

    private void stopCameraPreview() {
        if (workflowModel.isCameraLive()) {
            workflowModel.markCameraFrozen();
            binding.cameraPreview.stop();
        }
    }

    private void setUpWorkflowModel() {
        workflowModel = ViewModelProviders.of(this).get(WorkflowModel.class);

        // Observes the workflow state changes, if happens, update the overlay view indicators and
        // camera preview state.
        workflowModel.workflowState.observe(
                this,
                workflowState -> {
                    if (workflowState == null || Objects.equal(currentWorkflowState, workflowState)) {
                        return;
                    }

                    currentWorkflowState = workflowState;
                    //Log.d(TAG, "Current workflow state: " + currentWorkflowState.name());

                    boolean wasPromptChipGone = (binding.bottomPromptChip.getVisibility() == View.GONE);

                    switch (workflowState) {
                        case DETECTING:
                            binding.bottomPromptChip.setVisibility(View.VISIBLE);
                            binding.bottomPromptChip.setText(R.string.prompt_point_at_a_barcode);
                            startCameraPreview();
                            break;
                        case CONFIRMING:
                            binding.bottomPromptChip.setVisibility(View.VISIBLE);
                            binding.bottomPromptChip.setText(R.string.prompt_move_camera_closer);
                            startCameraPreview();
                            break;
                        case SEARCHING:
                            binding.bottomPromptChip.setVisibility(View.VISIBLE);
                            binding.bottomPromptChip.setText(R.string.prompt_searching);
                            stopCameraPreview();
                            break;
                        case DETECTED:
                        case SEARCHED:
                            binding.bottomPromptChip.setVisibility(View.GONE);
                            stopCameraPreview();
                            break;
                        default:
                            binding.bottomPromptChip.setVisibility(View.GONE);
                            break;
                    }

                    boolean shouldPlayPromptChipEnteringAnimation =
                            wasPromptChipGone && (binding.bottomPromptChip.getVisibility() == View.VISIBLE);
                    if (shouldPlayPromptChipEnteringAnimation && !promptChipAnimator.isRunning()) {
                        promptChipAnimator.start();
                    }
                });

        workflowModel.detectedBarcode.observe(
                this,
                barcode -> {
                    if (barcode != null) {
                        final String barcodeValue = barcode.getRawValue().trim();
                        addParcelItem(barcodeValue, true);
                        controlScanBarcodeView(true);//stop or start barcode detection again
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        controlScanBarcodeView(false);
    }

    private void startBarcodeDetection() {
        workflowModel.markCameraFrozen();
        currentWorkflowState = WorkflowModel.WorkflowState.NOT_STARTED;
        cameraSource.setFrameProcessor(new BarcodeProcessor(binding.cameraPreviewGraphicOverlay, workflowModel));
        workflowModel.setWorkflowState(WorkflowModel.WorkflowState.DETECTING);
    }

    private void stopBarcodeDetection() {
        currentWorkflowState = WorkflowModel.WorkflowState.NOT_STARTED;
        stopCameraPreview();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
            cameraSource = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private boolean isPermissionGranted() {
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion >= Build.VERSION_CODES.M) {
            //Android6.0(Marshmallow)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(PickupActivity.this, REQUEST_PERMISSIONS, RESULT_PERMISSIONS);
                return false;
            } else {
                return true;
            }
        } else {
            //Android6.0(Marshmallow)
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (RESULT_PERMISSIONS == requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                controlScanBarcodeView(true);
            } else {
                controlScanBarcodeView(false);
                Toast.makeText(this, R.string.err_camera__permission_not_granted, Toast.LENGTH_SHORT).show();
            }
            return;
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }
}
