package com.fundacionmagtel.android.teleasistenciaticplus.act;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.fundacionmagtel.android.teleasistenciaticplus.act.main.actMain;
import com.fundacionmagtel.android.teleasistenciaticplus.modelo.Constants;
import com.fundacionmagtel.android.teleasistenciaticplus.R;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.helper.AppSharedPreferences;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.stats.StatsFileLogTextGenerator;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Actividad que muestra una ventana splash de inicio de la aplicación
 * El tiempo que la animación se está ejecutando está dentro de las constantes.
 *
 * En la primera versión de la aplicación que se comunicaba con el servidor, este
 * tiempo tenía más uso práctico al comprobar por varias veces que el servidor
 * se encontraba en disposición de recibir peticiones.
 *
 * @author Juan Jose Ferres
 */

public class actLoadingScreen extends Activity implements Constants {


    /** Método de framework onCreate
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /////////////////////////////////////////////////////
        StatsFileLogTextGenerator.write("app", "pantalla de carga");
        /////////////////////////////////////////////////////

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