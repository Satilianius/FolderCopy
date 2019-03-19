package com.example.foldercopy;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    Button CopyBtn;


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
        //String source = ((TextView)findViewById(R.id.SourcePathTV)).getText().toString();
        if (ContextCompat.checkSelfPermission(this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                //request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
        } else {
            // Permission has already been granted
            System.out.println("Permission has already been granted");
        }

        File parentFolder = Environment.getExternalStorageDirectory();
        System.out.println("Parent folder abs path: " + parentFolder.getAbsolutePath());
        ((TextView)findViewById(R.id.SourcePathTV)).setText(parentFolder.getAbsolutePath());

        String sourceFolderPath = parentFolder + File.separator + "SourceFolder";
        System.out.println("source folder abs path: " + sourceFolderPath);

        File sourceFolder = new File(parentFolder + File.separator + "SourceFolder");
        if (sourceFolder.exists()) {
            Toast.makeText(getApplicationContext(), "exists", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getApplicationContext(), "Creating", Toast.LENGTH_LONG).show();
            System.out.println("Folder " + sourceFolder.getAbsolutePath() + " doesn't exist.");
            if (sourceFolder.mkdirs()){
                System.out.println("Folder " + sourceFolder.getAbsolutePath() + " created.");
                Toast.makeText(getApplicationContext(), "Created", Toast.LENGTH_LONG).show();
            }
            else{
                System.out.println("Folder " + sourceFolder.getAbsolutePath() + " was not created.");
            }

        }
//        String destination = findViewById("DestinationPathTV").text;
//        FileCopier fc = new FileCopier(new File(), new File());
//        fc.run();

//        if (!checkPermissions()) {
//            //ask for permissions
//            //if (!checkPermissions){
//            //error
//            //}
//        }
//        if (!checkAccess(source)){
//            error;
//        }
//        if (!checkAccess(destination)){
//            error
//        }
//        List<String> filesToCopy = new ArrayList<String>();
//        fillListOfFiles(source, filesToCopy);
//        Executor exec = new ThreadPoolExecutor();
//        for (int threadNumber = 1; threadNumber <= 2; threadNumber++) {
//            exec.execute(new FileCopier(source, destination));
//        }
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
