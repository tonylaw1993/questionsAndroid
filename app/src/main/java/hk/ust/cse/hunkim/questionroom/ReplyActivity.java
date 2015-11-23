package hk.ust.cse.hunkim.questionroom;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import hk.ust.cse.hunkim.questionroom.db.DBHelper;
import hk.ust.cse.hunkim.questionroom.db.DBUtil;
import hk.ust.cse.hunkim.questionroom.question.Question;
import hk.ust.cse.hunkim.questionroom.question.Reply;


public class ReplyActivity extends ActionBarActivity {

    // TODO: change this to your own Firebase URL

    private static final String FIREBASE_URL = "https://android-questions.firebaseio.com/";

    private String Qkey;
    private String Qtitle;
    private String Qmsg;
    private String QroomName;
    private String [] Qphotos;
    private Firebase mFirebaseRefReply;
    private Firebase mFirebaseRefQuestion;
    private ValueEventListener mConnectedListener;
    public static final String KEY = "key";
    public static final String TITLE = "title";
    public static final String MSG = "msg";
    public static final String ROOM_NAME = "room_name";

    private ReplyListAdapter mChatListAdapter;
    Context context;
    ListView listView;
    Toolbar toolbar;
    MainActivity m= new MainActivity();

    public String [] getQphotos(){ return Qphotos; }

    public void setQphotos(String [] Qphotos){ this.Qphotos = Qphotos; }
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

        QroomName = intent.getStringExtra(MainActivity.ROOM_NAME);
        Qkey = intent.getStringExtra(MainActivity.KEY);
        Qtitle = intent.getStringExtra(MainActivity.TITLE);
        Qmsg = intent.getStringExtra(MainActivity.MSG);
        MainActivity.DataHolder x = (MainActivity.DataHolder) intent.getSerializableExtra("photos");
        if(x.INSTANCE.hasData())
            Qphotos = Arrays.copyOf(x.INSTANCE.getData(), x.INSTANCE.getData().length);

        if (QroomName == null || QroomName.length() == 0) {
            QroomName = "all";
        }
        if (Qkey == null || Qkey.length() == 0) {
            Qkey = "all";
        }
        if (Qtitle  == null || Qtitle.length() == 0) {
            Qtitle = "all";
        }
        if (Qmsg == null || Qmsg.length() == 0) {
            Qmsg = "all";
        }

        // Creating The Toolbar and setting it as the Toolbar for the activity

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        setTitle("");
//        ((TextView) this.findViewById(R.id.qtitle)).setText(Qtitle);
//        ((TextView) this.findViewById(R.id.qmsg)).setText(Qmsg);
        // Setup our Firebase mFirebaseRefReply
        mFirebaseRefReply = new Firebase(FIREBASE_URL).child(QroomName).child("replies");
        mFirebaseRefQuestion = new Firebase(FIREBASE_URL).child(QroomName).child("questions").child(Qkey);


