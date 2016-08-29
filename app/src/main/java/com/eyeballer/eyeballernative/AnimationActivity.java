package com.eyeballer.eyeballernative;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AnimationActivity extends AppCompatActivity {

    ImageView img;
    ObjectAnimator animX;
    boolean animClickPaused = false;
    boolean animXRunning = false;

    EyeBallerUtils eyeBallerUtils;

    int animationSpeed = 700;
    int repeatCount = 99;   /* odd number of repeats will return the ball to its original position */
    int totalTime = 0;

    int animationRunning = 0;
    int sputnikLeftRightFlag = 0; /* alternate between 0 and 1 - play left or right sound */
    MediaPlayer sputnikLeft;
    MediaPlayer sputnikRight;
    int sputnikOn = 0;
    boolean sputnikClickPaused = false;

    ImageButton sputnikBtn;
    ImageButton playPauseBtn;
    TextView countRemaining;
    TextView timeRemaining;

    /* preferences */
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
         * Has to be BEFORE setContentView
         * http://stackoverflow.com/questions/12204336/getwindow-addflagswindowmanager-layoutparams-flag-keep-screen-on-no-response
         *
         */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        eyeBallerUtils = new EyeBallerUtils();

        /* preferences */
        Resources res = getResources();
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        animationSpeed  = sharedPref.getInt("prefSpeed", res.getInteger( R.integer.animation_speed ));
        repeatCount     = sharedPref.getInt("prefCount", res.getInteger( R.integer.repeat_count ));

        setContentView(R.layout.activity_animation);
        ImageView img = (ImageView) findViewById(R.id.ballView);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        playPauseBtn = (ImageButton) findViewById( R.id.playPauseImageBtn);
        sputnikBtn = (ImageButton) findViewById( R.id.sputnikBtn );
        /* restore animation, sound and UI related data after screen rotation */
        if( savedInstanceState != null){
            sputnikOn = savedInstanceState.getInt("sputnikOn", sputnikOn);
            if( sputnikOn == 1){
                sputnikBtn.setImageResource( R.drawable.ic_volume_up_black_24dp);
            }
            animClickPaused = savedInstanceState.getBoolean( "animClickPaused", animClickPaused );
            sputnikClickPaused = savedInstanceState.getBoolean( "sputnikClickPaused", sputnikClickPaused );
            animXRunning = savedInstanceState.getBoolean( "animXRunning", animXRunning );
            totalTime = savedInstanceState.getInt( "totalTime", totalTime );
            repeatCount = savedInstanceState.getInt( "repeatCount", repeatCount );
            animationSpeed = savedInstanceState.getInt( "animationSpeed", animationSpeed );
        }


        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics( metrics );
        float w = metrics.widthPixels;
        animX = ObjectAnimator.ofFloat(img, "x", (w - (48) )  );
        animX.setRepeatCount(repeatCount);
        animX.setRepeatMode( 2 );
        animX.setDuration( animationSpeed );

        totalTime = repeatCount * animationSpeed;

        if( animXRunning ){
            playPauseBtn.setImageResource( R.drawable.ic_pause_circle_filled_black_24dp);
            animationRunning = 1;
            animX.start();
        }

        this.setVolumeControlStream(AudioManager.STREAM_SYSTEM);
        sputnikLeft = MediaPlayer.create(this, R.raw.sputnik_left);
        sputnikRight = MediaPlayer.create(this, R.raw.sputnik_right);


        /* remaining count display */
        countRemaining = (TextView) findViewById(R.id.textViewCount);
        countRemaining.setText( countRemaining.getText() + " " + repeatCount );

        /* remaining time dispaly */
        timeRemaining = (TextView) findViewById(R.id.textViewTime);

        timeRemaining.setText( eyeBallerUtils.getFormattedTimeRemaining( totalTime ) );

        /* return ball to original position */

        animX.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animator) {
                //System.out.println("\n\nonAnimationStart called\n\n");
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                System.out.println("\n\nonAnimationEnd called\n\n");
                getWindow().clearFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                timeRemaining.setText( Long.toString(animX.getDuration() + animX.getRepeatCount()) );
                totalTime = 0;
                countRemaining.setText( "" + animX.getRepeatCount() );
                animationSpeed  = sharedPref.getInt("prefSpeed", AnimationActivity.this.getResources().getInteger(R.integer.animation_speed));
                repeatCount     = sharedPref.getInt("prefCount", AnimationActivity.this.getResources().getInteger(R.integer.repeat_count));
                playPauseBtn.setImageResource( R.drawable.ic_play_circle_filled_black_24dp);
                animationRunning = 0;
                sputnikClickPaused = false;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                totalTime = totalTime - animationSpeed;

                repeatCount = repeatCount - 1;
                countRemaining.setText( Integer.toString( repeatCount ) );

                timeRemaining.setText( eyeBallerUtils.getFormattedTimeRemaining( totalTime ) );

                if( sputnikOn == 1 ) {
                    if (sputnikLeftRightFlag == 0 && sputnikLeft != null) {
                        sputnikLeft.start();
                        sputnikLeftRightFlag = 1;
                    } else if (sputnikLeftRightFlag == 1 && sputnikRight != null) {
                        sputnikRight.start();
                        sputnikLeftRightFlag = 0;
                    } else if( sputnikLeftRightFlag == 0 && sputnikLeft == null){
                        sputnikLeft = MediaPlayer.create(AnimationActivity.this, R.raw.sputnik_left);
                        sputnikLeft.start();
                        sputnikLeftRightFlag = 1;
                    } else if( sputnikLeftRightFlag == 1 && sputnikRight == null){
                        sputnikRight = MediaPlayer.create(AnimationActivity.this, R.raw.sputnik_right);
                        sputnikRight.start();
                        sputnikLeftRightFlag = 0;
                    }


                }
            }
        });
    }
    @Override
    public void onSaveInstanceState( Bundle savedInstanceState ){
        savedInstanceState.putInt("sputnikOn", sputnikOn );
        savedInstanceState.putBoolean( "animClickPaused", animClickPaused );
        savedInstanceState.putBoolean( "sputnikClickPaused", sputnikClickPaused);
        savedInstanceState.putBoolean( "animXRunning", animX.isRunning() );
        savedInstanceState.putInt( "totalTime", totalTime );
        savedInstanceState.putInt( "repeatCount", repeatCount );
        savedInstanceState.putInt( "animationSpeed", animationSpeed );
    }

    public void animateHorizontal(View view) {
        switch ( animationRunning ){

            /* 1st time start */
            case 0:
                animX.start();
                playPauseBtn.setImageResource( R.drawable.ic_pause_circle_filled_black_24dp);
                animationRunning = 1;
                break;

            /* animation started and running - this will pause it*/
            case 1:
                playPauseBtn.setImageResource( R.drawable.ic_play_circle_filled_black_24dp);

                sputnikClickPaused = true;

                if( sputnikRight != null) {
                    sputnikRight.stop();
                    sputnikRight.release();
                    sputnikRight = null;
                }

                if( sputnikLeft != null ) {
                    sputnikLeft.stop();
                    sputnikLeft.release();
                    sputnikLeft = null;
                }
                animX.pause();
                animationRunning = 2;
                break;

            /* animation paused - this will restart it */
            case 2:
                playPauseBtn.setImageResource( R.drawable.ic_pause_circle_filled_black_24dp);

                if(sputnikClickPaused && sputnikOn == 1 && sputnikRight == null)
                {
                    sputnikLeft = MediaPlayer.create(this, R.raw.sputnik_left);
                    sputnikClickPaused = false;
                }
                if(sputnikClickPaused && sputnikOn == 1 && sputnikLeft == null){
                    sputnikRight = MediaPlayer.create(this, R.raw.sputnik_right);
                    sputnikClickPaused = false;
                }

                animX.resume();
                animationRunning = 1;
                break;

            default:
                animationRunning = 0;
                playPauseBtn.setImageResource( R.drawable.ic_play_circle_filled_black_24dp);
        }

    }

    public void sputnikOnOff( View view ){

        if( sputnikOn == 0 ){
            sputnikBtn.setImageResource(R.drawable.ic_volume_up_black_24dp);

            if( sputnikLeft == null){
                //sputnikLeft = MediaPlayer.create(this, R.raw.sputnik_left);
            }
            if( sputnikRight == null){
                //sputnikRight = MediaPlayer.create(this, R.raw.sputnik_right);
            }

            sputnikOn = 1;

        } else if( sputnikOn == 1){
            sputnikBtn.setImageResource( R.drawable.ic_volume_off_black_24dp);
            sputnikOn = 0;
        }
        System.out.println("sound button clicked");
    }

    public void pickTravelSpeed( View view ){
        view.setOnClickListener( new View.OnClickListener(){
            @Override
            public  void onClick(View view){
                LayoutInflater li = LayoutInflater.from( AnimationActivity.this );
                View pickBallTravelSpeedView = li.inflate( R.layout.layout_ball_travel_speed_dialog, null );
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( AnimationActivity.this);
                alertDialogBuilder.setView( pickBallTravelSpeedView );

                final NumberPicker pickSpeed = (NumberPicker) pickBallTravelSpeedView.findViewById(R.id.speedPicker);

                pickSpeed.setMinValue( 0 );
                final String[] values = {"500", "550", "600", "650", "700", "750", "800", "850", "900", "950", "1000"};
                pickSpeed.setMaxValue( values.length - 1);
                pickSpeed.setDisplayedValues( values );

                alertDialogBuilder
                        .setCancelable( true )
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        AnimationActivity.this.animationSpeed = Integer.parseInt( values[ pickSpeed.getValue()] );
                                        AnimationActivity.this.animX.setDuration( Long.parseLong( values[ pickSpeed.getValue()]));
                                        AnimationActivity.this.timeRemaining.setText( eyeBallerUtils.getFormattedTimeRemaining( AnimationActivity.this.totalTime ));

                                        editor.putInt("prefSpeed", Integer.parseInt( values[ pickSpeed.getValue()] ));
                                        editor.commit();
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener(){

                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    public void pickRepeatCount( View view ){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* prepare to create prompt dialogs */
                LayoutInflater li = LayoutInflater.from( AnimationActivity.this );
                View pickRepeatCountView = li.inflate( R.layout.layout_repeat_count_dialog, null );
                AlertDialog.Builder  alertDialogbuilder = new AlertDialog.Builder( AnimationActivity.this );
                alertDialogbuilder.setView( pickRepeatCountView );

                final NumberPicker pickCnt = (NumberPicker) pickRepeatCountView.findViewById( R.id.repeatCountPicker);

                pickCnt.setMinValue(0);
                final String[] values= {"59","69", "79", "89", "99", "109", "119", "129", "139", "149", "159", "169", "179", "189", "199", "209"};
                pickCnt.setMaxValue( values.length -1 );
                pickCnt.setDisplayedValues(  values );

                alertDialogbuilder
                        .setCancelable( true )
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        AnimationActivity.this.repeatCount = Integer.parseInt( values[ pickCnt.getValue() ] );
                                        AnimationActivity.this.animX.setRepeatCount( Integer.parseInt( values[ pickCnt.getValue()]) );
                                        System.out.println("repeat picker clicked : " + values[  pickCnt.getValue()] );
                                        AnimationActivity.this.countRemaining.setText( values[ pickCnt.getValue()] );

                                        editor.putInt("prefCount",  Integer.parseInt( values[ pickCnt.getValue()]));
                                        editor.commit();

                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener(){
                                    public void  onClick( DialogInterface dialog, int id){
                                        dialog.cancel();
                                    }

                        });
                AlertDialog alertDialog = alertDialogbuilder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Animation Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.eyeballer.eyeballernative/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Animation Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.eyeballer.eyeballernative/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}