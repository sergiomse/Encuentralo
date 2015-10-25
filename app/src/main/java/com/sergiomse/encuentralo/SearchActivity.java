package com.sergiomse.encuentralo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sergiomse.encuentralo.adapters.SearchAdapter;
import com.sergiomse.encuentralo.searcher.SearchItem;
import com.sergiomse.encuentralo.searcher.Searcher;
import com.sergiomse.encuentralo.views.SearchLinearLayoutManager;

import org.apache.commons.collections4.sequence.CommandVisitor;
import org.apache.commons.collections4.sequence.EditScript;
import org.apache.commons.collections4.sequence.SequencesComparator;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements SearchAdapter.OnThingItemClickListener {

    private static final String TAG = SearchActivity.class.getSimpleName();


    private EditText etSearch;
    private RecyclerView searchRecyclerView;
    private Searcher searcher;
    private TextView noSearchItems;
    private List<SearchItem> searchItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.title_activity_search));

        searcher = new Searcher(this);

        noSearchItems = (TextView) findViewById(R.id.noSearchItems);
        searchRecyclerView = (RecyclerView) findViewById(R.id.searchRecyclerView);

        SearchLinearLayoutManager layoutManager = new SearchLinearLayoutManager(this);
        searchRecyclerView.setLayoutManager(layoutManager);

        SearchAdapter adapter = new SearchAdapter();
        adapter.setHasStableIds(true);
        adapter.setOnThingItemClickListener(this);
        searchRecyclerView.setAdapter(adapter);

        etSearch = (EditText) findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = etSearch.getText().toString();
                searcher.search(str);
                List<SearchItem> items = searcher.getSearchItems();

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
                    SequencesComparator<SearchItem> sequencesComparator = new SequencesComparator<>(searchItems, items);
                    EditScript<SearchItem> script = sequencesComparator.getScript();

                    script.visit(new CommandVisitor<SearchItem>() {
                        @Override
                        public void visitInsertCommand(SearchItem item) {
                            Log.d(TAG, "Insert: " + item);
                            adapter.addItem(item);
                        }

                        @Override
                        public void visitKeepCommand(SearchItem item) {
                            Log.d(TAG, "Keep: " + item);
                        }

                        @Override
                        public void visitDeleteCommand(SearchItem item) {
                            Log.d(TAG, "Delete: " + item);
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

    @Override
    public void onThingItemClick(long id) {
        Intent intent = new Intent(this, PhotoLocationActivity.class);
        intent.putExtra("state", PhotoLocationActivity.VIEW_STATE);
        intent.putExtra("id", id);
        startActivity(intent);
    }
}
