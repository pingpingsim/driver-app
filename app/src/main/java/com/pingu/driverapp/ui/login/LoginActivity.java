package com.pingu.driverapp.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.pingu.driverapp.BuildConfig;
import com.pingu.driverapp.R;
import com.pingu.driverapp.databinding.ActivityLoginBinding;
import com.pingu.driverapp.ui.base.BaseActivity;
import com.pingu.driverapp.ui.forgotpassword.ForgotPasswordActivity;
import com.pingu.driverapp.ui.main.MainActivity;
import com.pingu.driverapp.util.DialogHelper;

import javax.inject.Inject;

public class LoginActivity extends BaseActivity {
    private ActivityLoginBinding binding;
    private LoginViewModel loginViewModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel.class);

        binding.txtVersionInfo.setText(String.format(getString(R.string.login_version_info), BuildConfig.VERSION_NAME));

        observeAuthenticationReponse();
        initToolbar();
        initClickEvents();
    }

    private void initToolbar() {
        getSupportActionBar().setTitle("");
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getSupportActionBar().setElevation(0);
    }

    private void initClickEvents() {
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginAction();
            }
        });

        binding.iconPasswordVisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.iconPasswordVisible.setVisibility(View.GONE);
                binding.iconPasswordInvisible.setVisibility(View.VISIBLE);
                binding.txtInputEditPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        binding.iconPasswordInvisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.iconPasswordVisible.setVisibility(View.VISIBLE);
                binding.iconPasswordInvisible.setVisibility(View.GONE);
                binding.txtInputEditPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
        });
        binding.txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent forgotPasswordIntent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(forgotPasswordIntent);
            }
        });


        binding.txtInputEditPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) ||
                        (actionId == EditorInfo.IME_ACTION_DONE)) {
                    loginAction();
                }
                return false;
            }
        });
    }

    private void loginAction() {
        hideSoftKeyboard();
        final String username = binding.txtInputEditUsername.getText().toString();
        final String password = binding.txtInputEditPassword.getText().toString();
        final String errorMsg = validateInput(username, password);

        if (TextUtils.isEmpty(errorMsg)) {
            loginViewModel.authenticateUser(username, password);
        } else {
            DialogHelper.showAlertDialog(DialogHelper.Type.ERROR, "", errorMsg,
                    "", LoginActivity.this, true);
        }
    }

    private void observeAuthenticationReponse() {
        loginViewModel.getProfileResponse().observe(this, profileData -> {
                    DialogHelper.dismissProgressDialog();
                    if (profileData != null) {
                        Intent dashboardIntent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(dashboardIntent);
                        finish();
                    }
                }
        );

        loginViewModel.getLoading().observe(this, isLoading -> {
                    if (isLoading) {
                        DialogHelper.showProgressDialog(this,
                                getString(R.string.login_user_auth_in_progress));
                    }
                }
        );

        loginViewModel.getErrorMsg().observe(this, errorMsg -> {
                    if (!TextUtils.isEmpty(errorMsg)) {
                        DialogHelper.dismissProgressDialog();
                        DialogHelper.showAlertDialog(DialogHelper.Type.ERROR,
                                "", errorMsg, null,
                                this, true);
                    }
                }
        );
    }

    private String validateInput(final String username, final String password) {
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            return null;
        } else {
            if (TextUtils.isEmpty(username) && TextUtils.isEmpty(password)) {
                return getString(R.string.msg_error_username_password_empty);
            } else if (TextUtils.isEmpty(username)) {
                return getString(R.string.msg_error_username_empty);
            } else {
                return getString(R.string.msg_error_password_empty);
            }
        }
    }
}
