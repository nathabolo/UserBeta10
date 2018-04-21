package com.munye.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.munye.user.R;
import com.munye.utils.AndyUtils;

/**
 * Created by Akash on 1/25/2017.
 */

public class MyRaidProBoldTextView extends TextView {

    private Typeface typeface;

    public MyRaidProBoldTextView(Context context) {
        super(context);
    }

    public MyRaidProBoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public MyRaidProBoldTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.app);
        String customFont = a.getString(R.styleable.app_customFont);
        setCustomFont(ctx, customFont);
        a.recycle();
    }

    private boolean setCustomFont(Context ctx, String asset) {
        try {
            if (typeface == null) {
                typeface = Typeface.createFromAsset(ctx.getAssets(),
                        "fonts/MyriadPro-BoldCond.otf");
            }

        } catch (Exception e) {
            AndyUtils.generateLog("Exception in component"+e);
            return false;
        }

        setTypeface(typeface);
        return true;
    }

}
