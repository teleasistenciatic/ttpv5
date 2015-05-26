package com.local.android.teleasistenciaticplus.modelo;

import android.app.Application;
import android.content.Context;

/**
 * Created by FESEJU on 11/02/2015.
 * - Se ejecutara en primer lugar, incluso antes que la actividad principal
 * - Es un Singleton que guardará el contexto de la aplicación allá cuando sea necesario
 * - Al guardar el Contexto (BIG) de la aplicación mediante getApplicationContext()
 * no tendremos el problema de memoryLeak
 *
 */

public class GlobalData extends Application {

    private static Context context; //Contexto de la aplicación
    private static String imei; //Número IMEI del terminal

    /**
     * Se guarda el contexto de la aplicación
     * @param context contexto BIG
     */
    public static void setContext(Context context) {
        GlobalData.context = context;
    }

    /**
     * Se guarda el numero de teléfono
     * @param imei número de teléfono
     */
    public static void setImei(String imei) {
        GlobalData.imei = imei;
    }

    /**
     * Getter del contexto de la aplicación
     * @return contexto BIG
     */
    public static Context getAppContext() {
        return GlobalData.context;
    }

    /**
     * Getter del numero de teléfono de la aplicación
     * @return numero de teléfono
     */
    public static String getImei() { return GlobalData.imei;}

}