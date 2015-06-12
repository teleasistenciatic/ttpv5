package com.fundacionmagtel.android.teleasistenciaticplus.act.user;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.fundacionmagtel.android.teleasistenciaticplus.R;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.bateria.MonitorBateria;
import com.fundacionmagtel.android.teleasistenciaticplus.act.main.actMain;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.helper.AppLog;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.stats.StatsFileLogTextGenerator;

/**
 * Actividad de configuración y consulta del monitor de batería.
 */
public class actUserOptionsMonitorBateria extends Activity implements View.OnClickListener
{
    /** Objetos TextView para para mostrar información en pantalla */
    private static TextView tvEstado, tvNivel, tvReceiver;
    /** Objetos NumberPicker para selección del nivel de alerta y la tasa de refresco */
    private static NumberPicker npNivelAlerta, npIntervalo;
    /** Objetos Button para iniciar o detener el monitor de bateria. */
    private static Button btnLanzarReceiver, btnPararReceiver;
    /** Objeto CheckBox para establecer si iniciar el monitor de batería con la app. */
    private static CheckBox cbIniciarAuto;
    /** Instancia del monitor de batería declarado en la actividad principal. */
    private static MonitorBateria monitor;

    /** Método del sistema onCreate */
    @Override
    public void onCreate(Bundle savedInstanceState) // Terminado
    {
        // Acciones a ejecutar al crear la actividad
        super.onCreate(savedInstanceState);

        /////////////////////////////////////////////////////
        StatsFileLogTextGenerator.write("configuracion", "bateria");
        /////////////////////////////////////////////////////

        setContentView(R.layout.layout_user_option_monitor_bateria);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getActionBar().setIcon(R.drawable.config_wheel);
        AppLog.i("actUserOptionsMonitorBateria", "Color del titulo: " + getTitle() + ", es: " + getTitleColor());
        // getWindow().setNavigationBarColor(0xe3e9e3);

        monitor = actMain.getInstance().getMonitorBateria();
        AppLog.i("Opciones.onCreate",
                "He recogido la instancia del monitor y monitor.hayDatos() = " +
                        monitor.hayDatos());

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

        npIntervalo = (NumberPicker) findViewById(R.id.npTasaRefresco);
        npIntervalo.setMinValue(0);
        npIntervalo.setMaxValue(8);
        npIntervalo.setDisplayedValues(new String[] {"MAX","-","ALTO","-","MED","-","BAJO","-","MIN"});
        npIntervalo.setWrapSelectorWheel(false);
        npIntervalo.setValue(monitor.getTasaRefresco());

        btnLanzarReceiver = (Button) findViewById(R.id.btnLanzarReceiver);
        btnLanzarReceiver.setOnClickListener(this);

        btnPararReceiver = (Button) findViewById(R.id.btnPararReceiver);
        btnPararReceiver.setOnClickListener(this);

        cbIniciarAuto = (CheckBox) findViewById(R.id.cbIniciarAuto);
        cbIniciarAuto.setChecked(monitor.getActivarAlInicio());

        // if(monitor.getReceiverActivo())
        //    mostrarDatos();
    }

    @Override
    public void onClick(View v) {   // Terminado
        switch (v.getId())
        {
            case R.id.btnLanzarReceiver:
                /* monitor.activaReceiver(false, false);
                monitor.desactivaReceiver(false); */
                monitor.activaReceiver(true, true);
                break;
            case R.id.btnPararReceiver:
                monitor.desactivaReceiver(true);
                break;
        }
        mostrarDatos();
    }

    public void mostrarDatos() // Terminado
    {
        AppLog.i("mostrarDatos()","getReceiverActivo = " + monitor.getReceiverActivo());
        if (monitor.getReceiverActivo())
        {
            if(monitor.hayDatos()){
                AppLog.i("mostrarDatos()","hayDatos = " + monitor.hayDatos());
                tvReceiver.setText(this.getText(R.string.tv_estado_receiver) + " Iniciado");
                tvNivel.setText(this.getText(R.string.tv_nivel_carga) + " " + monitor.textoNivel());
                tvEstado.setText(this.getText(R.string.tv_estado_bateria) + " " + monitor.textoEstado());
            }
        }
        else
        {
            tvReceiver.setText(this.getText(R.string.tv_estado_receiver) + " Detenido");
            tvNivel.setText(this.getText(R.string.tv_nivel_carga) + " No disponible");
            tvEstado.setText(this.getText(R.string.tv_estado_bateria) + " No disponible");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_act_user_options_monitor_bateria, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId())
        {
            case R.id.menu_user_options_monitor_bateria_guardar:
                // Actualizo el valor de los atributos afectados por cambios.
                monitor.setNivelAlerta(npNivelAlerta.getValue());
                monitor.setTasaRefresco(npIntervalo.getValue());
                monitor.setActivarAlInicio(cbIniciarAuto.isChecked());
                monitor.commit();
                this.mostrarDatos();
                break;
            case R.id.menu_user_options_monitor_bateria_salida:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}


