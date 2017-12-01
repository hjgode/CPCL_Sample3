package com.mobile.app.assist;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;
import com.mobile.app.assist.*;

import com.mobile.app.cpcl.R;

import java.util.ArrayList;
import java.util.List;

public class OptionsActivity extends Activity implements AdapterView.OnItemSelectedListener {

    boolean bUseAutoscale=true;
    int iPrinterwidth=3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        CheckBox checkBox=(CheckBox)findViewById(R.id.chkAutoscale);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CheckBox) view).isChecked()) {
                    Toast.makeText(OptionsActivity.this,"Autoscale ", Toast.LENGTH_LONG).show();
                    bUseAutoscale=true;
                }
                else
                    bUseAutoscale=false;
            }
        });
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

    void saveChanges() {
        // Create object of SharedPreferences.
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        //now get Editor
        SharedPreferences.Editor editor = sharedPref.edit();
        //put your value
        editor.putBoolean(CONSTANTS.PrefUseAutoscale, bUseAutoscale);
        editor.putInt(com.mobile.app.assist.CONSTANTS.PrefPDFfilename, iPrinterwidth);
        //commits your edits
        editor.commit();
    }
}
