package anders.olsen.moviebrowser.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import anders.olsen.moviebrowser.R;
import anders.olsen.moviebrowser.adapter.RecyclerMediaListAdapter;
import anders.olsen.moviebrowser.adapter.RecyclerStringListAdapter;
import anders.olsen.moviebrowser.listener.RecyclerClickListener;
import anders.olsen.moviebrowser.model.MediaObject;


/**
 * Fragment which show a list of Strings, presented with RecyclerViews.
 *
 * @author Anders Engen Olsen
 * @see RecyclerListAdapter
 * @see RecyclerClickListener
 */
public class RecyclerStringListFragment extends RecyclerListFragment<String> {

    private FragmentManager manager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

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
            contentList = savedInstanceState.getStringArrayList(CONTENT_KEY);
            notifyAdapter();
        }

        manager = getChildFragmentManager();

        return view;
    }

    /**
     * Retaining content in list.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(CONTENT_KEY, (ArrayList<String>) contentList);
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
        adapter = new RecyclerStringListAdapter(getActivity(), contentList);
        RecyclerView recyclerView = v.findViewById(R.id.recycler_list);
        recyclerView.addOnItemTouchListener(new RecyclerClickListener(getActivity(), this));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    /**
     * Adding a child fragment to the layout.
     *
     * @param fragment new fragment to show.
     */
    public void showChildFragment(Fragment fragment) {
        if (manager == null)
            return;

        FragmentTransaction childFragTrans = manager.beginTransaction();
        childFragTrans.setCustomAnimations(
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right);
        childFragTrans.replace(R.id.frame, fragment);
        childFragTrans.addToBackStack("tag");
        childFragTrans.commit();
    }
}
