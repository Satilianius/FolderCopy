package com.example.foldercopy;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    Button CopyBtn;
    public volatile int filesCopied = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CopyBtn = findViewById(R.id.CopyBtn);
        CopyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyBtnPress(view);
            }
        });
    }

    protected void copyBtnPress(View view){
        log("Checking permission.");
        //TODO: rewrite with onRequest
        if (!permissionIsGiven()){
            log("Permission is not given, requesting permission.");
            requestPermission();
            if (!permissionIsGiven()){
                log("Necessary permission is not provided. Unable to proceed.");
                return;
            }
        }
        log("Permission is given.");

        File sourceFolder = new File(Environment.getExternalStorageDirectory() + "/CW/Source");
        File destinationFolder = new File(Environment.getExternalStorageDirectory() + "/CW/Destination");

        if (!createFolderIfNotExist(sourceFolder) ||  !createFolderIfNotExist(destinationFolder)){
            log("Unable to proceed,");
            return;
        }

        try{
            new AsyncCopy(sourceFolder, destinationFolder).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        catch (IllegalArgumentException e){
            log("Error occurred: " + e.getMessage());
        }


//        log("Getting the list of files in the \"" + sourceFolder.getName() + "\" folder.");
//        File[] filesToCopy =  sourceFolder.listFiles();
//        filesCopied = filesToCopy.length;


//        if(filesToCopy.length > 0){
//            for (File file : filesToCopy){
//                log("Processing file " + file.getName());
//                FileCopier fc = new FileCopier(file, destinationFolder);
//                fc.run();
//            }
//        }
//        else{
//            log("No files in the \"" + sourceFolder.getAbsolutePath() + "\" folder.");
//        }

//        List<String> filesToCopy = new ArrayList<String>();
//        fillListOfFiles(source, filesToCopy);
//        Executor exec = new ThreadPoolExecutor();
//        for (int threadNumber = 1; threadNumber <= 2; threadNumber++) {
//            exec.execute(new FileCopier(source, destination));
//        }
    }

    /**
     *
     * @param folder folder to create
     * @return true if folder already exists or was created, false if attempt of creation failed.
     */
    private boolean createFolderIfNotExist(File folder) {
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

    private boolean permissionIsGiven() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
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

    public void log(String message){
        TextView logger = findViewById(R.id.LogTV);
        logger.append(message + "\n");

    }
//    private void fillListOfFiles (String source, List<String> list){
//        //add all the files to the list
//    }
//    private boolean checkPermissions(){
//
//        return true;//check
//    }
//    private boolean checkAccess(String resource){
//
//    }
}
