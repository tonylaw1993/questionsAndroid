package hk.ust.cse.hunkim.questionroom.question;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Poll implements Comparable<Poll> {

    private String key;
    private String head;
    private long timestamp;
    private int totalVote;
    private boolean latest;
    private Map<String, Object> itemContent = new HashMap<String, Object>();
    private List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();


    public Poll(String title, String [] options) {
        this.head = title;
        for(int i = 0; i < options.length; i++) {
            itemContent.clear();
            itemContent.put("option", options[i]);
            itemContent.put("vote", 0);
            items.add(i, itemContent);
        }
        this.totalVote = 0;
        this.timestamp = new Date().getTime();
    }

    /**
     * Get first sentence from a message
     * @param message
     * @return
     */
    public static String getFirstSentence(String message) {
        String[] tokens = {". ", "? ", "! "};

        int index = -1;

        for (String token : tokens) {
            int i = message.indexOf(token);
            if (i == -1) {
                continue;
            }

            if (index == -1) {
                index = i;
            } else {
                index = Math.min(i, index);
            }
        }

        if (index == -1) {
            return message;
        }

        return message.substring(0, index+1);
    }

    /* -------------------- Getters ------------------- */

    public List<Map<String, Object>> getItems() { return items; }

    public String getHead() { return head; }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isLatest() {
        return latest;
    }

    public void updateNewPoll() {
        latest = this.timestamp > new Date().getTime() - 180000;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) { this.key = key; }

    public String getTrustedDesc() { return trustedDesc; }

    private String trustedDesc;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private Poll() {
    }



    /**
     * New one/high echo goes bottom
     * @param other other chat
     * @return order
     */
    @Override
    public int compareTo(Poll other) {
        // Push new on top
        other.updateNewPoll(); // update NEW button
        this.updateNewPoll();

/*        if (this.latest != other.latest) {
            return this.latest ? 1 : -1; // this is the winner
        }


        if (this.echo == other.echo) {
            if (other.timestamp == this.timestamp) {
                return 0;
            }
            return other.timestamp > this.timestamp ? -1 : 1;
        }
        return this.echo - other.echo;*/
        return 0;
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Poll)) {
            return false;
        }
        Poll other = (Poll)o;
        return key.equals(other.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
