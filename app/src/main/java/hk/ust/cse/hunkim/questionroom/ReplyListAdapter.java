package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.graphics.Color;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.client.Query;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import hk.ust.cse.hunkim.questionroom.db.DBUtil;
import hk.ust.cse.hunkim.questionroom.question.Reply;

/**
 * @author greg
 * @since 6/21/13
 * <p/>
 * This class is an example of how to use FirebaseListAdapter. It uses the <code>Chat</code> class to encapsulate the
 * data for each individual chat message
 */
public class ReplyListAdapter extends FirebaseListAdapter<Reply> {

    // The mUsername for this client. We use this to indicate which messages originated from this user
    private String roomName;
    ReplyActivity activity;


    public ReplyListAdapter(Query ref, Activity activity, int layout, String roomName) {
        super(ref, Reply.class, layout, activity);


        assert (activity instanceof ReplyActivity);

        this.activity = (ReplyActivity) activity;

}
    @Override
    protected void populateView(View view, final Reply reply ) {
        final DBUtil dbUtil = activity.getDbutil();


        int echo = reply.getLike();
        ImageButton likeButton = (ImageButton) view.findViewById(R.id.replyLike);
        TextView likeNumberTextView = (TextView) view.findViewById(R.id.replyLike_number);
        likeNumberTextView.setText("" + echo);


        likeButton.setTag(reply.getKey());
        likeButton.setSelected(dbUtil.getLikeStatus(reply.getKey()));


        likeButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ReplyActivity m = (ReplyActivity) view.getContext();

                        ImageButton replyDislikeButton = (ImageButton) ((LinearLayout) view.getParent()).findViewById(R.id.replyDislike);
                        if(view.isSelected()){ // unlike when selected
                            m.updateLike((String) view.getTag(), -1);
                        }else if(replyDislikeButton.isSelected()){ // another dislike button is selected before
                            m.updateDislike((String) view.getTag(), -1);
                            m.updateLike((String) view.getTag(), 1);
                        }else{ //both like and dislike button are not selected before
                            m.updateLike((String) view.getTag(), 1);
                        }
                    }
                }

        );

        int dislike = reply.getDislike();

        ImageButton dislikeButton = (ImageButton) view.findViewById(R.id.replyDislike);
        TextView dislikeNumberTextView = (TextView) view.findViewById(R.id.replyDislike_number);
        dislikeNumberTextView.setText("" + dislike);



        dislikeButton.setTag(reply.getKey()); // Set tag for button
        dislikeButton.setSelected(dbUtil.getDislikeStatus(reply.getKey()));

                dislikeButton.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ReplyActivity m = (ReplyActivity) view.getContext();
                                ImageButton replyLikeButton = (ImageButton) ((LinearLayout) view.getParent()).findViewById(R.id.replyLike);
                                if (view.isSelected()) { // undislike when selected
                                    m.updateDislike((String) view.getTag(), -1);
                                } else if (replyLikeButton.isSelected()) { // another like button is selected before
                                    m.updateLike((String) view.getTag(), -1);
                                    m.updateDislike((String) view.getTag(), 1);
                                } else { //both like and dislike button are not selected before
                                    //From Mattherw: same situation as the like implementation above
                                    m.updateDislike((String) view.getTag(), 1);
                                }
                            }
                        }
                );

        String msgString = "";


//        reply.updateNewReply();


        msgString += reply.getReplyMsg();

        ((TextView) view.findViewById(R.id.replymsg)).setText(msgString);

        final TextView content = (TextView) view.findViewById(R.id.replymsg);


        String timedisplay = DateUtils.getRelativeTimeSpanString(reply.getTimestamp(), new Date().getTime(), 0, 262144).toString();
        ((TextView) view.findViewById(R.id.replyTimeDisplay)).setText(timedisplay);




        view.setTag(reply.getKey());  // store key in the view
    }

    @Override
    protected void sortModels(List<Reply> mModels) {
        Collections.sort(mModels);
    }

    @Override
    protected void setKey(String key, Reply model) {
        model.setKey(key);
    }
}