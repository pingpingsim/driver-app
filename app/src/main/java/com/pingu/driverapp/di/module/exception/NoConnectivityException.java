package com.pingu.driverapp.di.module.exception;

import com.pingu.driverapp.BaseApplication;
import com.pingu.driverapp.R;

import java.io.IOException;

public class NoConnectivityException extends IOException {

    @Override
    public String getMessage() {
        return BaseApplication.getInstance().getString(R.string.error_no_internet);
    }
}
