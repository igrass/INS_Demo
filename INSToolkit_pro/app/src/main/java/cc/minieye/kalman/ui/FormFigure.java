package cc.minieye.kalman.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.List;
import java.util.Locale;

import cc.minieye.instoolkit.R;
import cc.minieye.kalman.maths.CDataArray;

public class FormFigure extends Activity implements OnTouchListener {
    private static boolean DEBUG = false;
    private static final int INVALID_POINTER_ID = -1;
    private static int NONE;
    private static int ZOOM;
    private String directory;
    private float downXValue;
    private String filename;
    private int mActivePointerId;
    boolean mExternalStorageAvailable;
    boolean mExternalStorageWriteable;
    private ViewFlipper mFlipper;
    private Animation mIn1;
    private Animation mIn2;
    private float mLastTouchX;
    private float mLastTouchY;
    private Animation mOut1;
    private Animation mOut2;
    private PlotCanvas mPlotCanvas1;
    private PlotCanvas mPlotCanvas2;
    private PlotCanvas mPlotCanvas3;
    private PlotCanvas mPlotCanvas4;
    private PlotCanvas mPlotCanvas5;
    private PlotCanvas mPlotCanvas6;
    private boolean matrix_changed;
    private int max_view;
    DisplayMetrics metrics;
    private int mode;
    private String nameApp;
    private CDataArray vals_est_acc;
    private CDataArray vals_est_angles;
    private CDataArray vals_est_gyro;
    private CDataArray vals_est_vel;
    private CDataArray vals_raw_acc;
    private CDataArray vals_raw_gyro;
    private CDataArray vals_raw_mag;
    private int viewid;

    class PlotCanvas extends View {
        private float CanvasHeight;
        private float CanvasWidth;
        int[] ColorList;
        CDataArray dataArray;
        boolean display_debug_info;
        boolean display_meters_units;
        private float leftMargin;
        private float mScreenHeight;
        private float mScreenWidth;
        private Paint paint_axes;
        private Paint paint_data;
        private Paint paint_grid;
        private Paint paint_labels;
        private float scalex;
        private float scaley;
        private float topMargin;
        private int xTicks;
        private float xmax;
        private float xmin;
        private int yTicks;
        private float ymax;
        private float ymin;

        public PlotCanvas(Context context) {
            super(context);
            this.scalex = 1.0f;
            this.scaley = 1.0f;
            this.xmax = 100.0f;
            this.xmin = 0.0f;
            this.ymax = 10.0f;
            this.ymin = -10.0f;
            this.yTicks = 6;
            this.xTicks = 6;
            this.paint_axes = new Paint();
            this.paint_labels = new Paint();
            this.paint_grid = new Paint();
            this.paint_data = new Paint();
            this.display_debug_info = false;
            this.display_meters_units = true;
            this.ColorList = new int[]{-16776961, -16711936, -65536, -16711681, -256, -65281, -7829368};
            this.paint_axes.setColor(-1);
            this.paint_axes.setTextSize(20.0f);
            this.paint_axes.setAntiAlias(true);
            this.paint_axes.setStyle(Style.STROKE);
            this.paint_axes.setStrokeWidth(1.0f);
            this.paint_grid.setColor(-3355444);
            this.paint_grid.setAntiAlias(true);
            this.paint_grid.setStyle(Style.STROKE);
            this.paint_grid.setStrokeWidth(0.5f);
            this.paint_grid.setPathEffect(new DashPathEffect(new float[]{10.0f, 20.0f}, 0.0f));
            this.paint_labels.setColor(-1);
            this.paint_labels.setTextSize(20.0f);
            this.paint_labels.setAntiAlias(true);
            this.paint_labels.setStyle(Style.STROKE);
            this.paint_labels.setTypeface(Typeface.MONOSPACE);
            this.paint_data.setColor(-65536);
            this.paint_data.setTextSize(20.0f);
            this.paint_data.setAntiAlias(true);
            this.paint_data.setStyle(Style.STROKE);
            this.paint_data.setStrokeWidth(2.0f);
            this.leftMargin = (float) getLeftMargin();
            this.topMargin = 0.1f * this.mScreenHeight;
        }

