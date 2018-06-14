package anders.olsen.moviebrowser.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import anders.olsen.moviebrowser.R;
import anders.olsen.moviebrowser.model.MediaObject;


/**
 * Custom adapter for RecyclerView, with inner class for the ViewHolder in the Adapter.
 * The RecyclerViews is used in MainActivity.
 *
 * @author Anders Engen Olsen
 * @see RecyclerAdapter
 * @see anders.olsen.moviebrowser.activities.MainActivity
 */
public class RecyclerImageAdapter extends RecyclerAdapter<MediaObject> {

    /**
     * Constructor.
     *
     * @param context Activity context
     * @param content ArrayList with mediaobjects
     */
    public RecyclerImageAdapter(Context context, List<MediaObject> content) {
        super(context, content);
    }

    /**
     * Initiating ViewHolder with layout.
     *
     * @return RecyclerImageViewHolder
     * @see RecyclerImageViewHolder(View)
     */
    @Override
    public RecyclerImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_image_view, parent, false);

        return new RecyclerImageViewHolder(view);
    }

    /**
     * Setting content in Views in the ViewHolder.
     *
     * @param holder   ViewHolder
     * @param position position in adapter
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // Fetching correct MediaObject
        MediaObject mediaObject = (MediaObject) content.get(position);

        RecyclerImageViewHolder imageViewHolder = (RecyclerImageViewHolder) holder;

        Picasso.with(context).load(mediaObject.getImagePath())
                .error(R.drawable.ic_movie_black)
                .placeholder(R.drawable.ic_movie_black)
                .into(imageViewHolder.poster);
    }

    /**
     * Custom ViewHolder for the RecyclerView.
     * recycler_image_view.xml
     *
     * @author olsen engen olsen
     * @link http://square.github.io/picasso/
     */
    class RecyclerImageViewHolder extends RecyclerView.ViewHolder {

        ImageView poster;

        /**
         * @param view Root layout
         */
        private RecyclerImageViewHolder(View view) {
            super(view);
            poster = view.findViewById(R.id.poster);
        }
    }
}





