package olsen.anders.movieapp.loader;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.ArrayList;

import olsen.anders.movieapp.R;
import olsen.anders.movieapp.model.MediaObject;

/**
 * Base class for all services.
 *
 * @author Anders Engen Olsen
 */
public abstract class BaseService {

    /**
     * RequestQueue Volley-library
     */
    protected RequestQueue queue;
    /**
     * Activity context
     */
    protected Context context;
    /**
     * @see JsonParser
     */
    protected JsonParser jsonParser;
    /**
     * API KEY
     */
    protected String apiKey;

    /**
     * Constructor.
     */
    BaseService(Context context, RequestQueue queue, JsonParser jsonParser, String apiKey) {
        this.context = context;
        this.queue = queue;
        this.jsonParser = jsonParser;
        this.apiKey = apiKey;
    }

    /**
     * Fetching JSON from given URL. Listener fired when response is received.
     * Extracting MediaObjects from JSON through JsonParser
     *
     * @param url      API URL
     * @param listener callback, fired when downloaded
     * @param type     mediaobject type, movie / tv.
     * @see JsonParser#parseMediaObjects(JSONObject, String)
     */
    void fetchMediaObjects(String url,
                           final TmdbListener<ArrayList<MediaObject>> listener,
                           final String type) {
        // Volley-request
        final JsonObjectRequest json = new JsonObjectRequest(
                Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                listener.onSuccess(jsonParser.parseMediaObjects(response, type));
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
