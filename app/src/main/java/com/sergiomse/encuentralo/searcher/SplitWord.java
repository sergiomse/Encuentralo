package com.sergiomse.encuentralo.searcher;

/**
 * Created by sergiomse@gmail.com on 25/10/2015.
 */
public class SplitWord {

    public SplitWord(String word, int start, int end) {
        this.word = word;
        this.start = start;
        this.end = end;
    }

    private String word;
    private int start;
    private int end;


    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "SplitWord{" +
                "word='" + word + '\'' +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
