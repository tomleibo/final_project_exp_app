package gotr.bgu.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import behavioralCapture.env.Env;

public class ChestGameGuidelinesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Env.build(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chest_game_guidelines);
        Env.getInstance().addAllViews(findViewById(android.R.id.content));

    }

    public void moveToChestGame(View view){
        Intent intent = new Intent(this, ChestGameVersion2Activity.class);
        finish();
        startActivity(intent);
    }
}
