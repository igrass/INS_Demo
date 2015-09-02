package cc.minieye.instoolkit;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.List;
import java.util.Locale;

import cc.minieye.kalman.core.position.PositionFilter;
import cc.minieye.kalman.maths.CDataArray;
import cc.minieye.kalman.maths.CVector;
import cc.minieye.kalman.util.Constants;

public class FragmentFigure extends Fragment implements OnTouchListener {
    private static int DATA_ARRAY_SIZE = 0;
    private static boolean DEBUG = false;
    private static final int INVALID_POINTER_ID = -1;
    private static int NONE;
    private static int ZOOM;
    private float downXValue;
    private int mActivePointerId;
    private ViewFlipper mFlipper;
    private Animation mIn1;
    private Animation mIn2;
    private float mLastTouchX;
    private float mLastTouchY;
    private Animation mOut1;
    private Animation mOut2;
    private PlotCanvas mPlotCanvasAcc;
    private PlotCanvas mPlotCanvasAlt;
    private PlotCanvas mPlotCanvasAngles;
    private PlotCanvas mPlotCanvasEstAcc;
    private PlotCanvas mPlotCanvasEstAngles;
    private PlotCanvas mPlotCanvasEstGyro;
    private PlotCanvas mPlotCanvasEstVel;
    private PlotCanvas mPlotCanvasGyro;
    private PlotCanvas mPlotCanvasLinAcc;
    private PlotCanvas mPlotCanvasMag;
    private boolean matrix_changed;
    private int max_view;
    private int mode;
    private int viewid;

    /* renamed from: cc.minieye.instoolkit.FragmentFigure.1 */
    class AnonymousClass1 implements OnLongClickListener {
        private final /* synthetic */ AttitudeIndicatorActivity val$activity;

        AnonymousClass1(AttitudeIndicatorActivity attitudeIndicatorActivity) {
            this.val$activity = attitudeIndicatorActivity;
        }

