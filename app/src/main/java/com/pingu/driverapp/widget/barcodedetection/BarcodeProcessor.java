/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pingu.driverapp.widget.barcodedetection;

import android.animation.ValueAnimator;
import android.graphics.RectF;
import android.util.Log;

import androidx.annotation.MainThread;

import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;
import com.pingu.driverapp.widget.camera.CameraReticleAnimator;
import com.pingu.driverapp.widget.camera.FrameProcessorBase;
import com.pingu.driverapp.widget.camera.GraphicOverlay;
import com.pingu.driverapp.widget.camera.WorkflowModel;

import java.util.List;

/**
 * A processor to run the barcode detector.
 */
public class BarcodeProcessor extends FrameProcessorBase<List<Barcode>> {
    private static final String TAG = "BarcodeProcessor";

    private final BarcodeScanner detector = BarcodeScanning.getClient();
    private final WorkflowModel workflowModel;
    private final CameraReticleAnimator cameraReticleAnimator;

    public BarcodeProcessor(GraphicOverlay graphicOverlay, WorkflowModel workflowModel) {
        this.workflowModel = workflowModel;
        this.cameraReticleAnimator = new CameraReticleAnimator(graphicOverlay);
    }

    @Override
    protected Task<List<Barcode>> detectInImage(InputImage image) {
        return detector.process(image);
    }

    @MainThread
    @Override
    protected void onSuccess(
            InputImage image,
            List<Barcode> results,
            GraphicOverlay graphicOverlay) {
        if (!workflowModel.isCameraLive()) {
            return;
        }

        Log.d(TAG, "Barcode result size: " + results.size());

        // Picks the barcode, if exists, that covers the center of graphic overlay.
        Barcode barcodeInCenter = null;
        for (Barcode barcode : results) {
            RectF box = graphicOverlay.translateRect(barcode.getBoundingBox());
            if (box.contains(graphicOverlay.getWidth() / 2f, graphicOverlay.getHeight() / 2f)) {
                barcodeInCenter = barcode;
                break;
            }
        }

        graphicOverlay.clear();
        if (barcodeInCenter == null) {
            cameraReticleAnimator.start();
            graphicOverlay.add(new BarcodeReticleGraphic(graphicOverlay, cameraReticleAnimator));
            workflowModel.setWorkflowState(WorkflowModel.WorkflowState.DETECTING);

        } else {
            cameraReticleAnimator.cancel();
            float sizeProgress =
                    PreferenceUtils.getProgressToMeetBarcodeSizeRequirement(graphicOverlay, barcodeInCenter);
            if (sizeProgress < 1) {
                // Barcode in the camera view is too small, so prompt user to move camera closer.
                graphicOverlay.add(new BarcodeConfirmingGraphic(graphicOverlay, barcodeInCenter));
                workflowModel.setWorkflowState(WorkflowModel.WorkflowState.CONFIRMING);

            } else {
                // Barcode size in the camera view is sufficient.
//                if (PreferenceUtils.shouldDelayLoadingBarcodeResult(graphicOverlay.getContext())) {
//                if (PreferenceUtils.shouldDelayLoadingBarcodeResult(graphicOverlay.getContext())) {
//                    ValueAnimator loadingAnimator = createLoadingAnimator(graphicOverlay, barcodeInCenter);
//                    loadingAnimator.start();
//                    graphicOverlay.add(new BarcodeLoadingGraphic(graphicOverlay, loadingAnimator));
//                    workflowModel.setWorkflowState(WorkflowModel.WorkflowState.SEARCHING);
//
//                } else {
                workflowModel.setWorkflowState(WorkflowModel.WorkflowState.DETECTED);
                workflowModel.detectedBarcode.setValue(barcodeInCenter);
                //   }
            }
        }
        graphicOverlay.invalidate();
    }

    private ValueAnimator createLoadingAnimator(
            GraphicOverlay graphicOverlay, Barcode barcode) {
        float endProgress = 1.1f;
        ValueAnimator loadingAnimator = ValueAnimator.ofFloat(0f, endProgress);
        loadingAnimator.setDuration(2000);
        loadingAnimator.addUpdateListener(
                animation -> {
                    if (Float.compare((float) loadingAnimator.getAnimatedValue(), endProgress) >= 0) {
                        graphicOverlay.clear();
                        workflowModel.setWorkflowState(WorkflowModel.WorkflowState.SEARCHED);
                        workflowModel.detectedBarcode.setValue(barcode);
                    } else {
                        graphicOverlay.invalidate();
                    }
                });
        return loadingAnimator;
    }

    @Override
    protected void onFailure(Exception e) {
        Log.e(TAG, "Barcode detection failed!", e);
    }

    @Override
    public void stop() {
        try {
            detector.close();
        } catch (Exception e) {
            Log.e(TAG, "Failed to close barcode detector!", e);
        }
    }
}
