package gotr.bgu.final_project;

import android.app.Activity;

import java.util.ArrayList;


public class FindTheDifferenceContainer {
    private String title;
    private ArrayList<Paragraph> paragraphs;
    private ArrayList<String> images;
    private ArrayList<String> questions;
    private int currentImage;
    private Activity context;

    public FindTheDifferenceContainer(Activity context, String title, ArrayList<Paragraph> paragraphs, ArrayList<String> images, ArrayList<String> questions){
        this.title = title;
        this.paragraphs = paragraphs;
        this.questions = questions;
        this.images = images;
        this.currentImage = 0;
        this.context = context;
    }
    public String getTitle(){
        return this.title;
    }
    public ArrayList<Paragraph> getParagraphs(){
        return this.paragraphs;
    }
    public boolean hasImages(){
        return this.currentImage<images.size()-1;
    }
    public int getCurrentImage(){
        return this.currentImage;
    }
    public String getImage(int index){
        currentImage = index-1;
        return images.get(index-1);
    }
    public int[] getImageResources(){
        int[] imgs = new int[images.size()];
        for(int i=0;i<images.size();i++){
            imgs[i] = context.getResources().getIdentifier(images.get(i), "drawable", context.getPackageName());
        }
        return imgs;
    }
    public String[] getQuestions() {
        String[] arr = new String[this.questions.size()];
        return this.questions.toArray(arr);
    }
}
