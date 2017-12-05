package com.mobile.app.assist;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by E841719 on 04.12.2017.
 */

public class Options {
    Context _context;
    boolean _bUseAutoscale=true;
    int _iPrinterwidth=600;
    float _fManualScale=3f;

    public Options(Context cont){
        _context=cont;
    }
    public void saveChanges(boolean bUseAutoscale, int iPrinterwidth, float fManualScale) {
        _bUseAutoscale=bUseAutoscale;
        _iPrinterwidth=iPrinterwidth;
        _fManualScale=fManualScale;
        // Create object of SharedPreferences.
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(_context);
        //now get Editor
        SharedPreferences.Editor editor = sharedPref.edit();
        //put your value
        if(bUseAutoscale) {
            fManualScale = 0f;
        }
        editor.putBoolean(CONSTANTS.PrefUseAutoscale, bUseAutoscale);
        editor.putFloat(CONSTANTS.PrefScaleValue, fManualScale);
        editor.putInt(com.mobile.app.assist.CONSTANTS.PrefPrinterWidth, iPrinterwidth);
        //commits your edits
        editor.commit();
    }
    public void loadOptions(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(_context);
        boolean b=sharedPref.getBoolean(CONSTANTS.PrefUseAutoscale,true);
        int i=sharedPref.getInt(CONSTANTS.PrefPrinterWidth, 3);
        float f=sharedPref.getFloat(CONSTANTS.PrefScaleValue, 1);
        _bUseAutoscale=b;
        _iPrinterwidth=i;
        if(_bUseAutoscale)
            _fManualScale=0;
        else
            _fManualScale=f;
    }
}
