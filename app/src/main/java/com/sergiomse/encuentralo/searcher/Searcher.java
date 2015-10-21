package com.sergiomse.encuentralo.searcher;

import android.content.Context;

import com.sergiomse.encuentralo.database.ThingsDB;
import com.sergiomse.encuentralo.model.Thing;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sergiomse@gmail.com on 21/10/2015.
 */
public class Searcher {

    private List<Thing> things;
    private List<SearchItem> searchItems = new ArrayList<>();

    public Searcher(Context context) {
        ThingsDB db = new ThingsDB(context);
        things = db.getThingsOrderedByDate();
        db.cleanup();
    }


    public void search(String searched) {
        String searchedWords[] = searched.split("[\\s\\n]");

        for(Thing thing : things) {
            SearchItem searchItem = new SearchItem(thing);

            String tagWords[] = thing.getTags().split("[\\s\\n]");

            for(String tagWord : tagWords) {
                for(String searchedWord : searchedWords) {
                    float confidence = DamerauLevenshteinAlgorithm.calculateConfidence(tagWord, searchedWord);
                    if (confidence > 0.2f) {
                        int start = thing.getTags().indexOf(searchedWord);
                        searchItem.addMatch(new Match(start, start + tagWord.length(), confidence));
                    }
                }
            }

            if(searchItem.getTotalConfidence() > 0.0f) {
                searchItems.add(searchItem);
            }
        }
    }
}
