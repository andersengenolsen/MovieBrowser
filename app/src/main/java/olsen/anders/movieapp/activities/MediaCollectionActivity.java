package olsen.anders.movieapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;

import olsen.anders.movieapp.R;
import olsen.anders.movieapp.adapter.MainPagerAdapter;
import olsen.anders.movieapp.adapter.RecyclerAdapter;
import olsen.anders.movieapp.adapter.RecyclerMediaListAdapter;
import olsen.anders.movieapp.adapter.RecyclerStringListAdapter;
import olsen.anders.movieapp.fragment.RecyclerMediaListFragment;
import olsen.anders.movieapp.fragment.RecyclerStringListFragment;
import olsen.anders.movieapp.loader.BaseMovieTvService;
import olsen.anders.movieapp.loader.TmdbListener;
import olsen.anders.movieapp.loader.TmdbManager;
import olsen.anders.movieapp.model.Genre;
import olsen.anders.movieapp.model.MediaObject;


/**
 * Activity containing a TabLayout with 2 tabs, highest rated and most popular.
 * This activity is used both for movies and TV shows.
 * Whether it is movie / TV shows is determined by key: BaseActivity.MEDIA_OBJECT_KEY
 * <p>
 * Each tab contains a fragment with a list with MediaObjects.
 *
 * @author Anders Engen Olsen
 * @see RecyclerMediaListFragment
 */

public class MediaCollectionActivity extends BaseActivity implements
        RecyclerMediaListFragment.OnItemClickedListener {

    /**
     * The tabs with mediaobjects
     *
     * @see RecyclerMediaListFragment
     */
    private RecyclerMediaListFragment topRatedTab, upcomingTab;

    /**
     * This tab is hidden, not visible before genres are clicked in genreTab!
     */
    private RecyclerMediaListFragment genreMediaTab;

    /**
     * Tab with genres
     */
    private RecyclerStringListFragment genreTab;

    /**
     * Service used for either TV or movie
     */
    private BaseMovieTvService service;

    /**
     * Arraylist with genres
     */
    private ArrayList<Genre> genreList;

    /**
     * Pageradapter for the tabs
     */
    private MainPagerAdapter mainPagerAdapter;

    /**
     * Implementation of click event in the RecyclerMediaListFragment.
     * Method called when a mediaobject has been clicked in the list,
     * starting MediaObjectActivity.
     * <p>
     * If the returned adapter is a {@link RecyclerMediaListAdapter}, then a mediaobject has
     * been clicked.
     * <p>
     * If not, the genre tab has been clicked. Replacing the genre tab with a list.
     *
     * @see MediaObjectActivity
     */
    @Override
    public void onItemClicked(RecyclerAdapter adapter, int position) {
        if (adapter instanceof RecyclerMediaListAdapter) {

            RecyclerMediaListAdapter rAdapter = (RecyclerMediaListAdapter) adapter;
            MediaObject mediaObject = rAdapter.getElement(position);

            Intent intent = new Intent(this, MediaObjectActivity.class);
            intent.putExtra(MEDIA_OBJECT_KEY, mediaObject);
            startActivity(intent);

        } else if (adapter instanceof RecyclerStringListAdapter) {

            RecyclerStringListAdapter rAdapter = (RecyclerStringListAdapter) adapter;
            String genreString = rAdapter.getElement(position);

            Genre genre = null;
            // Finding correct genre to pass to service
            for (Genre g : genreList) {
                if (genreString.equalsIgnoreCase(g.getGenre())) {
                    genre = g;
                    break;
                }
            }

            if (genre == null) {
                showToast(getString(R.string.no_genres));
                return;
            }

            service.getByGenre(genre, new TmdbListener<ArrayList<MediaObject>>() {
                @Override
                public void onSuccess(ArrayList<MediaObject> result) {
                    genreMediaTab.setContent(result);
                    genreTab.showChildFragment(genreMediaTab);
                }

                @Override
                public void onError(String result) {
                    showToast(result);
                }
            });
        }
    }

    /**
     * Initial setup of fragments.
     * Handling intent, which indicates whether "movie" or "tv".
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addContentView(R.layout.activity_tablayout);

        topRatedTab = new RecyclerMediaListFragment();
        upcomingTab = new RecyclerMediaListFragment();
        genreMediaTab = new RecyclerMediaListFragment();

        genreTab = new RecyclerStringListFragment();
        genreList = new ArrayList<>();

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
            fetchGenres();
        } else {
            if (savedInstanceState != null)
                setTitle(savedInstanceState.getString(MEDIA_TYPE_KEY));
        }

        setUpTabLayout();
    }

    /**
     * Saving state of title
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(MEDIA_TYPE_KEY, getTitle().toString());
    }

    /**
     * Setting genres to the genre tab.
     * If no genres has been downloaded yet, the method returns.
     */
    private void fetchGenres() {
        service.getAllGenres(new TmdbListener<ArrayList<Genre>>() {
            @Override
            public void onSuccess(ArrayList<Genre> result) {
                genreList = result;
                ArrayList<String> genreStrings = new ArrayList<>();

                for (Genre g : genreList)
                    genreStrings.add(g.getGenre());

                genreTab.setContent(genreStrings);
            }

            @Override
            public void onError(String result) {
                showToast(result);
            }
        });

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
                upcomingTab.setContent(result);
            }

            @Override
            public void onError(String result) {
                showToast(result);
            }
        });

        service.getTopRated(new TmdbListener<ArrayList<MediaObject>>() {
            @Override
            public void onSuccess(ArrayList<MediaObject> result) {
                topRatedTab.setContent(result);
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
     * The fragment shown in each tab is RecyclerMediaListFragment.
     *
     * @see RecyclerMediaListFragment
     */
    private void setUpTabLayout() {
        // Fetching tab-layout.
        final TabLayout tabLayout = findViewById(R.id.tablayout);

        // Naming
        tabLayout.addTab(tabLayout.newTab().setText(R.string.best_rated));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.upcoming));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.genres));
        // Whole screen
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Adapter to administrate the fragments (the tabs).
        mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(),
                topRatedTab, upcomingTab, genreTab);

        final ViewPager viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(mainPagerAdapter);

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
     * Overriding default.
     * In some fragments the childFragmentManager is used.
     * If there is a fragment on any childFragmentManager's backstack, we pop it.
     * else, we return to the previous activity.
     */
    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        for (Fragment frag : fm.getFragments()) {
            if (frag.isVisible()) {
                FragmentManager childFm = frag.getChildFragmentManager();
                if (childFm.getBackStackEntryCount() > 0) {
                    childFm.popBackStack();
                    return;
                }
            }
        }
        super.onBackPressed();
    }
}
