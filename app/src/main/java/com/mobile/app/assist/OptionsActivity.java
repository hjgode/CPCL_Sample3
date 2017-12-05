package com.mobile.app.assist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.app.cpcl.R;

import java.util.ArrayList;
import java.util.List;

public class OptionsActivity extends Activity implements AdapterView.OnItemSelectedListener {

    boolean bUseAutoscale=true;
    int iPrinterwidth=3;

    TextView txtManualScaleFactorLabel;
    TextView txtOptionsScaleValue;
    Button btnSave;
    Button btnCancel;

    float fManualScale=0;
    Context context;
    Options thisOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        Options thisOptions=new Options(context);
        setContentView(R.layout.activity_options);
        // Spinner element
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("2");
        categories.add("3");
        categories.add("4");
        categories.add("5");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        txtManualScaleFactorLabel=(TextView)findViewById(R.id.txtManualScaleFactorLabel);
        txtOptionsScaleValue=(TextView)findViewById(R.id.txtOptionsScaleValue);
        CheckBox checkBox=(CheckBox)findViewById(R.id.chkAutoscale);

        //load values
        thisOptions.loadOptions();
        fManualScale=thisOptions._fManualScale;
        bUseAutoscale=thisOptions._bUseAutoscale;
        iPrinterwidth=thisOptions._iPrinterwidth;

        //updateUI
        txtOptionsScaleValue.setText(""+fManualScale);
        checkBox.setChecked(bUseAutoscale);
        showManualScale(!bUseAutoscale);
        switch (iPrinterwidth) {
            case 2:
                spinner.setSelection(0);
                break;
            case 3:
                spinner.setSelection(1);
                break;
            case 4:
                spinner.setSelection(2);
                break;
            case 5:
                spinner.setSelection(3);
                break;
            default:
                spinner.setSelection(1);
                break;
        }
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CheckBox) view).isChecked()) {
                    Toast.makeText(OptionsActivity.this,"Autoscale activated", Toast.LENGTH_LONG).show();
                    bUseAutoscale=true;
                }
                else {
                    bUseAutoscale = false;
                    Toast.makeText(OptionsActivity.this,"Autoscale disabled", Toast.LENGTH_LONG).show();
                }
                showManualScale(!bUseAutoscale);
            }
        });

        btnCancel=(Button)findViewById(R.id.btnOptionsCancel);
        btnSave=(Button)findViewById(R.id.btnOptionsSave);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thisOptions.saveChanges(bUseAutoscale,iPrinterwidth,fManualScale);
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
        showManualScale(!bUseAutoscale);
    }

    void showManualScale(boolean bShow){
        if(bShow){
            txtOptionsScaleValue.setVisibility(View.VISIBLE);
            txtManualScaleFactorLabel.setVisibility(View.VISIBLE);
        }else{
            txtOptionsScaleValue.setVisibility(View.GONE);
            txtManualScaleFactorLabel.setVisibility(View.GONE);
        }
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();
        int i=Integer.parseInt(item);
        iPrinterwidth=i;
        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

}
