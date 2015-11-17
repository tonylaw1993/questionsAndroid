package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.graphics.Color;
import android.text.format.DateUtils;
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
import hk.ust.cse.hunkim.questionroom.question.Reply;


public class ReplyListAdapter extends FirebaseListAdapter<Reply> {

    // The mUsername for this client. We use this to indicate which messages originated from this user
    private String roomName;
    MainActivity activity;

    public ReplyListAdapter(Query ref, Activity activity, int layout, String roomName) {
        super(ref, Reply.class, layout, activity);

        // Must be MainActivity
        assert (activity instanceof MainActivity);

        this.activity = (MainActivity) activity;
}


    @Override
    protected void populateView(View view, final Reply reply) {
        final DBUtil dbUtil = activity.getDbutil();

        // Map a Chat object to an entry in our listview
        int echo = reply.getEcho();
        Button likeButton = (Button) view.findViewById(R.id.like);
        likeButton.setText("" + echo);
        likeButton.setTextColor(Color.BLUE);

        likeButton.setTag(reply.getKey()); // Set tag for button
        likeButton.setSelected(dbUtil.getLikeStatus(reply.getKey()));


        likeButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainActivity m = (MainActivity) view.getContext();

                        Button questionDislikeButton = (Button) ((LinearLayout) view.getParent()).findViewById(R.id.dislike);
                        if(view.isSelected()){ // unlike when selected
                            m.updateLike((String) view.getTag(), -1);
                        }else if(questionDislikeButton.isSelected()){ // another dislike button is selected before
                            m.updateDislike((String) view.getTag(), -1);
                            m.updateLike((String) view.getTag(), 1);
                        }else{ //both like and dislike button are not selected before
                            m.updateLike((String) view.getTag(), 1);
                        }
                    }
                }

        );

        int dislike = reply.getDislike();
        Button dislikeButton = (Button) view.findViewById(R.id.dislike);
        dislikeButton.setText("" + dislike);
        dislikeButton.setTextColor(Color.RED);


        dislikeButton.setTag(reply.getKey()); // Set tag for button
        dislikeButton.setSelected(dbUtil.getDislikeStatus(reply.getKey()));

                dislikeButton.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                MainActivity m = (MainActivity) view.getContext();
                                Button questionLikeButton = (Button) ((LinearLayout) view.getParent()).findViewById(R.id.like);
                                if (view.isSelected()) { // undislike when selected
                                    m.updateDislike((String) view.getTag(), -1);
                                } else if (questionLikeButton.isSelected()) { // another like button is selected before
                                    m.updateLike((String) view.getTag(), -1);
                                    m.updateDislike((String) view.getTag(), 1);
                                } else { //both like and dislike button are not selected before
                                    //From Mattherw: same situation as the like implementation above
                                    m.updateDislike((String) view.getTag(), 1);
                                }
                            }
                        }
        );

        String titleString = "";
        String msgString = "";


        String timedisplay = DateUtils.getRelativeTimeSpanString(reply.getTimestamp(), new Date().getTime(), 0, 262144).toString();
        ((TextView) view.findViewById(R.id.timedisplay)).setText(timedisplay);


        view.setTag(reply.getKey());  // store key in the view
    }

    @Override
    protected void sortModels(List<Reply> mModels) {
        //Collections.sort(mModels);
    }

    @Override
    protected void setKey(String key, Reply model) {
        model.setKey(key);
    }
}