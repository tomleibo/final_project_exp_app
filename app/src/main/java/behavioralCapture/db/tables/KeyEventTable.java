package behavioralCapture.db.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.Arrays;
import java.util.List;

import behavioralCapture.Utils.Jsonable;
import behavioralCapture.softKeyboard.KeyboardEvent;
import behavioralCapture.env.Env;

/**
 * Created by thinkPAD on 10/12/2015.
 */
public class KeyEventTable implements Table<KeyboardEvent>,BaseColumns {

    public static final String TABLE_NAME = "KEY_EVENT";
    public static final String ID = BaseColumns._ID;
    public static final String ACTION = "TYPE";
    public static final String DOWN_TIME = "TIMESTAMP";
    public static final String KEY_CODE = "KEY_CODE";
    public static final String KEY_STR = "KEY_STR";
    public static final String IS_REMOVED = "IS_REMOVED";

    public static final String[] COLUMNS = {ID, ACTION, DOWN_TIME, KEY_CODE,KEY_STR,IS_REMOVED};
    public static final List<String> COLUMNS_AS_LIST = Arrays.asList(COLUMNS);
    private static final SqliteColumnTypes[] COLUMN_SQL_TYPES = {
            SqliteColumnTypes.INTEGER,
            SqliteColumnTypes.INTEGER,
            SqliteColumnTypes.INTEGER,
            SqliteColumnTypes.INTEGER,
            SqliteColumnTypes.TEXT,
            SqliteColumnTypes.INTEGER
    };
    private static final String[] COLUMN_MODIFIERS = {"PRIMARY KEY","","","","",""};
    private static final int INDEX_OF_ID = COLUMNS_AS_LIST.indexOf(ID);
    private static final int INDEX_OF_ACTION = COLUMNS_AS_LIST.indexOf(ACTION);
    private static final int INDEX_OF_DOWN_TIME = COLUMNS_AS_LIST.indexOf(DOWN_TIME);
    private static final int INDEX_OF_KEY_CODE = COLUMNS_AS_LIST.indexOf(KEY_CODE);
    private static final int INDEX_OF_KEY_STR= COLUMNS_AS_LIST.indexOf(KEY_STR);
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
    public KeyboardEvent cursorToEventObject(Cursor cursor, int joinOffset) {
        return new KeyboardEvent(cursor.getInt(INDEX_OF_ACTION),cursor.getLong(INDEX_OF_DOWN_TIME),
                cursor.getInt(INDEX_OF_KEY_CODE),cursor.getString(INDEX_OF_KEY_STR));
    }

    @Override
    public List<? extends Jsonable> selectFromDate(int limit, int offset, long time) {
        return Env.getInstance().getDb().selectKeyboardEvents(KeyEventTable.DOWN_TIME+">"+time+" AND "+KeyEventTable.IS_REMOVED+"=0",null,offset+","+limit);
    }

    @Override
    public List<? extends Jsonable> selectAll(int limit, int offset) {
        return Env.getInstance().getDb().selectKeyboardEvents(KeyEventTable.IS_REMOVED+"=0",null,offset+","+limit);
    }

    @Override
    public long setIsRemoved(long startTime,long endTime,int limit) {
        ContentValues cv = new ContentValues();
        cv.put(KeyEventTable.IS_REMOVED, 1);
        String where = KeyEventTable.IS_REMOVED+"=0 AND "+KeyEventTable.DOWN_TIME+">="+startTime+
                ((endTime>0)?" AND "+KeyEventTable.DOWN_TIME+"<="+endTime:"");
        SQLiteDatabase writableDatabase = Env.getInstance().getDb().getWritableDatabase();
        int result = writableDatabase.update(KeyEventTable.TABLE_NAME, cv, where, null);
        return result;

    }
}
