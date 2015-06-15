package com.fundacionmagtel.android.teleasistenciaticplus.lib.bateria;

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
import android.widget.Toast;

import com.fundacionmagtel.android.teleasistenciaticplus.act.main.actMain;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.helper.AppLog;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.sms.SmsLauncher;
import com.fundacionmagtel.android.teleasistenciaticplus.modelo.Constants;
import com.fundacionmagtel.android.teleasistenciaticplus.modelo.GlobalData;
import com.fundacionmagtel.android.teleasistenciaticplus.modelo.TipoAviso;
import com.fundacionmagtel.android.teleasistenciaticplus.R;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.helper.AppSharedPreferences;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.sound.SintetizadorVoz;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.stats.StatsFileLogTextGenerator;

/**
 * Clase para monitorizar la batería, proporcionando métodos para activarlo y desactivarlo cuando se
 * requiera, proporcionar una tasa de refresco de actualización de los valores para controlar el
 * consumo de energía, generar notificaciones del sistema con avisos del nivel de batería restante y
 * SMS de aviso a familiares con la posición GPS cuando el nivel de batería es crítico.
 * Created by MORUGE on 14/05/2015.
 * @author German Moreno
 */
public class MonitorBateria
{
    // Atributos de la clase.
    /** Bandera que indica si el Monitor de Batería debe iniciarse con la aplicación. */
    private boolean activarAlInicio = false;
    /** Bandera que indica si el Monitor de Batería está activo. */
    private boolean receiverActivado = false;
    /** Bandera que indica si se ha notificado un aviso nivel de batería. */
    private boolean notificado = false;
    /** Bandera que indica si hay fijada una tasa de refresco. */
    private boolean powerSafe = false;
    /** Bandera que indica si el Monitor se debe desactirvar a recibir el siguiente evento. */
    private boolean desactivarAlRecibir = false;
    /** Entero que guarda el nivel de carga sobrepasado el cual debe mostrar una notificación. */
    private int nivelAlerta;
    /** Byte que almacena el nivel de carga actual de nuestro móvil. */
    private byte nivel = 0;
    /** Byte que almacena el valor numérico del estado de carga en el que se encuentra el móvil. */
    private byte estado = 0;
    /** Receptor de eventos */
    private BroadcastReceiver mBatInfoReceiver = null;
    /** Entero con el valor de la tasa de refresco de los datos de la batería. */
    private int tasaRefresco;
    /** Entero que se utiliza para controlar el número de eventos recibidos del tipo cambio de
     * estado de la batería que hay que ignorar.
     */
    private int contador;
    /** Etiqueta para identificar las líneas de monitor de bateria en el LOG */
    private static String TAG = "MonitorBateria";
    /** Almacena el último intent de cambio de estado de batería que ha recogido el BroadcastReceiver */
    private Intent ultimoIntentRecibido = null;


