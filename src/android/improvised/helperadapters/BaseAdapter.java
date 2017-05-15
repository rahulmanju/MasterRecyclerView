package android.improvised.helperadapters;

import android.content.Context;
import android.improvised.widget.GridLayoutManager;
import android.improvised.widget.MasterRecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rahul.p on 11/28/16.
 */

public abstract class BaseAdapter<VH extends BaseAdapter.BaseViewHolder> extends MasterRecyclerView.Adapter<VH> {

    protected AdapterForPosition adapterForPosition;
    private boolean isAttachedToRecycleView = false;
    private MasterRecyclerView attachedRecycleView = null;

    public BaseAdapter() {
        adapterForPosition = new AdapterForPosition(null, 0, -1);
    }

    @Override
    public final VH onCreateViewHolder(ViewGroup parent, int viewType, int position) {
        return onCreateVH(parent, viewType, position);
    }

    @Override
    public final void onBindViewHolder(VH holder, int position) {
        onBindVH(holder, position);
    }

    @Override
    public final int getItemCount() {
        return getCount();
    }

    @Override
    public final int getItemViewType(int position) {
        return getItemLayoutType(position);
    }

    public abstract int getItemLayoutType(int position);

    public abstract VH onCreateVH(ViewGroup parent, int viewType, int position);

    public abstract void onBindVH(BaseViewHolder holder, int position);

    public abstract int getCount();

    public AdapterForPosition getInnermostAdapterAndDecodedPosition(final int position) {
        return adapterForPosition.adapterForPosition(position, this, position);
    }

    public AdapterForPosition getSubAdapterAndDecodedPosition(final int position) {
        return getSubAdapterAndDecodedPosition(position, null, position);
    }

    protected final AdapterForPosition getSubAdapterAndDecodedPosition(
            int position, BaseAdapter adapter, int originalPosition) {
        return adapterForPosition.adapterForPosition(position, adapter, originalPosition);
    }

    protected MasterRecyclerView getAttachedRecycleView() {
        return attachedRecycleView;
    }

    protected boolean isAttachedToRecycleView() {
        return isAttachedToRecycleView;
    }

    @Override
    public void onAttachedToRecyclerView(MasterRecyclerView masterRecyclerView) {
        this.isAttachedToRecycleView = true;
        this.attachedRecycleView = masterRecyclerView;
        onAttached(masterRecyclerView);
        super.onAttachedToRecyclerView(masterRecyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(MasterRecyclerView masterRecyclerView) {
        this.isAttachedToRecycleView = false;
        this.attachedRecycleView = null;
        onDetached(masterRecyclerView);
        super.onDetachedFromRecyclerView(masterRecyclerView);
    }

    protected void onAttached(MasterRecyclerView masterRecyclerView) {

    }

    protected void onDetached(MasterRecyclerView masterRecyclerView) {

    }

    protected void updateData(int oldSize, int newSize) {
        if (newSize == 0) {
            notifyItemRangeRemoved(0, oldSize);
        } else if (newSize == oldSize) {
            notifyItemRangeChanged(0, newSize);
        } else if (oldSize > newSize) {
            notifyItemRangeRemoved(newSize, oldSize - newSize);
            notifyItemRangeChanged(0, newSize);
        } else {
            notifyItemRangeInserted(oldSize, newSize - oldSize);
            notifyItemRangeChanged(0, oldSize);
        }
    }

    public GridLayoutManager.SpanSizeLookup createNewSpanSizeProvider(int maximumSize) {
        return new MSpanSizeLookupImpl(maximumSize);
    }


    public final int getMSpanSize(int position, int maximumSpanSize) {
        return getMSubSpanSize(position, maximumSpanSize);
    }

    protected int getMSubSpanSize(int position, int maxSize) {
        return maxSize;
    }

    public StickyLevel getStickyLevel(int position) {
        return StickyLevel.NO_STICKY;
    }

    public boolean shouldShowSticky(int position) {
        return true;
    }

    public class AdapterForPosition {
        public BaseAdapter adapter;
        public int position;
        public int originalPosition;

        private AdapterForPosition(BaseAdapter adapter, int position, int originalPosition) {
            this.adapter = adapter;
            this.position = position;
            this.originalPosition = originalPosition;
        }

        private AdapterForPosition adapterForPosition(int position, BaseAdapter adapter, int originalPosition) {
            this.adapter = adapter;
            this.position = position;
            this.originalPosition = originalPosition;
            return this;
        }

        public BaseAdapter getAdapter() {
            return adapter;
        }

        public int getPosition() {
            return position;
        }

        public int getOriginalPosition() {
            return originalPosition;
        }
    }

    public enum StickyLevel {
        NO_STICKY,
        LEVEL_1
    }

    private class MSpanSizeLookupImpl extends GridLayoutManager.SpanSizeLookup {

        private final int maximumSpanSize;

        private MSpanSizeLookupImpl(int spanSize) {
            maximumSpanSize = spanSize;
        }

        @Override
        public int getSpanSize(int position) {
            return BaseAdapter.this.getMSpanSize(position,
                    maximumSpanSize);
        }
    }

    public static class BaseViewHolder extends MasterRecyclerView.ViewHolder {
        private Map<Integer, View> viewHashMap;

        private View rootView;

        public BaseViewHolder(Context context, int layout, ViewGroup parent) {
            this(LayoutInflater.from(context).inflate(layout, parent, false));
        }

        public BaseViewHolder(View itemView) {
            super(itemView);
            this.viewHashMap = new HashMap<>();
            this.rootView = itemView;
        }

        public View getItemView() {
            return this.rootView;
        }

        public View getViewById(int id) {
            if (viewHashMap.containsKey(id))
                return viewHashMap.get(id);
            else {
                View view = this.itemView.findViewById(id);
                viewHashMap.put(id, view);
                return view;
            }
        }
    }

}
