package com.munye.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.munye.user.R;
import com.munye.adapter.CountryCodeAdapter;
import com.munye.model.CountryCode;
import com.munye.utils.RecyclerViewTouchListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Akash on 2/4/2017.
 */

public abstract class CustomCountryCodeDialog extends Dialog implements RecyclerViewTouchListener.ClickListener {

    private EditText edtSearchCountryCode;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerViewCountryCode;
    private List<CountryCode> listCountryCodes;
    private List<CountryCode> listSearchCountryCode;
    private CountryCodeAdapter adapterCountryCode;
    private Context context;

    public CustomCountryCodeDialog(Context context , ArrayList<CountryCode> listCountryCode) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_country_list);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(params);

        recyclerViewCountryCode = (RecyclerView) findViewById(R.id.recyclerViewCountryCode);
        edtSearchCountryCode = (EditText) findViewById(R.id.edtSearchCountryCode);
        edtSearchCountryCode = (EditText)findViewById(R.id.edtSearchCountryCode);
        recyclerViewCountryCode.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(context);
        recyclerViewCountryCode.setLayoutManager(linearLayoutManager);

        this.listCountryCodes = listCountryCode;
        listSearchCountryCode = new ArrayList<>();
        listSearchCountryCode.addAll(listCountryCodes);
        adapterCountryCode = new CountryCodeAdapter(listSearchCountryCode);
        recyclerViewCountryCode.setAdapter(adapterCountryCode);
        this.context = context;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        edtSearchCountryCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(adapterCountryCode != null){
                    listSearchCountryCode = filter(listCountryCodes , s.toString());
                    adapterCountryCode.setFilter(listSearchCountryCode);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        recyclerViewCountryCode.addOnItemTouchListener(new RecyclerViewTouchListener(context, recyclerViewCountryCode , this));

    }

    public abstract void onSelectCountryCode(View view, int position);


    /*It is use for filter country list as per search country*/
    private List<CountryCode> filter(List<CountryCode> listModel, String query) {
        final List<CountryCode> filteredModelList = new ArrayList<>();
        for (CountryCode countryCode : listModel) {
            final String text = countryCode.getCountryName().toLowerCase();
            if (text.contains(query.toLowerCase())) {
                filteredModelList.add(countryCode);
            }
        }
        return filteredModelList;
    }

    @Override
    public void onClickRecyclerListItem(View view, int position) {
        onSelectCountryCode(view , position);
    }

    @Override
    public void onLongClickRecyclerListItem(View view, int position) {

    }
}
