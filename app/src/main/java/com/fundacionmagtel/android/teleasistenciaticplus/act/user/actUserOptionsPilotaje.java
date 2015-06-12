package com.fundacionmagtel.android.teleasistenciaticplus.act.user;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.fundacionmagtel.android.teleasistenciaticplus.lib.helper.AppSharedPreferences;
import com.fundacionmagtel.android.teleasistenciaticplus.modelo.Constants;
import com.fundacionmagtel.android.teleasistenciaticplus.R;

/**
 * Actividad con las opciones necesarias en el pilotaje
 * @author Juan Jose Ferres
 */
public class actUserOptionsPilotaje extends Activity {

    /**
     * Método de framework
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_user_option_pilotaje);
        getActionBar().setIcon(R.drawable.config_wheel);

        TextView mensajeDisponibleText = (TextView) findViewById(R.id.user_options_pilotaje_mensajes_disponibles_text);
        String mensajesEnviados = new AppSharedPreferences().getSmsEnviados();

        mensajeDisponibleText.setText("El número de mensajes enviados es de " +
                mensajesEnviados + " ( de un máximo inicial de " +
                Constants.LIMITE_SMS_POR_DEFECTO + " )" ) ;
    }

    /**
     * Botón de salida
     * @param view
     */
    public void user_option_pilotaje_button_exit(View view) {
        finish();
    }

}
