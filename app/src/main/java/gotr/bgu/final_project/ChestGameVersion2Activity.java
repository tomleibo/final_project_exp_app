package gotr.bgu.final_project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

import behavioralCapture.env.Env;

public class ChestGameVersion2Activity extends AppCompatActivity {

    public static final int NUMBER_OF_GAMES = 5;
    float keyOriginX;
    float keyOriginY;
    ImageView key;
    ImageView closeChest;
    ImageView openChest;

    int counter;

    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Env.build(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chest_game_version2);
        Env.getInstance().addAllViews(findViewById(android.R.id.content));

        counter = 1;

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        key = (ImageView) findViewById(R.id.key);
        openChest = (ImageView) findViewById(R.id.openChest);
        closeChest = (ImageView) findViewById(R.id.closeChest);

        key.setOnTouchListener(new KeyTouchListener());
    }

    private final class KeyTouchListener implements View.OnTouchListener {
        float dX, dY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            float closeChestX = closeChest.getX();
            float closeChestY = closeChest.getY();
            float closeChestWidth = closeChest.getWidth()/2;
            float closeChestHeight = closeChest.getHeight()/2;

            float openChestX = openChest.getX();
            float openChestY = openChest.getY();
            float openChestWidth = openChest.getWidth()/2;
            float openChestHeight = openChest.getHeight()/2;

            switch (event.getActionMasked()) {

                case MotionEvent.ACTION_DOWN:
                    keyOriginX = v.getX();
                    keyOriginY = v.getY();

                    dX = v.getX() - event.getRawX();
                    dY = v.getY() - event.getRawY();
                    break;

                case MotionEvent.ACTION_MOVE:
                    v.animate()
                            .x(event.getRawX() + dX)
                            .y(event.getRawY() + dY)
                            .setDuration(0)
                            .alpha(0.5f)
                            .start();

                    if ((Math.abs(v.getX() - closeChestX) <= closeChestWidth) && (Math.abs(v.getY() - closeChestY) <= closeChestHeight)) {
                        closeChest.setVisibility(View.INVISIBLE);
                        openChest.setVisibility(View.VISIBLE);
                    } else {
                        openChest.setVisibility(View.INVISIBLE);
                        closeChest.setVisibility(View.VISIBLE);
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    if ((Math.abs(v.getX() - openChestX) <= openChestWidth) && (Math.abs(v.getY() - openChestY) <= openChestHeight)) {
                        final ViewGroup viewgroup = (ViewGroup) openChest.getParent();
                        viewgroup.removeView(openChest);
                        viewgroup.removeView(closeChest);
                        viewgroup.removeView(key);

                        closeChest.setVisibility(View.GONE);
                        openChest.setVisibility(View.GONE);
                        key.setVisibility(View.GONE);

                        initializeNextViews();

                        if (counter> NUMBER_OF_GAMES){
                            OpenNextGame();
                        }
                        else {
                            showProgressBar("Collecting Treasure");
                            key.setVisibility(View.VISIBLE);
                            closeChest.setVisibility(View.VISIBLE);

                            key.setOnTouchListener(new KeyTouchListener());
                        }
                    } else {
                        v.animate()
                                .x(keyOriginX)
                                .y(keyOriginY)
                                .setDuration(200)
                                .alpha(1f)
                                .start();

                        openChest.setVisibility(View.INVISIBLE);
                        closeChest.setVisibility(View.VISIBLE);
                    }
                    break;

                default:
                    Env.getInstance().getTouchHandler().onTouch(v, event);
                    return false;
            }
            Env.getInstance().getTouchHandler().onTouch(v, event);
            return true;
        }

        private void initializeNextViews() {
            counter ++;
            if (counter==2){
                key = (ImageView) findViewById(R.id.key2);
                openChest = (ImageView) findViewById(R.id.openChest2);
                closeChest = (ImageView) findViewById(R.id.closeChest2);
            }
            if (counter==3){
                key = (ImageView) findViewById(R.id.key3);
                openChest = (ImageView) findViewById(R.id.openChest3);
                closeChest = (ImageView) findViewById(R.id.closeChest3);
            }
            if (counter==4){
                key = (ImageView) findViewById(R.id.key4);
                openChest = (ImageView) findViewById(R.id.openChest4);
                closeChest = (ImageView) findViewById(R.id.closeChest4);
            }
            if (counter==5) {
                key = (ImageView) findViewById(R.id.key5);
                openChest = (ImageView) findViewById(R.id.openChest5);
                closeChest = (ImageView) findViewById(R.id.closeChest5);

            }
        }
    }

    private void OpenNextGame(){
        Intent intent = new Intent(this, ChestGameActivity.class);
        finish();
        startActivity(intent);
    }

    private void showProgressBar(String message){
        mDialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_DARK);
        mDialog.setTitle("Please wait");
        mDialog.setMessage(message);
        mDialog.show();

        WaitTime wait = new WaitTime();
        wait.execute();
    }

    private class WaitTime extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.show();
        }

        @Override
        protected void onCancelled() {
            mDialog.dismiss();
            super.onCancelled();
        }

        @Override
        protected Void doInBackground(Void... params) {
            long delayInMillis = 1500;
            Timer timer = new Timer();

            timer.schedule(new TimerTask() {
                @Override
                public void run() {

                    mDialog.dismiss();
                }
            }, delayInMillis);
            return null;
        }
    }



}
