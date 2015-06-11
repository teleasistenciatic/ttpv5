package com.fundacionmagtel.android.teleasistenciaticplus.lib.sms;

import com.fundacionmagtel.android.teleasistenciaticplus.lib.helper.AppLog;
import com.fundacionmagtel.android.teleasistenciaticplus.modelo.TipoAviso;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.helper.AppSharedPreferences;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.stats.StatsFileLogTextGenerator;

/**
 * Created by GAMO1J on 20/04/2015.
 * Esta clase enviará automáticamente un SMS a las personas de contacto recogidas en las sharedpreferences
 */
public class SmsLauncher {

    TipoAviso aviso;

    public SmsLauncher(TipoAviso tipoAviso) {
        aviso = tipoAviso;
        AppLog.d("TAG", tipoAviso + "");
    }

    /**
     * @return Si la lista de contactos está vacia
     */
    public boolean generateAndSend() {

         AppSharedPreferences misAppSharedPreferences = new AppSharedPreferences();
         if ( ! misAppSharedPreferences.hasPersonasContacto() ) {
             return false; //Si no hay telefonos de contacto, devuelve falso
             //TODO integrar el dialog
         }

        //Recuperar la información de las personas de contacto
        String telefonos[] = misAppSharedPreferences.getPersonasContacto(); //(Nos valen la 1,3 y 5)

        //Generar el texto
        for (int i = 0; i <= 5; i++) {

            if  ( ( (i == 1) || (i == 3) || (i == 5) ) && telefonos[i].length()!=0 ) {
                //Envio del SMS
                // Se genera un aviso u otro en base a la clase que lo llama
                String textoSms = null;

                switch (aviso) {

                    case DUCHANOATENDIDA:
                        /////////////////////////////////////////////////////
                        StatsFileLogTextGenerator.write("aviso", "ducha");
                        /////////////////////////////////////////////////////

                        textoSms = new SmsTextGenerator().getTextGenerateSmsDucha( telefonos[i] );

                    break;

                    case AVISO:
                        /////////////////////////////////////////////////////
                        StatsFileLogTextGenerator.write("aviso", "alerta");
                        /////////////////////////////////////////////////////

                        textoSms = new SmsTextGenerator().getTextGenerateSmsAviso(telefonos[i]);

                        break;

                    case IAMOK:
                        /////////////////////////////////////////////////////
                        StatsFileLogTextGenerator.write("aviso", "tranquilidad");
                        /////////////////////////////////////////////////////

                        textoSms = new SmsTextGenerator().getTextGenerateSmsIamOK(telefonos[i]);

                        break;

                    case CAIDADETECTADA:
                        /////////////////////////////////////////////////////
                        StatsFileLogTextGenerator.write("aviso", "caida");
                        /////////////////////////////////////////////////////

                        //textoSms= new SmsTextGenerator().getTextGenerateSmsCaida(telefonos[i]);
                        break;

                    case SALIDAZONASEGURA:
                        /////////////////////////////////////////////////////
                        StatsFileLogTextGenerator.write("aviso", "zona segura");
                        /////////////////////////////////////////////////////

                        //textoSms= new SmsTextGenerator().getTextGenerateSmsSalidaZonaSegura( telefonos[i] );
                        break;

                    case SINBATERIA:
                        /////////////////////////////////////////////////////
                        StatsFileLogTextGenerator.write("aviso", "sin bateria");
                        /////////////////////////////////////////////////////

                        textoSms= new SmsTextGenerator().getTextGenerateSmsBateriaAgotada( telefonos[i] );
                        break;

                }

                ///////////////////////////////////////////////////////////////////////
                // Sólo se envía el SMS en los modos ducha, aviso, tranquilidad y batería
                // Eliminar tras el pilotaje
                switch (aviso) {

                    case DUCHANOATENDIDA:
                    case AVISO:
                    case IAMOK:
                    case SINBATERIA:

                        AppLog.i("TAG","Envío \"fisico\" del SMS");
                        new SmsDispatcher( telefonos[i], textoSms).send();
                        break;
                }
                /////////////////////////////////////////////////////////////////////////
            }
        }

        //enviar los SMS's
        // todo comprobador de correcto envio SMS
        return true;
    }

}
