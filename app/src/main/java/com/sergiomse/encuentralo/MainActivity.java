package com.sergiomse.encuentralo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ScrollView scrollView;
    private LinearLayout buttonsLayout;
    private LinearLayout listLayout;

    private FrameLayout.LayoutParams buttonsLayoutParams;

    private DisplayMetrics dm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dm = getResources().getDisplayMetrics();

        buttonsLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (150 * dm.density));

        scrollView      = (ScrollView) findViewById(R.id.scrollView);
        buttonsLayout   = (LinearLayout) findViewById(R.id.buttonsLayout);
        listLayout      = (LinearLayout) findViewById(R.id.listLayout);

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

            @Override
            public void onScrollChanged() {
                int scrollY = scrollView.getScrollY();
                Log.d(TAG, "Scroll Y = " + scrollY);

                if(scrollY > 0 && scrollY < 102 * dm.density) {
                    buttonsLayoutParams.height = (int) (150 * dm.density - scrollY);
                } else if(scrollY > 102 * dm.density) {
                    buttonsLayoutParams.height = (int) (48 * dm.density);
                } else if(scrollY == 0) {
                    buttonsLayoutParams.height = (int) (150 * dm.density);
                }
                buttonsLayout.setLayoutParams(buttonsLayoutParams);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
