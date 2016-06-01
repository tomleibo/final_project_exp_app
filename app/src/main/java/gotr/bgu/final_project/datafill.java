package gotr.bgu.final_project;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;

import behavioralCapture.env.Env;


public class datafill extends ActionBarActivity {
    private GestureDetector gestureDetector;
    private FindTheDifferenceContainer article;
    private int articleToLoad;
    private int imageIndex;

    private final int FIRST_QUESTION = 0;
    private final int SECOND_QUESTION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datafill);



        final Activity $this = this;

        Datasets datasets = Datasets.instantiate(this);

        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            articleToLoad = extras.getInt("article");
            imageIndex = extras.getInt("imageIndex");
        }
        else{
            articleToLoad = 0;
            imageIndex = 1;
        }
        article = datasets.getArticle(articleToLoad);
        this.setTitle("Fill data on article " + (articleToLoad + 1));
        Env.getInstance().addAllViews(findViewById(android.R.id.content));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_datafill, menu); /* Disabling settings. */
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

}
