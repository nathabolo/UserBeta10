package com.munye;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.munye.dialog.CustomTitleDialog;
import com.munye.user.R;
import com.munye.model.ActiveJob;
import com.munye.parse.AsyncTaskCompleteListener;
import com.munye.parse.HttpRequester;
import com.munye.utils.AndyUtils;
import com.munye.utils.Const;
import com.munye.utils.Formatter;

import java.util.HashMap;

public class FeedbackActivity extends ActionBarBaseActivity implements View.OnClickListener, AsyncTaskCompleteListener {

    private Bundle feedbackData;
    private Button btnSubmitFeedback;
    private ActiveJob activeJob;
    private int position;
    private TextView tvFeedbackJobTitle ,tvFeedbackJobType , tvFeedbackJobDate , tvFeedbackJobDescription ,tvFeedbackJobAmount , tvFeedbackProviderName;
    private ImageView imgFeedbackJobIcon , imgFeedbackProviderImage;
    private EditText edtFeedback;
    private RatingBar ratingFeedback;
    private float rating;
    private String comment;
    private CustomTitleDialog exitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        initRequire();

        feedbackData = getIntent().getExtras();
        activeJob = feedbackData.getParcelable("FEEDBACK");
        position = feedbackData.getInt("position");

        tvFeedbackJobTitle.setText(activeJob.getTitle());
        tvFeedbackJobTitle.setTextColor(Color.parseColor(AndyUtils.getColorCode(position % 6 , this)));
        tvFeedbackJobDate.setText(Formatter.getDateInFormate(activeJob.getDate()));
        tvFeedbackJobDescription.setText(activeJob.getDescription());
        tvFeedbackJobAmount.setText(AndyUtils.getSymbolFromHex(activeJob.getCurrency())+activeJob.getAmount());
        tvFeedbackProviderName.setText(activeJob.getProviderName());

        switch (activeJob.getRequestType()){
            case Const.JobRequestType.TYPE_REPAIR_MAINTENANCE:
                tvFeedbackJobType.setText(getString(R.string.txt_repair_maintenance));
                break;

            case Const.JobRequestType.TYPE_INSTALLATION:
                tvFeedbackJobType.setText(getString(R.string.txt_installation));
                break;

            default:
                AndyUtils.generateLog("No srecice Type");
                break;
        }


        Glide.with(this).load(activeJob.getJobTypeIcon()).placeholder(R.mipmap.ic_launcher).skipMemoryCache(true).into(imgFeedbackJobIcon);
        imgFeedbackJobIcon.setColorFilter(Color.parseColor(AndyUtils.getColorCode(position % 6 , this)));
        Glide.with(this)
                .load(activeJob.getProviderPicture())
                .asBitmap()
                .placeholder(getResources().getDrawable(R.drawable.default_icon))
                .into(new BitmapImageViewTarget(imgFeedbackProviderImage){
                    @Override
                    protected void setResource(Bitmap resource) {
                        super.setResource(resource);
                        RoundedBitmapDrawable cirimage = RoundedBitmapDrawableFactory.create(getResources(),resource);
                        cirimage.setCircular(true);
                        imgFeedbackProviderImage.setImageDrawable(cirimage);
                    }
                });

        btnSubmitFeedback.setOnClickListener(this);

    }

    private void initRequire() {
        initToolBar();
        setToolBarTitle(getString(R.string.title_feedback));
        imgBtnDrawerToggle.setVisibility(View.INVISIBLE);

        btnSubmitFeedback = (Button)findViewById(R.id.btnSubmitFeedback);
        tvFeedbackJobTitle = (TextView)findViewById(R.id.tvFeedbackJobTitle);
        tvFeedbackJobType = (TextView)findViewById(R.id.tvFeedbackJobType);
        tvFeedbackJobDate = (TextView)findViewById(R.id.tvFeedbackJobDate);
        tvFeedbackJobDescription = (TextView)findViewById(R.id.tvFeedbackJobDescription);
        tvFeedbackJobAmount = (TextView)findViewById(R.id.tvFeedbackJobAmount);
        tvFeedbackProviderName = (TextView)findViewById(R.id.tvFeedbackProviderName);

        imgFeedbackJobIcon = (ImageView)findViewById(R.id.imgFeedbackJobIcon);
        imgFeedbackProviderImage = (ImageView)findViewById(R.id.imgFeedbackProviderImage);

        edtFeedback = (EditText)findViewById(R.id.edtFeedback);

        ratingFeedback = (RatingBar)findViewById(R.id.ratingFeedback);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnSubmitFeedback){
            checkValidData();
        }
    }

    private void checkValidData() {

        comment = edtFeedback.getText().toString().trim();
        rating = ratingFeedback.getRating();
        if(ratingFeedback.getRating() <=0){
            AndyUtils.showToast(this , "Please Provide Ratting");
        }
        else if(TextUtils.isEmpty(comment)){
            AndyUtils.showToast(this, "Please Provide Feedback");
        }
        else {
            submitFeedback();
        }
    }

    private void submitFeedback() {

        HashMap<String , String> map = new HashMap<>();

        map.put(Const.URL , Const.ServiceType.FEEDBACK);
        map.put(Const.Params.ID , preferenceHelper.getId());
        map.put(Const.Params.TOKEN , preferenceHelper.getToken());
        map.put(Const.Params.REQUEST_ID , activeJob.getActiveJobId());
        map.put(Const.Params.RATING , String.valueOf(rating));
        map.put(Const.Params.COMMENT , comment);

        AndyUtils.showCustomProgressDialog(this , false);
        new HttpRequester(this , map , Const.ServiceCode.FEEDBACK , Const.httpRequestType.POST , this);




    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {

        AndyUtils.removeCustomProgressDialog();
        if(dataParser.isSuccess(response)){
            Intent intentFeedbackBack = new Intent();
            intentFeedbackBack.putExtra("POSITION" , position);
            setResult(Const.ACTION_FEEDBACK , intentFeedbackBack);
            //onBackPressed();
            exitDialog = new CustomTitleDialog(FeedbackActivity.this , "JimmieJobs Feedback" ,  "Thank you for the Feedback, Now you will proceed to your jobs and complete the payment by entering your payment details" ,
                    "OK", "") {

                public void positiveResponse() {
                    Intent show_map = new Intent(FeedbackActivity.this,MyJobsActivity.class);
                    startActivity(show_map);
                }

                public void negativeResponse() {
                    exitDialog.dismiss();
                }
            };
            exitDialog.show();
        }

    }


}
