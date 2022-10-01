package com.pingu.driverapp.widget;

import android.app.Activity;
import android.app.Dialog;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pingu.driverapp.R;
import com.pingu.driverapp.util.DialogHelper;

public class CustomDialog extends Dialog implements
        android.view.View.OnClickListener {

    public Activity activity;
    public Button okBtn;
    private TextView primaryMsgTextView;
    private TextView secondaryMsgTextView;
    private ImageView statusImageView;
    private DialogHelper.Type dialogType;
    private String title;
    private String primaryMessage;
    private String secondaryMessage;
    private boolean showOkBtn;

    public CustomDialog(Activity activity, DialogHelper.Type type, String title, String primaryMessage, String secondaryMessage, boolean showOkBtn) {
        super(activity);
        this.activity = activity;
        this.dialogType = type;
        this.primaryMessage = primaryMessage;
        this.secondaryMessage = secondaryMessage;
        this.title = title;
        this.showOkBtn = showOkBtn;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_custom);
        primaryMsgTextView = (TextView) findViewById(R.id.txt_msg_primary);
        secondaryMsgTextView = (TextView) findViewById(R.id.txt_msg_secondary);
        statusImageView = (ImageView) findViewById(R.id.img_status);
        okBtn = (Button) findViewById(R.id.btn_ok);
        okBtn.setOnClickListener(this);
        okBtn.setVisibility(this.showOkBtn ? View.VISIBLE : View.GONE);
        secondaryMsgTextView.setVisibility(!TextUtils.isEmpty(secondaryMessage) ? View.VISIBLE : View.GONE);

        if (dialogType == DialogHelper.Type.SUCCESS || dialogType == DialogHelper.Type.INFO || dialogType == DialogHelper.Type.CONFIRMATION) {
            statusImageView.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.done));
            primaryMsgTextView.setTextColor(getContext().getResources().getColor(R.color.black));
        } else if (dialogType == DialogHelper.Type.ERROR) {
            statusImageView.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.error));
            primaryMsgTextView.setTextColor(getContext().getResources().getColor(R.color.dialog_error));
        }

        if (!TextUtils.isEmpty(secondaryMessage))
            secondaryMsgTextView.setText(secondaryMessage);
        primaryMsgTextView.setText(primaryMessage);

        //TODO, If dont show OK button, auto dismiss in N seconds
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                break;
            default:
                break;
        }
        dismiss();
    }
}
