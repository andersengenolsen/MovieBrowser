package anders.olsen.moviebrowser.loader;

import android.content.Context;
import android.net.Uri;

import com.android.volley.RequestQueue;

import java.util.ArrayList;

import anders.olsen.moviebrowser.listener.TmdbListener;
import anders.olsen.moviebrowser.model.Genre;
import anders.olsen.moviebrowser.model.MediaObject;

import static anders.olsen.moviebrowser.constants.TmdbConstants.API_BASE_URL;
import static anders.olsen.moviebrowser.constants.TmdbConstants.MEDIA_TYPE_TV;
import static anders.olsen.moviebrowser.constants.TmdbConstants.PARAM_API_KEY;
import static anders.olsen.moviebrowser.constants.TmdbConstants.PARAM_PAGE;
import static anders.olsen.moviebrowser.constants.TmdbConstants.PARAM_SORT_BY;
import static anders.olsen.moviebrowser.constants.TmdbConstants.PARAM_WITH_GENRES;
import static anders.olsen.moviebrowser.constants.TmdbConstants.POPULARITY_DESC;
import static anders.olsen.moviebrowser.constants.TmdbConstants.URL_DISCOVER;
import static anders.olsen.moviebrowser.constants.TmdbConstants.URL_GENRE;
import static anders.olsen.moviebrowser.constants.TmdbConstants.URL_LIST;
import static anders.olsen.moviebrowser.constants.TmdbConstants.URL_ON_THE_AIR;
import static anders.olsen.moviebrowser.constants.TmdbConstants.URL_POPULAR;
import static anders.olsen.moviebrowser.constants.TmdbConstants.URL_TOP_RATED;
import static anders.olsen.moviebrowser.constants.TmdbConstants.URL_TV;
import static anders.olsen.moviebrowser.constants.TmdbConstants.URL_VIDEOS;

/**
 * Class performing operations against the TMDB API.
 * This class is used when obtaining Movie-related information.
 * <p>
 * The constructor is ackage-private,
 * only reachable to activites through the TmdbManager singleton instance.
 *
 * @author Anders Engen Olsen
 */
public class TvService extends BaseMovieTvService {

    TvService(Context context, RequestQueue queue, JsonParser jsonParser, String apiKey) {
        super(context, queue, jsonParser, apiKey);
    }

    /**
     * Fetching current popular URL_TV shows
     *
     * @param listener listener, fired when downloaded
     * @param page     page to load from API
     * @see #fetchMediaObjects(String, TmdbListener, String)
     */
    @Override
    public void getPopular(int page, final TmdbListener<ArrayList<MediaObject>> listener) {
        Uri uri = Uri.parse(API_BASE_URL + URL_TV + URL_POPULAR).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, apiKey)
                .appendQueryParameter(PARAM_PAGE, String.valueOf(page))
                .build();

        fetchMediaObjects(uri.toString(), listener, MEDIA_TYPE_TV);
    }

    /**
     * Fetching top rated URL_TV shows.
     *
     * @param listener listener, fired when downloaded
     * @param page     page to load from API
     * @see #fetchMediaObjects(String, TmdbListener, String)
     */
    @Override
    public void getTopRated(int page, final TmdbListener<ArrayList<MediaObject>> listener) {
        Uri uri = Uri.parse(API_BASE_URL + URL_TV + URL_TOP_RATED).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, apiKey)
                .appendQueryParameter(PARAM_PAGE, String.valueOf(page))
                .build();

        fetchMediaObjects(uri.toString(), listener, MEDIA_TYPE_TV);
    }

    /**
     * Fetching URL_TV on the air.
     *
     * @param listener listener, fired when downloaded
     * @param page     page to load from API
     * @see #fetchMediaObjects(String, TmdbListener, String)
     */
    @Override
    public void getUpcoming(int page, final TmdbListener<ArrayList<MediaObject>> listener) {
        Uri uri = Uri.parse(API_BASE_URL + URL_TV + URL_ON_THE_AIR).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, apiKey)
                .appendQueryParameter(PARAM_PAGE, String.valueOf(page))
                .build();

        fetchMediaObjects(uri.toString(), listener, MEDIA_TYPE_TV);
    }

    /**
     * Downloding all tv genres.
     *
     * @param listener fired when downloaded.
     */
    @Override
    public void getAllGenres(TmdbListener<ArrayList<Genre>> listener) {
        Uri uri = Uri.parse(API_BASE_URL + URL_GENRE + URL_TV + URL_LIST).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, apiKey)
                .build();

        fetchMediaGenres(uri.toString(), listener);
    }

    /**
     * Fetching mediaobjects by genre
     *
     * @param genre    {@link Genre}
     * @param page     page to load from API
     * @param listener fired when downloaded
     */
    @Override
    public void getByGenre(int page, Genre genre, TmdbListener<ArrayList<MediaObject>> listener) {
        int genreId = genre.getId();

        Uri uri = Uri.parse(API_BASE_URL + URL_DISCOVER + MEDIA_TYPE_TV)
                .buildUpon()
                .appendQueryParameter(PARAM_API_KEY, apiKey)
                .appendQueryParameter(PARAM_WITH_GENRES, String.valueOf(genreId))
                .appendQueryParameter(PARAM_PAGE, String.valueOf(page))
                .appendQueryParameter(PARAM_SORT_BY, POPULARITY_DESC)
                .build();

        fetchMediaObjects(uri.toString(), listener, MEDIA_TYPE_TV);
    }

    /**
     * Returning youtube id for given mediaId, if one is present.
     * The youtube ID references a trailer or teaser for the mediaobject
     *
     * @param mediaId  Tmdb id
     * @param listener fired when downloaded
     * @throws java.util.NoSuchElementException no trailer url found
     */
    @Override
    public void getTrailerUrl(int mediaId, TmdbListener<String> listener) {
        Uri uri = Uri.parse(API_BASE_URL + URL_TV + mediaId + "/" + URL_VIDEOS).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, apiKey)
                .build();

        super.getTrailerUrl(uri, listener);
    }
}