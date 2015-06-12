package com.fundacionmagtel.android.teleasistenciaticplus.act.debug;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

import com.fundacionmagtel.android.teleasistenciaticplus.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Actividad de depuración para comprobar si funciona correctamente
 * el sistema de Google Services
 *
 * @author Juan Jose Ferres
 */

public class actDebugGoogleServices extends Activity {

    /**
     * Metodo de framework onCreate
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_debug_google_services);

        ////////////////////////////////////////////////////////
        // Comprobación de que los Google Services están funcionando
        ////////////////////////////////////////////////////////

        EditText googleServiceTextIsActivated = (EditText) findViewById(R.id.edit_string_google_services_output);

        int googleServices = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (googleServices == ConnectionResult.SUCCESS) {
            googleServiceTextIsActivated.setText("Google Services ONLINE");
        } else {
            googleServiceTextIsActivated.setText("Google Services OFFLINE");
        }
    }
}
