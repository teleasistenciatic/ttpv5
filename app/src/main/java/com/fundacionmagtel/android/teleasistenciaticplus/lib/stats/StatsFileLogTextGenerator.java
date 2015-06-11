package com.fundacionmagtel.android.teleasistenciaticplus.lib.stats;

import com.fundacionmagtel.android.teleasistenciaticplus.lib.filesystem.FileOperation;
import com.fundacionmagtel.android.teleasistenciaticplus.modelo.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by FESEJU on 27/05/2015.
 */
public class StatsFileLogTextGenerator {

    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

    /** Escritura del Stat Log **/

    public static void write(String accion, String valor) {

        StatsFileLog mStatsFileLog = new StatsFileLog();

        String currentDateandTime = sdf.format(new Date());
        String texto = ">" + currentDateandTime + ";" + accion + ";" + valor;

        mStatsFileLog.write(texto);
    }

    public static void writeGps(String accion, String valor) {

        String currentDateandTime = sdf.format(new Date());
        String texto = ">" + currentDateandTime + ";" + accion + ";" + valor;

        FileOperation.fileLogWrite(Constants.DEBUG_ZONA_SEGURA_LOG_FILE, texto, valor);
    }


}
