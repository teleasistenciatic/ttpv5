package com.local.android.teleasistenciaticplus.lib.helper;

/**
 * Created by FESEJU on 11/02/2015.
 * Genera log de aplicación añadiendo distintas funcionalidades:
 * - Mostrado o no de log en base a constantes de la aplicación
 * - Guardado en fichero de texto en la aplicación
 */

import android.util.Log;

import com.local.android.teleasistenciaticplus.lib.filesystem.FileOperation;
import com.local.android.teleasistenciaticplus.modelo.Constants;
import com.local.android.teleasistenciaticplus.modelo.DebugLevel;

/**
 * El método original es ( public static int d (String tag, String msg) )
 * Esta clase encapsula la llamada a Log y la hace dependiente de una constante:
 * Constants = DebugLevel.DEBUG;
 * <p/>
 * DEBUG	    Log.d.
 * ERROR	    Log.e.
 * INFO	    Log.i.
 * VERBOSE	Log.v.
 * WARN	    Log.w.
 *
 * Log.e: This is for when bad stuff happens. Use this tag in places like inside a catch statment. You know that an error
 *        has occurred and therefore you're logging an error.
 * Log.w: Use this when you suspect something shady is going on. You may not be completely in full on error mode,
 *        but maybe you recovered from some unexpected behavior. Basically, use this to log stuff you didn't expect to
 *        happen but isn't necessarily an error. Kind of like a "hey, this happened, and it's weird, we should look into it."
 * Log.i: Use this to post useful information to the log. For example: that you have successfully connected
 *        to a server. Basically use it to report successes.
 * Log.d: Use this for debugging purposes. If you want to print out a bunch of messages so you can log the
 *        exact flow of your program, use this. If you want to keep a log of variable values, use this.
 * Log.v: Use this when you want to go absolutely nuts with your logging. If for some reason you've decided
 *        to log every little thing in a particular part of your app, use the Log.v tag.
 */

public class AppLog {

    final static String TAG = "TIC";
    final static String fichero = Constants.DEBUG_LOG_FILE;

    /**
     * Log a DEBUG
     * @param tag clase donde se genera el log
     * @param msg mensaje de log
     */
    public static void d(String tag, String msg) {

        if (Constants.DEBUG_LEVEL == DebugLevel.PRODUCCION) {
            return;
        }

        Log.d(tag + " --> ", msg);

        if (Constants.LOG_TO_FILE) {
            FileOperation.fileLogWrite(fichero, tag, msg);
        }
    }

    /**
     * Función con la firma de parámetros simplificada
     * @param msg mensaje de log
     */
    public static void d(String msg) {
        d(TAG, msg);
    }

    /**
     * Log a ERROR
     * @param tag clase donde se genera el log
     * @param msg mensaje de log
     */
    public static void e(String tag, String msg) {

        if (Constants.DEBUG_LEVEL == DebugLevel.PRODUCCION) {
            return;
        }

        Log.e( tag + " --> ", msg);

        if (Constants.LOG_TO_FILE) {
            FileOperation.fileLogWrite(fichero, tag, msg);
        }
    }

    /**
     * Log a ERROR with Exception
     * @param tag lugar donde se produce el log
     * @param msg mensaje de log
     * @param e Exception
     */
    public static void e(String tag, String msg, Exception e) {

        if (Constants.DEBUG_LEVEL == DebugLevel.PRODUCCION) {
            return;
        }
        msg = msg + "\n" + e.getMessage();
        Log.e( tag + " --> ", msg);
        e.printStackTrace();


        if (Constants.LOG_TO_FILE ) {
            FileOperation.fileLogWrite(fichero, tag, msg);
        }
    }

    /**
     * Log a INFO
     * @param tag clase donde se genera el log
     * @param msg mensaje de log
     */
    public static void i(String tag, String msg) {

        if (Constants.DEBUG_LEVEL == DebugLevel.PRODUCCION) {
            return;
        }

        Log.i(tag, msg);

        if (Constants.LOG_TO_FILE ) {
            FileOperation.fileLogWrite(fichero, tag, msg);
        }
    }

    /**
     * Log a VERBOSE
     * @param tag clase donde se genera el log
     * @param msg mensaje de log
     */

    public static void v(String tag, String msg) {

        if (Constants.DEBUG_LEVEL == DebugLevel.PRODUCCION) {
            return;
        }

        Log.v( tag + " --> ", msg);

        if (Constants.LOG_TO_FILE) {
            FileOperation.fileLogWrite(fichero, tag, msg);
        }
    }

    /**
     * Log a WARNING
     * @param tag clase donde se genera el log
     * @param msg mensaje de log
     */
    public static void w(String tag, String msg) {

        if (Constants.DEBUG_LEVEL == DebugLevel.PRODUCCION) {
            return;
        }

        Log.w( tag + " --> ", msg);

        if (Constants.LOG_TO_FILE) {
            FileOperation.fileLogWrite(fichero, tag, msg);
        }
    }

} // Fin AppLog