package com.munye;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.munye.user.R;
import com.munye.dialog.ImageDialog;
import com.munye.model.PreviousJob;
import com.munye.parse.AsyncTaskCompleteListener;
import com.munye.parse.HttpRequester;
import com.munye.utils.AndyUtils;
import com.munye.utils.Const;
import com.munye.utils.Formatter;

import java.util.HashMap;

public class PreviousJobInfoActivity extends ActionBarBaseActivity implements View.OnClickListener, AsyncTaskCompleteListener {

    private ImageView imgViewHistroyJobIcon , imgHistoryIssueImage , imgHistoryProviderImage , imgFeedbackMessage;
    private TextView tvHistoryJobTitle , tvHistoryJobType , tvHistoryJobDate;
    private TextView tvHistoryProviderName , tvJobCancelStatus , tvHistoryJobDescription , tvHistoryJobAmount , tvHistoryJobAddress , tvFeedback;
    private RatingBar ratingHistoryProvider;
    private Button btnRetryRequest;
    private Bundle previousJobData;
    private PreviousJob previousJob;
    private int position;
    private ImageDialog imageDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_job_info);

        initRequire();

        previousJobData = getIntent().getExtras();
        previousJob = previousJobData.getParcelable("OBJECTDATA");
        position = previousJobData.getInt("POSITION");

        tvHistoryJobTitle.setText(previousJob.getJobTitle());
        tvHistoryJobTitle.setTextColor(Color.parseColor(AndyUtils.getColorCode(position % 6 , this)));
        tvHistoryJobDate.setText(Formatter.getDateInFormate(previousJob.getJobDate()));
        tvHistoryProviderName.setText(previousJob.getProviderName());
        tvHistoryJobDescription.setText(previousJob.getDescription());
        tvHistoryJobAmount.setText(AndyUtils.getSymbolFromHex(AndyUtils.getSymbolFromHex(previousJob.getCurrency())+Formatter.formateDigitAfterPoint(previousJob.getTotalAmount())));
        tvHistoryJobAddress.setText(previousJob.getAddress());

        if(TextUtils.isEmpty(previousJob.getFeedback())) {
            tvFeedback.setText(getString(R.string.txt_no_feedback));
        }
        else{
            tvFeedback.setText(previousJob.getFeedback());
        }

        Glide.with(this)
                .load(previousJob.getJobTypeIcon())
                .skipMemoryCache(true)
                .dontAnimate()
                .placeholder(R.drawable.placeholder_img)
                .into(imgViewHistroyJobIcon);
        imgViewHistroyJobIcon.setColorFilter(Color.parseColor(AndyUtils.getColorCode(position % 6 , this)));
        Glide.with(this).load(previousJob.getIssueImage()).placeholder(getResources().getDrawable(R.drawable.no_img)).skipMemoryCache(true).into(imgHistoryIssueImage);
        Glide.with(this)
                .load(previousJob.getProviderPicture())
                .asBitmap()
                .skipMemoryCache(true)
                .placeholder(getResources().getDrawable(R.drawable.default_icon))
                .into(new BitmapImageViewTarget(imgHistoryProviderImage){
                    @Override
                    protected void setResource(Bitmap resource) {
                        super.setResource(resource);
                        RoundedBitmapDrawable cirimage = RoundedBitmapDrawableFactory.create(getResources(),resource);
                        cirimage.setCircular(true);
                        imgHistoryProviderImage.setImageDrawable(cirimage);
                    }
                });

        ratingHistoryProvider.setRating((float) previousJob.getUserGivenRate());

        btnRetryRequest.setOnClickListener(this);


        switch (previousJob.getRequestType()){
            case 0:
                tvHistoryJobType.setText(getString(R.string.txt_repair_maintenance));
                break;

            case 1:
                tvHistoryJobType.setText(getString(R.string.txt_installation));
                break;

            default:
                AndyUtils.generateLog("No types");
                break;
        }


        switch (previousJob.getRequestStatus()){

            case Const.RequestStatus.CUSTOMER_RATED:
            case Const.RequestStatus.TRADESMAN_RATED:
                tvJobCancelStatus.setVisibility(View.GONE);
                break;

            case Const.RequestStatus.CANCEL_BY_PROVIDER:
                hideFeedback();
                tvJobCancelStatus.setVisibility(View.VISIBLE);
                tvJobCancelStatus.setText(getString(R.string.cancel_status_tradesmman));
                btnRetryRequest.setVisibility(View.VISIBLE);
                break;

            case Const.RequestStatus.CANCEL_BY_USER:
                hideFeedback();
                tvJobCancelStatus.setVisibility(View.VISIBLE);
                tvJobCancelStatus.setText(getString(R.string.cancel_status_you));
                break;

            default:
                AndyUtils.generateLog("No request status");
                break;
        }

    }

    private void initRequire() {

        initToolBar();

        imgBtnDrawerToggle.setVisibility(View.INVISIBLE);
        setToolBarTitle(getString(R.string.title_previous_job));
        imgViewHistroyJobIcon = (ImageView)findViewById(R.id.imgViewHistroyJobIcon);
        imgHistoryIssueImage = (ImageView)findViewById(R.id.imgHistoryIssueImage);
        imgHistoryProviderImage = (ImageView)findViewById(R.id.imgHistoryProviderImage);

        tvHistoryJobTitle = (TextView)findViewById(R.id.tvHistoryJobTitle);
        tvHistoryJobType = (TextView)findViewById(R.id.tvHistoryJobType);
        tvHistoryJobDate = (TextView)findViewById(R.id.tvHistoryJobDate);
        tvHistoryProviderName = (TextView)findViewById(R.id.tvHistoryProviderName);
        tvJobCancelStatus = (TextView)findViewById(R.id.tvJobCancelStatus);
        tvHistoryJobDescription = (TextView)findViewById(R.id.tvHistoryJobDescription);
        tvHistoryJobAmount = (TextView)findViewById(R.id.tvHistoryJobAmount);
        tvHistoryJobAddress = (TextView)findViewById(R.id.tvHistoryJobAddress);
        tvFeedback = (TextView)findViewById(R.id.tvFeedback);
        imgFeedbackMessage = (ImageView)findViewById(R.id.imgFeedbackMessage);

        ratingHistoryProvider = (RatingBar)findViewById(R.id.ratingHistoryProvider);

        btnRetryRequest = (Button)findViewById(R.id.btnRetryRequest);

    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.btnRetryRequest){
            retryRequest();
        }
        else if(v.getId() == R.id.imgHistoryIssueImage){
            showImage(previousJob.getIssueImage());
        }
    }


    private void showImage(String imgUrl){
        if(!TextUtils.isEmpty(imgUrl))
        {
            imageDialog = new ImageDialog(this ,imgUrl);
            imageDialog.show();
        }
    }


    private void hideFeedback(){
        tvFeedback.setVisibility(View.GONE);
        imgFeedbackMessage.setVisibility(View.GONE);
    }

    private void retryRequest() {

        HashMap<String , String> map = new HashMap<>();

        map.put(Const.URL , Const.ServiceType.RETRY_REQUEST);
        map.put(Const.Params.ID , preferenceHelper.getId());
        map.put(Const.Params.TOKEN , preferenceHelper.getToken());
        map.put(Const.Params.REQUEST_ID , previousJob.getPrviousJobId());

        AndyUtils.showCustomProgressDialog(this , false);
        new HttpRequester(this , map , Const.ServiceCode.RETRY_REQUEST , Const.httpRequestType.POST , this);

    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {

        AndyUtils.removeCustomProgressDialog();
        if(serviceCode == Const.ServiceCode.RETRY_REQUEST){
            if(dataParser.isSuccess(response)){
                Intent intentBack = new Intent();
                intentBack.putExtra("POSITION" , position);
                setResult(Const.ACTION_RETRY_REQUEST  , intentBack);
                onBackPressed();
            }
        }

    }
}
