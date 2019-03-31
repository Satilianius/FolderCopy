package com.example.foldercopy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Runnable class which copies one passed file into the destination folder.
 */
public class FileCopier implements Runnable {

    private File sourceFile;
    private File destinationFolder;
    /** Allows to notify the called Activity about copying process. */
    private MainActivity parentActivity;

    /**
     * Constructor, sets up fields.
     * @param sourceFile file to be copied.
     * @param destinationFolder destination folder.
     * @param activity activity which will receive updates.
     */
    FileCopier(File sourceFile, File destinationFolder, MainActivity activity) {
        this.sourceFile = sourceFile;
        this.destinationFolder = destinationFolder;
        this.parentActivity = activity;
    }

    /**
     * Copies the file and notifies the parent activity if the file was successfully created.
     */
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

        // Streams are closed inside copyFile() method.
        if (copyFile(in, out)){
            log("File " + sourceFile.getName() + " was successfully copied");
            // Notify activity that the file was successfully copied.
            parentActivity.update();
        }
        else{
            log("File " + sourceFile.getName() + " was not copied");
        }
    }

    /**
     * Creates a FileInputStream from a file.
     * @param sourceFile file which stream is to be created
     * @return input stream created from the file. Null if an exception occurred.
     */
    private InputStream getInputStream(File sourceFile) {
        try {
            return new FileInputStream(sourceFile);
        }
        catch (FileNotFoundException e){
            log("File " + sourceFile.getAbsolutePath() + " not found");
            return null;
        }
    }

    /**
     * Creates a file in the destination folder and returns its FileOutputStream.
     * @param destinationFolder folder in which output file is to be created
     * @return output stream of the created file. Null if an exception occurred.
     */
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
            FileOutputStream out = new FileOutputStream(destinationFile);
            // Releasing the memory allocated for the file.
            destinationFile = null;
            return out;
        }
        catch(IOException e){
            log("Could not create destination file");
            return null;
        }
    }

    /**
     * Copies the file from input stream to output stream.
     * @param in Input stream
     * @param out Output stream.
     * @return true if stream was successfully copied.
     */
    private boolean copyFile(InputStream in, OutputStream out) {
        try {
            // Using byte buffer for better than byte-by-byte copying performance.
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            // Releasing memory allocated for the buffer.
            buffer = null;
            return true;
        }
        catch(IOException e){
            log("An IOException occurs while trying to copy the file: " + sourceFile.getName());
            return false;
        }
        // Will return true in any case after this block, assuming that the file was copied.
        // Releases the memory taken for Streams.
        finally {
            try {
                in.close();
                in = null;
                out.close();
                out = null;
            }
            catch (IOException e){
                log("IO exception happened while closing fileStreams");
            }
        }
    }

    /**
     * Sends message to the parent activity to be logged.
     * Adds the name of the current thread to each message to show concurrency.
     * @param message message to be logged.
     */
    private void log(final String message){
        String threadTag = Thread.currentThread().getName() + ": ";
        parentActivity.runOnUiThread(() -> parentActivity.log(threadTag + message));
    }
}
