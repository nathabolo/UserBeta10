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
 * Created by Akash on 2/8/2017.
 */

public abstract class CustomTitleDialog extends Dialog implements View.OnClickListener {
    private Button btnTitleDialogYes , btnTitleDialogNo;
    private TextView tvDialogTitle , tvTitleDialogContent;


    public CustomTitleDialog(Context context , String title , String content , String textYes , String textNo) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_custom_with_tittle_view);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);


        btnTitleDialogYes = (Button)findViewById(R.id.btnTitleDialogYes);
        btnTitleDialogNo = (Button)findViewById(R.id.btnTitleDialogNo);
        tvDialogTitle = (TextView)findViewById(R.id.tvDialogTitle);
        tvTitleDialogContent = (TextView)findViewById(R.id.tvTitleDialogContent);
        tvDialogTitle.setText(title);
        tvTitleDialogContent.setText(content);
        btnTitleDialogYes.setText(textYes);
        btnTitleDialogNo.setText(textNo);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        btnTitleDialogNo.setOnClickListener(this);
        btnTitleDialogYes.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btnTitleDialogYes:
                positiveResponse();
                break;

            case R.id.btnTitleDialogNo:
                negativeResponse();
                break;

            default:
                //Default action here...
                break;
        }
    }

    public abstract void positiveResponse();
    public abstract void negativeResponse();
}
