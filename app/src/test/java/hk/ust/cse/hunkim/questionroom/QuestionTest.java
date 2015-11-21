package hk.ust.cse.hunkim.questionroom;

import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.TestCase;

import java.util.Date;
import java.util.Objects;

import hk.ust.cse.hunkim.questionroom.question.Question;


/**
 * Created by hunkim on 7/15/15.
 */

public class QuestionTest  extends TestCase {
    Question q;
    Question q2;
    Question t;
    long date;
    long date2;

    protected void setUp() throws Exception {
        super.setUp();

        q = new Question("title", "Hello? This is very nice");
        q2 = new Question("title", "Hello? This is very nice");
        t = new Question("title_need_to_be_longer", "Hello? This is shorter");
        date = q.getTimestamp();
        date2 = q2.getTimestamp();

    }

    @SmallTest

    public void testChatFirstString() {
        String[] strHead = {
                "Hello? This is very nice", "Hello?",
                "This is cool! Really?", "This is cool!",
                "How.about.this? Cool", "How.about.this?"
        };

        for (int i = 0; i < strHead.length; i += 2) {
            String head = q.getFirstSentence(strHead[i]);
            assertEquals("Chat.getFirstSentence", strHead[i + 1], head);
        }

        String sentence = "This is first sentence.This is second?No!";
        assertEquals("First Sentence", "This is first sentence.This is second?No!", q.getFirstSentence(sentence));
        String sentence1 = "This is first sentence. This is second.";
        assertEquals("First Sentence of one", "This is first sentence.", q.getFirstSentence(sentence1));
    }

    @SmallTest

    public void testHead() {
        assertEquals("Head", "title", q.getHead());
    }

    @SmallTest
    public void testDesc() {
        assertEquals("testgetDesc is fail", "? This is very nice", q.getDesc());
    }

    public void testEcho() {
        assertEquals("testEcho is fail", 0, q.getEcho());
    }

    public void testDislike() {
        assertEquals("testDislike is fail", 0, q.getDislike());
    }

    public void testWholeMsg() {
        assertEquals("testWholeMsg   is fail", "Hello? This is very nice", q.getWholeMsg());
    }

    public void testHeadLastChar() {
        assertEquals(" testHeadLastChar  is fail", "e", q.getHeadLastChar());
    }

    public void testLinkedDesc() {
        assertEquals("testLinkedDesc   is fail", "", q.getLinkedDesc());
    }

    public void testisCompleted() {
        assertEquals("testisCompleted   is fail", false, q.isCompleted());
    }

    public void testgetTimestamp() {
        long expected = date;
        assertEquals("testgetTimestamp   is fail", expected, q.getTimestamp());
    }

    public void testgetTags() {
        assertEquals("testgetTags   is fail", null, q.getTags());
    }

    public void testgetOrder() {
        assertEquals("testgetOrder   is fail", 0, q.getOrder());
    }

    public void testisLatest() {
        assertEquals("testisLatest   is fail", false, q.isLatest());
    }

    public void testupdateNewQuestion() {
        q.updateNewQuestion();
        assertEquals(" testupdateNewQuestion  is fail", true, q.isLatest());
    }

    public void testgetKey() {
        q.setKey("LOL");
        assertEquals("   is fail", "LOL", q.getKey());
    }

    public void testsetKey(String key) {
        q.setKey("LOL");
        assertEquals(" testsetKey  is fail", "LOL", q.getKey());
    }

    /**
     * New one/high echo goes bottom
     *
     * @param other other chat
     * @return order
     */

    public void testcompareTo() {
        // Push new on top
//        q.updateNewQuestion(); // update NEW button
//        q2.updateNewQuestion();
//        assertEquals(true,q.isLatest());
//        assertEquals(false,t.isLatest());
     if (q2.isLatest() != q.isLatest() ) {
        int expected = q2.isLatest()  ? 1 : -1; // this is the winner
        }


        if (q2.getEcho() == q.getEcho()) {
        if (date == date2) {
        int expected = 0;
        }
        int expected = date > date2 ? -1 : 1;
        }
        int expected =  q2.getEcho() - q.getEcho();

        try {
            Thread.sleep(190000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Question latestQT = new Question("Title", "Message!");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Question latestQ = new Question("Title", "Message!");

        assertEquals(" testcompareTo  is fail", -1, q.compareTo(latestQ));
        assertEquals(" testcompareTo  is fail", 1, latestQ.compareTo(q));

        assertEquals(" testcompareTo  is fail", 1, latestQ.compareTo(latestQT));
        assertEquals(" testcompareTo  is fail", -1, latestQT.compareTo(latestQ));

        assertEquals(" testcompareTo  is fail", 0, q.compareTo(q2));
    }


    public void testequals() {
        q.setKey("KEY");
        q2.setKey("KEY");
        assertTrue("Equal", q.equals(q2));
        assertFalse("NotEqual", q.equals(date));
    }

    public void testsetreadall(){
        q.setreadall();
        assertTrue("readall is true", q.getreadall());
    }

    /*
    public void testhashCode() {
        Object one = q;
        assertEquals(" testhashCode   is fail", "", q.getKey().hashCode());
    }
*/
}


