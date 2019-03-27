package com.example.foldercopy;

import android.os.AsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AsyncCopy extends AsyncTask<Void, Void, Integer> {
    private File sourceFolder;
    private File destinationFolder;
    File[] filesToCopy;
    int filesCopied;

    /**
     * Constructor. Checks passed source and destination folders and sets the attributes.
     *
     * @param sourceFolder Folder files from which will be copied.
     * @param destinationFolder Folder to which files will be copied.
     */
    public AsyncCopy(File sourceFolder, File destinationFolder) {
        super();
        if (!sourceFolder.isDirectory()) {
            System.out.println("Source folder \"" + sourceFolder.getAbsolutePath() + "\" does not exist.");
            throw new IllegalArgumentException("Source folder \"" + sourceFolder.getAbsolutePath() + "\" does not exist.");
        }

        if (!destinationFolder.isDirectory()) {
            System.out.println("Destination: \"" + destinationFolder.getAbsolutePath() + "\" does not exist.");
            throw new IllegalArgumentException("Destination: \"" + destinationFolder.getAbsolutePath() + "\" does not exist.");
        }

        this.sourceFolder = sourceFolder;
        this.destinationFolder = destinationFolder;
    }

    /**
     * Gets the list of files in the source folder.
     */
    @Override
    protected void onPreExecute(){
        System.out.println(Thread.currentThread().getName() + ": Getting the list of files in the \"" + sourceFolder.getName() + "\" folder.");
        filesToCopy = sourceFolder.listFiles();
        if (filesToCopy == null){
            System.out.println("IO error occurred while trying to get the list of files in " + sourceFolder.getName());
            cancel(true);
        }
        else if(filesToCopy.length == 0){
            System.out.println("No files to copy in: " + sourceFolder.getName());
        }
        filesCopied = 0;
    }

    /**
     * Copies files.
     *
     * @param voids
     * @return
     */
    @Override
    protected Integer doInBackground(Void... voids) {
        System.out.println(Thread.currentThread().getName() + " started doInBackground");
        if (isCancelled()){
            return filesCopied;
        }
        int count = filesToCopy.length;
        for (int i = 0; i < count; i++){
            File file = filesToCopy[i];
            InputStream in = getInputStream(file);
            if (in == null){
                System.out.println("Cannot create Input Stream. File " + file.getName() + " was not copied.");
                continue;
            }

            OutputStream out = getOutputStream(destinationFolder, file.getName());
            if (out == null) {
                System.out.println("Cannot create Output Stream. File " + file.getName() + " was not copied.");
                continue;
            }

            if (copyFile(in, out)){
                System.out.println(Thread.currentThread().getName() + ": File " + file.getName() + " was successfully copied.");
                publishProgress();
                // Escape early if cancel() is called
                if (isCancelled()) break;
            }
            else{
                System.out.println("An error occurred. File " + file.getName() + " was not copied.");
            }
        }
        return filesCopied;
    }

    /**
     * Returns InputStream of a file.
     * @param sourceFile file to be used as an input.
     * @return InputStream if file is found, null if exception occurred.
     */
    private InputStream getInputStream(File sourceFile) {
        try {
            return new FileInputStream(sourceFile);
        }
        catch (FileNotFoundException e){
            System.out.println("File " + sourceFile.getAbsolutePath() + " not found.");// If file not found just do nothing.
            return null;
        }
    }

    /**
     * Creates a file in passed folder and returns the Output stream of this file.
     *
     * @param destinationFolder folder where file should be created
     * @return OutputStream of created file, null if exception occurred
     */
    private OutputStream getOutputStream(File destinationFolder, String fileName) {

        System.out.println(Thread.currentThread().getName() + ": Creating destination file");
        File destinationFile = new  File(destinationFolder + "/" + fileName);
        try{
            destinationFile.createNewFile();
            return new FileOutputStream(destinationFile);
//            if (destinationFile.createNewFile()) {
//                return new FileOutputStream(destinationFile);
//            }
//            else{
//                System.out.println("Destination folder already has " + fileName);
//                //TODO: change name, rewrite, cancel
//                //TODO new close
//                return null;
//            }
        }
        catch(IOException e){
            System.out.println("Cannot create destination file.");
            return null;
        }
    }

    private boolean copyFile(InputStream in, OutputStream out) {
        System.out.println(Thread.currentThread().getName() + " started copying file");
        try {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }

            in.close();
            out.close();

            return true;
        } catch (IOException e) {
            System.out.println("An IOException occurs while trying to copy the file");
            return false;
        }
    }

    @Override
    protected void onProgressUpdate(Void... voids) {
        System.out.println(Thread.currentThread().getName() + " says that it finished copying something!");
        filesCopied++;
    }
}
