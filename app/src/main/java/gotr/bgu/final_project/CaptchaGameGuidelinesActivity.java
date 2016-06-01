package gotr.bgu.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import behavioralCapture.Utils.Jsonable;
import behavioralCapture.db.tables.MotionEventTable;
import behavioralCapture.db.tables.TouchEventTable;
import behavioralCapture.env.CommunicationService;
import behavioralCapture.env.Env;
import behavioralCapture.env.SensorHandler;
import behavioralCapture.events.MotionSensorEventWrapper;
import behavioralCapture.events.SingleTouchEvent;

public class CaptchaGameGuidelinesActivity extends AppCompatActivity {

    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captcha_game_guidelines);
        username = getIntent().getStringExtra("username");
        startCapture();
    }

    public void toCaptchaGame(View view){
        Intent intent = new Intent(this, CaptchaGameActivity.class);
        finish();
        startActivity(intent);
    }

    public void startCapture(){
        Env env = Env.build(this);
        env.getSensorHandler().registerMotionSensors(env.getSensorManager(), true, SensorHandler.DELAY_BETWEEN_EVENTS);
        Env.getInstance().addAllViews(findViewById(android.R.id.content));

        Intent in = new Intent(this, CommunicationService.class);
        in.putExtra(CommunicationService.STATE_EXTRA_NAME,CommunicationService.STATE_SEND_NEW);
        String url = getResources().getString(R.string.base_url)+username;
        in.putExtra(CommunicationService.URL_EXTRA_NAME, url);
        env.url=url;
        startService(in);
    }

    public String evaluate() {
        StringBuilder sb =new StringBuilder();
        for (Jsonable s : new TouchEventTable().selectAll(1000, 0)) {
            SingleTouchEvent e= (SingleTouchEvent)s;
            sb.append(e.getAction_str()+"="+e.getAction()+","+e.getTime()+
                    ","+e.getTimestamp()+"\n");
        }

        sb.append("\n\n\nMOTION EVENTS: \n\n\n");

        for (Jsonable s : new MotionEventTable().selectAll(1000, 0)) {
            MotionSensorEventWrapper e= (MotionSensorEventWrapper )s;
            sb.append(e.getType()+","+e.getTime()+
                    ","+
                    Math.sqrt(Math.pow(e.getValues()[0],2)+Math.pow(e.getValues()[1],2)+Math.pow(e.getValues()[2],2))
                    + "\n");
        }
        return sb.toString();
    }
}
