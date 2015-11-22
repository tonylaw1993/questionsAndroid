package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;

import hk.ust.cse.hunkim.questionroom.db.DBHelper;
import hk.ust.cse.hunkim.questionroom.db.DBUtil;


public class CreatePollActivity extends Activity {

    // TODO: change this to your own Firebase URL

    private static final String FIREBASE_URL = "https://android-questions.firebaseio.com/";

    private Firebase mFirebaseRef;
    private ValueEventListener mConnectedListener;
    private ReplyListAdapter mChatListAdapter;
    private String QroomName;
    private DBUtil dbutil;

    public DBUtil getDbutil() {
        return dbutil;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialized once with an Android context.
       Firebase.setAndroidContext(this);

        setContentView(R.layout.activity_createpoll);

        Intent intent = getIntent();
        assert (intent != null);

        QroomName = intent.getStringExtra(MainActivity.ROOM_NAME);

        if (QroomName == null || QroomName.length() == 0) {
            QroomName = "all";
        }

        setTitle("Room name: " + QroomName);

        //Setup our Firebase mFirebaseRef
        mFirebaseRef = new Firebase(FIREBASE_URL).child(QroomName).child("polls");
        // Setup our input methods. Enter key on the keyboard or pushing the send button
        //findViewById(R.id.sendpoll).setOnClickListener(new View.OnClickListener() {
            //@Override
            //public void onClick(View view) {

            //}
       // });

        // get the DB Helper
        DBHelper mDbHelper = new DBHelper(this);
        dbutil = new DBUtil(mDbHelper);
    }

    //@Override
   // public void onStart() {
        //super.onStart();


           // }

    //@Override
    //public void onStop() {
    //    super.onStop();
      //  mFirebaseRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
       // mChatListAdapter.cleanup();
   // }

    public void Close(View view) {
        finish();
    }
}