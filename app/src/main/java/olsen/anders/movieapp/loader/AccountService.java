package olsen.anders.movieapp.loader;

import android.content.Context;
import android.net.Uri;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import olsen.anders.movieapp.R;
import olsen.anders.movieapp.model.MediaObject;

import static olsen.anders.movieapp.constants.JsonConstants.VALUE;
import static olsen.anders.movieapp.constants.TmdbConstants.API_BASE_URL;
import static olsen.anders.movieapp.constants.TmdbConstants.BASE_URL_ACCOUNT;
import static olsen.anders.movieapp.constants.TmdbConstants.FAVORITE;
import static olsen.anders.movieapp.constants.TmdbConstants.JSON_MEDIA_ID;
import static olsen.anders.movieapp.constants.TmdbConstants.JSON_MEDIA_TYPE;
import static olsen.anders.movieapp.constants.TmdbConstants.MEDIA_TYPE_MOVIE;
import static olsen.anders.movieapp.constants.TmdbConstants.MEDIA_TYPE_MOVIES;
import static olsen.anders.movieapp.constants.TmdbConstants.MEDIA_TYPE_TV;
import static olsen.anders.movieapp.constants.TmdbConstants.PARAM_API_KEY;
import static olsen.anders.movieapp.constants.TmdbConstants.PARAM_SESSION_ID;
import static olsen.anders.movieapp.constants.TmdbConstants.RATING;
import static olsen.anders.movieapp.constants.TmdbConstants.WATCHLIST;


/**
 * Class responsible for handling operations against Tmdb API, when the user is logged in.
 *
 * @author Anders Engen Olsen
 * @see TmdbManager
 */
public class AccountService extends BaseService {

    /**
     * Session ID
     */
    private String sessionId;
    /**
     * @see TmdbSession
     */
    private TmdbSession session;

    /**
     * Constructor.
     *
     * @param context    Activity context
     * @param queue      RequestQueue
     * @param jsonParser JsonParser
     * @param apiKey     API-key to TMDB
     */
    AccountService(Context context, RequestQueue queue, JsonParser jsonParser, String apiKey) {
        super(context, queue, jsonParser, apiKey);
        session = new TmdbSession(context, queue, jsonParser, apiKey);
    }

    /**
     * Adding mediaobject to watchlist
     *
     * @param mo       MediaObject
     * @param listener TmdbListener
     * @see #postJson(JSONObject, Uri, TmdbListener)
     */
    public void addToWatchList(MediaObject mo, boolean status, TmdbListener<String> listener) {
        if (!validateSession(listener))
            return;
        Uri uri = Uri.parse(BASE_URL_ACCOUNT + WATCHLIST).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, apiKey)
                .appendQueryParameter(PARAM_SESSION_ID, sessionId)
                .build();

        JSONObject json = new JSONObject();
        try {
            json.put(JSON_MEDIA_TYPE, mo.getType());
            json.put(JSON_MEDIA_ID, mo.getId());
            json.put(WATCHLIST, status);

        } catch (JSONException err) {
            listener.onError(context.getString(R.string.error_json));
            return;
        }

