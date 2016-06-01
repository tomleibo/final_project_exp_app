package behavioralCapture.events;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import behavioralCapture.Utils.Jsonable;

public class ScaleTouchEvent implements Jsonable {

    public static final int SCALE = 1500;
    private final int type;
    private final long timestamp;
    private List<DoubleTouchEvent> events;
    private static final String UNDEFINED = "UNDEFINED";
    private String activity;
    private String tag;


    public ScaleTouchEvent(long timestamp, List<DoubleTouchEvent> events) {
        this.type = SCALE;
        this.timestamp = timestamp;
        this.events = events;
        if (events.isEmpty()) {
            this.tag = UNDEFINED;
            this.activity = UNDEFINED;
        } else {
            this.tag = events.get(0).getTag();
            this.activity = events.get(0).getActivity();
        }

    }

    public ScaleTouchEvent(long timestamp) {
        this.type = SCALE;
        this.timestamp = timestamp;
        this.events=new ArrayList<>();
        this.tag = UNDEFINED;
        this.activity = UNDEFINED;

    }

    public long getTimestamp() {
        return timestamp;
    }

    public void addEvent(DoubleTouchEvent e) {
        events.add(e);
        if (this.tag.equals(this.UNDEFINED)) {
            this.tag = e.getTag();
            this.activity = e.getActivity();
        }
    }

    public List<DoubleTouchEvent> getEvents () {
        return events;
    }

    @Override
    public String toJson(Gson gson) {
        return gson.toJson(this);
    }

    @Override
    public long getTime() {
        return timestamp;
    }

    public String getTag() {
        return tag;
    }

    public String getActivity() {
        return activity;
    }

    public int getType() {
        return type;
    }
}
