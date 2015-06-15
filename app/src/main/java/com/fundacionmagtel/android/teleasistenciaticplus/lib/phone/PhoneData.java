package com.fundacionmagtel.android.teleasistenciaticplus.lib.phone;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.fundacionmagtel.android.teleasistenciaticplus.lib.helper.AppLog;
import com.fundacionmagtel.android.teleasistenciaticplus.modelo.GlobalData;

/**
 * Created by GAMO1J on 02/03/2015.
 * Clase para recuperar los datos del terminal
 * @author Jose Manuel Galvez
 */

public class PhoneData {

    private Context mContext; //Contexto privado que saco de la global de la aplicación
    private TelephonyManager tm; //

    //Datos importantes del teléfono
    private String phoneNumber;

    private String phoneImei;

    /**
     * Constructor que obtiene datos como el IMEI.
     *
     * En la primera versión de la aplicación, la identificación del teléfono
     * se realizaba mediante el numero de teléfono. Dado los problemas de
     * phishing y privacidad que implicaba el que la aplciación tuviera acceso
     * al numero de teléfono del mismo, esto dejó de estar disponible con
     * posterioridad.
     *
     * Se pasó de usar el teléfono como identificador para que fuese el IMEI
     */
    public PhoneData() {

        mContext = GlobalData.getAppContext();

        //Recuperamos el número de teléfono
        //En teléfonos sin SIM, no se puede obtener el numero de teléfono
        try {
            tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            phoneImei = tm.getDeviceId();
            AppLog.e("El IMEI es ", phoneImei);
            //355179066263149 Sony
            //356570064378294 Samsung

        } catch (Exception e) {
            /*if (Constants.DEBUG_LEVEL == DebugLevel.DEBUG) {
                phoneNumber = "012345678";// Antonio Alameda
            }*/
            AppLog.e("PhoneData","Error recuperando el valor del terminal",e);
        }
        //TODO Eliminar el phoneNumber de la aplicación
        phoneNumber = phoneImei;

    }

    /**
     * Getter del IMEI del teléfono
     * @return IMEI del teléfono
     */
    public String getPhoneImei() {
        return phoneNumber;
    }

}
