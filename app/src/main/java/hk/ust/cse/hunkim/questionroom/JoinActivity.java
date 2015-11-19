package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;

import java.util.Arrays;

import hk.ust.cse.hunkim.questionroom.db.DBHelper;
import hk.ust.cse.hunkim.questionroom.db.DBUtil;


/**
 * A login screen that offers login via email/password.
 */
public class JoinActivity extends ActionBarActivity {
    public static final String ROOM_NAME = "Room_name";
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    // UI references.
    private TextView roomNameView;
    private DBUtil dbutil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

      /*  ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.logo);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);*/


        // get the DB Helper
        DBHelper mDbHelper = new DBHelper(this);
        dbutil = new DBUtil(mDbHelper);

        // AutoCompleteTextView
        String[] autoRoomSugg ={"COMP3111","3111","midterm1","midterm2","project","milestone1","milestone2","lab",
                "presentation","lab","AWS","bootstrap","Firebase","Trello","github","gitbase","canvas","KimSung","peter",
        "website"," AngularJS","android","app","deadline","tutorial","demonstration","grade","TA","AndroidStudio","testcase","xml","java"};
        AutoCompleteTextView roomlistcomplete = (AutoCompleteTextView) findViewById(R.id.room_name);
        ArrayAdapter<String> adapter =  new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, autoRoomSugg);
        roomlistcomplete.setAdapter(adapter);

        // Set up the login form.
        roomNameView = (TextView) findViewById(R.id.room_name);

        roomNameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    attemptJoin(textView);
                }
                return true;
            }
        });

//        String[] presetList = {"room1", "room2", "room3", "room4", "project", "lab"};
        final String[] tmp = dbutil.getRecentRoomName();
        final String [] recentRoom = new String[9];
        for(int i = 0; i < 4; i++){
            if(i < tmp.length)
                recentRoom[i] = tmp[i];
            else
                recentRoom[i] = "";
        }

        int[] viewIds = new int[] {R.id.room1, R.id.room2, R.id.room3, R.id.room4};
        for(int i = 0; i < 4; i++){
            final int j = i;
            Button recentButton = (Button) findViewById(viewIds[i]);
            if(recentRoom[i] == ""){
                recentButton.setVisibility(View.INVISIBLE);
                continue;
            }
            recentButton.setText(recentRoom[i]);
            recentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    suggestJoin(Arrays.copyOf(recentRoom, recentRoom.length)[j]);
                }
            });
        }
    }



    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */

    public void suggestJoin(String room) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(ROOM_NAME, room);
        startActivity(intent);
    }

    public void attemptJoin(View view) {
        // Reset errors.
        roomNameView.setError(null);

        // Store values at the time of the login attempt.
        String room_name = roomNameView.getText().toString();

        boolean cancel = false;

        // Check for a valid email address.
        if (TextUtils.isEmpty(room_name)) {
            roomNameView.setError(getString(R.string.error_field_required));

            cancel = true;
        } else if (!isEmailValid(room_name)) {
            roomNameView.setError(getString(R.string.error_invalid_room_name));
            cancel = true;
        }

        if (cancel) {
            roomNameView.setText("");
            roomNameView.requestFocus();
        } else {
            // Start main activity
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(ROOM_NAME, room_name);
            dbutil.updateRoomEntry(room_name);
            startActivity(intent);
        }
    }

    private boolean isEmailValid(String room_name) {
        // http://stackoverflow.com/questions/8248277
        // Make sure alphanumeric characters
        return !room_name.matches("^.*[^a-zA-Z0-9 ].*$");
    }
}

