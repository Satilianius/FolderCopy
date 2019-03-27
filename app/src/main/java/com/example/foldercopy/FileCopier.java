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

    public FileCopier(File sourceFile, File destinationFolder) {
        this.sourceFile = sourceFile;
        this.destinationFolder = destinationFolder;
    }

    public void run() {
        InputStream in = getInputStream(sourceFile);
        if (in == null){
            System.out.println("File " + sourceFile.getName() + " was not copied.");
            return;
        }

        OutputStream out = getOutputStream(destinationFolder);
        if (out == null) {
            System.out.println("File " + sourceFile.getName() + " was not copied.");
            return;
        }

        if (copyFile(in, out)){
            System.out.println("File " + sourceFile.getName() + " was successfully copied.");
        }
        else{
            System.out.println("File " + sourceFile.getName() + " was not copied.");
        }
    }

    private InputStream getInputStream(File sourceFile) {
        try {
            return new FileInputStream(sourceFile);
        }
        catch (FileNotFoundException e){
            System.out.println("File " + sourceFile.getAbsolutePath() + " not found.");// If file not found just do nothing.
            return null;
        }
    }

    private OutputStream getOutputStream(File destinationFolder) {
        if (!destinationFolder.isDirectory()) {
            System.out.println("Destination: \"" + destinationFolder.getAbsolutePath() + "\" is not a directory.");
            return null;
        }
        if (!destinationFolder.exists()){
            System.out.println("Folder \"" + destinationFolder.getAbsolutePath() + "\" not found.");
            return null;
        }

        System.out.println("Creating destination file");
        File destinationFile = new  File(destinationFolder + "/" + sourceFile.getName());
        try{
            if (destinationFile.createNewFile()) {
                return new FileOutputStream(destinationFile);
            }
            else{
                System.out.println("Destination folder already has " + sourceFile.getName());
                //TODO: change name, rewrite, cancel
                return null;
            }
        }
        catch(IOException e){
            System.out.println("Cannot create destination file.");
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
            System.out.println("An IOException occurs while trying to copy the file: " + sourceFile.getName());
            return false;
        }

        // Will return true in any case after this block, assuming that the file was copied.
        finally {
            try {
                out.close();
            }
            catch (IOException e){
                System.out.println("Cannot close the copied file");
            }

            try {
                in.close();
            }
            catch (IOException e){
                System.out.println("Cannot close the original file: " + sourceFile.getName());
            }

        }
    }


}
