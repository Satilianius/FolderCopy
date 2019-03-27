package com.example.foldercopy;

import android.widget.TextView;

public class Logger {
    private Logger LOGGER;
    private static TextView out;

    private Logger(TextView tw){
        out = tw;
    }
    public Logger getLogger(TextView tw){
        if (LOGGER == null){
            LOGGER = new Logger(tw);
            return LOGGER;
        }
        return LOGGER;
    }
    public void log(String message){

    }
}
