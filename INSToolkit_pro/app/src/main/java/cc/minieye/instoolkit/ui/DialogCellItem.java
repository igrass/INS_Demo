package cc.minieye.instoolkit.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import cc.minieye.instoolkit.R;
import cc.minieye.instoolkit.drag3.CellDataFormater;
import cc.minieye.instoolkit.drag3.CellDataFormater.Format;
import cc.minieye.instoolkit.drag3.CustomListViewAdapter;
import cc.minieye.instoolkit.ui.CustomDialog.Builder;

public final class DialogCellItem extends Builder {
    private ListView ListCellItems;
    CustomDialog customDialog;
    Context mContext;
    private final ReadyListener readyListener;

    public interface ReadyListener {
        void ready(boolean z, int i);
    }

    public DialogCellItem(Context context, ReadyListener readyListener) {
        super(context);
        this.mContext = context;
        this.readyListener = readyListener;
    }

    public void setDialog() {
        View inflate = LayoutInflater.from(this.mContext).inflate(R.layout.dialog_cellitems, null);
        setContentView(inflate);
        setTitle("Data To Add");
        this.customDialog = create();
        this.ListCellItems = (ListView) inflate.findViewById(R.id.ListCellItems);
        CustomListViewAdapter customListViewAdapter = new CustomListViewAdapter(this.mContext, R.layout.dialog_list_item);
        for (Format rowItem : CellDataFormater.dCellTextInfo) {
            customListViewAdapter.add(rowItem.getRowItem());
        }
        this.ListCellItems.setAdapter(customListViewAdapter);
        this.ListCellItems.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                DialogCellItem.this.readyListener.ready(true, CellDataFormater.dCellTextInfo[i].type);
                DialogCellItem.this.customDialog.dismiss();
            }
        });
        setNegativeButton("Cancel", new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
    }

    public void show() {
        this.customDialog.show();
    }
}
