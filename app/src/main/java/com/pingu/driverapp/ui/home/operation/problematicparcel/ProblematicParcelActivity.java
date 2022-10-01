package com.pingu.driverapp.ui.home.operation.problematicparcel;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.pingu.driverapp.R;
import com.pingu.driverapp.data.model.ParcelProblem;
import com.pingu.driverapp.databinding.ActivityOperationProblematicParcelBinding;
import com.pingu.driverapp.ui.base.BaseActivity;
import com.pingu.driverapp.ui.home.operation.barcodescanner.ScanBarcodeActivity;
import com.pingu.driverapp.ui.home.operation.deliverysignature.photo.CameraActivity;
import com.pingu.driverapp.util.Constants;
import com.pingu.driverapp.util.DialogHelper;
import com.pingu.driverapp.util.FileHelper;
import com.pingu.driverapp.widget.ParcelProblemDialog;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.common.FileWithBitmapResult;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

public class ProblematicParcelActivity extends BaseActivity implements ParcelProblemAdapter.ParcelProblemOptionClickListener {
    private ActivityOperationProblematicParcelBinding binding;
    private ProblematicParcelViewModel problematicParcelViewModel;
    private ParcelProblem selectedParcelProblem;
    private static final int SCAN_BARCODE_REQUEST_CODE = 888;
    private static final int TAKE_PARCEL_PHOTO_REQUEST_CODE = 887;
    private static final int SELECT_GALLERY_PHOTO_REQUEST_CODE = 889;

    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"};
    private int REQUEST_CODE_PERMISSIONS = 1001;

