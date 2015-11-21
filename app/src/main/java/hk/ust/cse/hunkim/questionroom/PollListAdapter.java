package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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



        final List<Map<String, Object>> optionItem = poll.getItems();
        int totalVote = 0;
        int [] vote = {0,0,0,0,0};
        LinearLayout parent = (LinearLayout) view.findViewById(R.id.optionsContainer);
        if(!(optionItem.size() < 2)) {
            parent.removeAllViews();
            for (int i = 0; i < optionItem.size(); i++) {
                Object optionName = optionItem.get(i).get("option");
                Object voteNumber = optionItem.get(i).get("vote");

                LinearLayout pollOptionView = (LinearLayout) LayoutInflater.from(
                        activity).inflate(R.layout.poll_option, parent, true);
                Button voteButton = ((Button) ((parent.getChildAt(i)).findViewById(R.id.selectpoll)));
                vote[i] = (int) voteNumber;
                voteButton.setText(voteNumber + "");
                voteButton.setTag(i);
                voteButton.setEnabled(!dbUtil.containsVote(poll.getKey()));
                voteButton.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (!(view.isSelected()))
                                    fragment.vote(poll.getKey(), (int) view.getTag());

                            }
                });


                ((TextView) ((parent.getChildAt(i)).findViewById(R.id.optioncontent))).setText(optionName + "");
                totalVote += (int) voteNumber;
            }

            for (int i = 0; i < optionItem.size(); i++) {
                View bar = ((parent.getChildAt(i)).findViewById(R.id.viewbar));
                bar.getLayoutParams().width = (int) (550.0 * ((float) vote[i] / (float) totalVote));
            }
        }




        poll.updateNewPoll();

        if (poll.isLatest())
            ((TextView) view.findViewById(R.id.isNew)).setVisibility(view.VISIBLE);
        else
           ((TextView) view.findViewById(R.id.isNew)).setVisibility(view.GONE);

        final String titleString = poll.getHead();
        ((TextView) view.findViewById(R.id.optionTitle)).setText(titleString);

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