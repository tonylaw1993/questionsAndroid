package hk.ust.cse.hunkim.questionroom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import hk.ust.cse.hunkim.questionroom.CreatePollActivity;
import hk.ust.cse.hunkim.questionroom.JoinActivity;
import hk.ust.cse.hunkim.questionroom.R;
import hk.ust.cse.hunkim.questionroom.ReplyActivity;
import hk.ust.cse.hunkim.questionroom.SlidingTabLayout;
import hk.ust.cse.hunkim.questionroom.ViewPagerAdapter;
import hk.ust.cse.hunkim.questionroom.question.Question;


public class CreateQuestionActivity extends ActionBarActivity {

    // Declaring Your View and Variables
    private Firebase mFirebaseRef;
    private static final String FIREBASE_URL = "https://android-questions.firebaseio.com/";
    public static String MSG;
    public static String ROOM_NAME;

    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);


        setContentView(R.layout.activity_create_question);

        Intent intent = getIntent();
        assert (intent != null);

        // Make it a bit more reliable
        ROOM_NAME = intent.getStringExtra(MainActivity.ROOM_NAME);
        // Setup our Firebase mFirebaseRef
        mFirebaseRef = new Firebase(FIREBASE_URL).child(ROOM_NAME).child("questions");

        // Creating The Toolbar and setting it as the Toolbar for the activity
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        EditText inputText = (EditText) findViewById(R.id.messageInput);

        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
                onBackPressed();
            }
        });



    }

    private  String FoulLanguageFilter (String s){
        if (s.length()==0){
            Toast.makeText( CreateQuestionActivity.this, "No Content ! ", Toast.LENGTH_LONG).show();
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

        EditText inputTitle = (EditText) findViewById(R.id.titleInput);
        EditText inputMsg = (EditText) findViewById(R.id.messageInput);
        String inputTitleText = inputTitle.getText().toString();
        String inputMsgText = inputMsg.getText().toString();
        if ((inputTitleText.length()==0)||(inputMsgText.length()==0)){
            Toast.makeText( CreateQuestionActivity.this, "Title/Content is/are Null ", Toast.LENGTH_SHORT).show();
            Toast.makeText( CreateQuestionActivity.this, "Please input again ", Toast.LENGTH_LONG).show();
            return;
        }
        if (inputTitleText.length()>=2 ) {
            inputTitleText = Character.toUpperCase(inputTitleText.charAt(0)) + inputTitleText.substring(1);
        }
        if (inputMsgText.length()>=2 ) {
            inputMsgText = Character.toUpperCase(inputMsgText.charAt(0)) + inputMsgText.substring(1);
        }
        String tempTitle = new String(inputTitleText);
        String tempMsg =   new String(inputMsgText);
        inputTitleText = FoulLanguageFilter(inputTitleText);
        inputMsgText = FoulLanguageFilter(inputMsgText);


        if(   ! (tempTitle.equals(inputTitleText)) || !(tempMsg.equals(inputMsgText))) {
            Toast.makeText( CreateQuestionActivity.this, "Title/Content: No foul language Please", Toast.LENGTH_LONG).show();
        }

        if (!inputMsgText.equals("") && !inputTitleText.equals("")) {
            if(inputMsgText.length()<3 || inputTitleText.length()<3){
                Toast.makeText( CreateQuestionActivity.this, "Title/Content: too short", Toast.LENGTH_LONG).show();
            }else if(inputMsgText.length()>1024 || inputTitleText.length()>1024)
            {
                Toast.makeText( CreateQuestionActivity.this, "Title/Content: too long", Toast.LENGTH_LONG).show();
            }else {
                // Create our 'model', a Chat object
                Question question = new Question(inputTitleText, inputMsgText);
                // Create a new, auto-generated child of that chat location, and save our chat data there
                mFirebaseRef.push().setValue(question);
                inputTitle.setText("");
                inputMsg.setText("");
            }
        }
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