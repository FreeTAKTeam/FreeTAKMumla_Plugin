
package com.atakmap.android.mumbleptt.plugin;

import android.content.Context;
import com.atak.plugins.impl.AbstractPluginTool;

public class mumblePttTool extends AbstractPluginTool {
    /** CONSTRUCTOR **/
    public mumblePttTool (final Context context) {
        super(context, context.getString(R.string.app_name), context.getString(R.string.app_name),
                context.getResources().getDrawable(R.drawable.ic_launcher), "com.atakmap.android.mumblePttDropDownReceiver.SHOW_PLUGIN");
        PluginNativeLoader.init(context);
    }
}
