package com.local.android.teleasistenciaticplus.act.debug;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.local.android.teleasistenciaticplus.R;
import com.local.android.teleasistenciaticplus.lib.helper.AppSharedPreferences;

public class actDebugGpsSharedPreferences extends Activity {

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
