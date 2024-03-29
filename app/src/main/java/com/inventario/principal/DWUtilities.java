package com.inventario.principal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

public class DWUtilities {
    private static final String PROFILE_NAME = "DWGettingStartedJava";
    private static final String ACTION_DATAWEDGE = "com.symbol.datawedge.api.ACTION";
    private static final String EXTRA_CREATE_PROFILE = "com.symbol.datawedge.api.CREATE_PROFILE";
    private static final String EXTRA_SET_CONFIG = "com.symbol.datawedge.api.SET_CONFIG";

    public static void CreateDWProfile(Context context)
    {
        sendDataWedgeIntentWithExtra(context, ACTION_DATAWEDGE, EXTRA_CREATE_PROFILE, PROFILE_NAME);

        //  Requires DataWedge 6.4

        //  Now configure that created profile to apply to our application
        Bundle profileConfig = new Bundle();
        profileConfig.putString("PROFILE_NAME", PROFILE_NAME);
        profileConfig.putString("PROFILE_ENABLED", "true"); //  Seems these are all strings
        profileConfig.putString("CONFIG_MODE", "UPDATE");

        Bundle barcodeConfig = new Bundle();
        barcodeConfig.putString("PLUGIN_NAME", "BARCODE");
        barcodeConfig.putString("RESET_CONFIG", "true");
        Bundle barcodeProps = new Bundle();
        barcodeProps.putString("configure_all_scanners", "true");
        barcodeProps.putString("scanner_input_enabled", "true");
        barcodeConfig.putBundle("PARAM_LIST", barcodeProps);
        profileConfig.putBundle("PLUGIN_CONFIG", barcodeConfig);
        Bundle appConfig = new Bundle();
        appConfig.putString("PACKAGE_NAME", context.getPackageName());      //  Associate the profile with this app
        appConfig.putStringArray("ACTIVITY_LIST", new String[]{"*"});
        profileConfig.putParcelableArray("APP_LIST", new Bundle[]{appConfig});
        sendDataWedgeIntentWithExtra(context, ACTION_DATAWEDGE, EXTRA_SET_CONFIG, profileConfig);

        //  You can only configure one plugin at a time, we have done the barcode input, now do the intent output
        profileConfig.remove("PLUGIN_CONFIG");
        Bundle intentConfig = new Bundle();
        intentConfig.putString("PLUGIN_NAME", "INTENT");
        intentConfig.putString("RESET_CONFIG", "true");
        Bundle intentProps = new Bundle();
        intentProps.putString("intent_output_enabled", "true");
        intentProps.putString("intent_action", context.getResources().getString(R.string.activity_intent_filter_action));
        intentProps.putString("intent_delivery", "0");  //  StartActivity
        intentConfig.putBundle("PARAM_LIST", intentProps);
        profileConfig.putBundle("PLUGIN_CONFIG", intentConfig);
        sendDataWedgeIntentWithExtra(context, ACTION_DATAWEDGE, EXTRA_SET_CONFIG, profileConfig);

        //  Disable keyboard output
        profileConfig.remove("PLUGIN_CONFIG");
        Bundle keystrokeConfig = new Bundle();
        keystrokeConfig.putString("PLUGIN_NAME", "KEYSTROKE");
        keystrokeConfig.putString("RESET_CONFIG", "true");
        Bundle keystrokeProps = new Bundle();
        keystrokeProps.putString("keystroke_output_enabled", "false");
        keystrokeConfig.putBundle("PARAM_LIST", keystrokeProps);
        profileConfig.putBundle("PLUGIN_CONFIG", keystrokeConfig);
        sendDataWedgeIntentWithExtra(context, ACTION_DATAWEDGE, EXTRA_SET_CONFIG, profileConfig);
    }

    public static void crearIPPlugin(Context context, String IP){
        Bundle bMain = new Bundle();

        Bundle bConfigIPOutput = new Bundle();
        Bundle bParamsIPOutput = new Bundle();
        bParamsIPOutput.putString("ip_output_enabled", "false");
        bParamsIPOutput.putString("ip_output_ip_wedge_enabled", "false");
        bParamsIPOutput.putString("ip_output_protocol", "TCP"); //Supported Values: TCP: UDP
        bParamsIPOutput.putString("ip_output_address", IP); //Supported Values : IP Address format
        bParamsIPOutput.putString("ip_output_port", "58627"); //Supported Values : 1 - 65535

        bConfigIPOutput.putString("PLUGIN_NAME", "IP");
        bConfigIPOutput.putString("RESET_CONFIG", "true");
        bConfigIPOutput.putBundle("PARAM_LIST", bParamsIPOutput);

        ArrayList<Bundle> bundlePluginConfig = new ArrayList<>();
        bundlePluginConfig.add(bConfigIPOutput);
        bMain.putParcelableArrayList("PLUGIN_CONFIG", bundlePluginConfig);


        bMain.putString("PROFILE_NAME", PROFILE_NAME);
        bMain.putString("PROFILE_ENABLED", "true");
        bMain.putString("CONFIG_MODE", "CREATE_IF_NOT_EXIST");

        Intent iSetConfig = new Intent();
        iSetConfig.setAction("com.symbol.datawedge.api.ACTION");
        iSetConfig.putExtra("com.symbol.datawedge.api.SET_CONFIG", bMain);
        iSetConfig.putExtra("SEND_RESULT", "LAST_RESULT");
        iSetConfig.putExtra("COMMAND_IDENTIFIER", "INTENT_API");

        context.sendBroadcast(iSetConfig);
    }

    private static void sendDataWedgeIntentWithExtra(Context context, String action, String extraKey, String extraValue)
    {
        Intent dwIntent = new Intent();
        dwIntent.setAction(action);
        dwIntent.putExtra(extraKey, extraValue);
        context.sendBroadcast(dwIntent);
    }

    private static void sendDataWedgeIntentWithExtra(Context context, String action, String extraKey, Bundle extras)
    {
        Intent dwIntent = new Intent();
        dwIntent.setAction(action);
        dwIntent.putExtra(extraKey, extras);
        context.sendBroadcast(dwIntent);
    }
}
