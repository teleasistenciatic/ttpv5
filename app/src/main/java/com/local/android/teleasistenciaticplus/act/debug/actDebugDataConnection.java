package com.local.android.teleasistenciaticplus.act.debug;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.local.android.teleasistenciaticplus.R;
import com.local.android.teleasistenciaticplus.lib.networking.Networking;

public class actDebugDataConnection extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_debug_data_connection);
    }

    /**
     * Fin de la activity
     * @param view vista
     */
    public void exit_button(View view) {
        finish();
    }

    /**
     * Comprueba si hay conexión de datos y lo muestra con color de fondo en una caja de introducción de texto
     * @param view vista
     */
    public void main_debug_data_connection_check(View view) {

        TextView dataConnection = (TextView) findViewById(R.id.edit_check_data_connection);
        TextView activeNetwork = (TextView) findViewById(R.id.edit_check_data_connection_active_network);
        TextView activeNetworkAvailable = (TextView) findViewById(R.id.edit_check_data_connection_active_network_is_available);
        TextView activeNetworkConnected = (TextView) findViewById(R.id.edit_check_data_connection_active_network_is_connected);

        Boolean isNetworkAvailable = Networking.isConnectedToInternet();

        setTextBackground(dataConnection,isNetworkAvailable); //Muestra si es positivo o negativo en base al color de fondo de la caja de texto

        ///////////////////////////////////////////////////////////
        // Comprobaciones pormenorizadas para más información
        ///////////////////////////////////////////////////////////
        setTextBackground(activeNetwork, Networking.activeNetworkNotNull() );
        setTextBackground(activeNetworkAvailable, Networking.activeNetworkIsAvailable() );
        setTextBackground(activeNetworkConnected, Networking.activeNetworkIsConnected() );
    }

    /**
     * Helper que mostrará los valores true/false de forma gráfica
     * asignando el fondo de color verde o rojo.
     * @param textView el Textview de la caja de texto a la que se le cambiará el color de fondo
     * @param valorPositivo si lo queremos en true o false
     */
    private void setTextBackground(TextView textView, Boolean valorPositivo) {

        if ( valorPositivo ) {
            textView.setBackgroundColor(getResources().getColor(R.color.green));
        } else {
            textView.setBackgroundColor(getResources().getColor(R.color.red));
        }
    }

}
