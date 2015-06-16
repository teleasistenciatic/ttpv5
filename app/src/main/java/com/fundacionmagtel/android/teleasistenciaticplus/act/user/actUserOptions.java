package com.fundacionmagtel.android.teleasistenciaticplus.act.user;

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
import com.fundacionmagtel.android.teleasistenciaticplus.lib.stats.StatsFileLogTextGenerator;

/**
 * Actividad que muestra las distintas opciones del menú de configuración de usuario
 * @author Juan Jose Ferres Serrano
 */

public class actUserOptions extends Activity {

    /**
     * Método de framework onCreate
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /////////////////////////////////////////////////////
        StatsFileLogTextGenerator.write("configuracion", "opciones usuario");
        /////////////////////////////////////////////////////

        /////////////////////////////////////////////////////////////////////
        // Creación del UI de ListView con los subapartados de Opciones de usuario
        ////////////////////////////////////////////////////////////////////

        /// Layout
        setContentView(R.layout.layout_user_options_main);
        getActionBar().setIcon(R.drawable.config_wheel);

        /// Listview
        final ListView listView = (ListView) findViewById(R.id.user_options_listView);

        /// String para el ListView
        String[] values = new String[]{
                "Datos personales", // 0,
                "Personas de contacto", // 1
                "Detección de caidas", //2
                "Zona segura", //3
                "Monitor de Batería", //4
                "Pilotaje", //5
                "Manos Libres" //6
        };

        // identificadores de imagenes para el ListView
        Integer[] imageId = {
                R.drawable.personal,
                R.drawable.familiar,
                R.drawable.caida,
                R.drawable.areasegura,
                R.drawable.bateria,
                R.drawable.ducha,
                R.drawable.manoslibres
        };

        /// Creación del adaptador con su String
        AdaptadorConf adapter = new AdaptadorConf(this,values,imageId);

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

                switch (position) {
                    case 0: //"Datos personales"
                        actToLoad = actUserOptionsDatosPersonales.class;
                        break;
                    case 1: //"Personas de contacto"
                        actToLoad = actUserOptionsPersonaContacto.class;
                        break;
                    case 2: //"Detección de caidas"
                        actToLoad = actUserOptionsCaidas.class;
                        break;
                    case 3: //"Zona segura"
                        actToLoad = actUserOptionsZonaSegura.class;
                        break;
                    case 4: //"Monitor de batería"
                        actToLoad = actUserOptionsMonitorBateria.class;
                        break;
                    case 5: //"Pilotaje"
                        actToLoad = actUserOptionsPilotaje.class;
                        break;
                    case 6: //"Manos libres"
                        actToLoad = actUserOptionsManosLibres.class;
                        break;
                }

                Intent newIntent;
                newIntent = new Intent().setClass(actUserOptions.this, actToLoad);
                startActivity(newIntent);
            }

        });

        /// Fin creación listView
        ////////////////////////////////////////////////////////////////////

    }


    /**
     * Creación del menú
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_act_user_options, menu);
        return true;
    }

    /**
     * Operaciones que se realizan al pulsar en la opción correspondiente
     * del menú
     *
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
        if (id == R.id.menu_user_options_exit_app) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
