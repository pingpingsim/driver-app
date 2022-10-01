package com.pingu.driverapp.ui.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.work.WorkManager;

import com.pingu.driverapp.BuildConfig;
import com.pingu.driverapp.R;
import com.pingu.driverapp.databinding.FragmentProfileBinding;
import com.pingu.driverapp.ui.login.LoginActivity;
import com.pingu.driverapp.ui.profile.changepassword.ChangePasswordActivity;
import com.pingu.driverapp.util.Constants;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

public class ProfileFragment extends DaggerFragment {
    private FragmentProfileBinding binding;
    private ProfileViewModel profileViewModel;
    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        profileViewModel =
                ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel.class);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.title_profile));
        binding.txtProfileAppVersion.setText(String.format(getString(R.string.profile_app_version_info), BuildConfig.VERSION_NAME));

        initClickEvents();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);

        return binding.getRoot();
    }

    private void initClickEvents() {
        binding.menuChangePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent changePwdIntent = new Intent(getContext(), ChangePasswordActivity.class);
                startActivity(changePwdIntent);
            }
        });
        binding.menuLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutConfirmation();
            }
        });
    }

    private void logoutConfirmation() {
        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.profile_dialog_logout_title))
                .setMessage(getString(R.string.profile_dialog_logout_msg))
                .setIcon(R.mipmap.toolbar_logo)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        logoutAction();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void logoutAction() {
        WorkManager.getInstance(getContext()).cancelAllWorkByTag(Constants.PERIODIC_PENDING_TASK_CHECKER);
        profileViewModel.deleteLocalData();
        Intent loginIntent = new Intent(getContext(), LoginActivity.class);
        startActivity(loginIntent);
        getActivity().finish();
    }
}