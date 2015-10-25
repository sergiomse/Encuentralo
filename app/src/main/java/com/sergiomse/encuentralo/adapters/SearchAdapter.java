package com.sergiomse.encuentralo.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sergiomse.encuentralo.R;
import com.sergiomse.encuentralo.searcher.SearchItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sergiomse@gmail.com on 24/09/2015.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private static final String TAG = ViewHolder.class.getSimpleName();

        private OnThingItemClickListener listener;

        public LinearLayout llSearchItem;
        public TextView tvSearchedText;

        public ViewHolder(View itemView) {
            super(itemView);
            llSearchItem = (LinearLayout) itemView.findViewById(R.id.searchItem);
            tvSearchedText = (TextView) itemView.findViewById(R.id.tvSearchedText);
            itemView.setOnClickListener(this);
        }

        public void setOnThingItemClickListener(OnThingItemClickListener listener) {
            this.listener = listener;
        }

        @Override
        public void onClick(View v) {
            listener.onThingItemClick((Long) llSearchItem.getTag());
        }
    }

    public interface OnThingItemClickListener {
        void onThingItemClick(long id);
    }


    private OnThingItemClickListener listener;

    private List<SearchItem> items;

    public SearchAdapter() {
        items = new ArrayList<>();
    }

    public SearchAdapter(List<SearchItem> items , OnThingItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public void setOnThingItemClickListener(OnThingItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recyclerview_search_item, viewGroup, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.setOnThingItemClickListener(listener);

        viewHolder.llSearchItem.setTag( items.get(i).getThing().getId() );
        viewHolder.tvSearchedText.setText(Html.fromHtml( items.get(i).getFormattedText()) );
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).getThing().getId();
    }

    public void addItem(SearchItem item) {
        items.add(item);
        notifyDataSetChanged();
    }

    public void addAllItem(List<SearchItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void deleteItem(SearchItem item) {
        int index = items.indexOf(item);
        items.remove(item);
        notifyDataSetChanged();
    }

    public void deleteAll() {
        items.clear();
        notifyDataSetChanged();
    }
}
