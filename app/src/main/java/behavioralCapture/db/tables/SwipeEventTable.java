package behavioralCapture.db.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.Arrays;
import java.util.List;

import behavioralCapture.Utils.Jsonable;
import behavioralCapture.env.Env;
import behavioralCapture.events.TouchEventChunk;

public class SwipeEventTable implements Table<TouchEventChunk>,BaseColumns {

    private SQLiteDatabase writableDatabase;

    public SwipeEventTable() {
    }


    public static final String TABLE_NAME = "SWIPE_EVENT";

    public static final String ID = BaseColumns._ID;
    public static final String TIMESTAMP = "TIMESTAMP";
    public static final String TYPE = "TYPE";
    public static final String IS_REMOVED = "IS_REMOVED";
    public static final String TAG = "TAG";
    public static final String ACTIVITY = "ACTIVITY";

    public static final String[] COLUMNS = {ID, TIMESTAMP, TYPE,IS_REMOVED,TAG,ACTIVITY};
    public static final List<String> COLUMNS_AS_LIST = Arrays.asList(COLUMNS);
    private static final SqliteColumnTypes[] COLUMN_SQL_TYPES = {
            SqliteColumnTypes.INTEGER,
            SqliteColumnTypes.INTEGER,
            SqliteColumnTypes.INTEGER,
            SqliteColumnTypes.INTEGER,
            SqliteColumnTypes.TEXT,
            SqliteColumnTypes.TEXT
    };
    private static final String[] COLUMN_MODIFIERS = {"PRIMARY KEY","","","","","","","","",""};
    private static final int INDEX_OF_ID = COLUMNS_AS_LIST.indexOf(ID);
    private static final int INDEX_OF_TIMESTAMP = COLUMNS_AS_LIST.indexOf(TIMESTAMP);

    private static final int INDEX_OF_IS_REMOVED = COLUMNS_AS_LIST.indexOf(IS_REMOVED);

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
    public TouchEventChunk cursorToEventObject(Cursor cursor, int joinOffset) {
        long currentId = cursor.getLong(INDEX_OF_ID);
        TouchEventChunk event = new TouchEventChunk(cursor.getInt(INDEX_OF_TIMESTAMP));
        while (!cursor.isAfterLast()) {
            if (cursor.getLong(INDEX_OF_ID)!=currentId) {
                cursor.moveToPrevious();
                break;
            }
            event.addEvent(new TouchEventTable().cursorToEventObject(cursor,COLUMNS.length));
            cursor.moveToNext();
        }
        return event;

    }

    @Override
    public List<? extends Jsonable> selectFromDate(int limit, int offset, long time) {
        return Env.getInstance().getDb().selectSwipeEvents(limit,offset,"t2."+TouchEventTable.IS_REMOVED+"=0 AND t2."+TouchEventTable.DOWN_TIME+">"+time+" AND "+TouchEventTable.CHUNK_ID+"<>-1");
    }

    @Override
    public List<? extends Jsonable> selectAll(int limit, int offset) {
        return Env.getInstance().getDb().selectSwipeEvents(limit,offset,"t2."+TouchEventTable.IS_REMOVED+"=0 AND "+TouchEventTable.CHUNK_ID+"<>-1");
    }

    @Override
    public long setIsRemoved(long startTime, long endTime, int limit) {
        ContentValues cv = new ContentValues();
        cv.put(TouchEventTable.IS_REMOVED, 1);
        String where = TouchEventTable.IS_REMOVED+"=0 AND "+TouchEventTable.DOWN_TIME+">="+startTime+
                " AND "+TouchEventTable.CHUNK_ID+"<>-1"+
                ((endTime>0)?" AND "+TouchEventTable.DOWN_TIME+"<="+endTime:"");
        long l = Env.getInstance().getDb().getWritableDatabase().update(TouchEventTable.TABLE_NAME, cv, where,null);
        return l;
    }
}
