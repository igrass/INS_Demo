package cc.minieye.instoolkit.drag3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.IBinder;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import cc.minieye.instoolkit.R;

public class DragView extends View {
    private static final int DRAG_SCALE = 0;
    private float mAnimationScale;
    private Bitmap mBitmap;
    private LayoutParams mLayoutParams;
    private Paint mPaint;
    private int mRegistrationX;
    private int mRegistrationY;
    private float mScale;
    private WindowManager mWindowManager;

    public DragView(Context context, Bitmap bitmap, int i, int i2, int i3, int i4, int i5, int i6) {
        super(context);
        this.mAnimationScale = 0.9f;
        this.mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Matrix matrix = new Matrix();
        float f = (float) i5;
        f = (0.0f + f) / f;
        this.mScale = f;
        matrix.setScale(f, f);
        this.mBitmap = Bitmap.createBitmap(bitmap, i3, i4, i5, i6, matrix, true);
        this.mRegistrationX = i + 0;
        this.mRegistrationY = i2 + 0;
    }

    void move(int i, int i2) {
        LayoutParams layoutParams = this.mLayoutParams;
        layoutParams.x = i - this.mRegistrationX;
        layoutParams.y = i2 - this.mRegistrationY;
        this.mWindowManager.updateViewLayout(this, layoutParams);
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mBitmap.recycle();
    }

    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStyle(Style.FILL);
        paint.setColor(getResources().getColor(R.color.cell_dragging));
        paint.setAlpha(80);
        canvas.drawRect(0.0f, 0.0f, (float) getWidth(), (float) getHeight(), paint);
        float f = this.mAnimationScale;
        if (f < 0.999f) {
            float height = (float) this.mBitmap.getHeight();
            float width = (float) this.mBitmap.getWidth();
            canvas.translate((width - (width * f)) / 2.0f, (height - (height * f)) / 2.0f);
            canvas.scale(f, f);
        }
        Paint paint2 = new Paint();
        paint2.setAlpha(100);
        canvas.drawBitmap(this.mBitmap, 0.0f, 0.0f, paint2);
    }

    protected void onMeasure(int i, int i2) {
        setMeasuredDimension(this.mBitmap.getWidth(), this.mBitmap.getHeight());
    }

    void remove() {
        this.mWindowManager.removeView(this);
    }

    public void setPaint(Paint paint) {
        this.mPaint = paint;
        invalidate();
    }

    public void setScale(float f) {
        if (f > 1.0f) {
            this.mAnimationScale = 1.0f;
        } else {
            this.mAnimationScale = f;
        }
        invalidate();
    }

    public void show(IBinder iBinder, int i, int i2) {
        LayoutParams layoutParams = new LayoutParams(-2, -2, i - this.mRegistrationX, i2 - this.mRegistrationY, 1002, 768, -3);
        layoutParams.gravity = 51;
        layoutParams.token = iBinder;
        layoutParams.setTitle("DragView");
        this.mLayoutParams = layoutParams;
        this.mWindowManager.addView(this, layoutParams);
    }
}
