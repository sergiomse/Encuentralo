package com.sergiomse.encuentralo.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private static final String TAG = ViewHolder.class.getSimpleName();

        private OnThingItemClickListener listener;

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
            itemView.setOnClickListener(this);
        }

        public void setOnThingItemClickListener(OnThingItemClickListener listener) {
            this.listener = listener;
        }

        @Override
        public void onClick(View v) {
            listener.onThingItemClick((Long) rootLayout.getTag());
        }
    }

    public interface OnThingItemClickListener {
        void onThingItemClick(long id);
    }


    private DisplayMetrics dm;
    private SimpleDateFormat sdf;
    private OnThingItemClickListener listener;

    private List<Thing> things;

    public TagsAdapter(Context ctx) {
        this.things = things;
        this.dm = dm;
        this.listener = listener;

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

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.setOnThingItemClickListener(listener);
        if(i == 0) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) (150 * dm.density));
            viewHolder.rootLayout.setLayoutParams(params);
            viewHolder.rootLayout.setTag(-1);
//            viewHolder.tvTags.setText("");
        } else {
            Thing thing = things.get(i - 1);
            viewHolder.rootLayout.setTag(thing.getId());
            viewHolder.tvTags.setText(thing.getTags());
            viewHolder.tvLocation.setText(thing.getLocation());
            viewHolder.tvDate.setText(sdf.format(thing.getModifDate()));

            File imgFile = new File(thing.getImagePath());
            if(imgFile.exists()){
                String thumbFile = imgFile.getAbsolutePath().substring(0, imgFile.getAbsolutePath().lastIndexOf("."));
                thumbFile += "_THUMB.jpg";
                Bitmap myBitmap = BitmapFactory.decodeFile(thumbFile);
                viewHolder.imagePhoto.setImageBitmap(myBitmap);
            }
        }
    }

    @Override
    public int getItemCount() {
        return things.size() + 1;
    }
}
