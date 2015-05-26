package com.local.android.teleasistenciaticplus.lib.phone;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.local.android.teleasistenciaticplus.lib.helper.AppLog;
import com.local.android.teleasistenciaticplus.modelo.GlobalData;

/**
 * Created by GAMO1J on 02/03/2015.
 * Clase para recuperar los datos del terminal
 */

public class PhoneData {

    private Context mContext; //Contexto privado que saco de la global de la aplicación
    private TelephonyManager tm; //

    //Datos importantes del teléfono
    private String phoneNumber;

    private String phoneImei;

    /**
     * Constructor
     */
    public PhoneData() {

        mContext = GlobalData.getAppContext();

        //Recuperamos el número de teléfono
        //En teléfonos sin SIM, no se puede obtener el numero de teléfono
        try {
            tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            phoneImei = tm.getDeviceId();
            AppLog.e("El IMEI es ",phoneImei);
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

    public String getPhoneImei() {
        return phoneNumber;
    }

}
