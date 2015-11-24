package hk.ust.cse.hunkim.questionroom;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

import hk.ust.cse.hunkim.questionroom.question.Question;


public class MainActivity extends ActionBarActivity {

    // Declaring Your View and Variables
    public static final String KEY = "key";
    public static final String TITLE = "title";
    public static final String MSG = "msg";
    public static final String ROOM_NAME = "room_name";
    public static final String PHOTOS = "photos";
    public enum DataHolder {
        INSTANCE;
        private String [] mObjectList;
        public static boolean hasData() {
            return INSTANCE.mObjectList != null;
        }
        public static void setData(final String [] photos) {
            INSTANCE.mObjectList = photos;
        }
        public static String [] getData() {
            final String [] retList = INSTANCE.mObjectList;
//            INSTANCE.mObjectList = null;
            return retList;
        }
    }
    String roomName;
    Toolbar toolbar;
    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[]={"Question","Poll"};
    int Numboftabs =2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        assert (intent != null);

        // Make it a bit more reliable
        roomName = intent.getStringExtra(JoinActivity.ROOM_NAME);
        if (roomName == null || roomName.length() == 0) {
            roomName = "all";
        }

        // Creating The Toolbar and setting it as the Toolbar for the activity

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter =  new ViewPagerAdapter(getSupportFragmentManager(),Titles,Numboftabs,roomName);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assigning the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }

        });


        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);



        setTitle(roomName);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            default:
                return super.onOptionsItemSelected(item);
        }

//        return super.onOptionsItemSelected(item);
    }

    public void attemptCreateQuestion() {
        Intent intent = new Intent(this, CreateQuestionActivity.class);
        intent.putExtra(ROOM_NAME, roomName );
        startActivity(intent);
    }

    public void attemptReply(Question question) {

        ReplyActivity r = new ReplyActivity();
        Intent intent = new Intent(this, ReplyActivity.class);
        String key = question.getKey();
        String title = question.getHead();
        String msg = question.getWholeMsg();


        // To speed things up :)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        // And add arguments to the Intent
        intent.putExtra(KEY, key);
        intent.putExtra(TITLE, title);
        intent.putExtra(MSG, msg);
        intent.putExtra(ROOM_NAME, roomName);
        DataHolder.setData(question.getPhotos());
        // Now we put the large data into our enum instead of using Intent extras

//        intent.putExtra(PHOTOS, DataHolder.INSTANCE);
        intent.putExtra(PHOTOS, DataHolder.INSTANCE);
        startActivity(intent);

    }

    public void attemptCreatePoll() {
        Intent intent = new Intent(this, CreatePollActivity.class);
        intent.putExtra(ROOM_NAME, roomName );
        startActivity(intent);
    }


    public static String encodeTobase64(Bitmap image)
    {
        Bitmap immagex=image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 25, baos);
        byte[] b = baos.toByteArray();
        try {
            baos.close();
            baos = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

//        Log.e("LOOK", imageEncoded);
        return imageEncoded;
    }
    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    @Override
    public void onStop() {
        super.onStop();
       /* getFragmentManager().mFirebaseRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
        mChatListAdapter.cleanup();*/
    }

    public void Close(View view) {
        finish();
    }
}