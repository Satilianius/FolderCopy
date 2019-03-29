package com.example.foldercopy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileCopier implements Runnable {
    private File sourceFile;
    private File destinationFolder;
    private MainActivity parentActivity;

    FileCopier(File sourceFile, File destinationFolder, MainActivity activity) {
        this.sourceFile = sourceFile;
        this.destinationFolder = destinationFolder;
        this.parentActivity = activity;
    }

    public void run() {
        log("Hello from " + Thread.currentThread().getName() + ". I am processing " + sourceFile.getName());
        InputStream in = getInputStream(sourceFile);
        if (in == null){
            log("File " + sourceFile.getName() + " was not copied.");
            return;
        }

        OutputStream out = getOutputStream(destinationFolder);
        if (out == null) {
            log("File " + sourceFile.getName() + " was not copied.");
            return;
        }

        if (copyFile(in, out)){
            log(Thread.currentThread().getName() + ": File " + sourceFile.getName() + " was successfully copied.");
            parentActivity.update();
        }
        else{
            log("File " + sourceFile.getName() + " was not copied.");
        }
    }

    private InputStream getInputStream(File sourceFile) {
        try {
            return new FileInputStream(sourceFile);
        }
        catch (FileNotFoundException e){
            log("File " + sourceFile.getAbsolutePath() + " not found.");// If file not found just do nothing.
            return null;
        }
    }

    private OutputStream getOutputStream(File destinationFolder) {
        if (!destinationFolder.isDirectory()) {
            log("Destination: \"" + destinationFolder.getAbsolutePath() + "\" is not a directory.");
            return null;
        }
        if (!destinationFolder.exists()){
            log("Folder \"" + destinationFolder.getAbsolutePath() + "\" not found.");
            return null;
        }

        log(Thread.currentThread().getName() + ": Creating destination file");
        File destinationFile = new  File(destinationFolder + "/" + sourceFile.getName());

        try{
            if (!destinationFile.exists()){
                if (!destinationFile.createNewFile()){
                    return null;
                }
            }
            return new FileOutputStream(destinationFile);
        }
        catch(IOException e){
            log("Cannot create destination file.");
            return null;
        }

    }

    private boolean copyFile(InputStream in, OutputStream out) {
        try {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            return true;
        }
        catch(IOException e){
            log("An IOException occurs while trying to copy the file: " + sourceFile.getName());
            return false;
        }

        // Will return true in any case after this block, assuming that the file was copied.
        finally {
            try {
                out.close();
            }
            catch (IOException e){
                log("Cannot close the copied file");
            }

            try {
                in.close();
            }
            catch (IOException e){
                log("Cannot close the original file: " + sourceFile.getName());
            }

        }
    }

    private void log(final String message){
        parentActivity.runOnUiThread(() -> parentActivity.log(message));
    }


}
