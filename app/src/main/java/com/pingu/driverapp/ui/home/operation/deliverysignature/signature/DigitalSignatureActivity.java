package com.pingu.driverapp.ui.home.operation.deliverysignature.signature;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.pingu.driverapp.R;
import com.pingu.driverapp.databinding.ActivityDigitalSignatureBinding;
import com.pingu.driverapp.ui.base.BaseActivity;
import com.pingu.driverapp.util.Constants;
import com.pingu.driverapp.util.DialogHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * https://github.com/gcacace/android-signaturepad
 */
public class DigitalSignatureActivity extends BaseActivity {
    private ActivityDigitalSignatureBinding binding;
    private boolean isSigned;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_digital_signature);

        initToolbar();
        initEventListener();
    }

    private void initEventListener() {
        binding.signaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {

            @Override
            public void onStartSigning() {
                //Event triggered when the pad is touched
            }

            @Override
            public void onSigned() {
                isSigned = true;
            }

            @Override
            public void onClear() {
                isSigned = false;
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (isSigned) {
                        Bitmap signatureBitmap = binding.signaturePad.getSignatureBitmap();

                        final String path = getExternalFilesDir(null).toString() + "/pingudriverapp/";
                        OutputStream fOutputStream = null;
                        File directory = new File(path);
                        if (!directory.exists()) {
                            directory.mkdirs();
                        }

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
                        File signatureFile = new File(path + dateFormat.format(new Date()) + "_sign.jpg");
                        fOutputStream = new FileOutputStream(signatureFile);
                        signatureBitmap.compress(Bitmap.CompressFormat.JPEG, 50, fOutputStream);
                        fOutputStream.flush();
                        fOutputStream.close();

                        Intent returnIntent = new Intent();
                        returnIntent.putExtra(Constants.INTENT_EXTRA_PARCEL_DELIVERED_SIGNATURE, signatureFile);
                        setResult(Activity.RESULT_OK, returnIntent);
                        onBackPressed();
                    } else {
                        DialogHelper.showAlertDialog(DialogHelper.Type.ERROR, "",
                                getString(R.string.operation_digital_signature_validation_failure), null
                                , DigitalSignatureActivity.this, true);
                    }
                } catch (Exception ex) {

                }
            }
        });
    }

    private void initToolbar() {
        getSupportActionBar().setTitle(getString(R.string.operation_digital_signature_screen_title));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getSupportActionBar().setElevation(0);
        final Drawable upArrow = getResources().getDrawable(R.mipmap.back_arrow);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.digital_signature_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_reset_signature:
                binding.signaturePad.clear();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