    /**
     * Constructor de la clase sin parámetros.
     * Carga las preferencias almacenadas en SharedPreferences, declara un BroadcastReceiver inline
     * que recibe los eventos de batería que se han filtrado y llama a los métodos correspondientes
     * a las acciones a realizar con cada evento.
     */
    public MonitorBateria()
    {
        // Llamo al método que lee de las SharedPreferences y asigna los valores iniciales.
        cargaPreferencias();
        AppLog.i(TAG + ".Constructor", "Preferencias cargadas: " +
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
                String accion = intent.getAction();
                AppLog.i(TAG + ".onReceive", "Evento recibido: " + accion);
                if(accion.equals(Intent.ACTION_POWER_CONNECTED)
                        || accion.equals(Intent.ACTION_POWER_DISCONNECTED))
                {
                    notificado = false;
                }

                // Si es la accion del Intent es la comunicación de batería baja, primero comprueba
                // que no sea la alerta del 15%, y si no lo es manda un sms indiando que la batería
                // está a punto de de agotarse.
                if(accion.equals(Intent.ACTION_BATTERY_LOW))
                {
                    // Mando mensaje de texto
                    String palabras = "Enviado SMS de aviso de Batería Descargada.";
                    AppLog.i(TAG + ".onReceive()", "Enviando un SMS de aviso por batería agotada");
                    SintetizadorVoz loro = actMain.getInstance().getSintetizador();
                    loro.hablaPorEsaBoquita(palabras);
                    new SmsLauncher(TipoAviso.SINBATERIA).generateAndSend();
                    Toast.makeText(GlobalData.getAppContext(), "Enviado SMS Batería Descargada",
                            Toast.LENGTH_LONG).show();
                }

                // Acción a realizar cuando llega un evento de cambio de estado de batería. Se pide
                // información con la frecuencia establecida en la configuración. (Muchas peticiones,
                // mucho gasto...)
                if(accion.equals(Intent.ACTION_BATTERY_CHANGED))
                {
                    // Guardo el intent para cuando nos demanden los niveles llamando a los métodos
                    // oportunos.
                    ultimoIntentRecibido = intent;
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
                        nivel = (byte)intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                        estado = (byte)intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
                        AppLog.i(TAG + ".onReceive()", "Recogido nivel = " + getNivel() +
                                " y estado = " + textoEstado());

                        if ( Constants.STATS_LOG_BATERIA )
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
        // pido desactivarlo, cosa que no hará hasta que reciba datos.
        activaReceiver(false, false);
        desactivaReceiver(false);

        // Si estaba configurado para activarse al inicio lo vuelvo a lanzar con las preferencias.
        AppLog.i(TAG + ".Constructor", "Tengo la orden activarAlInicio = " + activarAlInicio);
        if(activarAlInicio)
            activaReceiver(true, false);
    }

    /**
     * Método que carga las preferencias de funcionamiento del Monitor de batería que han sido
     * guardadas en SharedPreferences. Si no encuentra datos establece los valores por defecto:
     * No activar al inicio, carga de la batería al 30% para lanzar un aviso y una tasa de refresco
     * media.
     */
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

    /**
     * Guarda en SharedPreferences los valores de nivel de alerta, tasa de refresco y de iniciar
     * con la aplicaci&oacute;n. Estos valores ser&aacute;n usados en posteriores inicios de la
     * aplicaci&oacute;n.
     */
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
     * Guarda el &uacute;ltimo nivel de carga de bater&iacute;a recogido.
     */
    private void guardaUltimoNivel()
    {
        AppSharedPreferences miSharedPref = new AppSharedPreferences();
        miSharedPref.escribeUltimoNivelRegistradoBateria(Integer.toString(nivel));
        AppLog.i(TAG + "guardaUltimoNivel()", "Guardado el último nivel de batería leído: " + nivel);
    }

    /**
     * Método que registra el receiver declarado en el constructor de la clase con los filtros de
     * eventos de batería de cambio de estado, cargador conectado, desconectado y batería baja.
     * @param ahorrar Bandera que indica si activar el modo de ahorro de energía.
     * @param tostar Bandera que indica si lanzar un Toast al registrar el receiver.
     */
    public void activaReceiver(boolean ahorrar, boolean tostar) // Terminado
    {
        if ( Constants.STATS_LOG_BATERIA ) {
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
            IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_LOW);
            intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
            intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
            intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
            GlobalData.getAppContext().registerReceiver(mBatInfoReceiver, intentFilter);

            setReceiverActivo(true);
            AppLog.i(TAG + ".activaReceiver()","Registrado receiver de batería...");

            if(tostar)
                Toast.makeText(GlobalData.getAppContext(),"Monitor Batería Activado",Toast.LENGTH_SHORT).show();

            // Establezco que use la tasa de refresco
            setPowerSaver(ahorrar);
        }
    }

    /**
     * Desregistra el receiver haciendo que deje de recibir eventos y anulando su funcionamiento.
     * @param tostar Bandera que indica si se ha de lanzar un Toast al desregistrar el receiver.
     */
    public void desactivaReceiver(boolean tostar) // Terminado
    {
        if(getReceiverActivo())
        {
            // La variable de control me dice que el receiver está registrado, lo quito.
            if(!hayDatos())
            {
                // Estamos en el inicio de la aplicación y aun no han llegado datos de batería.
                // Mantengo el receiver activo y subo la bandera de desactivar al recibir.
                setDesactivarAlRecibir(true);
                AppLog.i(TAG + ".desactivaReceiver()","No hay datos, pongo desactivarAlRecibir = " +
                        getDesactivarAlRecibir() + ", getReceiverActivo = " + getReceiverActivo());
            }
            else
            {
                // Desregistro el receiver.
                GlobalData.getAppContext().unregisterReceiver(mBatInfoReceiver);
                setReceiverActivo(false);
                // Desactivo el modo ahorro de energía (valor por defecto)
                powerSafe = false;
                if(tostar)
                    Toast.makeText(GlobalData.getAppContext(), "Monitor Batería Desactivado",
                            Toast.LENGTH_SHORT).show();
                AppLog.i(TAG + ".desactivaReceiver()","Desactivado receiver.");
            }
        }
        else
            // El receiver está desactivado, lo aviso.
            if(tostar)
            {
                // Si está activo esperando a recibir antes de desactivarse retorno sin más.
                if(getDesactivarAlRecibir())
                    return;
                // Notifico que ya se había desactivado.
                Toast.makeText(GlobalData.getAppContext(),"El Monitor de Batería ya está inactivo",
                        Toast.LENGTH_SHORT).show();
            }
    }

    /**
     * Activa o desactiva el modo de ahorro de energía del Monitor de Batería.
     * @param valor true para activarlo, false en caso contrario.
     */
    public void setPowerSaver(boolean valor)
    {
        powerSafe = valor;
    }

    /**
     * Informa sobre el estado del modo de ahorro de energía del Monitor de Batería.
     * @return true si está activo, false en caso contrario.
     */
    public boolean getPowerSaver()
    {
        return powerSafe;
    }

    /**
     * Establece si el receiver quedará a la espera de recibir un nuevo evento, desactivándose al
     * recibirlo.
     * @param opcion true activa este modo de operación, false lo desactiva.
     */
    public void setDesactivarAlRecibir(boolean opcion)
    {
        desactivarAlRecibir = opcion;
    }

    /**
     * Indica si el receiver está a la espera de recibir un nuevo evento, desactivándose al recibirlo.
     * @return true si está a la espera, false en otro caso.
     */
    public boolean getDesactivarAlRecibir()
    {
        return desactivarAlRecibir;
    }

    /**
     * Devuelve el nivel de carga de la batería en tanto por ciento.
     * @return El nivel de carga.
     */
    public int getNivel() 
    {
        return ultimoIntentRecibido.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
    }

    /**
     * Devuelve el valor numérico del estado de la batería.
     * @return El valor numérico del estado.
     */
    public int getEstado()
    { 
        return ultimoIntentRecibido.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
    }

    /**
     * Devuelve el estado del receiver, activo o inactivo
     * @return True si está activo, false en otro caso.
     */
    public Boolean getReceiverActivo() { return receiverActivado; } // Terminado

    /**
     * Establece el valor del indicador de estado del receiver.
     * @param op True o false
     */
    public void setReceiverActivo(boolean op) { receiverActivado = op; }

    /**
     * Devuelve el nivel de batería al que se notificará un aviso de batería baja.
     * @return El nivel de batería al que se lanza la nitificación.
     */
    public int getNivelAlerta() { return nivelAlerta; } // Terminado

    /**
     * Establece el nivel de batería al que se notificará un aviso de batería baja.
     * @param alertLevel El nivel de batería al que se lanza la nitificación.
     */
    public void setNivelAlerta(int alertLevel) { nivelAlerta = alertLevel; } // Terminado

    /**
     * Devuelve si el receiver está configurado para iniciarse con la app.
     * @return True si se iniciará con la app, false en otro caso.
     */
    public Boolean getActivarAlInicio() { return activarAlInicio; } // Terminado

    /**
     * Establece si el receiver está configurado para iniciarse con la app.
     * @param alInicio True si se iniciará con la app, false en otro caso.
     */
    public void setActivarAlInicio(Boolean alInicio) { activarAlInicio = alInicio; } // Terminado

    /**
     * Devuelve la tasa de refresco según la cual se consultarán datos de la batería al llegar un
     * evento de cambio de estado de la misma.
     * @return Entero con la tasa de refresco.
     */
    public int getTasaRefresco() { return tasaRefresco; } // Terminado

    /**
     * Establece la tasa de refresco según la cual se consultarán datos de la batería al llegar un
     * evento de cambio de estado de la misma.
     * @param tasa Entero con la nueva tasa de refresco.
     */
    public void setTasaRefresco(int tasa) { tasaRefresco = tasa; } // Terminado

    /**
     * Llama al método que guarda las preferencias, una vez están listos los valores que han de
     * tener.
     */
    public void commit(){ guardaPreferencias(); } // Terminado

    /**
     * Devuelve si se ha recogido algún dato de batería.
     * @return True si se han recogido, false en otro caso.
     */
    public boolean hayDatos() {
        return ultimoIntentRecibido!=null;
    }
    
    /**
     * Lanza una notificación del sistema, alertando de que la batería está por debajo del nivel de
     * Alerta. Cuando esto ocurre además de la notificación se envía un SMS un falimiar.
     */
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
            AppLog.i("Notificador", "Notificacion lanzada con id = " + idNotificacion);
            notificado = true;

            // Lanzo también el nivel de batería por voz.
            SintetizadorVoz loro = actMain.getInstance().getSintetizador();
            loro.hablaPorEsaBoquita("¡Atención!. " + textoNivel() + ". Por favor, ponga el móvil a cargar.");
			
            /////////////////////////////////////////////////////
            StatsFileLogTextGenerator.write("bateria", "notificacion bateria baja");
            /////////////////////////////////////////////////////
        }
    }

    /**
     * Genera una cadena de texto con el estado de la batería.
     * @return String con el estado de batería correspondiente.
     */
    public String textoEstado() // Terminado
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

    /**
     * Genera una cadena de texto con el nivel de carga de la batería.
     * @return String con el nivel de carga.
     */
    public String textoNivel() // Terminado
    {
        return String.valueOf(getNivel()) + "%";
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
