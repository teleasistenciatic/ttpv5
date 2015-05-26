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
import com.local.android.teleasistenciaticplus.lib.sound.SintetizadorVoz;
import com.local.android.teleasistenciaticplus.modelo.GlobalData;

/**
 * Created by MORUGE on 14/05/2015.
 */
public class MonitorBateria
{
    // Atributos de la clase.
    private static boolean activarAlInicio = false, receiverActivado = false, notificado = false;
    private static boolean powerSafe = false, desactivarAlRecibir = false;
    private static int nivelAlerta, nivel = 0, estado = 0;
    private static BroadcastReceiver mBatInfoReceiver = null;
    private static int intervalo, contador;

    // Constructor sin parámetros.
    public MonitorBateria()
    {
        // Llamo al método que lee de las SharedPreferences y asigna los valores iniciales.
        cargaPreferencias();

        // Inicio el contador a 0.
        contador = 0;

        // El receiver de eventos de bateria, declarado inline por exigencias androidianas
        mBatInfoReceiver = new BroadcastReceiver() // Terminado
        {
            // private int nivel, estado;

            @Override
            public void onReceive( final Context context, final Intent intent )
            {
                if(intent.getAction().equals(Intent.ACTION_POWER_CONNECTED) ||
                        intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED))
                {
                    notificado = false;
                }
                else
                {
                    // Con esta condición el intervalo mínimo de comprobaciones es uno cada dos eventos si intervalo es 0.
                    // El contador se tiene en cuenta solo si powerSafe es true, si no pasa al else.
                    if(powerSafe && contador < intervalo && hayDatos())
                        contador++;
                    else
                    {
                        // Extraigo los datos de nivel de carga y estado de batería del intent recibido.
                        nivel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                        estado = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
                        Log.i("onReceive", "Recogido nivel = " + nivel + " y estado = " + estado);

                        // Guardo el dato de nivel de carga que acabo de leer.
                        guardaUltimoNivel();

                        // Actualizo datos del Layout y compruebo que el nivel de la batería esté por encima
                        // del nivel de alerta y que la batería no esté en carga.
                        // mostrarDatos(nivel, estado);
                        if ((nivel <= nivelAlerta && estado != BatteryManager.BATTERY_STATUS_CHARGING))
                            // Lanzo una notificación
                            notificacion();

                        // Reinicio el contador
                        contador = 0;
                    }
                }

                // En este punto ya he recibido un dato, ya que si no había por ser la primera vez
                // que se ha recibido alguno, la condición del if previo obliga a leer del intent.
                // Si tengo activada la opción de desactivar el receiver al recibir lo hago.
                if(getDesactivarAlRecibir())
                {
                    desactivaReceiver(false);
                    setDesactivarAlRecibir(false);
                }
            }
        };

        // Hago una primera lectura de datos de batería. Para ello activo el receiver y seguidamente
        // pido desactivarlo, cosa que no hará hasta que reciba datos
        activaReceiver(false, false);
        desactivaReceiver(false);

