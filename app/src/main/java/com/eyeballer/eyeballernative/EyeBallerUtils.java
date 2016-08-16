package com.eyeballer.eyeballernative;

/**
 * Created by tomekpilot on 8/15/16.
 */
public class EyeBallerUtils {

    public String getFormattedTimeRemaining( long timeInMilliseconds ){

        String strMinutes =  "" + (timeInMilliseconds/(1000*60))%60;
        if( ((timeInMilliseconds/(1000*60))%60) < 10 ){
            strMinutes = "0" + strMinutes;
        }

        String strSeconds = "" + (timeInMilliseconds/1000)%60;
        if(  ((timeInMilliseconds/1000)%60) < 10 ){
            strSeconds = "0" + strSeconds;
        }

        long millisecondsRemaining = (timeInMilliseconds%1000)/100;

        String strMillisecondsRemaining = "" + millisecondsRemaining;

        /*
        if( ("" + millisecondsRemaining).length() < 5 ){
            strMillisecondsRemaining = "0" + millisecondsRemaining;
        } else if(("" + millisecondsRemaining).length() < 4 ){
            strMillisecondsRemaining = "00" + millisecondsRemaining;
        } else if(("" + millisecondsRemaining).length() < 3 ){
            strMillisecondsRemaining = "000" + millisecondsRemaining;
        } else
        */
        if(("" + millisecondsRemaining).length() < 2 ){
            strMillisecondsRemaining = "0" + millisecondsRemaining;
        }

        return  (strMinutes + ":" + strSeconds + ":" + strMillisecondsRemaining);

    }



}
