package com.pingu.driverapp.ui.search.tracking;

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
import com.pingu.driverapp.databinding.ActivityParcelTrackingBinding;
import com.pingu.driverapp.ui.base.BaseActivity;
import com.pingu.driverapp.util.DateTimeHelper;
import com.pingu.driverapp.util.DialogHelper;
import com.pingu.driverapp.util.FileHelper;
import com.pingu.driverapp.util.ParcelHelper;
import com.pingu.driverapp.widget.barcodedetection.BarcodeProcessor;
import com.pingu.driverapp.widget.camera.CameraSource;
import com.pingu.driverapp.widget.camera.WorkflowModel;

import java.io.IOException;

import javax.inject.Inject;

public class TrackParcelActivity extends BaseActivity {
    private ActivityParcelTrackingBinding binding;
    private TrackParcelViewModel parcelTrackingViewModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private String[] REQUEST_PERMISSIONS = new String[]{Manifest.permission.CAMERA};
    private int RESULT_PERMISSIONS = 0x9000;

    private WorkflowModel workflowModel;
    private WorkflowModel.WorkflowState currentWorkflowState;
    private CameraSource cameraSource;
    private AnimatorSet promptChipAnimator;
    private String parcelNumber;
    private TrackTimeLineAdapter trackTimeLineAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_parcel_tracking);
        parcelTrackingViewModel = ViewModelProviders.of(this, viewModelFactory).get(TrackParcelViewModel.class);

        observeTrackParcelResponse();
        initToolbar();
        initRecyclerView();
        initSearchParcelEditText();
        initEventListeners();
        initBarcodeScanner();
    }

    private void initRecyclerView() {
        trackTimeLineAdapter = new TrackTimeLineAdapter(this, null);
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        binding.recyclerViewParcelTrackTimeLine.setLayoutManager(verticalLayoutManager);
        binding.recyclerViewParcelTrackTimeLine.setAdapter(trackTimeLineAdapter);
    }

    private void initBarcodeScanner() {
        cameraSource = new CameraSource(binding.cameraPreviewGraphicOverlay);
        promptChipAnimator =
                (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.bottom_prompt_chip_enter);
        promptChipAnimator.setTarget(binding.bottomPromptChip);
        setUpWorkflowModel();
        controlScanBarcodeView(true);
    }

    private void observeTrackParcelResponse() {
        parcelTrackingViewModel.getData().observe(this, parcelStatus -> {
                    binding.txtRefNo.setText(parcelNumber);
                    binding.txtCreatedDateTime.setText(DateTimeHelper.formatDateTime(parcelStatus.getCreatedDate()));
                    trackTimeLineAdapter.setStatusList(parcelStatus.getStatusList());
                    binding.panelParcelTrackingInfo.setVisibility(View.VISIBLE);
                }
        );

        parcelTrackingViewModel.getLoading().observe(this, isLoading -> {
                    if (isLoading) {

                    }
                }
        );

        parcelTrackingViewModel.getError().observe(this, errorMsg -> {
                    if (!TextUtils.isEmpty(errorMsg)) {
                        DialogHelper.showAlertDialog(DialogHelper.Type.ERROR, "", errorMsg,
                                null, TrackParcelActivity.this, true);
                    }
                }
        );
    }

    private void initSearchParcelEditText() {
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
                controlSearchParcelBtn();
            }
        });
    }

    private void controlSearchParcelBtn() {
        final String parcelId = binding.editTxtBarcodeInput.getText().toString();
        if (!TextUtils.isEmpty(parcelId)) {
            binding.btnSearchParcel.setEnabled(true);
            binding.btnSearchParcel.setTextColor(getResources().getColor(R.color.white));
            binding.btnSearchParcel.setBackground(getResources().getDrawable(R.drawable.enabled_btn_blue_bg));
        } else {
            binding.btnSearchParcel.setEnabled(false);
            binding.btnSearchParcel.setTextColor(getResources().getColor(R.color.black));
            binding.btnSearchParcel.setBackground(getResources().getDrawable(R.drawable.disabled_btn_bg));
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

    private boolean isPermissionGranted() {
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion >= Build.VERSION_CODES.M) {
            //Android6.0(Marshmallow)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(TrackParcelActivity.this, REQUEST_PERMISSIONS, RESULT_PERMISSIONS);
                return false;
            } else {
                return true;
            }
        } else {
            //Android6.0(Marshmallow)
            return true;
        }
    }

    private void initToolbar() {
        getSupportActionBar().setTitle(getString(R.string.parcel_tracking_screen_title));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getSupportActionBar().setElevation(0);

        final Drawable upArrow = getResources().getDrawable(R.mipmap.back_arrow);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initEventListeners() {
        binding.btnSearchParcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String parcelId = binding.editTxtBarcodeInput.getText().toString().trim();
                if (!TextUtils.isEmpty(parcelId)) {
                    hideSoftKeyboard();
                    binding.editTxtBarcodeInput.setText("");
                    binding.editTxtBarcodeInput.clearFocus();

                    parcelNumber = parcelId;
                    trackParcel(parcelId, false);
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

    private void trackParcel(final String parcelId, final boolean requireBeep) {
        if (ParcelHelper.validateParcelIdFormat(parcelId)) {
            if (requireBeep) {
                FileHelper.playBeepSuccess(this);
            }
            parcelTrackingViewModel.trackParcel(parcelId);
        } else {
            if (requireBeep) {
                FileHelper.playBeepFailure(this);
            }
            Toast.makeText(this,
                    getString(R.string.validation_incorrect_parcel_id_format), Toast.LENGTH_SHORT).show();
        }
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
                        parcelNumber = barcodeValue;
                        trackParcel(barcodeValue, true);
                        controlScanBarcodeView(true);
                    }
                });
    }

    private void startCameraPreview() {
        if (!workflowModel.isCameraLive() && cameraSource != null) {
            try {
                workflowModel.markCameraLive();
                binding.cameraPreview.start(cameraSource);
            } catch (IOException e) {
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

    @Override
    protected void onPause() {
        super.onPause();
        controlScanBarcodeView(false);
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
}
