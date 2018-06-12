package olsen.anders.movieapp.loader;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import olsen.anders.movieapp.activities.BaseActivity;
import olsen.anders.movieapp.config.Config;

/**
 * Class responsible for connection to the TMDB API v3.
 * Whenever operations should be performed against the API, the client has to fetch the appropriate
 * service from this class.
 * <p>
 * Calls directly to a Tmdb user's account is done through AccountService.
 * Fetching of movie lists etc are done through MovieService.
 * Fetching of tv lists etc are done through TvService
 *
 * @author Anders Engen Olsen
 * @link https://www.developers.themoviedb.org/3/
 * @see AccountService
 * @see MovieService
 * @see TvService
 */

public class TmdbManager {

    /**
     * @see MovieService
     */
    private MovieService movieService;
    /**
     * @see TvService
     */
    private TvService tvService;
    /**
     * @see AccountService
     */
    private AccountService accountService;
    /**
     * @see SearchService
     */
    private SearchService searchService;
    /**
     * Singleton
     */
    private static TmdbManager tmdbManager = null;
    /**
     * JSON-parser
     *
     * @see JsonParser
     */
    private JsonParser jsonParser;
    /**
     * Context
     */
    private Context context;
    /**
     * RequestQueue Volley-API
     */
    private RequestQueue queue;
    /**
     * @see MovieAccountService
     */
    private MovieAccountService movieAccountService;
    /**
     * @see TvAccountService
     */
    private TvAccountService tvAccountService;

    /**
     * Private constructor. Called from getInstance().
     *
     * @param context Activity-context
     * @see #getInstance(Context)
     */
    private TmdbManager(Context context) {
        this.context = context;
        jsonParser = new JsonParser(context);
        queue = Volley.newRequestQueue(context);

        accountService = new AccountService(context, queue, jsonParser, Config.TMDB_API_KEY);
        movieAccountService = new MovieAccountService(context, queue, jsonParser, Config.TMDB_API_KEY);
        tvAccountService = new TvAccountService(context, queue, jsonParser, Config.TMDB_API_KEY);
        movieService = new MovieService(context, queue, jsonParser, Config.TMDB_API_KEY);
        tvService = new TvService(context, queue, jsonParser, Config.TMDB_API_KEY);
        searchService = new SearchService(context, queue, jsonParser, Config.TMDB_API_KEY);
    }

    /**
     * Initalizing a TmdbManager object if null.
     *
     * @param context Activity-context
     * @return Singleton object
     */
    public static synchronized TmdbManager getInstance(Context context) {
        if (tmdbManager == null)
            tmdbManager = new TmdbManager(context);

        return tmdbManager;
    }

    /**
     * Determining whether genres has been downloaded and shared in SharedPrefs.
     *
     * @return true if genres downloaded
     */
    public boolean hasGenres() {
        SharedPreferences prefs = context.getSharedPreferences(BaseActivity.SHARED_PREF_GENRES,
                Context.MODE_PRIVATE);

        return prefs.getAll().size() != 0;
    }

    /**
     * @return MovieService
     * @see MovieService
     */
    public MovieService getMovieService() {
        return movieService;
    }

    /**
     * @return TvService
     * @see TvService
     */
    public TvService getTvService() {
        return tvService;
    }

    /**
     * @return AccountService
     * @see AccountService
     */
    public AccountService getAccountService() {
        return accountService;
    }

    /**
     * @return SearchService
     * @see SearchService
     */
    public SearchService getSearchService() {
        return searchService;
    }

    /**
     * @return {@link MovieAccountService}
     */
    public MovieAccountService getMovieAccountService() {
        return movieAccountService;
    }

    /**
     * @return {@link TvAccountService}
     */
    public TvAccountService getTvAccountService() {
        return tvAccountService;
    }
}
