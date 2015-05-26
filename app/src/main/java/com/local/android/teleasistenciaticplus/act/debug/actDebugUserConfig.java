package com.local.android.teleasistenciaticplus.act.debug;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.local.android.teleasistenciaticplus.R;
import com.local.android.teleasistenciaticplus.lib.helper.AppSharedPreferences;
import com.local.android.teleasistenciaticplus.modelo.Constants;

public class actDebugUserConfig extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_debug_user_config);
    }

    public void user_config_borrar_nombre_apellidos_button(View view) {

        new AppSharedPreferences().deleteUserData();
        Toast.makeText(getBaseContext(), "OK",
                Toast.LENGTH_SHORT).show();

    }

    public void user_config_borrar_personas_contacto_button(View view) {

        new AppSharedPreferences().deletePersonasContacto();
        Toast.makeText(getBaseContext(), "OK",
                Toast.LENGTH_SHORT).show();

    }

    public void user_config_borrar_appshared_aviso_tarificacion(View view) {

        new AppSharedPreferences().deletePreferenceData(Constants.NOMBRE_APP_SHARED_PREFERENCES_NO_MOSTRAR_AVISO_TARIFICACION);

        Toast.makeText(getBaseContext(), "OK",
                Toast.LENGTH_SHORT).show();

    }

    public void user_config_borrar_appshared_ultimo_aviso_enviado(View view) {

        new AppSharedPreferences().deletePreferenceData(Constants.NOMBRE_APP_SHARED_PREFERENCES_DATETIME_ULTIMO_SMS_ENVIADO);

        Toast.makeText(getBaseContext(), "OK",
                Toast.LENGTH_SHORT).show();

    }


    /**
     * Salida de la aplicación al pulsar el botón de salida del layout
     *
     * @param view vista
     */
    public void exit_button(View view) {
        finish();
    }

}
