package com.sergiomse.encuentralo.utils;

import android.content.Context;

import com.sergiomse.encuentralo.database.ThingsDB;
import com.sergiomse.encuentralo.model.Thing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by sergiomse@gmail.com on 13/10/2015.
 */
public class Searcher {

    public static class SearchItem implements Comparable<SearchItem> {

        private Thing thing;
        private List<Integer> tagIndices;
        private List<Integer> locIndices;

        public SearchItem() {
            tagIndices = new ArrayList<>();
            locIndices = new ArrayList<>();
        }

        public Thing getThing() {
            return thing;
        }

        public void setThing(Thing thing) {
            this.thing = thing;
        }

        public List<Integer> getTagIndices() {
            return tagIndices;
        }

        public void setTagIndices(List<Integer> tagIndices) {
            this.tagIndices = tagIndices;
        }

        public List<Integer> getLocIndices() {
            return locIndices;
        }

        public void setLocIndices(List<Integer> locIndices) {
            this.locIndices = locIndices;
        }

        public String getFormattedText() {
            return thing.getTags();
        }

        @Override
        public int compareTo(SearchItem another) {
            return (this.tagIndices.size() + this.locIndices.size()) -
                    (another.tagIndices.size() + another.locIndices.size());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SearchItem item = (SearchItem) o;

            return this.thing.getId() == item.thing.getId();
        }

        @Override
        public int hashCode() {
            return (int) (thing.getId() ^ (thing.getId() >>> 32));
        }
    }

    private Context context;
    List<Thing> list;

    public Searcher(Context context) {
        this.context = context;

        ThingsDB db = new ThingsDB(context);
        list = db.getThingsOrderedByDate();
        db.cleanup();
    }

    public List<SearchItem> search(String str) {
        List<SearchItem> searchItems = new ArrayList<>();

        if(str.length() < 2) {
            return searchItems;
        }

        for(Thing thing : list) {

            SearchItem item = new SearchItem();

            int index = thing.getTags().indexOf(str);
            while(index != -1) {
                item.getTagIndices().add(index);
                index = thing.getTags().indexOf(str, index + str.length());
            }

            index = thing.getLocation().indexOf(str);
            while(index != -1) {
                item.getLocIndices().add(index);
                index = thing.getLocation().indexOf(str, index + str.length());
            }

            if(!item.getTagIndices().isEmpty() || !item.getLocIndices().isEmpty()) {
                item.setThing(thing);
                searchItems.add(item);
            }
        }

        if(!searchItems.isEmpty()) {
            Collections.sort(searchItems);
        }

        return searchItems;
    }


}
