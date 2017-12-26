package com.easymi.common.util;

import android.content.Context;
import android.content.Intent;

import com.easymi.common.daemon.DaemonService;
import com.easymi.common.daemon.JobKeepLiveService;
import com.easymi.common.daemon.PuppetService;
import com.easymi.common.push.MQTTService;
import com.easymi.component.loc.LocService;
import com.easymi.component.loc.LocationHelperService;

/**
 * Created by developerLzh on 2017/12/26 0026.
 */

public class ServiceExitHelper {
    public static void exitAllService(Context context){
        Intent mqttIntent = new Intent(context,MQTTService.class);
        context.stopService(mqttIntent);

        Intent locIntent = new Intent(context,LocService.class);
        context.stopService(locIntent);

        Intent daemonIntent = new Intent(context,DaemonService.class);
        context.stopService(daemonIntent);

        Intent jobkeepIntent = new Intent(context,JobKeepLiveService.class);
        context.stopService(jobkeepIntent);

        Intent puppetIntent = new Intent(context,PuppetService.class);
        context.stopService(puppetIntent);

        Intent locHelpIntent = new Intent(context,LocationHelperService.class);
        context.stopService(locHelpIntent);
    }
}
