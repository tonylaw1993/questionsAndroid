package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import hk.ust.cse.hunkim.questionroom.db.DBHelper;
import hk.ust.cse.hunkim.questionroom.db.DBUtil;
import hk.ust.cse.hunkim.questionroom.question.Poll;


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


     private boolean isNullString(String s){
         if (s.length()==0){
             Toast.makeText(CreatePollActivity.this, "Content is Null ", Toast.LENGTH_SHORT).show();
             Toast.makeText(CreatePollActivity.this, "Please input again ", Toast.LENGTH_LONG).show();
             return true;
         }
         return false;
     }

    public void sendpoll(View view) {

        EditText polltitle = (EditText) findViewById(R.id.titleInput);
        String titleText = polltitle.getText().toString();
        if (isNullString(titleText)) {
            return;
        }

        ArrayList<String> content = new ArrayList<>();
        int[] ids = new int[]{R.id.polloption1, R.id.polloption2, R.id.polloption3, R.id.polloption4, R.id.polloption5};

        for (int id : ids) {
            EditText t = (EditText) findViewById(id);
            String Text = t.getText().toString();
            if (!(Text.length() == 0)) {
                content.add(Text);
            }
            t.setText("");
        }
        String[] options = new String[content.size()];
        options = content.toArray(options);





        Poll poll = new Poll(titleText, options);
//        for(int x =0; x < poll.getItems().size(); x++)
//        {System.out.println (poll.getItems().toString());}
        mFirebaseRef.push().setValue(poll);
        polltitle.setText("");
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