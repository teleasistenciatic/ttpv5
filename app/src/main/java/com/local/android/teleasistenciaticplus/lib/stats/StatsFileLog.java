package com.local.android.teleasistenciaticplus.lib.stats;

import android.os.Environment;

import com.local.android.teleasistenciaticplus.lib.helper.AppLog;
import com.local.android.teleasistenciaticplus.modelo.Constants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by FESEJU on 26/05/2015.
 */
public class StatsFileLog implements Constants {

    File sdcard;
    File statLogFile;

    String statLogFileName;

    public StatsFileLog() {

        sdcard = Environment.getExternalStorageDirectory();

        initStatLogDir();
        initStatLogFile();

    }

    /**
     * Inicializamos el directorio donde copiaremos los datos
     */
    private void initStatLogDir() {

        //¿Existe el directorio? Si no existe se crea
        File logDirectory = new File(sdcard, Constants.STATS_LOG_DIR);
        if (!logDirectory.exists()) {
            logDirectory.mkdir();
        }
    }

    /** Iniciamos el Fichero de Log **/

    public void initStatLogFile() {

        //Se genera el nombre del fichero
        // STATS_LOG_DIR/05052015_123014.log

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateandTime = sdf.format( new Date() );

        statLogFileName = Constants.STATS_LOG_DIR + "/" + currentDateandTime + ".log.txt";

        statLogFile = new File(sdcard, statLogFileName);

        if (!statLogFile.exists()) {
            try {
                statLogFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                AppLog.e("StatsFileLog", "Creación del fichero: " + statLogFileName + "ERROR", e);
            }
        }
    }

    /** Se escribe en el fichero LOG **/

    public void write(String text) {

        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(statLogFile, true));
            buf.append(text);
            buf.newLine();
            buf.flush(); //Creo que esto es necesario
            buf.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            AppLog.e("StatsFileLog", "escritura de archivo: " + statLogFileName + "ERROR ESCRITURA", e);
        }

    }

}
