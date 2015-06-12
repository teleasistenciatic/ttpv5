package com.fundacionmagtel.android.teleasistenciaticplus.act.debug;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.fundacionmagtel.android.teleasistenciaticplus.modelo.Constants;
import com.fundacionmagtel.android.teleasistenciaticplus.R;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.helper.AppSharedPreferences;

/**
 * Actividad que muestra la memoria disponible y usada en el terminal
 *
 * @author Juan Jose Ferres
 */

public class actDebugUserConfig extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_debug_user_config);
    }

    /**
     * Borrado de las appSharedPreferences nombre y apellidos
     *
     * @param view
     */
    public void user_config_borrar_nombre_apellidos_button(View view) {

        new AppSharedPreferences().deleteUserData();
        Toast.makeText(getBaseContext(), "OK",
                Toast.LENGTH_SHORT).show();

    }

    /**
     * Borrado de personas de contacto
     *
     * @param view
     */
    public void user_config_borrar_personas_contacto_button(View view) {

        new AppSharedPreferences().deletePersonasContacto();
        Toast.makeText(getBaseContext(), "OK",
                Toast.LENGTH_SHORT).show();

    }

    /**
     * Borrado de las appSharedPreferences de numero de SMS enviados
     *
     * @param view
     */

    public void user_config_reiniciar_sms_enviados(View view) {

        new AppSharedPreferences().setPreferenceData(Constants.SMS_ENVIADOS_SHARED_PREFERENCES, String.valueOf(0));

        Toast.makeText(getBaseContext(), "OK",
                Toast.LENGTH_SHORT).show();

    }

    /**
     * Borrado de las appSharedPreferences de aviso tarificacion
     *
     * @param view
     */
    public void user_config_borrar_appshared_aviso_tarificacion(View view) {

        new AppSharedPreferences().deletePreferenceData(Constants.NOMBRE_APP_SHARED_PREFERENCES_NO_MOSTRAR_AVISO_TARIFICACION);

        Toast.makeText(getBaseContext(), "OK",
                Toast.LENGTH_SHORT).show();

    }

    /**
     * Borrado de las appSharedPreferences de ultimo aviso enviado
     *
     * @param view
     */

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