    private String parcelIdFromDeliveryDetails;
    private File parcelPhoto = null;
    private boolean isRetryTriggered = false;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_operation_problematic_parcel);
        problematicParcelViewModel = ViewModelProviders.of(this, viewModelFactory).get(ProblematicParcelViewModel.class);

        if (getIntent().getExtras() != null &&
                getIntent().getExtras().containsKey(Constants.INTENT_EXTRA_PARCEL_ID)) {
            parcelIdFromDeliveryDetails = getIntent().getExtras().getString(Constants.INTENT_EXTRA_PARCEL_ID);
            binding.editTxtParcelId.setText(parcelIdFromDeliveryDetails);
        }

        observeProblematicParcelSubmittedResponse();
        initToolbar();
        initClickEvents();
    }

    private void initToolbar() {
        getSupportActionBar().setTitle(getString(R.string.home_problematic_parcel));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getSupportActionBar().setElevation(0);
        final Drawable upArrow = getResources().getDrawable(R.mipmap.back_arrow);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void markButtonState(final boolean completed) {
        if (completed) {
            binding.panelPhotoCompleted.setVisibility(View.VISIBLE);
            binding.panelPhotoIncomplete.setVisibility(View.GONE);

            if (parcelPhoto != null && parcelPhoto.exists()) {
                Bitmap parcelPhotoBitmap = BitmapFactory.decodeFile(parcelPhoto.getAbsolutePath());
                binding.imagePhoto.setImageBitmap(parcelPhotoBitmap);
            }
        } else {
            binding.panelPhotoIncomplete.setVisibility(View.VISIBLE);
            binding.panelPhotoCompleted.setVisibility(View.GONE);
            binding.imagePhoto.setImageBitmap(null);
            FileHelper.deleteFile(parcelPhoto);
            parcelPhoto = null;
        }
    }

    private void initClickEvents() {
        markButtonState(false);

        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String parcelId = binding.editTxtParcelId.getText().toString();
                final int selectParcelProblemId = selectedParcelProblem != null ? selectedParcelProblem.getId() : 0;

                final String validationError = validateInput(parcelId, selectParcelProblemId);
                if (TextUtils.isEmpty(validationError)) {
                    problematicParcelViewModel.submitProblematicParcel(parcelId, selectParcelProblemId, parcelPhoto);
                } else {
                    DialogHelper.showAlertDialog(DialogHelper.Type.ERROR, "",
                            validationError,
                            null, ProblematicParcelActivity.this, true);
                }
            }
        });

        binding.panelPhotoIncomplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPhotoSelectionOption();
            }
        });

        binding.editTxtParcelId.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (binding.editTxtParcelId.getRight() - binding.editTxtParcelId.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        binding.editTxtParcelId.clearFocus();
                        openScanBarcodeScreen();
                        return true;
                    }
                }
                return false;
            }
        });

        binding.editTxtProblemType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ParcelProblemDialog parcelProblemDialog =
                        new ParcelProblemDialog(ProblematicParcelActivity.this, ProblematicParcelActivity.this);
                parcelProblemDialog.show();
            }
        });

        binding.txtRemovePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                markButtonState(false);
            }
        });
    }

    private String validateInput(final String parcelID, final int problemId) {
        if (TextUtils.isEmpty(parcelID)) {
            return getString(R.string.operation_validation_parcel_id);
        } else if (problemId == 0) {
            return getString(R.string.operation_validation_parcel_problem);
        } else if (parcelPhoto == null) {
            return getString(R.string.operation_validation_parcel_photo);
        }

        return null;
    }

    private void openPhotoSelectionOption() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View view = this.getLayoutInflater().inflate(R.layout.dialog_photo_layout, null);
        builder.setView(view);

        final AlertDialog dialog = builder.create();
        dialog.show();

        view.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        view.findViewById(R.id.panel_take_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTakePhotoDialog();
                dialog.dismiss();
            }
        });

        view.findViewById(R.id.panel_choose_fr_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (allPermissionsGranted()) {
                    openChoosePhotoFromGallery();
                } else {
                    ActivityCompat.requestPermissions(ProblematicParcelActivity.this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
                }
                dialog.dismiss();
            }
        });
    }

    private void openChoosePhotoFromGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, SELECT_GALLERY_PHOTO_REQUEST_CODE);
    }

    private boolean allPermissionsGranted() {

        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                openChoosePhotoFromGallery();
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openTakePhotoDialog() {
        Intent intent = new Intent(ProblematicParcelActivity.this, CameraActivity.class);
        intent.putExtra(Constants.INTENT_EXTRA_PARCEL_ID, "");
        startActivityForResult(intent, TAKE_PARCEL_PHOTO_REQUEST_CODE);
    }

    private void openScanBarcodeScreen() {
        Intent scanBarcodeIntent = new Intent(this, ScanBarcodeActivity.class);
        startActivityForResult(scanBarcodeIntent, SCAN_BARCODE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SCAN_BARCODE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null &&
                    data.getExtras().containsKey(Constants.INTENT_EXTRA_BARCODE_PARCEL_ID)) {
                final String parcelId = data.getExtras().getString(Constants.INTENT_EXTRA_BARCODE_PARCEL_ID);
                binding.editTxtParcelId.setText(parcelId);
            }
        } else if (requestCode == TAKE_PARCEL_PHOTO_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null &&
                    data.getExtras().containsKey(Constants.INTENT_EXTRA_PARCEL_DELIVERED_PHOTO)) {
                parcelPhoto = (File) data.getSerializableExtra(Constants.INTENT_EXTRA_PARCEL_DELIVERED_PHOTO);
                markButtonState(true);

//                if (parcelPhoto != null && parcelPhoto.exists()) {
//                    Bitmap parcelPhotoBitmap = BitmapFactory.decodeFile(parcelPhoto.getAbsolutePath());
//                }
            }
        } else if (requestCode == SELECT_GALLERY_PHOTO_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                if (selectedImage != null) {
                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String picturePath = cursor.getString(columnIndex);

                        Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
                        options.compressDirectory = FileHelper.getBatchDirectoryName(ProblematicParcelActivity.this);
                        options.size = 40;
                        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
                        final String parcelID = dateFormat.format(new Date());
                        final FileWithBitmapResult result = Tiny.getInstance().source(picturePath).asFile().withOptions(options).compressWithReturnBitmapSync();
                        final File photoTargetFile = new File(FileHelper.getBatchDirectoryName(ProblematicParcelActivity.this), parcelID + "_photo.jpg");
                        File compressedParcelPhoto = null;
                        if (result != null && !TextUtils.isEmpty(result.outfile)) {
                            compressedParcelPhoto = new File(result.outfile);
                            if (compressedParcelPhoto != null) {
                                compressedParcelPhoto.renameTo(photoTargetFile);
                                parcelPhoto = photoTargetFile;
                                markButtonState(true);

                            }
                        }

                        cursor.close();
                    }
                }
            }
        }
    }

    private void resetFields() {
        binding.editTxtParcelId.setText("");
        binding.editTxtParcelId.clearFocus();
        binding.editTxtProblemType.setText("");
        binding.editTxtProblemType.clearFocus();
        markButtonState(false);
        parcelPhoto = null;
    }

    private void observeProblematicParcelSubmittedResponse() {
        problematicParcelViewModel.getData().observe(this, success -> {
                    if (success) {
                        if (!TextUtils.isEmpty(parcelIdFromDeliveryDetails)) {
                            Intent intent = new Intent();
                            intent.putExtra(Constants.INTENT_EXTRA_SUCCESS, true);
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            resetFields();
                            DialogHelper.dismissProgressDialog();
                            DialogHelper.showAlertDialog(DialogHelper.Type.SUCCESS, "",
                                    getString(R.string.operation_problematic_parcel_submitted),
                                    null, ProblematicParcelActivity.this, true);
                        }
                    }
                }
        );

        problematicParcelViewModel.getLoading().observe(this, isLoading -> {
                    if (isLoading) {
                        DialogHelper.showProgressDialog(this, getString(R.string.operation_pickup_process_request));
                    }
                }
        );

        problematicParcelViewModel.getError().observe(this, errorMsg -> {
                    if (!TextUtils.isEmpty(errorMsg)) {
                        DialogHelper.dismissProgressDialog();
                        DialogHelper.showAlertDialog(DialogHelper.Type.ERROR, "", errorMsg,
                                null, ProblematicParcelActivity.this, true);
                    }
                }
        );

        problematicParcelViewModel.getIsRetryTriggered().observe(this, isRetryTriggered -> {
            this.isRetryTriggered = isRetryTriggered;
        });
    }

    @Override
    public void onOptionSelected(ParcelProblem parcelProblem) {
        this.selectedParcelProblem = parcelProblem;
        binding.editTxtProblemType.setText(parcelProblem.getReason());
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
    public void onBackPressed() {
        cleanUpPhoto();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cleanUpPhoto();
    }

    private void cleanUpPhoto() {
        if (!isRetryTriggered) {
            FileHelper.deleteFile(parcelPhoto);
        }
    }
}
