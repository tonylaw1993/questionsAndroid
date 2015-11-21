package hk.ust.cse.hunkim.questionroom.question;

import java.util.Date;

public class Reply implements Comparable<Reply> {
    private String key;
    private String tags;
    private String wholeMsg;
    private int echo;
    private int dislike;
    private long timestamp;
    private String dateString;
    private boolean latest;
    private String parentid;

    public Reply(String message, String QuestionKey) {
        this.wholeMsg = message;
        this.echo = 0;
        this.dislike = 0;
        this.parentid = QuestionKey;
        this.timestamp = new Date().getTime();
        this.dateString = "";
    }

    public String getKey() {return key;}

    public void setKey(String key) {
        this.key = key;
    }

    public String getTags() {
        return tags;
    }

    public int getEcho() {return echo;}

    public int getDislike() {return dislike;}

    public String getWholeMsg() {return wholeMsg;}

    public long getTimestamp() {return timestamp;}

    public boolean isLatest() {
        return latest;
    }

    public void updateNewReply() {
        latest = this.timestamp > new Date().getTime() - 180000;
    }

    public String getDateString() {return dateString;}

    public String getTrustedDesc() {return trustedDesc;}

    private String trustedDesc;

    public String getParentid(){return parentid;}

    public boolean sameParent(Reply reply){
        if(this.getParentid().equals(reply.getParentid())){
            return true;}
        return false;
    }
    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private Reply() {}

    public int compareTo(Reply other) {
        // Push new on top
        other.updateNewReply(); // update NEW button
        this.updateNewReply();

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
        public int hashCode() {
        return key.hashCode();
    }
}

