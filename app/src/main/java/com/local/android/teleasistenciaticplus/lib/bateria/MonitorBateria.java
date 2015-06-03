package com.local.android.teleasistenciaticplus.lib.bateria;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.BatteryManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.local.android.teleasistenciaticplus.R;
import com.local.android.teleasistenciaticplus.act.main.actMain;
import com.local.android.teleasistenciaticplus.lib.helper.AppLog;
import com.local.android.teleasistenciaticplus.lib.helper.AppSharedPreferences;
import com.local.android.teleasistenciaticplus.lib.sms.SmsLauncher;
import com.local.android.teleasistenciaticplus.lib.sms.SmsTextGenerator;
import com.local.android.teleasistenciaticplus.lib.sound.SintetizadorVoz;
import com.local.android.teleasistenciaticplus.lib.stats.StatsFileLogTextGenerator;
import com.local.android.teleasistenciaticplus.modelo.GlobalData;
import com.local.android.teleasistenciaticplus.modelo.TipoAviso;

import static com.local.android.teleasistenciaticplus.modelo.Constants.*;

/**
 * Created by MORUGE on 14/05/2015.
 */
public class MonitorBateria
{
    // Atributos de la clase.
    private boolean activarAlInicio = false, receiverActivado = false, notificado = false;
    private boolean powerSafe = false, desactivarAlRecibir = false;
    private int nivelAlerta, nivel = 0, estado = 0;
    private BroadcastReceiver mBatInfoReceiver = null;
    private int tasaRefresco, contador;
    private static String TAG = "MonitorBateria";

