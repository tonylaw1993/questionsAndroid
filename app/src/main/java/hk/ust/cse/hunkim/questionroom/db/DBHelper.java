package hk.ust.cse.hunkim.questionroom.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hunkim on 7/15/15.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "questionList";
    public static final String HISTORY_TABLE_NAME = "history";
    public static final String ID_NAME = "_id";
    public static final String OPERATION_TYPE_NAME = "type";
    public static final String KEY_NAME = "key";
    public static final String LIKE_NAME = "likeStatus";
    public static final String DISLIKE_NAME = "dislikeStatus";
    public static final String ROOM_NAME = "roomName";
    public static final String TIMESTAMP_NAME = "timestamp";

    private static final String SQL_CREATE_ENTRIES1 =
            "CREATE TABLE " + TABLE_NAME + " (" + KEY_NAME + " TEXT UNIQUE,"
                                                + LIKE_NAME + " BOOLEAN,"
                                                + DISLIKE_NAME + " BOOLEAN);";
    private static final String SQL_CREATE_ENTRIES2 =
            "CREATE TABLE " + HISTORY_TABLE_NAME + " (" + ID_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                                                       + OPERATION_TYPE_NAME + " INTEGER," //1:enter room 2:post 3:reply
                                                       + ROOM_NAME + " TEXT,"
                                                       + KEY_NAME + " TEXT,"
                                                       + TIMESTAMP_NAME + " INTEGER);";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "questionList.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES1);
        db.execSQL(SQL_CREATE_ENTRIES2);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
