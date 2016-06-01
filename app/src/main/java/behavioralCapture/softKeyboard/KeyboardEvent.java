package behavioralCapture.softKeyboard;

import android.view.KeyEvent;

import com.google.gson.Gson;

import behavioralCapture.Utils.Jsonable;
import behavioralCapture.db.tables.Table;

/**
 * Created by user1 on 11/10/2015.
 */
// TODO: remove type (and maybe key_str also) from here
public class KeyboardEvent implements Jsonable {
    private final int type;
    private final long timestamp;
    private final int keyCode;
    private final String keyStr;


    public int getType() {
        return type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public String getKeyStr() {
        return keyStr;
    }

    public KeyboardEvent(int type, long timestamp, int keyCode, String keyStr) {
        this.type = Table.TOUCH;
        this.timestamp = timestamp;
        this.keyCode = keyCode;
        this.keyStr = keyStr;
    }

    public KeyboardEvent(int keyCode, String label, long timestamp) {
        this.timestamp = timestamp;
        this.keyCode = keyCode;
        this.type = Table.TOUCH;
        this.keyStr = label;
    }

    public KeyboardEvent(KeyEvent event) {
        this.type = Table.TOUCH;
        this.keyCode = event.getKeyCode();
        this.keyStr = KeyEvent.keyCodeToString(keyCode);
        this.timestamp = event.getDownTime();
    }

    @Override
    public String toJson(Gson gson) {
        return gson.toJson(this);
    }

    @Override
    public long getTime() {
        return timestamp;
    }
}
