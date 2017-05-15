package android.improvised.layoutmanagers;

import android.content.Context;
import android.improvised.widget.LinearLayoutManager;
import android.util.AttributeSet;

/**
 * Created by rahul.p on 2/17/17.
 */

public class MLinearLayoutManager extends LinearLayoutManager {

    public MLinearLayoutManager(Context context) {
        super(context);
    }

    public MLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public MLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }
}

