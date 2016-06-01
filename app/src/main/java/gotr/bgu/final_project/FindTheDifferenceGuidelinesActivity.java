package gotr.bgu.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import behavioralCapture.env.Env;

public class FindTheDifferenceGuidelinesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Env.build(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_the_difference_guidelines);
        Env.getInstance().addAllViews(findViewById(android.R.id.content));
    }

    public void toFindTheDifferenceGame(View view){
        Intent intent = new Intent(this,ImagesActivity.class);
        intent.putExtra("article", 0);
        intent.putExtra("imageIndex",1);
        finish();
        startActivity(intent);
    }
}
