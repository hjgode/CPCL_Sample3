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
import android.os.Environment;
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
import com.mobile.app.assist.Options;
import com.mobile.app.assist.PDFprint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by E841719 on 28.11.2017.
 */

public class PrintPDFmenReplacement extends Activity {
    String bitmapFilename = null;
    String pdfFilename = null;

    static final String TAG = "PrintPDFmenReplacement";
    static final int FILE_SELECT_PDF = 1;

    Context context;
    Object _printBitmapTask = null;
    Object _renderBitmapTask = null;

    Button btnLoadPdfFile;
    TextView txtPDFname;
    Button btnPrintPDF;
    ImageView imageView;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("PrintPDFmenReplacement", "OnDestroy");
        saveDataForPrint("", ""); //clear known data
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.print_pdf_menu_replacement);

        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    showImage(getBitmapFilename());
                }catch(Exception ex){}
            }
        });
        context = this;

        btnLoadPdfFile = (Button) findViewById(R.id.btnLoadPDF);
        btnLoadPdfFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFile(FILE_SELECT_PDF);
            }
        });
        txtPDFname = (TextView) findViewById(R.id.txtPDFname);
        btnPrintPDF = (Button) findViewById(R.id.btnPrintPDF);
        btnPrintPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (_printBitmapTask != null) {
                    Toast.makeText(context, "Already printing, please wait", Toast.LENGTH_LONG);
                }
                bitmapFilename = getBitmapFilename();
                if (bitmapFilename != null && bitmapFilename.length() > 0) {
                    if (_printBitmapTask == null) {
                        new PrintPDFmenReplacement.printBitmapFiles().execute(bitmapFilename);
                    } else {
                        Toast.makeText(context, "Already printing, please wait", Toast.LENGTH_LONG);
                    }
                    //sample.image3(count, bitmapFilename);
                } else {
                    AlertView.showAlert("Error with Bitmapfile!", context);
                }

            }
        });

        //try to read image
        bitmapFilename = getBitmapFilename();
        Log.d(TAG, "OnCreate done.");
    }

    String getBitmapFilename() {
        Log.d(TAG, "load Bitmap filename");
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String bitmapfilename = sharedPref.getString(CONSTANTS.PrefBitmapfilename, "");
        String _pdfFilename = sharedPref.getString(CONSTANTS.PrefPDFfilename, "");
        if (bitmapfilename != null && bitmapfilename.length() > 0) {
            if (new File(bitmapfilename).exists())
                imageView.setImageBitmap(BitmapFactory.decodeFile(bitmapfilename));
        }
        if (_pdfFilename != "") {
            if (new File(_pdfFilename).exists()) {
                pdfFilename = _pdfFilename;
                txtPDFname.setText(pdfFilename);
            }
        }
        return bitmapfilename;
    }

    void showImage(String source){
        String sDir="/";
        sDir = Environment.getExternalStorageDirectory()+File.separator+"myDirectory";
        //create folder
        File folder = new File(sDir); //folder name
        folder.mkdirs();
        File dir = new File(sDir);//your custom path,such as /mnt/sdcard/Pictures
        if(!dir.exists())
        {
            dir.mkdirs();
        }
        File f = new File(dir, "temporary_file.jpg");
        File target = copyImage(source, f.getAbsolutePath());
        //the following shows the gallery view with the image selectable
//        Uri uri = Uri.fromFile(target);
//        Intent intent = new Intent();
//        intent.setAction(Intent.ACTION_VIEW);
//        intent.setType("image/*");
//        intent.putExtra(Intent.EXTRA_STREAM,uri );
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent);
        //the following shows the image directly in Imager Viewer (of gallery)
        Intent galleryIntent = new Intent(Intent.ACTION_VIEW, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setDataAndType(Uri.fromFile(target), "image/*");
        galleryIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(galleryIntent);
    }

    File copyImage(String sourcePath, String targetPath){
        try {
            InputStream in = new FileInputStream(sourcePath);
            OutputStream out = new FileOutputStream(targetPath);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            return new File(targetPath);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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
                //use scale=0f for autoscale
                bitmap = pdFprint.renderFile(sPdfFilename, bitmapFilename);
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
