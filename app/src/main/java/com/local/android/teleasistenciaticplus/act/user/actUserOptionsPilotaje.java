package com.local.android.teleasistenciaticplus.act.user;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.local.android.teleasistenciaticplus.R;
import com.local.android.teleasistenciaticplus.lib.helper.AppSharedPreferences;
import com.local.android.teleasistenciaticplus.modelo.Constants;

public class actUserOptionsPilotaje extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_user_option_pilotaje);

        TextView mensajeDisponibleText = (TextView) findViewById(R.id.user_options_pilotaje_mensajes_disponibles_text);
        String mensajesEnviados = new AppSharedPreferences().getSmsEnviados();

        mensajeDisponibleText.setText("El número de mensajes enviados es de " +
                mensajesEnviados + " ( de un máximo inicial de " +
                Constants.LIMITE_SMS_POR_DEFECTO + " )" ) ;
    }

    public void user_option_pilotaje_button_exit(View view) {
        finish();
    }

}
