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
        try {
            InputStream in = new FileInputStream(sourceFile);
            try {
                OutputStream out = new FileOutputStream(destinationFolder);
                try {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0) {
                        out.write(buffer, 0, length);
                    }
                }
                catch(IOException e){
                    System.out.println("Cannot read or write the file: " + sourceFile.getName());
                }
                    finally {
                    try {
                        out.close();
                    }
                    catch (IOException e){
                        System.out.println("Cannot close the folder: " + destinationFolder.getName());
                    }

                }
            } finally {
                try {
                    in.close();
                }
                catch (IOException e){
                    System.out.println("Cannot close the file: " + sourceFile.getName());//popup cannot close the file
                }
            }

        }
        catch (FileNotFoundException e){
            System.out.println("File " + destinationFolder.getName() + " not found.");// If file not found just do nothing.

        }
    }
}
