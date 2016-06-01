package gotr.bgu.final_project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import behavioralCapture.env.Env;

public class ChestGameActivity extends AppCompatActivity {
    ImageView key;
    ImageView closeChest;
    ImageView openChest;
    int clicks = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chest_game);
        Env.getInstance().addAllViews(findViewById(android.R.id.content));


        key = (ImageView) findViewById(R.id.key);
        openChest = (ImageView) findViewById(R.id.openChest);
        closeChest = (ImageView) findViewById(R.id.closeChest);

        key.setOnTouchListener(new KeyTouchListener());

        EditText et = (EditText) findViewById(R.id.editText);
        et.setOnEditorActionListener(new DoneOnEditorActionListener());
}

    private final class KeyTouchListener implements View.OnTouchListener {
        float dX, dY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            float closeChestX = closeChest.getX();
            float closeChestY = closeChest.getY();
            float closeChestWidth = closeChest.getWidth();
            float closeChestHeight = closeChest.getHeight();

            float openChestX = openChest.getX();
            float openChestY = openChest.getY();
            float openChestWidth = openChest.getWidth();
            float openChestHeight = openChest.getHeight();

            switch (event.getActionMasked()) {

                case MotionEvent.ACTION_DOWN:
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
                    v.getParent().requestDisallowInterceptTouchEvent(false);

                    if ((Math.abs(v.getX() - openChestX) <= openChestWidth) && (Math.abs(v.getY() - openChestY) <= openChestHeight)) {
                        final ViewGroup viewgroup = (ViewGroup) openChest.getParent();
                        viewgroup.removeView(openChest);
                        viewgroup.removeView(closeChest);
                        viewgroup.removeView(key);

                        closeChest.setVisibility(View.GONE);
                        openChest.setVisibility(View.GONE);
                        key.setVisibility(View.GONE);

                        Button btn = (Button) findViewById(R.id.button);
                        btn.setVisibility(View.VISIBLE);
                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Button innerBtn = (Button) v;
                                clicks++;
                                if (clicks >= 1) {
                                    viewgroup.removeView(innerBtn);
                                    innerBtn.setVisibility(View.GONE);
                                    ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
                                    int sc = scrollView.getScrollY();
                                    Button done = (Button) findViewById(R.id.btnDone);
                                    TextView tv = (TextView) findViewById(R.id.whattowriteText);
                                    EditText et = (EditText) findViewById(R.id.editText);
                                    RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                    rlp.setMargins(0, sc, 0, 0);
                                    tv.setLayoutParams(rlp);
                                    rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                    rlp.addRule(RelativeLayout.BELOW, R.id.whattowriteText);
                                    rlp.setMargins(0, 15, 0, 0);
                                    et.setLayoutParams(rlp);
                                    rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                    rlp.addRule(RelativeLayout.BELOW, R.id.editText);
                                    rlp.setMargins(0, 15, 0, 0);
                                    done.setLayoutParams(rlp);
                                    done.setVisibility(View.VISIBLE);
                                    tv.setVisibility(View.VISIBLE);
                                    et.setVisibility(View.VISIBLE);

                                }
                            }
                        });

                    } else {
                        v.animate()
                                .setDuration(0)
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

    }


    public boolean doneChallenge(View v){
        /*EditText et = (EditText) findViewById(R.id.editText);
        if (et.getText().toString().equalsIgnoreCase(getResources().getString(R.string.textWriteValidator))) {
        */
            ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
            scrollView.removeAllViews();
            Intent intent = new Intent(getBaseContext(), ClickingGameGuidelinesActivity.class);
            finish();
            startActivity(intent);

            return true;

        /*}
        et.setError("You wrote something wrong.");
        et.setFocusable(true);
        return false; */
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
