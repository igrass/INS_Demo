package cc.minieye.instoolkit.ui;

import android.app.ListActivity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;


import cc.minieye.instoolkit.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FormSensorList extends ListActivity {
    private ViewHolder holder;
    List<HashMap<String, Object>> items;

    public class SpecialAdapter extends SimpleAdapter {
        private int[] colors;

        public SpecialAdapter(Context context, List<HashMap<String, Object>> list, int i, String[] strArr, int[] iArr) {
            super(context, list, i, strArr, iArr);
            this.colors = new int[]{-16777216, -16777216};
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            View view2 = super.getView(i, view, viewGroup);
            view2.setBackgroundColor(this.colors[i % this.colors.length]);
            if (((HashMap) FormSensorList.this.items.get(i)).get("color") == "blue") {
                FormSensorList.this.holder = new ViewHolder();
                FormSensorList.this.holder.text = (TextView) view2.findViewById(R.id.sensor_title);
                FormSensorList.this.holder.text2 = (TextView) view2.findViewById(R.id.sensor_content);
                FormSensorList.this.holder.text.setTextColor(-16776961);
                view2.setTag(FormSensorList.this.holder);
            }
            return view2;
        }
    }

    static class ViewHolder {
        TextView text;
        TextView text2;

        ViewHolder() {
        }
    }

    public FormSensorList() {
        this.items = new ArrayList();
    }

    private List<HashMap<String, Object>> createSensorsList() {
        for (Sensor sensor : ((SensorManager) getSystemService(Context.SENSOR_SERVICE)).getSensorList(-1)) {
            HashMap hashMap = new HashMap();
            hashMap.put("title", sensor.getName());
            Object stringBuilder = new StringBuilder(String.valueOf(sensor.getVendor() + "\n")).append("Resolution : ").append(sensor.getResolution()).append(" [sensor unit] \n").toString();
            float minDelay = (float) sensor.getMinDelay();
            if (minDelay > 0.0f) {
                stringBuilder = new StringBuilder(String.valueOf(stringBuilder)).append("Refresh rate : ").append(1000000.0d / ((double) minDelay)).append("Hz \n").toString();
            }
            hashMap.put("vendor", stringBuilder);
            if (sensor.getType() == 4 || sensor.getType() == 1 || sensor.getType() == 2) {
                hashMap.put("color", "blue");
            } else {
                hashMap.put("color", "red");
            }
            this.items.add(hashMap);
        }
        return this.items;
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setListAdapter(new SpecialAdapter(this, createSensorsList(), R.layout.sensor_layout, new String[]{"title", "vendor", "color"}, new int[]{R.id.sensor_title, R.id.sensor_content}));
    }
}
