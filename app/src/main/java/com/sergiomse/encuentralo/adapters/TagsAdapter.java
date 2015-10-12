package com.sergiomse.encuentralo.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.sergiomse.encuentralo.R;
import com.sergiomse.encuentralo.utils.Colors;

import java.util.List;

/**
 * Created by sergiomse@gmail.com on 24/09/2015.
 */
public class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.BaseViewHolder> {

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

        public TagsViewHolder(View itemView) {
            super(itemView);
            etTag = (EditText) itemView.findViewById(R.id.etTag);
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

    public TagsAdapter(Context ctx, String photoPath, List<String> tags, String location, int imageHeight) {
        this.photoPath = photoPath;
        this.tags = tags;
        this.location = location;

        composeImage(imageHeight);
    }

    //TODO control the width of the image so no exceed the layout width
    private void composeImage(int newHeight) {
        Bitmap source = BitmapFactory.decodeFile(photoPath);
        int rgbAvg[] = getAverageColorRGB(source);
        Log.d(TAG, "Avg color = " + rgbAvg);
        String nearestColor = Colors.nearestMaterialColor(Color.rgb(rgbAvg[0], rgbAvg[1], rgbAvg[2]));
        Log.d(TAG, "nearestColor = " + nearestColor);
        int complColor = Colors.getMaterialColor(Colors.complementMaterialColor(nearestColor));
        Log.d(TAG, "complColor = " + Colors.complementMaterialColor(nearestColor));

        double ratio = source.getWidth() / (double) source.getHeight();

        int newWidth = (int) (ratio * newHeight);
        Matrix matrix = new Matrix();
        matrix.postTranslate(5, 5);
        matrix.postScale(((float) newWidth) / source.getWidth(), ((float) newHeight) / source.getHeight());

        //TODO use dp instead of px in the image border
        imageBitmap = Bitmap.createBitmap(newWidth + 10, newHeight + 10, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(imageBitmap);
        Paint paintComp = new Paint();
        paintComp.setStyle(Paint.Style.FILL);
        paintComp.setAntiAlias(true);
        paintComp.setColor(complColor);
        Path path = getRoundedRectPath(0, 0, canvas.getWidth(), canvas.getHeight(), 5, 5);
        canvas.drawPath(path, paintComp);
        canvas.drawBitmap(source, matrix, null);

    }

    //TODO Extract all this methods to a new utilities class
    private Path getRoundedRectPath(int left, int top, int right, int bottom, int rx, int ry) {
        int width = right - left;
        int height = bottom - top;
        Path path = new Path();
        path.moveTo(rx, 0);
        path.lineTo(width - rx, 0);
        path.quadTo(width, 0, width, ry);
        path.lineTo(width, height - ry);
        path.quadTo(width, height, width - rx, height);
        path.lineTo(rx, height);
        path.quadTo(0, height, 0, height - ry);
        path.lineTo(0, ry);
        path.quadTo(0, 0, rx, 0);
        return path;
    }

    private int[] getAverageColorRGB(Bitmap bitmap) {
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        int size = width * height;
        int pixelColor;
        int r, g, b;
        r = g = b = 0;
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                pixelColor = bitmap.getPixel(x, y);
                if (pixelColor == 0) {
                    size--;
                    continue;
                }
                r += Color.red(pixelColor);
                g += Color.green(pixelColor);
                b += Color.blue(pixelColor);
            }
        }
        r /= size;
        g /= size;
        b /= size;
        return new int[] {
                r, g, b
        };
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        BaseViewHolder viewHolder = null;

        if(i == 0) {    // image
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.recyclerview_image_preview, viewGroup, false);
            viewHolder = new ImageViewHolder(v);

        } else if (i == tags.size() + 1) {     // location
            EditText editText = new EditText(viewGroup.getContext());
            viewHolder = new LocationViewHolder(editText);

        } else {      // tags
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.recyclerview_tag, viewGroup, false);
            viewHolder = new TagsViewHolder(v);

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
