package cc.minieye.instoolkit.ui;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.preference.ListPreference;
import android.util.AttributeSet;

public final class MultiSelectListPreference extends ListPreference {
    private static final String SEPARATOR = ",";
    private boolean[] mClickedDialogEntryIndices;

    public MultiSelectListPreference(Context context) {
        super(context);
    }

    public MultiSelectListPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public static String[] parseStoredValue(CharSequence charSequence) {
        return ("".equals(charSequence) || charSequence == null) ? null : ((String) charSequence).split(SEPARATOR);
    }

    private void restoreCheckedEntries() {
        CharSequence[] entryValues = getEntryValues();
        String[] parseStoredValue = parseStoredValue(getValue());
        if (parseStoredValue != null) {
            for (String trim : parseStoredValue) {
                String trim2 = trim.trim();
                for (int i = 0; i < entryValues.length; i++) {
                    if (entryValues[i].equals(trim2)) {
                        this.mClickedDialogEntryIndices[i] = true;
                        break;
                    }
                }
            }
        }
    }

    protected void onDialogClosed(boolean z) {
        CharSequence[] entryValues = getEntryValues();
        if (z && entryValues != null) {
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < entryValues.length; i++) {
                if (this.mClickedDialogEntryIndices[i]) {
                    stringBuffer.append(entryValues[i]).append(SEPARATOR);
                }
            }
            if (callChangeListener(stringBuffer)) {
                String stringBuffer2 = stringBuffer.toString();
                if (stringBuffer2.length() > 0) {
                    stringBuffer2 = stringBuffer2.substring(0, stringBuffer2.length() - SEPARATOR.length());
                }
                setValue(stringBuffer2);
            }
        }
    }

    protected void onPrepareDialogBuilder(Builder builder) {
        CharSequence[] entries = getEntries();
        CharSequence[] entryValues = getEntryValues();
        if (entries == null || entryValues == null || entries.length != entryValues.length) {
            throw new IllegalStateException("ListPreference requires an entries array and an entryValues array which are both the same length");
        }
        this.mClickedDialogEntryIndices = new boolean[entryValues.length];
        restoreCheckedEntries();
        builder.setMultiChoiceItems(entries, this.mClickedDialogEntryIndices, new OnMultiChoiceClickListener() {
            public void onClick(DialogInterface dialogInterface, int i, boolean z) {
                MultiSelectListPreference.this.mClickedDialogEntryIndices[i] = z;
            }
        });
    }
}
