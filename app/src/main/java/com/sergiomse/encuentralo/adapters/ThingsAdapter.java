package com.sergiomse.encuentralo.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
public class ThingsAdapter extends RecyclerView.Adapter<ThingsAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout rootLayout;
        public ImageView imagePhoto;
        public TextView tvTags;
        public TextView tvLocation;
        public TextView tvDate;

        public ViewHolder(View itemView) {
            super(itemView);
            rootLayout = (RelativeLayout) itemView;
            imagePhoto = (ImageView) itemView.findViewById(R.id.imagePhoto);
            tvTags = (TextView) itemView.findViewById(R.id.tvTags);
            tvLocation = (TextView) itemView.findViewById(R.id.tvLocation);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
        }
    }

    private DisplayMetrics dm;
    private SimpleDateFormat sdf;

    private List<Thing> things;

    public ThingsAdapter(List<Thing> things, DisplayMetrics dm) {
        this.things = things;
        this.dm = dm;

        sdf = new SimpleDateFormat("'El 'd' de 'MMM' de 'yyyy' a las 'HH:mm");
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recyclerview_things, viewGroup, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        if(i == 0) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) (150 * dm.density));
            viewHolder.rootLayout.setLayoutParams(params);
//            viewHolder.tvTags.setText("");
        } else {
            Thing thing = things.get(i - 1);
            viewHolder.tvTags.setText(thing.getTags());
            viewHolder.tvLocation.setText(thing.getLocation());
            viewHolder.tvDate.setText(sdf.format(thing.getModifDate()));

            File imgFile = new File(thing.getImagePath());
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                viewHolder.imagePhoto.setImageBitmap(myBitmap);
            }
        }
    }

    @Override
    public int getItemCount() {
        return things.size() + 1;
    }
}
