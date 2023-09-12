
package com.atakmap.android.mumbleptt.plugin;

import com.atak.plugins.impl.AbstractPlugin;

import gov.tak.api.plugin.IPlugin;
import gov.tak.api.plugin.IServiceController;

import com.atakmap.android.mumbleptt.mumblePttMapComponent;

public class mumblePttLifecycle extends AbstractPlugin implements IPlugin {

    public mumblePttLifecycle(IServiceController serviceController) {
        super(serviceController, new mumblePttMapComponent());

    }
}
