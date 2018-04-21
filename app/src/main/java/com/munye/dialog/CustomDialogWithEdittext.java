package com.munye.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.munye.user.R;

/**
 * Created by Akash on 2/4/2017.
 */

public abstract class CustomDialogWithEdittext extends Dialog implements View.OnClickListener {

    private Button btnUpdateYes , btnUpdateNo;
    private EditText edtDialogEdittext;
    private TextView tvEdittextDialogTitle;

    public CustomDialogWithEdittext(Context context , String title , String hint , String yesText , String noText) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_custom_edittext_view);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);

        edtDialogEdittext = (EditText)findViewById(R.id.edtDialogEdittext);
        btnUpdateYes = (Button)findViewById(R.id.btnUpdateYes);
        btnUpdateNo = (Button)findViewById(R.id.btnUpdateNo);
        tvEdittextDialogTitle = (TextView)findViewById(R.id.tvEdittextDialogTitle);

        tvEdittextDialogTitle.setText(title);
        edtDialogEdittext.setHint(hint);
        if(title.equalsIgnoreCase(context.getString(R.string.dilaog_title_forgot_password))){
            edtDialogEdittext.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        }
        btnUpdateYes.setText(yesText);
        btnUpdateNo.setText(noText);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        btnUpdateYes.setOnClickListener(this);
        btnUpdateNo.setOnClickListener(this);
    }

    public abstract void yesUpdate(String currentPass);
    public abstract void NoUpdate();

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.btnUpdateYes){
            yesUpdate(edtDialogEdittext.getText().toString().trim());
        }
        else if(v.getId() == R.id.btnUpdateNo) {
            NoUpdate();
        }
    }
}
