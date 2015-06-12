package com.fundacionmagtel.android.teleasistenciaticplus.act.debug;


import android.app.Activity;
import android.os.Bundle;


import com.fundacionmagtel.android.teleasistenciaticplus.R;

/**
 * Actividad de depuración para comprobar si funciona correctamente
 * la creación de elementos de GoogleMaps
 *
 * @author Juan Jose Ferres
 */
public class actDebugGoogleMaps extends Activity {

    /**
     * Metodo de framework onCreate
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_debug_googlemaps_fragment);
    }


}
