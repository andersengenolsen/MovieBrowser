package olsen.anders.movieapp.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;

import java.util.ArrayList;

import olsen.anders.movieapp.fragment.RecyclerListFragment;
import olsen.anders.movieapp.loader.SearchService;
import olsen.anders.movieapp.loader.TmdbListener;
import olsen.anders.movieapp.model.MediaObject;

/**
 * Activity showing search results, when user searches for movie / tv through Options Menu.
 * <p>
 * The activity contains a RecyclerListFragment with MediaObjects.
 *
 * @author Anders Engen Olsen
 */
public class SearchActivity extends BaseActivity
        implements RecyclerListFragment.OnItemClickedListener {

    private static final String LOG_TAG = SearchActivity.class.getSimpleName();

    /**
     * @see SearchService
     */
    private SearchService searchService;

    /**
     * @see RecyclerListFragment
     */
    private RecyclerListFragment searchResults;

    /**
     * Getting query from calling activity.
     *
     * @see #handleIntent(Intent)
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Up navigation, pil tilbake til forrige Aktivitet.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchService = tmdb.getSearchService();
        searchResults = new RecyclerListFragment();

        handleIntent(getIntent());
    }

    /**
     * This activity is launched with mode singleTop.
     * Avoiding new instance each time a user makes a search.
     * The activity may already exist, so we must implement onNewIntent().
     *
     * @see #handleIntent(Intent)
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        searchService = tmdb.getSearchService();
        searchResults = new RecyclerListFragment();
        handleIntent(intent);
    }

    /**
     * Receiving query from the search view in the calling activity.
     * <p>
     * Updating the content in the list, placing the searchresults in the RecyclerListFragment.
     */
    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            if (query != null) {
                setTitle(query);

                searchService.searchMoviesAndTV(query,
                        new TmdbListener<ArrayList<MediaObject>>() {
                            @Override
                            public void onSuccess(ArrayList<MediaObject> result) {
                                searchResults.setMediaObjects(result);
                                addFragmentToLayout(searchResults, "SearchResults");
                            }

                            @Override
                            public void onError(String result) {
                                showToast(result);
                            }
                        });
            }
        }
    }

    /**
     * Implementation of click event in the RecyclerListFragment.
     * Method called when a mediaobject has been clicked in the list,
     * starting MediaObjectActivity.
     *
     * @param mediaObject item clicked
     * @see MediaObjectActivity
     */
    @Override
    public void onItemClicked(MediaObject mediaObject) {
        Intent intent = new Intent(this, MediaObjectActivity.class);
        intent.putExtra(MEDIA_OBJECT_KEY, mediaObject);

        startActivity(intent);
    }


    /**
     * The activity can be started from several parent activities.
     * Overriding default, returning to previous activity when up pressed.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Closing activity on back pressed.
     */
    @Override
    public void onBackPressed() {
        finish();
    }
}
