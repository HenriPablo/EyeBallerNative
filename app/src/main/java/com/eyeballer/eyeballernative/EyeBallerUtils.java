package com.eyeballer.eyeballernative;

/**
 * Created by tomekpilot on 8/15/16.
 */
public class EyeBallerUtils {

    private String strMinutes = "";
    private String strSeconds = "";
    private String strMillisecondsRemaining = "";


    public String getFormattedTimeRemaining( long timeInMilliseconds ){

        strMinutes =  Long.toString( (timeInMilliseconds/(1000*60))%60 );

        if( strMinutes.length() < 2){
            strMinutes = "0" + strMinutes;
        }

        strSeconds = Long.toString((timeInMilliseconds/1000)%60);
        if(  strSeconds.length() < 2 ){
            strSeconds = "0" + strSeconds;
        }

        strMillisecondsRemaining = Long.toString( (timeInMilliseconds%1000)/100 );
        if(strMillisecondsRemaining.length() < 2 ){
            strMillisecondsRemaining = "0" + strMillisecondsRemaining;
        }

        return  (strMinutes + ":" + strSeconds + ":" + strMillisecondsRemaining);
    }



}
