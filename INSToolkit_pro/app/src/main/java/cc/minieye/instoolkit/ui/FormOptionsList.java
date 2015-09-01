package cc.minieye.instoolkit.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import cc.minieye.instoolkit.AttitudeIndicatorActivity;
import cc.minieye.instoolkit.R;

public final class FormOptionsList extends PreferenceActivity implements OnSharedPreferenceChangeListener {
    private ListPreference covSettingsPref;
    private EditTextPreference editaccelcov;
    private EditTextPreference editanglecov;
    private EditTextPreference editcompasscov;
    private EditTextPreference editomegacov;
    private EditTextPreference editpositioncov;
    private EditTextPreference editvelocitycov;
    private ListPreference selectFilter;
    private CustomListPreference selectLayout;

    /* renamed from: cc.minieye.instoolkit.ui.FormOptionsList.2 */
    class AnonymousClass2 implements OnPreferenceClickListener {
        private final /* synthetic */ Editor val$editor;

        AnonymousClass2(Editor editor) {
            this.val$editor = editor;
        }

        public boolean onPreferenceClick(Preference preference) {
            this.val$editor.putFloat("rest_pitch_value", 0.0f);
            this.val$editor.apply();
            return true;
        }
    }

    public FormOptionsList() {
        this.editanglecov = null;
        this.editcompasscov = null;
        this.editomegacov = null;
        this.editpositioncov = null;
        this.editvelocitycov = null;
        this.editaccelcov = null;
        this.selectFilter = null;
        this.selectLayout = null;
    }

    private void changeCovarianceSetting(int i) {
        boolean z = false;
        if (i == 999) {
            z = true;
        }
        this.editanglecov.setEnabled(z);
        this.editcompasscov.setEnabled(z);
        this.editomegacov.setEnabled(z);
        this.editpositioncov.setEnabled(z);
        this.editvelocitycov.setEnabled(z);
        this.editaccelcov.setEnabled(z);
    }

    private void displaySensorList() {
        startActivity(new Intent(this, FormSensorList.class));
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.layout.options_layout);
        this.editanglecov = (EditTextPreference) getPreferenceScreen().findPreference("editanglecov");
        this.editcompasscov = (EditTextPreference) getPreferenceScreen().findPreference("editcompasscov");
        this.editomegacov = (EditTextPreference) getPreferenceScreen().findPreference("editomegacov");
        this.editpositioncov = (EditTextPreference) getPreferenceScreen().findPreference("editpositioncov");
        this.editvelocitycov = (EditTextPreference) getPreferenceScreen().findPreference("editvelocitycov");
        this.editaccelcov = (EditTextPreference) getPreferenceScreen().findPreference("editaccelcov");
        this.selectFilter = (ListPreference) getPreferenceScreen().findPreference("filterchoice");
//        this.selectFilter.setEnabled(false);
        findPreference("sensorlist").setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                FormOptionsList.this.displaySensorList();
                return true;
            }
        });
        Preference findPreference = findPreference("resetpitch");
        findPreference.setOnPreferenceClickListener(new AnonymousClass2(findPreference.getEditor()));
        boolean isTablet = AttitudeIndicatorActivity.isTablet(this);
        this.selectLayout = (CustomListPreference) getPreferenceScreen().findPreference("selectlayout");
        if (isTablet) {
            CustomListPreference customListPreference = this.selectLayout;
            boolean[] zArr = new boolean[4];
            zArr[1] = true;
            zArr[2] = true;
            zArr[3] = true;
            customListPreference.setEnabledItems(zArr);
        }
        this.covSettingsPref = (ListPreference) findPreference("paramcov");
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    protected void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    protected void onResume() {
        super.onResume();
        changeCovarianceSetting(Integer.parseInt(this.covSettingsPref.getSharedPreferences().getString("paramcov", "1")));
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        AttitudeIndicatorActivity.isTablet(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
        if (str.equals("paramcov")) {
            changeCovarianceSetting(Integer.parseInt(sharedPreferences.getString(str, "1")));
        }
    }
}
