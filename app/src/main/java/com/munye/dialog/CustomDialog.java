package com.munye.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.munye.user.R;

/**
 * Created by Akash on 2/7/2017.
 */

public abstract class CustomDialog extends Dialog implements View.OnClickListener {

    private Button btnDialogYes , btnDialogNo;
    private TextView tvDialogContent;

    public CustomDialog(Context context ,String content , String yes , String no , boolean isCancelable) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_custom_view);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);
        setCancelable(isCancelable);

        btnDialogYes = (Button)findViewById(R.id.btnDialogYes);
        btnDialogNo = (Button)findViewById(R.id.btnDialogNo);
        tvDialogContent = (TextView)findViewById(R.id.tvDialogContent);
        tvDialogContent.setText(content);
        btnDialogYes.setText(yes);
        btnDialogNo.setText(no);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        btnDialogYes.setOnClickListener(this);
        btnDialogNo.setOnClickListener(this);
    }

    public abstract void positiveButton();
    public abstract void negativeButton();

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btnDialogYes:
                positiveButton();
                break;

            case R.id.btnDialogNo:
                negativeButton();
                break;

            default:
                //Default action...
                break;
        }

    }
}
