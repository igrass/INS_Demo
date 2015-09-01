package cc.minieye.instoolkit.drag3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cc.minieye.instoolkit.R;

public class CustomListViewAdapter extends ArrayAdapter<RowItem> {
    Context context;

    private class ViewHolder {
        ImageView imageView;
        TextView txtDesc;
        TextView txtTitle;

        private ViewHolder() {
        }
    }

    public CustomListViewAdapter(Context context, int i) {
        super(context, i);
        this.context = context;
    }

    public CustomListViewAdapter(Context context, int i, List<RowItem> list) {
        super(context, i, list);
        this.context = context;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        RowItem rowItem = (RowItem) getItem(i);
        LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null) {
            view = layoutInflater.inflate(R.layout.dialog_list_item, null);
            ViewHolder viewHolder2 = new ViewHolder();
            viewHolder2.txtDesc = (TextView) view.findViewById(R.id.desc);
            viewHolder2.txtTitle = (TextView) view.findViewById(R.id.title);
            viewHolder2.imageView = (ImageView) view.findViewById(R.id.icon);
            view.setTag(viewHolder2);
            viewHolder = viewHolder2;
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.txtDesc.setText(rowItem.getDesc());
        viewHolder.txtTitle.setText(rowItem.getTitle());
        viewHolder.imageView.setImageResource(rowItem.getImageId());
        return view;
    }
}
