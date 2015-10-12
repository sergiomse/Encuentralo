package com.sergiomse.encuentralo.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * Created by sergiomse@gmail.com on 12/10/2015.
 */
public class TagEditView extends LinearLayout {

    private float density;
    private Paint paint;

    public TagEditView(Context context) {
        super(context);
        init();
    }

    public TagEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TagEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        density = getContext().getResources().getDisplayMetrics().density;
        paint = new Paint();

        EditText editText = new EditText(getContext());
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        editText.setLayoutParams(params);
        addView(editText);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int h = getHeight();
        int w = getWidth();

        Path path = new Path();
        path.moveTo(h / 2.0f, 0.0f);
        path.lineTo(w - h / 2.0f, 0.0f);

        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2 * density);
        canvas.drawPath(path, paint);

        super.onDraw(canvas);
    }
}
