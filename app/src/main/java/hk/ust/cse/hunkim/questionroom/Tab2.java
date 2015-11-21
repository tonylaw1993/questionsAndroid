package hk.ust.cse.hunkim.questionroom;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import hk.ust.cse.hunkim.questionroom.question.Question;
/**
 * Created by LokHei on 2015/11/19.
 */


public class Tab2 extends ListFragment {
    private static final String FIREBASE_URL = "https://android-questions.firebaseio.com/";

    public static final String ROOM_NAME = "Room_name";
    private String roomName;
    private Firebase mFirebaseRef;
    private ValueEventListener mConnectedListener;
    private PollListAdapter mChatListAdapter;

    private DBUtil dbutil;

    public DBUtil getDbutil() {
        return dbutil;
    }

    public static Tab2 newInstance(String roomName) {
        Tab2 myFragment = new Tab2();

        Bundle args = new Bundle();
        args.putString(ROOM_NAME, roomName);

        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_2,container,false);
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialized once with an Android context.
        Firebase.setAndroidContext(getActivity());

        roomName = getArguments().getString(ROOM_NAME);

        // Setup our Firebase mFirebaseRef
        mFirebaseRef = new Firebase(FIREBASE_URL).child(roomName).child("polls");


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
        mChatListAdapter = new PollListAdapter(
                mFirebaseRef.orderByChild("timestamp").limitToFirst(200),
                getActivity(), Tab2.this, R.layout.poll, roomName);
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
    @Override
    public void onStop() {
        super.onStop();
        mFirebaseRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
        mChatListAdapter.cleanup();
    }

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
    public void vote(String key, final int option) {
        if (!dbutil.containsVote(key)) {
            //create new entry for this key
            dbutil.putVote(key, option);
        }

        final Firebase voteRef = mFirebaseRef.child(key).child("items").child(option+"").child("vote");
        voteRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Long voteValue = (Long) dataSnapshot.getValue();
                        Log.e("Vote update:", "" + voteValue);

                        voteRef.setValue(voteValue + 1);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                }
        );




    }


}