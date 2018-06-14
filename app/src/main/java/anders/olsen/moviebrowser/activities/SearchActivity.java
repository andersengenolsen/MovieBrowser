package anders.olsen.moviebrowser.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import java.util.ArrayList;

import anders.olsen.moviebrowser.adapter.RecyclerAdapter;
import anders.olsen.moviebrowser.fragment.RecyclerMediaListFragment;
import anders.olsen.moviebrowser.listener.ListFragmentListener;
import anders.olsen.moviebrowser.listener.TmdbListener;
import anders.olsen.moviebrowser.loader.SearchService;
import anders.olsen.moviebrowser.model.MediaObject;

/**
 * Activity showing search results, when user searches for movie / tv through Options Menu.
 * <p>
 * The activity contains a RecyclerMediaListFragment with MediaObjects.
 *
 * @author Anders Engen Olsen
 */
public class SearchActivity extends BaseActivity
        implements ListFragmentListener {

    /**
     * @see SearchService
     */
    private SearchService searchService;

    /**
     * @see RecyclerMediaListFragment
     */
    private RecyclerMediaListFragment searchResults;

    /**
     * The search query
     */
    private String query;

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
        searchResults = new RecyclerMediaListFragment();

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
        searchResults = new RecyclerMediaListFragment();
        handleIntent(intent);
    }

    /**
     * Receiving query from the search view in the calling activity.
     * <p>
     * Updating the content in the list, placing the searchresults in the RecyclerMediaListFragment.
     */
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);

            if (query == null)
                return;

            setTitle(query);
            loadSearchResultsFromApi(query, 1);
        }
    }

    /**
     * Implementation of click event in the RecyclerMediaListFragment.
     * Method called when a mediaobject has been clicked in the list,
     * starting MediaObjectActivity.
     *
     * @see MediaObjectActivity
     */
    @Override
    public void onItemClicked(RecyclerAdapter adapter, int position) {
        MediaObject mediaObject = (MediaObject) adapter.getElement(position);
        startMediaObjectActivity(this, mediaObject);
    }

    /**
     * Implementation of {@link ListFragmentListener}
     *
     * @param page     page to load.
     * @param fragment fragment to load into
     */
    @Override
    public void onScrollEnd(int page, Fragment fragment) {
        loadSearchResultsFromApi(query, ++page);
    }

    /**
     * Loading search results from the API.
     *
     * @param query search query
     * @param page  pagination API
     * @see SearchService#searchMoviesAndTV(String, int, TmdbListener)
     */
    private void loadSearchResultsFromApi(String query, final int page) {
        searchService.searchMoviesAndTV(query, page,
                new TmdbListener<ArrayList<MediaObject>>() {
                    @Override
                    public void onSuccess(ArrayList<MediaObject> result) {
                        if (page == 1) {
                            searchResults.setContent(result);
                            addFragmentToLayout(searchResults, "SearchTag");
                        } else
                            searchResults.appendContent(result);
                    }

                    @Override
                    public void onError(String result) {
                        showToast(result);
                    }
                });
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
