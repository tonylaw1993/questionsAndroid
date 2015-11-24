package hk.ust.cse.hunkim.questionroom;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ListFragment;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
        v.findViewById(R.id.fabPoll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity m = (MainActivity) view.getContext();
                m.attemptCreatePoll();
            }
        });
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
                    Snackbar snackbar = Snackbar
                            .make(getActivity().findViewById(R.id.coordinatorLayoutPoll), "Connected", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.GREEN);
                    snackbar.show();
                    getActivity().findViewById(R.id.fabPoll).setEnabled(true);
                } else {
                    Snackbar snackbar = Snackbar
                            .make(getActivity().findViewById(R.id.coordinatorLayoutPoll), "Disconnected", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.RED);
                    snackbar.show();
                    getActivity().findViewById(R.id.fabPoll).setEnabled(false);
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

        final Firebase totalVoteRef = mFirebaseRef.child(key).child("totalVote");
        voteRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Long totalVoteValue = (Long) dataSnapshot.getValue();
                        Log.e("totalVote update:", "" + totalVoteValue);

                        totalVoteRef.setValue(totalVoteValue + 1);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                }
        );




    }


}