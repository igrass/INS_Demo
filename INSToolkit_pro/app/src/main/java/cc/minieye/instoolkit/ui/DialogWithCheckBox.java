package cc.minieye.instoolkit.ui;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import cc.minieye.instoolkit.R;

public class DialogWithCheckBox {
    public static CheckBox dontShowAgain;

    /* renamed from: cc.minieye.instoolkit.ui.DialogWithCheckBox.1 */
    static class AnonymousClass1 implements OnClickListener {
        private final /* synthetic */ String val$PREFS_NAME;
        private final /* synthetic */ Context val$context;

        AnonymousClass1(Context context, String str) {
            this.val$context = context;
            this.val$PREFS_NAME = str;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            String str = "NOT checked";
            if (DialogWithCheckBox.dontShowAgain.isChecked()) {
                str = "checked";
            }
            Editor edit = this.val$context.getSharedPreferences(this.val$PREFS_NAME, 0).edit();
            edit.putString("skipMessage", str);
            edit.commit();
        }
    }

    /* renamed from: cc.minieye.instoolkit.ui.DialogWithCheckBox.2 */
    static class AnonymousClass2 implements OnClickListener {
        private final /* synthetic */ String val$PREFS_NAME;
        private final /* synthetic */ Context val$context;

        AnonymousClass2(Context context, String str) {
            this.val$context = context;
            this.val$PREFS_NAME = str;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            String str = "NOT checked";
            if (DialogWithCheckBox.dontShowAgain.isChecked()) {
                str = "checked";
            }
            Editor edit = this.val$context.getSharedPreferences(this.val$PREFS_NAME, 0).edit();
            edit.putString("skipMessage", str);
            edit.commit();
        }
    }

    public static void show(Context context, String str, String str2) {
        Builder builder = new Builder(context);
        View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_checkbox, null);
        dontShowAgain = (CheckBox) inflate.findViewById(R.id.skip);
        builder.setView(inflate);
        builder.setTitle("Attention");
        builder.setMessage(Html.fromHtml(str));
        builder.setPositiveButton("Ok", new AnonymousClass1(context, str2));
        builder.setNegativeButton("Cancel", new AnonymousClass2(context, str2));
        if (!context.getSharedPreferences(str2, 0).getString("skipMessage", "NOT checked").equalsIgnoreCase("checked")) {
            builder.show();
        }
    }
}
