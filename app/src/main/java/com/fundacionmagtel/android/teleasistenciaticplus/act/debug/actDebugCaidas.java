package com.fundacionmagtel.android.teleasistenciaticplus.act.debug;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.fundacionmagtel.android.teleasistenciaticplus.lib.detectorCaidas.ServicioMuestreador;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.helper.AppLog;
import com.fundacionmagtel.android.teleasistenciaticplus.modelo.Constants;
import com.fundacionmagtel.android.teleasistenciaticplus.R;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.helper.AppSharedPreferences;

/**
 * Actividad de depuración para el control de caidas
 * @author Antonio Salvador
 */

public class actDebugCaidas extends Activity {

    private Intent intent;
    private TextView texto;

    /**
     * Metodo onCreate
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_debug_caidas);

        //arrancar servicio que comprueba las caidas
        intent=new Intent(this, ServicioMuestreador.class);
        texto=(TextView) findViewById(R.id.textoEstadoServicio);

        //comprueba si el servicio está iniciado o no.
        AppSharedPreferences userSharedPreferences = new AppSharedPreferences();
        String valor_servicio=userSharedPreferences.getPreferenceData(Constants.DETECTOR_CAIDAS_SERVICIO_INICIADO);
        if(valor_servicio.equals("true")){
            texto.setText(R.string.caidas_texto_estado_activo);
        }else{
            texto.setText(R.string.caidas_texto_estado_inactivo);
        }

    }

    /**
     * Metodo que recoge las pulsaciones de la interfaz gráfica de la interfaz y actua en consecuencia
     * @param v
     */
    public void onClick(View v){
        switch(v.getId()){
            case R.id.button1:
                startService(intent);
                texto.setText(R.string.caidas_texto_estado_activo);
                break;
            case R.id.button2:
                boolean que=stopService(intent);
                AppLog.i("CAIDAS", "caidas servicio parado? " + que);
                texto.setText(R.string.caidas_texto_estado_inactivo);
                break;

        }
    }
}
