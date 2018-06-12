package olsen.anders.movieapp.loader;

import android.content.Context;
import android.net.Uri;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.ArrayList;

import olsen.anders.movieapp.R;
import olsen.anders.movieapp.listener.TmdbListener;
import olsen.anders.movieapp.model.Genre;
import olsen.anders.movieapp.model.MediaObject;

/**
 * Abstract class, responsible for fetching mediaobjects and genres.
 * <p>
 * Subclasses are TvService and MovieService
 *
 * @author Anders Engen Olsen
 * @see TvService
 * @see MovieService
 */
public abstract class BaseMovieTvService extends BaseService {

    /**
     * Constructor.
     *
     * @param context activity context
     */
    BaseMovieTvService(Context context, RequestQueue queue, JsonParser jsonParser, String apiKey) {
        super(context, queue, jsonParser, apiKey);
    }

    /**
     * Implemented in subclasses when trailer shall be shown
     *
     * @param mediaId  mediaobject id
     * @param listener TmdbListener
     */
    public abstract void getTrailerUrl(int mediaId, TmdbListener<String> listener);

    /**
     * Fetching popular movie / tv
     *
     * @param page     page from api
     * @param listener fired when downloaded
     */
    public abstract void getPopular(int page, final TmdbListener<ArrayList<MediaObject>> listener);

    /**
     * Fetching upcoming movie / tv
     *
     * @param listener fired when downloaded
     * @param page     page to load from API
     */
    public abstract void getUpcoming(int page, final TmdbListener<ArrayList<MediaObject>> listener);

    /**
     * Fetching top rated movie / tv
     *
     * @param listener fired when downloaded
     * @param page     page to load from API
     */
    public abstract void getTopRated(int page, final TmdbListener<ArrayList<MediaObject>> listener);

    /**
     * Downloading all genres.
     *
     * @param listener fired when downloaded.
     */
    public abstract void getAllGenres(final TmdbListener<ArrayList<Genre>> listener);

    /**
     * Fetching by genre
     *
     * @param genre    {@link Genre}
     * @param page     page to load from API
     * @param listener fired when downloaded
     */
    public abstract void getByGenre(int page, Genre genre,
                                    final TmdbListener<ArrayList<MediaObject>> listener);

    /**
     * Downloading JSON containing genres
     *
     * @param url url with genres
     * @see JsonParser#parseGenres(JSONObject)
     */
    void fetchMediaGenres(String url, final TmdbListener<ArrayList<Genre>> listener) {
        // Volley-request
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                ArrayList<Genre> genres = jsonParser.parseGenres(response);
                if (listener != null)
                    listener.onSuccess(genres);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (listener != null)
                    listener.onError(context.getString(R.string.error));
            }
        }
        );
        queue.add(jsonObjectRequest);
    }

    /**
     * Returning youtube id for given mediaId, if one is present.
     * The youtube ID references a trailer or teaser for the mediaobject
     *
     * @param uri TMDB API url
     * @throws java.util.NoSuchElementException no trailer url found
     */
    void getTrailerUrl(Uri uri, final TmdbListener<String> listener) {
        // Volley-call
        final JsonObjectRequest json = new JsonObjectRequest(
                Request.Method.GET, uri.toString(),
                null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                String trailer = jsonParser.parseTrailerID(response);
                if (trailer == null) {
                    listener.onError(context.getString(R.string.error));
                    return;
                }
                listener.onSuccess(trailer);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(context.getString(R.string.error));
            }
        }
        );
        queue.add(json);
    }
}
