
package com.atakmap.android.mumbleptt.plugin;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import com.atak.plugins.impl.AbstractPlugin;
import com.atak.plugins.impl.PluginContextProvider;
import com.atakmap.android.maps.MapComponent;
import com.atakmap.android.maps.MapView;
import com.atakmap.android.mumbleptt.mumblePttMapComponent;
import gov.tak.api.plugin.IServiceController;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;

import com.atakmap.android.mumbleptt.mumblePttWidget;
import com.atakmap.coremap.log.Log;

public class mumblePttLifecycle extends AbstractPlugin {

    /** CONSTRUCTOR **/
    public mumblePttLifecycle (IServiceController isc) {
        super(isc, new mumblePttTool(((PluginContextProvider) isc.getService(PluginContextProvider.class)).getPluginContext()),
                (MapComponent) new mumblePttMapComponent());
    }
}
