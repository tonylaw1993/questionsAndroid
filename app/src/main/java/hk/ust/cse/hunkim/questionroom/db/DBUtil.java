package hk.ust.cse.hunkim.questionroom.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;

/**
 * Created by hunkim on 7/15/15.
 */
public class DBUtil {
    SQLiteOpenHelper helper;

    public DBUtil(SQLiteOpenHelper helper) {
        this.helper = helper;
    }

    // add new entry to te database
    public long put(String key) {

        // Gets the data repository in write mode
        SQLiteDatabase db = helper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DBHelper.KEY_NAME, key);
        values.put(DBHelper.LIKE_NAME, false);
        values.put(DBHelper.DISLIKE_NAME, false);

        return db.insert(
                DBHelper.TABLE_NAME,
                DBHelper.KEY_NAME,
                values);
    }

    // add new entry to te database if it is not voted
    public long putVote(String key, int option) {

        // Gets the data repository in write mode
        SQLiteDatabase db = helper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DBHelper.KEY_NAME, key);
        values.put(DBHelper.VOTE_OPTION_NAME, option);

        return db.insert(
                DBHelper.VOTE_TABLE_NAME,
                DBHelper.KEY_NAME,
                values);
    }


    public boolean contains(String key) {
        // Gets the data repository in write mode
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT 1 FROM " + DBHelper.TABLE_NAME +
                        " WHERE " + DBHelper.KEY_NAME +
                        " = ?", new String[]{key});

        boolean exists = c.moveToFirst();
        c.close();
        return exists;
    }

    public boolean containsVote(String key) {
        // Gets the data repository in write mode
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT 1 FROM " + DBHelper.VOTE_TABLE_NAME +
                        " WHERE " + DBHelper.KEY_NAME +
                        " = ?", new String[]{key});

        boolean exists = c.moveToFirst();
        c.close();
        return exists;
    }

    public void delete(String key) {
        // Gets the data repository in write mode
        SQLiteDatabase db = helper.getWritableDatabase();

        // Define 'where' part of query.
        String selection = DBHelper.KEY_NAME + " = ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = {key};
        // Issue SQL statement.
        db.delete(DBHelper.TABLE_NAME, selection, selectionArgs);
    }

    public void deleteRoomVisitedHistory(){
        // Gets the data repository in write mode
        SQLiteDatabase db = helper.getWritableDatabase();

        // Define 'where' part of query.
        String selection = DBHelper.OPERATION_TYPE_NAME + " = 1";
        // Issue SQL statement.
        db.delete(DBHelper.HISTORY_TABLE_NAME, selection, null);
    }

    public void updateLikeStatus(String key, int toChange){
        // Gets the data repository in write mode
        SQLiteDatabase db = helper.getReadableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DBHelper.LIKE_NAME, (toChange==1)?true:false);

        // Issue SQL statement.
        db.update(DBHelper.TABLE_NAME, values, DBHelper.KEY_NAME + "='" + key + "'", null);
    }

    public void updateDislikeStatus(String key, int toChange){
        // Gets the data repository in write mode
        SQLiteDatabase db = helper.getReadableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DBHelper.DISLIKE_NAME, (toChange==1)?true:false);

        // Issue SQL statement.
        db.update(DBHelper.TABLE_NAME, values, DBHelper.KEY_NAME + "='" + key + "'", null);
    }

    public boolean getLikeStatus(String key){
        // Gets the data repository in write mode
        SQLiteDatabase db = helper.getReadableDatabase();
        boolean status = false;
        Cursor c = db.rawQuery(
                "SELECT " + DBHelper.LIKE_NAME + " FROM " + DBHelper.TABLE_NAME +
                        " WHERE " + DBHelper.KEY_NAME +
                        " = '" + key + "'", null);
        if(c.moveToFirst())
            status = c.getInt(c.getColumnIndex(DBHelper.LIKE_NAME))>0;
        c.close();
        return status;
    }

    public boolean getDislikeStatus(String key){
        // Gets the data repository in write mode
        SQLiteDatabase db = helper.getReadableDatabase();
        boolean status = false;
        Cursor c = db.rawQuery(
                "SELECT " + DBHelper.DISLIKE_NAME + " FROM " + DBHelper.TABLE_NAME +
                        " WHERE " + DBHelper.KEY_NAME +
                        " = '" + key + "'", null);
        if(c.moveToFirst())
            status = c.getInt(c.getColumnIndex(DBHelper.DISLIKE_NAME))>0;
        c.close();
        return status;
    }

    // record room name entered
    public void updateRoomEntry(String roomName){
        // Gets the data repository in write mode
        SQLiteDatabase db = helper.getReadableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DBHelper.OPERATION_TYPE_NAME, 1);
        values.put(DBHelper.ROOM_NAME, roomName);
        values.put(DBHelper.TIMESTAMP_NAME, new Date().getTime());

        db.insert(
                DBHelper.HISTORY_TABLE_NAME,
                null,
                values);
    }
    // return string array where col. 0 is room name col. 1 timestamp
    public String[] getRecentRoomName() {
        // Gets the data repository in write mode
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT DISTINCT tbl_name from sqlite_master where tbl_name = '"
                        + DBHelper.HISTORY_TABLE_NAME +"'", null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.close();
            }
            cursor.close();
        }else
             return null;

        Cursor c = db.rawQuery(
                    "SELECT DISTINCT " + DBHelper.ROOM_NAME
                            + " FROM " + DBHelper.HISTORY_TABLE_NAME
                            + " WHERE " + DBHelper.OPERATION_TYPE_NAME + " = 1 "
                            + " ORDER BY " + DBHelper.TIMESTAMP_NAME + " DESC", null);

        if(c == null)
            return null;
        String[] result = new String[c.getCount()];
        int i = 0;
        while(c.moveToNext()){
            result[i] = c.getString(c.getColumnIndex(DBHelper.ROOM_NAME));
            i++;
        }
        return result;
    }


}
