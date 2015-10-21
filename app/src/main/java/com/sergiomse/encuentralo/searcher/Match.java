package com.sergiomse.encuentralo.searcher;

/**
 * Created by sergiomse@gmail.com on 21/10/2015.
 */
public class Match {

    private int start;
    private int end;
    private float confidence;

    public Match(int start, int end, float confidence) {
        this.start = start;
        this.end = end;
        this.confidence = confidence;
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

    public float getConfidence() {
        return confidence;
    }

    public void setConfidence(float confidence) {
        this.confidence = confidence;
    }
}
