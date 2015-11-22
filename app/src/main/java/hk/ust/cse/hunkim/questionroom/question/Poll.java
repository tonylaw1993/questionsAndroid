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
            itemContent = new HashMap<String, Object>();
            itemContent.put("option", options[i]);
            itemContent.put("vote", 0);
            items.add(itemContent);
        }
        this.totalVote = 0;
        this.timestamp = new Date().getTime();
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

    public int getTotalVote() { return totalVote;}

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
