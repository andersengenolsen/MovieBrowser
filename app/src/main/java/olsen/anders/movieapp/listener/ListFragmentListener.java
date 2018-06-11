package olsen.anders.movieapp.listener;

import android.support.v4.app.Fragment;

import olsen.anders.movieapp.adapter.RecyclerAdapter;

/**
 * Created by anders on 11.06.18.
 */

public interface ListFragmentListener {

    void onScrollEnd(int page, Fragment fragment);
    void onItemClicked(RecyclerAdapter adapter, int position);

}
