package com.local.android.teleasistenciaticplus.lib.stats;

import com.local.android.teleasistenciaticplus.modelo.GlobalData;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by FESEJU on 27/05/2015.
 */
public class StatsFileLogTextGenerator {

    static String imei = GlobalData.getImei();

    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

    /** Escritura del Stat Log **/

    public static void write(String accion, String valor) {

        StatsFileLog mStatsFileLog = new StatsFileLog();

        String currentDateandTime = sdf.format(new Date());
        String texto = imei + ";" + currentDateandTime + ";" + accion + ";" + valor;

        mStatsFileLog.write(texto);

    }

}
