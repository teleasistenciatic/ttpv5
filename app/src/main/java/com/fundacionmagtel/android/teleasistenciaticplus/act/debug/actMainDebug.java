package com.fundacionmagtel.android.teleasistenciaticplus.act.debug;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.fundacionmagtel.android.teleasistenciaticplus.R;


/**
 * Actividad de menu para lanzar el resto de las actividades de depuracion
 *
 * @author Juan Jose Ferres
 */

public class actMainDebug extends Activity {

    /**
     * Método framework onCreate
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /////////////////////////////////////////////////////////////////////
        // Creación del UI de ListView con los subapartados de Depuración
        ////////////////////////////////////////////////////////////////////

            /// Layout
            setContentView(R.layout.layout_debug_main);
            /// Listview
            final ListView listView = (ListView) findViewById(R.id.debug_main_listView);
            /// String para el ListView
            String[] values = new String[]{
                    "Configuración usuario",// 0,
                    "Envío SMS", //id 1
                    "Lectura .LOG", //id 2
                    "Cifrado/Descifrado", //id 3
                    "Acceso de datos e internet", //id 4
                    "Uso de la memoria", //id 5
                    "Modo ducha", //id 6
                    "Google Services", //id 7
                    "Google Maps", //id 8
                    "Detección de caidas", //id 9
                    "Monitor de batería", //id 10
                    "GPS y SharedPreferences" //id 11
            };

            /// Creación del adaptador con su String
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, values);
            /// Vinculación del adaptador con la lista
            listView.setAdapter(adapter);

            /// Creación del OnClickListener para las pulsaciones
            ////////////////////////////////////////////////////////////////////

            // ListView Item Click Listener
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    Class actToLoad = null;

                    switch(position) {
                        case 0: //"Configuración de usuario"
                            actToLoad = actDebugUserConfig.class;
                            break;
                        case 1: //"Envio SMS"
                            actToLoad = actDebugSMS.class;
                            break;
                        case 2: //"Lectura de log de depuración"
                            actToLoad = actDebugShowLog.class;
                            break;
                        case 3: //"Cifrado Descifrado"
                            actToLoad = actDebugCifrado.class;
                            break;
                        case 4: //"Acceso de datos e internet"
                            actToLoad = actDebugDataConnection.class;
                            break;
                        case 5: //"Uso de la memoria"
                            actToLoad = actDebugMemory.class;
                            break;
                        case 6: //"Modo ducha"
                            actToLoad = actDebugChronometer.class;
                            break;
                        case 7: //"Google Services"
                            actToLoad = actDebugGoogleServices.class;
                            break;
                        case 8: //"Google Maps"
                            actToLoad = actDebugGoogleMaps.class;
                            break;
                        case 9: //"Detección de caidas"
                            actToLoad = actDebugCaidas.class;
                            break;
                        case 10: //"Monitor de Batería"
                            actToLoad = actDebugOjeadorBateria.class;
                            break;
                        case 11: //"Shared Preferences"
                            actToLoad = actDebugGpsSharedPreferences.class;
                            break;
                    }

                    Intent newIntent;
                    newIntent = new Intent().setClass(actMainDebug.this, actToLoad);
                    startActivity(newIntent);

                    /*
                    // ListView Clicked item index
                    int itemPosition = position;

                    // ListView Clicked item value
                    String itemValue = (String) listView.getItemAtPosition(position);

                    // Show Alert
                    Toast.makeText(getApplicationContext(),
                            "Position :" + itemPosition + "  ListItem : " + itemValue, Toast.LENGTH_LONG)
                            .show();
                    */
                }

            });

        /// Fin creación listView
        ////////////////////////////////////////////////////////////////////

        /*
        //Texto de la dirección de servidor
        /*TextView serverAddress = (TextView) findViewById(R.id.edit_server_adress);
        serverAddress.setText(Constants.SERVER_URL);
        */
    }

    /**
     * Menus de la actividad
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    /**
     * Opciones del menú
     * @param item Item del menú
     * @return true o false
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_act_main_exit_app) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Fin de la activity
     * @param view vista
     */
    public void exit_button(View view) {
        finish();
    }

} // Fin actividadMainDebug