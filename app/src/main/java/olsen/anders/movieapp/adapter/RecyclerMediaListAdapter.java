package olsen.anders.movieapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import olsen.anders.movieapp.R;
import olsen.anders.movieapp.model.MediaObject;


/**
 * Custom adapter for RecyclerView, with inner class for the ViewHolder in the Adapter.
 * Used whenever a list of mediaobjects is shown.
 *
 * @author Anders Engen Olsen
 * @see RecyclerAdapter
 * @see olsen.anders.movieapp.activities.MainActivity
 */

public class RecyclerMediaListAdapter extends RecyclerAdapter<MediaObject> {

    /**
     * Constructor.
     *
     * @param context      Activity context
     * @param mediaObjects ArrayList with mediaobjects
     */
    public RecyclerMediaListAdapter(Context context, ArrayList<MediaObject> mediaObjects) {
        super(context, mediaObjects);
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
        View view = inflater.inflate(R.layout.recycler_list_media_item, parent, false);

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

        MediaObject mediaObject = (MediaObject) content.get(position);

        recyclerListHolder.title.setText(mediaObject.getTitle());
        recyclerListHolder.genre.setText(mediaObject.getGenre());

        Picasso.with(context).load(mediaObject.getImagePath())
                .error(R.drawable.ic_movie_black)
                .placeholder(R.drawable.ic_movie_black)
                .into(recyclerListHolder.poster);
    }

    /**
     * Inner class, ViewHolder for the elements in the RecyclerView
     */
    private class RecyclerListHolder extends RecyclerView.ViewHolder {

        private ImageView poster;
        private TextView title;
        private TextView genre;

        /**
         * @param view Root
         */
        private RecyclerListHolder(View view) {
            super(view);

            poster = view.findViewById(R.id.poster);
            title = view.findViewById(R.id.title);
            genre = view.findViewById(R.id.genre);
        }
    }
}
