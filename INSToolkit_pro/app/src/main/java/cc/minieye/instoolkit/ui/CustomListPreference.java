package cc.minieye.instoolkit.ui;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;

import cc.minieye.instoolkit.R;

public class CustomListPreference extends ListPreference {
    boolean[] enables;
    private CustomArrayAdapter listAdapter;

    public CustomListPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        boolean[] zArr = new boolean[5];
        zArr[0] = true;
        zArr[1] = true;
        this.enables = zArr;
    }

    protected void onPrepareDialogBuilder(Builder builder) {
        CharSequence[] charSequenceArr = new String[]{"One Pane - Portrait", "One Pane - Landscape", "Two Panes", "Three Panes"};
        this.listAdapter = new CustomArrayAdapter(getContext(), R.layout.pane_layout, charSequenceArr, new int[]{R.drawable.android_portrait, R.drawable.android_landscape, R.drawable.android_landscape_two, R.drawable.android_landscape_three}, this.enables);
        setEntries(charSequenceArr);
        setEntryValues(new String[]{"1", "2", "3", "4"});
        builder.setAdapter(this.listAdapter, this);
        super.onPrepareDialogBuilder(builder);
    }

    public void setEnabledItems(boolean[] zArr) {
        this.enables = zArr;
    }
}
