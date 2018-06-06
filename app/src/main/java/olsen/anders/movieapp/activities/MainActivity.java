package olsen.anders.movieapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import olsen.anders.movieapp.R;
import olsen.anders.movieapp.adapter.RecyclerAdapter;
import olsen.anders.movieapp.adapter.RecyclerImageAdapter;
import olsen.anders.movieapp.listener.RecyclerClickListener;
import olsen.anders.movieapp.loader.MovieService;
import olsen.anders.movieapp.loader.TmdbListener;
import olsen.anders.movieapp.loader.TvService;
import olsen.anders.movieapp.model.MediaObject;


/**
 * Launcher activity for the application.
 * The activity contains several Buttons, which starts other activities.
 * In addition to this, the activity also contains 2 horizontal recyclerviews with mediaobjects.
 *
 * @author Anders Engen Olsen
 */
public class MainActivity extends BaseActivity
        implements View.OnClickListener, RecyclerClickListener.OnItemClickListener {

    /**
     * ArrayList with movies for recyclerviews.
     */
    private ArrayList<MediaObject> moviesList = new ArrayList<>();
    /**
     * ArrayList with tv shows for recyclerviews.
     */
    private ArrayList<MediaObject> tvList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_main);

        setUpRecyclers();
        setUpButtons();
    }

    /**
     * Implementation of RecyclerClickListener.OnItemClickListener.
     * Starting a MediaObjectActivity.
     */
    @Override
    public void onItemClick(RecyclerAdapter adapter, int position) {
        startMediaObjectActivity((MediaObject) adapter.getElement(position));
    }

    /**
     * Click on views.
     */
    @Override
    public void onClick(View view) {

        Intent intent = null;

        switch (view.getId()) {
            case R.id.favorite_button:
                intent = new Intent(this, ListActivity.class);
                intent.putExtra(MEDIA_LIST_KEY, MEDIA_LIST_FAVORITES);
                break;
            case R.id.watchlist_button:
                intent = new Intent(this, ListActivity.class);
                intent.putExtra(MEDIA_LIST_KEY, MEDIA_LIST_WATCHLIST);
                break;
            case R.id.movies_button:
                intent = new Intent(this, MediaCollectionActivity.class);
                intent.putExtra(MEDIA_TYPE_KEY, MEDIA_TYPE_MOVIE);
                break;
            case R.id.tv_button:
                intent = new Intent(this, MediaCollectionActivity.class);
                intent.putExtra(MEDIA_TYPE_KEY, MEDIA_TYPE_TV);
                break;
        }

        if (intent != null)
            startActivity(intent);
    }

    /**
     * Initiating RecyclerViews.
     *
     * @see RecyclerImageAdapter
     * @see #setRecyclerListener(RecyclerView)
     * @see MovieService#getPopular(TmdbListener)
     * @see TvService#getPopular(TmdbListener)
     */
    private void setUpRecyclers() {

        final RecyclerImageAdapter movieAdapter = new RecyclerImageAdapter(this, moviesList);
        final RecyclerImageAdapter tvAdapter = new RecyclerImageAdapter(this, tvList);

        final RecyclerView moviesRecyclerView = findViewById(R.id.recycler_main_movie);
        final RecyclerView tvRecyclerView = findViewById(R.id.recycler_main_tv);

        moviesRecyclerView.setAdapter(movieAdapter);
        tvRecyclerView.setAdapter(tvAdapter);

        moviesRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        tvRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));

        movieService.getPopular(
                new TmdbListener<ArrayList<MediaObject>>() {
                    @Override
                    public void onSuccess(ArrayList<MediaObject> result) {
                        moviesList = result;
                        movieAdapter.setContent(result);
                        movieAdapter.notifyDataSetChanged();
                        setRecyclerListener(moviesRecyclerView);
                    }

                    @Override
                    public void onError(String result) {
                        showToast(result);
                    }
                }
        );

        tvService.getPopular(
                new TmdbListener<ArrayList<MediaObject>>() {
                    @Override
                    public void onSuccess(ArrayList<MediaObject> result) {
                        tvList = result;
                        tvAdapter.setContent(result);
                        tvAdapter.notifyDataSetChanged();
                        setRecyclerListener(tvRecyclerView);
                    }

                    @Override
                    public void onError(String result) {
                        showToast(result);
                    }
                }
        );
    }

    /**
     * Setting ClickListener for a RecyclerView.
     *
     * @param view RecyclerView
     * @see RecyclerClickListener
     * @see #onItemClick(RecyclerAdapter, int)
     */
    private void setRecyclerListener(RecyclerView view) {
        view.addOnItemTouchListener(new RecyclerClickListener(this, this));
    }

    /**
     * Initiating Buttons, setting clicklistener.
     */
    private void setUpButtons() {
        Button favoriteBtn = findViewById(R.id.favorite_button);
        Button watchlistBtn = findViewById(R.id.watchlist_button);
        Button moviesBtn = findViewById(R.id.movies_button);
        Button tvBtn = findViewById(R.id.tv_button);

        favoriteBtn.setOnClickListener(this);
        watchlistBtn.setOnClickListener(this);
        moviesBtn.setOnClickListener(this);
        tvBtn.setOnClickListener(this);
    }

    /**
     * Starting MediaObjectActivity, sending the clicked MediaObject with the intent.
     *
     * @param mediaObject Mediaobject to show in MediaObjectActivity
     * @see MediaObject
     * @see MediaObjectActivity
     */
    private void startMediaObjectActivity(MediaObject mediaObject) {
        Intent intent = new Intent(this, MediaObjectActivity.class);
        intent.putExtra(MEDIA_OBJECT_KEY, mediaObject);

        startActivity(intent);
    }
}
