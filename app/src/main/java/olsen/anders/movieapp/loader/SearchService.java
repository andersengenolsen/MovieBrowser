package olsen.anders.movieapp.loader;

import android.content.Context;
import android.net.Uri;

import com.android.volley.RequestQueue;

import java.util.ArrayList;

import olsen.anders.movieapp.model.MediaObject;

import static olsen.anders.movieapp.constants.TmdbConstants.API_BASE_URL;
import static olsen.anders.movieapp.constants.TmdbConstants.PARAM_API_KEY;
import static olsen.anders.movieapp.constants.TmdbConstants.PARAM_QUERY;
import static olsen.anders.movieapp.constants.TmdbConstants.URL_SEARCH;

/**
 * Class responsible for searching after movies and TV shows at TMDB
 *
 * @author Anders Engen Olsen
 */

public class SearchService extends BaseService {

    /**
     * Constructor.
     */
    SearchService(Context context, RequestQueue queue, JsonParser jsonParser, String apiKey) {
        super(context, queue, jsonParser, apiKey);
    }

    /**
     * Searching for movies and TV shows.
     * Calling fetchMediaObjects to get the response.
     * Type parameter for fetchMediaObjects is null - thus, the jsonParser will try to determine
     * the type itself.
     *
     * @param query    query to search for
     * @param listener TmdbListener
     * @see #fetchMediaObjects(String, TmdbListener, String)
     */
    public void searchMoviesAndTV(String query,
                                  final TmdbListener<ArrayList<MediaObject>> listener) {

        Uri uri = Uri.parse(API_BASE_URL + URL_SEARCH).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, apiKey)
                .appendQueryParameter(PARAM_QUERY, query)
                .build();

        fetchMediaObjects(uri.toString(), listener, null);
    }
}
