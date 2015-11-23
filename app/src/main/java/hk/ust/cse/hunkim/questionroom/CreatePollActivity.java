package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import hk.ust.cse.hunkim.questionroom.db.DBHelper;
import hk.ust.cse.hunkim.questionroom.db.DBUtil;
import hk.ust.cse.hunkim.questionroom.question.Poll;


public class CreatePollActivity extends ActionBarActivity {

    // TODO: change this to your own Firebase URL

    private static final String FIREBASE_URL = "https://android-questions.firebaseio.com/";

    private Firebase mFirebaseRef;
    private ValueEventListener mConnectedListener;
    private ReplyListAdapter mChatListAdapter;
    private String QroomName;
    private DBUtil dbutil;
    Toolbar toolbar;
    public int optionnum = 2;

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

        // Creating The Toolbar and setting it as the Toolbar for the activity
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        // Setup our input methods. Enter key on the keyboard or pushing the send button
        //findViewById(R.id.sendPoll).setOnClickListener(new View.OnClickListener() {
            //@Override
            //public void onClick(View view) {

            //}
       // });

        setTitle("Create poll");
        // get the DB Helper
        DBHelper mDbHelper = new DBHelper(this);
        dbutil = new DBUtil(mDbHelper);

        Button gocreatepoll = (Button) findViewById(R.id.sendpoll);
        gocreatepoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPoll();
            }
        });


        Button btn = (Button) findViewById(R.id.morechoice);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOption();
            }
        });

        Button btn2 = (Button) findViewById(R.id.lesschoice);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteOption();
            }
        });





    }



     private boolean isNullString(String s){
         if (s.length()==0){
             Toast.makeText(CreatePollActivity.this, "Content is Null ", Toast.LENGTH_SHORT).show();
             Toast.makeText(CreatePollActivity.this, "Please input again ", Toast.LENGTH_LONG).show();
             return true;
         }
         return false;
     }


    public void addOption(){
        int num = optionnum;
        switch(num) {
            case(2):final LinearLayout e2 = (LinearLayout) findViewById(R.id.option3);
                e2.setVisibility(View.VISIBLE);
                optionnum++;
                break;
            case(3):final LinearLayout e3 = (LinearLayout) findViewById(R.id.option4);
                e3.setVisibility(View.VISIBLE);
                optionnum++;
                break;
            case(4):final LinearLayout e4 = (LinearLayout) findViewById(R.id.option5);
                e4.setVisibility(View.VISIBLE);
                optionnum++;
                break;
            case(5): Toast.makeText(CreatePollActivity.this, "At most five options ", Toast.LENGTH_LONG).show();
                break;
        }
    }



    public void deleteOption(){
        int num = optionnum;
        switch(num) {
            case(5):final LinearLayout e2 = (LinearLayout) findViewById(R.id.option5);
                e2.setVisibility(View.GONE);
                optionnum--;
                break;
            case(4):final LinearLayout e3 = (LinearLayout) findViewById(R.id.option4);
                e3.setVisibility(View.GONE);
                optionnum--;
                break;
            case(3):final LinearLayout e4 = (LinearLayout) findViewById(R.id.option3);
                e4.setVisibility(View.GONE);
                optionnum--;
                break;
            case(2): Toast.makeText(CreatePollActivity.this, "At least two options ", Toast.LENGTH_LONG).show();
                break;
        }
    }


    public void sendPoll() {

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
        mFirebaseRef.push().setValue(poll);
        polltitle.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_poll_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
      /*  if (id == R.id.action_settings) {
            return true;
        }*/

        switch (item.getItemId()) {
            case android.R.id.home:
                // this takes the user 'back', as if they pressed the left-facing triangle icon on the main android toolbar.
                // if this doesn't work as desired, another possibility is to call `finish()` here.
                onBackPressed();
                return true;
            case R.id.sendButton:
                sendPoll();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

//        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();
//        mFirebaseRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
//        mChatListAdapter.cleanup();
    }

    public void Close(View view) {
        finish();
    }
}