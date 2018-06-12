package olsen.anders.movieapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import olsen.anders.movieapp.model.MediaObject;


/**
 * Generic, abstract superclass for different Adapters used for RecyclerViews.
 *
 * @author Anders Engen Olsen
 */
public abstract class RecyclerAdapter<AnyType> extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    protected List<AnyType> content;

    /**
     * Constructor.
     *
     * @param context Activity context
     * @param content List with AnyType to show.
     */
    RecyclerAdapter(Context context, List<AnyType> content) {
        this.context = context;
        this.content = content;
    }

    /**
     * @return int number of objects in adapter.
     */
    @Override
    public int getItemCount() {
        return (null != content ? content.size() : 0);
    }

    /**
     * @param pos position in adapter
     * @return Object in position
     */
    public AnyType getElement(int pos) {
        return (null != content ? content.get(pos) : null);
    }

    /**
     * Setting content in adapter
     */
    public void setContent(List<AnyType> content) {
        this.content = content;
    }
}
