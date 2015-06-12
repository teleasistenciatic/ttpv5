package com.fundacionmagtel.android.teleasistenciaticplus.act.user;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.fundacionmagtel.android.teleasistenciaticplus.act.zonasegura.actZonaSeguraHomeSet;
import com.fundacionmagtel.android.teleasistenciaticplus.act.zonasegura.serviceZonaSegura;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.helper.AppLog;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.helper.AppSharedPreferences;
import com.fundacionmagtel.android.teleasistenciaticplus.modelo.Constants;
import com.fundacionmagtel.android.teleasistenciaticplus.R;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.stats.StatsFileLogTextGenerator;

public class actUserOptionsZonaSegura extends Activity implements ServiceConnection, Constants {

    private TextView texto;
    String TAG = "actUserOptionsZonaSegura";

    //private ServiceConnection mConnection = this; //Solo para el Bind

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_user_options_zona_segura);
        getActionBar().setIcon(R.drawable.config_wheel);

        /////////////////////////////////////////////////////
        StatsFileLogTextGenerator.write("configuracion", "zona segura");
        /////////////////////////////////////////////////////

        // Comprobacion de que la zona segura este activa o no

        AppSharedPreferences userSharedPreferences = new AppSharedPreferences();

        CheckBox micheckbox = (CheckBox) findViewById(R.id.zona_segura_checkbox);
        texto = (TextView) findViewById(R.id.zona_segura_texto_estado_servicio);

        String valor = userSharedPreferences.getPreferenceData(Constants.ZONA_SEGURA_ARRANCAR_AL_INICIO);

        if (valor.equals("true")) {
            micheckbox.setChecked(true);
        } else {
            micheckbox.setChecked(false);
        }

        String servicioIniciado = userSharedPreferences.getPreferenceData(Constants.ZONA_SEGURA_SERVICIO_INICIADO);

        if (servicioIniciado.equals("true")) {
            texto.setText(R.string.zona_segura_texto_estado_activo);
        } else {
            texto.setText(R.string.zona_segura_texto_estado_inactivo);
        }
    }

    ///////////////////////////// LISTENERS ///////////////////////////////

    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.zona_segura_establecer_home:

                //Llamamos a la actividad que hace aparecer el mapa Zona Segura Set Home
                Intent newIntent;
                newIntent = new Intent().setClass(this, actZonaSeguraHomeSet.class);
                startActivity(newIntent);
                break;

            case R.id.zona_segura_checkbox:

                //modificar la cte que controla el inicio del servicio al comienzo de la app.
                AppSharedPreferences userSharedPreferences = new AppSharedPreferences();

                boolean checked = ((CheckBox) v).isChecked();
                if (checked) {
                    userSharedPreferences.setPreferenceData(Constants.ZONA_SEGURA_ARRANCAR_AL_INICIO, "true");
                } else {
                    userSharedPreferences.setPreferenceData(Constants.ZONA_SEGURA_ARRANCAR_AL_INICIO, "false");
                }
                break;

            case R.id.zona_segura_boton_arrancar:

                /////////////////////// ARRANCAR EL SERVICIO ////////////////////////////////
                //bindService(new Intent(this, serviceZonaSegura.class), mConnection, getApplicationContext().BIND_AUTO_CREATE);

                Intent intentA=new Intent(this, serviceZonaSegura.class);
                startService(intentA);

                AppLog.d(TAG, "Servicio Iniciado");
                texto.setText(R.string.zona_segura_texto_estado_activo);
                //////////////////////////////////////////////////////////////////////////

                break;

            case R.id.zona_segura_boton_parar:

                /////////////////////// PARAR EL SERVICIO ////////////////////////////////

                /*unbindService(mConnection);*/
                Intent intent = new Intent(this, serviceZonaSegura.class);
                stopService(intent);

                AppLog.d(TAG,"Servicio detenido");
                texto.setText(R.string.zona_segura_texto_estado_inactivo);
                //////////////////////////////////////////////////////////////////////////

                break;
        }
    }

    /*            case R.id.caidas_boton_arrancar:
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
                break;*/

    ///////////////////////////// MENUS ///////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_act_user_options_zona_segura, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_user_options_zona_segura_exit_app) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        AppLog.d(TAG, "Servicio de Zona Segura iniciado");
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        AppLog.d(TAG, "Servicio de Zona Segura desconectado");
    }

    public static Context getContext() {
        return getContext();
    }
}