        private void HorizontalAxis(Canvas canvas, float f, float f2, float f3, float f4, float f5, float f6, int i) {
            Rect rect = new Rect();
            int i2 = i <= 1 ? 2 : i;
            if (i2 >= 20) {
                i2 = 20;
            }
            float f7 = f4 / ((float) i2);
            float[] fArr = new float[4];
            float[] fArr2 = new float[4];
            float f8 = (f6 - f5) / ((float) i2);
            float f9 = f3 > f ? f : f3;
            for (int i3 = 0; i3 <= i2; i3++) {
                fArr[0] = (((float) i3) * f7) + f2;
                fArr[1] = f9 - 10.0f;
                fArr[2] = fArr[0];
                fArr[3] = f9;
                float f10 = (((float) i3) * f8) + f5;
                String format = String.format(Locale.ENGLISH, "%.1f", new Object[]{Float.valueOf(f10)});
                this.paint_labels.getTextBounds(format, 0, format.length(), rect);
                canvas.drawLines(fArr, this.paint_axes);
                printText(canvas, format, fArr[0] - (((float) rect.width()) / 2.0f), (float) (((double) fArr[3]) + (1.2d * ((double) rect.height()))));
                fArr[1] = f - this.CanvasHeight;
                fArr[3] = f;
                canvas.drawLines(fArr, this.paint_grid);
                if (i3 < i2) {
                    fArr2[0] = ((0.5f + ((float) i3)) * f7) + f2;
                    fArr2[1] = f9 - 5.0f;
                    fArr2[2] = fArr2[0];
                    fArr2[3] = f9;
                    canvas.drawLines(fArr2, this.paint_axes);
                }
            }
            canvas.drawLine(f2, f9, f2 + f4, f9, this.paint_axes);
        }

        private void PlotData(Canvas canvas, float f, float f2, CDataArray cDataArray) {
            int dimension = cDataArray.dimension();
            int size = cDataArray.size() + FormFigure.INVALID_POINTER_ID;
            float[] fArr = new float[(size * 4)];
            for (int i = 0; i < dimension; i++) {
                this.paint_data.setColor(this.ColorList[i - ((i / 7) * 7)]);
                for (int i2 = 0; i2 < size; i2++) {
                    fArr[i2 * 4] = (this.scalex * cDataArray.getRollingData(i2, 0)) + f;
                    fArr[(i2 * 4) + 1] = f2 - (this.scaley * cDataArray.getRollingData(i2, i + 1));
                    fArr[(i2 * 4) + 2] = (this.scalex * cDataArray.getRollingData(i2 + 1, 0)) + f;
                    fArr[(i2 * 4) + 3] = f2 - (this.scaley * cDataArray.getRollingData(i2 + 1, i + 1));
                }
                canvas.drawLines(fArr, this.paint_data);
            }
        }

        private void VerticalAxis(Canvas canvas, float f, float f2, float f3, float f4, float f5, float f6, int i) {
            Rect rect = new Rect();
            this.paint_labels.getTextBounds("+000.0", 0, "+000.0".length(), rect);
            int i2 = i <= 1 ? 2 : i;
            if (i2 >= 20) {
                i2 = 20;
            }
            float f7 = f4 / ((float) i2);
            float[] fArr = new float[4];
            float[] fArr2 = new float[4];
            float f8 = (f6 - f5) / ((float) i2);
            float f9 = f2 < f ? f : f2;
            for (int i3 = 0; i3 <= i2; i3++) {
                fArr[0] = f9;
                fArr[1] = f3 - (((float) i3) * f7);
                fArr[2] = 10.0f + f9;
                fArr[3] = fArr[1];
                String format = Math.abs((((float) i3) * f8) + f5) < 1.0f ? String.format(Locale.ENGLISH, "%.1g", new Object[]{Float.valueOf((((float) i3) * f8) + f5)}) : String.format(Locale.ENGLISH, "%.1f", new Object[]{Float.valueOf((((float) i3) * f8) + f5)});
                this.paint_labels.getTextBounds(format, 0, format.length(), rect);
                canvas.drawLines(fArr, this.paint_axes);
                printText(canvas, format, (float) (((double) fArr[0]) - (1.1d * ((double) rect.width()))), fArr[1] + (((float) rect.height()) / 2.0f));
                fArr[0] = f;
                fArr[2] = this.CanvasWidth + f;
                canvas.drawLines(fArr, this.paint_grid);
                if (i3 < i2) {
                    fArr2[0] = f9;
                    fArr2[1] = f3 - ((0.5f + ((float) i3)) * f7);
                    fArr2[2] = 5.0f + f9;
                    fArr2[3] = fArr2[1];
                    canvas.drawLines(fArr2, this.paint_axes);
                }
            }
            canvas.drawLine(f9, f3, f9, f3 - f4, this.paint_axes);
        }

