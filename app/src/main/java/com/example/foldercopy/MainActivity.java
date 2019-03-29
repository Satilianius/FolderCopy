package com.example.foldercopy;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    TextView logTV;
    TextView filesCopiedTV;
    ProgressBar progressBar;
    Button CopyBtn;
    private int filesCopied;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logTV = findViewById(R.id.logTV);
        logTV.setMovementMethod(new ScrollingMovementMethod());

        filesCopiedTV = findViewById(R.id.filesCopiedTV);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(0);

        filesCopied = 0;

        CopyBtn = findViewById(R.id.CopyBtn);
        //TODO: view?
        CopyBtn.setOnClickListener((view) -> copyBtnPress());
    }

    protected void copyBtnPress(){
        //TODO: separate methods
        progressBar.setProgress(0);
        filesCopiedTV.setText("0");

        log("Checking permission.");
        //TODO: rewrite with onRequest
        if (permissionNotGiven()){
            log("Permission is not given, requesting permission.");
            requestPermission();
            if (permissionNotGiven()){
                log("Necessary permission is not provided. Unable to proceed.");
                return;
            }
        }
        log("Permission is given.");

        // Creating source and destination folders if needed.
        File sourceFolder = new File(Environment.getExternalStorageDirectory() + "/CW/Source");
        File destinationFolder = new File(Environment.getExternalStorageDirectory() + "/CW/Destination");
        if (!createFolderIfNotExist(sourceFolder) ||  !createFolderIfNotExist(destinationFolder)){
            log("Unable to proceed,");
            return;
        }

        log("Getting the list of files in the \"" + sourceFolder.getName() + "\" folder.");
        File[] filesToCopy =  sourceFolder.listFiles();
        if (filesToCopy == null){
            log("I/O Error occurred while getting the list of files in " + sourceFolder.getAbsolutePath());
            return;
        }
        if(filesToCopy.length == 0) {
            log("No files found in " + sourceFolder.getAbsolutePath());
            return;
        }
        progressBar.setMax(filesToCopy.length);
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        for (File file : filesToCopy) {
            executor.execute(new FileCopier(file, destinationFolder, this));
        }
        executor.shutdown();
    }

    /**
     *
     * @param folder folder to create
     * @return true if folder already exists or was created, false if attempt of creation failed.
     */
    private boolean createFolderIfNotExist(File folder) {
        //TODO: check for isDirectory?
        log("Checking if folder \"" + folder.getAbsolutePath() + "\" exists.");
        if (folder.exists()) {
            log("Folder exists.");
            return true;
        }
        else{
            log("Folder does not exist, attempting tot create.");
            if (folder.mkdirs()){
                log("Folder " + folder.getAbsolutePath() + " created.");
                return true;
            }
            else{
                log("Failed to create folder \"" + folder.getAbsolutePath() + "\"");
                return false;
            }
        }
    }

    private void requestPermission() {
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
        logTV.append(message + "\n");
    }

    public synchronized void update(){
        filesCopied++;
        progressBar.setProgress(filesCopied);
        filesCopiedTV.setText(String.valueOf(filesCopied));
    }
}
