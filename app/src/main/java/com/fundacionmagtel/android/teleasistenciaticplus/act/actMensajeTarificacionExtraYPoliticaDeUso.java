package com.fundacionmagtel.android.teleasistenciaticplus.act;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.fundacionmagtel.android.teleasistenciaticplus.modelo.Constants;
import com.fundacionmagtel.android.teleasistenciaticplus.R;
import com.fundacionmagtel.android.teleasistenciaticplus.act.main.actMain;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.helper.AppSharedPreferences;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.stats.StatsFileLogTextGenerator;

/**
 * Actividad que muestra una ventaja de información sobre la posible tarificación
 * asociada al envio de SMS. Esta ventaja se puede ocultar si el usuario/a
 * así lo indica mediante un check box que se refleja en las AppSharedPreferences
 */
public class actMensajeTarificacionExtraYPoliticaDeUso extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_act_mensaje_tarificacion_extra);

        /////////////////////////////////////////////////////
        StatsFileLogTextGenerator.write("app", "pantalla de tarificacion extra");
        /////////////////////////////////////////////////////
    }

    /**
     * Pulsado del boton de aceptar advertencia de tarificacion adicional
     *
     * @param view Vista del botón
     */
    public void aceptar_advertencia_tarificacion_button(View view) {

        // Si ha establecido el valor de "no volver a mostrar" se guarda un flagg en las AppSharedPreferences
        boolean noMostrarPantallaAvisoTarificacion = ((CheckBox) findViewById(R.id.act_mensaje_tarificacion_no_volver_a_mostrar)).isChecked();

        if ( noMostrarPantallaAvisoTarificacion ) {
            new AppSharedPreferences().setPreferenceData(Constants.NOMBRE_APP_SHARED_PREFERENCES_NO_MOSTRAR_AVISO_TARIFICACION,"true");
        }

        //Cargamos la aplicación principal
        Intent mainIntent;
        mainIntent = new Intent().setClass(actMensajeTarificacionExtraYPoliticaDeUso.this, actMain.class);
        startActivity(mainIntent);

        finish();
    }

}