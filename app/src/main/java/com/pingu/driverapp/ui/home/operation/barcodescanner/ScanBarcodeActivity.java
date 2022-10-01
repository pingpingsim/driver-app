package com.pingu.driverapp.ui.home.operation.barcodescanner;

import android.Manifest;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.common.internal.Objects;
import com.pingu.driverapp.R;
import com.pingu.driverapp.databinding.ActivityLiveBarcodeBinding;
import com.pingu.driverapp.ui.base.BaseActivity;
import com.pingu.driverapp.util.Constants;
import com.pingu.driverapp.util.FileHelper;
import com.pingu.driverapp.util.ParcelHelper;
import com.pingu.driverapp.widget.barcodedetection.BarcodeProcessor;
import com.pingu.driverapp.widget.camera.CameraSource;
import com.pingu.driverapp.widget.camera.WorkflowModel;

import java.io.IOException;

public class ScanBarcodeActivity extends BaseActivity {
    private ActivityLiveBarcodeBinding binding;
    private WorkflowModel workflowModel;
    private WorkflowModel.WorkflowState currentWorkflowState;
    private CameraSource cameraSource;
    private AnimatorSet promptChipAnimator;

    private String[] REQUEST_PERMISSIONS = new String[]{Manifest.permission.CAMERA};
    private int RESULT_PERMISSIONS = 0x9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_live_barcode);

        initToolbar();

        cameraSource = new CameraSource(binding.cameraPreviewGraphicOverlay);
        promptChipAnimator =
                (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.bottom_prompt_chip_enter);
        promptChipAnimator.setTarget(binding.bottomPromptChip);

        setUpWorkflowModel();
    }

    private void initToolbar() {
        getSupportActionBar().setTitle(getString(R.string.scan_parcel_id_screen_title));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getSupportActionBar().setElevation(0);
        final Drawable upArrow = getResources().getDrawable(R.mipmap.back_arrow_white);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPermissionGranted()) {
            startBarcodeDetection();
        } else {
            stopBarcodeDetection();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopBarcodeDetection();
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

    private void setUpWorkflowModel() {
        workflowModel = ViewModelProviders.of(this).get(WorkflowModel.class);
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

                        if (ParcelHelper.validateParcelIdFormat(barcodeValue)) {
                            FileHelper.playBeepSuccess(this);
                            Intent scanBarcodeResultIntent = new Intent();
                            scanBarcodeResultIntent.putExtra(Constants.INTENT_EXTRA_BARCODE_PARCEL_ID, barcodeValue);
                            setResult(Activity.RESULT_OK, scanBarcodeResultIntent);
                            finish();
                        } else {
                            FileHelper.playBeepFailure(this);
                            Toast.makeText(this,
                                    getString(R.string.validation_incorrect_parcel_id_format), Toast.LENGTH_SHORT).show();
                            startBarcodeDetection();
                        }
                    }
                });
    }

    private boolean isPermissionGranted() {
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion >= Build.VERSION_CODES.M) {
            //Android6.0(Marshmallow)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ScanBarcodeActivity.this, REQUEST_PERMISSIONS, RESULT_PERMISSIONS);
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
                startBarcodeDetection();
            } else {
                stopBarcodeDetection();
                finish();
                Toast.makeText(this, R.string.err_camera__permission_not_granted, Toast.LENGTH_SHORT).show();
            }
            return;
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
}
