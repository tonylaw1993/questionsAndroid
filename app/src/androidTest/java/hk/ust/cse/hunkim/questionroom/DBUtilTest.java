package hk.ust.cse.hunkim.questionroom;

import android.test.AndroidTestCase;

import hk.ust.cse.hunkim.questionroom.db.DBHelper;
import hk.ust.cse.hunkim.questionroom.db.DBUtil;

/**
 * Created by hunkim on 7/15/15.
 */
public class DBUtilTest extends AndroidTestCase {

    DBUtil dbutil;

    public DBUtilTest() {
        super();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // get the DB Helper
        DBHelper mDbHelper = new DBHelper(getContext());
        dbutil = new DBUtil(mDbHelper);
    }

    public void testPut () {
        String key = "1234";
        dbutil.put(key);
        assertTrue("Put the key", dbutil.contains(key));

        dbutil.delete(key);

        assertFalse("Key is deleted!", dbutil.contains(key));
    }

    public void testUpdateLikeStatus(){
        String key = "1234";
        dbutil.put(key);
        dbutil.updateLikeStatus(key, 1);
        assertTrue("testGetLikeStatus is failed", dbutil.getLikeStatus(key));
        dbutil.updateLikeStatus(key, 0);
        assertFalse("testGetLikeStatus is failed", dbutil.getLikeStatus(key));
    }

    public void testUpdateDislikeStatus(){
        String key = "1234";
        dbutil.put(key);
        dbutil.updateDislikeStatus(key, 1);
        assertTrue("testGetLikeStatus is failed", dbutil.getDislikeStatus(key));
        dbutil.updateDislikeStatus(key, 0);
        assertFalse("testGetLikeStatus is failed", dbutil.getDislikeStatus(key));
    }

    public void testGetLikeStatus(){
        String key = "1234";
        dbutil.put(key);
        dbutil.updateLikeStatus(key, 1);
        assertTrue("testGetLikeStatus is failed", dbutil.getLikeStatus(key));
        dbutil.updateLikeStatus(key, 0);
        assertFalse("testGetLikeStatus is failed", dbutil.getLikeStatus(key));
        key = "keyNotExist";
        assertFalse("testGetLikeStatus is failed", dbutil.getLikeStatus(key));
    }

    public void testGetDislikeStatus(){
        String key = "1234";
        dbutil.put(key);
        dbutil.updateDislikeStatus(key, 1);
        assertTrue("testGetLikeStatus is failed", dbutil.getDislikeStatus(key));
        dbutil.updateDislikeStatus(key, 0);
        assertFalse("testGetLikeStatus is failed", dbutil.getDislikeStatus(key));
        key = "keyNotExist";
        assertFalse("testGetLikeStatus is failed", dbutil.getDislikeStatus(key));
    }

    public void testGetRecentRoomName(){
        dbutil.deleteRoomVisitedHistory();
        assertEquals("testGetRecentRoomName is failed", 0, dbutil.getRecentRoomName().length);
        dbutil.updateRoomEntry("ABCRoom");
        assertEquals("testGetRecentRoomName is failed", "ABCRoom", dbutil.getRecentRoomName()[0]);
    }
}
