package olsen.anders.movieapp.constants;

/**
 * Constants for reading JSON containing mediaobjects from TMDB
 *
 * @author Anders Engen Olsen
 */
public class JsonConstants {
    /**
     * ID
     */
    public static final String ID = "id";
    /**
     * POSTER
     */
    public static final String POSTER_PATH = "poster_path";
    /**
     * RATING
     */
    public static final String VOTE_AVERAGE = "vote_average";
    /**
     * Language
     */
    public static final String ORIGINAL_LANGUAGE = "original_language";
    /**
     * Handling in movie / TV
     */
    public static final String OVERVIEW = "overview";
    /**
     * Release date, movie specific.
     */
    public static final String RELEASE_DATE = "release_date";
    /**
     * Name, TV specific
     */
    public static final String NAME = "name";
    /**
     * Title, movie specific
     */
    public static final String TITLE = "title";
    /**
     * Eiter MEDIA_TYPE_TV or MEDIA_TYPE_MOVIE
     */
    public static final String MEDIA_TYPE = "media_type";
    /**
     * First air date, tv specific
     */
    public static final String FIRST_AIR_DATE = "first_air_date";
    /**
     * Genres
     */
    public static final String GENRES = "genres";
    /**
     * Json results from TMDB, array
     */
    public static final String JSON_RESULT = "results";
    /**
     * ID for genres, array
     */
    public static final String GENRE_IDS = "genre_ids";
    /**
     * Status code parameter in JSON
     */
    public static final String STATUS_CODE = "status_code";
    /**
     * Status code updated
     */
    public static int STATUS_CODE_UPDATED = 12;
    /**
     * Status code created
     */
    public static int STATUS_CODE_CREATED = 1;
    /**
     * Status code deleted
     */
    public static int STATUS_CODE_DELETED = 13;
}
