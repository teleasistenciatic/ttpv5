package com.local.android.teleasistenciaticplus.act.main;


import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.local.android.teleasistenciaticplus.R;
import com.local.android.teleasistenciaticplus.act.debug.actMainDebug;
import com.local.android.teleasistenciaticplus.act.ducha.actModoDucha;
import com.local.android.teleasistenciaticplus.act.user.actUserOptions;
import com.local.android.teleasistenciaticplus.act.user.actUserOptionsDatosPersonales;
import com.local.android.teleasistenciaticplus.act.user.actUserOptionsPersonaContacto;
import com.local.android.teleasistenciaticplus.act.zonasegura.serviceZonaSegura;
import com.local.android.teleasistenciaticplus.lib.bateria.MonitorBateria;
import com.local.android.teleasistenciaticplus.lib.detectorCaidas.ServicioMuestreador;
import com.local.android.teleasistenciaticplus.lib.helper.AppDialog;
import com.local.android.teleasistenciaticplus.lib.helper.AppLog;
import com.local.android.teleasistenciaticplus.lib.helper.AppSharedPreferences;
import com.local.android.teleasistenciaticplus.lib.sms.SmsLauncher;
import com.local.android.teleasistenciaticplus.lib.sound.PlaySound;
import com.local.android.teleasistenciaticplus.lib.sound.SintetizadorVoz;
import com.local.android.teleasistenciaticplus.lib.stats.StatsFileLogTextGenerator;
import com.local.android.teleasistenciaticplus.modelo.Constants;
import com.local.android.teleasistenciaticplus.modelo.DebugLevel;
import com.local.android.teleasistenciaticplus.modelo.TipoAviso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Actividad principal
 *
 * @author TeleasistenciaTIC+ Team
 */

public class actMain extends FragmentActivity implements AppDialog.AppDialogNeutralListener {

    //TAG para depuración
    private final String TAG = getClass().getSimpleName() + "--> ";

    private ImageButton SMSAlertButton;
    private ImageButton SMSOKButton;

    private static AnimationDrawable tresdosunoGo; //Animación para cuenta atrás

    //Controles de estado del botón rojo
    public int boton_rojo_clicks; //Número de clicks sobre el botón
    public boolean boton_rojo_cuenta_atras_activa;
    public boolean boton_rojo_cancelar_envio;

    static actMain instanciaActMain;

    private MonitorBateria monBat;
    private SintetizadorVoz sintetizador = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /////////////////////////////////////////////////////
        StatsFileLogTextGenerator.write("app", "iniciada");
        /////////////////////////////////////////////////////

