package behavioralCapture.events;

import android.annotation.TargetApi;
import android.os.Build;

import com.google.gson.Gson;

import java.util.LinkedList;
import java.util.List;

import behavioralCapture.Utils.Jsonable;

/**
 * Created by user1 on 25/08/2015.
 */
public class TouchEventChunk implements Jsonable {

    private static final int SWIPE = 2000;

    private final int type=SWIPE;
    private final long timestamp;
    private final List<SingleTouchEvent> eventsChunk;
    private static final String UNDEFINED = "UNDEFINED";
    private String activity;
    private String tag;

    public long getTimestamp() {
        return timestamp;
    }

    public List<SingleTouchEvent> getEventsChunk() {
        return eventsChunk;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public TouchEventChunk(long downTime) {
        this.timestamp = downTime;
        this.eventsChunk = new LinkedList<>();
        this.tag = UNDEFINED;
        this.activity = UNDEFINED;
    }

    public TouchEventChunk(long timeStamp, List<SingleTouchEvent> eventsChunk) {
        this.timestamp = timeStamp;
        this.eventsChunk = eventsChunk;
        if (eventsChunk.isEmpty()) {
            this.tag = UNDEFINED;
            this.activity = UNDEFINED;
        } else {
            this.tag = eventsChunk.get(0).getTag();
            this.activity = eventsChunk.get(0).getActivity();
        }
    }

    public void addEvent(SingleTouchEvent e) {
        eventsChunk.add(e);
        if (this.tag.equals(this.UNDEFINED)) {
            this.tag = e.getTag();
            this.activity = e.getActivity();
        }
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