        postJson(json, uri, listener);
    }

    /**
     * Adding mediaobject to favoritelist
     *
     * @param mo       MediaObject
     * @param listener TmdbListener
     * @see #postJson(JSONObject, Uri, TmdbListener)
     */
    public void addToFavoriteList(MediaObject mo, boolean status, TmdbListener<String> listener) {
        if (!validateSession(listener))
            return;

        Uri uri = Uri.parse(BASE_URL_ACCOUNT + FAVORITE).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, apiKey)
                .appendQueryParameter(PARAM_SESSION_ID, sessionId)
                .build();

        JSONObject json = new JSONObject();
        try {
            json.put(JSON_MEDIA_TYPE, mo.getType());
            json.put(JSON_MEDIA_ID, mo.getId());
            json.put(FAVORITE, status);

        } catch (JSONException err) {
            listener.onError(context.getString(R.string.error_json));
            return;
        }
        postJson(json, uri, listener);
    }

    /**
     * Checking if a given mediaobject is in the users favorite list.
     *
     * @param mo       mediaobject
     * @param listener fired when call finished
     * @see #constructListInnerListener(MediaObject, TmdbListener)
     */
    public void hasInFavorite(final MediaObject mo, final TmdbListener<Boolean> listener) {
        if (!validateSession(listener))
            return;

        TmdbListener<ArrayList<MediaObject>> innerListener =
                constructListInnerListener(mo, listener);

        if (mo.isMovie())
            getFavoriteListMovie(innerListener);
        else
            getFavoriteListTv(innerListener);
    }

    /**
     * Checking if a given mediaobject is in the users watchlist.
     *
     * @param mo       mediaobject
     * @param listener fired when call finished
     * @see #constructListInnerListener(MediaObject, TmdbListener)
     */
    public void hasInWatchlist(final MediaObject mo, final TmdbListener<Boolean> listener) {
        if (!validateSession(listener))
            return;

        TmdbListener<ArrayList<MediaObject>> innerListener =
                constructListInnerListener(mo, listener);

        if (mo.isMovie())
            getWatchlistMovie(innerListener);
        else
            getWatchlistTv(innerListener);
    }


    /**
     * Fetching watchlist for tv shows
     *
     * @param listener TmdbListener
     */
    public void getWatchlistTv(final TmdbListener<ArrayList<MediaObject>> listener) {
        if (!validateSession(listener))
            return;
        fetchList(WATCHLIST, MEDIA_TYPE_TV, listener);
    }

    /**
     * Fetching watchlist for movies
     *
     * @param listener TmdbListener
     */
    public void getWatchlistMovie(final TmdbListener<ArrayList<MediaObject>> listener) {
        if (!validateSession(listener))
            return;
        fetchList(WATCHLIST, MEDIA_TYPE_MOVIE, listener);
    }

    /**
     * Fetching favorite list for tv shows
     *
     * @param listener TmdbListener
     */
    public void getFavoriteListTv(final TmdbListener<ArrayList<MediaObject>> listener) {
        if (!validateSession(listener))
            return;
        fetchList(FAVORITE, MEDIA_TYPE_TV, listener);
    }

    /**
     * Fetching favorite list for movies
     *
     * @param listener TmdbListener
     */
    public void getFavoriteListMovie(final TmdbListener<ArrayList<MediaObject>> listener) {
        if (!validateSession(listener))
            return;
        fetchList(FAVORITE, MEDIA_TYPE_MOVIE, listener);
    }


    /**
     * public driver-method, requesting a token
     *
     * @param listener TmdbListener
     * @see TmdbSession#requestToken(TmdbListener)
     */
    public void requestToken(final TmdbListener<String> listener) {
        session.requestToken(listener);
    }

    /**
     * public driver-method, creating a session
     *
     * @param listener TmdbListener
     * @see TmdbSession#createSession(TmdbListener)
     */
    public void createSession(final TmdbListener<String> listener) {
        session.createSession(listener);
    }

    /**
     * Adding rating to a mediaobject.
     *
     * @param mediaObject movie / tv show to rate
     * @param rating      the rating, 0.5 - 10
     * @param listener    listener
     */
    public void addRating(MediaObject mediaObject, int rating,
                          final TmdbListener<String> listener) {

        if (!validateSession(listener))
            return;

        String mediaType = mediaObject.getType();
        int mediaId = mediaObject.getId();

        Uri uri = Uri.parse(API_BASE_URL + mediaType + "/" + mediaId + "/" + RATING)
                .buildUpon()
                .appendQueryParameter(PARAM_API_KEY, apiKey)
                .appendQueryParameter(PARAM_SESSION_ID, sessionId)
                .build();

        JSONObject json = new JSONObject();
        try {
            json.put(VALUE, rating);
        } catch (JSONException err) {
            listener.onError(context.getString(R.string.error_json));
            return;
        }

        postJson(json, uri, listener);
    }

    /**
     * public driver-method, checking whether user is logged in
     *
     * @return true if logged in
     * @see TmdbSession#isLoggedIn()
     */

    public boolean isLoggedIn() {
        return session.isLoggedIn();
    }


    /**
     * Validating a session, checking that the user has a session id in shared preferences.
     *
     * @param listener TmdbListener, onError() fired if no session id.
     * @return true if valid session id
     */
    private <AnyType> boolean validateSession(TmdbListener<AnyType> listener) {
        sessionId = session.getSessionId();
        if (sessionId == null) {
            listener.onError(context.getString(R.string.not_logged_in));
            return false;
        }
        return true;
    }

    /**
     * Called from all methods where a list is fetched
     *
     * @param listType  favorite / watchlist
     * @param mediaType movie / tv show
     * @param listener  TmdbListener
     */
    private void fetchList(String listType, final String mediaType,
                           final TmdbListener<ArrayList<MediaObject>> listener) {
        if (!validateSession(listener))
            return;

        String type = (mediaType.equals(MEDIA_TYPE_MOVIE)) ? MEDIA_TYPE_MOVIES : MEDIA_TYPE_TV;

        Uri uri = Uri.parse(BASE_URL_ACCOUNT + listType + "/" + type).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, apiKey)
                .appendQueryParameter(PARAM_SESSION_ID, sessionId)
                .build();

        fetchMediaObjects(uri.toString(), listener, mediaType);
    }

    /**
     * Posting JSON to TMDB
     *
     * @param jsonPost Json to post
     * @param uri      uri to post to
     * @param listener listener, fired when call done
     */
    private void postJson(JSONObject jsonPost, Uri uri, final TmdbListener<String> listener) {
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST,
                uri.toString(), jsonPost, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                boolean success = jsonParser.parseJsonResponse(response);

                if (success)
                    listener.onSuccess(context.getString(R.string.done));
                else
                    listener.onError(context.getString(R.string.error));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(context.getString(R.string.error));
            }
        });
        queue.add(postRequest);
    }

    /**
     * Constructing an "inner" listener which is used to loop through an array of mediaobjects,
     * returning true if the supplied media object is in the fetched arraylist.
     *
     * @param mo       mediaobject to check is in list
     * @param listener fired when list is checked
     * @return TmdbListener
     */
    private TmdbListener<ArrayList<MediaObject>> constructListInnerListener(
            final MediaObject mo, final TmdbListener<Boolean> listener) {

        TmdbListener<ArrayList<MediaObject>> innerListener =
                new TmdbListener<ArrayList<MediaObject>>() {
                    @Override
                    public void onSuccess(ArrayList<MediaObject> result) {
                        for (MediaObject m : result) {
                            if (m.getId() == mo.getId()) {
                                listener.onSuccess(true);
                                return;
                            }
                        }
                        listener.onSuccess(false);
                    }

                    @Override
                    public void onError(String result) {
                        listener.onError(context.getString(R.string.error));
                    }
                };
        return innerListener;
    }
}
