package anders.olsen.moviebrowser.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;

import anders.olsen.moviebrowser.R;
import anders.olsen.moviebrowser.listener.RatingDialogListener;


/**
 * Dialog shown when a user wants to login to TMDB.
 * Dialog has layout dialog_login.xml
 * Activities must implement DialogListener
 *
 * @author Anders Engen Olsen
 */

public class RatingDialogFragment extends DialogFragment {

    /**
     * Interface, callback to activities
     *
     * @see RatingDialogListener
     */
    private RatingDialogListener ratingDialogListener;

    /**
     * The rating bar
     */
    private RatingBar ratingBar;

    /**
     * Activites must implement RatingDialogListener
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            ratingDialogListener = (RatingDialogListener) getActivity();
        } catch (ClassCastException err) {
            throw new ClassCastException(getActivity().getClass().getSimpleName()
                    + " must implement RatingDialogListener!");
        }
    }

    /**
     * Setting layout and clicklistener.
     * <p>
     * Be aware that the rating is multiplied by 2. Only 5 stars, TMDB accepts rating 0-10.
     *
     * @return the dialog
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View layout = inflater.inflate(R.layout.dialog_rating, null);

        ratingBar = layout.findViewById(R.id.ratingbar);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        Button addRatingBtn = layout.findViewById(R.id.add_rating_btn);
        addRatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int rating = (int) ratingBar.getRating() * 2;
                if (rating == 0)
                    rating = 1;
                ratingDialogListener.onDialogPositiveClick(rating);
                dismiss();
            }
        });

        builder.setView(layout);

        return builder.create();
    }
}
