package anders.olsen.moviebrowser.listener;

/**
 * Interface for callback from a
 * {@link anders.olsen.moviebrowser.fragment.MediaObjectInformationFragment}
 */
public interface MediaObjectFragmentListener extends RatingDialogListener {

    void startTrailer();

    void addFavorite();

    void addWatchlist();

    void showRatingDialog();

}
