package anders.olsen.moviebrowser.loader;

import android.content.Context;

import com.android.volley.RequestQueue;

import java.util.ArrayList;

import anders.olsen.moviebrowser.interfaces.Saveable;
import anders.olsen.moviebrowser.listener.TmdbListener;
import anders.olsen.moviebrowser.model.MediaObject;

import static anders.olsen.moviebrowser.constants.TmdbConstants.FAVORITE;
import static anders.olsen.moviebrowser.constants.TmdbConstants.MEDIA_TYPE_TV;
import static anders.olsen.moviebrowser.constants.TmdbConstants.WATCHLIST;

/**
 * Class responsible for handling calls agains a users TV-lists at TMDB.
 * {@link Saveable} common methods to save mediaobjects.
 *
 * @author Anders engen olsen
 */
public class TvAccountService extends AccountService implements Saveable {

    /**
     * Constructor.
     *
     * @param context    Activity context
     * @param queue      RequestQueue
     * @param jsonParser JsonParser
     * @param apiKey     API-key to TMDB
     */
    TvAccountService(Context context, RequestQueue queue, JsonParser jsonParser, String apiKey) {
        super(context, queue, jsonParser, apiKey);
    }

    /**
     * Adding/deleting mediaobject from watchlist
     *
     * @param mo       mediaobject to add
     * @param status   true if add, false if delete
     * @param listener fired when done
     */
    @Override
    public void addToWatchlist(MediaObject mo, boolean status, TmdbListener<String> listener) {
        super.postToWatchList(mo, status, listener);

    }

    /**
     * Adding/deleting mediaobject from favoritelist
     *
     * @param mo       mediaobject to add
     * @param status   true if add, false if delete
     * @param listener fired when done
     */
    @Override
    public void addToFavoriteList(MediaObject mo, boolean status, TmdbListener<String> listener) {
        super.postToFavoriteList(mo, status, listener);
    }

    /**
     * Checking whether a mediaobject is in a users favorite list.
     *
     * @param mo       mediaobject to check
     * @param listener fired when done
     */
    @Override
    public void hasInFavoriteList(MediaObject mo, TmdbListener<Boolean> listener) {
        TmdbListener<ArrayList<MediaObject>> innerListener =
                constructListInnerListener(mo, listener);

        getFavoriteList(1, innerListener);
    }

    /**
     * Checking whether a mediaobject is in a users watchlist.
     *
     * @param mo       mediaobject to check
     * @param listener fired when done
     */
    @Override
    public void hasInWatchlist(MediaObject mo, TmdbListener<Boolean> listener) {
        TmdbListener<ArrayList<MediaObject>> innerListener =
                constructListInnerListener(mo, listener);

        getWatchlist(1, innerListener);
    }

    /**
     * Fetching users watchlist
     *
     * @param page     pagination
     * @param listener fired when done
     */
    @Override
    public void getWatchlist(int page, TmdbListener<ArrayList<MediaObject>> listener) {
        if (!validateSession(listener))
            return;
        fetchList(page, WATCHLIST, MEDIA_TYPE_TV, listener);
    }

    /**
     * Fetching users favoritelist
     *
     * @param page     pagination
     * @param listener fired when done
     */
    @Override
    public void getFavoriteList(int page, final TmdbListener<ArrayList<MediaObject>> listener) {
        if (!validateSession(listener))
            return;
        fetchList(page, FAVORITE, MEDIA_TYPE_TV, listener);
    }
}
