package gotr.bgu.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import behavioralCapture.env.Env;

public class ClickingGameActivity extends AppCompatActivity {

    int counter;
    Button button0 ;
    Button button1 ;
    Button button2 ;
    Button button3 ;
    Button button4 ;
    Button button5 ;
    Button button6 ;
    Button button7 ;
    Button button8 ;
    final int max_iterations = 18;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Env.build(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clicking_game);
        this.setTitle("Clicking game.");
        button0 = (Button) findViewById(R.id.clickBut0);
        button1 = (Button) findViewById(R.id.clickBut1);
        button2 = (Button) findViewById(R.id.clickBut2);
        button3 = (Button) findViewById(R.id.clickBut3);
        button4 = (Button) findViewById(R.id.clickBut4);
        button5 = (Button) findViewById(R.id.clickBut5);
        button6 = (Button) findViewById(R.id.clickBut6);
        button7 = (Button) findViewById(R.id.clickBut7);
        button8 = (Button) findViewById(R.id.clickBut8);
        counter = 0;
        button0.setVisibility(View.VISIBLE);
        Env.getInstance().addAllViews(findViewById(android.R.id.content));


    }

    public void onClickGame(View v) {



        if (counter >= max_iterations) {
            Intent intent = new Intent(this, FinalscreenActivity.class);
            finish();
            startActivity(intent);
            return;
        }

        switch (counter % 9) {
            case 0:
                button0.setVisibility(View.INVISIBLE);
                button1.setVisibility(View.VISIBLE);
                counter ++ ;
                return;
            case 1:
                button1.setVisibility(View.INVISIBLE);
                button2.setVisibility(View.VISIBLE);
                counter ++ ;
                return;
            case 2:
                button2.setVisibility(View.INVISIBLE);
                button3.setVisibility(View.VISIBLE);
                counter ++ ;
                return;
            case 3:
                button3.setVisibility(View.INVISIBLE);
                button4.setVisibility(View.VISIBLE);
                counter ++ ;
                return;
            case 4:
                button4.setVisibility(View.INVISIBLE);
                button5.setVisibility(View.VISIBLE);
                counter ++ ;
                return;
            case 5:
                button5.setVisibility(View.INVISIBLE);
                button6.setVisibility(View.VISIBLE);
                counter ++ ;
                return;
            case 6:
                button6.setVisibility(View.INVISIBLE);
                button7.setVisibility(View.VISIBLE);
                counter ++ ;
                return;
            case 7:
                button7.setVisibility(View.INVISIBLE);
                button8.setVisibility(View.VISIBLE);
                counter ++ ;
                return;
            case 8:
                button8.setVisibility(View.INVISIBLE);
                button0.setVisibility(View.VISIBLE);
                counter ++ ;
                return;
        }
    }
}