        public boolean onLongClick(View view) {
            Toast.makeText(this.val$activity, "Start Recording...", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    /* renamed from: cc.minieye.instoolkit.FragmentFigure.2 */
    class AnonymousClass2 implements OnClickListener {
        private final /* synthetic */ AttitudeIndicatorActivity val$activity;

        AnonymousClass2(AttitudeIndicatorActivity attitudeIndicatorActivity) {
            this.val$activity = attitudeIndicatorActivity;
        }

        public void onClick(View view) {
            this.val$activity.showPopupMenu(view, FragmentFigure.this.getView());
        }
    }

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
        private Paint paint_title;
        private int plotType;
        private float scalex;
        private float scaley;
        private float topMargin;
        public float xInterval;
        private int xTicks;
        private float xmax;
        private float xmin;
        private int yTicks;
        private float ymax;
        private float ymin;

        public PlotCanvas(Context context, String str, String str2, String[] strArr) {
            super(context);
            this.scalex = 1.0f;
            this.scaley = 1.0f;
            this.xmax = 100.0f;
            this.xmin = 0.0f;
            this.xTicks = 10;
            this.xInterval = 20.0f;
            this.ymax = 10.0f;
            this.ymin = -10.0f;
            this.yTicks = 5;
            this.paint_axes = new Paint();
            this.paint_labels = new Paint();
            this.paint_grid = new Paint();
            this.paint_data = new Paint();
            this.paint_title = new Paint();
            this.display_debug_info = false;
            this.display_meters_units = true;
            this.ColorList = new int[]{-16776961, -16711936, -65536, -16711681, -256, -65281, -7829368};
            this.dataArray = new CDataArray(str, str2, strArr, FragmentFigure.DATA_ARRAY_SIZE);
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
            this.paint_title.setColor(-7829368);
            this.paint_title.setTextSize(20.0f);
            this.paint_title.setAntiAlias(true);
            this.paint_title.setStyle(Style.FILL);
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
            if (f6 > 100.0f) {
                i2 = 5;
            }
            float f7 = f4 / ((float) i2);
            float[] fArr = new float[4];
            float[] fArr2 = new float[4];
            float f8 = (f6 - f5) / ((float) i2);
            float f9 = f3 > f ? f : f3;
            String format = String.format(Locale.ENGLISH, "%.1f", new Object[]{Float.valueOf(f5)});
            this.paint_labels.getTextBounds(format, 0, format.length(), rect);
            for (int i3 = 0; i3 <= i2; i3++) {
                fArr[0] = (((float) i3) * f7) + f2;
                fArr[1] = f9 - 10.0f;
                fArr[2] = fArr[0];
                fArr[3] = f9;
                float f10 = (((float) i3) * f8) + f5;
                String format2 = String.format(Locale.ENGLISH, "%.1f", new Object[]{Float.valueOf(f10)});
                this.paint_labels.getTextBounds(format2, 0, format2.length(), rect);
                canvas.drawLines(fArr, this.paint_axes);
                printText(canvas, format2, fArr[0] - (((float) rect.width()) / 2.0f), (float) (((double) fArr[3]) + (1.2d * ((double) rect.height()))));
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
            int size = cDataArray.size() - 1;
            float[] fArr = new float[(size * 4)];
            for (int i = 0; i < dimension; i++) {
                this.paint_data.setColor(this.ColorList[i - ((i / 7) * 7)]);
                int i2 = 0;
                for (int i3 = 0; i3 < size; i3++) {
                    if (cDataArray.getRollingData(i3, 0) - this.xmin > 0.0f) {
                        fArr[i2 * 4] = (this.scalex * (cDataArray.getRollingData(i3, 0) - this.xmin)) + f;
                        fArr[(i2 * 4) + 1] = f2 - (this.scaley * cDataArray.getRollingData(i3, i + 1));
                        fArr[(i2 * 4) + 2] = (this.scalex * (cDataArray.getRollingData(i3 + 1, 0) - this.xmin)) + f;
                        fArr[(i2 * 4) + 3] = f2 - (this.scaley * cDataArray.getRollingData(i3 + 1, i + 1));
                        i2++;
                    }
                }
                canvas.drawLines(fArr, this.paint_data);
            }
        }

        private void VerticalAxis(Canvas canvas, float f, float f2, float f3, float f4, float f5, float f6, int i) {
            Rect rect = new Rect();
            this.paint_labels.getTextBounds("+000.0", 0, "+000.0".length(), rect);
            float f7 = (f6 - f5) / ((float) 10);
            float f8 = f4 / ((float) 10);
            float[] fArr = new float[4];
            float[] fArr2 = new float[4];
            float f9 = f2 < f ? f : f2;
            for (int i2 = 0; i2 <= 10; i2++) {
                fArr[0] = f9;
                fArr[1] = f3 - (((float) i2) * f8);
                fArr[2] = 10.0f + f9;
                fArr[3] = fArr[1];
                String format = Math.abs((((float) i2) * f7) + f5) < 1.0f ? String.format(Locale.ENGLISH, "%.1g", new Object[]{Float.valueOf((((float) i2) * f7) + f5)}) : String.format(Locale.ENGLISH, "%.1f", new Object[]{Float.valueOf((((float) i2) * f7) + f5)});
                this.paint_labels.getTextBounds(format, 0, format.length(), rect);
                canvas.drawLines(fArr, this.paint_axes);
                printText(canvas, format, (float) (((double) fArr[0]) - (1.1d * ((double) rect.width()))), fArr[1] + (((float) rect.height()) / 2.0f));
                fArr[0] = f;
                fArr[2] = this.CanvasWidth + f;
                canvas.drawLines(fArr, this.paint_grid);
                if (i2 < 10) {
                    fArr2[0] = f9;
                    fArr2[1] = f3 - ((0.5f + ((float) i2)) * f8);
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

        private void getWindowDimensions(Window window, int i, int i2) {
            setSize((float) (i2 + 0), (float) i);
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
            float width2 = 0.1f * ((float) rect.width());
            float height = 0.5f * ((float) rect.height());
            RectF rectF = new RectF();
            rectF.left = width - width2;
            rectF.bottom = f2 + height;
            rectF.right = width2 + (((float) rect.width()) + width);
            rectF.top = (f2 - ((float) rect.height())) - height;
            canvas.drawRect(rectF, this.paint_title);
            canvas.drawText(str, width, f2, this.paint_labels);
        }

        private void scaleData() {
            this.xmax = (float) Math.max(Math.ceil((double) this.dataArray.MaxX()), (double) this.xInterval);
            this.xmin = Math.max(this.xmax - this.xInterval, 0.0f);
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

        public CDataArray getData() {
            return this.dataArray;
        }

        protected void onDraw(Canvas canvas) {
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
            float f3 = f2 + (this.ymin * this.scaley);
            if (this.dataArray.size() > 0) {
                VerticalAxis(canvas, f, f, f2, this.CanvasHeight, this.ymin, this.ymax, this.yTicks);
                HorizontalAxis(canvas, f2, f, f3, this.CanvasWidth, this.xmin, this.xmax, this.xTicks);
                PlotData(canvas, f, f3, this.dataArray);
            }
            float f4 = 0.1f * this.mScreenHeight;
            printLegend(canvas, f, f4, this.dataArray.legends);
            printTitle(canvas, f, f4, this.dataArray.title);
        }

        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            getWindowDimensions(null, FragmentFigure.this.mFlipper.getWidth(), FragmentFigure.this.mFlipper.getHeight());
        }

        public void pushData(float f, float f2, float f3) {
            this.dataArray.addValue(f, f2 * f3);
            scaleData();
        }

        public void pushData(float f, CVector cVector, double d) {
            this.dataArray.addValue(f, cVector.times(d));
            scaleData();
        }

        public void pushData(float f, CVector cVector, CVector cVector2, double d) {
            this.dataArray.addValue(f, cVector.times(d), cVector2.times(d));
            scaleData();
            FragmentFigure.this.mFlipper.invalidate();
        }

        public void setIntervalXOfInterest(float f) {
            this.xInterval = f;
        }
    }

    static {
        NONE = 0;
        ZOOM = 1;
        DEBUG = false;
        DATA_ARRAY_SIZE = 500;
    }

    public FragmentFigure() {
        this.max_view = 0;
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

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_data, menu);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        setHasOptionsMenu(true);
        AttitudeIndicatorActivity attitudeIndicatorActivity = (AttitudeIndicatorActivity) getActivity();
        View inflate = layoutInflater.inflate(R.layout.figure_menu_layout, viewGroup, false);
        this.mFlipper = (ViewFlipper) inflate.findViewById(R.id.viewFlipper1);
        this.max_view = -1;
        this.mPlotCanvasAngles = new PlotCanvas(attitudeIndicatorActivity, "Attitude Measures (deg)", "t (s)", new String[]{"roll", "pitch", "yaw"});
        this.mFlipper.addView(this.mPlotCanvasAngles);
        this.max_view++;
        this.mPlotCanvasEstAngles = new PlotCanvas(attitudeIndicatorActivity, "Estimated Attitude (deg)", "t (s)", new String[]{"roll", "pitch", "yaw"});
        this.mFlipper.addView(this.mPlotCanvasEstAngles);
        this.max_view++;
        this.mPlotCanvasGyro = new PlotCanvas(attitudeIndicatorActivity, "Gyroscopes output (rad/s)", "t (s)", new String[]{"gx", "gy", "gz"});
        this.mFlipper.addView(this.mPlotCanvasGyro);
        this.max_view++;
        this.mPlotCanvasEstGyro = new PlotCanvas(attitudeIndicatorActivity, "Gyro Estimates (rad/s)", "t (s)", new String[]{"gx", "gy", "gz", "est gx", "est gy", "est gz"});
        this.mFlipper.addView(this.mPlotCanvasEstGyro);
        this.max_view++;
        this.mPlotCanvasAcc = new PlotCanvas(attitudeIndicatorActivity, "Accelerometers output (m/s2)", "t (s)", new String[]{"ax", "ay", "az"});
        this.mFlipper.addView(this.mPlotCanvasAcc);
        this.max_view++;
        this.mPlotCanvasLinAcc = new PlotCanvas(attitudeIndicatorActivity, "Lin. Accelerometers output (m/s2)", "t (s)", new String[]{"ax", "ay", "az"});
        this.mFlipper.addView(this.mPlotCanvasLinAcc);
        this.max_view++;
        this.mPlotCanvasEstAcc = new PlotCanvas(attitudeIndicatorActivity, "Estimated Accelerations (m/s2)", "t (s)", new String[]{"ax", "ay", "az"});
        this.mFlipper.addView(this.mPlotCanvasEstAcc);
        this.max_view++;
        this.mPlotCanvasMag = new PlotCanvas(attitudeIndicatorActivity, "Magnetometers output (uT)", "t (s)", new String[]{"mx", "my", "mz"});
        this.mFlipper.addView(this.mPlotCanvasMag);
        this.max_view++;
        this.mPlotCanvasAlt = new PlotCanvas(attitudeIndicatorActivity, "Altitude (m)", "t (s)", new String[]{"alt"});
        this.mFlipper.addView(this.mPlotCanvasAlt);
        this.max_view++;
        this.mOut1 = AnimationUtils.loadAnimation(attitudeIndicatorActivity, android.R.anim.slide_out_right);
        this.mIn1 = AnimationUtils.loadAnimation(attitudeIndicatorActivity, android.R.anim.slide_in_left);
        this.mOut2 = AnimationUtils.loadAnimation(attitudeIndicatorActivity, R.anim.slide_out_left);
        this.mIn2 = AnimationUtils.loadAnimation(attitudeIndicatorActivity, R.anim.slide_in_right);
        this.mFlipper.setOnTouchListener(this);
        this.mFlipper.setOnLongClickListener(new AnonymousClass1(attitudeIndicatorActivity));
        Button button = (Button) inflate.findViewById(R.id.win_menu_btn);
        if (attitudeIndicatorActivity.useTabletLayout) {
            button.setOnClickListener(new AnonymousClass2(attitudeIndicatorActivity));
            return inflate;
        }
        button.setVisibility(View.GONE);
        return inflate;
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

    public void onPrepareOptionsMenu(Menu menu) {
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
        super.onPrepareOptionsMenu(menu);
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
                    this.mPlotCanvasAngles.ChangeScale(this.mLastTouchX, this.mLastTouchY, x2, f);
                    this.mPlotCanvasGyro.ChangeScale(this.mLastTouchX, this.mLastTouchY, x2, f);
                    this.mPlotCanvasAcc.ChangeScale(this.mLastTouchX, this.mLastTouchY, x2, f);
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
            Toast.makeText(getActivity(), "Zoom", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    public void pushData(PositionFilter positionFilter) {
        float timeStamp = (float) positionFilter.getTimeStamp();
        if (this.mPlotCanvasAngles != null) {
            CVector cVector = new CVector(3);
            positionFilter.getAttitude().getMeasuredOrientation(cVector);
            this.mPlotCanvasAngles.pushData(timeStamp, cVector, (double) Constants.RAD2DEG);
        }
        if (this.mPlotCanvasEstAngles != null) {
            this.mPlotCanvasEstAngles.pushData(timeStamp, positionFilter.getEstimatedAngles(), (double) Constants.RAD2DEG);
        }
        if (this.mPlotCanvasGyro != null) {
            this.mPlotCanvasGyro.pushData(timeStamp, positionFilter.getAttitude().getMeasureGyro(), 1.0d);
        }
        if (this.mPlotCanvasEstGyro != null) {
            this.mPlotCanvasEstGyro.pushData(timeStamp, positionFilter.getAttitude().getMeasureGyro(), positionFilter.getEstimatedAngularVelocity(), 1.0d);
        }
        if (this.mPlotCanvasAcc != null) {
            this.mPlotCanvasAcc.pushData(timeStamp, positionFilter.getMeasureTotalAcc(), 1.0d);
        }
        if (this.mPlotCanvasLinAcc != null) {
            this.mPlotCanvasLinAcc.pushData(timeStamp, positionFilter.getMeasureLinearAcc(), 1.0d);
        }
        if (this.mPlotCanvasEstAcc != null) {
            this.mPlotCanvasEstAcc.pushData(timeStamp, positionFilter.getEstimatedAcceleration(), 1.0d);
        }
        if (this.mPlotCanvasMag != null) {
            this.mPlotCanvasMag.pushData(timeStamp, positionFilter.getAttitude().getMeasureMag(), 1.0d);
        }
        if (this.mPlotCanvasAlt != null) {
            this.mPlotCanvasAlt.pushData(timeStamp, (float) positionFilter.getEstimatedAltitude(), 1.0f);
        }
    }
}
