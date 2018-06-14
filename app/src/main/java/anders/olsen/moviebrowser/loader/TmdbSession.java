package anders.olsen.moviebrowser.loader;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import anders.olsen.moviebrowser.R;
import anders.olsen.moviebrowser.listener.TmdbListener;

import static anders.olsen.moviebrowser.constants.TmdbConstants.AUTH_URL;
import static anders.olsen.moviebrowser.constants.TmdbConstants.BASE_URL_AUTH;
import static anders.olsen.moviebrowser.constants.TmdbConstants.NEW_SESSION;
import static anders.olsen.moviebrowser.constants.TmdbConstants.NEW_TOKEN;
import static anders.olsen.moviebrowser.constants.TmdbConstants.PARAM_API_KEY;
import static anders.olsen.moviebrowser.constants.TmdbConstants.PARAM_REQUEST_TOKEN;
import static anders.olsen.moviebrowser.constants.TmdbConstants.SHARED_PREF_ID;
import static anders.olsen.moviebrowser.constants.TmdbConstants.SHARED_PREF_SESSION;

/**
 * Package private class. Calls to this class is done through AccountService.
 * This class creates a session, and the corresponding session id is written to SharedPrefs.
 *
 * @author Anders Engen Olsen
 * @see TmdbManager
 */
class TmdbSession {

    /**
     * Api-key
     */
    private String apiKey;
    /**
     * Token for authentication
     */
    private String token;
    /**
     * Activity context
     */
    private Context context;
    /**
     * Volley RequestQueue
     */
    private RequestQueue queue;
    /**
     * @see JsonParser
     */
    private JsonParser jsonParser;

    /**
     * Contstructor.
     *
     * @param context    Actitivty Context
     * @param queue      volley RequestQueue
     * @param jsonParser JsonParser
     * @see JsonParser
     */
    TmdbSession(Context context, RequestQueue queue, JsonParser jsonParser, String apiKey) {
        this.context = context;
        this.queue = queue;
        this.jsonParser = jsonParser;
        this.apiKey = apiKey;
    }

    /**
     * Driver-method to generate a new token, used when authenticating
     *
     * @param listener TmdbListener
     */
    void requestToken(final TmdbListener<String> listener) {
        Uri uri = Uri.parse(BASE_URL_AUTH + NEW_TOKEN).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, apiKey)
                .build();

        requestToken(uri.toString(), listener);
    }

    /**
     * Generating a new token, used in URL when the user accepts the application at TMDB.
     *
     * @param url      url where user accepts the app
     * @param listener TmdbListener
     */
    private void requestToken(String url, final TmdbListener<String> listener) {
        final JsonObjectRequest json = new JsonObjectRequest(
                Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                token = jsonParser.parseToken(response);

                String url = AUTH_URL + token;
                listener.onSuccess(url);

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

    /**
     * Driver method to generate a session id
     *
     * @param listener TmdbListener
     */
    void createSession(final TmdbListener<String> listener) {
        Uri uri = Uri.parse(BASE_URL_AUTH + NEW_SESSION).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, apiKey)
                .appendQueryParameter(PARAM_REQUEST_TOKEN, token)
                .build();

        createSession(uri.toString(), listener);
    }

    /**
     * Generating a session id, used to access a users tmdb account
     *
     * @param url      url to generate a session id
     * @param listener TmdbListener
     */
    private void createSession(String url, final TmdbListener<String> listener) {
        final JsonObjectRequest json = new JsonObjectRequest(
                Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                // Extracting session with jsonParser
                String sessionId = jsonParser.parseSession(response);

                // Writing to shared prefs.
                if (sessionId != null) {
                    SharedPreferences sharedPreferences =
                            context.getSharedPreferences(SHARED_PREF_SESSION, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putString(SHARED_PREF_ID, sessionId);

                    editor.apply();

                    listener.onSuccess(context.getString(R.string.logged_in));
                } else {
                    listener.onError(context.getString(R.string.error_login));
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(context.getString(R.string.not_logged_in));
            }
        }
        );

        queue.add(json);
    }

    /**
     * @return session id from shared prefs, null if none found
     */
    String getSessionId() {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF_SESSION,
                Context.MODE_PRIVATE);

        return prefs.getString(SHARED_PREF_ID, null);
    }

    /**
     * @return true if session id in shared prefs.
     */
    boolean isLoggedIn() {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF_SESSION,
                Context.MODE_PRIVATE);
        return prefs.getString(SHARED_PREF_ID, null) != null;
    }
}
