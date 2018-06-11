package olsen.anders.movieapp.loader;

import android.content.Context;
import android.net.Uri;

import com.android.volley.RequestQueue;

import java.util.ArrayList;

import olsen.anders.movieapp.model.Genre;
import olsen.anders.movieapp.model.MediaObject;

import static olsen.anders.movieapp.constants.TmdbConstants.API_BASE_URL;
import static olsen.anders.movieapp.constants.TmdbConstants.MEDIA_TYPE_MOVIE;
import static olsen.anders.movieapp.constants.TmdbConstants.MEDIA_TYPE_TV;
import static olsen.anders.movieapp.constants.TmdbConstants.PARAM_API_KEY;
import static olsen.anders.movieapp.constants.TmdbConstants.PARAM_SORT_BY;
import static olsen.anders.movieapp.constants.TmdbConstants.PARAM_WITH_GENRES;
import static olsen.anders.movieapp.constants.TmdbConstants.POPULARITY_DESC;
import static olsen.anders.movieapp.constants.TmdbConstants.URL_DISCOVER;
import static olsen.anders.movieapp.constants.TmdbConstants.URL_GENRE;
import static olsen.anders.movieapp.constants.TmdbConstants.URL_LIST;
import static olsen.anders.movieapp.constants.TmdbConstants.URL_MOVIE;
import static olsen.anders.movieapp.constants.TmdbConstants.URL_ON_THE_AIR;
import static olsen.anders.movieapp.constants.TmdbConstants.URL_POPULAR;
import static olsen.anders.movieapp.constants.TmdbConstants.URL_TOP_RATED;
import static olsen.anders.movieapp.constants.TmdbConstants.URL_TV;
import static olsen.anders.movieapp.constants.TmdbConstants.URL_VIDEOS;

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
     * @see #fetchMediaObjects(String, TmdbListener, String)
     */
    @Override
    public void getPopular(final TmdbListener<ArrayList<MediaObject>> listener) {
        Uri uri = Uri.parse(API_BASE_URL + URL_TV + URL_POPULAR).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, apiKey)
                .build();

        fetchMediaObjects(uri.toString(), listener, MEDIA_TYPE_TV);
    }

    /**
     * Fetching top rated URL_TV shows.
     *
     * @param listener listener, fired when downloaded
     * @see #fetchMediaObjects(String, TmdbListener, String)
     */
    @Override
    public void getTopRated(final TmdbListener<ArrayList<MediaObject>> listener) {
        Uri uri = Uri.parse(API_BASE_URL + URL_TV + URL_TOP_RATED).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, apiKey)
                .build();

        fetchMediaObjects(uri.toString(), listener, MEDIA_TYPE_TV);
    }

    /**
     * Fetching URL_TV on the air.
     *
     * @param listener listener, fired when downloaded
     * @see #fetchMediaObjects(String, TmdbListener, String)
     */
    @Override
    public void getUpcoming(final TmdbListener<ArrayList<MediaObject>> listener) {
        Uri uri = Uri.parse(API_BASE_URL + URL_TV + URL_ON_THE_AIR).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, apiKey)
                .build();

        fetchMediaObjects(uri.toString(), listener, MEDIA_TYPE_TV);
    }

    /**
     * Downloading list with URL_TV genres
     *
     * @see #fetchMediaGenres(String)
     */
    public void downloadTVGenres() {
        Uri uri = Uri.parse(API_BASE_URL + URL_GENRE + URL_TV + URL_LIST).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, apiKey)
                .build();

        fetchMediaGenres(uri.toString());
    }

    /**
     * Fetching mediaobjects by genre
     *
     * @param genre    {@link Genre}
     * @param listener fired when downloaded
     */
    @Override
    public void getByGenre(Genre genre, TmdbListener<ArrayList<MediaObject>> listener) {
        int genreId = genre.getId();

        Uri uri = Uri.parse(API_BASE_URL + URL_DISCOVER + URL_MOVIE)
                .buildUpon()
                .appendQueryParameter(PARAM_API_KEY, apiKey)
                .appendQueryParameter(PARAM_WITH_GENRES, String.valueOf(genreId))
                .appendQueryParameter(PARAM_SORT_BY, POPULARITY_DESC)
                .build();

        fetchMediaObjects(uri.toString(), listener, MEDIA_TYPE_MOVIE);
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