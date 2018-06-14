package anders.olsen.moviebrowser.listener;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import anders.olsen.moviebrowser.adapter.RecyclerAdapter;

/**
 * Custom click listener for RecyclerViews.
 *
 * @author Anders Engen Olsen
 */
public class RecyclerClickListener implements RecyclerView.OnItemTouchListener {

    /**
     * Local interface for the clicklistener
     */
    public interface OnItemClickListener {
        void onItemClick(RecyclerAdapter adapter, int position);

    }

    private OnItemClickListener listener;

    private GestureDetector gestureDetector;

    /**
     * Constructor.
     *
     * @param c        Activity context
     * @param listener Custom listener.
     */
    public RecyclerClickListener(Context c, OnItemClickListener listener) {

        this.listener = listener;
        gestureDetector = new GestureDetector(c, new GestureDetector.SimpleOnGestureListener() {

            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && listener != null && gestureDetector.onTouchEvent(e)) {
            listener.onItemClick((RecyclerAdapter) view.getAdapter(),
                    view.getChildAdapterPosition(childView));
            //listener.onItemClick(childView, view.getChildAdapterPosition(childView));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }
}
