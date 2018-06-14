package anders.olsen.moviebrowser.listener;

import android.support.v4.app.Fragment;

import anders.olsen.moviebrowser.adapter.RecyclerAdapter;

/**
 * Created by olsen on 11.06.18.
 */

public interface ListFragmentListener {

    void onScrollEnd(int page, Fragment fragment);
    void onItemClicked(RecyclerAdapter adapter, int position);

}
