package hk.ust.cse.hunkim.questionroom;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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

        setTitle("ask");
        EditText inputText = (EditText) findViewById(R.id.messageInput);

    }

    private String foulLanguageFilter(String s) {
        if (s.length() == 0) {
            Toast.makeText(CreateQuestionActivity.this, "No Content ! ", Toast.LENGTH_LONG).show();
        }
        String temp = s;
        String badwordStrings[] = {"fuck", "shit", "damn", "dick", "cocky", "pussy", "gayfag", "asshole", "bitch"};
        String goodwordStrings[] = {"love", "oh my shirt", "oh my god", "dragon", "lovely", "badlady", "handsome boy", "myfriend", "badgirl"};
        for (int index = 0; index < badwordStrings.length; index++) {
            temp = temp.replaceAll("(?i)" + badwordStrings[index], goodwordStrings[index]);
        }
        return temp;
    }

    private void sendMessage() {

        String inputTitleText = ((EditText) findViewById(R.id.titleInput)).getText().toString();
        String inputMsgText = ((EditText) findViewById(R.id.messageInput)).getText().toString();


        if ((inputTitleText.length() == 0) || (inputMsgText.length() == 0)) {
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.coordinatorLayoutCreateQuestion), "No content in Title/ Content", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.RED);
            snackbar.show();
            return;
        }

        String tempTitle = foulLanguageFilter(inputTitleText);
        String tempMsg = foulLanguageFilter(inputMsgText);
        if (!(inputTitleText.equals(tempTitle)) || !(inputMsgText.equals(tempMsg))) {
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.coordinatorLayoutCreateQuestion), "No foul language please :)", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.RED);
            snackbar.show();
        }

        if (inputMsgText.length() < 3 || inputTitleText.length() < 3) {
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.coordinatorLayoutCreateQuestion), "Too short in title/content", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.RED);
            snackbar.show();
            return;
        } else if (inputMsgText.length() > 50 || inputTitleText.length() > 1024) {
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.coordinatorLayoutCreateQuestion), "Too long in title/content", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.RED);
            snackbar.show();
            return;
        } else {
            // Create our 'model', a Chat object
            Question question = new Question(inputTitleText, inputMsgText);
            // Create a new, auto-generated child of that chat location, and save our chat data there
            mFirebaseRef.push().setValue(question);
        }

        onBackPressed();
    }

    public static String encodeTobase64(Bitmap image)
    {
        Bitmap immagex=image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        byte[] b = baos.toByteArray();
        try {
            baos.close();
            baos = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        System.out.println(imageEncoded);
        immagex.recycle();
//        Log.e("LOOK", imageEncoded);
        return imageEncoded;
    }
    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public void pickImage() {

        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, 200);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }

            Uri imageUri = data.getData();

            InputStream imageStream = null;
            try {
                imageStream = getContentResolver().openInputStream(imageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            Bitmap bmp = BitmapFactory.decodeStream(imageStream);

//            ((ImageView) findViewById(R.id.imageView)).setImageBitmap(decodeBase64(encodeTobase64(bmp)));

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_question_menu, menu);

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
                sendMessage();
                return true;
            case R.id.attachImageButton:
                pickImage();
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