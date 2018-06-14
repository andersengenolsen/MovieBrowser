package anders.olsen.moviebrowser.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import anders.olsen.moviebrowser.R;
import anders.olsen.moviebrowser.adapter.RecyclerMediaListAdapter;
import anders.olsen.moviebrowser.listener.EndlessRecyclerViewScrollListener;
import anders.olsen.moviebrowser.listener.RecyclerClickListener;
import anders.olsen.moviebrowser.model.MediaObject;


/**
 * Fragment which show a list of MediaObjects in RecyclerViews.
 * In the fragments onCreateView, an ArrayList with MediaObjects is obtained,
 * with key MEDIA_OBJECT_LIST_KEY.
 *
 * @author Anders Engen Olsen
 * @see RecyclerMediaListAdapter
 * @see RecyclerClickListener
 */

public class RecyclerMediaListFragment extends RecyclerListFragment<MediaObject> {

    /**
     * Obtaining bundle with mediaobjects.
     *
     * @see #setUpRecycler(View)
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        if (savedInstanceState != null) {
            contentList = savedInstanceState.getParcelableArrayList(CONTENT_KEY);
            notifyAdapter();
        }

        return view;
    }

    /**
     * Retaining content in list.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(CONTENT_KEY, (ArrayList<? extends Parcelable>) contentList);
    }

    /**
     * Setting up the recycler views in the layout.
     *
     * @see anders.olsen.moviebrowser.adapter.RecyclerImageAdapter
     * @see RecyclerClickListener
     * @see MediaObject
     */
    @Override
    protected void setUpRecycler(View v) {
        adapter = new RecyclerMediaListAdapter(getActivity(), contentList);
        RecyclerView recyclerView = v.findViewById(R.id.recycler_list);
        recyclerView.addOnItemTouchListener(new RecyclerClickListener(getActivity(), this));
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);

        // Adding scroll-listener for pagination
        // Interface fired when scroll to bottom.
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                fragmentListener.onScrollEnd(page, RecyclerMediaListFragment.this);
            }
        });
    }
}
