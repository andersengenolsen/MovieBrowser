package olsen.anders.movieapp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import olsen.anders.movieapp.R;
import olsen.anders.movieapp.adapter.RecyclerAdapter;
import olsen.anders.movieapp.adapter.RecyclerMediaListAdapter;
import olsen.anders.movieapp.listener.RecyclerClickListener;
import olsen.anders.movieapp.model.MediaObject;


/**
 * Fragment which show a list of MediaObjects in RecyclerViews.
 * In the fragments onCreateView, an ArrayList with MediaObjects is obtained,
 * with key MEDIA_OBJECT_LIST_KEY.
 *
 * @author Anders Engen Olsen
 * @see RecyclerMediaListAdapter
 * @see RecyclerClickListener
 */

public class RecyclerListFragment extends Fragment
        implements RecyclerClickListener.OnItemClickListener {

    /**
     * Local interface. Must be implemented in activities. Fired when element in list is clicked.
     */
    public interface OnItemClickedListener {
        void onItemClicked(MediaObject mediaObject);
    }

    /**
     * Orientation change key
     */
    private final String MEDIA_OBJECTS = "media_objects";

    /**
     * Interface
     */
    private OnItemClickedListener onItemClicked;

    /**
     * ArrayList with mediaobjects
     */
    private ArrayList<MediaObject> mediaObjects;

    /**
     * Custom adapter for the recyclerviews
     */
    private RecyclerMediaListAdapter adapter;

    /**
     * Implementation of RecyclerClickListener.OnItemClick
     */
    @Override
    public void onItemClick(RecyclerAdapter adapter, int position) {
        onItemClicked.onItemClicked((MediaObject) adapter.getElement(position));
    }

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
     * Obtaining bundle with mediaobjects.
     *
     * @see #setUpRecycler(View)
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            mediaObjects = savedInstanceState.getParcelableArrayList(MEDIA_OBJECTS);
        } else if (mediaObjects == null) {
            mediaObjects = new ArrayList<>();
        }

        View view = inflater.inflate(R.layout.fragment_list, container, false);
        setUpRecycler(view);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MEDIA_OBJECTS, mediaObjects);
    }

    /**
     * Setting up the recycler views in the layout.
     *
     * @see olsen.anders.movieapp.adapter.RecyclerImageAdapter
     * @see RecyclerClickListener
     * @see MediaObject
     */
    private void setUpRecycler(View v) {

        adapter = new RecyclerMediaListAdapter(getActivity(), mediaObjects);

        RecyclerView recyclerView = v.findViewById(R.id.recycler_list);

        recyclerView.addOnItemTouchListener(new RecyclerClickListener(getActivity(), this));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    /**
     * Updating the list with mediaobjects
     *
     * @param mediaObjects list with mediaobjects
     */
    public void setMediaObjects(ArrayList<MediaObject> mediaObjects) {
        this.mediaObjects = mediaObjects;

        if (adapter != null) {
            adapter.setContent(mediaObjects);
            adapter.notifyDataSetChanged();
        }
    }
}
