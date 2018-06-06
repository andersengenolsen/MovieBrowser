package olsen.anders.movieapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;

import olsen.anders.movieapp.R;
import olsen.anders.movieapp.adapter.MainPagerAdapter;
import olsen.anders.movieapp.fragment.RecyclerListFragment;
import olsen.anders.movieapp.loader.BaseMovieTvService;
import olsen.anders.movieapp.loader.TmdbListener;
import olsen.anders.movieapp.loader.TmdbManager;
import olsen.anders.movieapp.model.MediaObject;


/**
 * Activity containing a TabLayout with 2 tabs, highest rated and most popular.
 * This activity is used both for movies and TV shows.
 * Whether it is movie / TV shows is determined by key: BaseActivity.MEDIA_OBJECT_KEY
 * <p>
 * Each tab contains a fragment with a list with MediaObjects.
 *
 * @author Anders Engen Olsen
 * @see RecyclerListFragment
 */

public class MediaCollectionActivity extends BaseActivity implements
        RecyclerListFragment.OnItemClickedListener {

    /**
     * The tabs.
     *
     * @see RecyclerListFragment
     */
    private RecyclerListFragment topRatedTab, upcomingTab;

    /**
     * Service used for either TV or movie
     */
    private BaseMovieTvService service;


    /**
     * Implementation of click event in the RecyclerListFragment.
     * Method called when a mediaobject has been clicked in the list,
     * starting MediaObjectActivity.
     *
     * @param mediaObject item clicked
     * @see MediaObjectActivity
     */
    @Override
    public void onItemClicked(MediaObject mediaObject) {
        Intent intent = new Intent(this, MediaObjectActivity.class);
        intent.putExtra(MEDIA_OBJECT_KEY, mediaObject);

        startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addContentView(R.layout.activity_tablayout);

        topRatedTab = new RecyclerListFragment();
        upcomingTab = new RecyclerListFragment();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Handling intent. Movie or TV show?
        Intent intent = getIntent();

        if (intent != null) {
            int type = intent.getIntExtra(BaseActivity.MEDIA_TYPE_KEY, 0);
            if (type == BaseActivity.MEDIA_TYPE_TV) {
                service = tvService;
                setTitle(R.string.tv_shows);
            } else if (type == BaseActivity.MEDIA_TYPE_MOVIE) {
                service = movieService;
                setTitle(R.string.movies);
            }
            fetchMediaObjects();
        } else {
            if (savedInstanceState != null)
                setTitle(savedInstanceState.getString(MEDIA_TYPE_KEY));
        }

        setUpTabLayout();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(MEDIA_TYPE_KEY, getTitle().toString());
    }

    /**
     * Fetching MediaObjects from the TMDB-API.
     *
     * @see TmdbManager
     */
    private void fetchMediaObjects() {
        service.getUpcoming(new TmdbListener<ArrayList<MediaObject>>() {
            @Override
            public void onSuccess(ArrayList<MediaObject> result) {
                upcomingTab.setMediaObjects(result);
            }

            @Override
            public void onError(String result) {
                showToast(result);
            }
        });

        service.getTopRated(new TmdbListener<ArrayList<MediaObject>>() {
            @Override
            public void onSuccess(ArrayList<MediaObject> result) {
                topRatedTab.setMediaObjects(result);
            }

            @Override
            public void onError(String result) {
                showToast(result);
            }
        });
    }

    /**
     * Initializing the tab-layout
     * The tab-layout is used for both movies and URL_TV.
     * The tabs are: Movies | URL_TV shows
     * The fragment shown in each tab is RecyclerListFragment.
     *
     * @see RecyclerListFragment
     */
    private void setUpTabLayout() {
        // Fetching tab-layout.
        final TabLayout tabLayout = findViewById(R.id.tablayout);

        // Naming
        tabLayout.addTab(tabLayout.newTab().setText(R.string.best_rated));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.upcoming));

        // Whole screen
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Adapter to administrate the fragments (the tabs).
        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(),
                topRatedTab, upcomingTab);

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
}
