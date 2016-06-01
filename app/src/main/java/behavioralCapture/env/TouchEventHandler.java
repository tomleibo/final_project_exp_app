package behavioralCapture.env;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContextWrapper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import behavioralCapture.events.DoubleTouchEvent;
import behavioralCapture.events.ScaleTouchEvent;
import behavioralCapture.events.SingleTouchEvent;
import behavioralCapture.events.TouchEventChunk;
import behavioralCapture.softKeyboard.KeyboardEventHandler;

// TODO: Fix time sync
public class TouchEventHandler implements View.OnTouchListener {

    private HashMap<Long, List<SingleTouchEvent>> singleTouchEventsByDownEventTime;
    private HashMap<Long, List<DoubleTouchEvent>> doubleTouchEventsByDownEventTime;

    private long startingPtrDownEventTime;
    private float distanceThreshold = 10;

    public TouchEventHandler() {
        this.singleTouchEventsByDownEventTime = new HashMap<>();
        this.doubleTouchEventsByDownEventTime = new HashMap<>();
    }

    public void registerTouchEvents(View v) {
        v.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
//        Log.d("Touch", "PrimaryOnTouch | event: " + event.toString());
        String tag = v.getTag() == null ? "" : ((String) v.getTag());
        String activity = ((ContextWrapper) v.getContext()).getBaseContext().toString().split("@")[0];
        if (!(v instanceof EditText)) {
            KeyboardEventHandler.getInstance().hideSoftKeyboard();
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                addToSingleTouchEventsByDownTime(event, event.getEventTime(), tag, activity);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                startingPtrDownEventTime = event.getEventTime();
                addToDoubleTouchEventsByDownTime(event, event.getEventTime(), tag, activity);
                break;
            case MotionEvent.ACTION_MOVE:
                boolean isSingleTouchEvent = event.getPointerCount() == 1;

                if (isSingleTouchEvent) {
                    addToSingleTouchEventsByDownTime(event, event.getDownTime(), tag, activity);
                } else {
                    addToDoubleTouchEventsByDownTime(event, startingPtrDownEventTime, tag, activity);
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                sealScaleEvent(startingPtrDownEventTime, event, tag, activity);
                startingPtrDownEventTime = -1;
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.d("Swipe", "ACTION CANCEL");
            case MotionEvent.ACTION_UP:
                sealSwipeEvent(event.getDownTime(), event, tag, activity);
                break;
            default:
                System.out.println("What am I?" + event.toString());
        }
        return false;
    }

    private void addDoubleTouchEventToQueue(MotionEvent event, long downTime, String tag, String activity) {
        addDoubleTouchEventToQueue(new DoubleTouchEvent(event, downTime, tag, activity));
    }

    private void addDoubleTouchEventToQueue(DoubleTouchEvent event) {
        Env.getInstance().getDb().insert(DoubleTouchEvent.class, event);
    }

    private void addSingleTouchEventToQueue(MotionEvent event, String tag, String activity) {
        addSingleTouchEventToQueue(new SingleTouchEvent(event, false, tag, activity));
    }

    private void addSingleTouchEventToQueue(SingleTouchEvent event) {
        Env.getInstance().getDb().insert(SingleTouchEvent.class, event);
    }

    private synchronized void addToSingleTouchEventsByDownTime(MotionEvent event, long downTime, String tag, String activity) {
//        Log.d("SingleTouch", "Adding " + event.toString() + "\nat " + downTime);
        if (singleTouchEventsByDownEventTime.containsKey(downTime)) {
            singleTouchEventsByDownEventTime.get(downTime).add(new SingleTouchEvent(event, false, tag, activity));

        } else {
            ArrayList<SingleTouchEvent> events = new ArrayList<>();
            events.add(new SingleTouchEvent(event, false, tag, activity));
            singleTouchEventsByDownEventTime.put(downTime, events);
        }
    }

    private synchronized void addToDoubleTouchEventsByDownTime(MotionEvent event, long downTime, String tag, String activity) {
//        Log.d("DoubleTouch", "Adding " + event.toString() + "\nat " + downTime);
        DoubleTouchEvent doubleTouchEvent = new DoubleTouchEvent(event, downTime, tag, activity);
        if (doubleTouchEventsByDownEventTime.containsKey(downTime)) {
            doubleTouchEventsByDownEventTime.get(downTime).add(doubleTouchEvent);

        } else {
            ArrayList<DoubleTouchEvent> events = new ArrayList<>();
            events.add(doubleTouchEvent);
            doubleTouchEventsByDownEventTime.put(downTime, events);
        }
    }

    private void sealSwipeEvent(long downTime, MotionEvent upEvent, String tag, String activity) {

        if (singleTouchEventsByDownEventTime.containsKey(downTime)) {
//            Log.d("sealSwipeEvent", "downTime: " + downTime + " exists");
            List<SingleTouchEvent> events = singleTouchEventsByDownEventTime.get(downTime);
            if (events.size() >= 1 && isASwipe(events.get(0), upEvent)) {
                events.add(new SingleTouchEvent(upEvent, false, tag, activity));
                Env.getInstance().getDb().insert(TouchEventChunk.class, new TouchEventChunk(downTime, events));
            } else {
                for (SingleTouchEvent event : events) {
                    addSingleTouchEventToQueue(event);
                }
                addSingleTouchEventToQueue(upEvent, tag, activity);
            }
            singleTouchEventsByDownEventTime.remove(downTime);
        } else {
//            Log.d("sealSwipeEvent", "downTime: " + downTime + " not exists");
        }
    }

    private boolean isASwipe(SingleTouchEvent downEvent, MotionEvent upEvent) {
        float downEventX = downEvent.getX();
        float downEventY = downEvent.getY();
        float upEventX = upEvent.getRawX();
        float upEventY = upEvent.getRawY();

        return (Math.abs(downEventX - upEventX) >= this.distanceThreshold || Math.abs(downEventY - upEventY) >= this.distanceThreshold);
    }

    private void sealScaleEvent(long downTime, MotionEvent ptrUpEvent, String tag, String activity) {
        if (doubleTouchEventsByDownEventTime.containsKey(downTime)) {
//            Log.d("sealScaleEvent", "downTime: " + downTime + " exists");
            List<DoubleTouchEvent> events = doubleTouchEventsByDownEventTime.get(downTime);
            if (events.size() == 1) {
                // Only Down Event
//                Log.d("sealScaleEvent", "one event in list: " + events.get(0).getAction_str() + " | time: " + downTime);
                addDoubleTouchEventToQueue(events.get(0));
                addDoubleTouchEventToQueue(ptrUpEvent, downTime, tag, activity);
            } else {
//                Log.d("sealScaleEvent", "sealing list | time: " + downTime);
                events.add(new DoubleTouchEvent(ptrUpEvent, downTime, tag, activity));
                Env.getInstance().getDb().insert(ScaleTouchEvent.class, new ScaleTouchEvent(downTime, events));
            }

            doubleTouchEventsByDownEventTime.remove(downTime);
        } else {
//            Log.d("sealScaleEvent", "downTime: " + downTime + " not exists");
        }
    }


}
