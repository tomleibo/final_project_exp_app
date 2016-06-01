package gotr.bgu.final_project;

import android.app.Activity;

import java.util.ArrayList;


public class Datasets {

    private int currentArticle = 0;
    private ArrayList<FindTheDifferenceContainer> articles = new ArrayList<>();
    private static Datasets instance = null;

    public static Datasets instantiate(Activity context){
        if(instance==null) {
            instance =  new Datasets(context);
        }
        return instance;
    }

    private Datasets(Activity context){
        ArrayList<Paragraph> paragraphs = new ArrayList<>();
        ArrayList<String> images = new ArrayList<>();
        ArrayList<String> questions = new ArrayList<>();

        images.add(context.getResources().getString(R.string.article1_image_1));
        images.add(context.getResources().getString(R.string.article1_image_2));

        questions.add(context.getResources().getString(R.string.article1_image1_question));


        articles.add(new FindTheDifferenceContainer(context, context.getResources().getString(R.string.title_activity_article1), paragraphs, images, questions));

    }

    public FindTheDifferenceContainer getArticle(int index){
        if(index<articles.size()) {
            FindTheDifferenceContainer article = articles.get(index);
            this.currentArticle = index;
            return article;
        }
        return null;
    }

}
