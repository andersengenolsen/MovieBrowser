package olsen.anders.movieapp.loader;

import android.content.Context;
import android.net.Uri;

import com.android.volley.RequestQueue;

import java.util.ArrayList;

import olsen.anders.movieapp.model.Genre;
import olsen.anders.movieapp.model.MediaObject;

import static olsen.anders.movieapp.constants.TmdbConstants.API_BASE_URL;
import static olsen.anders.movieapp.constants.TmdbConstants.MEDIA_TYPE_MOVIE;
import static olsen.anders.movieapp.constants.TmdbConstants.PARAM_API_KEY;
import static olsen.anders.movieapp.constants.TmdbConstants.PARAM_SORT_BY;
import static olsen.anders.movieapp.constants.TmdbConstants.PARAM_WITH_GENRES;
import static olsen.anders.movieapp.constants.TmdbConstants.POPULARITY_DESC;
import static olsen.anders.movieapp.constants.TmdbConstants.URL_DISCOVER;
import static olsen.anders.movieapp.constants.TmdbConstants.URL_GENRE;
import static olsen.anders.movieapp.constants.TmdbConstants.URL_LIST;
import static olsen.anders.movieapp.constants.TmdbConstants.URL_MOVIE;
import static olsen.anders.movieapp.constants.TmdbConstants.URL_POPULAR;
import static olsen.anders.movieapp.constants.TmdbConstants.URL_TOP_RATED;
import static olsen.anders.movieapp.constants.TmdbConstants.URL_UPCOMING;
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
public class MovieService extends BaseMovieTvService {

    MovieService(Context context, RequestQueue queue, JsonParser jsonParser, String apiKey) {
        super(context, queue, jsonParser, apiKey);
    }

    /**
     * Fetching current popular movies.
     *
     * @param listener listener fired when downloaded
     * @see #fetchMediaObjects(String, TmdbListener, String)
     */
    @Override
    public void getPopular(final TmdbListener<ArrayList<MediaObject>> listener) {
        Uri uri = Uri.parse(API_BASE_URL + URL_MOVIE + URL_POPULAR).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, apiKey)
                .build();

        fetchMediaObjects(uri.toString(), listener, MEDIA_TYPE_MOVIE);
    }

    /**
     * Fetching top rated movies.
     *
     * @param listener listener, fired when downloaded
     * @see #fetchMediaObjects(String, TmdbListener, String)
     */
    @Override
    public void getTopRated(final TmdbListener<ArrayList<MediaObject>> listener) {
        Uri uri = Uri.parse(API_BASE_URL + URL_MOVIE + URL_TOP_RATED).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, apiKey)
                .build();

        fetchMediaObjects(uri.toString(), listener, MEDIA_TYPE_MOVIE);
    }

    /**
     * Fetching upcoming movies.
     *
     * @param listener listener, fired when downloaded
     * @see #fetchMediaObjects(String, TmdbListener, String)
     */
    @Override
    public void getUpcoming(final TmdbListener<ArrayList<MediaObject>> listener) {
        Uri uri = Uri.parse(API_BASE_URL + URL_MOVIE + URL_UPCOMING).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, apiKey)
                .build();

        fetchMediaObjects(uri.toString(), listener, MEDIA_TYPE_MOVIE);
    }

    /**
     * Downloading mediaobjects based on genre
     *
     * @param genre    {@link Genre}
     * @param listener fired when downloaded
     */
    @Override
    public void getByGenre(Genre genre, TmdbListener<ArrayList<MediaObject>> listener) {
        int genreId = genre.getId();

        Uri uri = Uri.parse(API_BASE_URL + URL_DISCOVER + MEDIA_TYPE_MOVIE)
                .buildUpon()
                .appendQueryParameter(PARAM_API_KEY, apiKey)
                .appendQueryParameter(PARAM_SORT_BY, POPULARITY_DESC)
                .appendQueryParameter(PARAM_WITH_GENRES, String.valueOf(genreId))
                .build();

        fetchMediaObjects(uri.toString(), listener, MEDIA_TYPE_MOVIE);
    }

    /**
     * Downloding all movie genres.
     *
     * @param listener fired when downloaded.
     */
    @Override
    public void getAllGenres(TmdbListener<ArrayList<Genre>> listener) {
        Uri uri = Uri.parse(API_BASE_URL + URL_GENRE + URL_MOVIE + URL_LIST).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, apiKey)
                .build();

        fetchMediaGenres(uri.toString(), listener);
    }

    /**
     * Returning youtube id for given mediaId, if one is present.
     * The youtube ID references a trailer or teaser for the mediaobject
     *
     * @param mediaId  Tmdb id
     * @param listener fired when downloaded
     */
    @Override
    public void getTrailerUrl(int mediaId, TmdbListener<String> listener) {
        Uri uri = Uri.parse(API_BASE_URL + URL_MOVIE + mediaId + "/" + URL_VIDEOS).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, apiKey)
                .build();

        super.getTrailerUrl(uri, listener);
    }
}