        // Si estaba configurado para activarse al inicio lo vuelvo a lanzar con las preferencias.
        AppLog.i("constructor MonitorBateria", "Tengo la orden activarAlInicio = " + activarAlInicio);
        if(activarAlInicio)
            activaReceiver(true, false);
    }

    private static void cargaPreferencias() // Termminado
    {
        // Saco el nivel de alerta y la opción de si se debe iniciar el receiver con la actividad.
        AppSharedPreferences miSharedPref = new AppSharedPreferences();
        if(miSharedPref.hasPreferenceData("NivelAlerta") && miSharedPref.hasPreferenceData("ActivarAlInicio")
                && miSharedPref.hasPreferenceData("Intervalo"))
        {
            // Hay valores guardados, los leo
            nivelAlerta = Integer.parseInt(miSharedPref.getPreferenceData("NivelAlerta"));
            intervalo = Integer.parseInt(miSharedPref.getPreferenceData("Intervalo"));
            activarAlInicio = Boolean.parseBoolean(miSharedPref.getPreferenceData("ActivarAlInicio"));
        }
        else
        {
            // No hay valores guardados, pongo valores por defecto.
            activarAlInicio = false;
            nivelAlerta = 30;
            intervalo = 6;
        }
        AppLog.i("Batería", "Preferencias cargadas: nivelAlerta = " + nivelAlerta +
                ", activarAlInicio = " + activarAlInicio +
                ", Intervalo = " + intervalo);
    }

    private static void guardaPreferencias() // Terminado
    {
        // Creo un editor para guardar las preferencias.
        AppSharedPreferences miSharedPref = new AppSharedPreferences();
        miSharedPref.setPreferenceData("NivelAlerta", Integer.toString(nivelAlerta));
        miSharedPref.setPreferenceData("Intervalo", Integer.toString(intervalo));
        miSharedPref.setPreferenceData("ActivarAlInicio", Boolean.toString(activarAlInicio));
        Toast.makeText(GlobalData.getAppContext(), "Configuración Guardada", Toast.LENGTH_SHORT).show();
        Log.i("guardaPreferencias","Preferencias guardadas con valores: nivelAlerta = " +
                nivelAlerta + ", activarAlInicio = " + activarAlInicio +
                "Intervalo de refresco = " + intervalo);
    }

    private static void guardaUltimoNivel()
    {
        AppSharedPreferences miSharedPref = new AppSharedPreferences();
        miSharedPref.setPreferenceData("UltimoNivelBateria", Integer.toString(nivel));
        Log.i("guardaUltimoNivel", "Guardado el último nivel de batería leído: " + nivel);
    }


    public void activaReceiver(boolean ahorrar, boolean tostar) // Terminado
    {
        if(getReceiverActivo())
            // El receiver está activo, devuelvo un aviso.
            Toast.makeText(GlobalData.getAppContext(),"El Monitor de Batería ya está Activo",Toast.LENGTH_SHORT).show();
        else
        {
            // Primero establezco el intervalo de refresco a 0 para que lea inmediatamente la
            // Registro el receiver para activarlo con el filtro de eventos de cambio de bateria,
            // cargador conectado, y cargador desconectado.
            IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
            intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
            GlobalData.getAppContext().registerReceiver(mBatInfoReceiver, intentFilter);

            setReceiverActivo(true);
            Log.i("activaReceiver","Registrado receiver de batería...");

            if(tostar)
                Toast.makeText(GlobalData.getAppContext(),"Monitor Batería Activado",Toast.LENGTH_SHORT).show();

            // Establezco el intervalo de refresco
            powerSafe = ahorrar;
        }
    }

    public void desactivaReceiver(boolean tostar) // Terminado
    {
        if(getReceiverActivo())
        {
            // La variable de control me dice que el receiver está registrado, lo quito.
            if(!hayDatos())
                setDesactivarAlRecibir(true);
            else
            {
                GlobalData.getAppContext().unregisterReceiver(mBatInfoReceiver);
                setReceiverActivo(false);
                powerSafe = false; // Desactivo el modo ahorro de energía (valor por defecto)
                if(tostar)
                    Toast.makeText(GlobalData.getAppContext(), "Monitor Batería Desactivado", Toast.LENGTH_SHORT).show();
            }
        }
        else
            // El receiver está desactivado, lo aviso.
            if(tostar)
                Toast.makeText(GlobalData.getAppContext(),"El Monitor de Batería ya está inactivo",Toast.LENGTH_SHORT).show();
    }

    public static void setDesactivarAlRecibir(boolean opcion)
    {
        desactivarAlRecibir = opcion;
    }
    public static boolean getDesactivarAlRecibir()
    {
        return desactivarAlRecibir;
    }
    public static int getNivel() { return nivel; }
    public static int getEstado() { return estado; }
    public Boolean getReceiverActivo() { return receiverActivado; } // Terminado
    public void setReceiverActivo(boolean op) { receiverActivado = op; }
    public int getNivelAlerta() { return nivelAlerta; } // Terminado
    public void setNivelAlerta(int alertLevel) { nivelAlerta = alertLevel; } // Terminado
    public Boolean getActivarAlInicio() { return activarAlInicio; } // Terminado
    public void setActivarAlInicio(Boolean alInicio) { activarAlInicio = alInicio; } // Terminado
    public int getIntervalo() { return intervalo; } // Terminado
    public void setIntervalo(int interval) { intervalo = interval; } // Terminado
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
                    .setContentTitle("POCA BATERIA")
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
        }
    }

    public static String textoEstado() // Terminado
    {
        String strEstado;
        switch(getEstado())
        {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                strEstado = "Bateria en carga...";
                break;
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                strEstado = "Descargando bateria...";
                break;
            case BatteryManager.BATTERY_STATUS_FULL:
                strEstado = "Bateria a plena carga...";
                break;
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                strEstado = "La bateria no esta cargando...";
                break;
            case BatteryManager.BATTERY_STATUS_UNKNOWN:
                strEstado = "Estado de la bateria desconocido...";
                break;
            default:
                strEstado = "Figureseeeeeeeee...";
                break;
        }
        return strEstado;
    }

    public static String textoNivel() // Terminado
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
