package olsen.anders.movieapp.constants;

/**
 * Class containing URL constants for the Tmdb API.
 *
 * @author Anders Engen Olsen
 */
public class TmdbConstants {
    /**
     * BASE URL TMDB API
     */
    public static final String API_BASE_URL = "https://api.themoviedb.org/3/";
    /**
     * URL_MOVIE
     */
    public static final String URL_MOVIE = "movie/";
    /**
     * URL_TV
     */
    public static final String URL_TV = "tv/";
    /**
     * URL_GENRE
     */
    public static final String URL_GENRE = "genre/";
    /**
     * LISTS
     */
    public static final String URL_LIST = "list";
    /**
     * URL_POPULAR URL_TV / MOVIES
     */
    public static final String URL_POPULAR = "popular";
    /**
     * TOP RATED URL_MOVIE / URL_TV
     */
    public static final String URL_TOP_RATED = "top_rated";
    /**
     * URL_UPCOMING URL_MOVIE / URL_TV
     */
    public static final String URL_UPCOMING = "upcoming";
    /**
     * URL_MOVIE / URL_TV ON THE AIR
     */
    public static final String URL_ON_THE_AIR = "on_the_air";
    /**
     * URL_VIDEOS FOR URL_MOVIE / URL_TV
     */
    public static final String URL_VIDEOS = "videos";
    /**
     * URL FOR SEARCH
     */
    public static final String URL_SEARCH = "search/multi";
    /**
     * DISCOVER URL
     */
    public static final String URL_DISCOVER = "discover/";
    /**
     * PARAM API KEY
     */
    public static final String PARAM_API_KEY = "api_key";
    /**
     * PARAM SEARCH QUERY
     */
    public static final String PARAM_QUERY = "query";
    /**
     * Genre search
     */
    public static final String PARAM_WITH_GENRES = "with_genres";
    /**
     * Sorting
     */
    public static final String PARAM_SORT_BY = "sort_by";
    /**
     * Descending popularity sorting
     */
    public static final String POPULARITY_DESC = "popularity.desc";
    /**
     * Media type movie
     */
    public final static String MEDIA_TYPE_MOVIE = "movie";
    /**
     * Media type movies
     */
    public final static String MEDIA_TYPE_MOVIES = "movies";
    /**
     * Media type TV
     */
    public final static String MEDIA_TYPE_TV = "tv";
    /**
     * Shared preferences for session id.
     */
    public static final String SHARED_PREF_SESSION = "shared_pref_session";
    /**
     * Shared preferences key for session id
     */
    public static final String SHARED_PREF_ID = "shared_pref_id";
    /**
     * Base URL for authentication
     */
    public static final String BASE_URL_AUTH = "https://api.themoviedb.org/3/authentication/";
    public static final String AUTH_URL = "https://www.themoviedb.org/authenticate/";

    /**
     * Sub-URL for new token
     */
    public static final String NEW_TOKEN = "token/new";
    /**
     * Sub-URL for new session
     */
    public static final String NEW_SESSION = "session/new";
    /**
     * Parameter for request token
     */
    public static final String PARAM_REQUEST_TOKEN = "request_token";
    /**
     * Base
     */
    public static final String BASE_URL_ACCOUNT = "https://api.themoviedb.org/3/account/{account_id}/";
    /**
     * Kall mot watchlist
     */
    public static final String WATCHLIST = "watchlist";
    /**
     * Kall mot favoritelist
     */
    public static final String FAVORITE = "favorite";
    /**
     * Session ID parameter
     */
    public static final String PARAM_SESSION_ID = "session_id";
    /**
     * JSON media type
     */
    public static final String JSON_MEDIA_TYPE = "media_type";
    /**
     * JSON media id
     */
    public static final String JSON_MEDIA_ID = "media_id";
}
