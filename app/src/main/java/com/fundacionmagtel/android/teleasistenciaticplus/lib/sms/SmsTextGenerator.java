package com.fundacionmagtel.android.teleasistenciaticplus.lib.sms;

import com.fundacionmagtel.android.teleasistenciaticplus.lib.helper.AppLog;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.helper.AppTime;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.sanitize.DataSanitize;
import com.fundacionmagtel.android.teleasistenciaticplus.modelo.Constants;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.helper.AppSharedPreferences;
import com.fundacionmagtel.android.teleasistenciaticplus.modelo.DebugLevel;

/**
 * Created by FESEJU on 25/03/2015.
 * Clase helper encargada de generar el texto de los mensajes SMS que luego enviará SmsDispatcher
 * @author Juan Jose Ferres
 */
public class SmsTextGenerator {

    String nombre;
    String apellidos;
    String nombreApp = "TELEASISTENCI@TIC+";
    final String TAG = "SmsTextGenerator";

    public SmsTextGenerator() {

        String[] nombreApellidos = new AppSharedPreferences().getUserData();
        String[] gps = new AppSharedPreferences().getGpsPos();
        //Para evitar los problemas al enviar SMS's, se eliminan los caracteres como los acentos

        DataSanitize miDataSanitize = new DataSanitize();

        nombre = miDataSanitize.cambiaCaracteresEspanolesPorIngleses(nombreApellidos[0]);
        apellidos = miDataSanitize.cambiaCaracteresEspanolesPorIngleses(nombreApellidos[1]);

        nombre = miDataSanitize.trimStringSize(nombre, Constants.MAX_NAME_SIZE);
        apellidos = miDataSanitize.trimStringSize(apellidos, Constants.MAX_APELLIDOS_SIZE);

    }

    /**
     * Genera el texto de un SMS que contiene el aviso de que el usuario se encuentra bien.
     * @param phoneNumberDestination El número de teléfono del destinatario del SMS. No usado en la versión actual.
     * @return Objeto String con el texto generado.
     */
    // TODO phoneNumberDestination
    public String getTextGenerateSmsIamOK(String phoneNumberDestination) {
        // Andres García comunica que se encuentra bien a las 12:00 del día 12/03/2015 en la posición GPS

        String smsBodyText = nombreApp + ": " + nombre + " " + apellidos + " comunica que se encuentra bien a las";

        smsBodyText = addDateText(smsBodyText);
        smsBodyText = addGpsText(smsBodyText);
        smsBodyText = addDebugText(smsBodyText);

        return smsBodyText;
    }

    /**
     * Genera el texto de un SMS de un aviso genérico.
     * @param phoneNumberDestination El número de teléfono del destinatario del SMS. No usado en la versión actual.
     * @return Objeto String con el texto generado.
     */
    // TODO phoneNumberDestination
    public String getTextGenerateSmsAviso(String phoneNumberDestination) {
        // Andres García comunica que se encuentra bien a las 12:00 del día 12/03/2015

        String smsBodyText = nombreApp + ": " + nombre + " " + apellidos + " genera aviso";

        smsBodyText = addDateText(smsBodyText);
        smsBodyText = addGpsText(smsBodyText);
        smsBodyText = addDebugText(smsBodyText);
        return smsBodyText;
    }

    /**
     * Genera el texto de un SMS que contiene el aviso de alerta de modo ducha no desactivado.
     * @param phoneNumberDestination El número de teléfono del destinatario del SMS. No usado en la versión actual.
     * @return Objeto String con el texto generado.
     */
    // TODO phoneNumberDestination
    public String getTextGenerateSmsDucha(String phoneNumberDestination) {

        String smsBodyText = nombreApp + ": " + nombre + " " + apellidos + " genera aviso de ducha";

        smsBodyText = addDateText(smsBodyText);
        smsBodyText = addGpsText(smsBodyText);
        smsBodyText = addDebugText(smsBodyText);

        return smsBodyText;

    }

    /**
     * Genera el texto de un SMS que contiene un aviso de que el sistema ha detectado una caída.
     * @param phoneNumberDestination El número de teléfono del destinatario del SMS. No usado en la versión actual.
     * @return Objeto String con el texto generado.
     */
    // TODO phoneNumberDestination
    public String getTextGenerateSmsCaida(String phoneNumberDestination){
        String smsBodyText = nombreApp + ": " + nombre + " " + apellidos + " genera aviso de caida";

        smsBodyText = addDateText(smsBodyText);
        smsBodyText = addGpsText(smsBodyText);
        smsBodyText = addDebugText(smsBodyText);

        return smsBodyText;
    }

