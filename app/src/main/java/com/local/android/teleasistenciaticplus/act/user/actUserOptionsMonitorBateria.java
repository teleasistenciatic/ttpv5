package com.local.android.teleasistenciaticplus.act.user;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.local.android.teleasistenciaticplus.R;
import com.local.android.teleasistenciaticplus.lib.bateria.MonitorBateria;
import com.local.android.teleasistenciaticplus.act.main.actMain;
import com.local.android.teleasistenciaticplus.lib.helper.AppLog;

public class actUserOptionsMonitorBateria extends Activity implements View.OnClickListener
{
    private static TextView tvEstado, tvNivel, tvReceiver;
    private static NumberPicker npNivelAlerta, npIntervalo;
    private static Button btnLanzarReceiver, btnPararReceiver, btnAplicar, btnSalir;
    private static CheckBox cbIniciarAuto;
    // Pillo el monitor de batería declarado en la actividad principal.
    private static MonitorBateria monitor;

    @Override
    public void onCreate(Bundle savedInstanceState) // Terminado
    {
        // Acciones a ejecutar al crear la actividad
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_user_option_monitor_bateria);

        monitor = actMain.getInstance().getMonitorBateria();
        AppLog.i("onCreate, options", "He recogido la instancia del monitor y monitor.hayDatos() = " + monitor.hayDatos());

        // Inicializo el layout
        tvEstado = (TextView) findViewById(R.id.tvEstado);
        tvNivel = (TextView) findViewById(R.id.tvNivel);
        tvReceiver = (TextView) findViewById(R.id.tvReceiver);


        mostrarDatos();

        npNivelAlerta = (NumberPicker) findViewById(R.id.npNivelAlerta);
        npNivelAlerta.setMinValue(20);
        npNivelAlerta.setMaxValue(50);
        npNivelAlerta.setWrapSelectorWheel(false);
        npNivelAlerta.setValue(monitor.getNivelAlerta());

        npIntervalo = (NumberPicker) findViewById(R.id.npIntervalo);
        npIntervalo.setMinValue(0); // Pide el estado de la batería al recibir cada evento.
        npIntervalo.setMaxValue(10); // Pide el estado de la batería al recibir 500 eventos.
        npIntervalo.setValue(monitor.getIntervalo());

        btnLanzarReceiver = (Button) findViewById(R.id.btnLanzarReceiver);
        btnLanzarReceiver.setOnClickListener(this);

        btnPararReceiver = (Button) findViewById(R.id.btnPararReceiver);
        btnPararReceiver.setOnClickListener(this);

        cbIniciarAuto = (CheckBox) findViewById(R.id.cbIniciarAuto);
        cbIniciarAuto.setChecked(monitor.getActivarAlInicio());

        btnAplicar = (Button) findViewById(R.id.btnAplicar);
        btnAplicar.setOnClickListener(this);

        btnSalir = (Button) findViewById(R.id.btnSalir);
        btnSalir.setFocusable(true);
        btnSalir.requestFocus();
        btnSalir.setOnClickListener(this);

        // if(monitor.getReceiverActivo())
        //    mostrarDatos();
    }

    @Override
    public void onClick(View v) {   // Terminado
        switch (v.getId())
        {
            case R.id.btnLanzarReceiver:
                monitor.activaReceiver(false, false);
                monitor.desactivaReceiver(false);
                monitor.activaReceiver(true, true);
                break;
            case R.id.btnPararReceiver:
                monitor.desactivaReceiver(true);
                break;
            case R.id.btnAplicar:
                // Actualizo el valor de los atributos afectados por cambios.
                monitor.setNivelAlerta(npNivelAlerta.getValue());
                monitor.setIntervalo(npIntervalo.getValue());
                monitor.setActivarAlInicio(cbIniciarAuto.isChecked());
                monitor.commit();
                break;
            case R.id.btnSalir:
                finish();
                break;
        }
        mostrarDatos();
    }

    public static void mostrarDatos() // Terminado
    {
        AppLog.i("mostrarDatos()","getReceiverActivo = " + monitor.getReceiverActivo());
        if (monitor.getReceiverActivo())
        {
            if(monitor.hayDatos()){
                AppLog.i("mostrarDatos()","hayDatos = " + monitor.hayDatos());
                tvReceiver.setText("Monitor Batería Activado");
                tvNivel.setText(monitor.textoNivel());
                tvEstado.setText(monitor.textoEstado());
            }
        }
        else
        {
            tvNivel.setText("Sin recepción de datos");
            tvEstado.setText("Monitor de batería");
            tvReceiver.setText("Desactivado");
        }
    }
}



