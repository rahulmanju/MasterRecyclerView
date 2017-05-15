package android.improvised.layoutmanagers;

import android.content.Context;
import android.improvised.widget.GridLayoutManager;
import android.util.AttributeSet;

/**
 * Created by rahul.p on 5/15/17.
 */

public class MGridLayoutManager extends GridLayoutManager {

    public MGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public MGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }
}
