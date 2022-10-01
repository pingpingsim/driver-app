package com.pingu.driverapp.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.pingu.driverapp.R;
import com.pingu.driverapp.widget.CustomDialog;

public class DialogHelper {
    private static DialogHelper dialogInstance;
    // alert dialog
    private static CustomDialog customDialog;
    //progress dialog
    private static AlertDialog.Builder progressDialogBuilder;
    private static AlertDialog progressDialog;

    public enum Type {
        SUCCESS,
        ERROR,
        INFO,
        CONFIRMATION,
    }

    private DialogHelper() {
    }

    public static DialogHelper getInstance(Context context) {
        if (dialogInstance == null)
            dialogInstance = new DialogHelper();

        return dialogInstance;
    }

    public static void dismissAlertDialog() {
        try {
            if (customDialog != null) {
                customDialog.dismiss();
                customDialog.cancel();
            }
        } catch (Exception ex) {
        }
    }

    public static void dismissProgressDialog() {
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog.cancel();
            }
        } catch (Exception ex) {
        }
    }

    public static void showAlertDialog(final Type dialogType, final String title, final String primaryMsg,
                                       final String secondaryMsg, final Context context, boolean showOkBtn) {
        try {
            if (customDialog != null && customDialog.isShowing())
                dismissAlertDialog();

            customDialog = new CustomDialog((Activity) context, dialogType, title, primaryMsg, secondaryMsg, showOkBtn);
            customDialog.setCancelable(false);
            if (!((Activity) context).isFinishing()) {
                customDialog.show();
            }
        } catch (Exception ex) {
        }
    }

    public static void showProgressDialog(final Context context, final String msg) {
        try {
            progressDialog = getDialogProgressBar(msg, context).create();
            if (!((Activity) context).isFinishing()) {
                progressDialog.show();
            }
        } catch (Exception ex) {
        }
    }

    private static AlertDialog.Builder getDialogProgressBar(final String msg, final Context context) {
        progressDialogBuilder = new AlertDialog.Builder(context);
        progressDialogBuilder.setCancelable(false);
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View alertLoadingView = inflater.inflate(R.layout.alert_loading, null);
        final TextView msgTextView = alertLoadingView.findViewById(R.id.txt_msg);
        msgTextView.setText(msg);
        progressDialogBuilder.setView(alertLoadingView);

        return progressDialogBuilder;
    }
}

