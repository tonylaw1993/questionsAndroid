package hk.ust.cse.hunkim.questionroom.question;

import java.util.Date;
import java.util.List;

public class Question implements Comparable<Question> {

    private String key;
    private String wholeMsg;
    private String title;
    private String head;
    private String headLastChar;
    private String desc;
    private String linkedDesc;
    private String tags;
    private String [] photos;

    private long timestamp;

    private int echo;
    private int dislike;
    private int numReply;
    private int order;
    private boolean completed;
    private boolean latest;

    public Question(String title, String message, List<String> tmpPhotos) {
        this.wholeMsg = message;
        this.linkedDesc = message;
        this.echo = 0;
        this.dislike = 0;
        this.numReply = 0;
        this.head = title;
        this.desc = "";
        if (this.head.length() < message.length()) {
            this.desc = message.substring(this.head.length());
        }
        this.photos = new String[tmpPhotos.size()];
        tmpPhotos.toArray(this.photos);

        // get the last char
        this.headLastChar = head.substring(head.length() - 1);
        this.tags = "...";
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

    public String getHead() { return head; }

    public String getDesc() { return desc; }

    public String getTags() { return tags; }

    public String [] getPhotos() { return photos; }

    public int getEcho() { return echo; }

    public int getDislike() { return dislike; }

    public String getWholeMsg() { return wholeMsg; }

    public String getHeadLastChar() {
        return headLastChar;
    }

    public String getLinkedDesc() { return linkedDesc; }

    public int getNumReply() {
        return numReply;
    }

    public boolean isCompleted() {
        return completed;
    }

    public long getTimestamp() { return timestamp; }

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

    public String getTrustedDesc() {
        return trustedDesc;
    }

    private String trustedDesc;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private Question() {
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