        // Setup our input methods. Enter key on the keyboard or pushing the send button
        EditText inputText = (EditText) findViewById(R.id.replyingmsg);
        inputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    replyMessage();
                }
                return true;
            }
        });

        findViewById(R.id.sendreply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replyMessage();
            }
        });

        // get the DB Helper
        DBHelper mDbHelper = new DBHelper(this);
        dbutil = new DBUtil(mDbHelper);

        listView = (ListView) findViewById(android.R.id.list);
        View header = getLayoutInflater().inflate(R.layout.reply_question_content, listView, false);
        listView.addHeaderView(header, null, false);
        ((TextView) findViewById(R.id.reply_question_title)).setText(Qtitle);
        ((TextView) findViewById(R.id.reply_question_msg)).setText(Qmsg);

        findViewById(R.id.replyImageHorizontalScrollView).setVisibility(View.GONE);
        if(x.INSTANCE.hasData()) {
            for (int i = 0; i < Qphotos.length; i++) {
                findViewById(R.id.replyImageHorizontalScrollView).setVisibility(View.VISIBLE);
                LinearLayout imageScrollView = (LinearLayout) findViewById(R.id.replyImageInScroll);
                ImageView imageView = new ImageView(getApplicationContext());
                String eString = Qphotos[i].substring(23);
                imageView.setImageBitmap(m.decodeBase64(eString));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(10, 10, 10, 10);
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageScrollView.getHeight(),
//                    imageScrollView.getHeight());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(400, 400);
                imageView.setLayoutParams(params);
//            imageScrollView.setBackgroundColor(Color.BLACK);
                imageScrollView.addView(imageView);

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

//    Object jj;
    @Override
    public void onStart() {
        super.onStart();



        ImageButton likeButton = (ImageButton) findViewById(R.id.like_reply_question);
        likeButton.setSelected(dbutil.getLikeStatus(Qkey));
        likeButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        ImageButton questionDislikeButton = (ImageButton) ((LinearLayout) view.getParent()).findViewById(R.id.dislike_reply_question);
                        if (view.isSelected()) { // unlike when selected
                            final Firebase echoRef = mFirebaseRefQuestion.child("echo");
                            echoRef.addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Long echoValue = (Long) dataSnapshot.getValue();
                                            Log.e("Echo update:", "" + echoValue);

                                            //update SQLite DB
                                            dbutil.updateLikeStatus(Qkey, -1);
                                            echoRef.setValue(echoValue - 1);
                                            findViewById(R.id.like_reply_question).setSelected(false);
                                        }

                                        @Override
                                        public void onCancelled(FirebaseError firebaseError) {
                                        }
                                    }
                            );

                        } else if (questionDislikeButton.isSelected()) { // another dislike button is selected before

                            final Firebase echoRef = mFirebaseRefQuestion.child("echo");
                            echoRef.addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Long echoValue = (Long) dataSnapshot.getValue();
                                            Log.e("echo update:", "" + echoValue);

                                            //update SQLite DB
                                            dbutil.updateLikeStatus(Qkey, 1);
                                            echoRef.setValue(echoValue + 1);
                                            findViewById(R.id.like_reply_question).setSelected(true);
                                        }

                                        @Override
                                        public void onCancelled(FirebaseError firebaseError) {
                                        }
                                    }
                            );

                            final Firebase dislikeRef = mFirebaseRefQuestion.child("dislike");
                            echoRef.addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Long dislikeValue = (Long) dataSnapshot.getValue();
                                            Log.e("dislike update:", "" + dislikeValue);

                                            //update SQLite DB
                                            dbutil.updateDislikeStatus(Qkey, -1);
                                            dislikeRef.setValue(dislikeValue - 1);
                                            findViewById(R.id.dislike_reply_question).setSelected(false);
                                        }

                                        @Override
                                        public void onCancelled(FirebaseError firebaseError) {
                                        }
                                    }
                            );

                        } else { //both like and dislike button are not selected before

                            final Firebase echoRef = mFirebaseRefQuestion.child("echo");
                            echoRef.addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Long echoValue = (Long) dataSnapshot.getValue();
                                            Log.e("echo update:", "" + echoValue);

                                            if (!dbutil.contains(Qkey)) {
                                                //create new entry for this key
                                                dbutil.put(Qkey);
                                            }
                                            //update SQLite DB
                                            dbutil.updateLikeStatus(Qkey, 1);
                                            echoRef.setValue(echoValue + 1);
                                            findViewById(R.id.like_reply_question).setSelected(true);
                                        }

                                        @Override
                                        public void onCancelled(FirebaseError firebaseError) {
                                        }
                                    }
                            );

                        }
                    }
                }

        );


        ImageButton dislikeButton = (ImageButton) findViewById(R.id.dislike_reply_question);
        dislikeButton.setSelected(dbutil.getLikeStatus(Qkey));
        dislikeButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        ImageButton questionLikeButton = (ImageButton) ((LinearLayout) view.getParent()).findViewById(R.id.like_reply_question);
                        if (view.isSelected()) { // unlike when selected
                            final Firebase dislikeRef = mFirebaseRefQuestion.child("dislike");
                            dislikeRef.addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Long dislikeValue = (Long) dataSnapshot.getValue();
                                            Log.e("Dislike update:", "" + dislikeValue);

                                            //update SQLite DB
                                            dbutil.updateDislikeStatus(Qkey, -1);
                                            dislikeRef.setValue(dislikeValue - 1);
                                            findViewById(R.id.dislike_reply_question).setSelected(false);
                                        }

                                        @Override
                                        public void onCancelled(FirebaseError firebaseError) {
                                        }
                                    }
                            );

                        } else if (questionLikeButton.isSelected()) { // another dislike button is selected before

                            final Firebase echoRef = mFirebaseRefQuestion.child("echo");
                            echoRef.addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Long echoValue = (Long) dataSnapshot.getValue();
                                            Log.e("echo update:", "" + echoValue);

                                            //update SQLite DB
                                            dbutil.updateLikeStatus(Qkey, -1);
                                            echoRef.setValue(echoValue - 1);
                                            findViewById(R.id.like_reply_question).setSelected(false);
                                        }

                                        @Override
                                        public void onCancelled(FirebaseError firebaseError) {
                                        }
                                    }
                            );

                            final Firebase dislikeRef = mFirebaseRefQuestion.child("dislike");
                            echoRef.addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Long dislikeValue = (Long) dataSnapshot.getValue();
                                            Log.e("dislike update:", "" + dislikeValue);

                                            //update SQLite DB
                                            dbutil.updateDislikeStatus(Qkey, 1);
                                            dislikeRef.setValue(dislikeValue + 1);
                                            findViewById(R.id.dislike_reply_question).setSelected(true);
                                        }

                                        @Override
                                        public void onCancelled(FirebaseError firebaseError) {
                                        }
                                    }
                            );

                        } else { //both like and dislike button are not selected before

                            final Firebase dislikeRef = mFirebaseRefQuestion.child("dislike");
                            dislikeRef.addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Long dislikeValue = (Long) dataSnapshot.getValue();
                                            Log.e("dislike update:", "" + dislikeValue);

                                            if (!dbutil.contains(Qkey)) {
                                                //create new entry for this key
                                                dbutil.put(Qkey);
                                            }
                                            //update SQLite DB
                                            dbutil.updateDislikeStatus(Qkey, 1);
                                            dislikeRef.setValue(dislikeValue + 1);
                                            findViewById(R.id.dislike_reply_question).setSelected(true);
                                        }

                                        @Override
                                        public void onCancelled(FirebaseError firebaseError) {
                                        }
                                    }
                            );

                        }
                    }
                }

        );

        // Attach an listener to read the data at our posts reference
        mFirebaseRefQuestion.child("echo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    long likeNumber = (long) dataSnapshot.getValue();
                    ((TextView) findViewById(R.id.like_number_reply_question)).setText(likeNumber+"");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
        mFirebaseRefQuestion.child("dislike").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long dislikeNumber = (long) dataSnapshot.getValue();
                ((TextView) findViewById(R.id.dislike_number_reply_question)).setText(dislikeNumber+"");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
        mFirebaseRefQuestion.child("numReply").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long numReplyNumber = (long) dataSnapshot.getValue();
                ((TextView) findViewById(R.id.reply_question_number)).setText(numReplyNumber+"");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        mFirebaseRefQuestion.child("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long timestampNumber = (long) dataSnapshot.getValue();
                String timedisplay = DateUtils.getRelativeTimeSpanString(timestampNumber, new Date().getTime(), 0, 262144).toString();
                ((TextView) findViewById(R.id.timedisplay)).setText(timedisplay+"");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        // Tell our list adapter that we only want 200 messages at a time
        mChatListAdapter = new ReplyListAdapter (mFirebaseRefReply.orderByChild("parentid").equalTo(Qkey).limitToFirst(200), this, R.layout.reply, QroomName);


        listView.setAdapter(mChatListAdapter);

        mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(mChatListAdapter.getCount() - 1);
            }
        });

        // Finally, a little indication of connection status
        mConnectedListener = mFirebaseRefReply.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {
//                    Toast.makeText(ReplyActivity.this, "Enjoy ! ", Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(ReplyActivity.this, "Please wait", Toast.LENGTH_LONG).show();
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
        mFirebaseRefReply.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
        mChatListAdapter.cleanup();
        m = null;
    }

    private  String FoulLanguageFilter (String s){
        if (s.length()==0){
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.coordinatorLayoutReply), "No content in comment", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.RED);
            snackbar.show();
        }
            String temp = s;
            String badwordStrings[] = {"fuck","shit","damn", "dick" ,"cocky","pussy","gayfag","asshole","bitch"};
            String goodwordStrings[] = {"love","oh my shirt","oh my god", "dragon" ,"lovely","badlady","handsome boy", "myfriend","badgirl"};
            for(int index = 0 ; index< badwordStrings.length ; index++){
                temp = temp.replaceAll( "(?i)"+badwordStrings[index] , goodwordStrings[index]);
            }
        return temp;
    }

    private void replyMessage() {

        EditText replying = (EditText) findViewById(R.id.replyingmsg);
        String replyMsgText = replying.getText().toString();

        String tempMsg = new String(replyMsgText);

        replyMsgText = FoulLanguageFilter(replyMsgText);


        if(   !  (tempMsg.equals(replyMsgText))) {
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.coordinatorLayoutReply), "Title/Content: No foul language Please", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.RED);
            snackbar.show();

        }

        if (!replyMsgText.equals("") ) {
            if(replyMsgText.length()<3 ){

                    Snackbar snackbar = Snackbar
                            .make(findViewById(R.id.coordinatorLayoutReply),"Title/Content: too short", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.RED);
                    snackbar.show();

            } else if(replyMsgText.length()>1024)
            {
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.coordinatorLayoutReply), "Title/Content: too long", Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.RED);
                snackbar.show();

            }else {
                // Create our 'model', a Chat object
                Reply reply = new Reply(replyMsgText, Qkey);
                // Create a new, auto-generated child of that chat location, and save our chat data there
                mFirebaseRefReply.push().setValue(reply);
                replying.setText("");

                Firebase mFirebaseQuestionRef = new Firebase(FIREBASE_URL).child(QroomName).child("questions");
                final Firebase numReplyRef = mFirebaseQuestionRef.child(Qkey).child("numReply");
                numReplyRef.addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Long numReplyValue = (Long) dataSnapshot.getValue();
                                Log.e("Like update:", "" + numReplyValue);

                                numReplyRef.setValue(numReplyValue + 1);
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        }
                );
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

        final Firebase likeRef = mFirebaseRefReply.child(key).child("like");
        likeRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Long likeValue = (Long) dataSnapshot.getValue();
                        Log.e("Like update:", "" + likeValue);

                        likeRef.setValue(likeValue + toChange);
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

        final Firebase dislikeRef = mFirebaseRefReply.child(key).child("dislike");
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

       

    }

    public void Close(View view) {
        finish();
    }
}