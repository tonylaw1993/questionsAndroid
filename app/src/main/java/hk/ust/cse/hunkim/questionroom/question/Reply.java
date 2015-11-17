package hk.ust.cse.hunkim.questionroom.question;

import java.util.Date;

/**
 * Created by hoyuichan on 11/16/2015.
 */
public class Reply {

    private String key;
    private String comment;
    private long timestamp;
    private String dateString;
    private int echo;
    private int dislike;
    private String tags;

    public Reply(String comment){
        this.comment = comment;
        this.echo = 0;
        this.dislike = 0;
        this.timestamp = new Date().getTime();
        this.dateString = "";
    }

    public String getComment(){
        return comment;
    }

    public int getEcho() { return echo; }

    public int getDislike() { return dislike; }

    public String getKey() {return key;}

    public void setKey(String key) {
        this.key = key;
    }

    public String getTags() {
        return tags;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
