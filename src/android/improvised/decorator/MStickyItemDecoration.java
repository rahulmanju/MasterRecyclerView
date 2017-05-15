package android.improvised.decorator;

import android.graphics.Canvas;
import android.improvised.helperadapters.BaseAdapter;
import android.improvised.widget.MasterRecyclerView;
import android.support.v4.view.ViewCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rahul.p on 5/15/17.
 */

public class MStickyItemDecoration extends MasterRecyclerView.ItemDecoration {

    private BaseAdapter mAdapter;
    private Map<Integer, BaseAdapter.BaseViewHolder> mStickyCache;
    private List<Integer> mStickyPositions;
    private View currentStickyItem = null;
    private MasterRecyclerView mParent;
    private boolean isDrawing = false;

    public MStickyItemDecoration(BaseAdapter adapter, MasterRecyclerView parent) {
        this.mParent = parent;
        this.mAdapter = adapter;
        this.mStickyCache = new HashMap<>();
        this.mStickyPositions = new ArrayList<>();
        parent.addOnItemTouchListener(new MasterRecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(MasterRecyclerView rv, MotionEvent e) {
                View stickyView = findHeaderViewUnder(e.getX(), e.getY());
                boolean shouldHandle = (stickyView != null && isDrawing);
                return shouldHandle;
            }

            @Override
            public void onTouchEvent(MasterRecyclerView rv, MotionEvent e) {
                if (e.getAction() != MotionEvent.ACTION_UP) {
                    return;
                }
                View stickyView = findHeaderViewUnder(e.getX(), e.getY());
                boolean shouldHandle = (stickyView != null && isDrawing);
                if (shouldHandle)
                    stickyView.performClick();
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    private View findHeaderViewUnder(float x, float y) {
        for (BaseAdapter.BaseViewHolder holder : mStickyCache.values()) {
            final View child = holder.itemView;
            final float translationX = ViewCompat.getTranslationX(child);
            final float translationY = ViewCompat.getTranslationY(child);

            if (x >= child.getLeft() + translationX &&
                    x <= child.getRight() + translationX &&
                    y >= child.getTop() + translationY &&
                    y <= child.getBottom() + translationY) {
                return child;
            }
        }
        return null;
    }


    @Override
    public void onDrawOver(Canvas canvas, MasterRecyclerView parent, MasterRecyclerView.State state) {
        final int count = parent.getChildCount();
        int previousStickyPosition = -1;
        isDrawing = false;
        for (int layoutPos = 0; layoutPos < count; layoutPos++) {
            final View child = parent.getChildAt(layoutPos);
            final int adapterPos = parent.getChildAdapterPosition(child);
            if (adapterPos != MasterRecyclerView.NO_POSITION && mAdapter.shouldShowSticky(adapterPos)) {
                if (mAdapter.getStickyLevel(adapterPos) != BaseAdapter.StickyLevel.NO_STICKY) {
                    pushStickyPosition(adapterPos);
                }
                int stickyPosition = getStickyPositionForItem(adapterPos);
                //Log.i("Sticky", "onDrawOver lastHeaderPosition :" + stickyPosition + " previousStickyPosition :" + previousStickyPosition + " stickyPosition " + stickyPosition + " adapterPos " + adapterPos);
                if (previousStickyPosition != stickyPosition) {
                    previousStickyPosition = stickyPosition;
                    if (stickyPosition >= 0) {
                        currentStickyItem = getItemViewHolder(parent, stickyPosition).itemView;
                    }
                    if (currentStickyItem != null && stickyPosition >= 0 && stickyPosition <= adapterPos) {
                        //Log.i("Sticky", "onDrawOver  stickyPosition " + stickyPosition + " adapterPos " + adapterPos);
                        final int left = getStickyViewLeft(parent, child, currentStickyItem);
                        final int top = getStickyViewTop(parent, child, currentStickyItem);
                        drawStickyItem(canvas, currentStickyItem, left, top);
                    }
                }

            }
        }

    }

    private void pushStickyPosition(int newStickyPosition) {
        int minimumIndex = mStickyPositions.size();
        int size = mStickyPositions.size();
        for (int index = size - 1; index >= 0; index--) {
            if (mStickyPositions.get(index) >= newStickyPosition) {
                minimumIndex = index;
            } else {
                break;
            }
        }
        for (int i = 0; i < (size - minimumIndex); i++) {
            mStickyPositions.remove(minimumIndex);
        }
        mStickyPositions.add(newStickyPosition);
    }

    private int getStickyPositionForItem(int adapterPosition) {
        int size = mStickyPositions.size();
        for (int index = size - 1; index >= 0; index--) {
            if (mStickyPositions.get(index) <= adapterPosition) {
                return mStickyPositions.get(index);
            }
        }
        return -1;
    }

    private BaseAdapter.BaseViewHolder getItemViewHolder(MasterRecyclerView parent, int position) {
        if (mStickyCache.containsKey(position)) {
            return mStickyCache.get(position);
        } else {
            int viewType = mAdapter.getItemViewType(position);
            BaseAdapter.BaseViewHolder holder = mAdapter.onCreateViewHolder(parent, viewType, position);
            mAdapter.onBindViewHolder(holder, position);
            setStickyView(parent, holder.getItemView());
            mStickyCache.put(position, holder);
            return holder;
        }

    }

    private void drawStickyItem(Canvas canvas, View stickyItem, int left, int top) {
        if (stickyItem != null && top == 0) {
            isDrawing = true;
            canvas.save();
            canvas.translate(left, top);
            stickyItem.setTranslationX(left);
            stickyItem.setTranslationY(top);
            stickyItem.draw(canvas);
            canvas.restore();
        }
    }

    private void setStickyView(MasterRecyclerView parent, View sticky) {

        int widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getMeasuredWidth(), View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(parent.getMeasuredHeight(), View.MeasureSpec.UNSPECIFIED);

        int childWidth = ViewGroup.getChildMeasureSpec(widthSpec,
                parent.getPaddingLeft() + parent.getPaddingRight(), sticky.getLayoutParams().width);
        int childHeight = ViewGroup.getChildMeasureSpec(heightSpec,
                parent.getPaddingTop() + parent.getPaddingBottom(), sticky.getLayoutParams().height);

        sticky.measure(childWidth, childHeight);
        sticky.layout(0, 0, sticky.getMeasuredWidth(), sticky.getMeasuredHeight());
    }

    protected int getStickyViewTop(MasterRecyclerView parent, View child, View stickyItem) {
        int headerHeight = getHeaderHeightForLayout(stickyItem);
        int top = ((int) child.getY()) - headerHeight;
        if (top < child.getHeight()) {
            top = 0;
        }
        top = Math.max(0, top);
        return top;
    }

    protected int getStickyViewLeft(MasterRecyclerView parent, View child, View stickyItem) {
        return stickyItem.getLeft();
    }

    protected int getHeaderHeightForLayout(View header) {
        return 0;
    }
}

