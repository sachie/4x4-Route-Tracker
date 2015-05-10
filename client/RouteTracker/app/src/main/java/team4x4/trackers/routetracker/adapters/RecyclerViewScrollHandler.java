package team4x4.trackers.routetracker.adapters;

import android.support.v7.widget.RecyclerView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Custom scroll handler for recycler views.
 */
public class RecyclerViewScrollHandler extends RecyclerView.OnScrollListener {

    /**
     * Called when the scroll state is changed.
     *
     * @param recyclerView Recycler view that is being scrolled.
     * @param newState     New scroll state.
     */
    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        switch (newState) {
            case RecyclerView.SCROLL_STATE_IDLE:
                ImageLoader.getInstance().resume();
                break;
            case RecyclerView.SCROLL_STATE_DRAGGING:
                ImageLoader.getInstance().pause();
                break;
            case RecyclerView.SCROLL_STATE_SETTLING:
                ImageLoader.getInstance().pause();
                break;
        }
    }
}