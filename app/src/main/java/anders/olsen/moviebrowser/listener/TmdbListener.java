package anders.olsen.moviebrowser.listener;

import anders.olsen.moviebrowser.loader.TmdbManager;

/**
 * Generic interface, fired when data is downloaded from TMDB-API.
 *
 * @author Anders Engen Olsen
 * @see TmdbManager
 */
public interface TmdbListener<AnyType> {

    /**
     * Returning result from Tmdb.
     *
     * @param result Tmdb result
     */
    void onSuccess(AnyType result);

    /**
     * Returning result from Tmdb.
     *
     * @param result Tmdb result
     */
    void onError(String result);
}
