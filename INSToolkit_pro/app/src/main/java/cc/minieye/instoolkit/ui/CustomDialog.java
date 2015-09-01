package cc.minieye.instoolkit.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import cc.minieye.instoolkit.R;

public class CustomDialog extends Dialog {

    public static class Builder {
        private View contentView;
        private Context context;
        private String message;
        private OnClickListener negativeButtonClickListener;
        private String negativeButtonText;
        private OnClickListener positiveButtonClickListener;
        private String positiveButtonText;
        private String title;

        /* renamed from: cc.minieye.instoolkit.ui.CustomDialog.Builder.1 */
        class AnonymousClass1 implements View.OnClickListener {
            private final /* synthetic */ CustomDialog val$dialog;

            AnonymousClass1(CustomDialog customDialog) {
                this.val$dialog = customDialog;
            }

            public void onClick(View view) {
                Builder.this.positiveButtonClickListener.onClick(this.val$dialog, -1);
            }
        }

        /* renamed from: cc.minieye.instoolkit.ui.CustomDialog.Builder.2 */
        class AnonymousClass2 implements View.OnClickListener {
            private final /* synthetic */ CustomDialog val$dialog;

            AnonymousClass2(CustomDialog customDialog) {
                this.val$dialog = customDialog;
            }

            public void onClick(View view) {
                Builder.this.negativeButtonClickListener.onClick(this.val$dialog, -2);
            }
        }

        public Builder(Context context) {
            this.context = context;
        }

        public CustomDialog create() {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            CustomDialog customDialog = new CustomDialog(this.context, R.style.Dialog);
            View inflate = layoutInflater.inflate(R.layout.dialog, null);
            customDialog.addContentView(inflate, new LayoutParams(-1, -1));
            if (this.title == null) {
                inflate.findViewById(R.id.title).setVisibility(View.GONE);
            } else {
                ((TextView) inflate.findViewById(R.id.title)).setText(this.title);
                inflate.findViewById(R.id.LayoutHeader).setVisibility(View.VISIBLE);
            }
            if (this.positiveButtonText != null) {
                ((Button) inflate.findViewById(R.id.positiveButton)).setText(this.positiveButtonText);
                if (this.positiveButtonClickListener != null) {
                    ((Button) inflate.findViewById(R.id.positiveButton)).setOnClickListener(new AnonymousClass1(customDialog));
                }
            } else {
                inflate.findViewById(R.id.positiveButton).setVisibility(View.GONE);
            }
            if (this.negativeButtonText != null) {
                ((Button) inflate.findViewById(R.id.negativeButton)).setText(this.negativeButtonText);
                if (this.negativeButtonClickListener != null) {
                    ((Button) inflate.findViewById(R.id.negativeButton)).setOnClickListener(new AnonymousClass2(customDialog));
                }
            } else {
                inflate.findViewById(R.id.negativeButton).setVisibility(View.GONE);
            }
            if (this.message != null) {
                ((TextView) inflate.findViewById(R.id.message)).setText(this.message);
            } else if (this.contentView != null) {
                ((LinearLayout) inflate.findViewById(R.id.content)).removeAllViews();
                ((LinearLayout) inflate.findViewById(R.id.content)).addView(this.contentView, new WindowManager.LayoutParams(-1, -2));
            }
            customDialog.setContentView(inflate);
            return customDialog;
        }

        public Builder setContentView(View view) {
            this.contentView = view;
            return this;
        }

        public Builder setMessage(int i) {
            this.message = (String) this.context.getText(i);
            return this;
        }

        public Builder setMessage(String str) {
            this.message = str;
            return this;
        }

        public Builder setNegativeButton(int i, OnClickListener onClickListener) {
            this.negativeButtonText = (String) this.context.getText(i);
            this.negativeButtonClickListener = onClickListener;
            return this;
        }

        public Builder setNegativeButton(String str, OnClickListener onClickListener) {
            this.negativeButtonText = str;
            this.negativeButtonClickListener = onClickListener;
            return this;
        }

        public Builder setPositiveButton(int i, OnClickListener onClickListener) {
            this.positiveButtonText = (String) this.context.getText(i);
            this.positiveButtonClickListener = onClickListener;
            return this;
        }

        public Builder setPositiveButton(String str, OnClickListener onClickListener) {
            this.positiveButtonText = str;
            this.positiveButtonClickListener = onClickListener;
            return this;
        }

        public Builder setTitle(int i) {
            this.title = (String) this.context.getText(i);
            return this;
        }

        public Builder setTitle(String str) {
            this.title = str;
            return this;
        }
    }

    public CustomDialog(Context context) {
        super(context);
    }

    public CustomDialog(Context context, int i) {
        super(context, i);
    }
}
