package com.local.android.teleasistenciaticplus.act.debug;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.local.android.teleasistenciaticplus.R;
import com.local.android.teleasistenciaticplus.lib.cifrado.Cifrado;
import com.local.android.teleasistenciaticplus.lib.helper.AppLog;

/**
 * Depuración: cifrado descifrado
 */

public class actDebugCifrado extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_debug_cifrado);
    }

    /**
     * Fin de la activity
     *
     * @param view vista
     */
    public void exit_button(View view) {
        finish();
    }

    /**
     * Cifrado
     *
     * @param view vista
     * @throws Exception
     */
    public void main_debug_button_cifrado_test(View view) throws Exception {

        //Obtenemos el texto de la caja de texto
        TextView textEditConTextoACifrar = (TextView) findViewById(R.id.edit_string_cifrado_input);
        String textoACifrar = textEditConTextoACifrar.getText().toString();

        if (textoACifrar.isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.error_empty_string), Toast.LENGTH_SHORT).show();
            return;
        }

        //Llamamos a la encriptación
        Cifrado miCifrado = new Cifrado();
        String cifrado = miCifrado.cifrar(textoACifrar);
        ///////////////////////////////

        //Obtenemos la caja de texto de salida
        TextView textEditConCifrado = (TextView) findViewById(R.id.edit_string_cifrado_output);
        textEditConCifrado.setText(cifrado);

        AppLog.i("actMainDebug -> texto cifrado", cifrado);
    }

    /**
     * Descifrado
     *
     * @param view vista
     * @throws Exception
     */
    public void main_debug_button_descifrado_test(View view) throws Exception {

        //Obtenemos el texto de la caja de texto
        TextView textEditConTextoADescifrar = (TextView) findViewById(R.id.edit_string_cifrado_input);
        String textoADescifrar = textEditConTextoADescifrar.getText().toString();

        if (textoADescifrar.isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.error_empty_string), Toast.LENGTH_SHORT).show();
            return;
        }

        //Llamamos a la desencriptación
        Cifrado miCifrado = new Cifrado();
        String descifrado = miCifrado.descifrar(textoADescifrar);
        ///////////////////////////////

        //Obtenemos la caja de texto de salida
        TextView textEditConDescifrado = (TextView) findViewById(R.id.edit_string_cifrado_output);
        textEditConDescifrado.setText(descifrado);

        AppLog.i("actMainDebug -> texto descifrado", descifrado);
    }

}
