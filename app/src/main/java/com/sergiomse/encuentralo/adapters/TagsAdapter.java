package com.sergiomse.encuentralo.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sergiomse.encuentralo.R;
import com.sergiomse.encuentralo.model.Thing;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by sergiomse@gmail.com on 24/09/2015.
 */
public class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private static final String TAG = ViewHolder.class.getSimpleName();

        public EditText etTag;

        public ViewHolder(View itemView) {
            super(itemView);
            etTag = (EditText) itemView.findViewById(R.id.etTag);
        }
    }

    private List<String> tags;

    public TagsAdapter(Context ctx, List<String> tags) {
        this.tags = tags;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.tag_recyclerview, viewGroup, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        // image
        if(i == 0) {


        }
        // location
        else if (i == tags.size() - 1) {

        }
        // tags
        else {
            viewHolder.etTag.setText(tags.get(i - 1));
        }
    }

    @Override
    public int getItemCount() {
        //included image and location
        return tags.size() + 2;
    }
}
