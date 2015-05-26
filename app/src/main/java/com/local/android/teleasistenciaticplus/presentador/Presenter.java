package com.local.android.teleasistenciaticplus.presentador;

import android.content.Context;
import com.local.android.teleasistenciaticplus.modelo.GlobalData;

/**
 * Created by FESEJU on 11/02/2015.
 * Clase presentadora
 */

public class Presenter {

    /**
     * Devuelve el contexto principal de la aplicación. Se genera al ejecutar el programa
     * y se almacena estáticamente en la clase GLobalData
     * @return context (Contexto de la aplicación (BIG) )
     */
    public static Context getAppContext() {
        Context context;
        context = GlobalData.getAppContext(); //singleton estático

        return context;
    }
}
