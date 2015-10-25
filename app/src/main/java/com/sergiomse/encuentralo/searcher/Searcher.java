package com.sergiomse.encuentralo.searcher;

import android.content.Context;

import com.sergiomse.encuentralo.database.ThingsDB;
import com.sergiomse.encuentralo.model.Thing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by sergiomse@gmail.com on 21/10/2015.
 */
public class Searcher {

    private List<Thing> things;
    private List<SearchItem> searchItems = new ArrayList<>();

    public Searcher(List<Thing> things) {
        this.things = things;
    }

    public Searcher(Context context) {
        ThingsDB db = new ThingsDB(context);
        things = db.getThingsOrderedByDate();
        db.cleanup();
    }

    public List<Thing> getThings() {
        return things;
    }

    public List<SearchItem> getSearchItems() {
        return searchItems;
    }

    public void search(String searched) {
        searchItems = new ArrayList<>();

        if(searched.length() < 2) {
            return;
        }

        String searchedWords[] = searched.split("[\\s\\n]");

        for(Thing thing : things) {
            SearchItem searchItem = new SearchItem(thing);

            //String tagWords[] = thing.getTags().split("[\\s\\n]");
            List<SplitWord> tagWords = getSplittedWords(thing.getTags());

            for(SplitWord tagWord : tagWords) {
                for(String searchedWord : searchedWords) {
                    float confidence = DamerauLevenshteinAlgorithm.calculateConfidence(tagWord.getWord(), searchedWord);
                    if (confidence > 0.5f) {
                        searchItem.addMatch(new Match(tagWord.getStart(), tagWord.getEnd(), confidence));
                    }
                }
            }

            if(searchItem.getTotalConfidence() > 0.0f) {
                searchItems.add(searchItem);
            }
        }

        for(SearchItem item : searchItems) {
            StringBuilder sb = new StringBuilder();

            int start = 0;
            int end   = 0;

            for(Match match : item.getMatches()) {
                if(start != match.getStart()) {
                    sb.append(item.getThing().getTags().substring( start, match.getStart() ));
                }

                sb.append("<strong>");
                start = match.getStart();
                end   = match.getEnd();
                sb.append(item.getThing().getTags().substring( start, end ));
                sb.append("</strong>");

                start = end;
            }

            if(!item.getMatches().isEmpty()  &&  item.getMatch(item.getMatches().size() - 1).getEnd() < item.getThing().getTags().length()) {
                sb.append(item.getThing().getTags().substring(item.getMatch(item.getMatches().size() - 1).getEnd()));
            }

            item.setFormattedText(sb.toString().replaceAll("\\n", " <font color=\"black\"><strong>|</strong></font> "));
        }

        Collections.sort(searchItems, new Comparator<SearchItem>() {
            @Override
            public int compare(SearchItem si1, SearchItem si2) {
                if ( si1.getExactMatches() != si2.getExactMatches() ) {
                    return si2.getExactMatches() - si1.getExactMatches();
                } else {
                    return (int) ((si2.getTotalConfidence() - si1.getTotalConfidence()) * 100);
                }
            }
        });
    }

    /**
     * Return a list of split words, i.e. words split by an '\n' or space character and
     * the start and end index of the word inside the whole string
     *
     * @param str
     * @return
     */
    private List<SplitWord> getSplittedWords(String str) {
        List<SplitWord> words = new ArrayList<>();

        int start = 0;
        int end = 0;
        boolean isWord = true;

        for (int i = 0; i < str.length(); i++) {

            if ( isWord  && (str.charAt(i) == '\n'  ||  str.charAt(i) == ' ') ) {
                    isWord = false;
                    end = i;
                    words.add(new SplitWord(str.substring(start, end), start, end));
                    continue;
            }

            if ( !isWord  &&  str.charAt(i) != '\n'  &&  str.charAt(i) != ' ') {
                isWord = true;
                start = i;
            }

        }

        if(isWord) {
            words.add(new SplitWord(str.substring(start, str.length()), start, str.length()));
        }

        return words;
    }

    public static void main(String args[]) {
        List<Thing> things = new ArrayList<>();
        things.add(new Thing(1, "", "Casa", "", new Date()));
        things.add(new Thing(0, "", "Cass \n Casita \n Fito cierra en caSü su gira triunfal \n No sin mi Caasa \n Patio del Tesorero, la empresa dueña de la Casaa de la Moneda y representada por Manuel Marañón", "", new Date()));
        things.add(new Thing(0, "", "Casa \n Casita \n Fito cierra en caSa su gira triunfal \n No sin mi Caasa \n Patio del Tesorero, la empresa dueña de la Casa de la Moneda y representada por Manuel Marañón", "", new Date()));

        Searcher searcher = new Searcher(things);

        searcher.search("casa");

        for (SearchItem item : searcher.getSearchItems() ) {
            System.out.println(item.toString());
        }

        List<SplitWord> splitted = searcher.getSplittedWords("Casa \n Casita \n Fito cierra en caSa su gira triunfal \n No sin mi Caasa \n Patio del Tesorero, la empresa dueña de la Casa de la Moneda y representada por Manuel Marañón           ");
        for (SplitWord item : splitted ) {
            System.out.println(item.toString());
        }
    }
}
