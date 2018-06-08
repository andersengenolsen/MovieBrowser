package olsen.anders.movieapp.listener;

/**
 * Interface that has to be implemented for all activites showing the RatingDialogFragment.
 *
 * @author Anders Engen Olsen
 */
public interface RatingDialogListener {
    void onDialogPositiveClick(int rating);
}
