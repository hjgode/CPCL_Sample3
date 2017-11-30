package com.mobile.app.cpcl;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.app.assist.AlertView;
import com.mobile.app.assist.CONSTANTS;
import com.mobile.app.assist.CPCLSample3;
import com.mobile.app.assist.PDFprint;

import java.io.File;
import java.io.IOException;

/**
 * Created by E841719 on 28.11.2017.
 */

public class PrintPDFmenReplacement extends Activity {
    String bitmapFilename=null;
    String pdfFilename=null;

    static final String TAG = "PrintPDFmenReplacement";
    static final int FILE_SELECT_PDF = 1;

    Context context;
    Object _printBitmapTask=null;
    Object _renderBitmapTask=null;

    Button btnLoadPdfFile;
    TextView txtPDFname;
    Button btnPrintPDF;
    ImageView imageView;

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.d("PrintPDFmenReplacement", "OnDestroy");
        saveDataForPrint("", ""); //clear known data
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.print_pdf_menu_replacement);

        imageView=(ImageView)findViewById(R.id.imageView);
        context=this;

        btnLoadPdfFile=(Button)findViewById(R.id.btnLoadPDF);
        btnLoadPdfFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFile(FILE_SELECT_PDF);
            }
        });
        txtPDFname =(TextView)findViewById(R.id.txtPDFname);
        btnPrintPDF=(Button)findViewById(R.id.btnPrintPDF);
        btnPrintPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(_printBitmapTask!=null){
                    Toast.makeText(context, "Already printing, please wait", Toast.LENGTH_LONG);
                }
                bitmapFilename=getBitmapFilename();
                if(bitmapFilename!=null && bitmapFilename.length()>0){
                    if(_printBitmapTask==null){
                        new PrintPDFmenReplacement.printBitmapFiles().execute(bitmapFilename);
                    }
                    else{
                        Toast.makeText(context, "Already printing, please wait", Toast.LENGTH_LONG);
                    }
                    //sample.image3(count, bitmapFilename);
                }
                else {
                    AlertView.showAlert("Error with Bitmapfile!", context);
                }

            }
        });

        //try to read image
        bitmapFilename = getBitmapFilename();
        Log.d(TAG, "OnCreate done.");
    }

    String getBitmapFilename(){
        Log.d(TAG, "load Bitmap filename");
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String bitmapfilename = sharedPref.getString(CONSTANTS.PrefBitmapfilename, "");
        String _pdfFilename = sharedPref.getString(CONSTANTS.PrefPDFfilename, "");
        if(bitmapfilename!=null && bitmapfilename.length()>0){
            if(new File(bitmapfilename).exists())
                imageView.setImageBitmap(BitmapFactory.decodeFile(bitmapfilename));
        }
        if(_pdfFilename!="") {
            if(new File(_pdfFilename).exists()) {
                pdfFilename = _pdfFilename;
                txtPDFname.setText(pdfFilename);
            }
        }
        return bitmapfilename;
    }

    void saveDataForPrint(String sBitmap, String sPDF){
        // Create object of SharedPreferences.
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        //now get Editor
        SharedPreferences.Editor editor = sharedPref.edit();
        //put your value
        editor.putString(CONSTANTS.PrefBitmapfilename, sBitmap);
        editor.putString(CONSTANTS.PrefPDFfilename, sBitmap);
        //commits your edits
        editor.commit();
    }

    void setProgressPercent(int percent){
        Toast.makeText(context, "Printing done: "+percent, Toast.LENGTH_SHORT);
    }

    // usage: new printBitmapFiles().execute(string, int, long);
    //                                      <Parameters, Progress, Result>
    class printBitmapFiles extends AsyncTask<String , Integer, Long > {
        private final ProgressDialog dialog = new ProgressDialog(PrintPDFmenReplacement.this);

        @Override
        protected void onPreExecute() {
            _printBitmapTask=this;
            Log.d(TAG, "_printBitmapTask=this;");
            dialog.setTitle("Printing PDF Bitmap");
            dialog.setMessage("Please wait");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Long doInBackground(String... strings){ //Parameters
            long count=strings.length;
            publishProgress((int)count);
            String sFileBitmap=strings[0];
            CPCLSample3 sample = new CPCLSample3();
            try {
                Log.d(TAG, "AsyncTask starting printBitmap");
                sample.image3(1, sFileBitmap);
                Log.d(TAG, "AsyncTask printBitmap DONE");
            }catch (IOException ex){
                Log.d(TAG, "doInBackground: sample.image3 filed for "+sFileBitmap);
            }
            return count;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            setProgressPercent(progress[0]);
        }

        @Override
        protected void onPostExecute(Long result) {
            if(dialog.isShowing())
                dialog.dismiss();
            Log.d(TAG, "_printBitmapTask=null;");
            _printBitmapTask=null;
        }
    }

    private void openFile(int  CODE) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, CODE);
    }

    //called from system if file choosen
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode!=RESULT_OK)
            return;
        if(requestCode==FILE_SELECT_PDF) {
            Uri Fpath = data.getData();
            String sFilePDF=Fpath.getPath();
            Log.d(TAG,"Open File return with: "+sFilePDF);
            //saveDataForPrint(Fpath.getPath().toString());
            File file=new File(sFilePDF);
            if(file.exists()) {
                txtPDFname.setText(sFilePDF);
                // do somthing...
                new loadAndRenderPdf().execute(sFilePDF);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    class loadAndRenderPdf extends AsyncTask<String , Integer, Long > {
        private final ProgressDialog dialog = new ProgressDialog(PrintPDFmenReplacement.this);
        Bitmap bitmap=null;
        Context _context;
        @Override
        protected void onPreExecute() {
            _context=PrintPDFmenReplacement.this;
            _renderBitmapTask=this;
            Log.d(TAG, "loading image");
            dialog.setTitle("Render PDF Bitmap");
            dialog.setMessage("Please wait");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Long doInBackground(String... strings){ //Parameters
            long count=strings.length;
            publishProgress((int)count);
            try {
                File bitmapfile = File.createTempFile("bitmap", ".bmp", _context.getCacheDir());
                bitmapFilename = bitmapfile.getPath();
                Log.d(TAG, "using bitmapfile: " + bitmapFilename);
                String sPdfFilename = strings[0];
                PDFprint pdFprint = new PDFprint(_context);
                bitmap = pdFprint.renderFile(sPdfFilename, 3.0f, bitmapFilename);
                saveDataForPrint(bitmapFilename, sPdfFilename);
            }catch(IOException ex){
                count=0;
            }
            return count;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            setProgressPercent(progress[0]);
        }

        @Override
        protected void onPostExecute(Long result) {
            if(dialog.isShowing())
                dialog.dismiss();
            Log.d(TAG, "_renderBitmapTask=null;");
            if(bitmap!=null)
                imageView.setImageBitmap(bitmap);
            _renderBitmapTask=null;
        }
    }

}
