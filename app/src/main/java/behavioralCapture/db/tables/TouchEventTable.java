package behavioralCapture.db.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import java.util.Arrays;
import java.util.List;

import behavioralCapture.Utils.Jsonable;
import behavioralCapture.events.SingleTouchEvent;
import behavioralCapture.env.Env;

public class TouchEventTable implements Table<SingleTouchEvent>,BaseColumns {
    public TouchEventTable() {
    }

    public static final String TABLE_NAME = "TOUCH_EVENT";
    public static final String ID = BaseColumns._ID;
    public static final String ACTION = "TYPE";
    public static final String ACTION_STR = "ACTION_STR";
    public static final String X = "X";
    public static final String Y = "Y";
    public static final String DOWN_TIME = "DOWN_TIME";
    public static final String TIMESTAMP = "TIMESTAMP";
    public static final String PRESSURE = "PRESSURE";
    public static final String SIZE = "SIZE";
    public static final String CHUNK_ID = "CHUNK_ID";
    public static final String IS_REMOVED = "IS_REMOVED";
    public static final String TAG = "TAG";
    public static final String ACTIVITY = "ACTIVITY";


    public static final String[] COLUMNS = {ID, ACTION, ACTION_STR, X, Y, DOWN_TIME, TIMESTAMP,PRESSURE, SIZE,CHUNK_ID,IS_REMOVED,TAG,ACTIVITY};
    public static final List<String> COLUMNS_AS_LIST = Arrays.asList(COLUMNS);
    private static final SqliteColumnTypes[] COLUMN_SQL_TYPES = {
            SqliteColumnTypes.INTEGER,
            SqliteColumnTypes.INTEGER,
            SqliteColumnTypes.TEXT,
            SqliteColumnTypes.REAL,
            SqliteColumnTypes.REAL,
            SqliteColumnTypes.INTEGER,
            SqliteColumnTypes.INTEGER,
            SqliteColumnTypes.REAL,
            SqliteColumnTypes.REAL,
            SqliteColumnTypes.INTEGER,
            SqliteColumnTypes.INTEGER,
            SqliteColumnTypes.TEXT,
            SqliteColumnTypes.TEXT
    };
    private static final String[] COLUMN_MODIFIERS = {"PRIMARY KEY","","","","","","","","","",""};
    private static final int INDEX_OF_ID = COLUMNS_AS_LIST.indexOf(ID);
    private static final int INDEX_OF_ACTION = COLUMNS_AS_LIST.indexOf(ACTION);
    private static final int INDEX_OF_ACTION_STR = COLUMNS_AS_LIST.indexOf(ACTION_STR);
    private static final int INDEX_OF_X = COLUMNS_AS_LIST.indexOf(X);
    private static final int INDEX_OF_Y = COLUMNS_AS_LIST.indexOf(Y);
    public static final int INDEX_OF_DOWN_TIME = COLUMNS_AS_LIST.indexOf(DOWN_TIME);
    private static final int INDEX_OF_TIMESTAMP = COLUMNS_AS_LIST.indexOf(TIMESTAMP);
    private static final int INDEX_OF_PRESSURE = COLUMNS_AS_LIST.indexOf(PRESSURE);
    private static final int INDEX_OF_CHUNK_ID = COLUMNS_AS_LIST.indexOf(CHUNK_ID);
    private static final int INDEX_OF_SIZE = COLUMNS_AS_LIST.indexOf(SIZE);
    private static final int INDEX_OF_TAG = COLUMNS_AS_LIST.indexOf(TAG);
    private static final int INDEX_OF_ACTIVITY = COLUMNS_AS_LIST.indexOf(ACTIVITY);




    @Override
    public String getName() {
        return TABLE_NAME;
    }

    @Override
    public String[] getColumnNames() {
        return COLUMNS;
    }

    @Override
    public SqliteColumnTypes[] getColumnTypes() {
        return COLUMN_SQL_TYPES;
    }

    @Override
    public String[] getColumnModifiers() {
        return COLUMN_MODIFIERS;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onUpgrade() {

    }

    @Override
    public boolean dropOnCreate() {
        return true;
    }

    @Override
    public SingleTouchEvent cursorToEventObject(Cursor cursor, int joinOffset) {
        return new SingleTouchEvent(cursor.getInt(INDEX_OF_ACTION+joinOffset),cursor.getString(INDEX_OF_ACTION_STR+joinOffset),cursor.getFloat(INDEX_OF_X+joinOffset),
                cursor.getFloat(INDEX_OF_Y+joinOffset),cursor.getLong(INDEX_OF_TIMESTAMP+joinOffset),cursor.getLong(INDEX_OF_DOWN_TIME+joinOffset),cursor.getFloat(INDEX_OF_PRESSURE+joinOffset),
                cursor.getFloat(INDEX_OF_SIZE+joinOffset), cursor.getString(INDEX_OF_TAG + joinOffset), cursor.getString(INDEX_OF_ACTIVITY + joinOffset));
    }

    @Override
    public List<? extends Jsonable> selectFromDate(int limit, int offset, long time) {
        return Env.getInstance().getDb().selectTouchEvents(TouchEventTable.IS_REMOVED+"=0 AND "+TouchEventTable.CHUNK_ID+"=-1 AND "+TouchEventTable.DOWN_TIME+">=?",new String[]{""+time},offset+","+limit);
    }

    @Override
    public List<? extends Jsonable> selectAll(int limit, int offset) {
        return Env.getInstance().getDb().selectTouchEvents(TouchEventTable.IS_REMOVED+"=0 AND "+TouchEventTable.CHUNK_ID+"=-1",null,offset+","+limit);
    }



    @Override
    public long setIsRemoved(long startTime, long endTime,int limit) {
        ContentValues cv = new ContentValues();
        cv.put(TouchEventTable.IS_REMOVED, 1);
        String where = TouchEventTable.IS_REMOVED+"=0 AND "+TouchEventTable.DOWN_TIME+">="+startTime+
                " AND "+TouchEventTable.CHUNK_ID+"=-1"+
                ((endTime>0)?" AND "+TouchEventTable.DOWN_TIME+"<="+endTime:"");
        long l = Env.getInstance().getDb().getWritableDatabase().update(TouchEventTable.TABLE_NAME, cv, where,null);
        return l;
    }

}
