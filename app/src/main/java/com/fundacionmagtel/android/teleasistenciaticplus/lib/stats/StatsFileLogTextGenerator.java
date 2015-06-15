package com.fundacionmagtel.android.teleasistenciaticplus.lib.stats;

import com.fundacionmagtel.android.teleasistenciaticplus.lib.filesystem.FileOperation;
import com.fundacionmagtel.android.teleasistenciaticplus.modelo.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by FESEJU on 27/05/2015.
 *
 * Esta clase vuelca completamente el log de la aplicación en un fichero de texto
 * en el caso de que no se pueda acceder al debug del mismo.
 *
 * Dado que los servicios interfieren en la escritura concurrente / paralela
 * cada uno de ellos escribe en un fichero distintos.
 *
 * Se podría auna la escritura de los ficheros pero usando un servicio aparte al
 * que todos llamasen.
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

    /** Escritura de los datos de GPS en su fichero correspondiente */
    public static void writeGps(String accion, String valor) {

        String currentDateandTime = sdf.format(new Date());
        String texto = ">" + currentDateandTime + ";" + accion + ";" + valor;

        FileOperation.fileLogWrite(Constants.DEBUG_ZONA_SEGURA_LOG_FILE, texto, valor);
    }
}
