package com.fundacionmagtel.android.teleasistenciaticplus.act.user;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fundacionmagtel.android.teleasistenciaticplus.R;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.helper.AppDialog;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.helper.AppSharedPreferences;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.stats.StatsFileLogTextGenerator;
import com.fundacionmagtel.android.teleasistenciaticplus.presentador.actStaff;


/**
 * Actividad que guarda los datos personales
 * @author Juan Jose Ferres Serrano
 */
public class actUserOptionsDatosPersonales extends FragmentActivity implements AppDialog.AppDialogNeutralListener {

    /**
     * Metodo de framework
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /////////////////////////////////////////////////////
        StatsFileLogTextGenerator.write("configuracion", "datos personales");
        /////////////////////////////////////////////////////

        setContentView(R.layout.layout_user_options_datos_personales);
        getActionBar().setIcon(R.drawable.config_wheel);

        //Leemos los valores por defecto que hay guardados en el SharedPreferences
        String[] datosPersonales = new AppSharedPreferences().getUserData();

        TextView textEditNombre = (TextView) findViewById(R.id.user_options_datos_personales_nombre_text);
        TextView textEditApellidos = (TextView) findViewById(R.id.user_options_datos_personales_apellidos_text);

        textEditNombre.setText(datosPersonales[0]);
        textEditApellidos.setText(datosPersonales[1]);

        //Easter egg
        Button btn_easteregg = (Button) findViewById(R.id.btnhidden);
        if( textEditNombre.getText().toString().equals("browndispatcher") ){
            btn_easteregg.setVisibility(View.VISIBLE);
        }else {
            btn_easteregg.setVisibility(View.INVISIBLE);
        }

    }

    /**
     * Al pulsar el boton de "atrás" se guardan los datos actuales
     * y no se permite salir si no se ha generado al menos un contacto
     */
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        TextView textEditNombre = (TextView) findViewById(R.id.user_options_datos_personales_nombre_text);
        TextView textEditApellidos = (TextView) findViewById(R.id.user_options_datos_personales_apellidos_text);

        //Sólo se permite la modificación con nombre y apellidos correctos -al menos con un valor-
        if ((textEditNombre.getText().length() > 0) && (textEditApellidos.getText().length() > 0)) {

            finish();

        } else {

            /////////
            //Feedback para que introduzca valores de nombre y apellidos
            /////////
            AppDialog newFragment = AppDialog.newInstance(AppDialog.tipoDialogo.SIMPLE,1,
                    getResources().getString(R.string.user_options_datos_personales_edit),
                    getResources().getString(R.string.user_options_datos_personales_empty_name_surname_edit),
                    getResources().getString(R.string.close_window),
                    "sin_uso");
            newFragment.show(getFragmentManager(),"dialog");
            //Fin del mensaje de información

        }

    }

    /**
     * Se guardan los datos del usuario en las SharedPreferences
     *
     * @param view vista
     */
    public void user_options_datos_personales_edit(View view) {

        TextView textEditNombre = (TextView) findViewById(R.id.user_options_datos_personales_nombre_text);
        TextView textEditApellidos = (TextView) findViewById(R.id.user_options_datos_personales_apellidos_text);

        //Sólo se permite la modificación con nombre y apellidos correctos -al menos con un valor-
        if ((textEditNombre.getText().length() > 0) && (textEditApellidos.getText().length() > 0)) {

            AppSharedPreferences userSharedPreferences = new AppSharedPreferences();
            userSharedPreferences.setUserData(textEditNombre.getText().toString(), textEditApellidos.getText().toString());

            // Para acelerar la UI, simplemente se guarda y cierra en una pulsación
            finish();

        } else {

            /////////
            //Feedback para que introduzca valores de nombre y apellidos
            /////////
            AppDialog newFragment = AppDialog.newInstance(AppDialog.tipoDialogo.SIMPLE,1,
                    getResources().getString(R.string.user_options_datos_personales_edit),
                    getResources().getString(R.string.user_options_datos_personales_empty_name_surname_edit),
                    getResources().getString(R.string.close_window),
                    "sin_uso");
            newFragment.show(getFragmentManager(),"dialog");
            //Fin del mensaje de información

        }

    }

    /**
     * Creación del menú
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_act_user_options_datos_personales, menu);
        return true;
    }

    /**
     * Ejecución de las opciones del menú
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_user_options_datos_personales_exit_app) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Método necesario para la llamada callBack desde el dialogo personalizado
     * @param dialog
     */
    public void onAccionNeutral(DialogFragment dialog){

        //finish();

    }

    /**
     * Easter egg
     * @param v
     */
    public void showStaff(View v){
        //Toast.makeText(getApplicationContext(), "¡¡¡ 30 de junio !!!", Toast.LENGTH_SHORT).show();
        //PlaySound.play(R.raw.error_aviso_no_enviado);
        Intent intent = new Intent(this, actStaff.class);
        startActivity(intent);
    }
}
