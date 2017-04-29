package fi.aalto.msp2017.shoppinglist.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import fi.aalto.msp2017.shoppinglist.R;
import fi.aalto.msp2017.shoppinglist.models.IItem;

/**
 * Created by sunil on 14-04-2017.
 */

/*
 * List item adapter class
 * sets the layout inflater
 */

public class ListItemAdapter extends ArrayAdapter<IItem> {
    private Activity context;
    private List<IItem> listItems;
    static class listItemHolder {
        ImageView thumbnail;
        TextView text;
    }
    private static final String LOG_TAG = ListItemAdapter.class.getSimpleName();

    public ListItemAdapter(Activity context, int layoutResourceId,
                           List<IItem> listItems) {
        super(context, layoutResourceId, listItems);
        this.context = context;
        this.listItems = listItems;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final listItemHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
            convertView = inflater.inflate(R.layout.list_item_view, parent, false);

            viewHolder = new listItemHolder();
            viewHolder.thumbnail = (ImageView) convertView.findViewById(R.id.list_item_thumbnail);
            viewHolder.text = (TextView) convertView.findViewById(R.id.list_item_text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (listItemHolder) convertView.getTag();
        }
        final IItem listItemEntry = getItem(position);
        viewHolder.text.setText(listItemEntry.getItemName());
        int id = context.getResources().getIdentifier(listItemEntry.getImageName(), "drawable", context.getPackageName());
        viewHolder.thumbnail.setImageResource(id);

        return convertView;
    }

}