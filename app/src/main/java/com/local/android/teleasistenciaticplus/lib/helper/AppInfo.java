package com.local.android.teleasistenciaticplus.lib.helper;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;

import com.local.android.teleasistenciaticplus.modelo.Constants;
import com.local.android.teleasistenciaticplus.modelo.GlobalData;

/**
 * Created by FESEJU on 13/02/2015.
 * Devolverá valores de configuración de la aplicación como la versión
 */
public class AppInfo implements Constants {
    /**
     * getAppVersion: devuelve el nombre de la versión recogido en el AndroidManifest
     */
    public static String getAppVersion() {
        Context miContext = GlobalData.getAppContext();

        try {
            String app_ver = miContext.getPackageManager().getPackageInfo(miContext.getPackageName(), 0).versionName;
            return app_ver;
        } catch (PackageManager.NameNotFoundException e) {
            AppLog.e("AppInfo.getAppVersion: ", e.getMessage());
            return null;
        }
    }

    /**
     * Memoria disponible
     *
     * @return Long memoria disponible
     */
    public static long getUsedMemory() {
        Context context = GlobalData.getAppContext();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);

        return memoryInfo.availMem / Constants.MEMORY_DIVIDER;
    }

    /**
     * Memoria disponible
     *
     * @return Long memoria disponible
     * Sólo para API > 15
     */
    public static long getTotalMemory() {
        if (android.os.Build.VERSION.SDK_INT >= 16) {

            Context context = GlobalData.getAppContext();
            ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(memoryInfo);

            return memoryInfo.totalMem / Constants.MEMORY_DIVIDER;

        } else {
            return 0; //La función sólo está disponible para la API 16 o superior
        }
    }
}
