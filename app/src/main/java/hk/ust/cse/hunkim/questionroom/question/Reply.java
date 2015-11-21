package hk.ust.cse.hunkim.questionroom.question;

import java.util.Date;

public class Reply implements Comparable<Reply> {
    private String key;
    private String tags;
    private String replyMsg;
    private int like;
    private int dislike;
    private long timestamp;
    private String parentid;

    public Reply(String message, String QuestionKey) {
        this.replyMsg = message;
        this.like = 0;
        this.dislike = 0;
        this.parentid = QuestionKey;
        this.timestamp = new Date().getTime();
    }

    public String getKey() {return key;}

    public void setKey(String key) {
        this.key = key;
    }

    public String getTags() {
        return tags;
    }

    public int getLike() {return like;}

    public int getDislike() {return dislike;}

    public String getReplyMsg() {return replyMsg;}

    public long getTimestamp() {return timestamp;}

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
 /*       other.updateNewReply(); // update NEW button
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
        return this.echo - other.echo;*/
        return 0;
    }
        @Override
        public int hashCode() {
        return key.hashCode();
    }
}

