package com.pingu.driverapp.ui.forgotpassword;

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
import com.pingu.driverapp.databinding.ActivityForgotPasswordBinding;
import com.pingu.driverapp.util.DialogHelper;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public class ForgotPasswordActivity extends DaggerAppCompatActivity {
    private ActivityForgotPasswordBinding binding;
    private ForgotPasswordViewModel forgotPasswordViewModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password);
        forgotPasswordViewModel = ViewModelProviders.of(this, viewModelFactory).get(ForgotPasswordViewModel.class);
        initUI();
        initToolbar();
        observeResetPasswordReponse();
        initClickEvents();
    }

    private void initUI() {
        binding.txtInputEditEmail.addTextChangedListener(inputTextWatcher);
        checkFieldsForEmptyValues();
    }

    private void initToolbar() {
        getSupportActionBar().setTitle(getString(R.string.reset_password_screen_title));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getSupportActionBar().setElevation(0);

        final Drawable upArrow = getResources().getDrawable(R.mipmap.back_arrow);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initClickEvents() {
        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitEmail();
            }
        });

        binding.txtInputEditEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) ||
                        (actionId == EditorInfo.IME_ACTION_DONE)) {
                    submitEmail();
                }
                return false;
            }
        });
    }

    private void observeResetPasswordReponse() {
        forgotPasswordViewModel.getResetPwdResponse().observe(this, resetPwdResponse -> {
                    if (resetPwdResponse != null) {
                        DialogHelper.showAlertDialog(DialogHelper.Type.SUCCESS,
                                "", resetPwdResponse.getMessage(), null,
                                this, true);

                        binding.txtInputEditEmail.setText("");
                        binding.txtInputEditEmail.clearFocus();
                    }
                    DialogHelper.dismissProgressDialog();
                }
        );
        forgotPasswordViewModel.getLoading().observe(this, isLoading -> {
                    if (isLoading) {
                        DialogHelper.showProgressDialog(this, getString(R.string.reset_password_in_progress));
                    }
                }
        );

        forgotPasswordViewModel.getErrorMsg().observe(this, errorMsg -> {
                    if (!TextUtils.isEmpty(errorMsg)) {
                        DialogHelper.dismissProgressDialog();
                        DialogHelper.showAlertDialog(DialogHelper.Type.ERROR,
                                "", errorMsg, null,
                                this, true);
                    }
                }
        );
    }

    private void submitEmail() {
        final String email = binding.txtInputEditEmail.getText().toString();

        if (TextUtils.isEmpty(email)) {
            DialogHelper.showAlertDialog(DialogHelper.Type.ERROR, null,
                    getString(R.string.reset_password_email_required),
                    null, ForgotPasswordActivity.this, true);
        } else {
            forgotPasswordViewModel.resetPassword(email);
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

    private void checkFieldsForEmptyValues() {
        final String email = binding.txtInputEditEmail.getText().toString();

        if (TextUtils.isEmpty(email)) {
            controlSubmitBtn(false);
        } else {
            controlSubmitBtn(true);
        }
    }

    private void controlSubmitBtn(final boolean isEnabled) {
        binding.btnSend.setEnabled(isEnabled);
        binding.btnSend.setBackground(isEnabled ? getResources().getDrawable(R.drawable.login_btn_bg) : getResources().getDrawable(R.drawable.disabled_btn_bg));
    }
}
