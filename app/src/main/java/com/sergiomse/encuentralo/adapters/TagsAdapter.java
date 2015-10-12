package com.sergiomse.encuentralo.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.sergiomse.encuentralo.R;

import java.util.List;

/**
 * Created by sergiomse@gmail.com on 24/09/2015.
 */
public class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.BaseViewHolder>{

    private static final String TAG = TagsAdapter.class.getSimpleName();


    public static class BaseViewHolder extends RecyclerView.ViewHolder {

        public BaseViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class ImageViewHolder extends BaseViewHolder {

        private static final String TAG = ImageViewHolder.class.getSimpleName();

        public ImageView ivPhoto;

        public ImageViewHolder(View itemView) {
            super(itemView);
            ivPhoto = (ImageView) itemView.findViewById(R.id.ivPhoto);
        }
    }

    public static class TagsViewHolder extends BaseViewHolder {

        private static final String TAG = TagsViewHolder.class.getSimpleName();

        public EditText etTag;

        public TagsViewHolder(View itemView, final OnTagsAdapterChange listener) {
            super(itemView);

            etTag = (EditText) itemView.findViewById(R.id.etTag);
            etTag.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            listener.newLine();
                            return true;
                    }
                    return false;
                }
            });
        }
    }

    public static class LocationViewHolder extends BaseViewHolder {

        private static final String TAG = LocationViewHolder.class.getSimpleName();

        public EditText etLocation;

        public LocationViewHolder(View itemView) {
            super(itemView);
            etLocation = (EditText) itemView;
        }
    }




    private String photoPath;
    private Bitmap imageBitmap;
    private List<String> tags;
    private String location;
    private OnTagsAdapterChange listener;

    public TagsAdapter(Context ctx, String photoPath, List<String> tags, String location, int imageHeight) {
        this.photoPath = photoPath;
        this.tags = tags;
        this.location = location;

//        composeImage(imageHeight);
    }







    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        BaseViewHolder viewHolder = null;

        if(i == 0) {    // image
//            View v = LayoutInflater.from(viewGroup.getContext())
//                    .inflate(R.layout.recyclerview_image_preview, viewGroup, false);
//            viewHolder = new ImageViewHolder(v);

        } else if (i == tags.size() + 1) {     // location
            EditText editText = new EditText(viewGroup.getContext());
            viewHolder = new LocationViewHolder(editText);

        } else {      // tags
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.recyclerview_tag, viewGroup, false);
            viewHolder = new TagsViewHolder(v, listener);

        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder viewHolder, int i) {

        if(viewHolder instanceof ImageViewHolder) {
            ImageViewHolder imageViewHolder = (ImageViewHolder) viewHolder;
            imageViewHolder.ivPhoto.setImageBitmap(imageBitmap);

        } else if(viewHolder instanceof TagsViewHolder) {
            TagsViewHolder tagsViewHolder = (TagsViewHolder) viewHolder;
            tagsViewHolder.etTag.setText(tags.get(i - 1));

        } else if(viewHolder instanceof LocationViewHolder) {
            LocationViewHolder locationViewHolder = (LocationViewHolder) viewHolder;
            locationViewHolder.etLocation.setText(location);
        }
    }

    @Override
    public int getItemCount() {
        //included image and location
        return tags.size() + 2;
    }
}
