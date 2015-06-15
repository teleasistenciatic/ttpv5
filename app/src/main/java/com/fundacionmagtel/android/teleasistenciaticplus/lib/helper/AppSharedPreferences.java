package com.fundacionmagtel.android.teleasistenciaticplus.lib.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.fundacionmagtel.android.teleasistenciaticplus.modelo.Constants;
import com.fundacionmagtel.android.teleasistenciaticplus.modelo.GlobalData;
import com.google.android.gms.maps.model.LatLng;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.stats.StatsFileLogTextGenerator;


/**
 * Created by FESEJU on 23/03/2015.
 * Leera y guardará preferencias de la aplicación mediante SharedPreferences
 * @author Juan Jose Ferres
 */

public class AppSharedPreferences implements Constants {

    String TAG = "AppSharedPreferences";

    public void setUserData(String nombre, String apellidos) {

        /////////////////////////////////////////////////////
        StatsFileLogTextGenerator.write("appSharedPreferences", "nombre y apellidos creados : " + nombre + "-" + apellidos );
        /////////////////////////////////////////////////////

        SharedPreferences.Editor editor = GlobalData.getAppContext().getSharedPreferences(APP_SHARED_PREFERENCES_FILE, Context.MODE_MULTI_PROCESS).edit();
        editor.putString("nombre", nombre);
        editor.putString("apellidos", apellidos);
        editor.commit();
    }

    /**
     * Recupera los datos de nombre y apellidos en el modo offline
     *
     * @return String[0] Nombre String[1] Apellidos
     */
    public String[] getUserData() {

        SharedPreferences prefs = GlobalData.getAppContext().getSharedPreferences(APP_SHARED_PREFERENCES_FILE, Context.MODE_MULTI_PROCESS);
        String nombre = prefs.getString("nombre", "");//"No name defined" is the default value.
        String apellidos = prefs.getString("apellidos", ""); //0 is the default value.

        String[] datosPersonalesUsuario = {nombre, apellidos};

        return datosPersonalesUsuario;
    }

