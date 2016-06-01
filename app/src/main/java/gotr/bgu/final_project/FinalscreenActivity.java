package gotr.bgu.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import behavioralCapture.env.CommunicationService;
import behavioralCapture.env.Env;

public class FinalscreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalscreen);
        this.setTitle("The end");
        Intent in = new Intent(this, CommunicationService.class);
        in.putExtra(CommunicationService.USER_AT_LAST_SCREEN_EXTRA_NAME,true);
        in.putExtra(CommunicationService.URL_EXTRA_NAME,Env.getInstance().url);
        startService(in);
        Env.getInstance().getSensorHandler().registerMotionSensors(Env.getInstance().getSensorManager(),false,0);
        Env.isFinished=true;
    }
}
