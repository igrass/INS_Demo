package cc.minieye.instoolkit.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import cc.minieye.instoolkit.R;
import cc.minieye.instoolkit.ui.CustomDialog.Builder;

public class FormMenu extends Builder {
    public CustomDialog dialog;
    private int idSelected;
    private Context mContext;
    GridView menu;
    private Integer[] menu_icon;
    private int[] menu_id;
    private String[] menu_text;
    private final ReadyListener readyListener;

    public class MenuItem extends ArrayAdapter<Object> {
        public MenuItem(Context context, int i, String[] strArr) {
            super(context, i, strArr);
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            View inflate = ((LayoutInflater) FormMenu.this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.menu_item, viewGroup, false);
            TextView textView = (TextView) inflate.findViewById(R.id.text);
            textView.setText(FormMenu.this.menu_text[i]);
            textView.setCompoundDrawablesWithIntrinsicBounds(0, FormMenu.this.menu_icon[i].intValue(), 0, 0);
            return inflate;
        }
    }

    public interface ReadyListener {
        void ready(boolean z, int i);
    }

    public FormMenu(Activity activity, Context context, ReadyListener readyListener) {
        super(context);
        this.idSelected = -1;
        this.menu_text = new String[]{"HUD / Artificial Horizon", "GroundTrack", "Compass", "Data Board", "Plot", "Calibrate", "Settings", "About"};
        this.menu_icon = new Integer[]{Integer.valueOf(17301567), Integer.valueOf(R.drawable.menuicon_globe), Integer.valueOf(17301561), Integer.valueOf(R.drawable.menuicon_activity), Integer.valueOf(R.drawable.menuicon_report), Integer.valueOf(R.drawable.menuicon_synchronize), Integer.valueOf(R.drawable.menuicon_settings), Integer.valueOf(R.drawable.menuicon_information)};
        this.menu_id = new int[]{R.id.lookup_background, R.id.lookup_groundtrack, R.id.lookup_compass, R.id.lookup_infoboard, R.id.lookup_plot, R.id.lookup_calibrate, R.id.lookup_settings, R.id.lookup_about};
        this.mContext = context;
        this.readyListener = readyListener;
        View inflate = LayoutInflater.from(context).inflate(R.layout.menu, null);
        setContentView(inflate);
        this.menu = (GridView) inflate.findViewById(R.id.Menu);
        this.menu.setAdapter(new MenuItem(context, R.layout.menu_item, this.menu_text));
        this.menu.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                FormMenu.this.idSelected = FormMenu.this.menu_id[i];
                FormMenu.this.readyListener.ready(true, FormMenu.this.idSelected);
                FormMenu.this.dialog.dismiss();
            }
        });
        this.dialog = create();
        this.dialog.setOnCancelListener(new OnCancelListener() {
            public void onCancel(DialogInterface dialogInterface) {
                FormMenu.this.readyListener.ready(false, -1);
            }
        });
    }

    public final void dismiss() {
        this.dialog.dismiss();
    }

    public final void show() {
        this.dialog.show();
    }

    public String toString() {
        return String.format("%d", new Object[]{Integer.valueOf(this.idSelected)});
    }
}
