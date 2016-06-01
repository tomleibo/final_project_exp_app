package gotr.bgu.final_project;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


public class Paragraph {
    private TextView title;
    private TextView text;

    public Paragraph(Activity context,String title,String text){
        this.title = new TextView(context);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if(title.contentEquals("")){
            params2.setMargins(0, -70, 0, 0);
        }
        this.title.setLayoutParams(params2);
        this.title.setTextAppearance(context, R.style.Paragraph_title);
        this.title.setText(title);
        this.text = new TextView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 40);
        this.text.setLayoutParams(params);
        this.text.setTextAppearance(context,R.style.Paragraph_text);
        this.text.setText(text);
    }

    public TextView getTitle(){
        return this.title;
    }

    public TextView getText(){
        return this.text;
    }
}