        private int getLeftMargin() {
            Rect rect = new Rect();
            this.paint_labels.getTextBounds("+000.0", 0, "+000.0".length(), rect);
            return rect.width();
        }

        private void getWindowDimensions(Window window) {
            Rect rect = new Rect();
            window.getDecorView().getWindowVisibleDisplayFrame(rect);
            setSize((float) (FormFigure.this.metrics.heightPixels - rect.top), (float) FormFigure.this.metrics.widthPixels);
        }

        private void printLegend(Canvas canvas, float f, float f2, List<String> list) {
            int size = list.size();
            int color = this.paint_labels.getColor();
            for (int i = 0; i < size; i++) {
                if (i < this.ColorList.length) {
                    this.paint_labels.setColor(this.ColorList[i]);
                }
                canvas.drawText((String) list.get(i), (this.CanvasWidth + f) - 100.0f, f2 + (((float) (i + 1)) * this.paint_data.getTextSize()), this.paint_labels);
            }
            this.paint_labels.setColor(color);
        }

        private void printText(Canvas canvas, String str, float f, float f2) {
            canvas.drawText(str, f, f2, this.paint_labels);
        }

        private void printTitle(Canvas canvas, float f, float f2, String str) {
            Rect rect = new Rect();
            this.paint_labels.getTextBounds(str, 0, str.length(), rect);
            float width = (this.mScreenWidth - ((float) rect.width())) / 2.0f;
            RectF rectF = new RectF();
            rectF.left = width - 5.0f;
            rectF.bottom = f2 + 5.0f;
            rectF.right = (((float) rect.width()) + width) + 5.0f;
            rectF.top = f2 - ((float) rect.height());
            canvas.drawRoundRect(rectF, 5.0f, 5.0f, this.paint_axes);
            canvas.drawText(str, width, f2, this.paint_labels);
        }

        private void scaleData() {
            this.xmin = this.dataArray.MinX();
            this.xmax = (float) Math.ceil((double) (1.01f * this.dataArray.MaxX()));
            this.ymin = Math.min(this.dataArray.MinY(0), Math.min(this.dataArray.MinY(1), this.dataArray.MinY(2))) * 1.2f;
            this.ymax = Math.max(this.dataArray.MaxY(0), Math.max(this.dataArray.MaxY(1), this.dataArray.MaxY(2))) * 1.2f;
        }

        private void setSize(float f, float f2) {
            this.mScreenHeight = f;
            this.mScreenWidth = f2;
            this.leftMargin = (float) getLeftMargin();
            this.topMargin = 0.1f * this.mScreenHeight;
        }

        public void ChangeScale(float f, float f2, float f3, float f4) {
            this.xmax += f3 / this.scalex;
            this.xmin -= f3 / this.scalex;
            this.ymax += f4 / this.scaley;
            this.ymin -= f4 / this.scaley;
            invalidate();
        }

        public void DrawCanvas(Canvas canvas) {
            this.CanvasWidth = this.mScreenWidth - (2.0f * this.leftMargin);
            this.CanvasHeight = this.mScreenHeight - (2.0f * this.topMargin);
            if (((double) Math.abs(this.xmin - this.xmax)) < 0.1d) {
                this.xmax = 0.1f + this.xmin;
            }
            if (((double) Math.abs(this.ymin - this.ymax)) < 0.01d) {
                this.ymax = 0.01f + this.ymin;
            }
            this.scalex = this.CanvasWidth / (this.xmax - this.xmin);
            this.scaley = this.CanvasHeight / (this.ymax - this.ymin);
            float f = this.leftMargin;
            float f2 = this.mScreenHeight - this.topMargin;
            float f3 = f - (this.xmin * this.scalex);
            float f4 = f2 + (this.ymin * this.scaley);
            if (this.dataArray.size() > 0) {
                VerticalAxis(canvas, f, f3, f2, this.CanvasHeight, this.ymin, this.ymax, this.yTicks);
                HorizontalAxis(canvas, f2, f, f4, this.CanvasWidth, this.xmin, this.xmax, this.xTicks);
                PlotData(canvas, f3, f4, this.dataArray);
            }
            float f5 = 0.1f * this.mScreenHeight;
            printLegend(canvas, f, f5, this.dataArray.legends);
            printTitle(canvas, f, f5, this.dataArray.title);
        }

