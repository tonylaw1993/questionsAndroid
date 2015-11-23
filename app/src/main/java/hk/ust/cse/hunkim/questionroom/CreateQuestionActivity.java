package hk.ust.cse.hunkim.questionroom;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import hk.ust.cse.hunkim.questionroom.question.Question;


public class CreateQuestionActivity extends ActionBarActivity {

    // Declaring Your View and Variables
    private Firebase mFirebaseRef;
    private static final String FIREBASE_URL = "https://android-questions.firebaseio.com/";
    public static String MSG;
    public static String ROOM_NAME;
    public List<String> photos = new ArrayList<String>();

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

        setTitle("Ask");
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
        } else if (inputMsgText.length() > 1024 || inputTitleText.length() > 50) {
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.coordinatorLayoutCreateQuestion), "Too long in title/content", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.RED);
            snackbar.show();
            return;
        } else {
            // Create our 'model', a Chat object
            Question question = new Question(inputTitleText, inputMsgText, photos);
            // Create a new, auto-generated child of that chat location, and save our chat data there
            mFirebaseRef.push().setValue(question);
            question = null;

        }

        onBackPressed();
    }

    public static String encodeTobase64(Bitmap image)
    {
        Bitmap immagex=image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 40, baos);
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

    public Bitmap shrinkBitmap(Bitmap bmp){

        int width = (int) (bmp.getWidth() * 0.4);
        int height = (int) (bmp.getHeight() * 0.4);
        return Bitmap.createScaledBitmap(bmp, width, height, true);
    }

    public void takePhoto(){
        if(photos.size() >= 5){
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.coordinatorLayoutCreateQuestion), "Cannot attach more than 5 images", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.RED);
            snackbar.show();
            return;
        }

        PackageManager pm = getPackageManager();

        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {

            Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

            i.putExtra(MediaStore.EXTRA_OUTPUT, MyFileContentProvider.CONTENT_URI);

            startActivityForResult(i, 100);

        } else {

            Toast.makeText(getBaseContext(), "Camera is not available", Toast.LENGTH_LONG).show();

        }

    }


    public void pickImage() {
        if(photos.size() >= 5){
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.coordinatorLayoutCreateQuestion), "Cannot attach more than 5 images", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.RED);
            snackbar.show();
            return;
        }

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
        int x = requestCode;
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

            String encodedString = encodeTobase64(shrinkBitmap(bmp));
            String encodedImage = "data:image/jpeg;base64," + encodedString;
            photos.add(encodedImage);

            LinearLayout imageScrollView = (LinearLayout) findViewById(R.id.imageInScroll);
            ImageView imageView = new ImageView(this);
            imageView.setImageBitmap(decodeBase64(encodedString));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(20,20,20,20);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(findViewById(R.id.imageHorizontalScrollView).getMeasuredHeight(),
                    findViewById(R.id.imageHorizontalScrollView).getMeasuredHeight());
            imageView.setLayoutParams(params);

            imageScrollView.addView(imageView);

            bmp.recycle();
            bmp = null;


        }else if(requestCode == 100 && resultCode == RESULT_OK){
            File out = new File(getFilesDir(), "newImage.jpg");
            if(!out.exists()) {
                Toast.makeText(getBaseContext(),"Error while capturing image", Toast.LENGTH_LONG).show();
                return;
            }

            Bitmap bmp = BitmapFactory.decodeFile(out.getAbsolutePath());

            String encodedString = encodeTobase64(shrinkBitmap(bmp));
            String encodedImage = "data:image/jpeg;base64," + encodedString;
            photos.add(encodedImage);

            LinearLayout imageScrollView = (LinearLayout) findViewById(R.id.imageInScroll);
            ImageView imageView = new ImageView(this);
            imageView.setImageBitmap(decodeBase64(encodedString));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(20,20,20,20);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(findViewById(R.id.imageHorizontalScrollView).getMeasuredHeight(),
                    findViewById(R.id.imageHorizontalScrollView).getMeasuredHeight());
            imageView.setLayoutParams(params);

            imageScrollView.addView(imageView);

            bmp.recycle();
            bmp = null;
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
                return true;
            case R.id.takePhotoButton:
                takePhoto();
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