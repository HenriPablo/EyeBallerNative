package com.eyeballer.eyeballernative;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class AnimationActivity extends AppCompatActivity {

    ImageView img;

    int animationSpeed = 800;
    int repeatCount = 99;   /* odd number of repeats will return the ball to its original position */
    int totalTime = 0;
    int animationRunning = 0;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    ObjectAnimator animX;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
         * Has to be BEFORE setContentView
         * http://stackoverflow.com/questions/12204336/getwindow-addflagswindowmanager-layoutparams-flag-keep-screen-on-no-response
         *
         */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_animation);
        ImageView img = (ImageView) findViewById(R.id.imageView);
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

        animX = ObjectAnimator.ofFloat(img, "x", (w - (48) )  );
        animX.setRepeatCount(repeatCount);
        animX.setRepeatMode( 2 );
        animX.setDuration( animationSpeed );

        totalTime = repeatCount * animationSpeed;

        /* remainig count display */
        final TextView countRemaining = (TextView) findViewById(R.id.textViewCount);
        countRemaining.setText( countRemaining.getText() + " " + repeatCount );

        /* remaining time dispaly */
        final TextView timeRemaining = (TextView) findViewById(R.id.textViewTime);
        timeRemaining.setText( timeRemaining.getText() + " " + totalTime );

        /* return ball to original position */


        animX.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                System.out.println("\n\nonAnimationStart called\n\n");
                //repeatCount = repeatCount - 1;
                //countRemaining.setText( "Cnt: " + repeatCount );
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                System.out.println("\n\nonAnimationEnd called\n\n");
                getWindow().clearFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                System.out.println("\n\n repeat count at end: " + animX.getRepeatCount() + "\n\n");
                timeRemaining.setText( "Time: " + animX.getDuration() + animX.getRepeatCount() );
                totalTime = 0;
                countRemaining.setText( "Cnt: " + animX.getRepeatCount() );
                animationSpeed = (int) animX.getDuration();
                repeatCount = animX.getRepeatCount();
                Button startBtn = (Button) findViewById(R.id.button);
                startBtn.setText("Start");
                animationRunning = 0;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                System.out.println("\n\nonAnimationCancel called\n\n");
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                totalTime = totalTime - animationSpeed;
                timeRemaining.setText( "Time: " + totalTime );

                System.out.println("\n\nonAnimationRepeat called\n\n");
                repeatCount = repeatCount - 1;
                countRemaining.setText( "Cnt: " + repeatCount );
            }
        });

    }


    public void animateHorizontal(View view) {

        Button startBtn = (Button) findViewById(R.id.button);

        switch ( animationRunning ){
            case 0:
                animX.start();
                startBtn.setText("Pause");
                animationRunning = 1;
                break;

            case 1:
                startBtn.setText("Resume");
                animX.pause();
                animationRunning = 2;
                break;
            case 2:
                startBtn.setText("Pause");
                animX.resume();
                animationRunning = 1;
                break;

            default:
                animationRunning = 0;
                startBtn.setText("Start 2");
        }

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