package com.example.foldercopy;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    EditText sourcePathET;
    EditText destinationPathET;
    File sourceFolder;
    File destinationFolder;
    Button copyBtn;
    TextView filesCopiedTV;
    ProgressBar progressBar;
    TextView logTV;
    //Shared resource, access encapsulated with update() synchronised method.
    private int filesCopied;

    //TODO: open buttons
    //TODO: on resume?
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sourcePathET = findViewById(R.id.sourcePathET);
        sourcePathET.setText(getResources().getString(R.string.source_path));

        destinationPathET = findViewById(R.id.destinationPathET);
        destinationPathET.setText(getResources().getString(R.string.destination_path));

        logTV = findViewById(R.id.logTV);
        //TODO: new ScrollingMovementMethod()
        logTV.setMovementMethod(new ScrollingMovementMethod());

        filesCopiedTV = findViewById(R.id.filesCopiedTV);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(0);

        filesCopied = 0;

        copyBtn = findViewById(R.id.CopyBtn);
        //TODO: view?
        copyBtn.setOnClickListener((view) -> copyBtnPress());
    }

    protected void copyBtnPress(){
        //TODO: separate methods
        progressBar.setProgress(0);
        filesCopied = 0;
        filesCopiedTV.setText("0");
        sourceFolder = new File(sourcePathET.getText().toString());
        destinationFolder = new File(destinationPathET.getText().toString());

        //checkPermission();
        log("Checking permission");
        //TODO: rewrite with onRequest
        if (permissionNotGiven()){
            log("Permission is not given, requesting permission");
            requestPermission();
            if (permissionNotGiven()){
                log("Necessary permission is not provided. Unable to proceed");
                return;
            }
        }

        log("Checking folders");
        if (!checkFolders()) return;

        log("Getting the list of files in the \"" + sourceFolder.getName() + "\" folder");
        File[] filesToCopy =  sourceFolder.listFiles();

        log("Checking files");
        if (!checkFiles(filesToCopy)) return;

        progressBar.setMax(filesToCopy.length);
        copyBtn.setEnabled(false);

        log("Copying files");
        copyFiles(filesToCopy, destinationFolder);
        copyBtn.setEnabled(true);
    }

    private boolean checkFolders() {
        if (!sourceFolder.isDirectory()) {
            log("Source folder \"" + sourceFolder.getAbsolutePath() + "\" does not exists or is not a directory" +
                    " Please choose another folder and try again");
            return false;
        }

        if (!destinationFolder.isDirectory()) {
            log("Destination folder \"" + destinationFolder.getAbsolutePath() + "\" does not exists or is not a directory" +
                    " Please choose another folder and try again");
            return false;
        }
        return true;
    }

    private boolean checkFiles(File[] filesToCopy) {
        if (filesToCopy == null){
            log("I/O Error occurred while getting the list of files in " + sourceFolder.getAbsolutePath());
            return false;
        }
        if(filesToCopy.length == 0) {
            log("No files found in \"" + sourceFolder.getAbsolutePath() + "\"");
            return false;
        }
        return true;
    }

    private void copyFiles(File[] filesToCopy, File destinationFolder) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        for (File file : filesToCopy) {
            //TODO: memory new anonymous?
            executor.execute(new FileCopier(file, destinationFolder, this));
        }
        //TODO: wait for tasks to be completed?
        executor.shutdown();
        executor = null;
    }

    private void requestPermission() {
        //TODO new string[]
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);
    }

    private boolean permissionNotGiven() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case PERMISSION_REQUEST_CODE: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // permission was granted, yay! Do the
//                    // contacts-related task you need to do.
//                } else {
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                }
//                return;
//            }
//
//            // other 'case' lines to check for other
//            // permissions this app might request.
//        }
//    }

    public  void log(String message){
        Log.i("MainActivity", message);
        logTV.append(message + "\n");
    }

    public synchronized void update(){
        filesCopied++;
        progressBar.setProgress(filesCopied);
        filesCopiedTV.setText(String.valueOf(filesCopied));
    }
}
