package com.atakmap.android.mumbleptt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.atakmap.android.gui.PanEditTextPreference;
import com.atakmap.android.mumbleptt.plugin.mumblePttLifecycle;
import com.atakmap.android.mumbleptt.plugin.R;

import com.atakmap.android.preference.PluginPreferenceFragment;
import com.atakmap.coremap.log.Log;

public class PluginPreferencesFragment extends PluginPreferenceFragment {

    @SuppressLint("StaticFieldLeak")
    private static Context pluginContext;

    public PluginPreferencesFragment() {
        super(pluginContext, R.xml.preferences);
    }

    @SuppressLint("ValidFragment")
    public PluginPreferencesFragment(final Context pluginContext) {
        super(pluginContext, R.xml.preferences);
        this.pluginContext = pluginContext;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public String getSubTitle() {
        return getSubTitle("Tool Preferences", pluginContext.getString(R.string.preferences_title));
    }
}