        instanciaActMain = this; //Se utiliza para obtener una instancia desde otra actividad

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);


        SMSAlertButton = (ImageButton) findViewById(R.id.tfmButton);
        SMSOKButton = (ImageButton) findViewById(R.id.btnIamOK);

        //Asigamos al botón rojo la animación de cuenta atrás
        SMSAlertButton.setBackgroundResource(R.drawable.boton_rojo_states);
        tresdosunoGo = (AnimationDrawable) SMSAlertButton.getBackground();

        //Inicializamos el estado del botón rojo
        boton_rojo_clicks = 0;
        boton_rojo_cuenta_atras_activa = false;
        boton_rojo_cancelar_envio = false;

        //Damos la bienvenida
        if ( (Constants.PLAY_SOUNDS) && (Constants.PLAY_BIENVENIDO_SOUND) ) {

            PlaySound.play(R.raw.bienvenido);

        }

        // Creo el objeto de la clase SintetizadorVoz si los sonidos de la app están activados.
        if(Constants.PLAY_SOUNDS)
            sintetizador = new SintetizadorVoz(this);

        /////////////////////////////////////////////////////////////
        // Si no tiene al menos un contacto de usuario, cargamos la ventana de contacto de usuario
        /////////////////////////////////////////////////////////////
        boolean hasContactData = new AppSharedPreferences().hasPersonasContacto();

        if ( !hasContactData) {
            Intent intent = new Intent(this, actUserOptionsPersonaContacto.class);
            startActivity(intent);
        }


        /////////////////////////////////////////////////////////////
        // Si no tiene datos personales (nombre + apellidos)
        /////////////////////////////////////////////////////////////
        boolean hasUserData = new AppSharedPreferences().hasUserData();

        if (!hasUserData) {
            //Carga de la ventana para la introducción del
            //Nombre y apellidos del usuario
            Intent intent = new Intent(this, actUserOptionsDatosPersonales.class);
            startActivity(intent);
        }

        //Si se envió con anterioridad algún sms, se actualiza el texto informativo
        boolean hasLastSMS = new AppSharedPreferences().hasPreferenceData(Constants.NOMBRE_APP_SHARED_PREFERENCES_DATETIME_ULTIMO_SMS_ENVIADO);
        if(hasLastSMS){

            TextView tvUltimoSMSEnviado = (TextView) findViewById(R.id.tvUltimoSMSEnviado);
            tvUltimoSMSEnviado.setText("Último SMS enviado el " +
                            new AppSharedPreferences().getPreferenceData(Constants.NOMBRE_APP_SHARED_PREFERENCES_DATETIME_ULTIMO_SMS_ENVIADO)
            );
        }

        ////////////////////////////////////////////////
        // Se inicia el servicio de detección de caidas
        /////////////////////////////////////////////
        AppSharedPreferences mispreferences = new AppSharedPreferences();
        String caidasActivas = mispreferences.getPreferenceData(Constants.DETECTOR_CAIDAS_ARRANCAR_AL_INICIO);
        if(caidasActivas.equals(Constants.DETECTOR_CAIDAS_ACTIVAR)){   //si esta indicado arranco
            Intent intentA= new Intent(this,ServicioMuestreador.class);
            startService(intentA);
            AppLog.i(TAG, "Caidas activo");
            /////////////////////////////////////////////////////
            StatsFileLogTextGenerator.write("caidas", "servicio iniciado");
            /////////////////////////////////////////////////////
        }else if( caidasActivas.equals(Constants.DETECTOR_CAIDAS_DESACTIVAR)){  //no hago nada.
            AppLog.i(TAG,"Caidas inactivo");
            /////////////////////////////////////////////////////
            StatsFileLogTextGenerator.write("caidas", "servicio inactivo");
            /////////////////////////////////////////////////////
        }else{ //no existe la preferencia. crear e iniciar el servicio.
            mispreferences.setPreferenceData(Constants.DETECTOR_CAIDAS_ARRANCAR_AL_INICIO,Constants.DETECTOR_CAIDAS_ACTIVAR);
            Intent intentA= new Intent(this,ServicioMuestreador.class);
            startService(intentA);
            AppLog.i(TAG, "caidas creado y activo");
            /////////////////////////////////////////////////////
            StatsFileLogTextGenerator.write("caidas", "no existe preferencia, servicio iniciado");
            /////////////////////////////////////////////////////
        }

        ////////////////////////////////////////////////
        // Se inicia el servicio Zona Segura
        /////////////////////////////////////////////
        String zonaSeguraArrancarAlInicio = mispreferences.getPreferenceData(Constants.ZONA_SEGURA_ARRANCAR_AL_INICIO);

        if( zonaSeguraArrancarAlInicio.equals("true") ) {   //si esta indicado arranco el servicio

            Intent intentA= new Intent(this, serviceZonaSegura.class);
            startService(intentA);

            AppLog.i(TAG, "Servicio Zona Segura cargado al inicio ");
            /////////////////////////////////////////////////////
            StatsFileLogTextGenerator.write("zona segura", "servicio iniciado");
            /////////////////////////////////////////////////////

        }

        /////////////////////////////////////////////////////////////////////
        // Inicio del Monitor de Batería, broadcastreceiver que corre en la
        // aplicación, no en un servicio. Se iniciará según su configuración.
        /////////////////////////////////////////////////////////////////////
        try {
            monBat = new MonitorBateria();

        } catch (Exception e) {
            e.printStackTrace();
            AppLog.i("Error en main", "Efectivamente se da aquí -> e = " + e.getMessage() + ", " + e.getLocalizedMessage());
            /////////////////////////////////////////////////////
            StatsFileLogTextGenerator.write("bateria", "no se ha podido monitorizar la bateria");
            /////////////////////////////////////////////////////
        }
        AppLog.i("Monitor Bateria", "Creado objeto monBat, " + monBat.textoNivel() + " " + monBat.textoEstado());
    }

    @Override
    protected void onStart() {

        super.onStart();

    }

    @Override
    public void onResume(){
        super.onResume();
    }



    public static actMain getInstance(){
        return instanciaActMain;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // SE muestra un menu u otro según el modo en que estemos
        // Si estamos en modo de depuración
        if (Constants.DEBUG_LEVEL == DebugLevel.DEBUG) {
            getMenuInflater().inflate(R.menu.menu_act_main, menu);
        } else { //si estamos en modo de producción no mostramos el menu de depuración
            getMenuInflater().inflate(R.menu.menu_act_main_produccion, menu);
        }

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_act_main_exit_app) {
            finish();
        } else if (id == R.id.menu_act_main_debug_screen) {
            Intent intent = new Intent(this, actMainDebug.class);
            startActivity(intent);
        } else if (id == R.id.menu_act_user_options) {
            Intent intent = new Intent(this, actUserOptions.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy()
    {
        // Quito el registro del BroadcastReceiver del contexto. (Desactivo)
        if(monBat.getReceiverActivo())
            monBat.desactivaReceiver(false);
        if(sintetizador!=null)
            sintetizador.finaliza();
        super.onDestroy();
    }

    /**
     * Getter de la clase MonitorBateria
     * @return Un objeto MonitorBateria.
     */
    public MonitorBateria getMonitorBateria()
    {
        return monBat;
    }

    /**
     * Getter del sintetizador de voz.
     * @return Objeto de la clase SintetizadorVoz, o null si no están activados los sonidos en la app.
     */
    public SintetizadorVoz getSintetizador() { return sintetizador; }

    /////////////////////////////////////////////////////////////
    // Métodos asociados a los botones de la UI
    /////////////////////////////////////////////////////////////

    /**
     * Botón de acceso a la Configuración
     * Da acceso a la configuración de parámetros personales
     *
     * @param view vista del botón.
     */
    public void configuration_action_button(View view) {

        Intent intent = new Intent(this, actUserOptions.class);

        startActivity(intent);

        if (Constants.SHOW_ANIMATION) {

            overridePendingTransition(R.anim.animation2, R.anim.animation1);

        }
    }

    /**
     * Botón para volver a casa
     * Activa la opción de volver a casa
     *
     * @param view vista del botón.
     */
    public void backtohome_action_button(View view) {

        //Toast.makeText(getBaseContext(), "Volver a Casa", Toast.LENGTH_LONG).show();

        // ¿Hay datos de Zona Segura/ Hogar?
        AppSharedPreferences misAppSharedPreferences = new AppSharedPreferences();

        if ( misAppSharedPreferences.hasZonaSegura() ) {

            String[] hogar = misAppSharedPreferences.getZonaSeguraData();

            String location= hogar[0] + ", " + hogar[1] ; //"36.993150, -2.657814";

            Intent mapIntent=new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + location + "&mode=w"));
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);

        } else {

            Toast.makeText(getBaseContext(), "No hay datos de zona segura", Toast.LENGTH_LONG).show();

        }




        //

        /*
        Intent intent = new Intent(this, actBackToHome.class);


        startActivity(intent);

        if( Constants.SHOW_ANIMATION ) {

            overridePendingTransition(R.animator.animation2, R.animator.animation1);

        }
        */
    }

    /**
     * Botón para llamada al primer contacto
     * Activa la llamada de teléfono al primer contacto listado
     *
     * @param view vista del botón.
     */
    public void llamada_action_button(View view) {

        AppSharedPreferences miAppSharedPreferences = new AppSharedPreferences();

        if ( !miAppSharedPreferences.hasPersonasContacto() ) {

            Toast.makeText(getBaseContext(), "Error: no existen personas de contacto", Toast.LENGTH_LONG).show();

        } else {
            //Existen personas de contacto
            // Se obtiene la primera persona de contacto
            miAppSharedPreferences.getFirstTelefonoContacto();

            String url = "tel:" + miAppSharedPreferences.getFirstTelefonoContacto();
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
            startActivity(intent);
        }
    }

    /**
     * Botón para activar el modo ducha
     * Activa el modo ducha
     *
     * @param view vista del botón.
     */
    public void showermode_action_button(View view) {

        Intent intent = new Intent(this, actModoDucha.class);

        startActivity(intent);

        if (Constants.SHOW_ANIMATION) {

            overridePendingTransition(R.anim.animation2, R.anim.animation1);

        }

    }


    /**
     * Botón para acceder a los contactos familiares
     * Atajo a los contactos familiares
     *
     * @param view Vista del botón
     */
    public void familiar_action_button(View view) {

        Intent intent = new Intent(this, actUserOptionsPersonaContacto.class);

        startActivity(intent);

        if (Constants.SHOW_ANIMATION) {

            overridePendingTransition(R.anim.animation2, R.anim.animation1);

        }
    }




    /**
     * Método inicial para comprobar si es posible el envio de los SMS a los familiares.
     * Comprueba que haya contactos almacenados.
     * Controla los clicks que recibe el botón, permitiendo temporalmente cancelar la acción del
     * envío del SMS.
     * Este método no realiza de forma directa el envío del SMS, en su lugar, llama al método
     * enviarSMSdiferido, que comprobará si es posible el envío del SMS (dependiendo de si se ha
     * cancelado la acción a través de un segundo click durante la cuenta atrás).
     *
     * @param view Vista del botón
     */
    public void sendAvisoSms(View view) {

        //0. Comprobamos si se ha pulsado más de una vez el botón
        //1. Leemos la lista de personas de contacto
        //2. Comprobamos el tiempo transcurrido desde el último SMS enviado
        //2. Se les envía SMS
        //3. Se muestra un mensaje de indicación

        boton_rojo_clicks++;


        AppLog.i(TAG,"-----------------");
        AppLog.i(TAG,"ENTRADA al Método principal");
        AppLog.i(TAG, "Click: " + boton_rojo_clicks);



        if(boton_rojo_clicks==1){
            AppLog.i(TAG,"Primer CLICK");
            AppLog.i(TAG,"Click: " + boton_rojo_clicks);
            //No hay clicks previos en proceso,
            // el estado del botón es óptimo para iniciar la cuenta atrás
            // y proceder con el intento de envío del SMS


            Boolean hayPersonasContactoConTelefono = new AppSharedPreferences().hasPersonasContacto();

            if (!hayPersonasContactoConTelefono) {
            /*
            /////////
            //Genera una alerta en caso de que no tengamos asignados los contactos
            /////////
            //Se abre el menú de personas de contacto
            */
                AppDialog newFragment = AppDialog.newInstance(AppDialog.tipoDialogo.SIMPLE,1,
                        "Contactos no disponibles",
                        "No se han encontrado contactos almacenados. Introduzca al menos un contacto",
                        getResources().getString(R.string.aceptar),
                        "sin_uso");
                newFragment.show(getFragmentManager(),"dialog");
                //Fin del mensaje de información

            }

            //Mostramos texto para posibilitar la cancelación del envío
            TextView tvUltimoSMSEnviado = (TextView) findViewById(R.id.tvUltimoSMSEnviado);
            tvUltimoSMSEnviado.setText("PULSE DE NUEVO PARA CANCELAR");

            //Iniciamos la cuenta atrás
            boton_rojo_cuenta_atras_activa = true;
            tresdosunoGo.start();

            //finalizada la cuenta atrás, intentamos enviar el SMS.
            //1. Recuperamos el tiempo total de la animación
            //2. Lanzamos el intento de envío del SMS con un Timer
            long totalDuration = 0;
            for(int i = 0; i< tresdosunoGo.getNumberOfFrames();i++){
                totalDuration += tresdosunoGo.getDuration(i);
            }

            Timer timer = new Timer();
            TimerTask timerTask = new TimerTask(){

                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            //Ejecutar el envío del SMS en una función aparte
                            AppLog.i(TAG,"-----------------");
                            AppLog.i(TAG,"Fin de la cuenta atrás");
                            AppLog.i(TAG,"Click: " + boton_rojo_clicks);
                            AppLog.i(TAG,"Cuenta atrás: " + boton_rojo_cuenta_atras_activa);
                            AppLog.i(TAG,"Cancelar envío: " + boton_rojo_cancelar_envio);

                            boton_rojo_cuenta_atras_activa = false;
                            SMSAlertButton.setEnabled(false);

                            //Mostramos el texto en la pantalla
                            TextView tvUltimoSMSEnviado = (TextView) findViewById(R.id.tvUltimoSMSEnviado);
                            tvUltimoSMSEnviado.setText("Enviando SMS. Por favor espere...");

                            enviarSMSdiferido(SMSAlertButton);

                        }
                    });
                }
            };

            timer.schedule(timerTask, totalDuration);
        }

        else {
            //Antes ya se había pulsado el botón.

            AppLog.i(TAG,"CLICK adicional");
            AppLog.i(TAG,"Click: " + boton_rojo_clicks);
            AppLog.i(TAG, "Cuenta atrás: " + boton_rojo_cuenta_atras_activa);
            AppLog.i(TAG, "Cancelar envío: " + boton_rojo_cancelar_envio);

            //Si es el segundo click comprobamos si la acuenta atrás está activa para deternela y cancelar el envío del SMS
            if (boton_rojo_clicks == 2) {
                AppLog.i(TAG,"Segundo Click");
                AppLog.i(TAG,"Click: " + boton_rojo_clicks);


                if(boton_rojo_cuenta_atras_activa) {
                    AppLog.i(TAG,"Animación activa!! Hay que detenerla y cancelar el envío");
                    //Mostramos el texto de envío cancelado
                    TextView tvUltimoSMSEnviado = (TextView) findViewById(R.id.tvUltimoSMSEnviado);
                    tvUltimoSMSEnviado.setText("Envío de SMS Cancelado");

                    //Acualizamos la UI
                    //SMSAlertButton.setEnabled(true);

                    tresdosunoGo.stop();
                    tresdosunoGo.selectDrawable(0);
                    //Nos aseguramos de que no se mande el SMS
                    boton_rojo_cancelar_envio = true;
                    boton_rojo_clicks--;
                }
            }
            else {

                //se han hecho más de dos clicks, así que lo ignoramos
                AppLog.i(TAG,"CLICK sin efecto");
                boton_rojo_clicks--;

            }
        }

    }

    /**
     * botón para enviar SMS de tranquilidad (I'm OK)
     *
     * @param view Vista del botón
     */
    public void sendAvisoIamOK(View view) {

        //1. Leemos la lista de personas de contacto
        //2. Se les envía SMS
        //3. Se muestra un mensaje de indicación

        Boolean hayPersonasContactoConTelefono = new AppSharedPreferences().hasPersonasContacto();

        if (!hayPersonasContactoConTelefono) {

            AppDialog newFragment = AppDialog.newInstance(AppDialog.tipoDialogo.SIMPLE,1,
                    "Contactos no disponibles",
                    "No se han encontrado contactos almacenados. Introduzca al menos un contacto",
                    getResources().getString(R.string.aceptar),
                    "sin_uso");
            newFragment.show(getFragmentManager(),"dialog");
            //Fin del mensaje de información

        }

        //Operación de envío de SMS

        String[] personasContacto = new AppSharedPreferences().getPersonasContacto();
        SmsLauncher miSmsLauncher = new SmsLauncher(TipoAviso.IAMOK);

        Boolean hayListaContactos = new SmsLauncher(TipoAviso.IAMOK).generateAndSend();


        //Si se ha mandado algún SMS...

        if ( hayListaContactos ) {

            actualizarUltimoSMSEnviado(null);

            //Avisamos al usuario de que ha enviado el SMS con un sonido
            if (Constants.PLAY_SOUNDS) {

                PlaySound.play(R.raw.mensaje_enviado);

            }

            //Deshabilitamos el botón y cambiamos su aspecto
            view.setEnabled(false);

            view.setBackgroundResource(R.drawable.iam_ok_pressed);

            //Habilitamos el botón transcurridos unos segundos
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    SMSOKButton.setEnabled(true);
                    SMSOKButton.setBackgroundResource(R.drawable.iam_ok);
                }
            }, Constants.SMS_SENDING_DELAY);

        }

    }

    /**
     * Refresca el texto en pantalla de la fecha del último envío de SMS
     *
     * @param date Fecha de envío del SMS
     */
    public void actualizarUltimoSMSEnviado (Date date) {

        //Actualizamos el tiempo del envío del mensaje
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd-MM-yyyy 'a las' HH:mm:ss");
        String currentDateandTime;
        if (date == null) currentDateandTime = sdf.format(new Date());
        else currentDateandTime = sdf.format(date);

        //Mostramos el texto en la pantalla
        TextView tvUltimoSMSEnviado = (TextView) findViewById(R.id.tvUltimoSMSEnviado);

        tvUltimoSMSEnviado.setText("Último SMS enviado el " + currentDateandTime);

        new AppSharedPreferences().setPreferenceData(Constants.NOMBRE_APP_SHARED_PREFERENCES_DATETIME_ULTIMO_SMS_ENVIADO, currentDateandTime );
    }



    //Implementación del interfaz de diálogo
    public void onAccionNeutral(DialogFragment dialog){

        Intent intent = new Intent(this, actUserOptionsPersonaContacto.class);

        startActivity(intent);

        if (Constants.SHOW_ANIMATION) {

            overridePendingTransition(R.anim.animation2, R.anim.animation1);

        }

    }

    /**
     * Realiza el envío de los SMS a todos los contactos siempre que no se haya cancelado con
     * anterioridad la acción como consecuencia de un segundo click sobre el botón rojo durante
     * la cuenta atrás.
     *
     * @param v Vista del botón
     */
    public void enviarSMSdiferido(View v) {

        SMSAlertButton.setEnabled(false);
        SMSAlertButton.setBackgroundResource(R.drawable.grey_button200);
        AppLog.i(TAG,"-----------------");
        AppLog.i(TAG,"ENTRADA al Método de envío de SMS");
        AppLog.i(TAG,"Click: " + boton_rojo_clicks);
        AppLog.i(TAG,"Cuenta atrás: " + boton_rojo_cuenta_atras_activa);
        AppLog.i(TAG,"Cancelar envío: " + boton_rojo_cancelar_envio);


        boton_rojo_clicks--;

        //Si no se ha cancelado el envío con un segundo click mientras estaba la cuenta atrás, se envía
        if(boton_rojo_cancelar_envio == false) {
            AppLog.i(TAG, "Envío no cancelado. MANDAMOS SMS");

            //Enviamos el SMS
            Boolean hayListaContactos = new SmsLauncher(TipoAviso.AVISO).generateAndSend();



            //TODO: mejorar con el control de errores de SMS

            //Si se ha mandado algún SMS...
            if (hayListaContactos) {

                //Habilitamos el botón transcurridos unos segundos
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        //Avisamos al usuario de que ha enviado el SMS con un sonido
                        if (Constants.PLAY_SOUNDS) {

                            PlaySound.play(R.raw.mensaje_enviado);

                        }

                        //Refrescamos la fecha de envío del SMS
                        actualizarUltimoSMSEnviado(new Date());
                        SMSAlertButton.setEnabled(true);
                        SMSAlertButton.setBackgroundResource(R.drawable.boton_rojo_states);
                        tresdosunoGo = (AnimationDrawable) SMSAlertButton.getBackground();

                        tresdosunoGo.stop();
                        tresdosunoGo.selectDrawable(0);

                        boton_rojo_cancelar_envio = false;

                    }
                }, Constants.SMS_SENDING_DELAY);
            }
        }

        else {

            //Mostramos el texto de envío cancelado
            AppLog.i(TAG, "Envío de SMS CANCELADO");
            TextView tvUltimoSMSEnviado = (TextView) findViewById(R.id.tvUltimoSMSEnviado);
            tvUltimoSMSEnviado.setText("Envío de SMS Cancelado");

            SMSAlertButton.setEnabled(true);
            SMSAlertButton.setBackgroundResource(R.drawable.boton_rojo_states);
            tresdosunoGo = (AnimationDrawable) SMSAlertButton.getBackground();

            tresdosunoGo.stop();
            tresdosunoGo.selectDrawable(0);

            boton_rojo_cancelar_envio = false;


            //Avisamos al usuario de que NO se ha enviado el SMS con un sonido
            if (Constants.PLAY_SOUNDS) {

                PlaySound.play(R.raw.mensaje_cancelado);

            }

            //Volvemos a poner en el texto el último sms enviado
            //Si se envió con anterioridad algún sms, se actualiza el texto informativo
            boolean hasLastSMS = new AppSharedPreferences().hasPreferenceData(Constants.NOMBRE_APP_SHARED_PREFERENCES_DATETIME_ULTIMO_SMS_ENVIADO);
            if(hasLastSMS){

                tvUltimoSMSEnviado.setText("Último SMS enviado el " +
                                new AppSharedPreferences().getPreferenceData(Constants.NOMBRE_APP_SHARED_PREFERENCES_DATETIME_ULTIMO_SMS_ENVIADO)
                );
            }

        }

    }

}