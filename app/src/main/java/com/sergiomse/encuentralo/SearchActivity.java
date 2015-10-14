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
import android.widget.TextView;

import com.sergiomse.encuentralo.adapters.SearchAdapter;
import com.sergiomse.encuentralo.database.ThingsDB;
import com.sergiomse.encuentralo.model.Thing;
import com.sergiomse.encuentralo.utils.Searcher;

import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText etSearch;
    private RecyclerView searchRecyclerView;
    private Searcher searcher;
    private TextView noSearchItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searcher = new Searcher(this);

        noSearchItems = (TextView) findViewById(R.id.noSearchItems);
        searchRecyclerView = (RecyclerView) findViewById(R.id.searchRecyclerView);
        searchRecyclerView.setVisibility(View.GONE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        searchRecyclerView.setLayoutManager(layoutManager);


        etSearch = (EditText) findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = etSearch.getText().toString();
                List<Searcher.SearchItem> items = searcher.search(str);

                if(!items.isEmpty()) {
                    noSearchItems.setVisibility(View.GONE);
                    searchRecyclerView.setVisibility(View.VISIBLE);
                    SearchAdapter adapter = new SearchAdapter(items);
                    searchRecyclerView.setAdapter(adapter);
                } else {
                    noSearchItems.setVisibility(View.VISIBLE);
                    searchRecyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

}
