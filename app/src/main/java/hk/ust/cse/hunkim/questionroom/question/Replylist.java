package hk.ust.cse.hunkim.questionroom.question;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hoyuichan on 11/16/2015.
 */
public class Replylist {

    private List<String> list;
    int size;

    public Replylist(){
          list = new ArrayList<>();
          size=0;
    }

    public List<String> getLlist(){
        return list;
    }

    public void addreply(String s){
        list.add("s");
        size++;
    }

    public int getSize(){
        return list.size();
    }
}
