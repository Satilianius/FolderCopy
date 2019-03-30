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
    //TODO: logs
    //TODO: comments
    public void run() {
        log("Now processing " + sourceFile.getName());
        InputStream in = getInputStream(sourceFile);
        if (in == null){
            log("File " + sourceFile.getName() + " was not copied");
            return;
        }

        OutputStream out = getOutputStream(destinationFolder);
        if (out == null) {
            log("File " + sourceFile.getName() + " was not copied");
            return;
        }

        if (copyFile(in, out)){
            log("File " + sourceFile.getName() + " was successfully copied");
            parentActivity.update();
        }
        else{
            log("File " + sourceFile.getName() + " was not copied");
        }
    }

    private InputStream getInputStream(File sourceFile) {
        try {
            return new FileInputStream(sourceFile);
        }
        catch (FileNotFoundException e){
            log("File " + sourceFile.getAbsolutePath() + " not found");
            return null;
        }
    }

    private OutputStream getOutputStream(File destinationFolder) {
        if (!destinationFolder.isDirectory()) {
            log("Destination folder \"" +
                    destinationFolder.getAbsolutePath() + "\" does not exist or is not a directory");
            return null;
        }

        log("Creating destination file " + sourceFile.getName());
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
            log("Could not create destination file");
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
            buffer = null;
            return true;
        }
        catch(IOException e){
            log("An IOException occurs while trying to copy the file: " + sourceFile.getName());
            return false;
        }
        // new
        // Will return true in any case after this block, assuming that the file was copied.
        finally {
            try {
                in.close();
                out.close();
            }
            catch (IOException e){
                log("IO exception happened while closing fileStreams");
            }
        }
    }

    private void log(final String message){
        String threadTag = Thread.currentThread().getName() + ": ";
        parentActivity.runOnUiThread(() -> parentActivity.log(threadTag + message));
    }


}
