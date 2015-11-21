package hk.ust.cse.hunkim.questionroom.question;

import java.util.Date;

public class Question implements Comparable<Question> {

    private String key;
    private String wholeMsg;
    private String title;
    private String head;
    private String headLastChar;
    private String desc;
    private String linkedDesc;
    private boolean completed;
    private long timestamp;
    private String tags;
    private int echo;
    private int dislike;
    private int order;
    private boolean latest;
    private String dateString;
    private boolean readall;



    public Question(String title, String message) {
        this.wholeMsg = message;
        this.echo = 0;
        this.dislike = 0;
        this.head = title;
        this.desc = "";
        if (this.head.length() < message.length()) {
            this.desc = message.substring(this.head.length());
        }

        // get the last char
        this.headLastChar = head.substring(head.length() - 1);

        this.timestamp = new Date().getTime();
        this.dateString = "";
        this.linkedDesc = "";
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

    public String getHead() { return head; }

    public String getDesc() {
        return desc;
    }

    public int getEcho() { return echo; }

    public int getDislike() { return dislike; }

    public String getWholeMsg() {
        return wholeMsg;
    }

    public String getHeadLastChar() {
        return headLastChar;
    }

    public String getLinkedDesc() {
        return linkedDesc;
    }

    public boolean isCompleted() {
        return completed;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getTags() {
        return tags;
    }

    public int getOrder() {
        return order;
    }

    public boolean isLatest() {
        return latest;
    }

    public void updateNewQuestion() {
        latest = this.timestamp > new Date().getTime() - 180000;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDateString() {
        return dateString;
    }
    public String getTrustedDesc() {
        return trustedDesc;
    }

    private String trustedDesc;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private Question() {
    }
    public boolean getreadall(){
        return readall;
    }

    public void setreadall(){
        boolean temp = ! readall;
        readall = temp;
    }


    /**
     * New one/high echo goes bottom
     * @param other other chat
     * @return order
     */
    @Override
    public int compareTo(Question other) {
        // Push new on top
        other.updateNewQuestion(); // update NEW button
        this.updateNewQuestion();

        if (this.latest != other.latest) {
            return this.latest ? 1 : -1; // this is the winner
        }


        if (this.echo == other.echo) {
            if (other.timestamp == this.timestamp) {
                return 0;
            }
            return other.timestamp > this.timestamp ? -1 : 1;
        }
        return this.echo - other.echo;
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Question)) {
            return false;
        }
        Question other = (Question)o;
        return key.equals(other.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
