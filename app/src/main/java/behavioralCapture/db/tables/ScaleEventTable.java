package behavioralCapture.db.tables;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

import behavioralCapture.Utils.Jsonable;
import behavioralCapture.events.ScaleTouchEvent;
import behavioralCapture.env.Env;

/**
 * Created by thinkPAD on 10/14/2015.
 */
public class ScaleEventTable implements Table<ScaleTouchEvent>,BaseColumns{
    public ScaleEventTable() {
    }

    public static final String TABLE_NAME = "SCALE_EVENT";

    public static final String TYPE = "TYPE";
    public static final String ID = BaseColumns._ID;
    public static final String DOWN_TIME = "TIMESTAMP";
    public static final String IS_REMOVED = "IS_REMOVED";
    public static final String TAG = "TAG";
    public static final String ACTIVITY = "ACTIVITY";

    public static final String[] COLUMNS = {ID, DOWN_TIME, IS_REMOVED,TAG,ACTIVITY,TYPE};
    public static final List<String> COLUMNS_AS_LIST = Arrays.asList(COLUMNS);
    private static final SqliteColumnTypes[] COLUMN_SQL_TYPES = {
            SqliteColumnTypes.INTEGER,
            SqliteColumnTypes.INTEGER,
            SqliteColumnTypes.INTEGER,
            SqliteColumnTypes.INTEGER,
            SqliteColumnTypes.TEXT,
            SqliteColumnTypes.TEXT
    };
    private static final String[] COLUMN_MODIFIERS = {"PRIMARY KEY","","","","",""};

    private static final int INDEX_OF_ID = COLUMNS_AS_LIST.indexOf(ID);
    private static final int INDEX_OF_DOWN_TIME = COLUMNS_AS_LIST.indexOf(DOWN_TIME);
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
    public ScaleTouchEvent cursorToEventObject(Cursor cursor, int joinOffset) {
        long currentId = cursor.getLong(INDEX_OF_ID);
        ScaleTouchEvent event = new ScaleTouchEvent(cursor.getLong(INDEX_OF_DOWN_TIME));
        while (!cursor.isAfterLast()) {
            if (cursor.getLong(INDEX_OF_ID)!=currentId) {
                break;
            }
            event.addEvent(new DoubleTouchTable().cursorToEventObject(cursor,COLUMNS.length));
            cursor.moveToNext();
        }
        return event;
    }

    @Override
    public List<? extends Jsonable> selectFromDate(int limit, int offset, long time) {
        return Env.getInstance().getDb().selectScaleEvents(limit,offset,"t1."+ScaleEventTable.IS_REMOVED+"=0 AND t1."+ScaleEventTable.DOWN_TIME+">"+time);
    }

    @Override
    public List<? extends Jsonable> selectAll(int limit, int offset) {
        return Env.getInstance().getDb().selectScaleEvents(limit,offset,"t1."+ScaleEventTable.IS_REMOVED+"=0");
    }

    @Override
    public long setIsRemoved(long startTime, long endTime,int limit) {
        // need to remove only touchevents that have this foreign id between these times with the limit.
        SQLiteDatabase db = Env.getInstance().getDb().getWritableDatabase();
        String subQuery = "select "+ScaleEventTable.ID+" from "+ScaleEventTable.TABLE_NAME+" where "+ScaleEventTable.DOWN_TIME+">="+startTime;
        if (endTime>0) {
            subQuery += " and "+ScaleEventTable.DOWN_TIME+"<="+endTime;
        }
        String query = "update "+DoubleTouchTable.TABLE_NAME+" set "+DoubleTouchTable.IS_REMOVED+"=1 where "+DoubleTouchTable.SCALE_EVENT_ID+" IN ("+subQuery+")";
        db.rawQuery(query,null).close();
        Cursor cursor = db.rawQuery("SELECT changes() AS affected_row_count", null);
        long affectedRowCount=0;
        if(cursor != null && cursor.getCount() > 0 && cursor.moveToFirst())
        {
            affectedRowCount = cursor.getLong(cursor.getColumnIndex("affected_row_count"));
            Log.d("LOG", "affectedRowCount = " + affectedRowCount);
        }
        cursor.close();
        return affectedRowCount;
    }


}
