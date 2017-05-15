package android.improvised.helperadapters;

import android.improvised.widget.MasterRecyclerView.AdapterDataObserver;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rahul.p on 11/28/16.
 */

public class MultiListAdapter extends BaseAdapter {

    private Map<AdapterDataObserver, BaseAdapter> observerHashMap;
    private List<BaseAdapter> adapterList;

    public MultiListAdapter() {
        observerHashMap = new HashMap<>();
        adapterList = new ArrayList<>();
    }

    @Override
    protected int getMSubSpanSize(int position, int maxSize) {
        final AdapterForPosition decodedAdapterForPosition = getSubAdapterAndDecodedPosition(
                position);
        final BaseAdapter adapter = decodedAdapterForPosition.adapter;
        if (adapter != null) {
            final int decodedPosition = decodedAdapterForPosition.position;
            return adapter.getMSpanSize(decodedPosition, maxSize);
        } else {
            return maxSize;
        }
    }

    @Override
    public int getItemLayoutType(int position) {
        final AdapterForPosition decodedAdapterForPosition = getSubAdapterAndDecodedPosition(
                position);
        final BaseAdapter adapter = decodedAdapterForPosition.adapter;
        if (adapter != null) {
            final int decodedPosition = decodedAdapterForPosition.position;
            return adapter.getItemViewType(decodedPosition);
        } else {
            return 0;
        }
    }

    @Override
    public StickyLevel getStickyLevel(int position) {
        final AdapterForPosition decodedAdapterForPosition = getSubAdapterAndDecodedPosition(
                position);
        final BaseAdapter adapter = decodedAdapterForPosition.adapter;
        if (adapter != null) {
            final int decodedPosition = decodedAdapterForPosition.position;
            return adapter.getStickyLevel(decodedPosition);
        } else {
            return super.getStickyLevel(position);
        }
    }

    @Override
    public boolean shouldShowSticky(int position) {
        final AdapterForPosition decodedAdapterForPosition = getSubAdapterAndDecodedPosition(
                position);
        final BaseAdapter adapter = decodedAdapterForPosition.adapter;
        if (adapter != null) {
            final int decodedPosition = decodedAdapterForPosition.position;
            return adapter.shouldShowSticky(decodedPosition);
        } else {
            return super.shouldShowSticky(position);
        }
    }

    @Override
    public BaseViewHolder onCreateVH(ViewGroup parent, int viewType, int position) {
        BaseViewHolder vh = null;
        final AdapterForPosition decodedAdapterForPosition = getSubAdapterAndDecodedPosition(
                position);
        final BaseAdapter adapter = decodedAdapterForPosition.adapter;
        final int decodedPosition = decodedAdapterForPosition.position;
        vh = adapter.onCreateViewHolder(parent, viewType, decodedPosition);
        return vh;
    }

    @Override
    public void onBindVH(BaseViewHolder holder, int position) {
        final AdapterForPosition adapterAndDecodedPosition = getSubAdapterAndDecodedPosition(
                position);
        final BaseAdapter adapter = adapterAndDecodedPosition.adapter;
        if (adapter != null) {
            final int decodedPosition = adapterAndDecodedPosition.position;
            adapter.onBindViewHolder(holder, decodedPosition);
        }
    }

    @Override
    public int getCount() {
        int totalCount = 0;
        for (BaseAdapter baseAdapter : adapterList) {
            totalCount = totalCount + baseAdapter.getItemCount();
        }
        return totalCount;
    }

    private AdapterDataObserver getNewObserver() {
        final BaseAdapter currentAdapter = this;
        return new AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                adapterForPosition.adapter = null;
                currentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                // super.onItemRangeChanged(positionStart, itemCount);
                adapterForPosition.adapter = null;
                currentAdapter.notifyItemRangeChanged(
                        positionStart + getTopCount(), itemCount);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                adapterForPosition.adapter = null;
                currentAdapter.notifyItemRangeInserted(
                        positionStart + getTopCount(), itemCount);

            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                adapterForPosition.adapter = null;
                currentAdapter.notifyItemRangeRemoved(
                        positionStart + getTopCount(), itemCount);

            }

            private int getTopCount() {
                final BaseAdapter selectedAdapter = observerHashMap
                        .get(this);
                int count = 0;
                for (BaseAdapter adapter : adapterList) {
                    if (selectedAdapter == adapter) {
                        break;
                    }
                    count += adapter.getItemCount();
                }

                return count;
            }
        };
    }

    public void addAdapter(BaseAdapter adapter) {
        addAdapter(adapter, adapterList.size());
    }

    public void addAdapter(BaseAdapter adapter, int index) {
        index = index >= 0 ? index : 0;
        this.adapterList.add(index, adapter);
        AdapterDataObserver newObserver = getNewObserver();
        observerHashMap.put(newObserver, adapter);
        adapter.registerAdapterDataObserver(newObserver);
        if (this.isAttachedToRecycleView()) {
            adapter.onAttachedToRecyclerView(getAttachedRecycleView());
        }
        final int itemCount = adapter.getItemCount();
        if (itemCount > 0) {
            if (index == this.adapterList.size() - 1) {
                notifyItemRangeInserted(getItemCount() - itemCount, itemCount);
            } else {
                int topCount = 0;
                for (BaseAdapter topAdapter : this.adapterList) {
                    if (topAdapter == adapter) {
                        break;
                    }
                    topCount += topAdapter.getItemCount();
                }
                notifyItemRangeInserted(topCount, itemCount);
            }
        }
    }

    public void removeAdapter(int index) {
        BaseAdapter remove = this.adapterList.remove(index);
        for (Map.Entry<AdapterDataObserver, BaseAdapter> entry : observerHashMap
                .entrySet()) {
            if (entry.getValue() == remove) {
                remove.unregisterAdapterDataObserver(entry.getKey());
                remove.onDetached(getAttachedRecycleView());
                observerHashMap.remove(entry.getKey());
                int top = 0;
                for (int i = 0; i < index; i++) {
                    BaseAdapter adp = this.adapterList.get(i);
                    if (i + 1 == index) {
                        top = top > 0 ? top-- : 0;
                        break;
                    } else {
                        top += adp.getItemCount();
                    }
                }
                notifyItemRangeRemoved(top, remove.getItemCount());
                break;
            }
        }
    }

    public void removeAdapter(BaseAdapter adapter) {
        int index = this.adapterList.indexOf(adapter);
        if (index != -1) {
            removeAdapter(index);
        }
    }

    @Override
    public AdapterForPosition getSubAdapterAndDecodedPosition(int position) {
        int count = 0;
        for (BaseAdapter adapter : adapterList) {
            if (position < (count + adapter.getItemCount())) {
                return super.getSubAdapterAndDecodedPosition(position - count,
                        adapter, position);
            }
            count += adapter.getItemCount();
        }
        return super.getSubAdapterAndDecodedPosition(0, null, position);
    }

    @Override
    public AdapterForPosition
    getInnermostAdapterAndDecodedPosition(final int position) {
        int count = 0;
        for (BaseAdapter adapter : adapterList) {
            if (position < (count + adapter.getItemCount())) {
                return adapter.getInnermostAdapterAndDecodedPosition(
                        position - count);
            }
            count += adapter.getItemCount();
        }
        return super.getSubAdapterAndDecodedPosition(0, null, position);
    }

}

