package olsen.anders.movieapp.activities;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

import olsen.anders.movieapp.R;
import olsen.anders.movieapp.fragment.LoginDialogFragment;
import olsen.anders.movieapp.fragment.RecyclerMediaListFragment;
import olsen.anders.movieapp.listener.LoginDialogListener;
import olsen.anders.movieapp.loader.AccountService;
import olsen.anders.movieapp.loader.MovieAccountService;
import olsen.anders.movieapp.loader.MovieService;
import olsen.anders.movieapp.listener.TmdbListener;
import olsen.anders.movieapp.loader.TmdbManager;
import olsen.anders.movieapp.loader.TvAccountService;
import olsen.anders.movieapp.loader.TvService;
import olsen.anders.movieapp.model.MediaObject;


/**
 * BaseActivity, all activities with a Toolbar with OptionsMenu extends this.
 * The activity defines the behaviour of elements in the OptionsMenu.
 * <p>
 * The layout for the activitiy is activity_base.xml, which contains a toolbar and a frame layout.
 * The frame layout is where the actual application content is loaded, through the method
 * addContentView.
 * NB! Subactivites has to use addContentView, not setContentView!
 *
 * @author Anders Engen Olsen
 * @see #addContentView(int)
 * @see LoginDialogFragment
 */

public class BaseActivity extends AppCompatActivity
        implements LoginDialogListener {
    /**
     * Key for media objects with intents
     */
    public static final String MEDIA_OBJECT_KEY = "media_object_key";
    /**
     * MEDIA_TYPE_KEY, either MEDIA_TYPE_TV or MEDIA_TYPE_MOVIE.
     */
    public static final String MEDIA_TYPE_KEY = "media_type_key";
    /**
     * TV show type
     */
    public static final int MEDIA_TYPE_TV = 3;
    /**
     * Movie type
     */
    public static final int MEDIA_TYPE_MOVIE = 4;
    /**
     * MEDIA_LIST_KEY, either MEDIA_LIST_WATCHLIST or MEDIA_LIST_FAVORITES.
     */
    public static final String MEDIA_LIST_KEY = "media_list_key";
    /**
     * Watchlist
     */
    public static final int MEDIA_LIST_WATCHLIST = 1;
    /**
     * Favorites
     */
    public static final int MEDIA_LIST_FAVORITES = 2;

    /**
     * Result from authenticating through browser
     */
    private static final int URL_RESULT_KEY = 5;
    /**
     * Shared pref where genres are saved.
     */
    public static String SHARED_PREF_GENRES = "shared_pref_genres";
    /**
     * @see TmdbManager
     */
    protected TmdbManager tmdb;
    /**
     * Movie calls to TMDB
     */
    protected MovieService movieService;
    /**
     * URL_TV show class to TMDB
     */
    protected TvService tvService;
    /**
     * Calls to users TMDB account
     */
    protected AccountService accountService;
    /**
     * Account calls for movies
     */
    protected MovieAccountService movieAccountService;
    /**
     * Account calls for TV
     */
    protected TvAccountService tvAccountService;
    /**
     * Layout for subactivities
     */
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tmdb = TmdbManager.getInstance(this);
        movieService = tmdb.getMovieService();
        tvService = tmdb.getTvService();
        accountService = tmdb.getAccountService();
        movieAccountService = tmdb.getMovieAccountService();
        tvAccountService = tmdb.getTvAccountService();

        // Fetching genres.
        if (!tmdb.hasGenres()) {
            movieService.getAllGenres(null);
            tvService.getAllGenres(null);
        }

        setContentView(R.layout.activity_base);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        frameLayout = findViewById(R.id.frame);
    }

    /**
     * Implementation of the local interface in LoginDialogFragment.
     * This is fired when the user clicks "login" in the dialog.
     * <p>
     * The method starts a browser on the users device, where the user
     * can authenticate through TMDB.
     *
     * @see #startBrowser(Uri)
     * @see AccountService#requestToken(TmdbListener)
     * @see LoginDialogListener
     */
    @Override
    public void onDialogPositiveClick() {

        accountService.requestToken(new TmdbListener<String>() {
            @Override
            public void onSuccess(String result) {
                startBrowser(Uri.parse(result));
            }

            @Override
            public void onError(String result) {
                showToast(result);
            }
        });
    }

    /**
     * Sending intent to browser, toast if none installed.
     * <p>
     * When the browser is closed, the result is handled in onActivityResult.
     *
     * @param uri TMDB authenticate page
     * @see #onActivityResult(int, int, Intent)
     */
    private void startBrowser(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

        // Making sure that there is a app that can handle the intent
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        boolean hasBrowser = activities.size() > 0;

        if (hasBrowser)
            startActivityForResult(intent, URL_RESULT_KEY);
        else
            showToast(getString(R.string.no_browser));
    }

    /**
     * Checking whether the requestCode implies that the user has closed a browser.
     * <p>
     * If the browser has been closed, a session will be initiated (if authenticated).
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == URL_RESULT_KEY) {
            // Trying to create session
            accountService.createSession(new TmdbListener<String>() {
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
    }

    /**
     * Initiating menu.
     * SearchView in Toolbar is activated, SearchActivity handles the actual search.
     *
     * @see SearchActivity
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.nav_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(
                        new ComponentName(BaseActivity.this, SearchActivity.class)));

        return true;
    }

    /**
     * Clicks in OptionMenu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_login:
                showLoginDialog();
                return true;
            case R.id.nav_about:
                showAboutDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    /**
     * Called in sub-activites to load layout into the framelayout in activity_base.xml
     *
     * @param layoutId layout to load into frame layout
     */
    protected void addContentView(int layoutId) {
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(layoutId, null, false);
        frameLayout.addView(contentView, 0);
    }

    /**
     * Helper method to display a toast
     *
     * @param msg Toast-message
     */
    protected void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Helper method to add a fragment to the empty frame-layout stub.
     *
     * @param fragment fragment to add
     */
    protected void addFragmentToLayout(RecyclerMediaListFragment fragment, String tag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.replace(frameLayout.getId(), fragment, tag);
        fragmentTransaction.commit();
    }

    /**
     * Returning all genres if downloaded.
     * Genres are fetched from SharedPrefs
     *
     * @return Map with genres
     */
    protected Map<String, ?> getGenres() {
        // Map with genres from Shared Prefs
        SharedPreferences prefs = getSharedPreferences(SHARED_PREF_GENRES,
                Context.MODE_PRIVATE);
        return prefs.getAll();
    }

    /**
     * Starting a {@link MediaObjectActivity}.
     * Used in several subactivities
     *
     * @param context     subactivity context
     * @param mediaObject {@link MediaObject} to show
     */
    protected final void startMediaObjectActivity(Context context, MediaObject mediaObject) {
        Intent intent = new Intent(context, MediaObjectActivity.class);
        intent.putExtra(MEDIA_OBJECT_KEY, mediaObject);
        startActivity(intent);
    }

    /**
     * Showing information about the developer
     */
    private void showAboutDialog() {
        Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_about);
        dialog.show();
    }

    /**
     * Starting a LoginDialogFragment
     *
     * @see LoginDialogFragment
     */
    private void showLoginDialog() {
        LoginDialogFragment dialog = new LoginDialogFragment();
        dialog.show(getSupportFragmentManager(), "LoginFragmentTag");
    }
}
