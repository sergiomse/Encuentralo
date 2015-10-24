package com.sergiomse.encuentralo.searcher;


import android.content.Context;

import com.sergiomse.encuentralo.model.Thing;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by sergiomse@gmail.com on 24/10/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class SearcherTest {

    @Mock
    private Searcher searcher;

    @Test
    public void testSearch() throws Exception {
        List<Thing> things = new ArrayList<>();
        things.add(new Thing(0, "", "Casa \n Choni \n PePelUís \n La maRe que la cata \n Mi caspa", "", new Date()));
        when(searcher.getThings()).thenReturn(things);

        searcher.search("caca");
        for (SearchItem item : searcher.getSearchItems() ) {
            System.out.println(item.toString());
        }
    }

}