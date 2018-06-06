package olsen.anders.movieapp.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import olsen.anders.movieapp.R;
import olsen.anders.movieapp.config.Config;

import static olsen.anders.movieapp.activities.MediaObjectActivity.YOUTUBE_ID;

/**
 * YouTube player fragment.
 * <p>
 * Used to show a YouTube movie.
 * The URL to the movie is received in onCreateView, through a Bundle.
 */
public class YoutubeFragment extends Fragment {

    /**
     * @see Config
     */
    private static final String API_KEY = Config.YOUTUBE_API_KEY;

    /**
     * Youtube VIDEO-ID
     */
    private static String VIDEO_ID;

    /**
     * Obtaining youtube id for a given video.
     * If none is provided, an error message is shown and the fragment is closed.
     *
     * @see #errorAndClose(String)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.youtube_fragment, container, false);


        if (savedInstanceState != null &&
                savedInstanceState.getString(YOUTUBE_ID) != null)
            VIDEO_ID = savedInstanceState.getString(YOUTUBE_ID);
        else if (getArguments() != null && getArguments().getString(YOUTUBE_ID) != null) {
            VIDEO_ID = getArguments().getString(YOUTUBE_ID);
        } else
            errorAndClose(getString(R.string.video_not_found));

        // Initializing YouTubeFragment
        YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.youtube_fragment, youTubePlayerFragment).commit();

        youTubePlayerFragment.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                YouTubePlayer player,
                                                boolean wasRestored) {
                if (!wasRestored) {
                    player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                    player.loadVideo(VIDEO_ID);
                    player.play();
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                YouTubeInitializationResult error) {
                // YouTube error
                errorAndClose(error.toString());
            }
        });

        return rootView;
    }

    /**
     * Displaying error message in a toast, and closing current fragment.
     *
     * @param err error message
     */
    private void errorAndClose(String err) {
        Toast.makeText(getActivity(), err, Toast.LENGTH_SHORT).show();
        getActivity().getSupportFragmentManager().popBackStack();
    }
}
