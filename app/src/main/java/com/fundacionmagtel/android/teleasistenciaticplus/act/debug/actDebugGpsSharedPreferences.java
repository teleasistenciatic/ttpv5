package com.fundacionmagtel.android.teleasistenciaticplus.act.debug;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.fundacionmagtel.android.teleasistenciaticplus.lib.helper.AppSharedPreferences;
import com.fundacionmagtel.android.teleasistenciaticplus.R;

/**
 * Actividad para controlar en depuración las appSharedPreferences
 * presentes en el sistema y así poder borrarlas.
 * @author Juan Jose Ferres
 */
public class actDebugGpsSharedPreferences extends Activity {

    /**
     * Metodo de framework onCreate
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_debug_gps_shared_preferences);

        TextView miTextView = (TextView) findViewById(R.id.texto_debug_gps);

        //Leer los ultimos valores y mostrado
        AppSharedPreferences miAppSharedPreferences = new AppSharedPreferences();

        String[] gpsPos = miAppSharedPreferences.getGpsPos();
        miTextView.setText( "Latitud: " + gpsPos[0] + '\n' +
                            "Longitud: " + gpsPos[1] + '\n' +
                            "Precisión: " + gpsPos[2] + '\n' +
                            "Fecha de ese valor: " + gpsPos[3] + '\n'
                        );
    }


}
