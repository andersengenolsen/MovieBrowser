package anders.olsen.moviebrowser.interfaces;

import java.util.ArrayList;

import anders.olsen.moviebrowser.listener.TmdbListener;
import anders.olsen.moviebrowser.model.MediaObject;

/**
 * Interface to be implemented by services who provide methods for saving mediaobjects in lists.
 *
 * @author Anders Engen Olsen
 */
public interface Saveable {

    /**
     * Adding/deleting mediaobject from watchlist
     *
     * @param mo       mediaobject to add
     * @param status   true if add, false if delete
     * @param listener fired when done
     */
    void addToWatchlist(MediaObject mo, boolean status, TmdbListener<String> listener);

    /**
     * Adding/deleting mediaobject from favoritelist
     *
     * @param mo       mediaobject to add
     * @param status   true if add, false if delete
     * @param listener fired when done
     */
    void addToFavoriteList(MediaObject mo, boolean status, TmdbListener<String> listener);

    /**
     * Checking whether a mediaobject is in a users watchlist.
     *
     * @param mo       mediaobject to check
     * @param listener fired when done
     */
    void hasInWatchlist(final MediaObject mo, final TmdbListener<Boolean> listener);

    /**
     * Checking whether a mediaobject is in a users favorite list.
     *
     * @param mo       mediaobject to check
     * @param listener fired when done
     */
    void hasInFavoriteList(final MediaObject mo, final TmdbListener<Boolean> listener);

    /**
     * Fetching users watchlist
     *
     * @param page     pagination
     * @param listener fired when done
     */
    void getWatchlist(int page, final TmdbListener<ArrayList<MediaObject>> listener);

    /**
     * Fetching users favoritelist
     *
     * @param page     pagination
     * @param listener fired when done
     */
    void getFavoriteList(int page, final TmdbListener<ArrayList<MediaObject>> listener);


}
