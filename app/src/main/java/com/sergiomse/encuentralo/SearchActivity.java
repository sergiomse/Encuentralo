package com.sergiomse.encuentralo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.sergiomse.encuentralo.adapters.SearchAdapter;
import com.sergiomse.encuentralo.database.ThingsDB;
import com.sergiomse.encuentralo.model.Thing;
import com.sergiomse.encuentralo.utils.Searcher;
import com.sergiomse.encuentralo.views.SearchLinearLayoutManager;

import org.apache.commons.collections4.sequence.CommandVisitor;
import org.apache.commons.collections4.sequence.EditScript;
import org.apache.commons.collections4.sequence.SequencesComparator;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText etSearch;
    private RecyclerView searchRecyclerView;
    private Searcher searcher;
    private TextView noSearchItems;
    private List<Searcher.SearchItem> searchItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Búsqueda");

        searcher = new Searcher(this);

        noSearchItems = (TextView) findViewById(R.id.noSearchItems);
        searchRecyclerView = (RecyclerView) findViewById(R.id.searchRecyclerView);

        SearchLinearLayoutManager layoutManager = new SearchLinearLayoutManager(this);
        searchRecyclerView.setLayoutManager(layoutManager);

        SearchAdapter adapter = new SearchAdapter();
        adapter.setHasStableIds(true);
        searchRecyclerView.setAdapter(adapter);

        etSearch = (EditText) findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = etSearch.getText().toString();
                List<Searcher.SearchItem> items = searcher.search(str);

                if( searchItems.isEmpty()  &&  !items.isEmpty()) {
                    //from empty to a new list
                    showListViews();

                    SearchAdapter adapter = (SearchAdapter) searchRecyclerView.getAdapter();
                    adapter.addAllItem(items);

                } else if (!searchItems.isEmpty()  &&  items.isEmpty()) {
                    //from list to empty
                    hideListViews();

                    SearchAdapter adapter = (SearchAdapter) searchRecyclerView.getAdapter();
                    adapter.deleteAll();

                } else if(!searchItems.isEmpty()  &&  !items.isEmpty()) {
                    //from list to list

                    final SearchAdapter adapter = (SearchAdapter) searchRecyclerView.getAdapter();
                    SequencesComparator<Searcher.SearchItem> sequencesComparator = new SequencesComparator<>(searchItems, items);
                    EditScript<Searcher.SearchItem> script = sequencesComparator.getScript();

                    script.visit(new CommandVisitor<Searcher.SearchItem>() {
                        @Override
                        public void visitInsertCommand(Searcher.SearchItem item) {
                            System.out.println("Insert: " + item);
                            adapter.addItem(item);
                        }

                        @Override
                        public void visitKeepCommand(Searcher.SearchItem item) {
                            System.out.println("Keep: " + item);
                        }

                        @Override
                        public void visitDeleteCommand(Searcher.SearchItem item) {
                            System.out.println("Delete: " + item);
                            adapter.deleteItem(item);
                        }
                    });

                }

                searchItems = items;
            }

            private void showListViews() {
                noSearchItems.setVisibility(View.GONE);
            }


            private void hideListViews() {
                noSearchItems.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

}
