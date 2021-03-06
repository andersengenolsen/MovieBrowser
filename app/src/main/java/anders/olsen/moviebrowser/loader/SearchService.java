package anders.olsen.moviebrowser.loader;

import android.content.Context;
import android.net.Uri;

import com.android.volley.RequestQueue;

import java.util.ArrayList;

import anders.olsen.moviebrowser.listener.TmdbListener;
import anders.olsen.moviebrowser.model.MediaObject;

import static anders.olsen.moviebrowser.constants.TmdbConstants.API_BASE_URL;
import static anders.olsen.moviebrowser.constants.TmdbConstants.PARAM_API_KEY;
import static anders.olsen.moviebrowser.constants.TmdbConstants.PARAM_PAGE;
import static anders.olsen.moviebrowser.constants.TmdbConstants.PARAM_QUERY;
import static anders.olsen.moviebrowser.constants.TmdbConstants.URL_SEARCH;

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
     * @param page     page to load from api
     * @param listener TmdbListener
     * @see #fetchMediaObjects(String, TmdbListener, String)
     */
    public void searchMoviesAndTV(String query, int page,
                                  final TmdbListener<ArrayList<MediaObject>> listener) {

        Uri uri = Uri.parse(API_BASE_URL + URL_SEARCH).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, apiKey)
                .appendQueryParameter(PARAM_PAGE, String.valueOf(page))
                .appendQueryParameter(PARAM_QUERY, query)
                .build();

        fetchMediaObjects(uri.toString(), listener, null);
    }
}
