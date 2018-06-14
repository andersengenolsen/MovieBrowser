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

import anders.olsen.moviebrowser.R;
import anders.olsen.moviebrowser.listener.LoginDialogListener;


/**
 * Dialog shown when a user wants to login to TMDB.
 * Dialog has layout dialog_login.xml
 * Activities must implement LoginDialogListener
 *
 * @author Anders Engen Olsen
 */

public class LoginDialogFragment extends DialogFragment {

    private LoginDialogListener loginDialogListener;

    /**
     * Activites must implement LoginDialogListener
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            loginDialogListener = (LoginDialogListener) getActivity();
        } catch (ClassCastException err) {
            throw new ClassCastException(getActivity().getClass().getSimpleName()
                    + " must implement LoginDialogListener!");
        }
    }

    /**
     * Setting layout and clicklistener
     *
     * @return the dialog
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View layout = inflater.inflate(R.layout.dialog_login, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        Button loginBtn = layout.findViewById(R.id.login_button);
        Button cancelBtn = layout.findViewById(R.id.cancel_button);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loginDialogListener.onDialogPositiveClick();
                dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        builder.setView(layout);

        return builder.create();
    }
}
