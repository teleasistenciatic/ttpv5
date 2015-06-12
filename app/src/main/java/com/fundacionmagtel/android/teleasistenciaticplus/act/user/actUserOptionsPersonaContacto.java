package com.fundacionmagtel.android.teleasistenciaticplus.act.user;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fundacionmagtel.android.teleasistenciaticplus.lib.helper.AppDialog;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.helper.AppLog;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.phone.PhoneContacts;
import com.fundacionmagtel.android.teleasistenciaticplus.R;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.helper.AppSharedPreferences;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.stats.StatsFileLogTextGenerator;

import java.util.Map;

public class actUserOptionsPersonaContacto extends FragmentActivity implements AppDialog.AppDialogNeutralListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_user_options_persona_contacto);
        getActionBar().setIcon(R.drawable.config_wheel);

        /////////////////////////////////////////////////////
        StatsFileLogTextGenerator.write("configuracion", "personas contacto");
        /////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////
        /// Leer los valores de persona s de contacto
        ///////////////////////////////////////////////////////////////////

        String personasContacto[] = new AppSharedPreferences().getPersonasContacto();

        ////////////////////////////////////////////////////////////////////
        /// Inflar el layout con los valores de personas de contacto
        ///////////////////////////////////////////////////////////////////

        TextView textedit = (TextView) findViewById(R.id.user_options_persona_contacto_text);
        TextView texteditName = (TextView) findViewById(R.id.user_options_persona_contacto_name_text);
        ImageButton borradoTexteditName = (ImageButton) findViewById(R.id.button_user_options_persona_contacto_1_borrar);

        TextView textedit1 = (TextView) findViewById(R.id.user_options_persona_contacto_text_1);
        TextView texteditName1 = (TextView) findViewById(R.id.user_options_persona_contacto_name_text_1);
        ImageButton borradoTexteditName1 = (ImageButton) findViewById(R.id.button_user_options_persona_contacto_2_borrar);

        TextView textedit2 = (TextView) findViewById(R.id.user_options_persona_contacto_text_2);
        TextView texteditName2 = (TextView) findViewById(R.id.user_options_persona_contacto_name_text_2);
        ImageButton borradoTexteditName2 = (ImageButton) findViewById(R.id.button_user_options_persona_contacto_3_borrar);

        textedit.setText(personasContacto[0]);
        texteditName.setText(personasContacto[1]);
        //Sólo se muestra el botón de borrado en cuando exista un texto
        if (personasContacto[0].length() == 0) {
            borradoTexteditName.setVisibility(View.INVISIBLE);
        }

        textedit1.setText(personasContacto[2]);
        texteditName1.setText(personasContacto[3]);
        if (personasContacto[2].length() == 0) {
            borradoTexteditName1.setVisibility(View.INVISIBLE);
        }

        textedit2.setText(personasContacto[4]);
        texteditName2.setText(personasContacto[5]);
        if (personasContacto[4].length() == 0) {
            borradoTexteditName2.setVisibility(View.INVISIBLE);
        }

    }

    /*
    @Override
    public void onBackPressed() {
        super.onDestroy();
        actMain.getInstance().finish(); //Si se pulsa el botón de BACK, eliminamos la Stack completa de llamadas
        finish();
    }
    */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_act_user_options_persona_contacto, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_user_options_persona_contacto_exit_app) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Selección del contacto principal
     *
     * @param view
     */
    public void user_options_persona_contacto_text_click_1(View view) {

        //Abrir la lista de contactos
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, 0);

    }

    /**
     * Selección del contacto 2
     *
     * @param view
     */
    public void user_options_persona_contacto_text_click_2(View view) {
        //Abrir la lista de contactos
      //  Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
      //  startActivityForResult(intent, 1);

        Toast.makeText(this,"Esta versión (Beta 1.0-piloto) sólo admite un contacto.",Toast.LENGTH_SHORT).show();
    }

    /**
     * Selección del contacto 3
     *
     * @param view
     */
    public void user_options_persona_contacto_text_click_3(View view) {
        //Abrir la lista de contactos
     //   Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
     //   startActivityForResult(intent, 2);

        Toast.makeText(this,"Esta versión (Beta 1.0-piloto) sólo admite un contacto.",Toast.LENGTH_SHORT).show();
    }

    /**
     * Listener de la pulsación de botones para el borrado de contactos
     *
     * @param view
     */
    public void user_options_persona_contacto_button_erase(View view) {

        int id = view.getId();
        int contacto_a_borrar = 0;
        TextView textedit = null;
        TextView texteditName = null;
        ImageButton borradoTexteditName = null;

        // Hay un sólo método que recibe todas las pulsaciones de botón
        // en base a eso se selecciona el contacto a borrar y se obtiene las cajas de texto
        // que contienen los valores

        if (id == R.id.button_user_options_persona_contacto_1_borrar) {
            textedit = (TextView) findViewById(R.id.user_options_persona_contacto_text);
            texteditName = (TextView) findViewById(R.id.user_options_persona_contacto_name_text);
            borradoTexteditName = (ImageButton) findViewById(R.id.button_user_options_persona_contacto_1_borrar);
            contacto_a_borrar = 0;
        } else if (id == R.id.button_user_options_persona_contacto_2_borrar) {
            textedit = (TextView) findViewById(R.id.user_options_persona_contacto_text_1);
            texteditName = (TextView) findViewById(R.id.user_options_persona_contacto_name_text_1);
            borradoTexteditName = (ImageButton) findViewById(R.id.button_user_options_persona_contacto_2_borrar);
            contacto_a_borrar = 1;
        } else if (id == R.id.button_user_options_persona_contacto_3_borrar) {
            textedit = (TextView) findViewById(R.id.user_options_persona_contacto_text_2);
            texteditName = (TextView) findViewById(R.id.user_options_persona_contacto_name_text_2);
            borradoTexteditName = (ImageButton) findViewById(R.id.button_user_options_persona_contacto_3_borrar);
            contacto_a_borrar = 2;
        }

        // Borrado del contacto seleccionado
        // 1. borrado del sharedpreferences
        new AppSharedPreferences().deletePersonasContactobyId(contacto_a_borrar);

        // 2. borrado de la interfaz
        textedit.setText("");
        texteditName.setText("");

        // 3. Se elimina el botón con la opción de borrado
        borradoTexteditName.setVisibility(View.INVISIBLE);

        AppLog.i("Contacto borrado de la lista", "" + textedit.getText() + " : " + texteditName.getText() + " " + contacto_a_borrar);

    }

    /**
     * Función que recoge los datos del contacto seleccionado
     *
     * @param reqCode    regcode
     * @param resultCode resultCode
     * @param data       El data del intent
     */
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        Map contactDataMap = null;

        TextView textedit = (TextView) findViewById(R.id.user_options_persona_contacto_text);
        TextView texteditName = (TextView) findViewById(R.id.user_options_persona_contacto_name_text);

        TextView textedit1 = (TextView) findViewById(R.id.user_options_persona_contacto_text_1);
        TextView texteditName1 = (TextView) findViewById(R.id.user_options_persona_contacto_name_text_1);

        TextView textedit2 = (TextView) findViewById(R.id.user_options_persona_contacto_text_2);
        TextView texteditName2 = (TextView) findViewById(R.id.user_options_persona_contacto_name_text_2);

        switch (reqCode) {

            case (0):
                if (resultCode == Activity.RESULT_OK) {

                    contactDataMap = new PhoneContacts(data).getPhoneContact();

                    textedit.setText(contactDataMap.get("displayName").toString());
                    texteditName.setText(contactDataMap.get("phoneNumber").toString());

                    ImageButton borradoTexteditName = (ImageButton) findViewById(R.id.button_user_options_persona_contacto_1_borrar);
                    borradoTexteditName.setVisibility(View.VISIBLE);

                    AppLog.i("Contactos", contactDataMap.toString());
                }
                break;

            case (1):
                if (resultCode == Activity.RESULT_OK) {

                    contactDataMap = new PhoneContacts(data).getPhoneContact();

                    textedit1.setText(contactDataMap.get("displayName").toString());
                    texteditName1.setText(contactDataMap.get("phoneNumber").toString());

                    ImageButton borradoTexteditName = (ImageButton) findViewById(R.id.button_user_options_persona_contacto_2_borrar);
                    borradoTexteditName.setVisibility(View.VISIBLE);

                    AppLog.i("Contactos", contactDataMap.toString());

                }
                break;

            case (2):
                if (resultCode == Activity.RESULT_OK) {

                    contactDataMap = new PhoneContacts(data).getPhoneContact();

                    textedit2.setText(contactDataMap.get("displayName").toString());
                    texteditName2.setText(contactDataMap.get("phoneNumber").toString());

                    ImageButton borradoTexteditName = (ImageButton) findViewById(R.id.button_user_options_persona_contacto_3_borrar);
                    borradoTexteditName.setVisibility(View.VISIBLE);

                    AppLog.i("Contactos", contactDataMap.toString());

                }
                break;
        }

        /*
        -Valores del Array asociativo-
        contactMap.put("displayName", displayName);
        contactMap.put("hasPhoneNumber", hasPhoneNumber);
        contactMap.put("phoneNumber", phoneNumber);
        contactMap.put("contactId", contactId);*/

        //////////////////////////////////////////////
        // Guardar las personas de contacto en el SharedPreferences
        //////////////////////////////////////////////

        AppSharedPreferences userSharedPreferences = new AppSharedPreferences();
        userSharedPreferences.setPersonasContacto(textedit.getText().toString(), texteditName.getText().toString(),
                textedit1.getText().toString(), texteditName1.getText().toString(),
                textedit2.getText().toString(), texteditName2.getText().toString()
        );

    }

    public void user_options_persona_contacto_salir_button(View view) {

        /** No se deja salir al usuario hasta que introduzca un contacto **/

        boolean hasContactData = new AppSharedPreferences().hasPersonasContacto();

        if (!hasContactData) {
            /////////
            //Feedback para que introduzca valores de nombre y apellidos
            /////////
            AppDialog newFragment = AppDialog.newInstance(AppDialog.tipoDialogo.SIMPLE,1,
                    getResources().getString(R.string.user_options_contactos_edit),
                    getResources().getString(R.string.user_options_contactos_empty_edit),
                    getResources().getString(R.string.close_window),
                    "sin_uso");
            newFragment.show(getFragmentManager(),"dialog");
            //Fin del mensaje de información

        } else {
            finish();
        }
    }

    //Implementación del interfaz de diálogo
    public void onAccionNeutral(DialogFragment dialog){

        //finish();

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        boolean hasContactData = new AppSharedPreferences().hasPersonasContacto();

        if (!hasContactData) {
            /////////
            //Feedback para que introduzca al menos una persona de contacto
            /////////
            AppDialog newFragment = AppDialog.newInstance(AppDialog.tipoDialogo.SIMPLE,1,
                    getResources().getString(R.string.user_options_contactos_edit),
                    getResources().getString(R.string.user_options_contactos_empty_edit),
                    getResources().getString(R.string.close_window),
                    "sin_uso");
            newFragment.show(getFragmentManager(),"dialog");
            //Fin del mensaje de información

        } else {
            finish();
        }

    }
}