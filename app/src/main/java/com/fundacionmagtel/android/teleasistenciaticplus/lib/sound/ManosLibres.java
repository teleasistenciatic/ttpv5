package com.fundacionmagtel.android.teleasistenciaticplus.lib.sound;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.fundacionmagtel.android.teleasistenciaticplus.lib.helper.AppLog;


/**
 * Clase que detecta llamadas entrantes y salientes activando el altavoz del movil.
 * Created by ANTONIO SALVADOR Y GERMAN MORENO 28/05/2015.
 */
public class ManosLibres
{

    /** Entero que guarda el último estado conocido del teléfono. Se inicializa a inactivo. */
    private int ultimoEstado = TelephonyManager.CALL_STATE_IDLE;
    /** Bandera que indica si la llamada es entrante */
    private boolean isIncoming = false;
    /** Bandera que indica si el manos libres está activado */
    private boolean activado = false;
    /** Receptor de eventos del teléfono */
    private BroadcastReceiver receiver;
    /** Contexto de la actividad que nos instancia */
    private Context contexto;

    AudioManager audioManager;
    /** TAG para el Log */
    String MLTAG = "ManosLibres";


    /**
     * Constructor con parámetros. Recibe el contexto de la app que crea la clase.
     * @param c Contexto de la aplicación que nos instancia.
     */
    public ManosLibres(Context c)
    {
        contexto = c;
        audioManager = (AudioManager)contexto.getSystemService(Context.AUDIO_SERVICE);
        /**
         * Broadcast Receiver que captará los eventos de PHONE_STATE y NEW_OUTGOING_CALL
         */
        receiver = new BroadcastReceiver()
        {
            /**
             * Método onReceive del BroadcastReceiver dinamico, que recibe los eventos del sistema
             * PHONE_STATE, que proporcionan información sobre el estado del teléfono
             * y sobre la realización de nuevas llamadas salientes.
             * @param context Almacena el contexto de donde se lanzó el Intent.
             * @param intent Intent del evento de teléfono, que almacena información extra.
             */
            @Override
            public void onReceive(Context context, Intent intent)
            {
                // Recojo la información extra que viene con el Intent.
                String stateStr = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                int state = 0;
                if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    // El telefono está inactivo (0).
                    state = TelephonyManager.CALL_STATE_IDLE;
                } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                    // Hay una llamada en curso.
                    state = TelephonyManager.CALL_STATE_OFFHOOK;
                } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    // Una llamada se está recibiendo y no se ha contestado.
                    state = TelephonyManager.CALL_STATE_RINGING;
                }

                AppLog.i(MLTAG + ".onReceive", "llamada detectada: " + stateStr);
                AppLog.i(MLTAG + ".onReceive", "llamada detectada estado anterior " + ultimoEstado);

                if (ultimoEstado == state) {
                    // No hay cambios de estado, no miro los extras.
                    AppLog.i(MLTAG + ".onReceive", "No hago comprobaciones al no haber cambios de estado");
                    return;
                }

                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        // Se trata de una llamada entrante que está sonando.
                        AppLog.i(MLTAG + ".onReceive", "Llamada entrante sonando...");
                        // Establezco el flag de llamada entrante.
                        isIncoming = true;
                        break;

                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        // Teléfono descolgado.
                        AppLog.i(MLTAG + ".onReceive", "Llamada entrante en curso...");

                        try {
                            Thread.sleep(500); // Delay 0,5 seconds to handle better turning on loudspeaker
                        } catch (InterruptedException e) {
                            // Se ha producido un error.
                            e.printStackTrace();
                        }
                        // Si el modo Manos Libres está activo redirijo el sonido del auricular al altavoz a máximo volumen.
                        if(estaActivo()) {
                            enchufaElAltavoz();
                        }
                        isIncoming = false;
                        break;

                    case TelephonyManager.CALL_STATE_IDLE:
                        // Telefono en reposo.
                        if (ultimoEstado == TelephonyManager.CALL_STATE_RINGING) {
                            AppLog.i(MLTAG + ".onReceive", "En reposo pero ha sonado antes sin contestar, llamada perdida.");
                        } else if (isIncoming) {
                            AppLog.i(MLTAG + ".onReceive", "Inactivo pero ha habido una llamada entrante...");
                        } else {
                            AppLog.i(MLTAG + ".onReceive", "Inactivo despues de un descuelgue, pongo AudioManager.MODE_NORMAL");
                            desenchufaElAltavoz();
                            // audioManager2.setSpeakerphoneOn(false);
                            // audioManager2.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager2.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
                        }
                        break;
                }
                ultimoEstado = state;
            }
        };
        this.registraReceiver();
    }

    /**
     * Registra el BroadcastReceiver con los filtros que nos interesan para activarlo.
     */
    private void registraReceiver()
    {
        // Primero creo el filtro vacio.
        IntentFilter filtro = new IntentFilter();
        // Voy añadiendo lo que quiero filtrar.
        filtro.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        // Registro el broadcast para que empiece a funcionar.
        contexto.registerReceiver(receiver, filtro);
    }

    /**
     * Desactiva el BroadcastReceiver desregistrándolo.
     */
    public void desregistraReceiver()
    {
        // Lo desregistra para desactivarlo.
        contexto.unregisterReceiver(receiver);
    }

    /**
     * Establece el modo de audio de la llamada, activo -> altavoz, inactivo -> auricular.
     * @param opcion true -> activo, false -> inactivo.
     */
    public void setActivado(boolean opcion) {
        activado = opcion;
    }

    /**
     * Devuelve el modo de audio de la llamada, activo -> altavoz, inactivo -> auricular.
     * @return true si está activo, false en otro caso.
     */
    public boolean estaActivo(){ return activado;
    }

    /**
     * Activa el altavoz del móvil al máximo volumen y establece el modo en llamada.
     */
    public void enchufaElAltavoz() {
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        audioManager.setSpeakerphoneOn(true);
        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), 0);
        setActivado(true);
        AppLog.i(MLTAG + ".activarManosLibres", "Activado altavoz y volumen al máximo.");
    }

    /**
     * Método que restablece el modo normal y desactiva el altavoz.
     */
    public void desenchufaElAltavoz() {
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setSpeakerphoneOn(false);
        setActivado(false);
        AppLog.i(MLTAG + ".desenchufaElAltavoz()", "Desactivado altavoz y audio en el auricular.");
    }
}