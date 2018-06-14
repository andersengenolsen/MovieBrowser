package anders.olsen.moviebrowser.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * PagerAdapter, administrating the tabs in the activity.
 * Note that the tabs are actually fragments.
 * <p>
 * The fragments can be replaced. Simulating adding to "backstack" by using a stack.
 *
 * @author Anders Engen Olsen
 */
public class MainPagerAdapter extends FragmentStatePagerAdapter {

    /**
     * The fragments in the tabs
     */
    private Fragment[] fragments;

    /**
     * Constructor.
     *
     * @param manager   fragmentmanager
     * @param fragments Fragments
     */
    public MainPagerAdapter(FragmentManager manager, Fragment... fragments) {
        super(manager);
        this.fragments = fragments;
    }


    /**
     * Returning a fragment, based on which tab the user clicked.
     *
     * @param position The fragments position
     * @return fragment in given position, null as default.
     */
    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    /**
     * @return number of tabs
     */
    @Override
    public int getCount() {
        return fragments.length;
    }
}