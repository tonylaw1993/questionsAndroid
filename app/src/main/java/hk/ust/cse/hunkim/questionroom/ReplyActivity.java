package hk.ust.cse.hunkim.questionroom;

import android.app.ListActivity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import hk.ust.cse.hunkim.questionroom.db.DBHelper;
import hk.ust.cse.hunkim.questionroom.db.DBUtil;
import hk.ust.cse.hunkim.questionroom.question.Question;
import hk.ust.cse.hunkim.questionroom.question.Reply;


public class ReplyActivity extends ListActivity {

    // TODO: change this to your own Firebase URL

    private static final String FIREBASE_URL = "https://android-questions.firebaseio.com/";


    private String roomName;
    private Firebase mFirebaseRef;
    private ValueEventListener mConnectedListener;
    private QuestionListAdapter mChatListAdapter;

    private DBUtil dbutil;

    public DBUtil getDbutil() {
        return dbutil;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialized once with an Android context.
        Firebase.setAndroidContext(this);

        setContentView(R.layout.activity_reply);

        Intent intent = getIntent();
        assert (intent != null);

        // Make it a bit more reliable
        roomName = intent.getStringExtra(JoinActivity.ROOM_NAME);
        if (roomName == null || roomName.length() == 0) {
            roomName = "all";
        }

        setTitle("Room name: " + roomName);

        // Setup our Firebase mFirebaseRef
        mFirebaseRef = new Firebase(FIREBASE_URL).child(roomName).child("replies");

        // Setup our input methods. Enter key on the keyboard or pushing the send button
        EditText inputText = (EditText) findViewById(R.id.replymsg);
        inputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    sendMessage();
                }
                return true;
            }
        });

        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        // get the DB Helper
        DBHelper mDbHelper = new DBHelper(this);
        dbutil = new DBUtil(mDbHelper);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
        final ListView listView = getListView();
        // Tell our list adapter that we only want 200 messages at a time
        //mChatListAdapter = new ReplyListAdapter(mFirebaseRef.orderByChild("echo").limitToFirst(200), this, R.layout.reply, roomName);
        listView.setAdapter(mChatListAdapter);

        mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(mChatListAdapter.getCount() - 1);
            }
        });

        // Finally, a little indication of connection status
        mConnectedListener = mFirebaseRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {
                    Toast.makeText(ReplyActivity.this, "Connected to Firebase", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ReplyActivity.this, "Disconnected from Firebase", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // No-op
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        mFirebaseRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
        mChatListAdapter.cleanup();
    }

    private  String FoulLanguageFilter (String s){
        if (s.length()==0){
            Toast.makeText(ReplyActivity.this, "No Content ! ", Toast.LENGTH_LONG).show();
        }
            String temp = s;
            String badwordStrings[] = {"fuck","shit","damn", "dick" ,"cocky","pussy","gayfag","asshole","bitch"};
            String goodwordStrings[] = {"love","oh my shirt","oh my god", "dragon" ,"lovely","badlady","handsome boy", "myfriend","badgirl"};
            for(int index = 0 ; index< badwordStrings.length ; index++){
                temp = temp.replaceAll( "(?i)"+badwordStrings[index] , goodwordStrings[index]);
            }
            temp = Character.toUpperCase(temp.charAt(0)) + temp.substring(1);
        return temp;
    }

    private void sendMessage() {

        EditText replying = (EditText) findViewById(R.id.replymsg);
        String replyMsgText = replying.getText().toString();
        if (replyMsgText.length()==0){
                        Toast.makeText(ReplyActivity.this, "Content is Null ", Toast.LENGTH_SHORT).show();
                        Toast.makeText(ReplyActivity.this, "Please input again ", Toast.LENGTH_LONG).show();
                        return;
                    }

        if (replyMsgText.length()>=2 ) {
        replyMsgText = Character.toUpperCase(replyMsgText.charAt(0)) + replyMsgText.substring(1);
        }

        String tempMsg =   new String(replyMsgText);

        replyMsgText = FoulLanguageFilter(replyMsgText);


        if(   !  (tempMsg.equals(replyMsgText))) {
            Toast.makeText(ReplyActivity.this, "Title/Content: No foul language Please", Toast.LENGTH_LONG).show();
        }

        if (!replyMsgText.equals("") ) {
            if(replyMsgText.length()<3 ){
                Toast.makeText(ReplyActivity.this, "Title/Content: too short", Toast.LENGTH_LONG).show();
            }else if(replyMsgText.length()>1024)
            {
                Toast.makeText(ReplyActivity.this, "Title/Content: too long", Toast.LENGTH_LONG).show();
            }else {
                // Create our 'model', a Chat object
                Reply reply = new Reply(replyMsgText);
                // Create a new, auto-generated child of that chat location, and save our chat data there
                mFirebaseRef.push().setValue(reply);
                replying.setText("");
            }
        }
    }

    //toChange is the number of like want to edit
    public void updateLike(String key, final int toChange) {
        if (!dbutil.contains(key)) {
            //create new entry for this key
            dbutil.put(key);
        }
        //update SQLite DB
        dbutil.updateLikeStatus(key, toChange);

        final Firebase echoRef = mFirebaseRef.child(key).child("echo");
        echoRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Long echoValue = (Long) dataSnapshot.getValue();
                        Log.e("Echo update:", "" + echoValue);

                        echoRef.setValue(echoValue + toChange);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                }
        );



        final Firebase orderRef = mFirebaseRef.child(key).child("order");
        orderRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Long orderValue = (Long) dataSnapshot.getValue();
                        Log.e("Order update:", "" + orderValue);

                        orderRef.setValue(orderValue - toChange);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                }
        );
    }

    public void updateDislike(String key, final int toChange) {
        if (!dbutil.contains(key)) {
            //create new entry for this key
            dbutil.put(key);
        }
        //update SQLite DB
        dbutil.updateDislikeStatus(key, toChange);

        final Firebase dislikeRef = mFirebaseRef.child(key).child("dislike");
        dislikeRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Long dislikeValue = (Long) dataSnapshot.getValue();
                        Log.e("Dislike update:", "" + dislikeValue);

                        dislikeRef.setValue(dislikeValue + toChange);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                }
        );

        final Firebase orderRef = mFirebaseRef.child(key).child("order");
        orderRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Long orderValue = (Long) dataSnapshot.getValue();
                        Log.e("Order update:", "" + orderValue);

                        orderRef.setValue(orderValue + toChange);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                }
        );

    }

    public void Close(View view) {
        finish();
    }
}