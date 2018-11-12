package anders.olsen.moviebrowser.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import anders.olsen.moviebrowser.R;
import anders.olsen.moviebrowser.adapter.MainPagerAdapter;
import anders.olsen.moviebrowser.adapter.RecyclerAdapter;
import anders.olsen.moviebrowser.fragment.MediaObjectInformationFragment;
import anders.olsen.moviebrowser.fragment.RatingDialogFragment;
import anders.olsen.moviebrowser.fragment.RecyclerMediaListFragment;
import anders.olsen.moviebrowser.fragment.YoutubeFragment;
import anders.olsen.moviebrowser.interfaces.Saveable;
import anders.olsen.moviebrowser.listener.ListFragmentListener;
import anders.olsen.moviebrowser.listener.MediaObjectFragmentListener;
import anders.olsen.moviebrowser.listener.TmdbListener;
import anders.olsen.moviebrowser.loader.BaseMovieTvService;
import anders.olsen.moviebrowser.model.MediaObject;

/**
 * Media object activity.
 * Showing information about current media object,
 * and a list of similar mediaobjects.
 * 2 tabs, one for information, and one for similar media objects.
 */
public class MediaObjectActivity extends BaseActivity implements ListFragmentListener,
        MediaObjectFragmentListener {

    /**
     * Youtube video ID key
     */
    public final static String YOUTUBE_ID = "youtube_id";

    /**
     * Current media object.
     */
    private MediaObject mediaObject;

    /**
     * Similar mediaobjects.
     */
    private RecyclerMediaListFragment similarTab;

    /**
     * Information about current media object
     */
    private MediaObjectInformationFragment mediaTab;

    /**
     * {@link anders.olsen.moviebrowser.interfaces.Saveable}
     */
    private Saveable saveable;

    /**
     * Flag, watchlist
     */
    private boolean inWatchlist;
    /**
     * Flag, favoritelist
     */
    private boolean inFavoritelist;
    /**
     * Key for saved instance state, watchlist
     */
    private final String WATCHLIST_FLAG = "watchlist_flag";
    /**
     * Key for saved instance state, favoritelist
     */
    private final String FAVORITE_FLAG = "favorite_flag";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addContentView(R.layout.activity_tablayout);

        // Up navigation, displaying arrow back to previous activity.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getParcelableExtra(BaseActivity.MEDIA_OBJECT_KEY) != null) {
            mediaObject = getIntent().getParcelableExtra(BaseActivity.MEDIA_OBJECT_KEY);
        }

        similarTab = new RecyclerMediaListFragment();
        mediaTab = new MediaObjectInformationFragment();
        mediaTab.setMediaObject(mediaObject);
        setUpTabLayout();

        if (mediaObject.isMovie())
            saveable = tmdb.getMovieAccountService();
        else
            saveable = tmdb.getTvAccountService();

        // Changing color of watchlist and favorite button.
        // Green indicating that it is saved in list.
        if (accountService.isLoggedIn()) {
            fetchLists();
        }
    }

    @Override
    public void onScrollEnd(int page, Fragment fragment) {
        // TODO: Scroll end in listfragment
    }

    @Override
    public void onItemClicked(RecyclerAdapter adapter, int position) {
        // TODO: Item click in list fragment
    }

    /**
     * Showing a RatingDialogFragment.
     * <p>
     * The rating given by the user is handled in the interface method onDialogPositiveClick.
     *
     * @see #onDialogPositiveClick(int)
     */
    @Override
    public void showRatingDialog() {
        RatingDialogFragment dialogFragment = new RatingDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "RatingDialog");
    }

    /**
     * Starting a YoutubeFragment if a youtube id is available for the current mediaobject.
     *
     * @see BaseMovieTvService#getTrailerUrl(int, TmdbListener)
     * @see YoutubeFragment
     */
    @Override
    public void startTrailer() {
        BaseMovieTvService service;
        service = (mediaObject.isMovie()) ? movieService : tvService;

        service.getTrailerUrl(mediaObject.getId(), new TmdbListener<String>() {
            @Override
            public void onSuccess(String youtubeID) {

                Bundle bundle = new Bundle();
                bundle.putString(YOUTUBE_ID, youtubeID);

                // Launching YoutubeFragment
                // Replacing current frame with the YoutubeFragment.
                YoutubeFragment fragment = new YoutubeFragment();
                fragment.setArguments(bundle);
                FragmentManager manager = getSupportFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.frame, fragment)
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onError(String result) {
                showToast(result);
            }
        });
    }

    /**
     * Adding movie / URL_TV to the users favorite list.
     * If successful, the color of the button will change
     *
     * @see Saveable#addToFavoriteList(MediaObject, boolean, TmdbListener)
     * @see MediaObjectInformationFragment#alternateFavoritelistBtnColor(boolean)
     */
    @Override
    public void addFavorite() {
        saveable.addToFavoriteList(mediaObject, !inFavoritelist,
                new TmdbListener<String>() {
                    @Override
                    public void onSuccess(String result) {
                        inFavoritelist = !inFavoritelist;
                        mediaTab.alternateFavoritelistBtnColor(inFavoritelist);
                        showToast(result);
                    }

                    @Override
                    public void onError(String result) {
                        showToast(result);
                    }
                });
    }

    /**
     * Adding movie / URL_TV to the users watchlist.
     * If successful, the color of the button will change
     *
     * @see MediaObjectInformationFragment#alternateWatchlistBtnColor(boolean)
     * @see Saveable(MediaObject, boolean, TmdbListener)
     */
    @Override
    public void addWatchlist() {
        saveable.addToWatchlist(mediaObject, !inWatchlist,
                new TmdbListener<String>() {
                    @Override
                    public void onSuccess(String result) {
                        inWatchlist = !inWatchlist;
                        mediaTab.alternateWatchlistBtnColor(inWatchlist);
                        showToast(result);
                    }

                    @Override
                    public void onError(String result) {
                        showToast(result);
                    }
                });
    }

    /**
     * Implementation of RatingDialogListener
     * Adding rating to the current mediaobject
     *
     * @param rating the rating to add
     * @see anders.olsen.moviebrowser.loader.AccountService#addRating(MediaObject, int, TmdbListener)
     */
    @Override
    public void onDialogPositiveClick(int rating) {
        accountService.addRating(mediaObject, rating, new TmdbListener<String>() {
            @Override
            public void onSuccess(String result) {
                showToast(result);
            }

            @Override
            public void onError(String result) {
                showToast(result);
            }
        });
    }

    /**
     * The activity can be started from several parent activities.
     * Overriding default, returning to previous activity when up pressed.
     *
     * @param item
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
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
        tabLayout.addTab(tabLayout.newTab().setText(mediaObject.getTitle()));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.similar));

        // Whole screen
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Adapter to administrate the fragments (the tabs).
        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(),
                mediaTab, similarTab);

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

    private void loadSimilarFromApi(int page) {
        // TODO: Load similar movies / TV from the API.
    }

    /**
     * Fetching user-lists from the API.
     * The saveable can be either MovieAccountService or TvAccountService.
     */
    private void fetchLists() {
        saveable.hasInFavoriteList(mediaObject, new TmdbListener<Boolean>() {
            @Override
            public void onSuccess(Boolean inList) {
                inFavoritelist = inList;
                if (inList)
                    mediaTab.alternateFavoritelistBtnColor(inFavoritelist);
            }

            @Override
            public void onError(String result) {

            }
        });
        saveable.hasInWatchlist(mediaObject, new TmdbListener<Boolean>() {
            @Override
            public void onSuccess(Boolean inList) {
                inWatchlist = inList;
                if (inList)
                    mediaTab.alternateWatchlistBtnColor(inWatchlist);
            }

            @Override
            public void onError(String result) {

            }
        });
    }
}
