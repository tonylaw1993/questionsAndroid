package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.graphics.Color;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.client.Query;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import hk.ust.cse.hunkim.questionroom.db.DBUtil;
import hk.ust.cse.hunkim.questionroom.question.Poll;

/**
 * @author greg
 * @since 6/21/13
 * <p/>
 * This class is an example of how to use FirebaseListAdapter. It uses the <code>Chat</code> class to encapsulate the
 * data for each individual chat message
 */
public class PollListAdapter extends FirebaseListAdapter<Poll> {

    // The mUsername for this client. We use this to indicate which messages originated from this user
    private String roomName;
    MainActivity activity;
    Tab2 fragment;

    public PollListAdapter(Query ref, Activity activity, Tab2 fragment, int layout, String roomName) {
        super(ref, Poll.class, layout, activity);

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
     * @param poll An instance representing the current state of a chat message
     */
    @Override
    protected void populateView(View view, final Poll poll) {
        final DBUtil dbUtil = fragment.getDbutil();


        // Map a Chat object to an entry in our listview
  /*      int echo = question.getEcho();
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

        );*/

        final List<Map<String, Object>> optionItem = poll.getItems();
        for(int i = 0; i < optionItem.size(); i++) {
            Object optionName = optionItem.get(i).get("option");
            Object voteNumber = optionItem.get(i).get("vote");
            View pollOptionView = LayoutInflater.from(view.getContext()).inflate(R.layout.poll_option, view.findViewById(R.id.optionsContainer), false);
          
        }

        poll.updateNewPoll();

        if (poll.isLatest())
            ((TextView) view.findViewById(R.id.isNew)).setVisibility(view.VISIBLE);
        else
           ((TextView) view.findViewById(R.id.isNew)).setVisibility(view.GONE);

        final String titleString = poll.getHead();
        ((TextView) view.findViewById(R.id.head_desc)).setText(titleString);

        String timedisplay = DateUtils.getRelativeTimeSpanString(poll.getTimestamp(), new Date().getTime(), 0, 262144).toString();
        ((TextView) view.findViewById(R.id.timedisplay)).setText(timedisplay);


        view.setTag(poll.getKey());  // store key in the view
    }

    @Override
    protected void sortModels(List<Poll> mModels) {
        Collections.sort(mModels);
    }
    @Override
    protected void setKey(String key, Poll model) {
        model.setKey(key);
    }
}