    /**
     * ¿Tiene datos personales?
     *
     * @return boolean tiene datos personales
     */
    public boolean hasUserData() {

        SharedPreferences prefs = GlobalData.getAppContext().getSharedPreferences(APP_SHARED_PREFERENCES_FILE, Context.MODE_MULTI_PROCESS);
        String nombre = prefs.getString("nombre", "");
        String apellidos = prefs.getString("apellidos", "");

        if ((nombre.length() > 0) && (apellidos.length() > 0)) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * Borrar datos personales
     */
    public void deleteUserData() {

        SharedPreferences.Editor editor = GlobalData.getAppContext().getSharedPreferences(APP_SHARED_PREFERENCES_FILE, Context.MODE_MULTI_PROCESS).edit();
        editor.putString("nombre", "");
        editor.putString("apellidos", "");
        editor.commit();

    }

    /**
     * Función para almacenar en el shared preferences los datos de personas de contacto
     */
    public void setPersonasContacto(String nombre1, String telefono1, String nombre2, String telefono2, String nombre3, String telefono3) {

        /////////////////////////////////////////////////////
        StatsFileLogTextGenerator.write("appSharedPreferences", "contactos creados : "
                + nombre1 + "-" + telefono1 + "_"
                        + nombre2 + "-" + telefono2 + "_"
                        + nombre3 + "-" + telefono3
        );
        /////////////////////////////////////////////////////

        SharedPreferences.Editor editor = GlobalData.getAppContext().getSharedPreferences(APP_SHARED_PREFERENCES_FILE, Context.MODE_MULTI_PROCESS).edit();

        editor.putString("nombre1", nombre1);
        editor.putString("telefono1", telefono1);

        editor.putString("nombre2", nombre2);
        editor.putString("telefono2", telefono2);

        editor.putString("nombre3", nombre3);
        editor.putString("telefono3", telefono3);

        editor.commit();
    }

    /**
     * Borrar los datos del sharedPreferences
     */
    public void deletePersonasContacto() {
        deletePersonasContactobyId(0);
        deletePersonasContactobyId(1);
        deletePersonasContactobyId(2);
    }

    /**
     * Borrar los datos de contacto seleccionado del sharedPreferences
     */
    public void deletePersonasContactobyId(int contactoABorrar) {
        SharedPreferences.Editor editor = GlobalData.getAppContext().getSharedPreferences(APP_SHARED_PREFERENCES_FILE, Context.MODE_MULTI_PROCESS).edit();

        if (contactoABorrar == 0) {
            editor.putString("nombre1", "");
            editor.putString("telefono1", "");
        } else if (contactoABorrar == 1) {
            editor.putString("nombre2", "");
            editor.putString("telefono2", "");
        } else if (contactoABorrar == 2) {
            editor.putString("nombre3", "");
            editor.putString("telefono3", "");
        }

        editor.commit();
    }

    /**
     * Devuelve un array con los teléfonos de contacto
     *
     * @return
     */

    public String[] getPersonasContacto() {

        SharedPreferences prefs = GlobalData.getAppContext().getSharedPreferences(APP_SHARED_PREFERENCES_FILE, Context.MODE_MULTI_PROCESS);
        String nombre1 = prefs.getString("nombre1", "");
        String telefono1 = prefs.getString("telefono1", "");
        String nombre2 = prefs.getString("nombre2", "");
        String telefono2 = prefs.getString("telefono2", "");
        String nombre3 = prefs.getString("nombre3", "");
        String telefono3 = prefs.getString("telefono3", "");

        String[] personasContacto = {nombre1, telefono1,
                nombre2, telefono2,
                nombre3, telefono3};

        return personasContacto;
    }

    /**
     * Obtenemos la primera persona de contacto que tiene valor
     *
     * @return
     */

    public String getFirstTelefonoContacto() {

        SharedPreferences prefs = GlobalData.getAppContext().getSharedPreferences(APP_SHARED_PREFERENCES_FILE, Context.MODE_MULTI_PROCESS);
        String telefono1 = prefs.getString("telefono1", "");
        String telefono2 = prefs.getString("telefono2", "");
        String telefono3 = prefs.getString("telefono3", "");

        String firstTelefonoContacto = "";

        if ( telefono3.length() > 0 ) {
            firstTelefonoContacto = telefono3;
        }

        if ( telefono2.length() > 0 ) {
            firstTelefonoContacto = telefono2;
        }

        if ( telefono1.length() > 0 ) {
            firstTelefonoContacto = telefono1;
        }

        return firstTelefonoContacto;
    }

    /**
     * Comprobación de si hay datos de contacto
     *
     * @return boolean hay alguna persona de contacto
     */
    public Boolean hasPersonasContacto() {

        SharedPreferences prefs = GlobalData.getAppContext().getSharedPreferences(APP_SHARED_PREFERENCES_FILE, Context.MODE_MULTI_PROCESS);
        String nombre1 = prefs.getString("nombre1", "");
        String telefono1 = prefs.getString("telefono1", "");
        String nombre2 = prefs.getString("nombre2", "");
        String telefono2 = prefs.getString("telefono2", "");
        String nombre3 = prefs.getString("nombre3", "");
        String telefono3 = prefs.getString("telefono3", "");

        if (telefono1.length() > 0 || telefono2.length() > 0 || telefono3.length() > 0) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * ¿Tiene la posición de zona segura guardada?
     *
     * @return boolean tiene establecida la zona segura
     */
    public boolean hasZonaSegura() {

        SharedPreferences prefs = GlobalData.getAppContext().getSharedPreferences(APP_SHARED_PREFERENCES_FILE, Context.MODE_MULTI_PROCESS);

        String latitud = prefs.getString(Constants.ZONA_SEGURA_LATITUD, "");
        String longitud = prefs.getString(Constants.ZONA_SEGURA_LONGITUD, "");


        if ( (latitud.length() > 0) && (longitud.length() > 0) ) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * Leemos los datos de la última posición de zona segura guardada en caso de haberlos
     * @return
     */
    public String[] getZonaSeguraData() {

        AppSharedPreferences miAppSharedPreferences = new AppSharedPreferences();

        String[] datos = {
                miAppSharedPreferences.getPreferenceData(Constants.ZONA_SEGURA_LATITUD),
                miAppSharedPreferences.getPreferenceData(Constants.ZONA_SEGURA_LONGITUD),
                miAppSharedPreferences.getPreferenceData(Constants.ZONA_SEGURA_RADIO)
        };

        return datos;
    }

    /**
     * Guarda los datos de la Zona Segura en las Shared Preferences
     * @param pos
     * @param radio
     */
    public void setZonaSeguraData(LatLng pos, Double radio) {

        setPreferenceData(Constants.ZONA_SEGURA_LATITUD, String.valueOf(pos.latitude));
        setPreferenceData(Constants.ZONA_SEGURA_LONGITUD, String.valueOf(pos.longitude));

        if (radio < 10) {
            radio = 10.0;
        }

        /////////////////////////////////////////////////////
        StatsFileLogTextGenerator.write("zona segura", "zona segura establecida: " + String.valueOf(pos.latitude) + "," + String.valueOf(pos.longitude) + "," + String.valueOf(radio));
        /////////////////////////////////////////////////////

        setPreferenceData(Constants.ZONA_SEGURA_RADIO, String.valueOf(radio));
    }

    /*
    // Guarda posicion GPS
    public void setGpsPos(String latitud, String longitud, String precision, String ultimaActualizacion) {

        setPreferenceData(Constants.GPS_LATITUD, latitud);
        setPreferenceData(Constants.GPS_LONGITUD, longitud);
        setPreferenceData(Constants.GPS_PRECISION, precision);
        setPreferenceData(Constants.GPS_ULTIMA_ACTUALIZACION, ultimaActualizacion);

    }*/

    // Lee posicion GPS
    public String[] getGpsPos() {

        String[] gpsPosicion = new String[5];

        gpsPosicion[0] = getPreferenceData(Constants.GPS_LATITUD);
        gpsPosicion[1] = getPreferenceData(Constants.GPS_LONGITUD);
        gpsPosicion[2] = getPreferenceData(Constants.GPS_PRECISION);
        gpsPosicion[3] = getPreferenceData(Constants.GPS_ULTIMA_ACTUALIZACION);
        gpsPosicion[4] = getPreferenceData(Constants.GPS_ULTIMA_ACTUALIZACION_FORMATO_NUMERICO);

        return gpsPosicion;
    }

    public boolean hasGpsPos() {

        SharedPreferences prefs = GlobalData.getAppContext().getSharedPreferences(APP_SHARED_PREFERENCES_FILE, Context.MODE_MULTI_PROCESS);

        String latitud = prefs.getString(Constants.GPS_LATITUD, "");
        String longitud = prefs.getString(Constants.GPS_LONGITUD, "");


        if ( (latitud.length() > 0) && (longitud.length() > 0) ) {
            return true;
        } else {
            return false;
        }

    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
    // METODOS PARA EL MONITOR DE BATERIA
    //////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Proporciona una cadena de caracteres con información sobre los datos de batería guardados.
     * Se utiliza para información de estado y debug.
     * @return CharSequence con la información.
     */
    public CharSequence dameCadenaPreferenciasMonitorBateria()
    {
        return "ActivarAlInicio = " + getPreferenceData(MONITOR_BATERIA_ARRANCAR_AL_INICIO) +
                ", TasaRefresco = " + getPreferenceData(MONITOR_BATERIA_TASA_REFRESCO) +
                ", NivelAlerta = " + getPreferenceData(MONITOR_BATERIA_NIVEL_ALERTA);
    }

    /**
     * Método que devuelve si hay almacenados datos de batería en SharedPreferences.
     * @return true si hay datos registrados, false en otro caso.
     */
    public boolean hayDatosBateria()
    {
        CharSequence letras = getPreferenceData(MONITOR_BATERIA_ARRANCAR_AL_INICIO);
        return (letras.length() > 0);
    }

    /**
     * Método que guarda en SharedPreferences el dato de nivel de batería para dar una alerta
     * de poca batería.
     * @param nivelAlerta Valor entero que corresponde al % de batería al que se debe alertar.
     */
    public void escribePreferenciasBateriaNivelAlerta(int nivelAlerta)
    {
        setPreferenceData(MONITOR_BATERIA_NIVEL_ALERTA, String.valueOf(nivelAlerta));
    }

    /**
     * Método que lee el el dato de nivel de batería para dar una alerta almacenado en SharedPreferences.
     * @return Valor entero que corresponde al % de batería al que se debe alertar. 0 si no existe el dato.
     */
    public int damePreferenciasBateriaNivelAlerta()
    {
        CharSequence letras = getPreferenceData(MONITOR_BATERIA_NIVEL_ALERTA);
        if(letras.length()>0)
            return Integer.parseInt(letras.toString());
        else
            return 0;
    }

    /**
     * Método que guarda en SharedPreferences el dato de tasa de refresco de los datos de batería,
     * es decir, la frecuencia con la que se mira el estado de la batería.
     * @param tasaRefresco Valor entero que corresponde al nº de eventos de batería a ignorar antes
     *                     de pedir nueva información de estado.
     */
    public void escribePreferenciasBateriaTasaRefresco(int tasaRefresco)
    {
        setPreferenceData(MONITOR_BATERIA_TASA_REFRESCO, String.valueOf(tasaRefresco));
    }

    /**
     * Método getter que lee el dato de tasa de refresco del estado de la batería de SharedPreferences.
      * @return Entero con el valor de la tasa de refresco.
     */
    public int damePreferenciasBateriaTasaRefresco()
    {
        CharSequence letras = getPreferenceData(MONITOR_BATERIA_TASA_REFRESCO);
        if(letras.length()>0)
            return Integer.parseInt(letras.toString());
        else
            return 0;
    }

    /**
     * Método que guarda en SharedPreferences el dato que indica si hay que activar el monitor de
     * batería
     * @param activarAlInicio true que indica que hay que activar el monitor de batería al iniciar la app.
     *                        false en otro caso.
     */
    public void escribePreferenciasBateriaActivarAlInicio(boolean activarAlInicio)
    {
        setPreferenceData(MONITOR_BATERIA_ARRANCAR_AL_INICIO, String.valueOf(activarAlInicio));
    }


    /**
     * Método que lee de SharedPreferences el dato que indica si hay que activar el monitor de
     * batería al iniciar la app.
     * @return true si hay que activar el monitor de batería al iniciar la app, false en otro caso.
     */
    public boolean damePreferenciasBAteriaActivarAlInicio()
    {
        return (getPreferenceData(MONITOR_BATERIA_ARRANCAR_AL_INICIO).length() > 0 &&
            Boolean.parseBoolean(getPreferenceData(MONITOR_BATERIA_ARRANCAR_AL_INICIO)));
    }

    /**
     * Método que guarda en SharedPreferences el datos del último nivel de batería conocido.
     * @param ultimoNivel Entero con el último nivel de carga de la batería.
     */
    public void escribeUltimoNivelRegistradoBateria(CharSequence ultimoNivel)
    {
        setPreferenceData(MONITOR_BATERIA_ULTIMO_NIVEL_REGISTRADO, ultimoNivel.toString());
    }

    /////////////////////////////////////////////////////////
    // METODOS PARA EL MANOS LIBRES
    /////////////////////////////////////////////////////////

    /**
     * Getter. Lee de SharedPreferences la configuración de activar al inicio del manos libres.
     * @return true si se ha de iniciar con la app, false en otro caso.
     */
    public boolean getActivarManosLibresAlInicio()
    {
        return (new AppSharedPreferences().getPreferenceData(MANOS_LIBRES_ACTIVAR_AL_INICIO).length() > 0 &&
                Boolean.parseBoolean(getPreferenceData(MANOS_LIBRES_ACTIVAR_AL_INICIO)));
    }

    /**
     * Setter. Escribe en SharedPreferences la configuración de activar al inicio del manos libres.
     * @param activarAlInicio true si se ha de iniciar con la app, false en otro caso.
     */
    public void setActivarManosLibresAlInicio(boolean activarAlInicio)
    {
        new AppSharedPreferences().setPreferenceData(MANOS_LIBRES_ACTIVAR_AL_INICIO,
                String.valueOf(activarAlInicio));
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////// METODOS GENERICOS CUALQUIER SHARED PREFERENCES //////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Establece un valor del SharedPreferences genérico. El fichero donde se guarda está en
     * las constantes APP_SHARED_PREFERENCES_FILE
     *
     * @param map
     * @param valor
     */
    public void setPreferenceData(String map, String valor) {
        SharedPreferences.Editor editor = GlobalData.getAppContext().getSharedPreferences(APP_SHARED_PREFERENCES_FILE, Context.MODE_MULTI_PROCESS).edit();
        editor.putString(map, valor);
        editor.commit();
    }

    /**
     * Eliminación de un valor mediante su establecimiento a cadena vacía
     * @param map
     */
    public void deletePreferenceData(String map) {
        setPreferenceData(map, "");
    }

    /**
     * Lee un valor de datos del SharedPreferences
     * @param map
     * @return
     */
    public String getPreferenceData(String map) {

        SharedPreferences prefs = GlobalData.getAppContext().getSharedPreferences(APP_SHARED_PREFERENCES_FILE, Context.MODE_MULTI_PROCESS);
        String value = prefs.getString(map, "");

        return value;
    }

    /**
     * ¿Existe ese valor del SharedAppPreferences? Comprobación distinas de ""
     * @param map
     * @return
     */
    public boolean hasPreferenceData(String map) {

        SharedPreferences prefs = GlobalData.getAppContext().getSharedPreferences(APP_SHARED_PREFERENCES_FILE, Context.MODE_MULTI_PROCESS);
        String value = prefs.getString(map, "");

        if (value.length() > 0) {
            return true;
        } else {
            return false;
        }
    }

    ///////////////////////////////////////////////
    // Métodos para el conteo de SMS: sólo para versión pilotaje
    ///////////////////////////////////////////////

    /** Mensajes SMS enviados **/
    public String getSmsEnviados() {
        return ( getPreferenceData(Constants.SMS_ENVIADOS_SHARED_PREFERENCES) );
    }

    /**
     * Aumentamos el número de SMS's enviados
     */
    public void incrementaSmsEnviado() {
        String mensajesEnviados = getPreferenceData(Constants.SMS_ENVIADOS_SHARED_PREFERENCES);

        int mEnviados;

        try {
            mEnviados = Integer.valueOf(mensajesEnviados);
            if ( mEnviados < Constants.LIMITE_CARACTERS_SMS ) {
                mEnviados = mEnviados + 1;
            } else {
                mEnviados = Constants.LIMITE_CARACTERS_SMS;
            }

        } catch (Exception e) {
            AppLog.e(TAG, "incrementaSmsEnviado", e);
            mEnviados = Constants.LIMITE_CARACTERS_SMS;
        }

        setPreferenceData(Constants.SMS_ENVIADOS_SHARED_PREFERENCES, String.valueOf(mEnviados) );
    }

    /*

    //Actualmente no está en uso

    public void decrementaSmsEnviado() {
        String mensajesEnviados = getPreferenceData(Constants.SMS_ENVIADOS_SHARED_PREFERENCES);

        int mEnviados;

        try {
            mEnviados = Integer.valueOf(mensajesEnviados);
            if ( mEnviados > 0) {
                mEnviados = mEnviados - 1;
            } else {
                mEnviados = 0;
            }
        } catch (Exception e) {
            AppLog.e(TAG, "incrementaSmsEnviado", e);
            mEnviados = Constants.LIMITE_CARACTERS_SMS;
        }

        setPreferenceData(Constants.SMS_ENVIADOS_SHARED_PREFERENCES, String.valueOf(mEnviados) );
    }*/
}