
package com.atakmap.android.mumbleptt;

import android.content.Context;
import android.content.Intent;

import com.atakmap.android.ipc.AtakBroadcast.DocumentedIntentFilter;

import com.atakmap.android.maps.MapView;
import com.atakmap.android.dropdown.DropDownMapComponent;

import com.atakmap.app.preferences.ToolsPreferenceFragment;
import com.atakmap.coremap.log.Log;
import com.atakmap.android.mumbleptt.plugin.R;

public class mumblePttMapComponent extends DropDownMapComponent {

    private static final String TAG = "mumblePttMapComponent";
    private Context pluginContext;
    private mumblePttDropDownReceiver ddr;
    public static mumblePttWidget widget;
    public void onCreate(final Context context, Intent intent,
            final MapView view) {

        context.setTheme(R.style.ATAKPluginTheme);
        super.onCreate(context, intent, view);
        pluginContext = context;

        ddr = new mumblePttDropDownReceiver(
                view, context);

        widget = new mumblePttWidget(view, ddr);
    }

    @Override
    protected void onDestroyImpl(Context context, MapView view) {
        super.onDestroyImpl(context, view);
    }

}
