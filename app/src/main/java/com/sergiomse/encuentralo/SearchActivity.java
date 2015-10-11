package com.sergiomse.encuentralo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.sergiomse.encuentralo.database.ThingsDB;
import com.sergiomse.encuentralo.model.Thing;

import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText etSearch;

    private List<Thing> things;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ThingsDB db = new ThingsDB(this);
        things = db.getThingsOrderedByDate();
        db.cleanup();

        etSearch = (EditText) findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String search = etSearch.getText().toString();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

}
