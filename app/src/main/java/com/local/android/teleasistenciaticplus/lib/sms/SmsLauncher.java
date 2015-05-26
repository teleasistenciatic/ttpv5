package com.local.android.teleasistenciaticplus.lib.sms;

import com.local.android.teleasistenciaticplus.lib.helper.AppLog;
import com.local.android.teleasistenciaticplus.lib.helper.AppSharedPreferences;
import com.local.android.teleasistenciaticplus.modelo.TipoAviso;

/**
 * Created by GAMO1J on 20/04/2015.
 * Esta clase enviará automáticamente un SMS a las personas de contacto recogidas en las sharedpreferences
 */
public class SmsLauncher {

    TipoAviso aviso;

    public SmsLauncher(TipoAviso tipoAviso) {
        aviso = tipoAviso;
        AppLog.d("TAG",tipoAviso + "");
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

            if  ( (i == 1) || (i == 3) || (i == 5) ) {
                //Envio del SMS
                // Se genera un aviso u otro en base a la clase que lo llama
                String textoSms = null;

                switch (aviso) {

                    case DUCHANOATENDIDA:
                        textoSms = new SmsTextGenerator().getTextGenerateSmsDucha( telefonos[i] );
                    break;

                    case AVISO:
                        textoSms = new SmsTextGenerator().getTextGenerateSmsAviso(telefonos[i]);
                        break;

                    case IAMOK:
                        textoSms = new SmsTextGenerator().getTextGenerateSmsIamOK(telefonos[i]);
                        break;

                    case CAIDADETECTADA:
                        textoSms= new SmsTextGenerator().getTextGenerateSmsCaida(telefonos[i]);
                        break;

                    case SALIDAZONASEGURA:
                        textoSms= new SmsTextGenerator().getTextGenerateSmsSalidaZonaSegura( telefonos[i] );
                        break;
                }

                //Envío "fisico" del SMS
                new SmsDispatcher( telefonos[i], textoSms).send();
            }

        }

        //enviar los SMS's

        return true;
    }

}
