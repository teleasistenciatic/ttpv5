package com.fundacionmagtel.android.teleasistenciaticplus.act.debug;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fundacionmagtel.android.teleasistenciaticplus.modelo.GlobalData;
import com.fundacionmagtel.android.teleasistenciaticplus.R;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.helper.AppSharedPreferences;

public class actDebugOjeadorBateria extends Activity
{
    private static NotificationManager notificador;
    private static TextView tvEstado, tvNivel, tvReceiver;
    private static EditText etNivelAlerta;
    private static Button btnLanzarReceiver, btnPararReceiver, btnAplicar, btnSalir;
    private static CheckBox cbIniciarAuto;
    private static Boolean activarAlInicio = false, receiverActivado = false, notificado = false;
    private static int nivelAlerta=0;
    private static BroadcastReceiver mBatInfoReceiver = null;

    @Override
    public void onCreate(Bundle savedInstanceState) // Terminado
    {
        // Acciones a ejecutar al crear la actividad
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_act_debug_ojeador_bateria);

        // Pillo el servicio de notificaciones del sistema.
        notificador = (NotificationManager) GlobalData.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // Llamo al método que lee de las SharedPreferences y asigna los valores iniciales.
        cargaPreferencias();

        // Inicializo el layout
        tvEstado = (TextView)findViewById(R.id.tvEstado);
        tvNivel = (TextView)findViewById(R.id.tvNivel);
        tvReceiver = (TextView)findViewById(R.id.tvReceiver);
        tvNivel.setText("Sin recepción de datos");
        tvEstado.setText("Monitor de batería");
        tvReceiver.setText("Desactivado");

        etNivelAlerta = (EditText)findViewById(R.id.etNivelAlerta);
        etNivelAlerta.setText(Integer.toString(nivelAlerta));

