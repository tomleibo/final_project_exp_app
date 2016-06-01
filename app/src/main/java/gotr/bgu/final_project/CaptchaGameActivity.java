package gotr.bgu.final_project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import behavioralCapture.env.Env;

public class CaptchaGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Env.build(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captcha_game);
        Env.getInstance().addAllViews(findViewById(android.R.id.content));

        EditText dream = (EditText) findViewById(R.id.dreamtheater);
        dream.setOnEditorActionListener(new DoneOnEditorActionListener());
    }

    public void toDifferenceGame(View view){

        //if (checkSpelling()) {
            Intent intent = new Intent(this, FindTheDifferenceGuidelinesActivity.class);
            finish();
            startActivity(intent);
        //}
    }

    public boolean checkSpelling(){
        EditText name = (EditText) findViewById(R.id.fullname);
        EditText color = (EditText) findViewById(R.id.rockortrance);
        EditText stairway = (EditText) findViewById(R.id.stairwaytoheaven);
        EditText dream = (EditText) findViewById(R.id.dreamtheater);

        if (!(name.getText().toString().length() > 1)) {
            name.setError("Your name is at least two chars long");
            name.setFocusable(true);
            return false;
        }

        if (!(color.getText().toString().length() > 2)) {
            color.setError("Please write a proper color");
            color.setFocusable(true);
            return false;
        }

        if (!(stairway.getText().toString().equalsIgnoreCase("stairway to heaven"))) {
            stairway.setError("You wrote something wrong.");
            stairway.setFocusable(true);
            return false;
        }

        if (!(dream.getText().toString().equalsIgnoreCase("Dream Theater"))) {
            dream.setError("You wrote something wrong.");
            dream.setFocusable(true);
            return false;
        }

        return true;
    }

    class DoneOnEditorActionListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == 0) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                v.setFocusable(false);
                return true;
            }
            return false;
        }
    }
}
