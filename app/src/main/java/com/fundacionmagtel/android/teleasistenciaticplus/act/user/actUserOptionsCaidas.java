package com.fundacionmagtel.android.teleasistenciaticplus.act.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.fundacionmagtel.android.teleasistenciaticplus.lib.detectorCaidas.ServicioMuestreador;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.helper.AppSharedPreferences;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.stats.StatsFileLogTextGenerator;
import com.fundacionmagtel.android.teleasistenciaticplus.modelo.Constants;
import com.fundacionmagtel.android.teleasistenciaticplus.R;

/**
 * Actividad que muestra las opciones para el módulo de Caidas
 * @author Antonio Salvador
 */

public class actUserOptionsCaidas extends Activity {

    private TextView texto;

    /**
     * Método de framework onCreate
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /////////////////////////////////////////////////////
        StatsFileLogTextGenerator.write("configuracion", "caidas");
        /////////////////////////////////////////////////////

        setContentView(R.layout.activity_act_user_options_caidas);
        getActionBar().setIcon(R.drawable.config_wheel);

        AppSharedPreferences userSharedPreferences = new AppSharedPreferences();

        CheckBox micheckbox=(CheckBox) findViewById(R.id.caidas_checkbox);
        texto = (TextView) findViewById(R.id.caidas_texto_estado);

        //marcar checkbox si esta configurado
        String valor=userSharedPreferences.getPreferenceData(Constants.DETECTOR_CAIDAS_ARRANCAR_AL_INICIO);
        if( valor.equals(Constants.DETECTOR_CAIDAS_ACTIVAR)){
            micheckbox.setChecked(true);
        }else{
            micheckbox.setChecked(false);
        }

        //comprueba si el servicio está iniciado o no.
        String valor_servicio=userSharedPreferences.getPreferenceData(Constants.DETECTOR_CAIDAS_SERVICIO_INICIADO);
        if(valor_servicio.equals("true")){
            texto.setText(R.string.caidas_texto_estado_activo);
        }else{
            texto.setText(R.string.caidas_texto_estado_inactivo);
        }


    }

    /**
     * Creación del menú
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_act_user_options_caidas, menu);
        return true;
    }

    /**
     * Ejecución de los comandos del menú
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_user_options_caidas_exit_app) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Metodo de framework onClick
     * @param v
     */
    public void onClick(View v){
        switch(v.getId()) {
            case R.id.caidas_checkbox:
                //modificar la cte que controla el inicio del servicio al comienzo de la app.
                AppSharedPreferences userSharedPreferences = new AppSharedPreferences();
                boolean checked = ((CheckBox) v).isChecked();
                if(checked){
                    userSharedPreferences.setPreferenceData(Constants.DETECTOR_CAIDAS_ARRANCAR_AL_INICIO,Constants.DETECTOR_CAIDAS_ACTIVAR);
                }else{
                    userSharedPreferences.setPreferenceData(Constants.DETECTOR_CAIDAS_ARRANCAR_AL_INICIO,Constants.DETECTOR_CAIDAS_DESACTIVAR);
                }
                break;
            case R.id.caidas_boton_arrancar:
                //arrancar el servicio ?
                Intent intentA=new Intent(this, ServicioMuestreador.class);
                startService(intentA);
                texto.setText(R.string.caidas_texto_estado_activo);
                break;
            case R.id.caidas_boton_parar:
                //parar el servicio ?
                Intent intentB=new Intent(this, ServicioMuestreador.class);
                stopService(intentB);
                texto.setText(R.string.caidas_texto_estado_inactivo);
                break;
        }
    }
}
