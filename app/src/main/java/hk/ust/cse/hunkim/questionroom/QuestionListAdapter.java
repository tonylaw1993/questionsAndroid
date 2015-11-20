package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.app.ListFragment;
import android.graphics.Color;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.client.Query;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import hk.ust.cse.hunkim.questionroom.db.DBUtil;
import hk.ust.cse.hunkim.questionroom.question.Question;
/**
 * @author greg
 * @since 6/21/13
 * <p/>
 * This class is an example of how to use FirebaseListAdapter. It uses the <code>Chat</code> class to encapsulate the
 * data for each individual chat message
 */
public class QuestionListAdapter extends FirebaseListAdapter<Question> {

    // The mUsername for this client. We use this to indicate which messages originated from this user
    private String roomName;
    MainActivity activity;
    Tab1 fragment;

    public QuestionListAdapter(Query ref, Activity activity, Tab1 fragment, int layout, String roomName) {
        super(ref, Question.class, layout, activity);

        // Must be MainActivity
        assert (activity instanceof MainActivity);

        this.activity = (MainActivity) activity;
        this.fragment = fragment;
}

    /**
     * Bind an instance of the <code>Chat</code> class to our view. This method is called by <code>FirebaseListAdapter</code>
     * when there is a data change, and we are given an instance of a View that corresponds to the layout that we passed
     * to the constructor, as well as a single <code>Chat</code> instance that represents the current data to bind.
     *
     * @param view     A view instance corresponding to the layout we passed to the constructor.
     * @param question An instance representing the current state of a chat message
     */
    @Override
    protected void populateView(View view, final Question question) {
        final DBUtil dbUtil = fragment.getDbutil();

        Button Comment = (Button) view.findViewById(R.id.comment);
                        Comment.setOnClickListener(new View.OnClickListener(){
                                @Override
                                       public void onClick(View view) {
                                                    MainActivity m = (MainActivity) view.getContext();
                                                    m.attemptReply(question);
                                            }});

        // Map a Chat object to an entry in our listview
        int echo = question.getEcho();
        Button likeButton = (Button) view.findViewById(R.id.like);
        likeButton.setText("" + echo);
        likeButton.setTextColor(Color.BLUE);

        likeButton.setTag(question.getKey()); // Set tag for button
        likeButton.setSelected(dbUtil.getLikeStatus(question.getKey()));


        likeButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainActivity m = (MainActivity) view.getContext();

                        Button questionDislikeButton = (Button) ((LinearLayout) view.getParent()).findViewById(R.id.dislike);
                        if(view.isSelected()){ // unlike when selected
                            fragment.updateLike((String) view.getTag(), -1);
                        }else if(questionDislikeButton.isSelected()){ // another dislike button is selected before
                            fragment.updateDislike((String) view.getTag(), -1);
                            fragment.updateLike((String) view.getTag(), 1);
                        }else{ //both like and dislike button are not selected before
                            fragment.updateLike((String) view.getTag(), 1);
                        }
                    }
                }

        );

        int dislike = question.getDislike();
        Button dislikeButton = (Button) view.findViewById(R.id.dislike);
        dislikeButton.setText("" + dislike);
        dislikeButton.setTextColor(Color.RED);


        dislikeButton.setTag(question.getKey()); // Set tag for button
        dislikeButton.setSelected(dbUtil.getDislikeStatus(question.getKey()));

                dislikeButton.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                MainActivity m = (MainActivity) view.getContext();
                                Button questionLikeButton = (Button) ((LinearLayout) view.getParent()).findViewById(R.id.like);
                                if (view.isSelected()) { // undislike when selected
                                    fragment.updateDislike((String) view.getTag(), -1);
                                } else if (questionLikeButton.isSelected()) { // another like button is selected before
                                    fragment.updateLike((String) view.getTag(), -1);
                                    fragment.updateDislike((String) view.getTag(), 1);
                                } else { //both like and dislike button are not selected before
                                    //From Mattherw: same situation as the like implementation above
                                    fragment.updateDislike((String) view.getTag(), 1);
                                }
                            }
                        }
        );

        String titleString = "";
        String msgString = "";


        question.updateNewQuestion();

        if (question.isLatest()) {
            ((TextView) view.findViewById(R.id.isNew)).setVisibility(view.VISIBLE);
        }
        else {
           ((TextView) view.findViewById(R.id.isNew)).setVisibility(view.GONE);
        }

        titleString += question.getHead();
        msgString += question.getWholeMsg();
        String subStringOfMsg = msgString ;
        if ( msgString.length()>10) {
            subStringOfMsg = msgString.substring(0, 9) + "...";
        }

        ((TextView) view.findViewById(R.id.head_desc)).setText(titleString);
        ((TextView) view.findViewById(R.id.onlymsg)).setText(subStringOfMsg);

        final TextView content = (TextView) view.findViewById(R.id.onlymsg);

        final Button showAllContent = (Button) view.findViewById(R.id.showall);
        showAllContent.setText("more..." );
        showAllContent.setTextColor(Color.BLUE);
        showAllContent.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        question.setreadall();
                        String msgString = "";
                        msgString += question.getWholeMsg();
                        String subStringOfMsg = msgString;
                        if ( msgString.length()>10) {
                            subStringOfMsg = msgString.substring(0, 9) + "...";
                        }
                        if(question.getreadall()==true) {
                            content.setText(msgString);
                            showAllContent.setText("less...");
                        }
                        else {
                            content.setText(subStringOfMsg);
                            showAllContent.setText("more..." );
                        }
                        // ((TextView) view.findViewById(R.id.onlymsg)).setText(msgString);
                    }
                });


        String timedisplay = DateUtils.getRelativeTimeSpanString(question.getTimestamp(), new Date().getTime(), 0, 262144).toString();
        ((TextView) view.findViewById(R.id.timedisplay)).setText(timedisplay);


        view.setTag(question.getKey());  // store key in the view
    }

    @Override
    protected void sortModels(List<Question> mModels) {
        Collections.sort(mModels);
    }

    @Override
    protected void setKey(String key, Question model) {
        model.setKey(key);
    }
}