package hk.ust.cse.hunkim.questionroom;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.ListActivity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.KeyEvent;
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
import static hk.ust.cse.hunkim.questionroom.R.id.activity_reply;
import hk.ust.cse.hunkim.questionroom.question.Question;
/**
 * Created by LokHei on 2015/11/19.
 */

public class Tab1 extends ListFragment {

    private static final String FIREBASE_URL = "https://android-questions.firebaseio.com/";

    public static final String ROOM_NAME = "Room_name";
    private String roomName;
    private Firebase mFirebaseRef;
    private ValueEventListener mConnectedListener;
    private QuestionListAdapter mChatListAdapter;

    private DBUtil dbutil;

    public DBUtil getDbutil() {
        return dbutil;
    }

    public static Tab1 newInstance(String roomName) {
        Tab1 myFragment = new Tab1();

        Bundle args = new Bundle();
        args.putString(ROOM_NAME, roomName);

        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.tab_1,container,false);
        EditText inputText = (EditText) v.findViewById(R.id.messageInput);
        inputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    sendMessage();
                }
                return true;
            }
        });

       v.findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialized once with an Android context.
        Firebase.setAndroidContext(getActivity());

//        setContentView(R.layout.activity_main);

        roomName = getArguments().getString(ROOM_NAME);

        // Setup our Firebase mFirebaseRef
        mFirebaseRef = new Firebase(FIREBASE_URL).child(roomName).child("questions");

        // Setup our input methods. Enter key on the keyboard or pushing the send button

        // get the DB Helper
        DBHelper mDbHelper = new DBHelper(getActivity());
        dbutil = new DBUtil(mDbHelper);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
        final ListView listView = getListView();
        // Tell our list adapter that we only want 200 messages at a time
        mChatListAdapter = new QuestionListAdapter(
                mFirebaseRef.orderByChild("echo").limitToFirst(200),
                getActivity(), Tab1.this, R.layout.question, roomName);
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
                    Toast.makeText(getActivity(), "Connected to Firebase", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Disconnected from Firebase", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // No-op
            }
        });
    }

/*    @Override
    public void onStop() {
        super.onStop();
        mFirebaseRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
        mChatListAdapter.cleanup();
    }*/

    private  String FoulLanguageFilter (String s){
        if (s.length()==0){
            Toast.makeText(getActivity(), "No Content ! ", Toast.LENGTH_LONG).show();
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

        EditText inputTitle = (EditText) getView().findViewById(R.id.titleInput);
        EditText inputMsg = (EditText) getView().findViewById(R.id.messageInput);
        String inputTitleText = inputTitle.getText().toString();
        String inputMsgText = inputMsg.getText().toString();
        if ((inputTitleText.length()==0)||(inputMsgText.length()==0)){
            Toast.makeText(getActivity(), "Title/Content is/are Null ", Toast.LENGTH_SHORT).show();
            Toast.makeText(getActivity(), "Please input again ", Toast.LENGTH_LONG).show();
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
            Toast.makeText(getActivity(), "Title/Content: No foul language Please", Toast.LENGTH_LONG).show();
        }

        if (!inputMsgText.equals("") && !inputTitleText.equals("")) {
            if(inputMsgText.length()<3 || inputTitleText.length()<3){
                Toast.makeText(getActivity(), "Title/Content: too short", Toast.LENGTH_LONG).show();
            }else if(inputMsgText.length()>1024 || inputTitleText.length()>1024)
            {
                Toast.makeText(getActivity(), "Title/Content: too long", Toast.LENGTH_LONG).show();
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


}

