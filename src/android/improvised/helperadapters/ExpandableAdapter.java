package android.improvised.helperadapters;

import android.improvised.widget.MasterRecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rahul.p on 12/15/16.
 */

public class ExpandableAdapter extends BaseAdapter {

    private boolean isExpanded = false;

    private ParentAdapter parentAdapter;

    private BaseAdapter childrenAdapter;

    private MasterRecyclerView.AdapterDataObserver parentDataObserver;

    private MasterRecyclerView.AdapterDataObserver childrenDataObserver;

    private List<IToggleListener> toggleListeners;

    public ExpandableAdapter() {
        toggleListeners = new ArrayList<>();
        parentDataObserver = getParentDataObserver();
        childrenDataObserver = getChildrenDataObserver();
    }

    public void setParentAdapter(final ParentAdapter parentAdapter) {
        this.parentAdapter = parentAdapter;
        this.parentAdapter.registerAdapterDataObserver(parentDataObserver);
    }

    public void setChildrenAdapter(BaseAdapter childrenAdapter) {
        this.childrenAdapter = childrenAdapter;
        this.childrenAdapter.registerAdapterDataObserver(childrenDataObserver);
    }

    @Override
    public int getItemLayoutType(int position) {
        if (position == 0 && parentAdapter.getItemCount() > 0) {
            return parentAdapter.getItemViewType(0);
        } else {
            if (parentAdapter.getItemCount() > 0)
                return childrenAdapter.getItemViewType(position - 1);
            else
                return childrenAdapter.getItemViewType(position);
        }
    }

    @Override
    public BaseViewHolder onCreateVH(ViewGroup parent, int viewType, int position) {
        if (position == 0 && parentAdapter.getItemCount() > 0) {
            return parentAdapter.onCreateViewHolder(parent, viewType, position);
        } else {
            if (parentAdapter.getItemCount() > 0)
                return childrenAdapter.onCreateViewHolder(parent, viewType, position - 1);
            else
                return childrenAdapter.onCreateViewHolder(parent, viewType, position);
        }
    }

    @Override
    public StickyLevel getStickyLevel(int position) {
        if (position == 0 && parentAdapter.getItemCount() > 0) {
            return parentAdapter.getStickyLevel(0);
        } else {
            if (parentAdapter.getItemCount() > 0)
                return childrenAdapter.getStickyLevel(position - 1);
            else
                return childrenAdapter.getStickyLevel(position);
        }
    }

    @Override
    public boolean shouldShowSticky(int position) {
        if (position == 0 && parentAdapter.getItemCount() > 0) {
            return parentAdapter.shouldShowSticky(0);
        } else {
            if (parentAdapter.getItemCount() > 0)
                return childrenAdapter.shouldShowSticky(position - 1);
            else
                return childrenAdapter.shouldShowSticky(position);
        }
    }

    @Override
    public void onBindVH(BaseViewHolder holder, int position) {

        if (position == 0 && parentAdapter.getItemCount() > 0) {
            parentAdapter.setParentClickListener(new ParentAdapter.onParentClickListener() {
                @Override
                public void onParentItemClick(BaseViewHolder holder) {
                    doToggle();
                }
            });
            parentAdapter.onParentBindVH(holder, position, isExpanded);
        } else {
            if (parentAdapter.getItemCount() > 0)
                childrenAdapter.onBindVH(holder, position - 1);
            else
                childrenAdapter.onBindVH(holder, position);
        }
    }

    public void doToggle() {
        onToggleUpdate();
        broadCastToggle();
    }

    private void onToggleUpdate() {
        isExpanded = !isExpanded;
        updateAdapters();
    }

    public void setExpanded(boolean isExpanded) {
        this.isExpanded = isExpanded;
        updateAdapters();
        broadCastToggle();
    }

    private void updateAdapters() {
        if (childrenAdapter.getItemCount() > 0) {
            if (isExpanded) {
                notifyItemRangeInserted(1, childrenAdapter.getItemCount());
            } else {
                notifyItemRangeRemoved(1, childrenAdapter.getItemCount());
            }
        }
        parentAdapter.onDataUpdate();
    }

    private void broadCastToggle() {
        for (IToggleListener toggleListener : toggleListeners) {
            toggleListener.onToggle(this, isExpanded);
        }
    }

    @Override
    public int getCount() {
        int parentCount = parentAdapter.getItemCount();
        int childernCount = 0;
        if (isExpanded)
            childernCount = childrenAdapter.getItemCount();
        else
            childernCount = 0;
        return parentCount + childernCount;
    }

    private MasterRecyclerView.AdapterDataObserver getParentDataObserver() {
        return new MasterRecyclerView.AdapterDataObserver() {

            @Override
            public void onChanged() {
                super.onChanged();
                notifyItemRangeChanged(0, parentAdapter.getItemCount());
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                notifyItemRangeChanged(positionStart, itemCount);
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
                super.onItemRangeChanged(positionStart, itemCount, payload);
                notifyItemRangeChanged(positionStart, itemCount, payload);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                notifyItemRangeInserted(positionStart, itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                notifyItemRangeRemoved(positionStart, itemCount);
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            }
        };
    }

    private MasterRecyclerView.AdapterDataObserver getChildrenDataObserver() {
        return new MasterRecyclerView.AdapterDataObserver() {

            @Override
            public void onChanged() {
                super.onChanged();
                notifyItemRangeChanged(parentAdapter.getItemCount(), childrenAdapter.getItemCount());
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                notifyItemRangeChanged(parentAdapter.getItemCount() + positionStart, itemCount);
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
                super.onItemRangeChanged(positionStart, itemCount, payload);
                notifyItemRangeChanged(parentAdapter.getItemCount() + positionStart, itemCount, payload);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                notifyItemRangeInserted(parentAdapter.getItemCount() + positionStart, itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                notifyItemRangeRemoved(parentAdapter.getItemCount() + positionStart, itemCount);
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            }
        };
    }


    public static class ParentAdapter extends SingleViewAdapter {

        public onParentClickListener parentClickListener;

        public ParentAdapter(int layout) {
            super(layout);
        }

        @Override
        public final void onBindVH(BaseViewHolder holder, int postion) {
            super.onBindVH(holder, postion);
        }

        public void onParentBindVH(final BaseViewHolder holder, int position, boolean isExpanded) {
            holder.getItemView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (parentClickListener != null) {
                        parentClickListener.onParentItemClick(holder);
                    }
                }
            });
        }

        public void setParentClickListener(onParentClickListener parentClickListener) {
            this.parentClickListener = parentClickListener;
        }

        public static interface onParentClickListener {
            public void onParentItemClick(BaseViewHolder holder);
        }
    }

    public void addToggleListener(IToggleListener toggleListener) {
        this.toggleListeners.add(toggleListener);
    }

    public void removeToggleListener(IToggleListener toggleListener) {
        this.toggleListeners.remove(toggleListener);
    }

    public static interface IToggleListener {
        public void onToggle(ExpandableAdapter adapter, boolean isExpanded);
    }
}
