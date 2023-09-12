
package com.atakmap.android.mumbleptt;

import static android.view.KeyEvent.ACTION_UP;
import static android.view.KeyEvent.ACTION_DOWN;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;

import com.atakmap.android.ipc.AtakBroadcast;
import com.atakmap.android.ipc.AtakBroadcast.DocumentedIntentFilter;

import com.atakmap.android.maps.MapView;
import com.atakmap.android.dropdown.DropDownMapComponent;

import com.atakmap.android.widgets.RootLayoutWidget;

import com.atakmap.app.preferences.ToolsPreferenceFragment;
import com.atakmap.coremap.log.Log;
import com.atakmap.android.mumbleptt.plugin.R;
import com.atakmap.map.AtakMapView;

import java.util.List;

public class mumblePttMapComponent extends DropDownMapComponent {

    private static final String TAG = "mumblePttMapComponent";
    private SharedPreferences sharedPreference;
    private Context pluginContext;
    public static mumblePttWidget widget;
    private int width;

    public void onCreate(final Context context, Intent intent,
                         final MapView view) {

        context.setTheme(R.style.ATAKPluginTheme);
        super.onCreate(context, intent, view);
        pluginContext = context;
        this.sharedPreference = PreferenceManager.getDefaultSharedPreferences(view.getContext().getApplicationContext());
        widget = new mumblePttWidget(view);

        width = view.getWidth();

        view.addOnMapViewResizedListener(new AtakMapView.OnMapViewResizedListener() {
            @Override
            public void onMapViewResized(AtakMapView atakMapView) {
                Log.d(TAG, "RESIZED");
                Log.d(TAG, "Width: " + atakMapView.getWidth());
                if (width < atakMapView.getWidth()) {
                    widget.setMargins(0, 0, atakMapView.getWidth() - 128, 0);
                } else {
                    widget.setMargins(0, 0, atakMapView.getWidth() - 256, 0);
                }
            }
        });

        if (width < 2000)
            widget.setMargins(0,0,width-256,0);
        else
            widget.setMargins(0,0,width-256,0);

        view.addOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent event) {
                Log.d(TAG, "i: " + i + " onhKeyEvent: " + event.toString());
                String hwkey = sharedPreference.getString("plugin_mumbleptt_hwbutton", "999");
                if (!hwkey.equalsIgnoreCase("999")) {
                    if (i == KeyEvent.KEYCODE_HEADSETHOOK) {
                        if (hwkey.equalsIgnoreCase(String.valueOf(i)) && event.getAction() == ACTION_DOWN && event.getRepeatCount() == 0) {
                            Intent t = new Intent("se.lublin.mumla.action.TALK");
                            AtakBroadcast.getInstance().sendSystemBroadcast(t);
                            widget.toggleIcon(((++(widget.toggled)) % 2));
                            return true;
                        }
                    }
                    if (hwkey.equalsIgnoreCase(String.valueOf(i)) && event.getAction() == ACTION_UP) {
                        Intent t = new Intent("se.lublin.mumla.action.TALK");
                        AtakBroadcast.getInstance().sendSystemBroadcast(t);
                        widget.toggleIcon(((++(widget.toggled)) % 2));
                        return true;
                    }
                }
                return false;
            }
        });

        ToolsPreferenceFragment.register(
                new ToolsPreferenceFragment.ToolPreference(
                        pluginContext.getString(R.string.preferences_title),
                        pluginContext.getString(R.string.preferences_summary),
                        pluginContext.getString(R.string.key_mumbleptt_preferences),
                        pluginContext.getResources().getDrawable(R.drawable.ic_launcher),
                        new PluginPreferencesFragment(
                                pluginContext)));
    }

    @Override
    protected void onDestroyImpl(Context context, MapView view) {
        super.onDestroyImpl(context, view);
    }

}