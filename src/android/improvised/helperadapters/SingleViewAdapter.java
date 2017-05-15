package android.improvised.helperadapters;

import android.view.ViewGroup;

/**
 * Created by rahul.p on 11/28/16.
 */

public class SingleViewAdapter extends BaseAdapter {

    private int layout;

    public SingleViewAdapter(int layout) {
        this.layout = layout;
    }

    @Override
    public int getItemLayoutType(int position) {
        return layout;
    }

    @Override
    public BaseViewHolder onCreateVH(ViewGroup parent, int viewType, int position) {
        return new BaseViewHolder(parent.getContext(), viewType, parent);
    }

    @Override
    public void onBindVH(BaseViewHolder holder, int postion) {

    }

    @Override
    public int getCount() {
        return 1;
    }

    public void onDataUpdate() {
        int initialSize = getItemCount();
        int currrentSize = getCount();
        if (initialSize <= 0 && currrentSize > 0)
            notifyItemInserted(0);
        else if (initialSize > 0 && currrentSize <= 0)
            notifyItemRemoved(0);
        else if (initialSize == 1)
            notifyItemChanged(0);
    }
}
