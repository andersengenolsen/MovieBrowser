package olsen.anders.movieapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;

import olsen.anders.movieapp.R;
import olsen.anders.movieapp.adapter.MainPagerAdapter;
import olsen.anders.movieapp.adapter.RecyclerAdapter;
import olsen.anders.movieapp.fragment.RecyclerMediaListFragment;
import olsen.anders.movieapp.interfaces.Saveable;
import olsen.anders.movieapp.listener.ListFragmentListener;
import olsen.anders.movieapp.listener.TmdbListener;
import olsen.anders.movieapp.loader.MovieAccountService;
import olsen.anders.movieapp.loader.TvAccountService;
import olsen.anders.movieapp.model.MediaObject;


/**
 * This activity contains a TabLayout with 2 tabs, movies and URL_TV shows.
 * The purpose of this activity is to fetch the users saved lists at TMDB.
 * The list can either be a watchlist or a favoritelist.
 * <p>
 * In each tab, it is displayed a FragmentList with RecyclerViews.
 *
 * @author Anders Engen Olsen
 * @see RecyclerMediaListFragment
 */

public class ListActivity extends BaseActivity implements
        ListFragmentListener {

    /**
     * Representing an unitialized key
     */
    private final static int UNINITIALIZED = Integer.MIN_VALUE;
    /**
     * Movie tab
     */
    private RecyclerMediaListFragment movieTab;
    /**
     * Tv tab
     */
    private RecyclerMediaListFragment tvTab;
    /**
     * The type of mediaobject, movie or tv.
     * It is either MEDIA_LIST_WATCHLIST or MEDIA_LIST_FAVORITES
     */
    private int type;

    /**
     * The fetching of the list is done on onResume.
     * The user might remove a movie / URL_TV from the list while this activity is on the stack, since
     * a click on any mediaobject launches the MediaObjectActivity.
     * Placing the fetchList in the onCreate will cause the list to not be updated if this activity
     * is on the stack.
     *
     * @param savedInstanceState
     * @see #onResume()
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addContentView(R.layout.activity_tablayout);

        // Tabs
        movieTab = new RecyclerMediaListFragment();
        tvTab = new RecyclerMediaListFragment();

        // TabLayout
        setUpTabLayout();

        // Up navigation, displaying arrow back to previous activity.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            // Newly created, getting intent
            Intent intent = getIntent();

            if (intent != null) {
                type = intent.getIntExtra(BaseActivity.MEDIA_LIST_KEY, UNINITIALIZED);
            }
        } else {
            // Orientation change, setting correct title
            setTitle(savedInstanceState.getString(MEDIA_LIST_KEY));
        }
    }

    /**
     * Fetching the appropriate list, watchlist or favorites.
     * <p>
     * This is done in onResume, since this activity can be on the call stack while the user
     * removes objects from his/hers watchlist / favorite-list.
     * <p>
     * If it is on the call stack, the list will be updated.
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (type == UNINITIALIZED)
            return;

        if (type == BaseActivity.MEDIA_LIST_WATCHLIST) {
            setTitle(R.string.watchlist);
        } else {
            setTitle(R.string.favourites);
        }

        fetchList(1, movieAccountService);
        fetchList(1, tvAccountService);
    }

    /**
     * Implementation of ListFragmentListener.
     * <p>
     * If an item in the list is clicked, the MediaObjectActivity is started.
     *
     * @see ListFragmentListener#onItemClicked(RecyclerAdapter, int)
     * @see MediaObjectActivity
     */
    @Override
    public void onItemClicked(RecyclerAdapter adapter, int pos) {
        MediaObject mediaObject = (MediaObject) adapter.getElement(pos);
        startMediaObjectActivity(this, mediaObject);
    }

    /**
     * Implementation of ListFragmentListener.
     * <p>
     * If it's scrolled to the bottom of the list, a new page is loaded from the API.
     *
     * @see ListFragmentListener#onScrollEnd(int, Fragment)
     * @see MediaObjectActivity
     */
    @Override
    public void onScrollEnd(int page, Fragment fragment) {
        Saveable saveable;
        if (fragment == movieTab)
            saveable = movieAccountService;
        else
            saveable = tvAccountService;

        fetchList(++page, saveable);
    }

    /**
     * Saving the arraylists with the mediaobjects on orientation change.
     *
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(MEDIA_LIST_KEY, getTitle().toString());
        super.onSaveInstanceState(outState);
    }

    /**
     * Initializing the tab-layout
     * The tab-layout is used for both movies and URL_TV.
     * The tabs are: Movies | URL_TV shows
     * The fragment shown in each tab is RecyclerMediaListFragment.
     *
     * @see RecyclerMediaListFragment
     */
    private void setUpTabLayout() {
        // Fetching tab-layout.
        final TabLayout tabLayout = findViewById(R.id.tablayout);

        // Naming
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.movies)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tv_shows)));

        // Whole screen
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Adapter to administrate the fragments (the tabs).
        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(),
                movieTab, tvTab);

        final ViewPager viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(adapter);

        // Setting EventListeners
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        // Behaviour when the user clicks on a tab.
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            /**
             * Setting the correct fragment.
             *
             * @param tab chosen tab
             */
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }


            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }

        });
    }

    /**
     * Fetching MediaObjects from the users list on TMDB.
     *
     * @see Saveable
     * @see MovieAccountService
     * @see TvAccountService
     */
    private void fetchList(final int page, Saveable saveable) {

        final RecyclerMediaListFragment fragment;

        if (saveable instanceof MovieAccountService)
            fragment = movieTab;
        else
            fragment = tvTab;

        if (type == MEDIA_LIST_FAVORITES) {
            saveable.getFavoriteList(page, new TmdbListener<ArrayList<MediaObject>>() {
                @Override
                public void onSuccess(ArrayList<MediaObject> result) {
                    if (page == 1)
                        fragment.setContent(result);
                    else
                        fragment.appendContent(result);
                }

                @Override
                public void onError(String result) {
                    showToast(result);
                }
            });
        } else if (type == MEDIA_LIST_WATCHLIST)
            saveable.getWatchlist(page, new TmdbListener<ArrayList<MediaObject>>() {
                @Override
                public void onSuccess(ArrayList<MediaObject> result) {
                    if (page == 1)
                        fragment.setContent(result);
                    else
                        fragment.appendContent(result);
                }

                @Override
                public void onError(String result) {
                    showToast(result);
                }
            });
    }
}
