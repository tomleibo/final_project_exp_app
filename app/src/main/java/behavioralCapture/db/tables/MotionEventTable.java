package behavioralCapture.db.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.Arrays;
import java.util.List;

import behavioralCapture.Utils.Jsonable;
import behavioralCapture.events.MotionSensorEventWrapper;
import behavioralCapture.env.Env;

public class MotionEventTable implements Table<MotionSensorEventWrapper>,BaseColumns{
    public static final String TABLE_NAME = "MOTION_EVENT";
    public static final String ID = BaseColumns._ID;
    public static final String TYPE = "TYPE";
    public static final String TIMESTAMP = "TIME";
    public static final String X = "X";
    public static final String Y = "Y";
    public static final String Z = "Z";
    public static final String ACCURACY = "ACCURACY";
    public static final String IS_REMOVED = "IS_REMOVED";

    private static final String[] COLUMNS = {ID, TYPE, TIMESTAMP, X, Y, Z, ACCURACY,IS_REMOVED};
    public static final List<String> COLUMNS_AS_LIST = Arrays.asList(COLUMNS);
    private static final SqliteColumnTypes[] COLUMN_SQL_TYPES = {SqliteColumnTypes.INTEGER, SqliteColumnTypes.INTEGER, SqliteColumnTypes.INTEGER
            , SqliteColumnTypes.REAL, SqliteColumnTypes.REAL, SqliteColumnTypes.REAL, SqliteColumnTypes.INTEGER, SqliteColumnTypes.INTEGER};
    private static final String[] COLUMN_MODIFIERS = {"PRIMARY KEY","","","","","","","","","default 0"};
    private static final int INDEX_OF_ID = COLUMNS_AS_LIST.indexOf(ID);
    private static final int INDEX_OF_X = COLUMNS_AS_LIST.indexOf(X);
    private static final int INDEX_OF_Y = COLUMNS_AS_LIST.indexOf(Y);
    private static final int INDEX_OF_Z = COLUMNS_AS_LIST.indexOf(Z);
    private static final int INDEX_OF_TIMESTAMP = COLUMNS_AS_LIST.indexOf(TIMESTAMP);
    private static final int INDEX_OF_TYPE = COLUMNS_AS_LIST.indexOf(TYPE);
    private static final int INDEX_OF_ACCURACY = COLUMNS_AS_LIST.indexOf(ACCURACY);
    private SQLiteDatabase writableDatabase;


    public MotionEventTable() {
    }

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
    public MotionSensorEventWrapper cursorToEventObject(Cursor c, int joinOffset) {
        return new MotionSensorEventWrapper(c.getInt(INDEX_OF_TYPE),new float[]{c.getFloat(INDEX_OF_X),
                c.getFloat(INDEX_OF_Y),c.getFloat(INDEX_OF_Z)},c.getLong(INDEX_OF_TIMESTAMP),c.getInt(INDEX_OF_ACCURACY));
    }

    @Override
    public List<? extends Jsonable> selectFromDate(int limit, int offset, long time) {
        return Env.getInstance().getDb().getAllMotionEvents(MotionEventTable.TIMESTAMP+">"+time+" AND "+MotionEventTable.IS_REMOVED+"=0",new String[]{},offset+","+limit);
    }

    @Override
    public List<? extends Jsonable> selectAll(int limit, int offset) {
        return Env.getInstance().getDb().getAllMotionEvents(MotionEventTable.IS_REMOVED+"=0",null,offset+","+limit);
    }

    @Override
    public long setIsRemoved(long startTime, long endTime,int limit) {
        ContentValues cv = new ContentValues();
        cv.put(MotionEventTable.IS_REMOVED, 1);
        String where = MotionEventTable.IS_REMOVED+"=0 AND "+MotionEventTable.TIMESTAMP+">="+startTime+
                ((endTime>0)?" AND "+MotionEventTable.TIMESTAMP+"<="+endTime:"");
        writableDatabase = Env.getInstance().getDb().getWritableDatabase();
        int result = writableDatabase.update(MotionEventTable.TABLE_NAME, cv, where, null);
        return result;
    }

}
