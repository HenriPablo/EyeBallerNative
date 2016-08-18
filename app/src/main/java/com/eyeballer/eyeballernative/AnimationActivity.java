package com.eyeballer.eyeballernative;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.DialogInterface;
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

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AnimationActivity extends AppCompatActivity {

    ImageView img;
    ObjectAnimator animX;
    EyeBallerUtils eyeBallerUtils;

    int animationSpeed = 700;
    int repeatCount = 99;   /* odd number of repeats will return the ball to its original position */
    int totalTime = 0;

    int animationRunning = 0;
    int sputnikLeftRightFlag = 0; /* alternate between 0 and 1 - play left or right sound */
    MediaPlayer sputnikLeft;
    MediaPlayer sputnikRight;
    int sputnikOn = 0;
    ImageButton sputnikBtn;
    ImageButton playPauseBtn;
    TextView countRemaining;

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

        setContentView(R.layout.activity_animation);
        ImageView img = (ImageView) findViewById(R.id.ballView);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics( metrics );
        float w = metrics.widthPixels;
        float h = metrics.heightPixels;
        //System.out.println( "w in metrics: " + w  + "H in metrics: "  + h );

        RelativeLayout rLayout = (RelativeLayout) findViewById( R.id.layout_animation);
        float p = rLayout.getPaddingRight();
        //System.out.println( "right padding p: " + p );

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) img.getLayoutParams();
        //System.out.println( "lp.rightMargin: " + lp.rightMargin );
        System.out.println( "\n\nimg.getWidth(): " + img+ "\n\n");

        playPauseBtn = (ImageButton) findViewById( R.id.playPauseImageBtn);

        animX = ObjectAnimator.ofFloat(img, "x", (w - (48) )  );
        animX.setRepeatCount(repeatCount);
        animX.setRepeatMode( 2 );
        animX.setDuration( animationSpeed );

        totalTime = repeatCount * animationSpeed;

        this.setVolumeControlStream(AudioManager.STREAM_SYSTEM);
        sputnikLeft = MediaPlayer.create(this, R.raw.sputnik_left);
        sputnikLeft.setVolume( 5.0f, 5.0f );
        sputnikRight = MediaPlayer.create(this, R.raw.sputnik_right);
        sputnikRight.setVolume( 5.0f, 5.0f );
        sputnikBtn = (ImageButton) findViewById( R.id.sputnikBtn );



        /* remainig count display */
        countRemaining = (TextView) findViewById(R.id.textViewCount);
        countRemaining.setText( countRemaining.getText() + " " + repeatCount );

        /* remaining time dispaly */
        final TextView timeRemaining = (TextView) findViewById(R.id.textViewTime);

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
                System.out.println("\n\n repeat count at end: " + animX.getRepeatCount() + "\n\n");
                timeRemaining.setText( Long.toString(animX.getDuration() + animX.getRepeatCount()) );
                totalTime = 0;
                countRemaining.setText( "" + animX.getRepeatCount() );
                animationSpeed = (int) animX.getDuration();
                repeatCount = animX.getRepeatCount();
                playPauseBtn.setImageResource( R.drawable.ic_play_circle_filled_black_24dp);

                animationRunning = 0;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                System.out.println("\n\nonAnimationCancel called\n\n");
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                totalTime = totalTime - animationSpeed;


                System.out.println("\n\nonAnimationRepeat called\n\n");
                repeatCount = repeatCount - 1;
                countRemaining.setText( Integer.toString( repeatCount ) );

                //date = new Date( totalTime );
                timeRemaining.setText( eyeBallerUtils.getFormattedTimeRemaining( totalTime ) );

                if( sputnikOn == 1 ) {
                    if (sputnikLeftRightFlag == 0) {
                        sputnikLeft.start();
                        sputnikLeftRightFlag = 1;
                    } else if (sputnikLeftRightFlag == 1) {
                        sputnikRight.start();
                        sputnikLeftRightFlag = 0;
                    }
                }
                //} catch(Exception e) { e.printStackTrace(); }

            }
        });

    }


    public void animateHorizontal(View view) {
        switch ( animationRunning ){
            case 0:
                animX.start();
                playPauseBtn.setImageResource( R.drawable.ic_pause_circle_filled_black_24dp);
                animationRunning = 1;
                break;

            case 1:
                playPauseBtn.setImageResource( R.drawable.ic_play_circle_filled_black_24dp);
                animX.pause();
                animationRunning = 2;
                break;
            case 2:
                playPauseBtn.setImageResource( R.drawable.ic_pause_circle_filled_black_24dp);
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
            sputnikOn = 1;
        } else if( sputnikOn == 1){
            sputnikBtn.setImageResource( R.drawable.ic_volume_off_black_24dp);
            sputnikOn = 0;
        }
        System.out.println("sound button clicked");
    }

    public void pickTravelSpeed( View view ){

    }

    public void pickRepeatCount( View view ){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* prepare to create prompt dialogs */
                LayoutInflater li = LayoutInflater.from( getApplicationContext());
                View pickRepeatCountView = li.inflate( R.layout.layout_repeat_count_dialog, null );
                AlertDialog.Builder  alertDialogbuilder = new AlertDialog.Builder( AnimationActivity.this );
                alertDialogbuilder.setView( pickRepeatCountView );

                final NumberPicker pickCnt = (NumberPicker) pickRepeatCountView.findViewById( R.id.repeatCountPicker);

                pickCnt.setMinValue(0);
                final String[] values= {"59","69", "79", "89", "99", "109", "119", "129", "139", "149", "159", "169", "179", "189", "199", "209"};
                pickCnt.setMaxValue( values.length -1 );
                pickCnt.setDisplayedValues(  values );
                pickCnt.setWrapSelectorWheel( true );

                /*
                pickCnt.setOnValueChangedListener( new NumberPicker.OnValueChangeListener(){
                    @Override
                    public void onValueChange(NumberPicker numberPicker, int i, int i1) {

                    }

                });
                */
                alertDialogbuilder
                        .setCancelable( false )
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        AnimationActivity.this.repeatCount = Integer.parseInt( values[ pickCnt.getValue() ] );
                                        AnimationActivity.this.animX.setRepeatCount( Integer.parseInt( values[ pickCnt.getValue()]) );
                                        System.out.println("repeat picker clicked : " + values[  pickCnt.getValue()] );
                                        AnimationActivity.this.countRemaining.setText( values[ pickCnt.getValue()] );

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