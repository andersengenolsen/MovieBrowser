package olsen.anders.movieapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import olsen.anders.movieapp.R;
import olsen.anders.movieapp.model.Genre;


/**
 * Custom adapter for RecyclerView with Strings, with inner class for the ViewHolder in the Adapter.
 * Used whenever a list of Strings is shown.
 *
 * @author Anders Engen Olsen
 * @see RecyclerAdapter
 * @see olsen.anders.movieapp.activities.MainActivity
 */

public class RecyclerStringListAdapter extends RecyclerAdapter {

    /**
     * Constructor.
     *
     * @param context Activity context
     * @param content ArrayList with String content in list
     */
    public RecyclerStringListAdapter(Context context, ArrayList<String> content) {
        super(context, content);
    }

    /**
     * Initiating ViewHolder with layout.
     *
     * @return RecyclerImageViewHolder
     * @see RecyclerListHolder(View)
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_list_string_item, parent, false);

        return new RecyclerListHolder(view);
    }

    /**
     * Setting content in Views in the ViewHolder.
     *
     * @param holder   ViewHolder
     * @param position position in adapter
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecyclerListHolder recyclerListHolder = (RecyclerListHolder) holder;
        Genre entry = (Genre) content.get(position);
        recyclerListHolder.entry.setText(entry.getGenre());
    }

    /**
     * Inner class, ViewHolder for the elements in the RecyclerView
     */
    private class RecyclerListHolder extends RecyclerView.ViewHolder {

        private TextView entry;

        /**
         * @param view Root
         */
        private RecyclerListHolder(View view) {
            super(view);
            entry = view.findViewById(R.id.entry);
        }
    }
}
