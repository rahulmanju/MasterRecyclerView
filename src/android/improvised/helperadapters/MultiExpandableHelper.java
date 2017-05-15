package android.improvised.helperadapters;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rahul.p on 12/16/16.
 */

public class MultiExpandableHelper {

    private List<ExpandableAdapter> expandableAdapters;

    private ExpandableAdapter lastExpandableAdapter;

    public MultiExpandableHelper() {
        expandableAdapters = new ArrayList<>();
    }

    public void addExpandableAdapter(final ExpandableAdapter expandableAdapter) {
        expandableAdapters.add(expandableAdapter);
        expandableAdapter.addToggleListener(toggleListener);
    }

    private ExpandableAdapter.IToggleListener toggleListener = new ExpandableAdapter.IToggleListener() {
        @Override
        public void onToggle(ExpandableAdapter adapter, boolean isExpanded) {
            if (isExpanded) {
                if (lastExpandableAdapter != null)
                    lastExpandableAdapter.doToggle();
                lastExpandableAdapter = adapter;
            } else {
                if (lastExpandableAdapter.equals(adapter)) {
                    lastExpandableAdapter = null;
                }
            }
        }
    };

    public void removeExpandableAdapter(ExpandableAdapter expandableAdapter) {
        expandableAdapter.removeToggleListener(toggleListener);
        expandableAdapters.remove(expandableAdapter);
    }
}