        btnLanzarReceiver = (Button)findViewById(R.id.btnLanzarReceiver);
        btnLanzarReceiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                activaReceiver();
            }
        });

        btnPararReceiver = (Button)findViewById(R.id.btnPararReceiver);
        btnPararReceiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                desactivaReceiver();
            }
        });

        cbIniciarAuto = (CheckBox)findViewById(R.id.cbIniciarAuto);
        cbIniciarAuto.setChecked(activarAlInicio);

        /**************** No necesito recoger los eventos de marcar/desmarcar la casilla **********
        cbIniciarAuto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()

        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                activarAlInicio = isChecked;
                Log.i("onCheckedChanged","Establecido activarAlInicio a " + isChecked);
            }
        });
        ******************************************************************************************/

        btnAplicar = (Button)findViewById(R.id.btnAplicar);
        btnAplicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // Actualizo el valor de los atributos afectados por cambios.
                String alerta = etNivelAlerta.getText().toString();
                if(!alerta.isEmpty())
                    nivelAlerta = Integer.parseInt(alerta);
                activarAlInicio = cbIniciarAuto.isChecked();
                guardaPreferencias();
            }
        });

        btnSalir = (Button)findViewById(R.id.btnSalir);
        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        // El receiver
        mBatInfoReceiver = new BroadcastReceiver() // Terminado
        {
            private int nivel, estado;

            @Override
            public void onReceive( final Context ctxt, final Intent intent )
            {
                // Extraigo los datos de nivel de carga y estado de batería del intent recibido.
                nivel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                estado = intent.getIntExtra(BatteryManager.EXTRA_STATUS,0);
                // Actualizo datos del Layout y compruebo que el nivel de la batería esté por encima
                // del nivel de alerta y que la batería no esté en carga.
                mostrarDatos(nivel, estado);
                if((nivel<=nivelAlerta && estado != BatteryManager.BATTERY_STATUS_CHARGING))
                    // Lanzo una notificación
                    notificacion(nivel, estado);
                // Si estoy cargando la batería reestablezco el flag de notificado a false.
                if(estado == BatteryManager.BATTERY_STATUS_CHARGING)
                    notificado = false;
            }
        };

        if(activarAlInicio)
            activaReceiver();
    }


    @Override
    public void onDestroy() // Terminado
    {
        super.onDestroy();
        if(receiverActivado)
            desactivaReceiver();
        // Reinicio el notificador
        notificado = false;
    }

    private static void cargaPreferencias() // Termminado
    {
        // Saco el nivel de alerta y la opción de si se debe iniciar el receiver con la actividad.
        AppSharedPreferences miSharedPref = new AppSharedPreferences();
        if(miSharedPref.hasPreferenceData("NivelAlerta") &&
                miSharedPref.hasPreferenceData("ActivarAlInicio"))
        {
            // Hay valores guardados, los leo
            nivelAlerta = Integer.parseInt(miSharedPref.getPreferenceData("NivelAlerta"));
            activarAlInicio = Boolean.parseBoolean(miSharedPref.getPreferenceData("ActivarAlInicio"));
        }
        else
        {
            // No hay valores guardados, pongo valores por defecto.
            activarAlInicio = false;
            nivelAlerta = 30;
        }
        Log.i("OjeadorBateria", "Preferencias cargadas: nivelAlerta = " + nivelAlerta +
                ", activarAlInicio = " + activarAlInicio);
    }

    private static void guardaPreferencias() // Terminado
    {
        // Creo un editor para guardar las preferencias.
        AppSharedPreferences miSharedPref = new AppSharedPreferences();
        miSharedPref.setPreferenceData("NivelAlerta", Integer.toString(nivelAlerta));
        miSharedPref.setPreferenceData("ActivarAlInicio", Boolean.toString(activarAlInicio));
        Toast.makeText(GlobalData.getAppContext(),"Cambios guardados",Toast.LENGTH_SHORT).show();
        Log.i("guardaPreferencias","Preferencias guardadas con valores: nivelAlerta = " +
                nivelAlerta + ", activarAlInicio = " + activarAlInicio);
    }

    public static void activaReceiver() // Terminado
    {
        if(receiverActivado)
            // El receiver está activo, devuelvo un aviso.
            Toast.makeText(GlobalData.getAppContext(),"El Receiver ya está Activo",Toast.LENGTH_SHORT).show();
        else
        {
            // Registro el receiver para activarlo.
            GlobalData.getAppContext().registerReceiver(mBatInfoReceiver,
                    new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            receiverActivado = true;
            Toast.makeText(GlobalData.getAppContext(),"Receiver Activado",Toast.LENGTH_SHORT).show();
        }
    }

    public static void desactivaReceiver() // Terminado
    {
        if(receiverActivado)
        {
            // La variable de control me dice que el receiver está registrado, lo quito.
            GlobalData.getAppContext().unregisterReceiver(mBatInfoReceiver);
            receiverActivado = false;
            tvNivel.setText("Sin recepción de datos");
            tvEstado.setText("Monitor de batería");
            tvReceiver.setText("Desactivado");
            Toast.makeText(GlobalData.getAppContext(), "Receiver Desactivado", Toast.LENGTH_SHORT).show();
        }
        else
            // El receiver está desactivado, lo aviso.
            Toast.makeText(GlobalData.getAppContext(),"El Receiver ya está inactivo",Toast.LENGTH_SHORT).show();
    }

    private static String creaCadenaEstado(int estado) // Terminado
    {
        String strEstado;
        switch(estado)
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

    private static String creaCadenaNivel(int nivel) // Terminado
    {
        return "Nivel de carga: " + String.valueOf(nivel) + "%";
    }


    public static int pideNivelAlerta() { return nivelAlerta; } // Terminado

    public static void mostrarDatos(int nivel, int estado) // Terminado
    {
        tvNivel.setText(creaCadenaNivel(nivel));
        tvEstado.setText(creaCadenaEstado(estado));
        if(receiverActivado)
            tvReceiver.setText("Receiver Activado");
        else
            tvReceiver.setText("Receiver Desactivado");
    }

    public static void notificacion(int nivel, int estado) // Terminado
    {
        if(!notificado)
        {
            int idNotificacion=0;
            // Construcción de la notificación
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(GlobalData.getAppContext())
                    .setSmallIcon(R.drawable.logo_transparente_1x1)
                    .setContentText(creaCadenaNivel(nivel))
                    // .setContentInfo(creaCadenaEstado(estado))
                    .setContentTitle("POCA BATERIA")
                    .setLargeIcon(BitmapFactory.decodeResource(GlobalData.getAppContext().getResources(),
                            R.drawable.logo_transparente_1x1))
                    .setAutoCancel(true)
                    // Asigno Intent vacio para que al pulsar quite la notificacion pero no haga nada.
                    .setContentIntent(PendingIntent.getActivity(
                            GlobalData.getAppContext().getApplicationContext(), 0, new Intent(), 0));
            //.setContentIntent(resultPendingIntent);

            // Lanzo la notificación.
            Notification notif = mBuilder.build();
            notif.defaults |= Notification.DEFAULT_VIBRATE;
            notif.defaults |= Notification.DEFAULT_SOUND;
            notificador.notify(idNotificacion, notif);
            Log.i("Notificador", "Notificacion lanzada con id = " + idNotificacion);
            notificado = true;
        }

    }

        ////////////////////////////////////////////////////////////////////
        /* El siguiente codigo crea una llamada a una actividad que será
         * llamada al pulsar sobre la notificación generada, y además crea
         * la pila de tareas para la navegación generada por la notificación,
         * con el fin de saber a que actividad debe volver cuando pulsemos la
         * tecla de volver o una posible acción "salir" de un menú. Se deja
         * comentado para un posible uso futuro.

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
}