    // Constructor sin parámetros.
    public MonitorBateria()
    {
        // Llamo al método que lee de las SharedPreferences y asigna los valores iniciales.
        cargaPreferencias();
        AppLog.i(TAG + ".Constructor","Preferencias cargadas: " +
                new AppSharedPreferences().dameCadenaPreferenciasMonitorBateria());

        // Inicio el contador a 0.
        contador = 0;

        // El receiver de eventos de bateria, declarado inline por exigencias androidianas
        mBatInfoReceiver = new BroadcastReceiver() // Terminado
        {
            // private int nivel, estado;

            @Override
            public void onReceive( final Context context, final Intent intent )
            {
                if(intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)
                        || intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED))
                {
                    notificado = false;
                }

                // Si es la accion del Intent es la comunicación de batería baja, primero comprueba
                // que no sea la alerta del 15%, y si no lo es manda un sms indiando que la batería
                // está a punto de de agotarse.
                if(intent.getAction().equals(Intent.ACTION_BATTERY_LOW))
                {
                    if(getNivel()<=5) {
                        String palabras = "¡¡¡Enviado SMS de aviso de Batería Descargada!!!";
                        SintetizadorVoz loro = actMain.getInstance().getSintetizador();
                        loro.hablaPorEsaBoquita(palabras);
                        new SmsLauncher(TipoAviso.SINBATERIA).generateAndSend();
                        Toast.makeText(GlobalData.getAppContext(), "Enviado SMS Batería Descargada",
                                Toast.LENGTH_LONG).show();
                        AppLog.i(TAG + ".onReceive", "Enviado un SMS de aviso por batería agotada");
                    }
                }

                // Acción a realizar cuando llega un evento de cambio de estado de batería. Se pide
                // información con la frecuencia establecida en la configuración. (Muchas peticiones,
                // mucho gasto...)
                if(intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED))
                {
                    // Con esta condición la tasa de refresco mínimo de comprobaciones es uno cada
                    // dos eventos si tasaRefresco es 0.
                    // El contador se tiene en cuenta la tasa de refresco solo si powerSafe es true,
                    // si no pasa al else.
                    if(powerSafe && contador < (tasaRefresco * 2) && hayDatos())
                    {
                        contador++;
                    }
                    else
                    {
                        // Extraigo los datos de nivel de carga y estado de batería del intent recibido.
                        nivel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                        estado = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
                        AppLog.i(TAG + ".onReceive()", "Recogido nivel = " + getNivel() + " y estado = " + textoEstado());

                        if ( STATS_LOG_BATERIA )
						{
                            /////////////////////////////////////////////////////
                            StatsFileLogTextGenerator.write("bateria", "nivel: " + nivel + " estado: " + textoEstado() + " tasa de refresco: " + tasaRefresco);
                            /////////////////////////////////////////////////////
                        }
						
                        // Guardo el dato de nivel de carga que acabo de leer.
                        guardaUltimoNivel();

                        // Actualizo datos del Layout y compruebo que el nivel de la batería esté por encima
                        // del nivel de alerta y que la batería no esté en carga.
                        // mostrarDatos(nivel, estado);
                        if ((nivel <= nivelAlerta) && estado != BatteryManager.BATTERY_STATUS_CHARGING)
                            // Lanzo una notificación
                            notificacion();

                        // Reinicio el contador
                        contador = 0;
                    }

                    // En este punto ya he recibido un dato, ya que si no había por ser la primera vez
                    // que se ha recibido alguno, la condición del if previo obliga a leer del intent.
                    // Si tengo activada la opción de desactivar el receiver al recibir lo hago.
                    if(getDesactivarAlRecibir())
                    {
                        setDesactivarAlRecibir(false);
                        desactivaReceiver(false);
                    }
                }
            }
        };

        // Hago una primera lectura de datos de batería. Para ello activo el receiver y seguidamente
        // pido desactivarlo, cosa que no hará hasta que reciba datos
        activaReceiver(false, false);
        desactivaReceiver(false);

        // Si estaba configurado para activarse al inicio lo vuelvo a lanzar con las preferencias.
        AppLog.i(TAG + ".Constructor", "Tengo la orden activarAlInicio = " + activarAlInicio);
        if(activarAlInicio)
            activaReceiver(true, false);
    }

    private void cargaPreferencias() // Termminado
    {
        // Saco el nivel de alerta y la opción de si se debe iniciar el receiver con la actividad.
        AppSharedPreferences prefMonitorBateria = new AppSharedPreferences();
        if(prefMonitorBateria.hayDatosBateria())
        {
            // Hay valores guardados, los leo
            nivelAlerta = prefMonitorBateria.damePreferenciasBateriaNivelAlerta();
            tasaRefresco = prefMonitorBateria.damePreferenciasBateriaTasaRefresco();
            activarAlInicio = prefMonitorBateria.damePreferenciasBAteriaActivarAlInicio();
        }
        else
        {
            // No hay valores guardados, pongo valores por defecto.
            activarAlInicio = false;
            nivelAlerta = 30;
            tasaRefresco = 4;
        }
    }

    private void guardaPreferencias() // Terminado
    {
        // Creo un editor para guardar las preferencias.
        AppSharedPreferences prefMonitorBateria = new AppSharedPreferences();
        prefMonitorBateria.escribePreferenciasBateriaNivelAlerta(getNivelAlerta());
        prefMonitorBateria.escribePreferenciasBateriaTasaRefresco(getTasaRefresco());
        prefMonitorBateria.escribePreferenciasBateriaActivarAlInicio(getActivarAlInicio());

        Toast.makeText(GlobalData.getAppContext(), "Configuración Guardada", Toast.LENGTH_SHORT).show();
        AppLog.i(TAG + ".guardaPreferencias()","Preferencias guardadas con valores: " +
                new AppSharedPreferences().dameCadenaPreferenciasMonitorBateria());
    }

    /**
     * Guarda el nivel de carga de batería más reciente.
     */
    private void guardaUltimoNivel()
    {
        AppSharedPreferences miSharedPref = new AppSharedPreferences();
        miSharedPref.escribeUltimoNivelRegistradoBateria(Integer.toString(nivel));
        AppLog.i(TAG + "guardaUltimoNivel()", "Guardado el último nivel de batería leído: " + nivel);
    }

    /**
     * Método que registra un receiver dinámicamente para que entre en funcionamiento.
     * @param ahorrar Bandera que indica si activar el modo de ahorro de energía.
     * @param tostar Bandera que indica si lanzar un Toast al registrar el receiver.
     */
    public void activaReceiver(boolean ahorrar, boolean tostar) // Terminado
    {
        if ( STATS_LOG_BATERIA ) {
            /////////////////////////////////////////////////////
            StatsFileLogTextGenerator.write("bateria", "reciver activo");
            /////////////////////////////////////////////////////
        }
		
        if(getReceiverActivo())
        {
            if(getDesactivarAlRecibir())
            {
                setDesactivarAlRecibir(false);
                setPowerSaver(ahorrar);
                AppLog.i(TAG + ".activaReceiver()", "Al intentar activar el receiver veo que ya está activo, pero " +
                        "tiene activada la bandera desactivarAlRecibir, por lo que está esperando datos para desactivarse " +
                        "al recibirlos. Desactivo la bandera y lo pongo a funcionar normalmente.");
                return;
            }
            // El receiver está activo, devuelvo un aviso.
            Toast.makeText(GlobalData.getAppContext(),"El Monitor de Batería ya está Activo",Toast.LENGTH_SHORT).show();
        }
        else
        {
            // Primero establezco el tasaRefresco de refresco a 0 para que lea inmediatamente la
            // Registro el receiver para activarlo con el filtro de eventos de cambio de bateria,
            // cargador conectado, y cargador desconectado.
            IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
            intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
            intentFilter.addAction(Intent.ACTION_BATTERY_LOW);
            GlobalData.getAppContext().registerReceiver(mBatInfoReceiver, intentFilter);

            setReceiverActivo(true);
            AppLog.i(TAG + ".activaReceiver()","Registrado receiver de batería...");

            if(tostar)
                Toast.makeText(GlobalData.getAppContext(),"Monitor Batería Activado",Toast.LENGTH_SHORT).show();

            // Establezco que use la tasa de refresco
            setPowerSaver(ahorrar);
        }
    }

    public void desactivaReceiver(boolean tostar) // Terminado
    {
        if(getReceiverActivo())
        {
            // La variable de control me dice que el receiver está registrado, lo quito.
            if(!hayDatos())
            {
                setDesactivarAlRecibir(true);
                AppLog.i(TAG + ".desactivaReceiver()","No hay datos, pongo desactivarAlRecibir = " + getDesactivarAlRecibir() +
                        ", getReceiverActivo = " + getReceiverActivo());
            }
            else
            {
                GlobalData.getAppContext().unregisterReceiver(mBatInfoReceiver);
                setReceiverActivo(false);
                powerSafe = false; // Desactivo el modo ahorro de energía (valor por defecto)
                if(tostar)
                    Toast.makeText(GlobalData.getAppContext(), "Monitor Batería Desactivado", Toast.LENGTH_SHORT).show();
                AppLog.i(TAG + ".desactivaReceiver()","Desactivado receiver.");
            }
        }
        else
            // El receiver está desactivado, lo aviso.
            if(tostar)
            {
                if(getDesactivarAlRecibir())
                    return;
                Toast.makeText(GlobalData.getAppContext(),"El Monitor de Batería ya está inactivo",Toast.LENGTH_SHORT).show();
            }
    }

    public void setPowerSaver(boolean valor)
    {
        powerSafe = valor;
    }
    public boolean getPowerSaver()
    {
        return powerSafe;
    }
    public void setDesactivarAlRecibir(boolean opcion)
    {
        desactivarAlRecibir = opcion;
    }
    public boolean getDesactivarAlRecibir()
    {
        return desactivarAlRecibir;
    }
    public int getNivel() { return nivel; }
    public int getEstado() { return estado; }
    public Boolean getReceiverActivo() { return receiverActivado; } // Terminado
    public void setReceiverActivo(boolean op) { receiverActivado = op; }
    public int getNivelAlerta() { return nivelAlerta; } // Terminado
    public void setNivelAlerta(int alertLevel) { nivelAlerta = alertLevel; } // Terminado
    public Boolean getActivarAlInicio() { return activarAlInicio; } // Terminado
    public void setActivarAlInicio(Boolean alInicio) { activarAlInicio = alInicio; } // Terminado
    public int getTasaRefresco() { return tasaRefresco; } // Terminado
    public void setTasaRefresco(int tasa) { tasaRefresco = tasa; } // Terminado
    public void commit(){ guardaPreferencias(); } // Terminado
    public boolean hayDatos() {
        // Devuelvo true si estado != 0, que significa que ha leido algo
        return (nivel != 0 && estado != 0);
    }
    public void notificacion() // Terminado
    {
        // Pillo el servicio de notificaciones del sistema.
        NotificationManager notificador = (NotificationManager)GlobalData.getAppContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);

        if(!notificado)
        {
            int idNotificacion=0;
            // Construcción de la notificación
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(GlobalData.getAppContext())
                    .setSmallIcon(R.drawable.logo_transparente_1x1)
                    .setContentText("Nivel de carga: " + String.valueOf(nivel) + "%")
                            // .setContentInfo(creaCadenaEstado(estado))
                    .setContentTitle("AVISO BATERIA")
                    .setLargeIcon(BitmapFactory.decodeResource(GlobalData.getAppContext().getResources(),
                            R.drawable.logo_transparente_1x1))
                    .setAutoCancel(true)
                            // Asigno Intent vacio para que al pulsar quite la notificacion pero no haga nada.
                    .setContentIntent(PendingIntent.getActivity(
                            GlobalData.getAppContext().getApplicationContext(), 0, new Intent(), 0));
            //.setContentIntent(resultPendingIntent); // Utilizado para lanzar algo al pulsar en la notificación.

            // Lanzo la notificación.
            Notification notif = mBuilder.build();
            notif.defaults |= Notification.DEFAULT_VIBRATE;
            //notif.defaults |= Notification.DEFAULT_SOUND;
            notificador.notify(idNotificacion, notif);
            Log.i("Notificador", "Notificacion lanzada con id = " + idNotificacion);
            notificado = true;

            // Lanzo también el nivel de batería por voz.
            SintetizadorVoz loro = actMain.getInstance().getSintetizador();
            loro.hablaPorEsaBoquita("¡Atención!. " + textoNivel() + ". Por favor, ponga el móvil a cargar.");
			
            /////////////////////////////////////////////////////
            StatsFileLogTextGenerator.write("bateria", "notificacion bateria baja");
            /////////////////////////////////////////////////////
        }
    }

    public  String textoEstado() // Terminado
    {
        String strEstado;
        switch(getEstado())
        {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                strEstado = "Cargando";
                break;
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                strEstado = "Descargando";
                break;
            case BatteryManager.BATTERY_STATUS_FULL:
                strEstado = "Llena";
                break;
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                strEstado = "No está cargando";
                break;
            case BatteryManager.BATTERY_STATUS_UNKNOWN:
                strEstado = "Desconocido";
                break;
            default:
                strEstado = "Error";
                break;
        }
        return strEstado;
    }

    public String textoNivel() // Terminado
    {
        return "Nivel de carga: " + String.valueOf(getNivel()) + "%";
    }

}

///////////////////////////////////////////////////////////////////////
// El siguiente codigo crea una llamada a una actividad que será
// llamada al pulsar sobre la notificación generada, y además crea
// la pila de tareas para la navegación generada por la notificación,
// con el fin de saber a que actividad debe volver cuando pulsemos la
// tecla de volver o una posible acción "salir" de un menú. Se deja
// comentado para un posible uso futuro.
///////////////////////////////////////////////////////////////////////
        /*
        // Creo el Intent que llamará a la activity de configuracion.
         Intent lanzaConfig = new Intent(contexto,Configurador.class);

        // Creo la pila de navegación de notificación.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(GlobalData.getAppContext());

        // Añade la pila de vuelta atrás para el intent, pero no el intent en si mismo,
        stackBuilder.addParentStack(Configurador.class);

        // Añade el intent que lanza la actividad en el top de la pila
        stackBuilder.addNextIntent(lanzaConfig);

        // Obtengo el PendingIntent para la notificación y lo agrego al Builder.
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);
        */
