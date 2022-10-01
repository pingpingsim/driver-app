package com.pingu.driverapp.ui.profile.changepassword;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.pingu.driverapp.R;
import com.pingu.driverapp.databinding.ActivityChangePasswordBinding;
import com.pingu.driverapp.util.DialogHelper;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public class ChangePasswordActivity extends DaggerAppCompatActivity {
    private ActivityChangePasswordBinding binding;
    private ChangePasswordViewModel changePasswordViewModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password);
        changePasswordViewModel = ViewModelProviders.of(this, viewModelFactory).get(ChangePasswordViewModel.class);

        initToolbar();
        initUI();
        observeChangePasswordReponse();
        initClickEvents();
    }

    private void initUI() {
        binding.txtInputCurrentPassword.addTextChangedListener(inputTextWatcher);
        binding.txtInputNewPassword.addTextChangedListener(inputTextWatcher);
        binding.txtInputRetypeNewPassword.addTextChangedListener(inputTextWatcher);

        checkFieldsForEmptyValues();
    }

    private void checkFieldsForEmptyValues() {
        final String currentPassword = binding.txtInputCurrentPassword.getText().toString();
        final String newPassword = binding.txtInputNewPassword.getText().toString();
        final String retypeNewPassword = binding.txtInputRetypeNewPassword.getText().toString();

        if (TextUtils.isEmpty(currentPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(retypeNewPassword)) {
            controlUpdatePasswordBtn(false);
        } else {
            controlUpdatePasswordBtn(true);
        }
    }

    private void controlUpdatePasswordBtn(final boolean isEnabled) {
        binding.btnUpdatePassword.setEnabled(isEnabled);
        binding.btnUpdatePassword.setBackground(isEnabled ? getResources().getDrawable(R.drawable.login_btn_bg) : getResources().getDrawable(R.drawable.disabled_btn_bg));
    }

    private boolean validateAllInputNotEmpty() {
        return true;
    }

    private void initToolbar() {
        getSupportActionBar().setTitle("");
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getSupportActionBar().setElevation(0);

        final Drawable upArrow = getResources().getDrawable(R.mipmap.back_arrow);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initClickEvents() {
        binding.btnUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword();
            }
        });

        binding.txtInputRetypeNewPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) ||
                        (actionId == EditorInfo.IME_ACTION_DONE)) {
                    changePassword();
                }
                return false;
            }
        });
    }

    private void changePassword() {
        final String currentPassword = binding.txtInputCurrentPassword.getText().toString();
        final String newPassword = binding.txtInputNewPassword.getText().toString();
        final String retypeNewPassword = binding.txtInputRetypeNewPassword.getText().toString();

        if (!newPassword.equals(retypeNewPassword)) {
            DialogHelper.showAlertDialog(DialogHelper.Type.ERROR, null,
                    getString(R.string.change_password_new_password_confirmation_mismatch),
                    null, ChangePasswordActivity.this, true);
        } else {
            changePasswordViewModel.changePassword(currentPassword, newPassword);
        }
    }

    private void observeChangePasswordReponse() {
        changePasswordViewModel.getChangePwdResponse().observe(this, changePwdResponse -> {
                    if (changePwdResponse != null) {
                        DialogHelper.showAlertDialog(DialogHelper.Type.SUCCESS,
                                "", getString(R.string.change_password_success), null,
                                this, true);

                        binding.txtInputCurrentPassword.setText("");
                        binding.txtInputCurrentPassword.clearFocus();

                        binding.txtInputNewPassword.setText("");
                        binding.txtInputNewPassword.clearFocus();

                        binding.txtInputRetypeNewPassword.setText("");
                        binding.txtInputRetypeNewPassword.clearFocus();
                    }
                    DialogHelper.dismissProgressDialog();
                }
        );
        changePasswordViewModel.getLoading().observe(this, isLoading -> {
                    if (isLoading) {
                        DialogHelper.showProgressDialog(this, getString(R.string.change_password_in_progress));
                    }
                }
        );

        changePasswordViewModel.getErrorMsg().observe(this, errorMsg -> {
                    if (!TextUtils.isEmpty(errorMsg)) {
                        DialogHelper.dismissProgressDialog();
                        DialogHelper.showAlertDialog(DialogHelper.Type.ERROR,
                                "", errorMsg, null,
                                this, true);
                    }
                }
        );
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

    private TextWatcher inputTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            checkFieldsForEmptyValues();
        }
    };
}
