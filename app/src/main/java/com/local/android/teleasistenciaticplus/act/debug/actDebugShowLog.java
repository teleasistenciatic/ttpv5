package com.local.android.teleasistenciaticplus.act.debug;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.local.android.teleasistenciaticplus.R;
import com.local.android.teleasistenciaticplus.modelo.Constants;

import static com.local.android.teleasistenciaticplus.lib.filesystem.FileOperation.fileRead;

/**
 * Lectura del fichero LOG que se encuentra fisicamente en el dispositivo
 */
public class actDebugShowLog extends Activity implements Constants{

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
     * @param view vista
     */
    public void exit_button(View view) {
        finish();
    }

}