    /**
     * Genera el texto de un SMS que contiene un aviso de salida de la Zona Segura.
     * @param phoneNumberDestination El número de teléfono del destinatario del SMS. No usado en la versión actual.
     * @return Objeto String con el texto generado.
     */
    // TODO phoneNumberDestination
    public String getTextGenerateSmsSalidaZonaSegura(String phoneNumberDestination){
        String smsBodyText = nombreApp + ": " + nombre + " " + apellidos + " genera aviso de salida de zona segura";

        smsBodyText = addDateText(smsBodyText);
        smsBodyText = addGpsText(smsBodyText);
        smsBodyText = addDebugText(smsBodyText);

        return smsBodyText;
    }

    /**
     * Genera el texto de un SMS que contiene el aviso de batería a punto de agotarse.
     * @param phoneNumberDestination El número de teléfono del destinatario del SMS. No usado en la versión actual.
     * @return Objeto String con el texto generado.
     */
    // TODO phoneNumberDestination
    public String getTextGenerateSmsBateriaAgotada(String phoneNumberDestination) {
        String smsBodyText = nombreApp + ": " + nombre + " " + apellidos + " genera aviso de bateria agotada";

        smsBodyText = addDateText(smsBodyText);
        smsBodyText = addGpsText(smsBodyText);
        smsBodyText = addDebugText(smsBodyText);

        return smsBodyText;
    }

    /**
     * Añadimos la fecha al mensaje
     * @param mensaje
     * @return
     */
    private String addDateText(String mensaje) {

        String currentDate = new AppTime("dd/MM/yy:HH:mm:ss").getTimeDate();

        return mensaje + " " + currentDate;

    }

    /**
     * Genera el mensaje anexo de posición GPS en el caso de disponer de esa información
     * @return
     */
    private String addGpsText(String mensaje) {

        AppSharedPreferences miAppSharedPreferences = new AppSharedPreferences();

        String informacionGps = null;
        String[] valorGpsEnAppSharedPreferences;


        if ( miAppSharedPreferences.hasGpsPos() ) {

            valorGpsEnAppSharedPreferences = miAppSharedPreferences.getGpsPos();

            //////////////////////////////////////////////////////////////
            //Sólo añadimos la posicion GPS si los datos son recientes
            //////////////////////////////////////////////////////////////

            long tiempoActual = System.currentTimeMillis();
            long tiempoUltimaActualizacionGps = 0;

            //Leemos de las sharedpreferences, pero puede ser cero
            String tiempoUltimaActualizacionGpsEnCadena = valorGpsEnAppSharedPreferences[4];

            //Si la cadena es vacía
            if ( tiempoUltimaActualizacionGpsEnCadena.length() != 0 ) {

                try {
                    tiempoUltimaActualizacionGps = Long.valueOf(valorGpsEnAppSharedPreferences[4]);
                } catch (Exception e) {
                    AppLog.e(TAG, "Error al convertir long", e);
                }
            }

            Long diferenciaTemporalGpsEnSegundos = (tiempoActual - tiempoUltimaActualizacionGps) / 1000; //de milidegundos a segundos;

            if ( diferenciaTemporalGpsEnSegundos < Constants.MAX_GPS_TIME ) {

                informacionGps = " en posicion ( " + "https://maps.google.com/?q="
                        + valorGpsEnAppSharedPreferences[0] + ","
                        + valorGpsEnAppSharedPreferences[1] + " )";

            }
            ///////////////////////////////////////////////////////////////

        }

        if ( informacionGps != null) {
            return mensaje + informacionGps;
        } else {
            return mensaje;
        }
    }

    /**
     * Si estamos en modo depuración se envía el numero de caracteres
     * @param mensaje
     * @return
     */
    private String addDebugText(String mensaje) {
        if ( Constants.DEBUG_LEVEL == DebugLevel.DEBUG ) {
            return mensaje + " (SMS:" + mensaje.length() + ")";
         } else {
            return mensaje;
        }
    }

}