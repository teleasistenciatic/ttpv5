package com.local.android.teleasistenciaticplus.lib.networking;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.local.android.teleasistenciaticplus.modelo.GlobalData;

public class Networking {

    /**
     * F8 sirve para activar los datos en el emulador
     * @return si existe conexión o no
     */
    public static boolean isConnectedToInternet() {
        Context mContext = GlobalData.getAppContext();
        try {
            ConnectivityManager cm =
                    (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null &&
                    activeNetwork.isAvailable() &&
                    activeNetwork.isConnected();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * La red activa no es null
     * @return boolean
     */
    public static boolean activeNetworkNotNull() {
        Context mContext = GlobalData.getAppContext();

        try {
            ConnectivityManager cm =
                    (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            if ( activeNetwork != null ) {
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * La red activa está conectada
     * @return boolean
     */
    public static boolean activeNetworkIsConnected() {
        Context mContext = GlobalData.getAppContext();

        try {
            ConnectivityManager cm =
                    (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            return activeNetwork.isConnected();

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * La red activa está disponible
     * @return boolean
     */
    public static boolean activeNetworkIsAvailable() {
        Context mContext = GlobalData.getAppContext();

        try {
            ConnectivityManager cm =
                    (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            return activeNetwork.isAvailable();

        } catch (Exception e) {
            return false;
        }
    }

}
