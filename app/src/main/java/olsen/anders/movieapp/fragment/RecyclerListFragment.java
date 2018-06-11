package olsen.anders.movieapp.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import olsen.anders.movieapp.R;
import olsen.anders.movieapp.adapter.RecyclerAdapter;
import olsen.anders.movieapp.listener.RecyclerClickListener;
import olsen.anders.movieapp.model.MediaObject;

/**
 * Fragment which show a list of MediaObjects in RecyclerViews.
 * In the fragments onCreateView, an ArrayList with MediaObjects is obtained,
 * with key MEDIA_OBJECT_LIST_KEY.
 *
 * @author Anders Engen Olsen
 * @see olsen.anders.movieapp.adapter.RecyclerMediaListAdapter
 * @see olsen.anders.movieapp.listener.RecyclerClickListener
 */

public abstract class RecyclerListFragment<AnyType> extends Fragment
        implements RecyclerClickListener.OnItemClickListener {

    /**
     * Local interface. Must be implemented in activities. Fired when element in list is clicked.
     */
    public interface OnItemClickedListener {
        void onItemClicked(RecyclerAdapter adapter, int position);
    }

    /**
     * Implementation of RecyclerClickListener.OnItemClick
     */
    @Override
    public void onItemClick(RecyclerAdapter adapter, int position) {
        onItemClicked.onItemClicked(adapter, position);
    }

    /**
     * Interface
     */
    protected OnItemClickedListener onItemClicked;

    /**
     * Orientation change key
     */
    protected final String CONTENT_KEY = "content";

    /**
     * ArrayList with content in list
     */
    protected ArrayList<AnyType> contentList;

    /**
     * The adapter.
     */
    protected RecyclerAdapter adapter;

    /**
     * Activites must implement the local interface
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            onItemClicked = (OnItemClickedListener) getActivity();
        } catch (ClassCastException err) {
            throw new ClassCastException(getActivity().getClass().getSimpleName()
                    + " must implement " + RecyclerListFragment.class.getSimpleName());
        }
    }

    /**
     * Inflating layout.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        setUpRecycler(view);
        return view;
    }

    /**
     * Setting up the recycler views in the layout.
     *
     * @see olsen.anders.movieapp.adapter.RecyclerImageAdapter
     * @see RecyclerClickListener
     * @see MediaObject
     */
    protected abstract void setUpRecycler(View v);

    /**
     * Updating the list with content
     *
     * @param contentList list with content
     */
    public void setContent(ArrayList<AnyType> contentList) {
        this.contentList = contentList;

        if (adapter != null) {
            adapter.setContent(contentList);
            adapter.notifyDataSetChanged();
        }
    }
}
