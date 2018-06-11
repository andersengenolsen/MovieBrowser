package olsen.anders.movieapp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import olsen.anders.movieapp.R;
import olsen.anders.movieapp.adapter.RecyclerMediaListAdapter;
import olsen.anders.movieapp.listener.EndlessRecyclerViewScrollListener;
import olsen.anders.movieapp.listener.ListFragmentListener;
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

public class RecyclerMediaListFragment extends RecyclerListFragment<MediaObject> {

    /**
     * The recyclerview in the layout
     */
    private RecyclerView recyclerView;

    /**
     * Obtaining bundle with mediaobjects.
     *
     * @see #setUpRecycler(View)
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            contentList = savedInstanceState.getParcelableArrayList(CONTENT_KEY);
        } else if (contentList == null) {
            contentList = new ArrayList<>();
        }

        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(CONTENT_KEY, contentList);
    }

    /**
     * Setting up the recycler views in the layout.
     *
     * @see olsen.anders.movieapp.adapter.RecyclerImageAdapter
     * @see RecyclerClickListener
     * @see MediaObject
     */
    @Override
    protected void setUpRecycler(View v) {
        if (recyclerView != null)
            return;

        adapter = new RecyclerMediaListAdapter(getActivity(), contentList);
        recyclerView = v.findViewById(R.id.recycler_list);
        recyclerView.addOnItemTouchListener(new RecyclerClickListener(getActivity(), this));
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                fragmentListener.onScrollEnd(page, RecyclerMediaListFragment.this);
            }
        });
    }
}
