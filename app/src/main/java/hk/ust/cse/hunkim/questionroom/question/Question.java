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
    private String tags;
    private String [] photos = {
            "data:image/png;base64,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",
            "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEgAAABICAQAAAD/5HvMAAABUklEQVR4Ae3Wv0pCYRzG8a8Z6tbgEpgt1Wprc7fgLbSW1mK3Erm1dg/lfA7RkYKggg65tr4J/fm1C6L4vkcf4v0+N/AZH2JKxWIx/WqckuKwhTYmpUuVyWzKZrbNA+a9IY0woBqPWJBlVEKAzrBg6/iDyuQBQYk/qI0FnPMH3WIh5wtqYVqgvhaojtMC9TAlUJlcC9TGtEADLVAL0wL1tUB1nBboHFMClcm1QG1MCzTQAu1jWqC+FqiO0wL1MC1QpgUq8a0FWsf+FyiCImhNDQRjNVCqBuqqgaoMC+R8skANssJACQtVpUOCKwB0QvB8/uU9FQrvaG7OMw2W0MXcnC2W0p0Wp8aXEgcOtDhwPJPzskwOXGlx4EmLs8GvEgcOtTjQ0+LA9VROk5X0psUp8aPEAfjQ4sDlBOd1tRzYZKTEAdhjpMQB2OVdiQOwww1NYrE/DNRxTGfwp8oAAAAASUVORK5CYII="
    };;

    private long timestamp;

    private int echo;
    private int dislike;
    private int numReply;
    private int order;
    private boolean completed;
    private boolean latest;

    public Question(String title, String message) {
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
        this.photos = photos;
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
