package anders.olsen.moviebrowser.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import anders.olsen.moviebrowser.R;
import anders.olsen.moviebrowser.adapter.RecyclerAdapter;
import anders.olsen.moviebrowser.listener.ListFragmentListener;
import anders.olsen.moviebrowser.listener.RecyclerClickListener;
import anders.olsen.moviebrowser.model.MediaObject;

/**
 * Abstract class, used for when showing RecyclerViews in a list.
 * @author Anders Engen Olsen
 * @see anders.olsen.moviebrowser.listener.RecyclerClickListener
 */
public abstract class RecyclerListFragment<AnyType> extends Fragment
        implements RecyclerClickListener.OnItemClickListener {

    /**
     * Interface
     */
    protected ListFragmentListener fragmentListener;

    /**
     * Orientation change key
     */
    protected final String CONTENT_KEY = "content";

    /**
     * ArrayList with content in list
     */
    protected List<AnyType> contentList;

    /**
     * The adapter.
     */
    protected RecyclerAdapter adapter;

    /**
     * Activites must implement the local interface
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            fragmentListener = (ListFragmentListener) getActivity();
        } catch (ClassCastException err) {
            throw new ClassCastException(getActivity().getClass().getSimpleName()
                    + " must implement " + ListFragmentListener.class.getSimpleName());
        }
    }

    /**
     * Implementation of RecyclerClickListener.OnItemClick
     */
    @Override
    public void onItemClick(RecyclerAdapter adapter, int position) {
        fragmentListener.onItemClicked(adapter, position);
    }


    /**
     * Inflating layout.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        if (contentList == null)
            contentList = new ArrayList<>();
        setUpRecycler(view);
        return view;
    }

    /**
     * Setting up the recycler views in the layout.
     *
     * @see anders.olsen.moviebrowser.adapter.RecyclerImageAdapter
     * @see RecyclerClickListener
     * @see MediaObject
     */
    protected abstract void setUpRecycler(View v);

    /**
     * Replacing the current content in the list with new content.
     *
     * @param contentList list with new content
     */
    public void setContent(List<AnyType> contentList) {
        this.contentList = contentList;
        notifyAdapter();
    }

    /**
     * Appending content to the current list in the fragment.
     *
     * @param contentList list with content to append
     */
    public void appendContent(List<AnyType> contentList) {
        if (this.contentList == null || this.contentList.isEmpty())
            setContent(contentList);
        else {
            this.contentList.addAll(contentList);
            notifyAdapter();
        }
    }

    /**
     * Notifying the adapter about changes in the list content.
     */
    protected void notifyAdapter() {
        if (adapter != null) {
            adapter.setContent(this.contentList);
            adapter.notifyDataSetChanged();
        }

    }


}
