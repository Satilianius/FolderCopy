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

/**
 * Copies files from one user defined folder to another asynchronously using ThreadPoolExecutor
 */
public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    /** Text fields where user inputs path to the folders. */
    EditText sourcePathET;
    EditText destinationPathET;
    /** Contain path to the source and destination folder inputted by user. */
    File sourceFolder;
    File destinationFolder;
    /** Starts copying process. */
    Button copyBtn;
    /** Shows in real time how many files were copied from the last button press. */
    TextView filesCopiedTV;
    ProgressBar progressBar;
    /** Shows the process of copying and any errors occurred to the user. */
    TextView logTV;
    /**Contains the number of files copied from the last button press.
     * Shared resource, encapsulated with update() synchronised method,
     * which is accessible from other threads.*/
    private int filesCopied;

    /**
     *  Initiates fields.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // From these text fields source and destination folder's paths will be taken
        // after the copy button press.
        sourcePathET = findViewById(R.id.sourcePathET);
        sourcePathET.setText(getResources().getString(R.string.source_path));

        destinationPathET = findViewById(R.id.destinationPathET);
        destinationPathET.setText(getResources().getString(R.string.destination_path));

        logTV = findViewById(R.id.logTV);
        //TODO: new ScrollingMovementMethod()
        logTV.setMovementMethod(new ScrollingMovementMethod());

        filesCopied = 0;

        filesCopiedTV = findViewById(R.id.filesCopiedTV);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(0);

        copyBtn = findViewById(R.id.CopyBtn);
        copyBtn.setOnClickListener((view) -> copyBtnPress());
    }

    /**
     * Copies he files from the Source folder to the Destination folder.
     */
    protected void copyBtnPress(){
        // Clears data which shows the progress.
        progressBar.setProgress(0);
        filesCopied = 0;
        filesCopiedTV.setText("0");
        // Gets user inputted folders.
        sourceFolder = new File(sourcePathET.getText().toString());
        destinationFolder = new File(destinationPathET.getText().toString());

        log("Checking permission");
        if (!permissionIsGiven()){
            log("The necessary permission is not given. Please, grant the application access to your storage and try again.");
            requestPermission();
            return;
        }

        // Checks if folders exist and are folders, not files.
        // Function will log any errors occurred from inside.
        log("Checking folders");
        if (!checkFolders()) return;

        log("Getting the list of files in the \"" + sourceFolder.getName() + "\" folder");
        File[] filesToCopy =  sourceFolder.listFiles();
        // Checks if there are any files in the source folder.
        if (!checkFiles(filesToCopy)) return;

        progressBar.setMax(filesToCopy.length);
        // Prevents button to be clicked while copying is on progress.
        copyBtn.setEnabled(false);

        log("Copying files");
        copyFiles(filesToCopy, destinationFolder);
        copyBtn.setEnabled(true);
    }

    /**
     * Checks if the user inputted folders are correct.
     * Assumes that fields were instantiated before and are not null;
     * @return true if source and destination folders exist and are directories.
     */
    private boolean checkFolders() {
        if (!sourceFolder.isDirectory()) {
            log("Source folder \"" + sourceFolder.getAbsolutePath() +
                    "\" does not exists or is not a directory." +
                    " Please choose another folder and try again");
            return false;
        }

        if (!destinationFolder.isDirectory()) {
            log("Destination folder \"" + destinationFolder.getAbsolutePath() +
                    "\" does not exists or is not a directory." +
                    " Please choose another folder and try again");
            return false;
        }
        return true;
    }

    /**
     * Checks if files were successfully obtained from the Source folder.
     * @param filesToCopy list of files, which was obtained from the folder.
     * @return true if at least one file was successfully obtained from the source folder.
     */
    private boolean checkFiles(File[] filesToCopy) {
        if (filesToCopy == null){
            log("I/O Error occurred while getting the list of files in "
                    + sourceFolder.getAbsolutePath());
            return false;
        }
        if(filesToCopy.length == 0) {
            log("No files found in \"" + sourceFolder.getAbsolutePath() + "\"");
            return false;
        }
        return true;
    }

    /**
     * Copies the files from the array to to the destination folder.
     * Assumes that files in the array are exist.
     *
     * @param filesToCopy array of files to be copied.
     * @param destinationFolder folder to which files have to be copied.
     */
    private void copyFiles(File[] filesToCopy, File destinationFolder) {
        // Creates a thread pool executor to perform the task concurrently.
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        // Create anonymous runnable objects for each file to be copied and executes it.
        for (File file : filesToCopy) {
            executor.execute(new FileCopier(file, destinationFolder, this));
        }
        executor.shutdown();
        executor = null;
        // At this point no more references to the anonymous FileCopiers exist,
        // so they can be garbage collected.
    }

    /**
     * Requests access to Storage.
     */
    private void requestPermission() {
        //TODO new anonymous string[]
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);
    }

    /**
     * Checks if the Application has an access to Storage.
     * @return true if the permission is given.
     */
    private boolean permissionIsGiven() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
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

    //TODO: protection from other thread calls?
    /**
     * Logs the passed message in the Log TextView for the user and in the Log.i(),
     * for simplicity does not distinguish between error, warning and other type of messages.
     * Can be accessed from other threads only via runOnUiThread().
     * Not synchronized since runOnUiThread() doesn't immediately start method but uses queue.
     *
     * @param message massage to be logged.
     */
    public void log(String message){
         {
            Log.i("MainActivity", message);
            logTV.append(message + "\n");
        }
    }

    /**
     * Updates progress bar and increases the number of files copied by one.
     * Should be called from other threads every time when a file copied.
     *
     */
    public synchronized void update(){
        {
            filesCopied++;
            progressBar.setProgress(filesCopied);
            filesCopiedTV.setText(String.valueOf(filesCopied));
        }
    }
}
