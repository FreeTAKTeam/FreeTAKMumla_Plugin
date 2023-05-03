
package com.atakmap.android.mumbleptt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.atakmap.android.maps.MapView;
import com.atakmap.android.mumbleptt.plugin.R;
import com.atakmap.android.dropdown.DropDown.OnStateListener;
import com.atakmap.android.dropdown.DropDownReceiver;

import com.atakmap.coremap.log.Log;
import com.atakmap.map.AtakMapView;

import android.webkit.ConsoleMessage;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import android.widget.LinearLayout;

import java.util.Locale;

public class mumblePttDropDownReceiver extends DropDownReceiver implements
        OnStateListener {

    public static final String TAG = "mumblePtt";
    public static final String SHOW_PLUGIN = "com.atakmap.android.mumbleptt.SHOW_PLUGIN";

    private final Context pluginContext;
    private final Context appContext;
    private final MapView mapView;
    private final LinearLayout ll_map;


    /**************************** CONSTRUCTOR *****************************/

    public mumblePttDropDownReceiver(final MapView mapView,
            final Context context) {
        super(mapView);
        this.pluginContext = context;
        this.appContext = mapView.getContext();
        this.mapView = mapView;

        LayoutInflater inflater = LayoutInflater.from(pluginContext);
        ll_map = (LinearLayout) inflater.inflate(R.layout.main_layout, null);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(TAG, String.format("Received Action: %s", action));
    }

    @Override
    public void disposeImpl() {

    }

    @Override
    public void onDropDownSelectionRemoved() {

    }

    @Override
    public void onDropDownVisible(boolean v) {
    }

    @Override
    public void onDropDownSizeChanged(double width, double height) {
    }

    @Override
    public void onDropDownClose() {
    }
}
