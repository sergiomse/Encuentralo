package com.sergiomse.encuentralo.searcher;


import com.sergiomse.encuentralo.model.Thing;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.when;

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
        things.add(new Thing(0, "", "Cass \n Casita \n Fito cierra en caSü su gira triunfal \n No sin mi Caasa \n Patio del Tesorero, la empresa dueña de la Casaa de la Moneda y representada por Manuel Marañón", "", new Date()));
        when(searcher.getThings()).thenReturn(things);

        searcher.search("caca");
        for (SearchItem item : searcher.getSearchItems() ) {
            System.out.println(item.toString());
        }
    }

}