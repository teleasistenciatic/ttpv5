package com.local.android.teleasistenciaticplus.act.debug;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.local.android.teleasistenciaticplus.R;
import com.local.android.teleasistenciaticplus.lib.helper.AppLog;
import com.local.android.teleasistenciaticplus.lib.sms.SmsDispatcher;
import com.local.android.teleasistenciaticplus.lib.phone.PhoneContacts;
import com.local.android.teleasistenciaticplus.modelo.GlobalData;

import java.util.Map;


/**
 * Actividad de DEBUG que enviará un SMS al numero de teléfono indicado
 */
public class actDebugSMS extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_debug_sms);
    }

    /**
     * Debug SMS send
     *
     * @param view vista
     */
    public void sms_send(View view) {

        //Obtener los datos para enviar el SMS
        TextView phoneNumberEdit = (TextView) findViewById(R.id.debug_edit_sms_number);
        String phoneNumber = phoneNumberEdit.getText().toString();

        TextView smsMessageEdit = (TextView) findViewById(R.id.debug_edit_sms_message);
        String smsBodyText = smsMessageEdit.getText().toString();

        // Se envía el SMS
        SmsDispatcher miSmsDispatcher = new SmsDispatcher(phoneNumber,smsBodyText);
        miSmsDispatcher.send();
    }


    /**
     * Lanza la actividad para almacenar el contacto seleccionado
     * @param view
     */
    public void get_contact_from_contactlist(View view) {

        //Abrir la lista de contactos
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

        startActivityForResult(intent, 1);

    }

    /**
     * Función que recoge los datos del contacto seleccionado
     * @param reqCode regcode
     * @param resultCode resultCode
     * @param data El data del intent
     */
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        Map contactDataMap = null;

        switch (reqCode) {
            case (1):
                if (resultCode == Activity.RESULT_OK) {

                    contactDataMap = new PhoneContacts(data).getPhoneContact();
                    AppLog.i("Contactos",contactDataMap.toString());

                }
                break;
        }

        /*
        -Valores del Array asociativo-
        contactMap.put("displayName", displayName);
        contactMap.put("hasPhoneNumber", hasPhoneNumber);
        contactMap.put("phoneNumber", phoneNumber);
        contactMap.put("contactId", contactId);*/

        TextView phoneNumberEdit = (TextView) findViewById(R.id.debug_edit_sms_number);
        phoneNumberEdit.setText( contactDataMap.get("phoneNumber").toString() );

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
