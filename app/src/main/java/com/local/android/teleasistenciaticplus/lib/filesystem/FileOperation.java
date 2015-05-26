package com.local.android.teleasistenciaticplus.lib.filesystem;

import android.os.Environment;

import com.local.android.teleasistenciaticplus.modelo.Constants;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by FESEJU on 12/02/2015.
 * Escritura del log a un fichero físico en android
 */

public class FileOperation implements Constants {

    public static void fileLogInitialize(String fichero) {

        File sdcard = Environment.getExternalStorageDirectory();
        File logFile = new File(sdcard,fichero);

        if (logFile.exists()) {
            fileLogErase(fichero);
        }
    }

    /**
     * Borrado del fichero de log
     */
    public static void fileLogErase(String fichero) {

        File sdcard = Environment.getExternalStorageDirectory();
        File logFile = new File(sdcard,fichero);

        try {
            logFile.delete();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Un mensaje de debug tienen la opción de ser escritos a un fichero LOG
     * esta opción de LOG se define en Constants (LOG_TO_FILE = false;)
     *
     * @param tag La clase o módulo donde se lanza el mensaje
     * @param msg El mensaje generado
     */
    public static void fileLogWrite(String fichero, String tag, String msg) {
        {
            //Timestamp
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String currentDateandTime = sdf.format(new Date());

            String text = currentDateandTime + ":" + tag + "--> " + msg;

            File sdcard = Environment.getExternalStorageDirectory();
            File logFile = new File(sdcard,fichero);

            //////////// En el caso que no quisieramos borrar el fichero ///////
            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            try {
                //BufferedWriter for performance, true to set append to file flag
                BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
                buf.append(text);
                buf.newLine();
                buf.flush(); //Creo que esto es necesario
                buf.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static String fileRead(String fileName) {

        //Find the directory for the SD Card using the API
        //*Don't* hardcode "/sdcard"
        File sdcard = Environment.getExternalStorageDirectory();

        //Get the text file
        File file = new File(sdcard, fileName);

        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
            // TODO Excepciones en la lectura de archivos
        }

        return text.toString();
    }

    /**
     * Lectura del fichero por defecto de LOG de sistema que se encuentra en CONSTANTS.
     * @return
     */
    /*
    public static String fileLogRead() {

      return ( fileRead( Constants.DEBUG_LOG_FILE )) ;

    }*/
}