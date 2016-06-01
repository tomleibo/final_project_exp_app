package behavioralCapture.softKeyboard;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import behavioralCapture.events.SingleTouchEvent;
import behavioralCapture.env.Env;

// TODO: Fix time sync
public class KeyboardEventHandler implements View.OnTouchListener {

    private SoftKeyboard softKeyboard;
    private List<SingleTouchEvent> pendingEventsForUpdate;
    private static KeyboardEventHandler instance = null;

    public static synchronized KeyboardEventHandler getInstance(){
        if (instance == null) {
            synchronized (KeyboardEventHandler.class) {
                if (instance == null) {
                    instance = new KeyboardEventHandler();
                }
            }
        }
        return instance;
    }

    private KeyboardEventHandler() {
        pendingEventsForUpdate = new ArrayList<>();
    }

    public void registerTouchEvents(View v, SoftKeyboard softKeyboard) {
        this.softKeyboard = softKeyboard;
        v.setOnTouchListener(this);
    }

    // TODO: uncomment below
    private void addTouchEventToQueue(MotionEvent event, String tag, String activity) {
        Log.d("INSERT", "keyboard originated touch event");
//        Env.getInstance().getDb().insert(SingleTouchEvent.class, new SingleTouchEvent(event, true, tag, activity));

        this.pendingEventsForUpdate.add(new SingleTouchEvent(event, true, tag, activity));
    }

    public void updateTagAndPushAllEventsToToatalQueue(String tag){
        for(SingleTouchEvent event : this.pendingEventsForUpdate) {
            event.setTag(tag);
            Env.getInstance().getDb().insert(SingleTouchEvent.class, event);
        }

        this.pendingEventsForUpdate.clear();
    }

    public static void addKeyCodeEventToQueue(int keyCode, String label, long time) {
        Log.d("INSERT", "keyboard keycode");
        Env.getInstance().getDb().insert(KeyboardEvent.class, new KeyboardEvent(keyCode, label, time));
    }

    @Override
    public boolean onTouch(View v, MotionEvent me) {
        if(!SoftKeyboard. isInitialized()){
            return false;
        }

        String tag = v.getTag() == null ? "": ((String) v.getTag());
        String activity = this.softKeyboard.getActivityName();
        addTouchEventToQueue(me, tag, activity);
        return false;
    }

    public void hideSoftKeyboard(){
        this.softKeyboard.hideSoftKeyboard();
    }
}
