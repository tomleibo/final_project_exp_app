package gotr.bgu.final_project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import behavioralCapture.env.Env;


public class Dataset extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dataset);
        final Activity $this = this;
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.datasetLayout);
        LinearLayout scrollViewLayout = (LinearLayout) findViewById(R.id.scrollViewLayout);
        Datasets datasets = Datasets.instantiate(this);
        final int articleToLoad;
        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            articleToLoad = extras.getInt("next_article");
        }
        else{
            articleToLoad = 0;
        }
        FindTheDifferenceContainer article = datasets.getArticle(articleToLoad);
        setTitle(article.getTitle());
        ArrayList<Paragraph> paragraphs = article.getParagraphs();
        for(int i=0;i<paragraphs.size();i++){
            Paragraph p = paragraphs.get(i);
            TextView title = p.getTitle();
            TextView text = p.getText();
            scrollViewLayout.addView(title);
            scrollViewLayout.addView(text);
        }
        if(article.hasImages()) {
            Button button = new Button(this);
            button.setText(R.string.toArticleImage);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent($this,ImagesActivity.class);
                    intent.putExtra("article",articleToLoad);
                    intent.putExtra("imageIndex",1);
                    startActivity(intent);
                }
            });
            scrollViewLayout.addView(button);
        }

        Env.getInstance().addAllViews(findViewById(android.R.id.content));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_dataset, menu);
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