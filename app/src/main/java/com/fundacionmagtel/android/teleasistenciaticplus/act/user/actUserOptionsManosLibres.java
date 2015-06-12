package com.fundacionmagtel.android.teleasistenciaticplus.act.user;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.fundacionmagtel.android.teleasistenciaticplus.R;
import com.fundacionmagtel.android.teleasistenciaticplus.act.main.actMain;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.helper.AppLog;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.helper.AppSharedPreferences;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.sound.ManosLibres;

/**
 * Clase de configuración de usuario para el Modo Manos Libres.
 */
public class actUserOptionsManosLibres extends Activity
        implements CompoundButton.OnCheckedChangeListener
{
    private final String TAG = "actUserOptionsManosLibres";
    private ManosLibres ml;
    private Switch swInterruptor, swAlInicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_user_option_manos_libres);
        getActionBar().setIcon(R.drawable.config_wheel);

        // Obtengo el objeto ManosLibres de la actividad principal.
        ml = actMain.getInstance().getManosLibres();

        swInterruptor = (Switch)findViewById(R.id.sw_manos_libres_on_off);
        swInterruptor.setOnCheckedChangeListener(this);
        swInterruptor.setChecked(ml.estaActivo());
        swInterruptor.setText("Activado ");

        swAlInicio = (Switch)findViewById(R.id.sw_manos_libres_al_inicio);
        swAlInicio.setChecked(this.isActivarAlInicio());
        swAlInicio.setText("Activar al inicio ");

        AppLog.i(TAG + ".onCreate", "Manos Libres Activo: " + swInterruptor.isChecked() +
                ", Activar al Inicio: " + swAlInicio.isChecked());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_act_user_options_manos_libres, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.menu_user_options_manos_libres_guardar:
                guardaPreferencias();
                AppLog.i(TAG, "Preferencias guardadas");
                break;
            case R.id.menu_user_options_manos_libres_salida:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.sw_manos_libres_on_off:
                if(isChecked) {
                    ml.enchufaElAltavoz();
                    AppLog.i(TAG, "Manos Libres Activado.");
                }
                else {
                    ml.desenchufaElAltavoz();
                    AppLog.i(TAG, "Manos Libres Desactivado.");
                }
                break;
        }
    }

    /**
     * Método que guarda las preferencias de funcionamiento del Manos Libres que han sido
     * guardadas en SharedPreferences. Si no encuentra datos establece el valor por defecto.
     */
    private void guardaPreferencias()
    {
        // Obtengo si se debe iniciar el receiver con la actividad.
        new AppSharedPreferences().setActivarManosLibresAlInicio(swAlInicio.isChecked());
    }

    /**
     * Método que devuelve un booleano que indica si la última actialización del Fiat.
     */
    private boolean isActivarAlInicio() {
        return new AppSharedPreferences().getActivarManosLibresAlInicio();
    }

/*
    /////////////////////////////////////////////////////////
    // METODOS PARA EL MANOS LIBRES (APPSHAREDPREFERENCES)
    /////////////////////////////////////////////////////////
    public boolean getActivarManosLibresAlInicio()
    {
        return (new AppSharedPreferences().getPreferenceData(MANOS_LIBRES_ACTIVAR_AL_INICIO).length() > 0 &&
                Boolean.parseBoolean(getPreferenceData(MANOS_LIBRES_ACTIVAR_AL_INICIO)));
    }

    public void setActivarManosLibresAlInicio(boolean activarAlInicio)
    {
        new AppSharedPreferences().setPreferenceData(MANOS_LIBRES_ACTIVAR_AL_INICIO,
                String.valueOf(activarAlInicio));
    }

    ////////////////////////////////////////////////////////
    // MANOS LIBRES (CONSTANTS)
    ////////////////////////////////////////////////////////
    public static final String MANOS_LIBRES_ACTIVAR_AL_INICIO = "ManosLibresActivarAlInicio";

    ////////////////////////////////////////////////////////
    // MANOS LIBRES (STRINGS)
    ////////////////////////////////////////////////////////
    <!-- Relacionados con Monitor Bateria -->
    <string name="txt_estado_manos_libres">Estado del Modo Manos Libres</string>
    <string name="txt_menu_user_options_manos_libres_salida">Salida</string>

    <!-- Cadenas de configuración de usuario -->
    <string name="title_activity_act_user_options_monitor_bateria">Configuración > Monitor de Batería</string>
    */
}
