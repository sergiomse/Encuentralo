package com.sergiomse.encuentralo.searcher;

import com.sergiomse.encuentralo.model.Thing;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sergiomse@gmail.com on 21/10/2015.
 */
public class SearchItem {

    private Thing thing;
    private List<Match> matches = new ArrayList<>();
    private int exactMatches;
    private float totalConfidence;

    public SearchItem(Thing thing) {
        this.thing = thing;
    }


    public Thing getThing() {
        return thing;
    }

    public void setThing(Thing thing) {
        this.thing = thing;
    }

    public void addMatch(Match match) {
        if (match.getConfidence() == 1.0f) {
            exactMatches ++;
        }
        totalConfidence += match.getConfidence();
    }

    public Match getMatch(int i) {
        return matches.get(i);
    }

    public int getExactMatches() {
        return exactMatches;
    }

    public void setExactMatches(int exactMatches) {
        this.exactMatches = exactMatches;
    }

    public float getTotalConfidence() {
        return totalConfidence;
    }

    public void setTotalConfidence(float totalConfidence) {
        this.totalConfidence = totalConfidence;
    }
}
