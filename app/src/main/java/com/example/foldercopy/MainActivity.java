package com.example.foldercopy;

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

    Button CopyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CopyBtn = findViewById(R.id.CopyBtn);
        CopyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyBtnPress();
            }
        });
    }

    protected void copyBtnPress(){
        String source = ((TextView)findViewById(R.id.SourcePathTV)).getText().toString();
        Toast.makeText(getApplicationContext(), source, Toast.LENGTH_LONG).show();//display the text on button press
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
