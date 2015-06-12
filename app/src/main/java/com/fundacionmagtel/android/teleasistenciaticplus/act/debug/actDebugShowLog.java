package com.fundacionmagtel.android.teleasistenciaticplus.act.debug;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.fundacionmagtel.android.teleasistenciaticplus.modelo.Constants;
import com.fundacionmagtel.android.teleasistenciaticplus.R;

import static com.fundacionmagtel.android.teleasistenciaticplus.lib.filesystem.FileOperation.fileRead;

/**
 * Lectura del fichero LOG que se encuentra fisicamente en el dispositivo
 * @author Juan Jose Ferres
 */
public class actDebugShowLog extends Activity implements Constants {

    /**
     * Método de framework onCreate
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_debug_show_log);

        TextView textAreaLogFile = (TextView) findViewById(R.id.debug_screen_show_log_file);
        if( textAreaLogFile.getText().length() == 0 ) {
            textAreaLogFile.setText(Constants.DEBUG_LOG_FILE); //Si el fichero está vacio se asigna un nuevo fichero
        }
    }

    /**
     * Lectura de un fichero de log que se encuentra en las constantes DEBUG_LOG_FILE
     * y mostrado por pantalla.
     *
     * @param view vista
     */
    public void debug_screen_button_read_log_file_button(View view) {

        TextView textAreaLogFile = (TextView) findViewById(R.id.debug_screen_show_log_file);
        TextView textArea = (TextView) findViewById(R.id.debug_screen_show_log_area);

        //Lectura del archivo
        String readFile = fileRead(String.valueOf(textAreaLogFile.getText()));
        textArea.setText(readFile);

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
