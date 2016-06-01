package gotr.bgu.final_project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import behavioralCapture.env.Env;


public class ImagesActivity extends ActionBarActivity {

    class DoneOnEditorActionListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == 0) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                return true;
            }
            return false;
        }
    }

    private FindTheDifferenceContainer article;
    private int articleToLoad;
    private AwesomePagerAdapter awesomeAdapter;
    public static NonSwipeableViewPager awesomePager;
    RelativeLayout v;
    Context con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Env.build(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);


        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            articleToLoad = extras.getInt("article");
        }
        else{
            articleToLoad = 0;
        }

        awesomeAdapter = new AwesomePagerAdapter(this);
        awesomePager = (NonSwipeableViewPager) findViewById(R.id.view_pager);
        awesomePager.setAdapter(awesomeAdapter);
        con = this;
        Env.getInstance().addAllViews(findViewById(android.R.id.content));

        awesomePager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch(event.getActionMasked()){
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                }

                return Env.getInstance().getTouchHandler().onTouch(v, event);
            }
        });


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_images, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class AwesomePagerAdapter extends PagerAdapter {

        private int[] GalImages;
        private String[] article_questions;

        private int NUM_VIEWS = 0;

        public AwesomePagerAdapter(Activity context){
            Datasets datasets = Datasets.instantiate(context);
            FindTheDifferenceContainer article = datasets.getArticle(articleToLoad);
            GalImages = article.getImageResources();
            article_questions = article.getQuestions();
            NUM_VIEWS = GalImages.length+article_questions.length;

        }

        @Override
        public int getCount() {
            return NUM_VIEWS;
        }

        @Override
        public Object instantiateItem(View container, int position) {
            TouchImageView.b = true;



            if(position%3==2){
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                v = (RelativeLayout) inflater.inflate(R.layout.activity_datafill, null);
                EditText ans = (EditText) v.findViewById(R.id.question1_answer);
                ans.setOnEditorActionListener(new DoneOnEditorActionListener());
                TextView question1 = (TextView) v.findViewById(R.id.question1_header);
                question1.setGravity(Gravity.LEFT);
                question1.setText(article_questions[0]);
                EditText answer = (EditText) findViewById(R.id.question1_answer);
            }
            else{
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                v = (RelativeLayout) inflater.inflate(R.layout.image_item, null);
                TouchImageView tm = (TouchImageView) v.findViewById(R.id.image);
                Bitmap bm = BitmapFactory.decodeResource(getResources(),
                        GalImages[position]);
                tm.setImageBitmap(bm);
                Env.getInstance().registerTouchEvents(tm);
            }

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            if(position!=NUM_VIEWS-1) {
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                TextView tip = new TextView(con);
                tip.setLayoutParams(params);
                tip.setText(R.string.swipe_tip);
                tip.setTextSize(20);
                tip.setPadding(5, 0, 5, 20);
                v.addView(tip);

            } else {

                Button button = new Button(con);
                params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

                if (articleToLoad+1 <= 0) {

                    button.setText(con.getText(R.string.nextArticle));
                    button.setLayoutParams(params);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(con, Dataset.class);
                            intent.putExtra("next_article", articleToLoad + 1);
                            finish();
                            startActivity(intent);
                        }
                    });
                }

                else {
                    button.setText("To the next game.");
                    button.setLayoutParams(params);


                    button.setOnClickListener(new View.OnClickListener() {

                                                  @Override
                                                  public void onClick(View v) {

                                                      EditText answer = (EditText) findViewById(R.id.question1_answer);

                                                      boolean isNum = true;
                                                      try {
                                                          Integer.parseInt(answer.getText().toString());
                                                      } catch (NumberFormatException e){
                                                          isNum = false;
                                                      }
                                                      if (!isNum || answer.getText().toString().length() < 1 || answer.getText().toString().length() > 2) {
                                                          answer.setError("Please write a proper number (between 1 and 99)");
                                                          answer.setFocusable(true);
                                                      }
                                                      else {
                                                          Intent intent = new Intent(con, ChestGameGuidelinesActivity.class);
                                                          finish();
                                                          startActivity(intent);
                                                      }
                                                  }
                                              });
                }


                v.addView(button);
            }

            ((ViewPager) container).addView(v, 0);
            return v;
        }

        @Override
        public void destroyItem(View container, int position, Object view) {
            View v = (View) view;
            ((ViewPager) container).removeView(v);
            TouchImageView.b = true;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

}