        protected void onDraw(Canvas canvas) {
            DrawCanvas(canvas);
        }

        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            getWindowDimensions(FormFigure.this.getWindow());
        }

        public void setData(CDataArray cDataArray) {
            this.dataArray = cDataArray;
            scaleData();
        }
    }

    static {
        NONE = 0;
        ZOOM = 1;
        DEBUG = false;
    }

    public FormFigure() {
        this.max_view = 0;
        this.metrics = new DisplayMetrics();
        this.mExternalStorageAvailable = false;
        this.mExternalStorageWriteable = false;
        this.matrix_changed = false;
        this.mActivePointerId = INVALID_POINTER_ID;
        this.viewid = 0;
        this.mode = NONE;
    }

    private void FlipperNext() {
        this.mFlipper.setInAnimation(this.mIn2);
        this.mFlipper.setOutAnimation(this.mOut2);
        this.mFlipper.showNext();
        this.viewid++;
    }

    private void FlipperPrevious() {
        this.mFlipper.setInAnimation(this.mIn1);
        this.mFlipper.setOutAnimation(this.mOut1);
        this.mFlipper.showPrevious();
        this.viewid += INVALID_POINTER_ID;
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindowManager().getDefaultDisplay().getMetrics(this.metrics);
        this.mPlotCanvas1 = new PlotCanvas(this);
        this.mPlotCanvas2 = new PlotCanvas(this);
        this.mPlotCanvas3 = new PlotCanvas(this);
        this.mPlotCanvas4 = new PlotCanvas(this);
        this.mPlotCanvas5 = new PlotCanvas(this);
        this.mPlotCanvas6 = new PlotCanvas(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.vals_est_angles = (CDataArray) extras.getParcelable("attest");
            this.mPlotCanvas1.setData(this.vals_est_angles);
            this.vals_raw_gyro = (CDataArray) extras.getParcelable("gyro");
            this.mPlotCanvas2.setData(this.vals_raw_gyro);
            this.vals_raw_acc = (CDataArray) extras.getParcelable("acc");
            this.mPlotCanvas3.setData(this.vals_raw_acc);
            this.vals_raw_mag = (CDataArray) extras.getParcelable("mag");
            this.vals_est_gyro = (CDataArray) extras.getParcelable("gyroest");
            this.mPlotCanvas5.setData(this.vals_est_gyro);
            this.vals_est_vel = (CDataArray) extras.getParcelable("vel");
            if (this.vals_est_vel != null) {
                this.mPlotCanvas4.setData(this.vals_est_vel);
            }
            this.vals_est_acc = (CDataArray) extras.getParcelable("accest");
            if (this.vals_est_acc != null) {
                this.mPlotCanvas6.setData(this.vals_est_acc);
            }
        }
        this.mOut1 = AnimationUtils.loadAnimation(this, 17432579);
        this.mIn1 = AnimationUtils.loadAnimation(this, 17432578);
        this.mOut2 = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
        this.mIn2 = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        this.mFlipper = new ViewFlipper(this);
        this.max_view = INVALID_POINTER_ID;
        if (this.vals_est_angles.size() > 0) {
            this.mFlipper.addView(this.mPlotCanvas1);
            this.max_view++;
        }
        if (this.vals_est_gyro.size() > 0) {
            this.mFlipper.addView(this.mPlotCanvas5);
            this.max_view++;
        }
        if (this.vals_raw_gyro.size() > 0) {
            this.mFlipper.addView(this.mPlotCanvas2);
            this.max_view++;
        }
        if (this.vals_est_acc != null && this.vals_est_acc.size() > 0) {
            this.mFlipper.addView(this.mPlotCanvas6);
            this.max_view++;
        }
        if (this.vals_raw_acc.size() > 0) {
            this.mFlipper.addView(this.mPlotCanvas3);
            this.max_view++;
        }
        if (this.vals_est_vel != null && this.vals_est_vel.size() > 0) {
            this.mFlipper.addView(this.mPlotCanvas4);
            this.max_view++;
        }
        this.mFlipper.setOnTouchListener(this);
        this.nameApp = (String) getResources().getText(R.string.app_name);
        this.directory = this.nameApp;
        this.filename = this.nameApp;
        String externalStorageState = Environment.getExternalStorageState();
        if ("mounted".equals(externalStorageState)) {
            this.mExternalStorageWriteable = true;
            this.mExternalStorageAvailable = true;
        } else if ("mounted_ro".equals(externalStorageState)) {
            this.mExternalStorageAvailable = true;
            this.mExternalStorageWriteable = false;
        } else {
            this.mExternalStorageWriteable = false;
            this.mExternalStorageAvailable = false;
        }
        setContentView(this.mFlipper);
        ActionBar actionBar = getActionBar();
        actionBar.show();
        actionBar.setDisplayShowTitleEnabled(false);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_data, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.menu_graph_previous) {
            FlipperPrevious();
            return true;
        } else if (menuItem.getItemId() != R.id.menu_graph_next) {
            return super.onOptionsItemSelected(menuItem);
        } else {
            FlipperNext();
            return true;
        }
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        if (this.viewid > 0) {
            menu.findItem(R.id.menu_graph_previous).setEnabled(true);
        } else {
            menu.findItem(R.id.menu_graph_previous).setEnabled(false);
        }
        if (this.viewid < this.max_view) {
            menu.findItem(R.id.menu_graph_next).setEnabled(true);
        } else {
            menu.findItem(R.id.menu_graph_next).setEnabled(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    protected void onResume() {
        super.onResume();
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        this.matrix_changed = false;
        float x;
        float y;
        float x2;
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN /*0*/:
                if (DEBUG) {
                    Log.d("AndroidPicRotate", "ACTION_DOWN");
                    Log.d("AndroidPicRotate", "X = " + motionEvent.getX() + "\tY = " + motionEvent.getY());
                }
                this.downXValue = motionEvent.getX();
                x = motionEvent.getX();
                y = motionEvent.getY();
                this.mLastTouchX = x;
                this.mLastTouchY = y;
                this.mActivePointerId = motionEvent.getPointerId(0);
                break;
            case MotionEvent.ACTION_UP /*1*/:
                if (DEBUG) {
                    Log.d("AndroidPicRotate", "ACTION_UP");
                }
                x2 = motionEvent.getX();
                if (((this.downXValue < x2 ? 1 : 0) & (this.viewid > 0 ? 1 : 0)) != 0) {
                    FlipperPrevious();
                }
                if (((this.downXValue > x2 ? 1 : 0) & (this.viewid < this.max_view ? 1 : 0)) != 0) {
                    FlipperNext();
                }
                this.mActivePointerId = INVALID_POINTER_ID;
                break;
            case MotionEvent.ACTION_MOVE /*2*/:
                if (this.mode == ZOOM) {
                    int findPointerIndex = motionEvent.findPointerIndex(this.mActivePointerId);
                    y = motionEvent.getX(findPointerIndex);
                    x = motionEvent.getY(findPointerIndex);
                    x2 = y - this.mLastTouchX;
                    float f = x - this.mLastTouchY;
                    this.mLastTouchX = y;
                    this.mLastTouchY = x;
                    this.mPlotCanvas1.ChangeScale(this.mLastTouchX, this.mLastTouchY, x2, f);
                    this.mPlotCanvas2.ChangeScale(this.mLastTouchX, this.mLastTouchY, x2, f);
                    this.mPlotCanvas3.ChangeScale(this.mLastTouchX, this.mLastTouchY, x2, f);
                    break;
                }
                break;
            case MotionEvent.ACTION_CANCEL /*3*/:
                this.mActivePointerId = INVALID_POINTER_ID;
                break;
            case MotionEvent.ACTION_POINTER_DOWN /*5*/:
                if (DEBUG) {
                    Log.d("AndroidPicRotate", "ACTION_POINTER_DOWN");
                    break;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP /*6*/:
                if (DEBUG) {
                    Log.d("AndroidPicRotate", "ACTION_POINTER_UP");
                    break;
                }
                break;
        }
        if (this.matrix_changed) {
            Toast.makeText(getApplicationContext(), "Zoom", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}
