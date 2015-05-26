package com.local.android.teleasistenciaticplus.act;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.local.android.teleasistenciaticplus.R;
import com.local.android.teleasistenciaticplus.act.main.actMain;
import com.local.android.teleasistenciaticplus.lib.helper.AppSharedPreferences;
import com.local.android.teleasistenciaticplus.modelo.Constants;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Ventana splash de inicio de la aplicación
 */

public class actLoadingScreen extends Activity implements Constants {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ////////////////////////////////////////////////////////
        /// Operaciones cosméticas sobre la UI de la actividad
        ////////////////////////////////////////////////////////
        View decorView2 = getWindow().getDecorView();
        // Oculta status bar
        //Para ocultar también el navigation bar, quitar la parte comentada
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN; // | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView2.setSystemUiVisibility(uiOptions);

        ////////////////////////////////////////////////////
        // LAYOUT
        // Creación de la pantalla de carga
        setContentView(R.layout.layout_loadingscreen);
        ////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////
        // CARGA DE LA SIGUIENTE ACTIVITY
        ////////////////////////////////////////////////////////////////////

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Intent mainIntent;

                String valorCargaPantallaAviso = new AppSharedPreferences().getPreferenceData(Constants.NOMBRE_APP_SHARED_PREFERENCES_NO_MOSTRAR_AVISO_TARIFICACION);

                // Se muestra la pantalla de aviso de tarificación de SMS si no la ha ocultado (valor sharedpreferences)
                if ( valorCargaPantallaAviso.length() > 0 ) { //el valor que se crea es "true"

                    mainIntent = new Intent().setClass(actLoadingScreen.this, actMain.class);

                } else {

                    mainIntent = new Intent().setClass(actLoadingScreen.this, actMensajeTarificacionExtraYPoliticaDeUso.class);

                }

                startActivity(mainIntent);

                if (Constants.SHOW_ANIMATION) {
                    overridePendingTransition(R.anim.animation2, R.anim.animation1);
                }
                // Cerramos la ventana de carga para que salga del BackStack
                finish();
            }
        };

        Timer timer = new Timer();
        timer.schedule(task, Constants.LOADING_SCREEN_DELAY);

    } //Fin onCreate

} // Fin actLoadingScreen