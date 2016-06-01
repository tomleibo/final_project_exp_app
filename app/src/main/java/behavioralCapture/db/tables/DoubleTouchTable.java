package behavioralCapture.db.tables;

import android.database.Cursor;
import android.provider.BaseColumns;

import java.util.Arrays;
import java.util.List;

import behavioralCapture.events.DoubleTouchEvent;
import behavioralCapture.Utils.Jsonable;

/**
 * Created by thinkPAD on 10/21/2015.
 */
public class DoubleTouchTable implements Table<DoubleTouchEvent>,BaseColumns {
    public DoubleTouchTable() {
    }

    public static final String TABLE_NAME = "DOUBLE_TOUCH_EVENT";
    public static final String ID = BaseColumns._ID;
    public static final String ACTION = "TYPE";
    public static final String ACTION_STR = "ACTION_STR";
    public static final String X0 = "X0";
    public static final String X1 = "X1";
    public static final String Y0 = "Y0";
    public static final String Y1 = "Y1";
    public static final String DOWN_TIME = "DOWN_TIME";
    public static final String TIMESTAMP = "TIMESTAMP";
    public static final String PRESSURE0 = "PRESSURE0";
    public static final String PRESSURE1 = "PRESSURE1";
    public static final String SIZE0 = "SIZE0";
    public static final String SIZE1 = "SIZE1";
    public static final String SCALE_EVENT_ID = "SCALE_EVENT_ID";
    public static final String IS_REMOVED = "IS_REMOVED";
    public static final String TAG = "TAG";
    public static final String ACTIVITY = "ACTIVITY";


    public static final String[] COLUMNS = {ID, ACTION, ACTION_STR, X0, X1,Y0,Y1, DOWN_TIME, TIMESTAMP,PRESSURE0,
            PRESSURE1, SIZE0,SIZE1,SCALE_EVENT_ID,IS_REMOVED,TAG,ACTIVITY};
    public static final List<String> COLUMNS_AS_LIST = Arrays.asList(COLUMNS);
    private static final SqliteColumnTypes[] COLUMN_SQL_TYPES = {
            SqliteColumnTypes.INTEGER,
            SqliteColumnTypes.INTEGER,
            SqliteColumnTypes.TEXT,
            SqliteColumnTypes.REAL,
            SqliteColumnTypes.REAL,
            SqliteColumnTypes.REAL,
            SqliteColumnTypes.REAL,
            SqliteColumnTypes.INTEGER,
            SqliteColumnTypes.INTEGER,
            SqliteColumnTypes.REAL,
            SqliteColumnTypes.REAL,
            SqliteColumnTypes.REAL,
            SqliteColumnTypes.REAL,
            SqliteColumnTypes.INTEGER,
            SqliteColumnTypes.INTEGER,
            SqliteColumnTypes.TEXT,
            SqliteColumnTypes.TEXT
    };
    private static final String[] COLUMN_MODIFIERS = {"PRIMARY KEY","","","","","","","","","","",""," default false ","",""};
    private static final int INDEX_OF_ID = COLUMNS_AS_LIST.indexOf(ID);
    private static final int INDEX_OF_ACTION = COLUMNS_AS_LIST.indexOf(ACTION);
    private static final int INDEX_OF_ACTION_STR = COLUMNS_AS_LIST.indexOf(ACTION_STR);
    private static final int INDEX_OF_X0 = COLUMNS_AS_LIST.indexOf(X0);
    private static final int INDEX_OF_X1 = COLUMNS_AS_LIST.indexOf(X1);
    private static final int INDEX_OF_Y0 = COLUMNS_AS_LIST.indexOf(Y0);
    private static final int INDEX_OF_Y1 = COLUMNS_AS_LIST.indexOf(Y1);
    public static final int INDEX_OF_DOWN_TIME = COLUMNS_AS_LIST.indexOf(DOWN_TIME);
    private static final int INDEX_OF_TIMESTAMP = COLUMNS_AS_LIST.indexOf(TIMESTAMP);
    private static final int INDEX_OF_SIZE0 = COLUMNS_AS_LIST.indexOf(SIZE0);
    private static final int INDEX_OF_SIZE1 = COLUMNS_AS_LIST.indexOf(SIZE1);
    private static final int INDEX_OF_PRESSURE0 = COLUMNS_AS_LIST.indexOf(PRESSURE0);
    private static final int INDEX_OF_PRESSURE1 = COLUMNS_AS_LIST.indexOf(PRESSURE1);
    private static final int INDEX_OF_SCALE_EVENT_ID = COLUMNS_AS_LIST.indexOf(SCALE_EVENT_ID);
    private static final int INDEX_OF_IS_REMOVED = COLUMNS_AS_LIST.indexOf(IS_REMOVED);
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
    public DoubleTouchEvent cursorToEventObject(Cursor cursor,int joinOffset) {
        return new DoubleTouchEvent(cursor.getInt(INDEX_OF_ACTION + joinOffset),
                cursor.getString(INDEX_OF_ACTION_STR + joinOffset),
                cursor.getFloat(INDEX_OF_X0 + joinOffset),
                cursor.getFloat(INDEX_OF_X1+joinOffset),
                cursor.getFloat(INDEX_OF_Y0+joinOffset),
                cursor.getFloat(INDEX_OF_Y1 + joinOffset),
                cursor.getFloat(INDEX_OF_PRESSURE0+joinOffset),
                cursor.getFloat(INDEX_OF_PRESSURE1+joinOffset),
                cursor.getFloat(INDEX_OF_SIZE0+joinOffset),
                cursor.getFloat(INDEX_OF_SIZE1 + joinOffset),
                cursor.getLong(INDEX_OF_DOWN_TIME + joinOffset),
                cursor.getLong(INDEX_OF_TIMESTAMP + joinOffset),
                cursor.getString(INDEX_OF_TAG + joinOffset),
                cursor.getString(INDEX_OF_ACTIVITY + joinOffset)
                );
    }

    @Override
    public List<? extends Jsonable> selectFromDate(int limit, int offset, long time) {
        throw new RuntimeException("not to be queried by itself");
    }

    @Override
    public List<? extends Jsonable> selectAll(int limit, int offset) {
        throw new RuntimeException("not to be queried by itself");
    }

    @Override
    public long setIsRemoved(long startTime, long endTime,int limit) {
        throw new RuntimeException("not to be queried by itself");
    }


}
