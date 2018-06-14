package anders.olsen.moviebrowser.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import anders.olsen.moviebrowser.R;
import anders.olsen.moviebrowser.fragment.RatingDialogFragment;
import anders.olsen.moviebrowser.fragment.YoutubeFragment;
import anders.olsen.moviebrowser.interfaces.Saveable;
import anders.olsen.moviebrowser.listener.RatingDialogListener;
import anders.olsen.moviebrowser.listener.TmdbListener;
import anders.olsen.moviebrowser.loader.BaseMovieTvService;
import anders.olsen.moviebrowser.model.MediaObject;

/**
 * Activity containing information about a single MediaObject.
 * The activity can be launched from several other activities, thus no parent.
 * <p>
 * The MediaObject is fetched in the onCreate, by key: BaseActivtiy.MEDIA_OBJECT_KEY
 *
 * @author Anders Engen Olsen
 * @see MediaObject
 */

public class MediaObjectActivity extends BaseActivity
        implements View.OnClickListener, RatingDialogListener {

    /**
     * Youtube video ID key
     */
    public final static String YOUTUBE_ID = "youtube_id";
    /**
     * MediaObject som vises
     */
    private MediaObject mediaObject;

    /**
     * ImageButton watchlist
     */
    private ImageButton watchlistBtn;
    /**
     * ImageButton favoritelist
     */
    private ImageButton favoriteBtn;
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
    /**
     * {@link anders.olsen.moviebrowser.interfaces.Saveable}
     */
    private Saveable saveable;

    /**
     * Handling intent.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Layout
        addContentView(R.layout.activity_mediaobject);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getParcelableExtra(BaseActivity.MEDIA_OBJECT_KEY) != null) {

            mediaObject = getIntent().getParcelableExtra(BaseActivity.MEDIA_OBJECT_KEY);

            setTitle(mediaObject.getTitle());

            initViews(mediaObject);
        }

        // Restoring button states
        if (savedInstanceState != null) {
            inFavoritelist = savedInstanceState.getBoolean(FAVORITE_FLAG);
            inWatchlist = savedInstanceState.getBoolean(WATCHLIST_FLAG);
            alternateButtonColor(favoriteBtn, inFavoritelist);
            alternateButtonColor(watchlistBtn, inWatchlist);
        }

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
     * Changing the color of the the button.
     * DKGRAY indicating the mediaobject is in a list.
     * BLUE indication the mediaobject is not in a list.
     * <p>
     * This method should only be called prior to a successful call to TmdbManager,
     * as it does not check against the users actual list. It simply switches between two
     * drawable styles. If the method is called without a successful call to TmdbManager,
     * it will change the color, regardless of the actual result of the list-operations.
     *
     * @param button Button which will change background
     */
    private void alternateButtonColor(ImageButton button, boolean inList) {
        if (inList)
            button.setBackgroundColor(getColor(R.color.colorChecked));
        else
            button.setBackgroundColor(getColor(R.color.colorPrimary));
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(WATCHLIST_FLAG, inWatchlist);
        outState.putBoolean(FAVORITE_FLAG, inFavoritelist);
        super.onSaveInstanceState(outState);
    }

    /**
     * On click events for views.
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_favorite:
                addFavorite();
                break;
            case R.id.add_watchlist:
                addWatchlist();
                break;
            case R.id.watch_trailer:
                startTrailer();
                break;
            case R.id.add_rating:
                showRatingDialog();
                break;
        }
    }

    /**
     * Showing a RatingDialogFragment.
     * <p>
     * The rating given by the user is handled in the interface method onDialogPositiveClick.
     *
     * @see #onDialogPositiveClick(int)
     */
    private void showRatingDialog() {
        RatingDialogFragment dialogFragment = new RatingDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "RatingDialog");
    }

    /**
     * Starting a YoutubeFragment if a youtube id is available for the current mediaobject.
     *
     * @see BaseMovieTvService#getTrailerUrl(int, TmdbListener)
     * @see YoutubeFragment
     */
    private void startTrailer() {
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
     * @see #alternateButtonColor(ImageButton, boolean)
     */
    private void addFavorite() {
        saveable.addToFavoriteList(mediaObject, !inFavoritelist,
                new TmdbListener<String>() {
                    @Override
                    public void onSuccess(String result) {
                        inFavoritelist = !inFavoritelist;
                        alternateButtonColor(favoriteBtn, inFavoritelist);
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
     * @see #alternateButtonColor(ImageButton, boolean)
     * @see Saveable(MediaObject, boolean, TmdbListener)
     */
    private void addWatchlist() {
        saveable.addToWatchlist(mediaObject, !inWatchlist,
                new TmdbListener<String>() {
                    @Override
                    public void onSuccess(String result) {
                        inWatchlist = !inWatchlist;
                        alternateButtonColor(watchlistBtn, inWatchlist);
                        showToast(result);
                    }

                    @Override
                    public void onError(String result) {
                        showToast(result);
                    }
                });
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
                    alternateButtonColor(favoriteBtn, inFavoritelist);
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
                    alternateButtonColor(watchlistBtn, inWatchlist);
            }

            @Override
            public void onError(String result) {

            }
        });
    }

    /**
     * Setting up views
     * Image is loaded with Picasso.
     *
     * @param mediaObject MediaObject
     * @see #setText(TextView, String)
     */
    private void initViews(MediaObject mediaObject) {
        watchlistBtn = findViewById(R.id.add_watchlist);
        favoriteBtn = findViewById(R.id.add_favorite);
        watchlistBtn.setOnClickListener(this);
        favoriteBtn.setOnClickListener(this);
        findViewById(R.id.watch_trailer).setOnClickListener(this);
        findViewById(R.id.add_rating).setOnClickListener(this);

        ImageView logoImg = findViewById(R.id.media_image);
        ImageView bgImg = findViewById(R.id.background);

        RequestCreator creator = Picasso.with(this).load(mediaObject.getImagePath())
                .error(R.drawable.ic_movie_black)
                .placeholder(R.drawable.ic_movie_black);

        creator.into(logoImg);
        creator.into(bgImg);

        TextView genreTxt = findViewById(R.id.genre);
        TextView releaseTxt = findViewById(R.id.release);
        TextView ratingTxt = findViewById(R.id.rating);
        TextView languageTxt = findViewById(R.id.language);
        TextView handlingTxt = findViewById(R.id.handling);

        setText(genreTxt, mediaObject.getGenre());
        setText(releaseTxt, mediaObject.getReleaseDate());
        setText(ratingTxt, mediaObject.getRating());
        setText(languageTxt, mediaObject.getLanguage());
        setText(handlingTxt, mediaObject.getHandling());
    }

    /**
     * Checking whether a String is null or empty. String containing only whitespace is considered
     * empty.
     * If empty or null, string with "unknown" will be placed in TextView.
     *
     * @param tv  TextView
     * @param txt Text to place in textview
     */
    private void setText(TextView tv, String txt) {
        if (txt == null || txt.trim().length() == 0)
            tv.setText(R.string.unknown);
        tv.setText(txt);
    }
}
