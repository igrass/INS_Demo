package cc.minieye.instoolkit.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import cc.minieye.instoolkit.R;

public class CustomArrayAdapter extends ArrayAdapter<CharSequence> {
    private CharSequence[] objects;
    private boolean[] resEnables;
    private int[] resImages;

    public CustomArrayAdapter(Context context, int i, CharSequence[] charSequenceArr, int[] iArr, boolean[] zArr) {
        super(context, i, charSequenceArr);
        this.objects = charSequenceArr;
        this.resImages = iArr;
        this.resEnables = zArr;
    }

    public boolean areAllItemsEnabled() {
        return false;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.pane_layout, null);
        }
        CharSequence charSequence = this.objects[i];
        int i2 = this.resImages[i];
        if (!(charSequence == null || i2 == 0)) {
            TextView textView = (TextView) view.findViewById(R.id.pane_title);
            ImageView imageView = (ImageView) view.findViewById(R.id.pane_image);
            if (textView != null) {
                textView.setText(charSequence);
            }
            if (imageView != null) {
                imageView.setImageResource(i2);
            }
        }
        return view;
    }

    public boolean isEnabled(int i) {
        return this.resEnables[i];
    }

    public void setEnable(boolean[] zArr) {
        this.resEnables = zArr;
    }
}
