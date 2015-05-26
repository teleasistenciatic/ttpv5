package com.local.android.teleasistenciaticplus.act.debug;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.local.android.teleasistenciaticplus.R;
import com.local.android.teleasistenciaticplus.lib.detectorCaidas.ServicioMuestreador;
import com.local.android.teleasistenciaticplus.lib.helper.AppLog;
import com.local.android.teleasistenciaticplus.lib.helper.AppSharedPreferences;
import com.local.android.teleasistenciaticplus.modelo.Constants;

public class actDebugCaidas extends Activity {



    private Intent intent;
    private TextView texto;

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

    public void onClick(View v){
        switch(v.getId()){
            case R.id.button1:
                startService(intent);
                texto.setText(R.string.caidas_texto_estado_activo);
                break;
            case R.id.button2:
                boolean que=stopService(intent);
                AppLog.i("CAIDAS","caidas servicio parado? "+que);
                texto.setText(R.string.caidas_texto_estado_inactivo);
                break;

        }
    }
}
