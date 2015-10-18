package com.sergiomse.encuentralo.views;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

/**
 * Created by sergiomse@gmail.com on 18/10/2015.
 */
public class SearchLinearLayoutManager extends LinearLayoutManager {

    public SearchLinearLayoutManager(Context context) {
        super(context);
    }

    public SearchLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public SearchLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    public boolean supportsPredictiveItemAnimations() {
        return true;
    }
}
