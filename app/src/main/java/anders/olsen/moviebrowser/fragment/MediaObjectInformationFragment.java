package anders.olsen.moviebrowser.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import anders.olsen.moviebrowser.R;
import anders.olsen.moviebrowser.listener.MediaObjectFragmentListener;
import anders.olsen.moviebrowser.model.MediaObject;

public class MediaObjectInformationFragment extends Fragment
        implements View.OnClickListener {

    /**
     * MediaObject to show
     */
    private MediaObject mediaObject;

    /**
     * ImageButton watchlist
     */
    private ImageButton watchlistBtn;
    /**
     * ImageButton favoritelist
     */
    private ImageButton favoriteBtn;

    /**
     * {@link MediaObjectFragmentListener}
     */
    private MediaObjectFragmentListener listener;

    /**
     * Flag for in watchlist or not
     */
    private boolean inWatchlist;

    /**
     * Flag for in favorite list or not
     */
    private boolean inFavList;

    /**
     * Activites must implement {@link MediaObjectFragmentListener}
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            listener = (MediaObjectFragmentListener) getActivity();
        } catch (ClassCastException err) {
            throw new ClassCastException(getActivity().getClass().getSimpleName()
                    + " must implement MediaObjectFragmentListener!");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_mediaobject, container, false);

        initViews(rootView, mediaObject);

        return rootView;
    }

    /**
     * Setting current mediaobject.
     *
     * @param mediaObject current
     */
    public void setMediaObject(MediaObject mediaObject) {
        this.mediaObject = mediaObject;
    }

    /**
     * Setting up views
     * Image is loaded with Picasso.
     *
     * @param mediaObject MediaObject
     * @see #setText(TextView, String)
     */
    private void initViews(View view, MediaObject mediaObject) {
        watchlistBtn = view.findViewById(R.id.add_watchlist);
        favoriteBtn = view.findViewById(R.id.add_favorite);

        // Setting color based on in list or not
        alternateButtonColor(watchlistBtn, inWatchlist);
        alternateButtonColor(favoriteBtn, inFavList);

        watchlistBtn.setOnClickListener(this);
        favoriteBtn.setOnClickListener(this);
        view.findViewById(R.id.watch_trailer).setOnClickListener(this);
        view.findViewById(R.id.add_rating).setOnClickListener(this);


        ImageView logoImg = view.findViewById(R.id.media_image);
        ImageView bgImg = view.findViewById(R.id.background);

        RequestCreator creator = Picasso.with(getActivity()).load(mediaObject.getImagePath())
                .error(R.drawable.ic_movie_black)
                .placeholder(R.drawable.ic_movie_black);

        creator.into(logoImg);
        creator.into(bgImg);

        TextView genreTxt = view.findViewById(R.id.genre);
        TextView releaseTxt = view.findViewById(R.id.release);
        TextView ratingTxt = view.findViewById(R.id.rating);
        TextView languageTxt = view.findViewById(R.id.language);
        TextView handlingTxt = view.findViewById(R.id.handling);

        setText(genreTxt, mediaObject.getGenre());
        setText(releaseTxt, mediaObject.getReleaseDate());
        setText(ratingTxt, mediaObject.getRating());
        setText(languageTxt, mediaObject.getLanguage());
        setText(handlingTxt, mediaObject.getHandling());
    }

    /**
     * On click events for views.
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_favorite:
                listener.addFavorite();
                break;
            case R.id.add_watchlist:
                listener.addWatchlist();
                break;
            case R.id.watch_trailer:
                listener.startTrailer();
                break;
            case R.id.add_rating:
                listener.showRatingDialog();
                break;
        }
    }

    /**
     * Alternating color of watchlist button
     *
     * @param inWatchlist
     */
    public void alternateWatchlistBtnColor(boolean inWatchlist) {
        this.inWatchlist = inWatchlist;
        if (watchlistBtn != null)
            alternateButtonColor(watchlistBtn, inWatchlist);
    }

    /**
     * Alternating color of favorite button
     *
     * @param inFavList
     */
    public void alternateFavoritelistBtnColor(boolean inFavList) {
        this.inFavList = inFavList;
        if (favoriteBtn != null)
            alternateButtonColor(favoriteBtn, inFavList);
    }

    /**
     * Changing the color of the the button.
     * DKGRAY indicating the mediaobject is in a list.
     * BLUE indication the mediaobject is not in a list.
     * <p>
     * This method should only be called prior to a successful call to TmdbManager,
     * as it does not check against the users actual list. It simply switches between two
     * drawable styles. If the method is called without a successful call to TmdbManager,
     * it will change the color, regardless of the actual result of the list-operations.
     *
     * @param button Button which will change background
     */
    private void alternateButtonColor(ImageButton button, boolean inList) {
        if (inList)
            button.setBackgroundColor(getActivity().getColor(R.color.colorChecked));
        else
            button.setBackgroundColor(getActivity().getColor(R.color.colorPrimary));
    }

    /**
     * Checking whether a String is null or empty. String containing only whitespace is considered
     * empty.
     * If empty or null, string with "unknown" will be placed in TextView.
     *
     * @param tv  TextView
     * @param txt Text to place in textview
     */
    private void setText(TextView tv, String txt) {
        if (txt == null || txt.trim().length() == 0)
            tv.setText(R.string.unknown);
        tv.setText(txt);
    }
}
